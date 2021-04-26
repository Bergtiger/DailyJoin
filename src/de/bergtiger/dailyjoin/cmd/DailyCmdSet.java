package de.bergtiger.dailyjoin.cmd;

import de.bergtiger.dailyjoin.dailyjoin;
import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;
import de.bergtiger.dailyjoin.utils.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import static de.bergtiger.dailyjoin.utils.TigerPermission.*;

public class DailyCmdSet {

    public static final String DAYS_TOTAL = "daysTotal", DAYS_CONSECUTIVE = "daysConsecutive";

    private DailyCmdSet(){}

    public static void run(CommandSender cs, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(dailyjoin.inst(), () -> new DailyCmdSet().setPlayerData(cs, args));
    }

    /**
     * cmd: /daily set(0) [uuid](1) [type](2) [value](3)
     * @param cs
     * @param args
     */
    private void setPlayerData(CommandSender cs, String[] args) {
        if(hasPermission(cs, ADMIN, SET)) {
            if (args.length >= 4) {
                try {
                    String uuid = args[1];
                    String type = args[2];
                    int value = Integer.valueOf(args[3]);
                    // get Player
                    try {
						DailyPlayer dp = TigerConnection.inst().getPlayerDAO().getPlayer(uuid);
						if(dp != null) {
							// set player value
							if(type.equalsIgnoreCase(DAYS_TOTAL)) {
								dp.setDaysTotal(value);
							} else if (type.equalsIgnoreCase(DAYS_CONSECUTIVE)) {
								dp.setDaysConsecutive(value);
							} else {
								cs.spigot().sendMessage(Lang.build(Lang.WRONG_ARGUMENT.get()));
								return;
							}
							// save player value
							TigerConnection.inst().getPlayerDAO().updatePlayer(dp);
							cs.spigot().sendMessage(Lang.build(
									Lang.DAILY_SET_SUCCESS.get().replace(Lang.PLAYER, uuid).replace(Lang.DATA, type).replace(Lang.VALUE, Integer.toString(value)),
									String.format("/%s %s %s", DailyCommand.CMD_CMD, DailyCommand.CMD_PLAYER, uuid),
									null,
									null));
						} else {
							cs.spigot().sendMessage(Lang.build(Lang.NOPLAYER.get().replace(Lang.PLAYER, uuid)));
						}
					} catch (NoSQLConnectionException e) {
						cs.spigot().sendMessage(Lang.build(Lang.NOCONNECTION.get()));
						TigerConnection.noConnection();
					} catch (UpdatePlayerException e) {
						cs.spigot().sendMessage(Lang.build(Lang.DAILY_SET_ERROR.get().replace(Lang.PLAYER, uuid).replace(Lang.DATA, type).replace(Lang.VALUE, Integer.toString(value))));
					}
                } catch (NumberFormatException e) {
                    cs.spigot().sendMessage(Lang.build(Lang.NONUMBER.get().replace(Lang.VALUE, args[3])));
                }
            } else {
                cs.spigot().sendMessage(Lang.build(Lang.WRONG_ARGUMENT.get()));
            }
        } else {
            cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION.get()));
        }
    }
}
