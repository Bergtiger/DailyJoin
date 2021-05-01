package de.bergtiger.dailyjoin;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.bergtiger.dailyjoin.cmd.DailyCommand;
import de.bergtiger.dailyjoin.dao.impl.PlayerDAOimpl;
import de.bergtiger.dailyjoin.listener.DailyListener;
import de.bergtiger.dailyjoin.tab.DailyTabComplete;
import de.bergtiger.dailyjoin.utils.DailyReward;
import de.bergtiger.dailyjoin.utils.config.DailyConfig;

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
		DailyConfig.load();
		// start SQLConnection
		TigerConnection.inst().reload();
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
		reloadConfig();
		DailyConfig.load();
		// reload connection
		TigerConnection.inst().reload();
		// reload dao
		PlayerDAOimpl.inst().reload();
		// reload listener
		DailyListener.inst().reload();
		// reload reward
		DailyReward.inst().reload();
	}
	
	@Override
	public void onDisable() {
		getDailyLogger().log(Level.INFO, "DailyJoin beendet.");
		TigerConnection.inst().closeConnection();
	}
}
