package de.bergtiger.dailyjoin.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import de.bergtiger.dailyjoin.DailyFileToSQL;
import de.bergtiger.dailyjoin.dailyjoin;
import de.bergtiger.dailyjoin.dao.impl.sql.playerDAOImplSQL;

public class TigerConnection {
	private String host;
	private int port;
	private String user;
	private String password;
	private String database;
	
	private static Connection conn;

	private FileConfiguration cfg;
	private int thread = -1;
	
	private static TigerConnection instance;
	
	public static TigerConnection inst() {
		if(instance == null)
			instance = new TigerConnection();
		return instance;
	}
	
	private TigerConnection(){
		this.cfg = dailyjoin.inst().getConfig();
		
		if(this.cfg.getString("config.SQL").equalsIgnoreCase("true")){
			String db = "database.";
		
			this.host = this.cfg.getString(db + "host");
			this.port = this.cfg.getInt(db + "port");
			this.user = this.cfg.getString(db + "user");
			this.password = this.cfg.getString(db + "password");
			this.database = this.cfg.getString(db + "database");
		
			try {
				this.openConnection();
				new DailyDataBase(this);
				new DailyFileToSQL().FileToSQL();
			} catch (Exception e) {
				System.out.println("[DailyJoin] Error No SQL-Connection");
				System.out.println("[DailyJoin] Try SQL-Reconnection.");		
				this.thread = Bukkit.getScheduler().scheduleSyncDelayedTask(dailyjoin.inst(), new Runnable(){
					@Override
					public void run() {
						reconnect();
				}}, 0*20L);
			}
		}
	}
	
	
	public void reload(){
		this.cfg = dailyjoin.inst().getConfig();	
		
		this.closeThread();
		
		if(this.cfg.getString("config.SQL").equalsIgnoreCase("true")){
			String db = "database.";
		
			this.host = this.cfg.getString(db + "host");
			this.port = this.cfg.getInt(db + "port");
			this.user = this.cfg.getString(db + "user");
			this.password = this.cfg.getString(db + "password");
			this.database = this.cfg.getString(db + "database");
		
			if(hasConnection()){
				this.clossConnection();
			}
			try {
				this.openConnection();
				new DailyDataBase(this);
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
	}
	
	public void reconnect(){
		this.closeThread();
		try {
			this.openConnection();
			new DailyDataBase(this);
			System.out.println("[DailyJoin] SQL-Connection");
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
	
	public void closeThread(){
		if(this.thread != -1){
			Bukkit.getScheduler().cancelTask(this.thread);
			this.thread = -1;
		}
	}
	
	public Connection openConnection()throws Exception{
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.user, this.password);
		return conn;
	}
	
	@Deprecated
	public Connection getConnection(){
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
	 * check if Connection is available.
	 * @return true if connected.
	 */
	public static boolean hasConnection() {
		try {	
			if(conn == null){
				return false;
			} else if(conn != null || conn.isValid(1)){	
				return true;
			}
		} catch (SQLException e) {	
			System.out.println(e);
		}
		return false;
	}
	
	/**
	 * 
	 */
	public static void noConnection() {
		// TODO
	}
	
	/**
	 * save close of resources.
	 * @param rs ResultSet
	 * @param st PreparedStatement
	 */
	public static void closeRessources(ResultSet rs, PreparedStatement st) {
		if(rs != null){
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
		if(st != null){
			try {
				st.close();
			} catch (SQLException e) {
			}
		}
	}
	
	/**
	 * close Connection
	 */
	public void clossConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn = null;
		}
	}
	
	public playerDAO getPlayerDAO() {
		return new playerDAOImplSQL();
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
