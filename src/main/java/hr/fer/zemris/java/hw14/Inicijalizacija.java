package hr.fer.zemris.java.hw14;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

import hr.fer.zemris.java.hw14.VotingUtil.BandInfo;
import hr.fer.zemris.java.hw14.VotingUtil.Poll;
import hr.fer.zemris.java.hw14.VotingUtil.WebsiteInfo;

/**
 * Initialization of connection-pool and its destroying is performed in this web
 * listener. The initialization may consist of creating and filling SQL tables
 * if necessary. During the web-application startup, tables are verified to be
 * existent in database; if not, an appropriate CREATE statements are sent to
 * create them (but only if they do not already exists); if they exist, they
 * remain unmodified.
 *
 * @author Mario Bobic
 */
@WebListener
public class Inicijalizacija implements ServletContextListener {

	/** Poll list loaded from file on disk. */
	private static List<Poll> pollList;
	/** Band list loaded from file on disk. */
	private static List<BandInfo> bandList;
	/** Website list loaded from file on disk. */
	private static List<WebsiteInfo> websiteList;
	
	/** Map of generated poll IDs where id is mapped to poll title. */
	public static Map<String, Long> pollIDs = new HashMap<>();

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			pollList = VotingUtil.getPollList(sce.getServletContext());
			bandList = VotingUtil.getBandList(sce.getServletContext());
			websiteList = VotingUtil.getWebsiteList(sce.getServletContext());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		ComboPooledDataSource cpds = VotingUtil.getComboPooledDataSource(sce.getServletContext());
		sce.getServletContext().setAttribute("hr.fer.zemris.dbpool", cpds);
		
		Connection con;
		try {
			con = cpds.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("Failed to connect to database.", e);
		}
//		dropTables(con); // used for debugging
		
		try {System.out.println("---+ " + con.getSchema() + " connected! +---");} catch (SQLException e) {}
		
		DatabaseMetaData dbmd;
		ResultSet rs;
		try {
			dbmd = con.getMetaData();
			
			rs = dbmd.getTables(null, null, "POLLS", null);
			if (!rs.next()) {
				createPollsTable(con);
			}
			
			rs = dbmd.getTables(null, null, "POLLOPTIONS", null);
			if (!rs.next()){
				createPollOptionsTable(con);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		fillPollIDs(con);
		
		try {
			con.close();
		} catch (SQLException ignorable) {}
	}
	
	/**
	 * Fills the <tt>pollIDs<tt> map by mapping poll title to the poll id.
	 * 
	 * @param con connection used for preparing statements
	 */
	private static void fillPollIDs(Connection con) {
		PreparedStatement pst = null;
		
		try {
			pst = con.prepareStatement("SELECT id, title FROM Polls ORDER BY id");
			ResultSet rset = pst.executeQuery();
			try {
				while (rset!=null && rset.next()) {
					long id = rset.getLong(1);
					String title = rset.getString(2);
					
					pollIDs.put(title, id);
				}
			} finally {
				try { rset.close(); } catch (SQLException ignorable) {}
			}
		} catch (SQLException ignorable) {
			// http://stackoverflow.com/questions/5866154/how-to-create-table-if-it-doesnt-exist-using-derby-db
			// "create-and-ignore-error"
		} finally {
			try { pst.close(); } catch (Exception ignorable) {}
		}
	}

	/**
	 * Creates and fills the polls table to the specified connection
	 * <tt>con</tt>.
	 * 
	 * @param con connection used for preparing statements
	 */
	private static void createPollsTable(Connection con) {
		PreparedStatement pst = null;
		
		try {
			pst = con.prepareStatement("CREATE TABLE Polls(" +
									  "    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY" +
									  " ,  title VARCHAR(150) NOT NULL" + 
									  " ,  message CLOB(2048) NOT NULL" +
									  ")");
			pst.executeUpdate();
		} catch (SQLException ignorable) {
			// http://stackoverflow.com/questions/5866154/how-to-create-table-if-it-doesnt-exist-using-derby-db
			// "create-and-ignore-error"
		} finally {
			try { pst.close(); } catch (Exception ignorable) {}
		}
		
		fillPollsTable(con);
	}
	
	/**
	 * Fills the polls table to the specified connection <tt>con</tt>.
	 * <p>
	 * Generates the poll ID and remembers it in a class field.
	 * 
	 * @param con connection used for preparing statements
	 */
	private static void fillPollsTable(Connection con) {
		PreparedStatement pst = null;
		ResultSet rset = null;
		
		for (Poll poll : pollList) {
			try {
				pst = con.prepareStatement(
					"INSERT INTO Polls(title, message) VALUES (?,?)",
					Statement.RETURN_GENERATED_KEYS);
				
				pst.setString(1, poll.title);
				pst.setString(2, poll.message);
	
				pst.executeUpdate();
				rset = pst.getGeneratedKeys();
				if (rset != null && rset.next()) {
					Long id = rset.getLong(1);
					pollIDs.put(poll.title, id);
				} else {
					throw new RuntimeException("Failed to insert poll, id not available.");
				}
			} catch (SQLException ignorable) {
				// entry already exists
			} finally {
				try { pst.close(); } catch (Exception ignorable) {}
				try { rset.close(); } catch (Exception ignorable) {}
			}
		}
	}

	/**
	 * Creates and fills the poll options table to the specified connection
	 * <tt>con</tt>.
	 * 
	 * @param con connection used for preparing statements
	 */
	private static void createPollOptionsTable(Connection con) {
		PreparedStatement pst = null;
		
		try {
			pst = con.prepareStatement("CREATE TABLE PollOptions(" +
									   "    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY" +
									   " ,  optionTitle VARCHAR(100) NOT NULL" +
									   " ,  optionLink VARCHAR(150) NOT NULL" + 
									   " ,  pollID BIGINT" + 
									   " ,  votesCount BIGINT" + 
									   " ,  FOREIGN KEY (pollID) REFERENCES Polls(id)" +
									   ")");
			pst.executeUpdate();
		} catch (SQLException ignorable) {
			// http://stackoverflow.com/questions/5866154/how-to-create-table-if-it-doesnt-exist-using-derby-db
			// "create-and-ignore-error"
		} finally {
			try { pst.close(); } catch (Exception ignorable) {}
		}
		
		fillPollOptionsTable(con);
	}
	
	/**
	 * Fills the poll options table to the specified connection <tt>con</tt>.
	 * 
	 * @param con connection used for preparing statements
	 */
	private static void fillPollOptionsTable(Connection con) {
		PreparedStatement pst = null;

		for (BandInfo bandInfo : bandList) {
			try {
				pst = con.prepareStatement(
					"INSERT INTO PollOptions(optionTitle, optionLink, pollID, votesCount) VALUES (?,?,?,?)", 
					Statement.RETURN_GENERATED_KEYS);
				
				pst.setString(1, bandInfo.name);
				pst.setString(2, bandInfo.link);
				pst.setLong(3, pollIDs.get("Glasanje za omiljeni bend"));
				pst.setLong(4, bandInfo.getVotes());
	
				pst.executeUpdate();
			} catch (SQLException ignorable) {
				// entry already exists
				continue;
			} finally {
				try { pst.close(); } catch (Exception ignorable) {}
			}
		}
		
		for (WebsiteInfo websiteInfo : websiteList) {
			try {
				pst = con.prepareStatement(
					"INSERT INTO PollOptions(optionTitle, optionLink, pollID, votesCount) VALUES (?,?,?,?)", 
					Statement.RETURN_GENERATED_KEYS);
				
				pst.setString(1, websiteInfo.name);
				pst.setString(2, websiteInfo.link);
				pst.setLong(3, pollIDs.get("Glasanje za omiljenu web stranicu"));
				pst.setLong(4, websiteInfo.getVotes());
	
				pst.executeUpdate();
			} catch (SQLException ignorable) {
				// entry already exists
				continue;
			} finally {
				try { pst.close(); } catch (Exception ignorable) {}
			}
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ComboPooledDataSource cpds = (ComboPooledDataSource) sce.getServletContext()
				.getAttribute("hr.fer.zemris.dbpool");
		if (cpds != null) {
			try {
				DataSources.destroy(cpds);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * <strong>Debugging purposes.</strong> Used for dropping tables
	 * <tt>PollOptions</tt> and <tt>Polls</tt>.
	 * <p>
	 * If used, it should be called before checking if tables exist,
	 * and after a connection has been established.
	 * 
	 * @param con a connection
	 */
	@SuppressWarnings("unused")
	private void dropTables(Connection con) {
		PreparedStatement pst = null;

		try {
			pst = con.prepareStatement("DROP TABLE PollOptions");
			pst.executeUpdate();
		} catch (SQLException ignorable) {
		} finally {
			try { pst.close(); } catch (Exception ignorable) {}
		}
		
		try {
			pst = con.prepareStatement("DROP TABLE Polls");
			pst.executeUpdate();
		} catch (SQLException ignorable) {
		} finally {
			try { pst.close(); } catch (Exception ignorable) {}
		}
	}
	
}