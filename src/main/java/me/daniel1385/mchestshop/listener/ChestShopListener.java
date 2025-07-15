package me.daniel1385.mchestshop.listener;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.plot.Plot;
import me.daniel1385.mchestshop.MChestShop;
import me.daniel1385.mchestshop.apis.LuckPermsAPI;
import me.daniel1385.mchestshop.guis.AdminChestshopGUI;
import me.daniel1385.mchestshop.guis.ChestShopGUI;
import me.daniel1385.mchestshop.guis.FreeShopGUI;
import me.daniel1385.mchestshop.guis.PlotChestShopGUI;
import me.daniel1385.mchestshop.objects.ChestShopInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ChestShopListener implements Listener {
	private MChestShop plugin;
	
	public ChestShopListener(MChestShop plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(PlayerInteractEvent event) {
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		if(!event.getHand().equals(EquipmentSlot.HAND)) {
			return;
		}
		Block block = event.getClickedBlock();
		if(!(block.getState() instanceof Sign)) {
			return;
		}
		Sign sign = (Sign) block.getState();
		ChestShopInfo info = plugin.getChestShopInfo(sign);
		if(info == null) {
			return;
		}
		event.setCancelled(true);
		Location loc = sign.getLocation();
		Plot plot = Plot.getPlot(BukkitUtil.adapt(loc));
		if(plot == null) {
			return;
		}
		if(plot.getOwner() == null) {
			return;
		}
		if(!plot.getOwner().equals(info.getOwner())) {
			event.getPlayer().sendMessage(plugin.getPrefix() + "§cShops müssen nach einem Besitzerwechsel erneut erstellt werden.");
			return;
		}
		String desc = info.getDescription();
		String owner = info.getOwner().toString();
		if(info.isPlotShop()) {
			double price = info.getPrice();
			new PlotChestShopGUI(plugin, sign, info).open(event.getPlayer());
			return;
		}
		ItemStack item = info.getItem();
		if(info.isAdminShop() && (info.isBuyShop() || info.isSellShop())) {
			double price = info.getPrice();
			new AdminChestshopGUI(plugin, sign, info).open(event.getPlayer());
			return;
		}
		if(info.isFreeShop() && info.isAdminShop()) {
			String rank = info.getFreeShopInfo().getRank();
			if(!groupExist(rank)) {
				event.getPlayer().sendMessage(plugin.getPrefix() + "§cDer Rang existiert nicht mehr!");
				return;
			}
			new FreeShopGUI(plugin, sign, info, event.getPlayer()).open(event.getPlayer());
			return;
		}
		Location chestLoc = info.getChestLocation();
		Plot chestPlot = Plot.getPlot(BukkitUtil.adapt(chestLoc));
		if(chestPlot == null) {
			return;
		}
		if(!chestPlot.equals(plot)) {
			return;
		}
		if(info.isFreeShop()) {
			String rank = info.getFreeShopInfo().getRank();
			if(!groupExist(rank)) {
				event.getPlayer().sendMessage(plugin.getPrefix() + "§cDer Rang existiert nicht mehr!");
				return;
			}
			new FreeShopGUI(plugin, sign, info, event.getPlayer()).open(event.getPlayer());
			return;
		}
		double price = info.getPrice();
		new ChestShopGUI(plugin, sign, info).open(event.getPlayer());
	}

	private boolean groupExist(String s) {
		Plugin luckperms = Bukkit.getPluginManager().getPlugin("LuckPerms");
		if(luckperms != null) {
			return LuckPermsAPI.groupExist(s);
		}
		return false;
	}

}
