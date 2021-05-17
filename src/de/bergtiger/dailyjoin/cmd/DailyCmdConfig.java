package de.bergtiger.dailyjoin.cmd;

import de.bergtiger.dailyjoin.DailyJoin;
import de.bergtiger.dailyjoin.utils.config.DailyConfig;
import de.bergtiger.dailyjoin.utils.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static de.bergtiger.dailyjoin.utils.permission.TigerPermission.*;
import static de.bergtiger.dailyjoin.utils.config.DailyConfig.*;

public class DailyCmdConfig {

	/**
	 * runs daily set configuration command in its own thread.
	 * 
	 * @param cs   {@link CommandSender}
	 * @param args command arguments
	 */
	public static void run(CommandSender cs, String[] args) {
		Bukkit.getScheduler().runTaskAsynchronously(DailyJoin.inst(),
				() -> new DailyCmdConfig().modifyConfig(cs, args));
	}

	/**
	 * /daily [config](0) [type](1) [value](2)
	 * 
	 * @param cs   {@link CommandSender}
	 * @param args command arguments
	 */
	private void modifyConfig(CommandSender cs, String[] args) {
		if (hasPermission(cs, ADMIN, CONFIG)) {
			if (args.length >= 3) {
				// check correct type
				switch (args[1].toLowerCase()) {
				case DATA_FORMAT: {
					// allowed value (sql|file)
					setValue(cs, DATA_FORMAT, args[2], String.format("(?i)(%s|%s)", DATA_SQL, DATA_FILE), String.class);
				}
					break;
				case DELAY: {
					// allowed value (number >= 0)
					setValue(cs, DELAY, args[2], "\\d*", Integer.class);
				}
					break;
				case PAGE_SIZE: {
					// allowed value (number >= 1)
					setValue(cs, PAGE_SIZE, args[2], "[1-9]\\d*", Integer.class);
				}
					break;
				case FILE_DAYS_TOTAL: {
					// allowed value
					setValue(cs, FILE_DAYS_TOTAL, args[2], ".+\\.yml", String.class);
				}
					break;
				case FILE_DAYS_CONSECUTIVE: {
					// allowed value
					setValue(cs, FILE_DAYS_CONSECUTIVE, args[2], ".+\\.yml", String.class);
				}
					break;
				case LOAD_FILE_ON_SQL_CONNECTION: {
					// allowed value (boolean)
					setValue(cs, LOAD_FILE_ON_SQL_CONNECTION, args[2], "(?i)(true|file)", Boolean.class);
				}
					break;
				case REWARD_ON_SQL_CONNECTION_LOST: {
					// allowed value (boolean)
					setValue(cs, REWARD_ON_SQL_CONNECTION_LOST, args[2], "(?i)(true|file)", Boolean.class);
				}
					break;
				default:
					cs.spigot().sendMessage(Lang.build(Lang.WRONG_ARGUMENT.get()));
				}
			} else {
				cs.spigot().sendMessage(Lang.build(Lang.WRONG_ARGUMENT.get()));
			}
		} else {
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION.get()));
		}
	}

	/**
	 * handle set value. check if value matches RegEx.
	 * 
	 * @param cs    {@link CommandSender} to show prozess
	 * @param key   configuration identifier
	 * @param value new value to save in configuration
	 * @param regex check value RegEx
	 */
	private void setValue(CommandSender cs, String key, String value, String regex, Class<?> clazz) {
		// allowed value (regex)
		if (value.matches(regex)) {
			String old = DailyConfig.inst().getValueSave(key);
			try {
				DailyConfig.inst().setValue(key, value, clazz);
				cs.spigot().sendMessage(Lang.build(Lang.CONFIG_CHANGED_SUCCESS.get().replace(Lang.TYPE, key)
						.replace(Lang.DATA, old).replace(Lang.VALUE, value)));
			} catch (Exception e) {
				DailyConfig.inst().setValue(key, old);
				cs.spigot().sendMessage(Lang.build(Lang.CONFIG_CHANGED_ERROR.get().replace(Lang.TYPE, key)
						.replace(Lang.DATA, old).replace(Lang.VALUE, value)));
			}
		} else {
			// wrong argument
			cs.spigot().sendMessage(
					Lang.build(Lang.CONFIG_WRONG_ARGUMENT.get().replace(Lang.TYPE, key).replace(Lang.VALUE, value)));
		}
	}
}
