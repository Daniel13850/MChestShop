package me.daniel1385.mchestshop.apis;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import org.bukkit.entity.Player;

public class LuckPermsAPI {

    public static boolean groupExist(String s) {
        LuckPerms api = LuckPermsProvider.get();
        return api.getGroupManager().getGroup(s) != null;
    }

    public static boolean isInGroup(Player p, String s) {
        return p.hasPermission("group." + s);
    }

    public static String getDisplayNameOfGroup(String s) {
        LuckPerms api = LuckPermsProvider.get();
        Group group = api.getGroupManager().getGroup(s);
        if(group != null) {
            return group.getDisplayName();
        } else {
            return null;
        }
    }

}
