package de.bergtiger.dailyjoin.cmd;

import de.bergtiger.dailyjoin.dailyjoin;
import de.bergtiger.dailyjoin.utils.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static de.bergtiger.dailyjoin.utils.TigerPermission.*;

public class DailyCmdPlugin {

    private DailyCmdPlugin() {};

    public static void run(CommandSender cs) {
        Bukkit.getScheduler().runTaskAsynchronously(dailyjoin.inst(), () -> new DailyCmdPlugin().showPlugin(cs));
    }

    private void showPlugin(CommandSender cs) {
        if(hasPermission(cs, ADMIN, PLUGIN)){
            //plugin info
            cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_HEADER.get()));
            cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_VERSION.get().replace(Lang.VALUE, dailyjoin.inst().getDescription().getVersion())));
            //config
            cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_TIMEFORMAT.get().replace(Lang.VALUE, Lang.FORMAT_TIME.get())));
            if(dailyjoin.inst().getConfig().getString("config.SQL").equalsIgnoreCase("true")){
                cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_SYSTEM.get().replace(Lang.VALUE, "SQL")));
                cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_REWARD_RECONNECTION.get().replace(Lang.VALUE, dailyjoin.inst().getConfig().getString("config.GetRewardOnSQLConnectionLost"))));
            } else {
                cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_SYSTEM.get().replace(Lang.VALUE, "File")));
            }
            cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_DELAY.get().replace(Lang.VALUE, Integer.toString(dailyjoin.inst().getConfig().getInt("config.delay")))));
            cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_PAGE_SIZE.get().replace(Lang.VALUE, Integer.toString(dailyjoin.inst().getConfig().getInt("config.TopPlayer")))));
            cs.spigot().sendMessage(Lang.build(Lang.PLUGIN_FOOTER.get()));
        } else {
            cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION.get()));
        }
    }
}
