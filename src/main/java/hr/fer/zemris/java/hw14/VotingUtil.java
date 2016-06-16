package hr.fer.zemris.java.hw14;

import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import javax.servlet.ServletContext;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * This class is a utility class used for manipulating voting files. It defines
 * methods for handling files and has classes commonly used for gathering
 * information on bands and vote results.
 *
 * @author Mario Bobic
 */
public class VotingUtil {
	
	/** An array of properties that must be present in .properties file. */
	private static final String[] PROPERTIES = {"host", "port", "name", "user", "password"};

	/**
	 * Disables instantiation.
	 */
	private VotingUtil() {
	}
	
	/**
	 * Creates a new vote results file with the specified <tt>path</tt>. Vote
	 * results are all set to <tt>0</tt>, where the IDs are separated from
	 * results with a tab. Attribute names are fetched from the <tt>context</tt>.
	 * 
	 * @param path path of the file to be created
	 * @param context HTTP servlet context
	 * @throws IOException if an I/O exception occurs
	 */
	public static synchronized void createFile(Path path, ServletContext context) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			Enumeration<String> en = context.getAttributeNames();
			while (en.hasMoreElements()) {
				String id = en.nextElement();
				writer.write(id + "\t" + "0\n");
			}
		}
	}
	
	/**
	 * Returns a <tt>List</tt> of {@linkplain BandInfo} objects loaded from the
	 * <tt>/WEB-INF/bands-definition.txt</tt> file. Lines are parsed and a new
	 * <tt>BandInfo</tt> object is created for each line.
	 * <p>
	 * This method also loads votes from <tt>/WEB-INF/bands-results.txt</tt>
	 * file.
	 * <p>
	 * Throws {@linkplain IllegalArgumentException} if any line in the file
	 * contains not exactly three attributes separated by a tab symbol.
	 * 
	 * @param context HTTP servlet context
	 * @return a list containing band info
	 * @throws IllegalArgumentException if any line in the file is invalid
	 * @throws IOException if an I/O exception occurs
	 */
	public static List<BandInfo> getBandList(ServletContext context) throws IOException {
		String fileName = context.getRealPath("/WEB-INF/bands-definition.txt");
		
		Path path = Paths.get(fileName);
		if (!Files.exists(path)) {
			throw new RuntimeException("File " + fileName + " does not exist.");
		}
		
		Map<Long, Long> voteResults = getBandResults(context);
		
		List<BandInfo> bandList = new ArrayList<>();
		try (Stream<String> lines = Files.lines(Paths.get(fileName), StandardCharsets.UTF_8)) {
			lines.forEach(line -> {
				String[] attributes = line.split("\\t");
				if (attributes.length != 3) {
					throw new IllegalArgumentException("Line [" + line + "] does not contain 3 attributes.");
				}
				
				long id = Long.parseLong(attributes[0]);
				String name = attributes[1];
				String songLink = attributes[2];
				long votes = voteResults.get(id);
				bandList.add(new BandInfo(id, name, songLink, votes));
			});
		}
		
		return bandList;
	}
	
	/**
	 * Returns a <tt>Map</tt> of vote results loaded from the
	 * <tt>/WEB-INF/bands-results.txt</tt> file. Lines are parsed
	 * attributes <tt>id</tt> and <tt>votes</tt> are added to the map for each
	 * line.
	 * 
	 * @param context HTTP servlet context
	 * @return a map containing vote results
	 * @throws IOException if an I/O exception occurs
	 */
	private static Map<Long, Long> getBandResults(ServletContext context) throws IOException {
		String fileName = context.getRealPath("/WEB-INF/bands-results.txt");
		return getVoteResultsFromFile(fileName, context);
	}
	
	/**
	 * Returns a <tt>List</tt> of {@linkplain WebsiteInfo} objects loaded from the
	 * <tt>/WEB-INF/websites-definition.txt</tt> file. Lines are parsed and a new
	 * <tt>WebsiteInfo</tt> object is created for each line.
	 * <p>
	 * This method also loads votes from <tt>/WEB-INF/websites-results.txt</tt>
	 * file.
	 * <p>
	 * Throws {@linkplain IllegalArgumentException} if any line in the file
	 * contains not exactly three attributes separated by a tab symbol.
	 * 
	 * @param context HTTP servlet context
	 * @return a list containing website info
	 * @throws IllegalArgumentException if any line in the file is invalid
	 * @throws IOException if an I/O exception occurs
	 */
	public static List<WebsiteInfo> getWebsiteList(ServletContext context) throws IOException {
		String fileName = context.getRealPath("/WEB-INF/websites-definition.txt");
		
		Path path = Paths.get(fileName);
		if (!Files.exists(path)) {
			throw new RuntimeException("File " + fileName + " does not exist.");
		}
		
		Map<Long, Long> voteResults = getWebsiteResults(context);
		
		List<WebsiteInfo> websiteList = new ArrayList<>();
		try (Stream<String> lines = Files.lines(Paths.get(fileName), StandardCharsets.UTF_8)) {
			lines.forEach(line -> {
				String[] attributes = line.split("\\t");
				if (attributes.length != 3) {
					throw new IllegalArgumentException("Line [" + line + "] does not contain 3 attributes.");
				}
				
				long id = Long.parseLong(attributes[0]);
				String name = attributes[1];
				String link = attributes[2];
				long votes = voteResults.get(id);
				websiteList.add(new WebsiteInfo(id, name, link, votes));
			});
		}
		
		return websiteList;
	}
	
	/**
	 * Returns a <tt>Map</tt> of vote results loaded from the
	 * <tt>/WEB-INF/websites-results.txt</tt> file. Lines are parsed attributes
	 * <tt>id</tt> and <tt>votes</tt> are added to the map for each line.
	 * 
	 * @param context HTTP servlet context
	 * @return a map containing vote results
	 * @throws IOException if an I/O exception occurs
	 */
	private static Map<Long, Long> getWebsiteResults(ServletContext context) throws IOException {
		String fileName = context.getRealPath("/WEB-INF/websites-results.txt");
		return getVoteResultsFromFile(fileName, context);
	}
	
	/**
	 * Returns a <tt>Map</tt> of vote results loaded from the a file with the
	 * specified <tt>fileName</tt>. Lines are parsed attributes <tt>id</tt> and
	 * <tt>votes</tt> are added to the map for each line.
	 * 
	 * @param fileName name of the file
	 * @param context HTTP servlet context
	 * @return a map containing vote results
	 * @throws IOException if an I/O exception occurs
	 */
	static Map<Long, Long> getVoteResultsFromFile(String fileName, ServletContext context) throws IOException {
		Path path = Paths.get(fileName);
		if (!Files.exists(path)) {
			createFile(path, context);
		}
		
		Map<Long, Long> voteResults = new HashMap<>();
		try (Stream<String> lines = Files.lines(Paths.get(fileName), StandardCharsets.UTF_8)) {
			lines.forEach(line -> {
				String[] attrs = line.split("\\t");

				long id = Long.parseLong(attrs[0]);
				long votes = Long.parseLong(attrs[1]);
				voteResults.put(id, votes);
			});
		}
		
		return voteResults;
	}
	
	/**
	 * Returns a <tt>List</tt> of {@linkplain Poll} objects loaded from the
	 * <tt>/WEB-INF/polls.txt</tt> file. Lines are parsed and a
	 * new <tt>Poll</tt> object is created for each line.
	 * <p>
	 * Throws {@linkplain IllegalArgumentException} if any line in the file
	 * contains not exactly three attributes separated by a tab symbol.
	 * 
	 * @param context HTTP servlet context
	 * @return a list containing polls
	 * @throws IllegalArgumentException if any line in the file is invalid
	 * @throws IOException if an I/O exception occurs
	 */
	public static List<Poll> getPollList(ServletContext context) throws IOException {
		String fileName = context.getRealPath("/WEB-INF/polls.txt");
		
		Path path = Paths.get(fileName);
		if (!Files.exists(path)) {
			throw new RuntimeException("File " + fileName + " does not exist.");
		}
		
		List<Poll> pollList = new ArrayList<>();
		try (Stream<String> lines = Files.lines(Paths.get(fileName), StandardCharsets.UTF_8)) {
			lines.forEach(line -> {
				String[] attributes = line.split("\\t");
				if (attributes.length != 3) {
					throw new IllegalArgumentException("Line [" + line + "] does not contain 3 attributes.");
				}
				
				long id = Long.parseLong(attributes[0]);
				String title = attributes[1];
				String message = attributes[2];
				pollList.add(new Poll(id, title, message));
			});
		}
		
		return pollList;
	}
	
	/**
	 * Returns a {@linkplain ComboPooledDataSource} object initialized with
	 * properties obtained from the <tt>/WEB-INF/dbsettings.properties</tt>
	 * file. The combo pooled data source object is set initially with the
	 * following parameters:
	 * <ul>
	 * <li>connection URL:
	 * <tt>jdbc:derby://" + host + ":" + port + "/" + dbName</tt>, where
	 * <tt>host</tt>, <tt>port</tt> and <tt>dbName</tt> are obtained from the
	 * properties file.
	 * <li>user: <tt>user</tt> obtained from the properties file.
	 * <li>password: <tt>password</tt> obtained from the properties file.
	 * <li>initial pool size: <tt>5</tt>
	 * <li>minimum pool size: <tt>5</tt>
	 * <li>acquire increment: <tt>5</tt>
	 * <li>maximum pool size: <tt>20</tt>
	 * </ul>
	 * 
	 * @param context HTTP servlet context
	 * @return an instance of {@code ComboPooledDataSource}
	 */
	public static ComboPooledDataSource getComboPooledDataSource(ServletContext context) {
		Properties properties = getProperties("/WEB-INF/dbsettings.properties", context);

		// Pool preparation
		ComboPooledDataSource cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass("org.apache.derby.jdbc.ClientDriver");
		} catch (PropertyVetoException e) {
			throw new RuntimeException(e);
		}
		
		String host = properties.getProperty("host");
		String port = properties.getProperty("port");
		String dbName = properties.getProperty("name");
		String connectionURL = "jdbc:derby://" + host + ":" + port + "/" + dbName;
		
		cpds.setJdbcUrl(connectionURL);
		cpds.setUser(properties.getProperty("user"));
		cpds.setPassword(properties.getProperty("password"));
		cpds.setInitialPoolSize(5);
		cpds.setMinPoolSize(5);
		cpds.setAcquireIncrement(5);
		cpds.setMaxPoolSize(20);
		
		return cpds;
	}
	
	/**
	 * Gets the properties by converting the specified relative <tt>path</tt> to
	 * a real path using {@code ServletContextEvent} <tt>sce</tt> and returns
	 * it.
	 * 
	 * @param path relative path to properties
	 * @param context HTTP servlet context
	 * @return object with properties mapping
	 * @throws RuntimeException if properties can not be loaded
	 */
	private static Properties getProperties(String path, ServletContext context) {
		String realPath = context.getRealPath(path);
		if (!Files.isRegularFile(Paths.get(realPath))) {
			throw new RuntimeException("File " + realPath + " does not exist.");
		}
		
		try {
			Properties properties = loadProperties(realPath);
			
			for (String property : PROPERTIES) {
				if (!properties.containsKey(property))
					throw new RuntimeException("Missing property: " + property);
			}
			
			return properties;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Loads the properties of a file specified by the <tt>path</tt> parameter and
	 * returns an instance of {@code Properties} object.
	 * 
	 * @param path path of the property file
	 * @return {@code Properties} object containing file properties
	 * @throws IllegalArgumentException if an I/O error occurs, if a security
	 *         exception occurs, if the path string cannot be converted to a
	 *         {@code Path} or if the file contains a malformed Unicode escape
	 *         sequence
	 */
	private static Properties loadProperties(String path) {
		Properties properties = new Properties();
		
		try {
			properties.load(Files.newInputStream(Paths.get(path)));
		} catch (Exception e) {
			throw new IllegalArgumentException("Error loading file " + path, e);
		}
		
		return properties;
	}
	
	/**
	 * This class represents info of one information object, holding information
	 * on object's unique ID number, name, a link and the number of votes the
	 * object has gained.
	 *
	 * @author Mario Bobic
	 */
	public abstract static class Info {
		/** Comparator by unique ID numbers. */
		public static final Comparator<Info> BY_ID =
				(info1, info2) -> Long.compare(info1.id, info2.id);
		/** Comparator by number of votes, <strong>descending</strong>. */
		public static final Comparator<Info> BY_VOTES =
				(info1, info2) -> -Long.compare(info1.votes, info2.votes);
		
		/** Unique ID of the information object. */
		public final long id;
		/** Name of the information object. */
		public final String name;
		/** Link of the information object. */
		public final String link;
		
		/** Number of votes the information object has gained. */
		protected long votes;
		
		/**
		 * Constructs an instance of {@code Info} with the specified
		 * arguments.
		 * 
		 * @param id unique ID of the information object
		 * @param name name of the information object
		 * @param link link of the information object
		 */
		public Info(long id, String name, String link) {
			this(id, name, link, 0L);
		}
		
		/**
		 * Constructs an instance of {@code Info} with the specified
		 * arguments.
		 * 
		 * @param id unique ID of the information object
		 * @param name name of the information object
		 * @param link link of the information object
		 * @param votes number of votes the information object has gained
		 */
		public Info(long id, String name, String link, long votes) {
			this.id = id;
			this.name = name;
			this.link = link;
			this.votes = votes;
		}
		
		/**
		 * Returns the unique ID of the information object.
		 * 
		 * @return the unique ID of the information object
		 */
		public long getId() {
			return id;
		}
		
		/**
		 * Returns the name of the information object.
		 * 
		 * @return the name of the information object
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns a link of the information object
		 * 
		 * @return a link of information object
		 */
		public String getLink() {
			return link;
		}
		
		/**
		 * Returns the number of votes this information object has gained.
		 * 
		 * @return the number of votes this information object has gained
		 */
		public long getVotes() {
			return votes;
		}
		
		/**
		 * Increases the number of votes for this information object by one.
		 */
		public void vote() {
			votes++;
		}
		
	}
	
	/**
	 * This class represents info of one band, holding information on the band's
	 * unique ID number, band name, a link to one representative song of that
	 * band and the number of votes the band has gained.
	 *
	 * @author Mario Bobic
	 */
	public static class BandInfo extends Info {
		
		/**
		 * Constructs an instance of {@code BandInfo} with the specified
		 * arguments.
		 * 
		 * @param id unique ID of the band
		 * @param name name of the band
		 * @param songLink link to one representative song of the band
		 */
		public BandInfo(long id, String name, String songLink) {
			this(id, name, songLink, 0L);
		}
		
		/**
		 * Constructs an instance of {@code BandInfo} with the specified
		 * arguments.
		 * 
		 * @param id unique ID of the band
		 * @param name name of the band
		 * @param songLink link to one representative song of the band
		 * @param votes number of votes the band gained
		 */
		public BandInfo(long id, String name, String songLink, long votes) {
			super(id, name, songLink, votes);
		}

		/**
		 * Returns a link to one representative song of the band.
		 * <p>
		 * This method is the same as calling {@linkplain #getLink()}.
		 * 
		 * @return a link to one representative song of the band
		 */
		public String getSongLink() {
			return super.getLink();
		}
	}
	
	/**
	 * This class represents info of one website, holding information on the
	 * website's unique ID number, website's name, the link to the website and
	 * the number of votes the website has gained.
	 *
	 * @author Mario Bobic
	 */
	public static class WebsiteInfo extends Info {
		
		/**
		 * Constructs an instance of {@code WebsiteInfo} with the specified
		 * arguments.
		 * 
		 * @param id unique ID of the website
		 * @param name name of the website
		 * @param link link to the website
		 */
		public WebsiteInfo(long id, String name, String link) {
			this(id, name, link, 0L);
		}
		
		/**
		 * Constructs an instance of {@code WebsiteInfo} with the specified
		 * arguments.
		 * 
		 * @param id unique ID of the website
		 * @param name name of the website
		 * @param link link to the website
		 * @param votes number of votes the website has gained
		 */
		public WebsiteInfo(long id, String name, String link, long votes) {
			super(id, name, link, votes);
		}
	}
	
	/**
	 * This class represents info of one poll, holding information on the poll's
	 * unique ID number, the poll title and a message that comes with the poll.
	 *
	 * @author Mario Bobic
	 */
	public static class Poll {
		
		/** Unique ID of the poll. */
		public final long id;
		/** Title of the poll. */
		public final String title;
		/** Message of the poll. */
		public final String message;
		
		/**
		 * Constructs an instance of {@code Poll} with the specified arguments.
		 * 
		 * @param id unique ID of the poll
		 * @param title name of the poll
		 * @param message message of the poll
		 */
		public Poll(long id, String title, String message) {
			this.id = id;
			this.title = title;
			this.message = message;
		}

		/**
		 * Returns the unique ID of the poll.
		 * 
		 * @return the unique ID of the poll
		 */
		public long getId() {
			return id;
		}

		/**
		 * Returns the title of the poll.
		 * 
		 * @return the title of the poll
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * Returns the message of the poll.
		 * 
		 * @return the message of the poll
		 */
		public String getMessage() {
			return message;
		}
	}

}
