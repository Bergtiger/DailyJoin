package de.bergtiger.dailyjoin.utils.permission;

import org.bukkit.command.CommandSender;

public enum TigerPermission {

    /** contains all permissions*/
    ADMIN ("dailyjoin.admin"),
    /** contains typical selection of needed permissions*/
    USER ("dailyjoin.user"),

    /** can perform top command*/
    TOP ("dailyjoin.top"),
    /** can perform add and set command*/
    SET ("dailyjoin.set"),
    /** can see command overview, only commands that player has permissions for*/
    CMD ("dailyjoin.cmd"),
    /** can perform and see player details*/
    PLAYER ("dailyjoin.player"),
    /** can reload the plugin*/
    RELOAD ("dailyjoin.reload"),
    /** can change the configuration*/
    CONFIG ("dailyjoin.config"),
    /** can see the current configuration*/
    PLUGIN ("dailyjoin.plugin"),
    /** can migrate betwee data formats*/
    MIGRATION ("dailyjoin.migration"),
    /** can start name update*/
    UPDATE_NAMES ("dailyjoin.update.names"),
    /** needed to get daily reward*/
    JOIN("dailyjoin.join");

    private final String permission;

    TigerPermission(String permission) {
        this.permission = permission;
    }

    /**
     * get permission string
     * @return permission
     */
    public String get() {
        return permission;
    }

    /**
     * check if CommandSender has permission
     *
     * @param cs {@link CommandSender}
     * @param permission {@link TigerPermission} to check
     * @return true if cs has any of the permissions
     */
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
