package de.bergtiger.dailyjoin;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.bergtiger.dailyjoin.data.MySQL;


public class dailyjoin extends JavaPlugin{
	private MySQL sql;
	private DailyListener dl;
	private DailyCommand dc;
	private DailyReward dr;
	private DailyFile dFile;
	private DailySQL dSQL;

	private static dailyjoin instance;

	/**
	 * Get plugin instance,
	 * available after onEnable.
	 * @return dailyjoin instance.
	 */
	public static dailyjoin inst() {
		return instance;
	}
	
	@Override
	public void onEnable() {
		instance = this;
		
		System.out.println("Starte DailyJoin...");
		
		PluginManager pm = this.getServer().getPluginManager();
		
		new DailyConfig(this);
		
		this.sql = MySQL.inst();
//		this.dl = new DailyListener(this);
		this.dl = DailyListener.inst();
//		this.dc = new DailyCommand(this);
		this.dc = new DailyCommand();
		this.dr = new DailyReward(this);
		this.dFile = new DailyFile();
		this.dSQL = new DailySQL(this);
		
		pm.registerEvents(this.dl, this);
		
		this.getCommand("dailyjoin").setExecutor(this.dc);
	}
	
	public MySQL getMySQL(){
		return this.sql;
	}
	
	public DailyReward getDailyReward(){
		return this.dr;
	}
	
	public DailySQL getDailySQL(){
		return this.dSQL;
	}
	
	public DailyFile getDailyFile(){
		return this.dFile;
	}
	
	/**
	 * Plugin Logger.
	 * @return
	 */
	public static Logger getDailyLogger() {
		return inst().getLogger();
	}
	
	public void reload() {
		this.reloadConfig();
		this.dFile.reload();
//		this.sql.reload();
		MySQL.inst().reload();
//		this.dl.reload();
		DailyListener.inst().reload();
		this.dc.reload();
		this.dr.reload();
	}
	
	@Override
	public void onDisable() {
		System.out.println("DailyJoin beendet.");
		MySQL.inst().closeThread();
	}
}
