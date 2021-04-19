package de.bergtiger.dailyjoin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DailyListener implements Listener{
	
	private dailyjoin plugin;
	private boolean sql;
	private int delay;
	private String
				p_admin = "dailyjoin.admin",
				p_user = "dailyjoin.user",
				p_join = "dailyjoin.join";
	

	public DailyListener(dailyjoin plugin) {
		this.plugin = plugin;
		this.setData(plugin);
	}
	
	public void reload(){
		this.plugin.reloadConfig();
		this.setData(plugin);
	}
	
	private void setData(dailyjoin plugin) {
		this.delay = plugin.getConfig().getInt("config.delay");
		if(plugin.getConfig().getString("config.SQL").equalsIgnoreCase("true")){
			this.sql = true;
		} else {
			this.sql = false;
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if((p.hasPermission(p_admin)||p.hasPermission(p_user))||p.hasPermission(p_join)){
			Long time = this.delay * 20L;
			if(time < 0){
				time = 10*20L;
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin,new Runnable(){
				@Override
				public void run() {
					//onPlayerOnline(p);
					if(p.isOnline()){
						if(sql && plugin.getMySQL().hasConnection()){
							plugin.getDailySQL().dailyjoin_sql(p);
						} else {
							plugin.getDailyFile().dailyjoin_file(p);
						}
					}
				}
			}, time);
		}
	}
}
