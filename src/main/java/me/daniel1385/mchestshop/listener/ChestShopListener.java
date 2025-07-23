package me.daniel1385.mchestshop.listener;

import me.daniel1385.mchestshop.MChestShop;
import me.daniel1385.mchestshop.apis.LuckPermsAPI;
import me.daniel1385.mchestshop.apis.RegionsAPI;
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

import java.util.UUID;

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
		UUID powner = RegionsAPI.getPlotOwner(loc);
		if(powner == null) {
			return;
		}
		if(!powner.equals(info.getOwner())) {
			event.getPlayer().sendMessage(plugin.getPrefix() + "§cShops müssen nach einem Besitzerwechsel erneut erstellt werden.");
			return;
		}
		if(info.isPlotShop()) {
			Plugin ps2 = Bukkit.getPluginManager().getPlugin("PlotSquared");
			if(ps2 != null) {
				new PlotChestShopGUI(plugin, sign, info).open(event.getPlayer());
			} else {
				event.getPlayer().sendMessage(plugin.getPrefix() + "§cDiese Art von Shops wird hier nicht unterstützt!");
			}
			return;
		}
		if(info.isAdminShop() && (info.isBuyShop() || info.isSellShop())) {
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
		if(!RegionsAPI.isSamePlot(loc, info.getChestLocation())) {
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
