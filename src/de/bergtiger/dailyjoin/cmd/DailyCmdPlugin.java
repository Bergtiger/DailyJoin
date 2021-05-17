package de.bergtiger.dailyjoin.cmd;

import de.bergtiger.dailyjoin.DailyJoin;
import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.dao.migration.DailyNameUpdate;
import de.bergtiger.dailyjoin.utils.config.DailyConfig;
import de.bergtiger.dailyjoin.utils.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static de.bergtiger.dailyjoin.utils.permission.TigerPermission.*;

public class DailyCmdPlugin {

	private DailyCmdPlugin() {
	}

	/**
	 * runs daily plugin information command in its own thread.
	 * 
	 * @param cs   {@link CommandSender}
	 */
	public static void run(CommandSender cs) {
		Bukkit.getScheduler().runTaskAsynchronously(DailyJoin.inst(), () -> new DailyCmdPlugin().showPlugin(cs));
	}

	/**
	 * /daily info
	 * 
	 * @param cs {@link CommandSender}
	 */
	private void showPlugin(CommandSender cs) {
		if (hasPermission(cs, ADMIN, PLUGIN)) {
			DailyConfig dc = DailyConfig.inst();
			// plugin info
			cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_HEADER.get()));
			// version
			cs.spigot().sendMessage(Lang.build(
					Lang.PLUGIN_VERSION.get().replace(Lang.VALUE, DailyJoin.inst().getDescription().getVersion())));
			// time format
			cs.spigot()
					.sendMessage(Lang.build(Lang.PLUGIN_TIMEFORMAT.get().replace(Lang.VALUE, Lang.FORMAT_TIME.get())));
			if (dc.getBoolean(DailyConfig.DATA_FORMAT_SQL)) {
				// storage system sql
				cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_SYSTEM.get().replace(Lang.VALUE, DailyConfig.DATA_SQL)));
				// has connection
				cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_CONNECTED.get().replace(Lang.VALUE,
						Boolean.toString(TigerConnection.hasConnection()))));
				// reward disconnected
				cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_REWARD_RECONNECTION.get().replace(Lang.VALUE,
						dc.getValue(DailyConfig.REWARD_ON_SQL_CONNECTION_LOST))));
				// merge on connection
				cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_MERGE_ON_CONNECTION.get().replace(Lang.VALUE,
						dc.getValue(DailyConfig.LOAD_FILE_ON_SQL_CONNECTION))));
			} else {
				// storage system file
				cs.spigot()
						.sendMessage(Lang.build(Lang.PLUGIN_SYSTEM.get().replace(Lang.VALUE, DailyConfig.DATA_FILE)));
			}
			// login delay
			cs.spigot().sendMessage(Lang.build(
					Lang.PLUGIN_DELAY.get().replace(Lang.VALUE, Integer.toString(dc.getInteger(DailyConfig.DELAY)))));
			// lists page size
			cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_PAGE_SIZE.get().replace(Lang.VALUE,
					Integer.toString(dc.getInteger(DailyConfig.PAGE_SIZE)))));
			// update names
			cs.spigot().sendMessage(Lang.build((
					DailyNameUpdate.inst().hasThread() ?
							Lang.PLUGIN_UPDATE_NAMESVALUE.get() : Lang.PLUGIN_UPDATE_NAMES.get())
								.replace(Lang.VALUE, Boolean.toString(DailyNameUpdate.inst().hasThread()))
								.replace(Lang.DATA, DailyNameUpdate.inst().getProcessFormated())));
			
			cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_FOOTER.get()));
		} else {
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION.get()));
		}
	}
}
