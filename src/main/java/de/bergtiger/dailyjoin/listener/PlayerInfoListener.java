package de.bergtiger.dailyjoin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.dao.impl.PlayerDAOImpl;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.utils.lang.Lang;
import de.bergtiger.tigerlist.event.PlayerInfoEvent;

import static de.bergtiger.dailyjoin.utils.permission.TigerPermission.*;

public class PlayerInfoListener implements Listener {

	private static PlayerInfoListener instance;

	public static PlayerInfoListener inst() {
		if (instance == null)
			instance = new PlayerInfoListener();
		return instance;
	}

	private PlayerInfoListener() {
	}

	@EventHandler
	public void onPlayerInfo(PlayerInfoEvent event) {
		if (!event.isCancelled()) {
			if (hasPermission(event.getCommandSender(), ADMIN, USER, PLAYER)) {
				try {
					DailyPlayer dp;
					if ((dp = PlayerDAOImpl.inst().getPlayer(event.getPlayer().getUuid())) != null) {
						event.addMessage(
							Lang.build(
									Lang.PLAYER_DAYS_CONSECUTIVE.get()
										.replace(Lang.VALUE, Integer.toString(dp.getDaysConsecutive()))),
							Lang.build(
									Lang.PLAYER_DAYS_TOTAL.get()
										.replace(Lang.VALUE, Integer.toString(dp.getDaysTotal()))));
					}
				} catch (NoSQLConnectionException e) {
					event.getCommandSender().spigot().sendMessage(Lang.build(Lang.NOCONNECTION.get()));
					TigerConnection.noConnection();
				}
			}
		}
	}
}
