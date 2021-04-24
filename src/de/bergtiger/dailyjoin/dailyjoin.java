package de.bergtiger.dailyjoin;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.bergtiger.dailyjoin.cmd.DailyCommand;
import de.bergtiger.dailyjoin.dao.impl.file.DailyFile;
import de.bergtiger.dailyjoin.listener.DailyListener;
import de.bergtiger.dailyjoin.utils.DailyReward;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.bergtiger.dailyjoin.dao.TigerConnection;


public class dailyjoin extends JavaPlugin{
	private DailyListener dl;
	private DailyCommand dc;
	private DailyReward dr;
	private DailyFile dFile;

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

		getDailyLogger().log(Level.INFO, "Starte DailyJoin...");
		
		PluginManager pm = this.getServer().getPluginManager();
		
		new DailyConfig(this);
		
		TigerConnection.inst().loadData();
		TigerConnection.inst().connect();
//		this.dl = new DailyListener(this);
		this.dl = DailyListener.inst();
//		this.dc = new DailyCommand(this);
		this.dc = new DailyCommand();
//		this.dr = new DailyReward(this);
		this.dr = DailyReward.inst();
		this.dFile = new DailyFile();
		
		pm.registerEvents(this.dl, this);
		
		this.getCommand("dailyjoin").setExecutor(this.dc);
	}
	
	public DailyReward getDailyReward(){
		return this.dr;
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

		TigerConnection.inst().reload();
		DailyListener.inst().reload();
		DailyReward.inst().reload();
	}
	
	@Override
	public void onDisable() {
		getDailyLogger().log(Level.INFO, "DailyJoin beendet.");
		TigerConnection.inst().closeConnection();
		TigerConnection.inst().closeThread();	
	}
}
