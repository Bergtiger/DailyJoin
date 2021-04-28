package de.bergtiger.dailyjoin.dao;

import static de.bergtiger.dailyjoin.utils.config.DailyConfig.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import de.bergtiger.dailyjoin.DailyFileToSQL;
import de.bergtiger.dailyjoin.dailyjoin;
import de.bergtiger.dailyjoin.dao.impl.sql.PlayerDAOImplSQL;
import de.bergtiger.dailyjoin.utils.config.DailyConfig;

public class TigerConnection {
	
	private static Connection conn;
	private static TigerConnection instance;
	
	private String host, user, password, database;
	private int port;
	private int thread = -1;

	
	public static TigerConnection inst() {
		if(instance == null)
			instance = new TigerConnection();
		return instance;
	}
	
	private TigerConnection() {}

	public void loadData() {
		DailyConfig c = DailyConfig.inst();
		if(c.hasValue(DATA_FORMAT_SQL) && c.getBoolean(DATA_FORMAT_SQL)) {
//		if(c.hasValue(DATA_FORMAT) && c.getValue(DATA_FORMAT).equalsIgnoreCase(DATA_SQL)) {
			// host
			if(c.hasValue(HOST))
				host = c.getValue(HOST);
			else
				dailyjoin.getDailyLogger().log(Level.SEVERE, "Missing value for " + HOST);
			// user
			if(c.hasValue(USER))
				user = c.getValue(USER);
			else
				dailyjoin.getDailyLogger().log(Level.SEVERE, "Missing value for " + USER);
			// password
			if(c.hasValue(PASSWORD))
				password = c.getValue(PASSWORD);
			else
				dailyjoin.getDailyLogger().log(Level.SEVERE, "Missing value for " + PASSWORD);
			// database
			if(c.hasValue(DATABASE))
				database = c.getValue(DATABASE);
			else
				dailyjoin.getDailyLogger().log(Level.SEVERE, "Missing value for " + DATABASE);
			// port
			if(c.hasValue(PORT))
				try {
					port = Integer.valueOf(c.getValue(PORT));
				} catch (NumberFormatException e) {
					dailyjoin.getDailyLogger().log(Level.SEVERE, "Wrong value for database.Port, has to be a number");
				}
			else
				dailyjoin.getDailyLogger().log(Level.SEVERE, "Missing value for " + PORT);
		}
	}
	
	public void connect() {
		// if Thread exists stop
		closeThread();
		// if connection exists stop
		if(hasConnection())
			closeConnection();
		// try Connection
		try {
			openConnection();
			System.out.println("[DailyJoin] SQL-Connection");
			DailyDataBase.createTable();
			System.out.println("[DailyJoin] Get offline Joins");
			new DailyFileToSQL().FileToSQL();
		} catch (Exception e) {
			System.out.println("[DailyJoin] Error No Connection");
			System.out.println("[DailyJoin] Try SQL-Reconnection in 30 seconds.");
			this.thread = Bukkit.getScheduler().scheduleSyncDelayedTask(dailyjoin.inst(), new Runnable(){
				@Override
				public void run() {
					noConnection();
			}}, 30*20L);
		}
	}
	
	public void reload(){
		// if Thread exists stop
		closeThread();
		// if connection exists stop
		if(hasConnection())
			closeConnection();
		// if sql is set
		if(DailyConfig.inst().getBoolean(DATA_FORMAT_SQL)) {
			// load new Data
			loadData();
			// connect
			connect();
		}
	}
	
	private void closeThread(){
		if(this.thread != -1){
			Bukkit.getScheduler().cancelTask(this.thread);
			this.thread = -1;
		}
	}
	
	private Connection openConnection() throws Exception{
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.user, this.password);
		return conn;
	}
	
	/**
	 * get SQL-Connection.
	 * @return
	 */
	public static Connection conn() {
		return conn;
	}
	
	/**
	 * Checks if the connection exists and is valid
	 * @return true when connection is valid
	 */
	public static Boolean hasConnection() {
		try {
			return (conn != null) && conn.isValid(1);
		} catch (SQLException ex) {
			return false;
		}
	}
	
	/**
	 * 
	 */
	public static void noConnection() {
		// if Thread exists stop Thread
		instance.closeThread();
		// if has no Connection try new
		if (!hasConnection())
			// connect
			instance.connect();
	}
	
	/**
	 * save close of resources
	 * @param st PreparedStatment
	 * @param rs ResultSet
	 */
	public static void closeRessources(ResultSet rs, PreparedStatement st) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException ex) {
				dailyjoin.getDailyLogger().log(Level.SEVERE, null, ex);
			}
		}
		if(st != null) {
			try {
				st.close();
			} catch (SQLException ex) {
				dailyjoin.getDailyLogger().log(Level.SEVERE, null, ex);
			}
		}
	}
	
	/**
	 * if there is a Valid Connection the Connection will be closed
	 * after closing the connection value will be set to null
	 */
	public void closeConnection() {
		try {
			closeThread();
			if(hasConnection()) {
				conn.close();
				dailyjoin.getDailyLogger().log(Level.INFO, "Logout");
			}
		} catch (SQLException ex) {
			dailyjoin.getDailyLogger().log(Level.SEVERE, null, ex);
		} finally {
			conn = null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public PlayerDAO getPlayerDAO() {
		return new PlayerDAOImplSQL();
	}
}
