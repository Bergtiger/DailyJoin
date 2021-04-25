package de.bergtiger.dailyjoin;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.bergtiger.dailyjoin.cmd.DailyCommand;
import de.bergtiger.dailyjoin.listener.DailyListener;
import de.bergtiger.dailyjoin.tab.DailyTabComplete;
import de.bergtiger.dailyjoin.utils.DailyReward;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.bergtiger.dailyjoin.dao.TigerConnection;


public class dailyjoin extends JavaPlugin{

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
		// set instance
		instance = this;
		getDailyLogger().log(Level.INFO, "Starte DailyJoin...");
		// load configuration
		new DailyConfig(this);
		// start SQLConnection
		TigerConnection.inst().loadData();
		TigerConnection.inst().connect();
		// register Listener
		Bukkit.getPluginManager().registerEvents(DailyListener.inst(), this);
		// register Command
		getCommand("dailyjoin").setExecutor(new DailyCommand());
		getCommand("dailyjoin").setTabCompleter(new DailyTabComplete());
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
