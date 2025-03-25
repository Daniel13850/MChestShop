package me.daniel1385.mchestshop.apis;

import me.daniel1385.ultrabarrels.UltraBarrels;
import me.daniel1385.ultrabarrels.objects.LagerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Barrel;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class UltraBarrelsAPI {

    public static boolean isLager(Barrel barrel) {
        UltraBarrels plugin = (UltraBarrels) Bukkit.getPluginManager().getPlugin("UltraBarrels");
        LagerData data = plugin.getLager(barrel);
        return data != null;
    }

    public static ItemStack getLagerItem(Barrel barrel) {
        UltraBarrels plugin = (UltraBarrels) Bukkit.getPluginManager().getPlugin("UltraBarrels");
        LagerData data = plugin.getLager(barrel);
        if(data != null) {
            return data.getItem();
        } else {
            return null;
        }
    }

    public static long getLagerAmount(Barrel barrel) {
        UltraBarrels plugin = (UltraBarrels) Bukkit.getPluginManager().getPlugin("UltraBarrels");
        LagerData data = plugin.getLager(barrel);
        if(data != null) {
            return data.getAmount();
        } else {
            return 0;
        }
    }

    public static List<ItemStack> removeLager(Barrel barrel, int amount) {
        UltraBarrels plugin = (UltraBarrels) Bukkit.getPluginManager().getPlugin("UltraBarrels");
        return plugin.removeLager(barrel, amount);
    }

    public static boolean removeLager(Barrel barrel, ItemStack stack, int amount) {
        UltraBarrels plugin = (UltraBarrels) Bukkit.getPluginManager().getPlugin("UltraBarrels");
        return plugin.removeLager(barrel, stack, amount);
    }

    public static boolean addLager(Barrel barrel, ItemStack item) {
        UltraBarrels plugin = (UltraBarrels) Bukkit.getPluginManager().getPlugin("UltraBarrels");
        return plugin.addLager(barrel, item);
    }

    public static boolean addLager(Barrel barrel, ItemStack item, int amount) {
        UltraBarrels plugin = (UltraBarrels) Bukkit.getPluginManager().getPlugin("UltraBarrels");
        return plugin.addLager(barrel, item, amount);
    }

}
