package de.bergtiger.dailyjoin.tab;

import de.bergtiger.dailyjoin.cmd.DailyCmdSet;
import de.bergtiger.dailyjoin.cmd.DailyCmdTop;
import de.bergtiger.dailyjoin.dao.DailyDataBase;
import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.dao.impl.PlayerDAOimpl;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import static de.bergtiger.dailyjoin.utils.TigerPermission.*;
import static de.bergtiger.dailyjoin.cmd.DailyCommand.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DailyTabComplete implements TabCompleter {

    /**
     *
     * @param cs
     * @param cmd
     * @param label
     * @param args
     * @return
     */
    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if(args.length <= 1) {
            // ADD, SET
            if (hasPermission(cs, ADMIN, SET)) {
                suggestions.add(CMD_ADD);
                suggestions.add(CMD_SET);
            }
            // TOP
            if (hasPermission(cs, ADMIN, TOP)) {
                suggestions.add(CMD_TOP);
            }
            // INFO
            if (hasPermission(cs, ADMIN, PLUGIN)) {
                suggestions.add(CMD_INFO);
            }
            // PLAYER
            if (hasPermission(cs, ADMIN, PLAYER)) {
                suggestions.add(CMD_PLAYER);
            }
            // RELOAD
            if (hasPermission(cs, ADMIN, RELOAD)) {
                suggestions.add(CMD_RELOAD);
            }
            if(args.length == 1)
                return suggestions.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
            return suggestions;
            // filter
        } else if (args.length > 1) {
            // second
            // daily [player](0) player(1)
            // daily [add/set](0) player(1) [type](2) [value](3)
            // daily [top](0) [column](1) [page](2) [order](3)
            // daily [plugin/reload] []
            switch (args[0].toLowerCase()) {
                case CMD_PLAYER: {
                    if(args.length == 2 && args[1].length() >= 3) {
                        // player
                        return getNames(args[1]);
                    }
                } break;
                case CMD_ADD: case CMD_SET: {
                    if(args.length == 2 && args[1].length() >= 3) {
                        // player
                        return getNames(args[1]);
                    } else if(args.length == 3) {
                        // type
                        suggestions.add(DailyCmdSet.DAYS_TOTAL);
                        suggestions.add(DailyCmdSet.DAYS_CONSECUTIVE);
                        return suggestions.stream().filter(s -> s.startsWith(args[2])).collect(Collectors.toList());
                    }
                } break;
                case CMD_TOP: {
                    if(args.length == 2) {
                        // column
                    	suggestions.add(DailyDataBase.DAYS_TOTAL);
                        suggestions.add(DailyDataBase.DAYS_CONSECUTIVE);
                        return suggestions.stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
                    } else if(args.length == 4) {
                    	suggestions.add(DailyCmdTop.ASC);
                        suggestions.add(DailyCmdTop.DESC);
                        return suggestions.stream().filter(s -> s.startsWith(args[3])).collect(Collectors.toList());
                    }
                } break;
            }
        }
        return suggestions;
    }
    
    private List<String> getNames(String args) {
        // player
        try {
        	return PlayerDAOimpl.inst().getNames(args);
        } catch (NoSQLConnectionException e) {
            TigerConnection.noConnection();
        }
        return null;
    }
}
