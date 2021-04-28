package de.bergtiger.dailyjoin.cmd;

import de.bergtiger.dailyjoin.dailyjoin;
import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.utils.config.DailyConfig;
import de.bergtiger.dailyjoin.utils.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static de.bergtiger.dailyjoin.utils.TigerPermission.*;

public class DailyCmdPlugin {

	private DailyCmdPlugin() {}

	public static void run(CommandSender cs) {
		Bukkit.getScheduler().runTaskAsynchronously(dailyjoin.inst(), () -> new DailyCmdPlugin().showPlugin(cs));
	}

	private void showPlugin(CommandSender cs) {
		if (hasPermission(cs, ADMIN, PLUGIN)) {
			DailyConfig dc = DailyConfig.inst();
			// plugin info
			cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_HEADER.get()));
			// version
			cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_VERSION.get().replace(Lang.VALUE, dailyjoin.inst().getDescription().getVersion())));
			// time format
			cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_TIMEFORMAT.get().replace(Lang.VALUE, Lang.FORMAT_TIME.get())));
			if (dc.getBoolean(DailyConfig.DATA_FORMAT_SQL)) {
				// storage system sql
				cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_SYSTEM.get().replace(Lang.VALUE, DailyConfig.DATA_SQL)));
				// has connection
				cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_CONNECTED.get().replace(Lang.VALUE, Boolean.toString(TigerConnection.hasConnection()))));
				// reward disconnected
				cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_REWARD_RECONNECTION.get().replace(Lang.VALUE,
						dc.getValue(DailyConfig.REWARD_ON_SQL_CONNECTION_LOST))));
			} else {
				// storage system file
				cs.spigot()
						.sendMessage(Lang.build(Lang.PLUGIN_SYSTEM.get().replace(Lang.VALUE, DailyConfig.DATA_FILE)));
			}
			// login delay
			cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_DELAY.get().replace(Lang.VALUE, Integer.toString(dc.getInteger(DailyConfig.DELAY)))));
			// lists page size
			cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_PAGE_SIZE.get().replace(Lang.VALUE,Integer.toString(dc.getInteger(DailyConfig.PAGE_SIZE)))));
			cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_FOOTER.get()));
		} else {
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION.get()));
		}
	}
}
