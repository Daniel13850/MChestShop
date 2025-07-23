package me.daniel1385.mchestshop.apis;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlotSquaredAPI {

    public static boolean isPlot(Location loc) {
        Plot plot = Plot.getPlot(BukkitUtil.adapt(loc));
        return plot != null;
    }

    public static boolean isSamePlot(Location loc1, Location loc2) {
        Plot plot1 = Plot.getPlot(BukkitUtil.adapt(loc1));
        Plot plot2 = Plot.getPlot(BukkitUtil.adapt(loc2));
        if(plot1 == null || plot2 == null) {
            return false;
        }
        return plot1.equals(plot2);
    }

    public static UUID getPlotOwner(Location loc) {
        Plot plot = Plot.getPlot(BukkitUtil.adapt(loc));
        if(plot != null) {
            return plot.getOwner();
        }
        return null;
    }

    public static boolean canBuild(Player p, Location loc) {
        Plot plot = Plot.getPlot(BukkitUtil.adapt(loc));
        if(plot != null) {
            UUID owner = plot.getOwner();
            if(owner == null) {
                return false;
            }
            if(owner.equals(p.getUniqueId())) {
                return true;
            }
            if(plot.getTrusted().contains(p.getUniqueId())) {
                return true;
            }
            if(Bukkit.getPlayer(owner) != null) {
                if(plot.getMembers().contains(p.getUniqueId())) {
                    return true;
                }
            }
            return p.isOp() || p.hasPermission("plots.admin");
        }
        return false;
    }

}
