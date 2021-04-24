package de.bergtiger.dailyjoin.cmd;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.bdo.TigerList;
import de.bergtiger.dailyjoin.dailyjoin;
import de.bergtiger.dailyjoin.dao.DailyDataBase;
import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.utils.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static de.bergtiger.dailyjoin.utils.TigerPermission.*;

import java.util.HashMap;

public class DailyCmdTop {

	public static final String ASC = "ASC", DESC = "DESC";

	private static DailyCmdTop instance;

	public static DailyCmdTop inst() {
		if (instance == null)
			instance = new DailyCmdTop();
		return instance;
	}

	private DailyCmdTop() {}

	private HashMap<String, TigerList<DailyPlayer>> players = new HashMap<>();

	/**
	 * @param cs
	 * @param args
	 */
	public static void run(CommandSender cs, String[] args) {
		Bukkit.getScheduler().runTaskAsynchronously(dailyjoin.inst(), () -> DailyCmdTop.inst().showTopPlayers(cs, args));
	}

	/**
	 * cmd: /daily top(0) [column](1) [page](2) [order](3)
	 *
	 * @param cs
	 * @param args
	 */
	private void showTopPlayers(CommandSender cs, String[] args) {
		if (hasPermission(cs, ADMIN, TOP)) {
			int page = 0;
			String column = DailyDataBase.DAYS_TOTAL, order = ASC;
			// set column
			if(args.length >= 2) {
			    column = args[1];
            }
			// set page
            if(args.length >= 3) {
                try {
                    page = Integer.valueOf(args[2]);
                } catch (NumberFormatException e) {
                    cs.spigot().sendMessage(Lang.buildTC(Lang.NONUMBER.get().replace(Lang.VALUE, args[2])));
                    return;
                }
            }
            // set order
            if(args.length >= 4) {
                order = args[3];
            }
            TigerList<DailyPlayer> players = getPlayers(cs.getName(), column, order);
            if(players != null && !players.isEmpty()) {
                players.setPage(page);
                showPage(cs, players);
            } else {
                // no player
                cs.spigot().sendMessage(Lang.buildTC(Lang.NOPLAYER.get()));
            }
		} else {
			cs.spigot().sendMessage(Lang.buildTC(Lang.NOPERMISSION.get()));
		}
	}

	private TigerList<DailyPlayer> getPlayers(String uuid, String column, String order) {
		// check if players contains uuid
		if (players != null && players.containsKey(uuid)) {
			return players.get(uuid);
			// add to players if not exists
		}
		try {
			TigerList<DailyPlayer> list = TigerConnection.inst().getPlayerDAO().getOrderedPlayers(column, order);
			return players.put(uuid, list);
		} catch (NoSQLConnectionException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void showPage(CommandSender cs, TigerList<DailyPlayer> players) {
		if (cs != null) {
			if (players != null && !players.isEmpty()) {
				// header
				cs.spigot().sendMessage(Lang.buildTC("header"));
				// show page
				for (int i = 0; i < players.getPageSize(); i++) {
					DailyPlayer dp = players.get(i);
					cs.spigot().sendMessage(Lang.buildTC("player".replace(Lang.PLAYER, dp.getName()).replace(Lang.VALUE, Integer.toString(dp.getDaysTotal()))));
				}
				// footer
				cs.spigot().sendMessage(Lang.buildTC("prev"), Lang.buildTC("footer"), Lang.buildTC("next"));
			} else {
				// no player
				cs.spigot().sendMessage(Lang.buildTC(Lang.NOLIST.get()));
			}
		}
	}

	public void clear() {
		players.clear();
	}
}
