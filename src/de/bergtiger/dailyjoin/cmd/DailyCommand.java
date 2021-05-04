package de.bergtiger.dailyjoin.cmd;

import de.bergtiger.dailyjoin.dao.migration.DailyFileToSQL;
import de.bergtiger.dailyjoin.DailyJoin;
import de.bergtiger.dailyjoin.dao.migration.DailySQLToFile;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import de.bergtiger.dailyjoin.utils.lang.Lang;
import static de.bergtiger.dailyjoin.utils.permission.TigerPermission.*;

public class DailyCommand implements CommandExecutor {

	public static final String
	/** general command */
	CMD_CMD = "dailyjoin",
			/** list players */
			CMD_TOP = "top",
			/** set a specific value */
			CMD_SET = "set",
			/** add a specific value */
			CMD_ADD = "add",
			/** plugin info */
			CMD_INFO = "info",
			/** reload configuration and cache */
			CMD_RELOAD = "reload",
			/** player statistic */
			CMD_PLAYER = "player",
			/** migration to SQL or File */
			CMD_MIGRATION = "migration",
			/** change config */
			CMD_CONFIG = "config",

			SQL_TO_FILE = "sql_to_file", FILE_TO_SQL = "file_to_sql";

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		// info - set - reload - config - player
		if (args.length > 0) {
			switch (args[0]) {
			case CMD_TOP:
				DailyCmdTop.run(cs, args);
				break;

			case CMD_SET:
				DailyCmdSet.run(cs, args);
				break;

			case CMD_ADD:
				DailyCmdAdd.run(cs, args);
				break;

			case CMD_INFO:
				DailyCmdPlugin.run(cs);
				break;

			case CMD_RELOAD:
				daily_reload(cs);
				break;

			case CMD_PLAYER:
				DailyCmdPlayer.run(cs, args);
				break;

			case CMD_MIGRATION:
				migration(cs, args);
				break;

			case CMD_CONFIG:
				DailyCmdConfig.run(cs, args);
				break;

			default: {
				cs.sendMessage(Lang.WRONG_ARGUMENT.colored());
				return true;
			}
			}
		} else {
			daily_command(cs);
		}
		return true;
	}

	/**
	 * show daily commands.
	 * 
	 * @param cs CommandSender to show all available commands
	 */
	private void daily_command(CommandSender cs) {
		if (hasPermission(cs, ADMIN, CMD)) {
			String cmdShort = String.format("/%s %s", CMD_CMD, "%s");
			String cmdLong = String.format("/%s %s ", CMD_CMD, "%s");
			// if CommandSender has not ADMIN or CMD, how did he get here ?
			cs.spigot().sendMessage(Lang.build(Lang.INFO_CMD.get()));
			// Top
			if (hasPermission(cs, ADMIN, TOP)) {
				cs.spigot().sendMessage(Lang.build(Lang.INFO_TOP.get(), null, Lang.INFO_HOVER_TOP.get(), String.format(cmdShort, CMD_TOP)));
			}
			// Set
			if (hasPermission(cs, ADMIN, SET)) {
				cs.spigot().sendMessage(
						Lang.build(Lang.INFO_SET.get(), null, Lang.INFO_HOVER_SET.get(), String.format(cmdLong, CMD_SET)));
				cs.spigot().sendMessage(
						Lang.build(Lang.INFO_ADD.get(), null, Lang.INFO_HOVER_ADD.get(), String.format(cmdLong, CMD_ADD)));
			}
			// PluginInfo
			if (hasPermission(cs, ADMIN, PLUGIN))
				cs.spigot().sendMessage(
						Lang.build(Lang.INFO_INFO.get(), String.format(cmdShort, CMD_INFO), Lang.INFO_HOVER_INFO.get(), null));
			// Reload
			if (hasPermission(cs, ADMIN, RELOAD))
				cs.spigot().sendMessage(
						Lang.build(Lang.INFO_RELOAD.get(), String.format(cmdShort, CMD_RELOAD), Lang.INFO_HOVER_RELOAD.get(), null));
			// Migration
			if (hasPermission(cs, ADMIN, MIGRATION))
				cs.spigot().sendMessage(
						Lang.build(Lang.INFO_MIGRATION.get(), null, Lang.INFO_HOVER_MIGRATION.get(), String.format(cmdLong, CMD_MIGRATION)));
			// Config
			if (hasPermission(cs, ADMIN, CONFIG))
				cs.spigot().sendMessage(
						Lang.build(Lang.INFO_CONFIG.get(), null, Lang.INFO_HOVER_CONFIG.get(), String.format(cmdLong, CMD_CONFIG)));
			// Player
			if (hasPermission(cs, ADMIN, PLAYER))
				cs.spigot().sendMessage(
						Lang.build(Lang.INFO_PLAYER.get(), null, Lang.INFO_HOVER_PLAYER.get(), String.format(cmdLong, CMD_PLAYER)));
		} else {
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION.get()));
		}
	}

	/**
	 * reloads dailyjoin configuration.
	 * 
	 * @param cs CommandSender to show success
	 */
	private void daily_reload(CommandSender cs) {
		if (hasPermission(cs, ADMIN, RELOAD)) {
			DailyJoin.inst().reload();
			cs.spigot().sendMessage(Lang.build(Lang.DAILY_RELOAD.get()));
		} else {
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION.get()));
		}
	}

	/**
	 * migrates player values from source to destination.
	 * 
	 * @param cs   CommandSender to show success
	 * @param args migration(0) type(1)
	 */
	private void migration(CommandSender cs, String[] args) {
		if (hasPermission(cs, ADMIN, MIGRATION)) {
			if (args.length >= 2) {
				switch (args[1].toLowerCase()) {
				case FILE_TO_SQL:
					DailyFileToSQL.run(cs);
					break;
				case SQL_TO_FILE:
					DailySQLToFile.run(cs);
					break;
				default:
					cs.spigot().sendMessage(Lang.build(Lang.WRONG_ARGUMENT.get()));
				}
			}
		} else {
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION.get()));
		}
	}
}
