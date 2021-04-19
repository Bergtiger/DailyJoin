package de.bergtiger.dailyjoin.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import de.bergtiger.dailyjoin.DailyFileToSQL;
import de.bergtiger.dailyjoin.dailyjoin;

public class MySQL {
	private String host;
	private int port;
	private String user;
	private String password;
	private String database;
	
	private Connection conn;

	private dailyjoin plugin;
	private FileConfiguration cfg;
	private int thread = -1;
	
	public MySQL(dailyjoin plugin){
		this.plugin = plugin;
		
		this.cfg = this.plugin.getConfig();	
		
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
				new DailyFileToSQL(this.plugin).FileToSQL();
			} catch (Exception e) {
				System.out.println("[DailyJoin] Error No SQL-Connection");
				System.out.println("[DailyJoin] Try SQL-Reconnection.");		
				this.thread = Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable(){
					@Override
					public void run() {
						reconnect();
				}}, 0*20L);
			}
		}
	}
	
	
	public void reload(){
		this.plugin.reloadConfig();
		this.cfg = this.plugin.getConfig();	
		
		this.closeThread();
		
		if(this.cfg.getString("config.SQL").equalsIgnoreCase("true")){
			String db = "database.";
		
			this.host = this.cfg.getString(db + "host");
			this.port = this.cfg.getInt(db + "port");
			this.user = this.cfg.getString(db + "user");
			this.password = this.cfg.getString(db + "password");
			this.database = this.cfg.getString(db + "database");
		
			if(this.hasConnection()){
				this.clossConnection();
			}
			try {
				this.openConnection();
				new DailyDataBase(this);
				new DailyFileToSQL(this.plugin).FileToSQL();
			} catch (Exception e) {
				System.out.println("[DailyJoin] Error No Connection");
				System.out.println("[DailyJoin] Try SQL-Reconnection in 30 seconds.");
				this.thread = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
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
			new DailyFileToSQL(this.plugin).FileToSQL();
		} catch (Exception e) {
			System.out.println("[DailyJoin] Error No Connection");
			System.out.println("[DailyJoin] Try SQL-Reconnection in 30 seconds.");
			this.thread = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
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
		Connection conn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.user, this.password);
		this.conn =conn;
		return conn;
	}
	
	public Connection getConnection(){
		return this.conn;
	}
	
	public boolean hasConnection() {
		try {	
			if(this.conn == null){
				return false;
			} else if(this.conn != null || this.conn.isValid(1)){	
				return true;
			}
		} catch (SQLException e) {	
			System.out.println(e);
		}
		return false;
	}
	
	public void closeRessources(ResultSet rs, PreparedStatement st) {
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
	
	public void clossConnection() {
		try {
			this.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		this.conn = null;
		}
	}
	
	public void queryUpdate(String query) {
		Connection conn = this.conn;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(query);
			st.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Failed to sendupdate '" + query + "'.");
		} finally {
			this.closeRessources(null, st);
		}
	}
}
