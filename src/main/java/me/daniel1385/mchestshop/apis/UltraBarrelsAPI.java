package me.daniel1385.mchestshop.apis;

import me.daniel1385.ultrabarrels.UltraBarrels;
import me.daniel1385.ultrabarrels.objects.LagerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class UltraBarrelsAPI {

    public static boolean isLager(Location loc) {
        UltraBarrels plugin = (UltraBarrels) Bukkit.getPluginManager().getPlugin("UltraBarrels");
        LagerData data = plugin.getLager(loc);
        return data != null;
    }

    public static ItemStack getLagerItem(Location loc) {
        UltraBarrels plugin = (UltraBarrels) Bukkit.getPluginManager().getPlugin("UltraBarrels");
        LagerData data = plugin.getLager(loc);
        if(data != null) {
            return data.getItem();
        } else {
            return null;
        }
    }

    public static long getLagerAmount(Location loc) {
        UltraBarrels plugin = (UltraBarrels) Bukkit.getPluginManager().getPlugin("UltraBarrels");
        LagerData data = plugin.getLager(loc);
        if(data != null) {
            return data.getAmount();
        } else {
            return 0;
        }
    }

    public static List<ItemStack> removeLager(Location loc, int amount) {
        UltraBarrels plugin = (UltraBarrels) Bukkit.getPluginManager().getPlugin("UltraBarrels");
        return plugin.removeLager(loc, amount);
    }

    public static boolean removeLager(Location loc, ItemStack stack, int amount) {
        UltraBarrels plugin = (UltraBarrels) Bukkit.getPluginManager().getPlugin("UltraBarrels");
        return plugin.removeLager(loc, stack, amount);
    }

    public static boolean addLager(Location loc, ItemStack item) {
        UltraBarrels plugin = (UltraBarrels) Bukkit.getPluginManager().getPlugin("UltraBarrels");
        return plugin.addLager(loc, item);
    }

    public static boolean addLager(Location loc, ItemStack item, int amount) {
        UltraBarrels plugin = (UltraBarrels) Bukkit.getPluginManager().getPlugin("UltraBarrels");
        return plugin.addLager(loc, item, amount);
    }

}
