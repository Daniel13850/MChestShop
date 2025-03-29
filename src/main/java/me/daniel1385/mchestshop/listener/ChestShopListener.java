package me.daniel1385.mchestshop.listener;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.plot.Plot;
import me.daniel1385.mchestshop.MChestShop;
import me.daniel1385.mchestshop.apis.LuckPermsAPI;
import me.daniel1385.mchestshop.apis.MySQL;
import me.daniel1385.mchestshop.guis.AdminChestshopGUI;
import me.daniel1385.mchestshop.guis.ChestShopGUI;
import me.daniel1385.mchestshop.guis.FreeShopGUI;
import me.daniel1385.mchestshop.guis.PlotChestShopGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class ChestShopListener implements Listener {
	private MChestShop plugin;
	private MySQL mysql;
	private String economy;
	
	public ChestShopListener(MChestShop plugin, MySQL mysql, String economy) {
		this.plugin = plugin;
		this.mysql = mysql;
		this.economy = economy;
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
		PersistentDataContainer cont = sign.getPersistentDataContainer();
		NamespacedKey key = new NamespacedKey(plugin, "shopid");
		if(!cont.has(key, PersistentDataType.INTEGER)) {
			return;
		}
		event.setCancelled(true);
		int id = cont.get(key, PersistentDataType.INTEGER);
		try {
			if(!mysql.idExist(id)) {
				return;
			}
			UUID owner = mysql.getOwner(id);
			Location loc = mysql.getSignLocation(id);
			if(!loc.equals(block.getLocation())) {
				return;
			}
			Plot plot = Plot.getPlot(BukkitUtil.adapt(loc));
			if(plot == null) {
				return;
			}
			if(plot.getOwner() == null) {
				return;
			}
			if(!plot.getOwner().equals(owner)) {
				event.getPlayer().sendMessage(plugin.getPrefix() + "§cShops müssen nach einem Besitzerwechsel erneut erstellt werden.");
				return;
			}
			String desc = mysql.getDescription(id);
			int mode = mysql.getMode(id);
			if(mode == 6) {
				double price = mysql.getPrice(id);
				new PlotChestShopGUI(id, mysql, plugin, desc, price, owner.toString(), economy, loc).open(event.getPlayer());
				return;
			}
			ItemStack item = mysql.getItem(id);
			if(mode == 4 || mode == 5) {
				double price = mysql.getPrice(id);
				new AdminChestshopGUI(id, mysql, plugin, desc, item, price, mode == 5, owner.toString(), economy, loc).open(event.getPlayer());
				return;
			}
			if(mode == 7) {
				String rank = mysql.getFreeShopRank(id);
				if(!groupExist(rank)) {
					event.getPlayer().sendMessage(plugin.getPrefix() + "§cDer Rang existiert nicht mehr!");
					return;
				}
				new FreeShopGUI(id, mysql, plugin, desc, item, null, owner.toString(), isInGroup(event.getPlayer(), rank), mysql.getRestTime(event.getPlayer().getUniqueId(), id), rank, mysql.getFreeShopDelay(id), loc).open(event.getPlayer());
				return;
			}
			Location chestLoc = mysql.getChestLocation(id);
			Plot chestPlot = Plot.getPlot(BukkitUtil.adapt(chestLoc));
			if(chestPlot == null) {
				return;
			}
			if(!chestPlot.equals(plot)) {
				return;
			}
			if(mode == 3) {
				String rank = mysql.getFreeShopRank(id);
				if(!groupExist(rank)) {
					event.getPlayer().sendMessage(plugin.getPrefix() + "§cDer Rang existiert nicht mehr!");
					return;
				}
				new FreeShopGUI(id, mysql, plugin, desc, item, chestLoc, owner.toString(), isInGroup(event.getPlayer(), rank), mysql.getRestTime(event.getPlayer().getUniqueId(), id), rank, mysql.getFreeShopDelay(id), loc).open(event.getPlayer());
				return;
			}
			double price = mysql.getPrice(id);
			new ChestShopGUI(id, mysql, plugin, desc, item, price, mode == 2, chestLoc, plot.getOwner().toString(), economy, loc).open(event.getPlayer());
		} catch(Exception e) {
			e.printStackTrace();
			event.getPlayer().sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
			return;
		}
	}

	private boolean groupExist(String s) {
		Plugin luckperms = Bukkit.getPluginManager().getPlugin("LuckPerms");
		if(luckperms != null) {
			return LuckPermsAPI.groupExist(s);
		}
		return false;
	}

	private boolean isInGroup(Player p, String group) {
		Plugin luckperms = Bukkit.getPluginManager().getPlugin("LuckPerms");
		if(luckperms != null) {
			return LuckPermsAPI.isInGroup(p, group);
		}
		return false;
	}

}
