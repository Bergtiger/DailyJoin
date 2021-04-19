package de.bergtiger.dailyjoin;

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

	@Override
	public void onEnable() {
		
		System.out.println("Starte DailyJoin...");
		
		PluginManager pm = this.getServer().getPluginManager();
		
		new DailyConfig(this);
		
		this.sql = new MySQL(this);
		this.dl = new DailyListener(this);
		this.dc = new DailyCommand(this);
		this.dr = new DailyReward(this);
		this.dFile = new DailyFile(this);
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
	
	public void reload() {
		this.reloadConfig();
		this.dFile.reload();
		this.sql.reload();
		this.dl.reload();
		this.dc.reload();
		this.dr.reload();
	}
	
	@Override
	public void onDisable() {
		System.out.println("DailyJoin beendet.");
		this.sql.closeThread();
	}
}
