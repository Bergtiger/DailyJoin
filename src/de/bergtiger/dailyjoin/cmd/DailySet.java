package de.bergtiger.dailyjoin.cmd;

import de.bergtiger.dailyjoin.dailyjoin;
import de.bergtiger.dailyjoin.utils.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import static de.bergtiger.dailyjoin.utils.TigerPermission.*;

public class DailySet {

    public static final String DAYS_TOTAL = "daysTotal", DAYS_CONSECUTIVE = "daysConsecutive";

    private DailySet(){}

    public static void run(CommandSender cs, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(dailyjoin.inst(), () -> new DailySet().setPlayerData(cs, args));
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

                } catch (NumberFormatException e) {
                    cs.spigot().sendMessage(Lang.buildTC(Lang.NoNumber.get().replace(Lang.VALUE, args[3])));
                }
            } else {
                cs.spigot().sendMessage(Lang.buildTC(Lang.WrongArgument.get()));
            }
        } else {
            cs.spigot().sendMessage(Lang.buildTC(Lang.NoPermission.get()));
        }
    }
}
