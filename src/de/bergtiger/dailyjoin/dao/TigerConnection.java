package de.bergtiger.dailyjoin.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import de.bergtiger.dailyjoin.DailyFileToSQL;
import de.bergtiger.dailyjoin.dailyjoin;
import de.bergtiger.dailyjoin.dao.impl.sql.PlayerDAOImplSQL;

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
		FileConfiguration cfg = dailyjoin.inst().getConfig();
		if(cfg.getString("config.SQL").equalsIgnoreCase("true")){
			String db = "database.";
		
			host = cfg.getString(db + "host");
			port = cfg.getInt(db + "port");
			user = cfg.getString(db + "user");
			password = cfg.getString(db + "password");
			database = cfg.getString(db + "database");
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
					reconnect();
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
		if(dailyjoin.inst().getConfig().getString("config.SQL").equalsIgnoreCase("true")) {
			// load new Data
			loadData();
			// connect
			connect();
		}
	}
	
	public void reconnect(){
		// if Thread exists stop
		closeThread();
		// if connection exits stop
		if(hasConnection())
			return;
		// connect
		connect();
	}
	
	public void closeThread(){
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
		// TODO
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
	
	public PlayerDAO getPlayerDAO() {
		return new PlayerDAOImplSQL();
	}
	
	@Deprecated
	public void queryUpdate(String query) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(query);
			st.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Failed to sendupdate '" + query + "'.");
		} finally {
			closeRessources(null, st);
		}
	}
}
