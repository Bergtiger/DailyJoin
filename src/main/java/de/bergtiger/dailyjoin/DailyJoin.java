package de.bergtiger.dailyjoin;

import java.util.logging.Level;

import de.bergtiger.dailyjoin.cmd.DailyCmdTop;
import de.bergtiger.dailyjoin.cmd.DailyCommand;
import de.bergtiger.dailyjoin.dao.impl.PlayerDAOImpl;
import de.bergtiger.dailyjoin.dao.migration.DailyNameUpdate;
import de.bergtiger.dailyjoin.listener.DailyListener;
import de.bergtiger.dailyjoin.listener.PlayerInfoListener;
import de.bergtiger.dailyjoin.tab.DailyTabComplete;
import de.bergtiger.dailyjoin.utils.DailyReward;
import de.bergtiger.dailyjoin.utils.TigerLogger;
import de.bergtiger.dailyjoin.utils.config.DailyConfig;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.bergtiger.dailyjoin.dao.TigerConnection;

public class DailyJoin extends JavaPlugin{

	private static DailyJoin instance;
	private PluginManager pm = Bukkit.getPluginManager();
	
	/**
	 * Get plugin instance,
	 * available after onLoad.
	 * @return DailyJoin instance.
	 */
	public static DailyJoin inst() {
		return instance;
	}
	
	@Override
	public void onEnable() {
		// set instance
		instance = this;
		// set logger
		TigerLogger.setLogger(getLogger());
		// TODO nessesary ?
		TigerLogger.log(Level.INFO, "Starte DailyJoin...");
		// load configuration
		DailyConfig.load();
		// start SQLConnection
		TigerConnection.inst().reload();
		// register Listener
		pm.registerEvents(DailyListener.inst(), this);
		if(pm.isPluginEnabled("TigerList"))
			pm.registerEvents(PlayerInfoListener.inst(), this);
		// register Command
		getCommand("dailyjoin").setExecutor(new DailyCommand());
		getCommand("dailyjoin").setTabCompleter(new DailyTabComplete());
	}
	
	/**
	 * reload plugin.
	 */
	public void reload() {
		reloadConfig();
		DailyConfig.load();
		// end possible name update
		DailyNameUpdate.inst().endThread();
		// reload connection
		TigerConnection.inst().reload();
		// reload dao
		PlayerDAOImpl.inst().reload();
		// reload listener
		DailyListener.inst().reload();
		// reload reward
		DailyReward.inst().reload();
		// clear top player cache
		DailyCmdTop.inst().clear();
		// check if Listener needs to change
		// PlayerInfoCommandListener
		HandlerList.unregisterAll(PlayerInfoListener.inst());
		if(pm.isPluginEnabled("TigerList"))
			pm.registerEvents(PlayerInfoListener.inst(), instance);
	}
	
	@Override
	public void onDisable() {
		DailyNameUpdate.inst().endThread();
		TigerConnection.inst().closeConnection();
		TigerLogger.log(Level.INFO, "DailyJoin beendet.");
	}
}
