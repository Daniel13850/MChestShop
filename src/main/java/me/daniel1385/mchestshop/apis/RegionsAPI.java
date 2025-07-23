package me.daniel1385.mchestshop.apis;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class RegionsAPI {

    public static boolean isPlot(Location loc) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlotSquared");
        if(plugin != null) {
            if(PlotSquaredAPI.isPlot(loc)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSamePlot(Location loc1, Location loc2) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlotSquared");
        if(plugin != null) {
            if(PlotSquaredAPI.isPlot(loc1)) {
                return PlotSquaredAPI.isSamePlot(loc1, loc2);
            }
        }
        return false;
    }

    public static UUID getPlotOwner(Location loc) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlotSquared");
        if(plugin != null) {
            if(PlotSquaredAPI.isPlot(loc)) {
                return PlotSquaredAPI.getPlotOwner(loc);
            }
        }
        return null;
    }

    public static boolean canBuild(Player p, Location loc) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlotSquared");
        if(plugin != null) {
            if(PlotSquaredAPI.isPlot(loc)) {
                return PlotSquaredAPI.canBuild(p, loc);
            }
        }
        return false;
    }

}
