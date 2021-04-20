package de.bergtiger.dailyjoin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.bergtiger.dailyjoin.dao.TigerConnection;

public class DailyListener implements Listener {

	private boolean sql;
	private int delay;
	private String p_admin = "dailyjoin.admin", p_user = "dailyjoin.user", p_join = "dailyjoin.join";

	private static DailyListener instance;

	/**
	 * get DailyListener instance.
	 * @return
	 */
	public static DailyListener inst() {
		if (instance == null)
			instance = new DailyListener();
		return instance;
	}

	private DailyListener() {
		setData();
	}

	/**
	 * reload configuration
	 */
	public void reload() {
		setData();
	}

	/**
	 * set configuration data. (delay and saveAsSQL)
	 */
	private void setData() {
		delay = dailyjoin.inst().getConfig().getInt("config.delay");
		sql = dailyjoin.inst().getConfig().getString("config.SQL").equalsIgnoreCase("true");
	}

	// TODO getDailySQL getDailyFile
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if ((p.hasPermission(p_admin) || p.hasPermission(p_user)) || p.hasPermission(p_join)) {
			Long time = this.delay * 20L;
			if (time < 0) {
				time = 10 * 20L;
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(dailyjoin.inst(), new Runnable() {
				@Override
				public void run() {
					// onPlayerOnline(p);
					if (p.isOnline()) {
						if (sql && TigerConnection.hasConnection()) {
							dailyjoin.inst().getDailySQL().dailyjoin_sql(p);
						} else {
							dailyjoin.inst().getDailyFile().dailyjoin_file(p);
						}
					}
				}
			}, time);
		}
	}
}
