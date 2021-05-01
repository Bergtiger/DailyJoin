package de.bergtiger.dailyjoin.cmd;

import de.bergtiger.dailyjoin.DailyJoin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import de.bergtiger.dailyjoin.utils.lang.Lang;
import static de.bergtiger.dailyjoin.utils.TigerPermission.*;

public class DailyCommand implements CommandExecutor {
	
	public static final String CMD_CMD = "dailyjoin", CMD_TOP = "top", CMD_SET = "set", CMD_ADD = "add", CMD_INFO = "info", CMD_RELOAD = "reload", CMD_PLAYER = "player";
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		//info - set - reload - config - player
		if(args.length > 0){
			switch (args[0]) {
				case CMD_TOP: DailyCmdTop.run(cs, args); break;

				case CMD_SET: DailyCmdSet.run(cs, args); break;

				case CMD_ADD: DailyCmdAdd.run(cs, args); break;

				case CMD_INFO: DailyCmdPlugin.run(cs); break;

				case CMD_RELOAD: daily_reload(cs); break;

				case CMD_PLAYER: DailyCmdPlayer.run(cs, args); break;

		//		case "config": daily_config(cs, args);break;
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
	 * @param cs CommandSender to show all available commands
	 */
	private void daily_command(CommandSender cs){
		if(hasPermission(cs, ADMIN, CMD)){
			String cmdShort = String.format("/%s %s", CMD_CMD, "%s");
			String cmdLong = String.format("/%s %s ", CMD_CMD, "%s");
			cs.spigot().sendMessage(Lang.build(Lang.DAILY_INFO_CMD.get()));
			cs.spigot().sendMessage(Lang.build(Lang.DAILY_INFO_TOP.get(), null, null, String.format(cmdShort, CMD_TOP)));
			// Set
			if(hasPermission(cs, ADMIN, SET)) {
				cs.spigot().sendMessage(Lang.build(Lang.DAILY_INFO_SET.get(), null, null, String.format(cmdLong, CMD_SET)));
				cs.spigot().sendMessage(Lang.build(Lang.DAILY_INFO_ADD.get(), null, null, String.format(cmdLong, CMD_ADD)));
			}
			// PluginInfo
			if(hasPermission(cs, ADMIN, PLUGIN))
				cs.spigot().sendMessage(Lang.build(Lang.DAILY_INFO_INFO.get(), String.format(cmdShort, CMD_INFO), null, null));
			// Reload
			if(hasPermission(cs, ADMIN, RELOAD))
				cs.spigot().sendMessage(Lang.build(Lang.DAILY_INFO_RELOAD.get(), String.format(cmdShort, CMD_RELOAD), null, null));
			// Player
			if(hasPermission(cs, ADMIN, PLAYER))
				cs.spigot().sendMessage(Lang.build(Lang.DAILY_INFO_PLAYER.get(), null, null, String.format(cmdLong, CMD_PLAYER)));
		} else {
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION.get()));
		}
	}

	/**
	 * reloads dailyjoin configuration.
	 * @param cs CommandSender to show success
	 */
	private void daily_reload(CommandSender cs){
		if(hasPermission(cs, ADMIN, RELOAD)){
			DailyJoin.inst().reload();
			cs.spigot().sendMessage(Lang.build(Lang.DAILY_RELOAD.get()));
		} else {
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION.get()));
		}
	}
}
