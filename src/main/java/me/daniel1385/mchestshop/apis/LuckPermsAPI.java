package me.daniel1385.mchestshop.apis;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LuckPermsAPI {

    public static String getGroupID(String s) {
        LuckPerms api = LuckPermsProvider.get();
        Group direkt = api.getGroupManager().getGroup(s);
        if(direkt != null) {
            return direkt.getName();
        }
        Map<String, String> groups = new HashMap<>();
        for(Group group : api.getGroupManager().getLoadedGroups()) {
            if(group.getDisplayName() != null) {
                groups.put(group.getDisplayName(), group.getName());
            }
        }
        return groups.get(s);
    }

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
