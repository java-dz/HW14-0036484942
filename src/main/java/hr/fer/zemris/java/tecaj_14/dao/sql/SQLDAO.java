package hr.fer.zemris.java.tecaj_14.dao.sql;

import hr.fer.zemris.java.hw14.Inicijalizacija;
import hr.fer.zemris.java.hw14.VotingUtil.BandInfo;
import hr.fer.zemris.java.hw14.VotingUtil.Info;
import hr.fer.zemris.java.hw14.VotingUtil.Poll;
import hr.fer.zemris.java.hw14.VotingUtil.WebsiteInfo;
import hr.fer.zemris.java.tecaj_14.dao.DAO;
import hr.fer.zemris.java.tecaj_14.dao.DAOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ovo je implementacija podsustava DAO uporabom tehnologije SQL. Ova
 * konkretna implementacija očekuje da joj veza stoji na raspolaganju
 * preko {@link SQLConnectionProvider} razreda, što znači da bi netko
 * prije no što izvođenje dođe do ove točke to trebao tamo postaviti.
 * U web-aplikacijama tipično rješenje je konfigurirati jedan filter 
 * koji će presresti pozive servleta i prije toga ovdje ubaciti jednu
 * vezu iz connection-poola, a po zavrsetku obrade je maknuti.
 *  
 * @author marcupic
 */
public class SQLDAO implements DAO {

	@Override
	public Poll getPoll(long pollID) throws DAOException {
		Connection con = SQLConnectionProvider.getConnection();
		PreparedStatement pst = null;
		
		Poll poll = null;
		try {
			pst = con.prepareStatement("SELECT id, title, message FROM Polls WHERE id = ?");
			pst.setLong(1, pollID);
			ResultSet rset = pst.executeQuery();
			try {
				if (rset!=null && rset.next()) {
					long id = rset.getLong(1);
					String title = rset.getString(2);
					String message = rset.getString(3);
					
					poll = new Poll(id, title, message);
				} else {
					throw new DAOException("Failed to retrieve poll from poll ID.");
				}
			} finally {
				try { rset.close(); } catch (SQLException ignorable) {}
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			try { pst.close(); } catch (Exception ignorable) {}
		}
		
		return poll;
	}
	
	@Override
	public List<Poll> getPollList() throws DAOException {
		Connection con = SQLConnectionProvider.getConnection();
		PreparedStatement pst = null;

		List<Poll> pollList = new ArrayList<>();
		try {
			pst = con.prepareStatement("SELECT id, title, message FROM Polls ORDER BY id");
			ResultSet rset = pst.executeQuery();
			try {
				while (rset!=null && rset.next()) {
					long id = rset.getLong(1);
					String title = rset.getString(2);
					String message = rset.getString(3);
					
					pollList.add(new Poll(id, title, message));
				}
			} finally {
				try { rset.close(); } catch (SQLException ignorable) {}
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			try { pst.close(); } catch (Exception ignorable) {}
		}
		
		return pollList;
	}
	
	@Override
	public List<Info> getInfoList(long pollID) throws DAOException {
		Connection con = SQLConnectionProvider.getConnection();
		PreparedStatement pst = null;

		List<Info> infoList;
		try {
			pst = con.prepareStatement("SELECT id, optionTitle, optionLink, votesCount "
									   + "FROM PollOptions "
									   + "WHERE pollID = ? "
									   + "ORDER BY id");
			pst.setLong(1, pollID);
			ResultSet rset = pst.executeQuery();
			try {
				infoList = determineInfoListType(pollID, rset);
			} finally {
				try { rset.close(); } catch (SQLException ignorable) {}
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			try { pst.close(); } catch (Exception ignorable) {}
		}
		
		return infoList;
	}
	
	/**
	 * Determines the info list type from the <tt>pollID</tt> that should be
	 * returned and returns it by obtaining a result set from the <tt>rset</tt>
	 * and creating new info objects on the way.
	 * 
	 * @param pollID poll ID
	 * @param rset the result set
	 * @return a list of info objects with type obtained from <tt>pollID</tt>
	 * @throws SQLException if a SQL exception occurs
	 */
	private static List<Info> determineInfoListType(long pollID, ResultSet rset) throws SQLException {
		if (pollID == Inicijalizacija.pollIDs.get("Glasanje za omiljeni bend")) {
			return getBandList(rset);
		} else if (pollID == Inicijalizacija.pollIDs.get("Glasanje za omiljenu web stranicu")) {
			return getWebsiteList(rset);
		} else {
			throw new SQLException("Poll ID not available.");
		}
	}
	
	/**
	 * Returns a band list by obtaining a result set from the <tt>rset</tt>
	 * and creating new band info objects on the way.
	 * 
	 * @param rset the result set
	 * @return list of band info objects
	 * @throws SQLException if a SQL exception occurs
	 */
	private static List<Info> getBandList(ResultSet rset) throws SQLException {
		List<Info> bandList = new ArrayList<>();
		
		while (rset.next()) {
			long id = rset.getLong(1);
			String name = rset.getString(2);
			String songLink = rset.getString(3);
			long votesCount = rset.getLong(4);
			
			bandList.add(new BandInfo(id, name, songLink, votesCount));
		}
		
		return bandList;
	}
	
	/**
	 * Returns a band list by obtaining a result set from the <tt>rset</tt>
	 * and creating new website info objects on the way.
	 * 
	 * @param rset the result set
	 * @return list of website info objects
	 * @throws SQLException if a SQL exception occurs
	 */
	private static List<Info> getWebsiteList(ResultSet rset) throws SQLException {
		List<Info> websiteList = new ArrayList<>();
		
		while (rset.next()) {
			long id = rset.getLong(1);
			String title = rset.getString(2);
			String link = rset.getString(3);
			long votesCount = rset.getLong(4);

			websiteList.add(new WebsiteInfo(id, title, link, votesCount));
		}
		
		return websiteList;
	}
	
	@Override
	public void vote(long id) throws DAOException {
		Connection con = SQLConnectionProvider.getConnection();
		PreparedStatement pst = null;
		
		try {
			pst = con.prepareStatement("UPDATE PollOptions SET votesCount=votesCount+1"+
									   " WHERE id = ?");
			pst.setLong(1, id);
			
			try {
				int affectedRows = pst.executeUpdate();
				if (affectedRows != 1){
					throw new DAOException("Failed to update poll options row.");
				}
			} finally {
				try { pst.close(); } catch(Exception ignorable) {}
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

}