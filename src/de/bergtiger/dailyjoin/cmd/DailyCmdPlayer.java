package de.bergtiger.dailyjoin.cmd;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.dailyjoin;
import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.utils.TimeUtils;
import de.bergtiger.dailyjoin.utils.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

import static de.bergtiger.dailyjoin.utils.TigerPermission.*;

public class DailyCmdPlayer {

	private DailyCmdPlayer() {
	}

	public static void run(CommandSender cs, String[] args) {
		Bukkit.getScheduler().runTaskAsynchronously(dailyjoin.inst(), () -> new DailyCmdPlayer().showPlayer(cs, args));
	}

	/**
	 * cmd: /daily player(0) [uuid](1)
	 *
	 * @param cs
	 * @param args
	 */
	private void showPlayer(CommandSender cs, String[] args) {
		if (hasPermission(cs, ADMIN, USER, PLAYER)) {
			String uuid = cs instanceof Player ? ((Player) cs).getUniqueId().toString() : cs.getName();
			// check who is searched
			if (args.length >= 2 && (args[1].equalsIgnoreCase(uuid) || hasPermission(cs, ADMIN, PLAYER)))
				uuid = args[1];
			// get player
			DailyPlayer dp;
			if ((dp = getPlayer(uuid)) != null) {
				showPlayer(cs, dp);
			} else {
				cs.spigot().sendMessage(Lang.buildTC(Lang.NOPLAYER.get().replace(Lang.PLAYER, uuid)));
			}
		} else {
			cs.spigot().sendMessage(Lang.buildTC(Lang.NOPERMISSION.get()));
		}
	}

	private DailyPlayer getPlayer(String uuid) {
		try {
			return TigerConnection.inst().getPlayerDAO().getPlayer(uuid);
		} catch (NoSQLConnectionException e) {
			TigerConnection.noConnection();
		}
		return null;
	}

    /**
     * shows DailyPlayer in CommandSender's chat.
     * @param cs CommandSender who asked for information
     * @param dp DailyPlayer information to be shown
     */
	private void showPlayer(@Nonnull CommandSender cs, @Nonnull DailyPlayer dp) {
		// Header
		cs.spigot().sendMessage(Lang.buildTC(Lang.PlayerHeader.get().replace(Lang.PLAYER, dp.getName())));
		// Data
		cs.spigot().sendMessage(Lang.buildTC(Lang.PlayerFirstJoin.get().replace(Lang.VALUE, TimeUtils.formated(dp.getFirstjoin()))));
		cs.spigot().sendMessage(Lang.buildTC(Lang.PlayerLastJoin.get().replace(Lang.VALUE, TimeUtils.formated(dp.getLastjoin()))));
		cs.spigot().sendMessage(Lang.buildTC(Lang.PlayerDay.get().replace(Lang.VALUE, Integer.toString(dp.getDaysConsecutive()))));
		cs.spigot().sendMessage(Lang.buildTC(Lang.PlayerTotalDays.get().replace(Lang.VALUE, Integer.toString(dp.getDaysTotal()))));
		// Footer
		cs.spigot().sendMessage(Lang.buildTC(Lang.PlayerFooter.get()));
	}
}
