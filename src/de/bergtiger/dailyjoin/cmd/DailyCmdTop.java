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

import static de.bergtiger.dailyjoin.utils.permission.TigerPermission.*;

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

	private final HashMap<String, TigerList<DailyPlayer>> players = new HashMap<>();

	/**
	 * show player list sorted by a given column.
	 * 
	 * @param cs   CommandSender
	 * @param args command message
	 */
	public static void run(CommandSender cs, String[] args) {
		Bukkit.getScheduler().runTaskAsynchronously(DailyJoin.inst(),
				() -> DailyCmdTop.inst().showTopPlayers(cs, args));
	}

	/**
	 * cmd: /daily top(0) [column](1) [page](2) [order](3)
	 *
	 * @param cs   CommandSender
	 * @param args command message
	 */
	private void showTopPlayers(CommandSender cs, String[] args) {
		if (hasPermission(cs, ADMIN, TOP)) {
			int page = 0;
			String column = DailyDataBase.DAYS_TOTAL, order = ASC;
			// set column
			if (args.length >= 2) {
				// check column allowed
				if (args[1].matches(String.format("(?i)(%s|%s|%s|%s)", DailyDataBase.DAYS_TOTAL,
						DailyDataBase.DAYS_CONSECUTIVE, DailyDataBase.LASTJOIN, DailyDataBase.FIRSTJOIN))) {
					column = args[1];
				} else {
					cs.spigot().sendMessage(Lang.build(Lang.WRONG_ARGUMENT.get()));
					return;
				}
			}
			// set page
			if (args.length >= 3) {
				try {
					page = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					cs.spigot().sendMessage(Lang.build(Lang.NONUMBER.get().replace(Lang.VALUE, args[2])));
					return;
				}
			}
			// set order
			if (args.length >= 4) {
				// check order allowed
				if (args[3].matches(String.format("(?i)(%s|%s)", ASC, DESC))) {
					order = args[3];
				} else {
					cs.spigot().sendMessage(Lang.build(Lang.WRONG_ARGUMENT.get()));
					return;
				}
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
	 * get List of DailyPlayers from cache or Database if not in cache. will reset
	 * cache if search argument is different from cache
	 * 
	 * @param uuid   CommandSender identification as key for cache
	 * @param column searched column
	 * @param order  order for searched column asc or desc
	 * @return TigerList with DailyPlayer or null if error
	 */
	private TigerList<DailyPlayer> getPlayers(String uuid, String column, String order) {
		// check if players contains uuid
		if (players.containsKey(uuid) && players.get(uuid).getColumn().equalsIgnoreCase(column)) {
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

	/**
	 * shows a Page from given List to the CommandSender
	 * 
	 * @param cs      CommandSender
	 * @param players TigerList with DailyPlayers to show
	 */
	private void showPage(CommandSender cs, TigerList<DailyPlayer> players) {
		if (cs != null) {
			if (players != null && !players.isEmpty()) {
				Function<? super DailyPlayer, Object> f = getMethod(players.getColumn());
				// header
				cs.spigot().sendMessage(Lang.build(Lang.TOP_HEADER.get().replace(Lang.VALUE, players.getColumn())));
				// show page
				try {
					if (hasPermission(cs, ADMIN, PLAYER)) {
						for (int i = 0; i < players.getPageSize(); i++) {
							DailyPlayer dp = players.get(i + players.getPage() * players.getPageSize());
							cs.spigot()
									.sendMessage(Lang.build(
											Lang.TOP_PLAYER.get().replace(Lang.PLAYER, dp.getName()).replace(Lang.VALUE,
													getValue(dp, f)),
											"/daily player " + dp.getUuid(), buildHover(dp), null));
						}
					} else {
						for (int i = 0; i < players.getPageSize(); i++) {
							DailyPlayer dp = players.get(i + players.getPage() * players.getPageSize());
							cs.spigot().sendMessage(Lang.build(Lang.TOP_PLAYER.get().replace(Lang.PLAYER, dp.getName())
									.replace(Lang.VALUE, getValue(dp, f))));
						}
					}
				} catch (IndexOutOfBoundsException e) {
					// abort
				}
				// footer
				cs.spigot()
						.sendMessage(Lang.build(Lang.TOP_PREV.get(),
								String.format("/daily top %s %d", players.getColumn(), players.getPage()),
								Lang.TOP_HOVER_PREV.get(), null),
								Lang.build(Lang.TOP_FOOTER.get()
										.replace(Lang.PAGE, Integer.toString(players.getPage() + 1))
										.replace(Lang.PAGE_MAX, Integer.toString(players.getPageMax()))),
								Lang.build(Lang.TOP_NEXT.get(),
										String.format("/daily top %s %d", players.getColumn(), players.getPage() + 1),
										Lang.TOP_HOVER_NEXT.get(), null));
			} else {
				// no player
				cs.spigot().sendMessage(Lang.build(Lang.NOLIST.get()));
			}
		}
	}

	private String getValue(DailyPlayer dp, Function<? super DailyPlayer, Object> f) {
		if (dp != null) {
			Object o = f.apply(dp);
			// Integer
			if (o instanceof Integer)
				return ((Integer) o).toString();
			// Timestamp
			if (o instanceof Timestamp)
				return TimeUtils.formated((Timestamp) o);
			// String ?
			return o.toString();
		}
		return "";
	}

	private Function<? super DailyPlayer, Object> getMethod(String column) {
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

	/**
	 * build string from player data for hover effect in chat.
	 * 
	 * @param dp player
	 * @return string representing player
	 */
	private String buildHover(DailyPlayer dp) {
		if (dp != null) {
			return new StringBuilder().append(Lang.HOVER_PLAYER_NAME.get().replace(Lang.VALUE, dp.getName()))
					.append("\n")
					.append(Lang.HOVER_PLAYER_DAYS_TOTAL.get().replace(Lang.VALUE, Integer.toString(dp.getDaysTotal())))
					.append("\n")
					.append(Lang.HOVER_PLAYER_DAYS_CONSECUTIVE.get().replace(Lang.VALUE,
							Integer.toString(dp.getDaysConsecutive())))
					.append("\n")
					.append(Lang.HOVER_PLAYER_LASTJOIN.get().replace(Lang.VALUE, TimeUtils.formated(dp.getLastjoin())))
					.append("\n").append(Lang.HOVER_PLAYER_FIRSTJOIN.get().replace(Lang.VALUE,
							TimeUtils.formated(dp.getFirstjoin())))
					.toString();
		}
		return null;
	}

	public void clear() {
		players.clear();
	}
}
