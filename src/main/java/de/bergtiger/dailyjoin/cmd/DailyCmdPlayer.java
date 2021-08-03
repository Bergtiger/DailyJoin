package de.bergtiger.dailyjoin.cmd;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.DailyJoin;
import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.dao.impl.PlayerDAOImpl;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.utils.TimeUtils;
import de.bergtiger.dailyjoin.utils.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static de.bergtiger.dailyjoin.utils.permission.TigerPermission.*;

public class DailyCmdPlayer {

	private DailyCmdPlayer() {
	}

	/**
	 * runs daily add command in its own thread.
	 * 
	 * @param cs   {@link CommandSender}}
	 * @param args command arguments
	 */
	public static void run(CommandSender cs, String[] args) {
		Bukkit.getScheduler().runTaskAsynchronously(DailyJoin.inst(), () -> new DailyCmdPlayer().showPlayer(cs, args));
	}

	/**
	 * /daily player(0) [uuid](1)
	 *
	 * @param cs   {@link CommandSender}
	 * @param args command arguments
	 */
	private void showPlayer(CommandSender cs, String[] args) {
		if (hasPermission(cs, ADMIN, USER, PLAYER)) {
			String uuid = cs instanceof Player ? ((Player) cs).getUniqueId().toString() : cs.getName();
			// check who is searched
			if (args.length >= 2 && (args[1].equalsIgnoreCase(uuid) || hasPermission(cs, ADMIN, PLAYER)))
				uuid = args[1];
			// get player
			try {
				DailyPlayer dp;
				if ((dp = PlayerDAOImpl.inst().getPlayer(uuid)) != null) {
					showPlayer(cs, dp);
				} else {
					cs.spigot().sendMessage(Lang.build(Lang.NOPLAYER.get().replace(Lang.PLAYER, uuid)));
				}
			} catch (NoSQLConnectionException e) {
				cs.spigot().sendMessage(Lang.build(Lang.NOCONNECTION.get()));
				TigerConnection.noConnection();
			}
		} else {
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION.get()));
		}
	}

	/**
	 * shows DailyPlayer in CommandSender's chat.
	 * 
	 * @param cs {@link CommandSender} who asked for information
	 * @param dp {@link DailyPlayer} information to be shown
	 */
	private void showPlayer(CommandSender cs, DailyPlayer dp) {
		// Header
		cs.spigot().sendMessage(Lang.build(Lang.PLAYER_HEADER.get().replace(Lang.PLAYER, dp.getName())));
		// Data
		cs.spigot().sendMessage(
				Lang.build(Lang.PLAYER_JOIN_FIRST.get().replace(Lang.VALUE, TimeUtils.formatted(dp.getFirstjoin()))));
		cs.spigot().sendMessage(
				Lang.build(Lang.PLAYER_JOIN_LAST.get().replace(Lang.VALUE, TimeUtils.formatted(dp.getLastjoin()))));
		cs.spigot().sendMessage(Lang.build(
				Lang.PLAYER_DAYS_CONSECUTIVE.get().replace(Lang.VALUE, Integer.toString(dp.getDaysConsecutive()))));
		cs.spigot().sendMessage(
				Lang.build(Lang.PLAYER_DAYS_TOTAL.get().replace(Lang.VALUE, Integer.toString(dp.getDaysTotal()))));
		// Footer
		cs.spigot().sendMessage(Lang.build(Lang.PLAYER_FOOTER.get()));
	}
}
