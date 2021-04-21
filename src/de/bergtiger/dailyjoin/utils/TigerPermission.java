package de.bergtiger.dailyjoin.utils;

import org.bukkit.command.CommandSender;

public enum TigerPermission {

    ADMIN ("dailyjoin.admin"),
    USER ("dailyjoin.user"),

    TOP ("dailyjoin.top"),
    SET ("dailyjoin.set"),
    CMD ("dailyjoin.cmd"),
    PLAYER ("dailyjoin.player"),
    RELOAD ("dailyjoin.reload"),
    PLUGIN ("dailyjoin.plugin"),
    JOIN("dailyjoin.join");

    private final String permission;

    TigerPermission(String permission) {
        this.permission = permission;
    }

    public String get() {
        return permission;
    }

    public static Boolean hasPermission(CommandSender cs, TigerPermission...permission) {
        if(cs != null && permission != null) {
            for(TigerPermission p : permission) {
                if(cs.hasPermission(p.get()))
                    return true;
            }
        }
        return false;
    }
}
