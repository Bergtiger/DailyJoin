package de.bergtiger.dailyjoin.dao;

import static de.bergtiger.dailyjoin.utils.config.DailyConfig.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import de.bergtiger.dailyjoin.utils.TigerLogger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import de.bergtiger.dailyjoin.dao.migration.DailyFileToSQL;
import de.bergtiger.dailyjoin.DailyJoin;
import de.bergtiger.dailyjoin.utils.config.DailyConfig;

public class TigerConnection {
	
	private static Connection conn;
	private static TigerConnection instance;
	
	private String host, user, password, database;
	private int port;
	private BukkitTask thread = null;

	
	public static TigerConnection inst() {
		if(instance == null)
			instance = new TigerConnection();
		return instance;
	}
	
	private TigerConnection() {}

	/**
	 * load connection data from configuration
	 */
	public void loadData() {
		DailyConfig c = DailyConfig.inst();
		if(c.hasValue(DATA_FORMAT_SQL) && c.getBoolean(DATA_FORMAT_SQL)) {
			// host
			if(c.hasValue(HOST))
				host = c.getValue(HOST);
			else
				TigerLogger.log(Level.SEVERE, "Missing value for " + HOST);
			// user
			if(c.hasValue(USER))
				user = c.getValue(USER);
			else
				TigerLogger.log(Level.SEVERE, "Missing value for " + USER);
			// password
			if(c.hasValue(PASSWORD))
				password = c.getValue(PASSWORD);
			else
				TigerLogger.log(Level.SEVERE, "Missing value for " + PASSWORD);
			// database
			if(c.hasValue(DATABASE))
				database = c.getValue(DATABASE);
			else
				TigerLogger.log(Level.SEVERE, "Missing value for " + DATABASE);
			// port
			if(c.hasValue(PORT))
				try {
					port = Integer.parseInt(c.getValue(PORT));
				} catch (NumberFormatException e) {
					TigerLogger.log(Level.SEVERE, "Wrong value for database.Port, has to be a number");
				}
			else
				TigerLogger.log(Level.SEVERE, "Missing value for " + PORT);
		}
	}

	/**
	 * open a sql connection
	 */
	private void connect() {
		// if Thread exists stop
		closeThread();
		// if connection exists stop
		if(hasConnection())
			closeConnection();
		// try Connection
		try {
			openConnection();
			TigerLogger.log(Level.INFO, "SQL-Connection");
			DailyDataBase.createTable();
			if(DailyConfig.inst().hasValue(LOAD_FILE_ON_SQL_CONNECTION) && DailyConfig.inst().getBoolean(LOAD_FILE_ON_SQL_CONNECTION)) {
				TigerLogger.log(Level.INFO, "Get offline Joins");
				DailyFileToSQL.inst().FileToSQL();
			}
		} catch (Exception e) {
			TigerLogger.log(Level.WARNING, "No Connection");
			TigerLogger.log(Level.WARNING, "Try SQL-Reconnection in 30 seconds.");
			thread = Bukkit.getScheduler().runTaskLaterAsynchronously(DailyJoin.inst(), this::connect, 30*20L);
		}
	}

	/**
	 * reloads connection and configuration.
	 * resets possible reconnect thread.
	 */
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

	/**
	 * if a reconnect thread is running, stops it hopefully
	 */
	private void closeThread(){
		if(thread != null){
			thread.cancel();
			thread = null;
		}
	}

	/**
	 * opens actual connection, chooses driver and connection link
	 * @throws Exception went wrong
	 */
	private void openConnection() throws Exception{
		// TODO test if needed
		Class.forName("com.mysql.jdbc.Driver");
		// TODO new sql driver
		//Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
	}
	
	/**
	 * get SQL-Connection.
	 * @return {@link Connection}
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
	 * handle no connection exception
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
	 * @param st {@link PreparedStatement} to close
	 * @param rs {@link ResultSet} to close
	 */
	public static void closeResources(ResultSet rs, PreparedStatement st) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException ex) {
				TigerLogger.log(Level.SEVERE, null, ex);
			}
		}
		if(st != null) {
			try {
				st.close();
			} catch (SQLException ex) {
				TigerLogger.log(Level.SEVERE, null, ex);
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
				TigerLogger.log(Level.INFO, "Logout");
			}
		} catch (SQLException ex) {
			TigerLogger.log(Level.SEVERE, null, ex);
		} finally {
			conn = null;
		}
	}
}
