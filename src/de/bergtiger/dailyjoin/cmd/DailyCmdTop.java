package de.bergtiger.dailyjoin.cmd;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.bdo.TigerList;
import de.bergtiger.dailyjoin.DailyJoin;
import de.bergtiger.dailyjoin.dao.DailyDataBase;
import de.bergtiger.dailyjoin.dao.impl.PlayerDAOimpl;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.utils.TimeUtils;
import de.bergtiger.dailyjoin.utils.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static de.bergtiger.dailyjoin.utils.TigerPermission.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.function.Function;

public class DailyCmdTop {

	public static final String ASC = "ASC", DESC = "DESC";

	private static DailyCmdTop instance;

	public static DailyCmdTop inst() {
		if (instance == null)
			instance = new DailyCmdTop();
		return instance;
	}

	private DailyCmdTop() {
	}

	private HashMap<String, TigerList<DailyPlayer>> players = new HashMap<>();

	/**
	 * @param cs
	 * @param args
	 */
	public static void run(CommandSender cs, String[] args) {
		Bukkit.getScheduler().runTaskAsynchronously(DailyJoin.inst(),
				() -> DailyCmdTop.inst().showTopPlayers(cs, args));
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
			if (args.length >= 2) {
				column = args[1];
			}
			// set page
			if (args.length >= 3) {
				try {
					page = Integer.valueOf(args[2]);
				} catch (NumberFormatException e) {
					cs.spigot().sendMessage(Lang.build(Lang.NONUMBER.get().replace(Lang.VALUE, args[2])));
					return;
				}
			}
			// set order
			if (args.length >= 4) {
				order = args[3];
			}
			TigerList<DailyPlayer> players = getPlayers(cs.getName(), column, order);
			if (players != null && !players.isEmpty()) {
				players.setPage(page);
				showPage(cs, players);
			} else {
				// no player
				cs.spigot().sendMessage(Lang.build(Lang.NOPLAYER.get()));
			}
		} else {
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION.get()));
		}
	}

	/**
	 * 
	 * @param uuid
	 * @param column
	 * @param order
	 * @return
	 */
	private TigerList<DailyPlayer> getPlayers(String uuid, String column, String order) {
		// check if players contains uuid
		if (players != null && players.containsKey(uuid) && players.get(uuid).getColumn().equalsIgnoreCase(column)) {
			return players.get(uuid);
			// add to players if not exists
		}
		try {
			TigerList<DailyPlayer> list = PlayerDAOimpl.inst().getOrderedPlayers(column, order);
			list.setColumn(column);
			players.put(uuid, list);
			return players.get(uuid);
		} catch (NoSQLConnectionException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void showPage(CommandSender cs, TigerList<DailyPlayer> players) {
		if (cs != null) {
			if (players != null && !players.isEmpty()) {
				Function<? super DailyPlayer, ? extends Object> f = getMethod(players.getColumn());
				// header
				cs.spigot().sendMessage(Lang.build(Lang.TOP_HEADER.get().replace(Lang.VALUE, players.getColumn())));
				// show page
				try {
					for (int i = 0; i < players.getPageSize(); i++) {
						DailyPlayer dp = players.get(i + players.getPage() * players.getPageSize());
						cs.spigot().sendMessage(Lang.build(Lang.TOP_PLAYER.get().replace(Lang.PLAYER, dp.getName())
								.replace(Lang.VALUE, getValue(dp, f))));
					}
				} catch (IndexOutOfBoundsException e) {
					// abort
				}
				// footer
				cs.spigot()
						.sendMessage(Lang.build(Lang.TOP_PREV.get(),
								String.format("/daily top %s %d", players.getColumn(), players.getPage()), null, null),
								Lang.build(Lang.TOP_FOOTER.get()
										.replace(Lang.PAGE, Integer.toString(players.getPage() + 1)).replace(Lang.PAGE_MAX, Integer.toString(players.getPageMax()))),
								Lang.build(Lang.TOP_NEXT.get(),
										String.format("/daily top %s %d", players.getColumn(), players.getPage() + 1),
										null, null));
			} else {
				// no player
				cs.spigot().sendMessage(Lang.build(Lang.NOLIST.get()));
			}
		}
	}

	private String getValue(DailyPlayer dp, Function<? super DailyPlayer, ? extends Object> f) {
		if (dp != null) {
			Object o = f.apply(dp);
			if (o instanceof Timestamp)
				return TimeUtils.formated((Timestamp) o);
			// Integer
			if (o instanceof Integer)
				return ((Integer) o).toString();
			// String ?
			return o.toString();
		}
		return "";
	}

	private Function<? super DailyPlayer, ? extends Object> getMethod(String column) {
		if (column != null && !column.isEmpty()) {
			switch (column.toLowerCase()) {
			case DailyDataBase.UUID:
				return DailyPlayer::getUuid;
			case DailyDataBase.NAME:
				return DailyPlayer::getName;
			case DailyDataBase.LASTJOIN:
				return DailyPlayer::getLastjoin;
			case DailyDataBase.FIRSTJOIN:
				return DailyPlayer::getFirstjoin;
			case DailyDataBase.DAYS_TOTAL:
				return DailyPlayer::getDaysTotal;
			case DailyDataBase.DAYS_CONSECUTIVE:
				return DailyPlayer::getDaysConsecutive;
			}
		}
		return DailyPlayer::getDaysTotal;
	}

	public void clear() {
		players.clear();
	}
}
