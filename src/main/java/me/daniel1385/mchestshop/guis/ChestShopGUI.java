package me.daniel1385.mchestshop.guis;

import me.daniel1385.mchestshop.MChestShop;
import me.daniel1385.mchestshop.apis.ContainerAPI;
import me.daniel1385.mchestshop.apis.InventoryGUI;
import me.daniel1385.mchestshop.apis.MoneyAPI;
import me.daniel1385.mchestshop.apis.RegionsAPI;
import me.daniel1385.mchestshop.objects.ChestShopInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.UUID;

public class ChestShopGUI extends InventoryGUI {
	private MChestShop plugin;
	private Location loc;
	private ChestShopInfo info;
	
	public ChestShopGUI(MChestShop plugin, Sign sign, ChestShopInfo info) {
		super(plugin, info.getDescription(), 3);
		this.plugin = plugin;
		this.loc = sign.getLocation();
		this.info = info;
		setItem(4, info.getItem());
		if(info.isSellShop()) {
			setItem(10, Material.RED_DYE, "§c" + info.getItem().getAmount() + " verkaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice()) + plugin.getEconomySuffix());
			setItem(11, 2, Material.RED_DYE, "§c" + info.getItem().getAmount()*2 + " verkaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice() * 2) + plugin.getEconomySuffix());
			setItem(12, 4, Material.RED_DYE, "§c" + info.getItem().getAmount()*4 + " verkaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice() * 4) + plugin.getEconomySuffix());
			setItem(13, 8, Material.RED_DYE, "§c" + info.getItem().getAmount()*8 + " verkaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice() * 8) + plugin.getEconomySuffix());
			setItem(14, 16, Material.RED_DYE, "§c" + info.getItem().getAmount()*16 + " verkaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice() * 16) + plugin.getEconomySuffix());
			setItem(15, 32, Material.RED_DYE, "§c" + info.getItem().getAmount()*32 + " verkaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice() * 32) + plugin.getEconomySuffix());
			setItem(16, 64, Material.RED_DYE, "§c" + info.getItem().getAmount()*64 + " verkaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice() * 64) + plugin.getEconomySuffix());
			setItem(22, Material.CHEST, "§cMaximale Anzahl verkaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice()) + plugin.getEconomySuffix() + " je " + (info.getItem().getAmount() != 1 ? info.getItem().getAmount() + "er-Stack" : "Stück"));
		} else {
			setItem(10, Material.LIME_DYE, "§a" + info.getItem().getAmount() + " kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice()) + plugin.getEconomySuffix());
			setItem(11, 2, Material.LIME_DYE, "§a" + info.getItem().getAmount()*2 + " kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice() * 2) + plugin.getEconomySuffix());
			setItem(12, 4, Material.LIME_DYE, "§a" + info.getItem().getAmount()*4 + " kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice() * 4) + plugin.getEconomySuffix());
			setItem(13, 8, Material.LIME_DYE, "§a" + info.getItem().getAmount()*8 + " kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice() * 8) + plugin.getEconomySuffix());
			setItem(14, 16, Material.LIME_DYE, "§a" + info.getItem().getAmount()*16 + " kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice() * 16) + plugin.getEconomySuffix());
			setItem(15, 32, Material.LIME_DYE, "§a" + info.getItem().getAmount()*32 + " kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice() * 32) + plugin.getEconomySuffix());
			setItem(16, 64, Material.LIME_DYE, "§a" + info.getItem().getAmount()*64 + " kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice() * 64) + plugin.getEconomySuffix());
			setItem(22, Material.CHEST, "§aMaximale Anzahl kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice()) + plugin.getEconomySuffix() + " je " + (info.getItem().getAmount() != 1 ? info.getItem().getAmount() + "er-Stack" : "Stück"));
		}
	}

	@Override
	public void click(Player paramPlayer, int paramInt, boolean paramBoolean) {
		Block block = loc.getBlock();
		if (!(block.getState() instanceof Sign sign)) {
			paramPlayer.closeInventory();
			return;
		}
		if(!plugin.getChestShopInfo(sign).equals(info)) {
			paramPlayer.closeInventory();
			return;
		}
		UUID powner = RegionsAPI.getPlotOwner(loc);
		if(powner == null) {
			paramPlayer.closeInventory();
			return;
		}
		if(!powner.equals(info.getOwner())) {
			paramPlayer.closeInventory();
			return;
		}
		if(!RegionsAPI.isSamePlot(loc, info.getChestLocation())) {
			paramPlayer.closeInventory();
			return;
		}
		int anzahl;
		if(paramInt == 10) {
			anzahl = 1;
		} else if(paramInt == 11) {
			anzahl = 2;
		} else if(paramInt == 12) {
			anzahl = 4;
		} else if(paramInt == 13) {
			anzahl = 8;
		} else if(paramInt == 14) {
			anzahl = 16;
		} else if(paramInt == 15) {
			anzahl = 32;
		} else if(paramInt == 16) {
			anzahl = 64;
		} else if(paramInt == 22) {
			double kontostand = info.isBuyShop() ? MoneyAPI.get(paramPlayer.getUniqueId()) : MoneyAPI.get(info.getOwner());
			int geld = (int) (kontostand / info.getPrice());
			int platz = info.isBuyShop() ? checkInvSpace(info.getItem(), paramPlayer.getInventory().getStorageContents()) : ContainerAPI.getSpace(info.getChestLocation(), new ItemStack(info.getItem()));
			platz = platz/info.getItem().getAmount();
			int items = info.isBuyShop() ? ContainerAPI.getAmount(info.getChestLocation(), new ItemStack(info.getItem())) : checkInvItems(info.getItem(), paramPlayer.getInventory().getStorageContents());
			items = items/info.getItem().getAmount();
			anzahl = geld;
			if(platz < anzahl) {
				anzahl = platz;
			}
			if(items < anzahl) {
				anzahl = items;
			}
			if(anzahl == 0) {
				anzahl = 1;
			}
		} else {
			return;
		}
		if(info.isBuyShop()) {
			if(checkInvSpace(info.getItem(), paramPlayer.getInventory().getStorageContents()) < (anzahl*info.getItem().getAmount())) {
				paramPlayer.sendMessage(plugin.getPrefix() + "§cDein Inventar ist voll!");
				return;
			}
			int entfernen = anzahl*info.getItem().getAmount();
			if(ContainerAPI.getAmount(info.getChestLocation(), new ItemStack(info.getItem())) < entfernen) {
				paramPlayer.sendMessage(plugin.getPrefix() + "§cDer Behälter ist leer!");
				return;
			}
			double betrag = info.getPrice() * anzahl;
			if(!MoneyAPI.removeMoney(paramPlayer.getUniqueId(), betrag, "ChestShop Nutzung (" + info.getDescription() + ") " + anzahl + "x")) {
				paramPlayer.sendMessage(plugin.getPrefix() + "§cDu hast nicht genug Geld!");
				return;
			}
			ContainerAPI.removeItems(info.getChestLocation(), new ItemStack(info.getItem()), entfernen);
			while(entfernen > 0) {
				int max = info.getItem().getMaxStackSize();
				ItemStack neu = new ItemStack(info.getItem());
				neu.setAmount(Math.min(entfernen, max));
				entfernen -= neu.getAmount();
				paramPlayer.getInventory().addItem(neu);
			}
			MoneyAPI.addMoney(info.getOwner(), betrag, "Eigener ChestShop (" + info.getDescription() + ") " + anzahl + "x" + " (" + paramPlayer.getName() + ")");
			Player owner = Bukkit.getPlayer(info.getOwner());
			if(owner != null) {
				owner.sendMessage(plugin.getPrefix() + "§9" + paramPlayer.getName() + " §7hat §e" + anzahl*info.getItem().getAmount() + " Stück §7von §6" + info.getDescription() + " §7gekauft (§a+" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(betrag) + plugin.getEconomySuffix() + "§7).");
			}
		} else {
			if(checkInvItems(info.getItem(), paramPlayer.getInventory().getStorageContents()) < (anzahl*info.getItem().getAmount())) {
				paramPlayer.sendMessage(plugin.getPrefix() + "§cDu hast nicht genügend Items!");
				return;
			}
			int entfernen = anzahl*info.getItem().getAmount();
			if(ContainerAPI.getSpace(info.getChestLocation(), new ItemStack(info.getItem())) < entfernen) {
				paramPlayer.sendMessage(plugin.getPrefix() + "§cDer Behälter ist voll!");
				return;
			}
			double betrag = info.getPrice() * anzahl;
			if(!MoneyAPI.removeMoney(info.getOwner(), betrag, "Eigener ChestShop (" + info.getDescription() + ") " + anzahl + "x" + " (" + paramPlayer.getName() + ")")) {
				paramPlayer.sendMessage(plugin.getPrefix() + "§cDer Besitzer hat nicht genug Geld!");
				return;
			}
			for(ItemStack item : paramPlayer.getInventory().getStorageContents()) {
				if(item == null) {
					continue;
				}
				if(item.isSimilar(info.getItem())) {
					if(item.getAmount() < entfernen) {
						entfernen -= item.getAmount();
						item.setAmount(0);
					} else {
						ItemStack neu = new ItemStack(item);
						neu.setAmount(entfernen);
						item.setAmount(item.getAmount()-entfernen);
						break;
					}
				}
			}
			ContainerAPI.addItems(info.getChestLocation(), new ItemStack(info.getItem()), entfernen);
			MoneyAPI.addMoney(paramPlayer.getUniqueId(), betrag, "ChestShop Nutzung (" + info.getDescription() + ") " + anzahl + "x");
			Player owner = Bukkit.getPlayer(info.getOwner());
			if(owner != null) {
				owner.sendMessage(plugin.getPrefix() + "§9" + paramPlayer.getName() + " §7hat §e" + anzahl*info.getItem().getAmount() + " Stück §7von §6" + info.getDescription() + " §7verkauft (§c-" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(betrag) + plugin.getEconomySuffix() + "§7).");
			}
		}
	}
	
	private int checkInvSpace(ItemStack stack, ItemStack[] inv) {
		int space = 0;
		for(ItemStack item : inv) {
			if(item == null) {
				space += stack.getMaxStackSize();
				continue;
			}
			if(item.isSimilar(stack)) {
				space += stack.getMaxStackSize()-item.getAmount();
				continue;
			}
		}
		return space;
	}
	
	private int checkInvItems(ItemStack stack, ItemStack[] inv) {
		int space = 0;
		for(ItemStack item : inv) {
			if(item == null) {
				continue;
			}
			if(item.isSimilar(stack)) {
				space += item.getAmount();
				continue;
			}
		}
		return space;
	}

}
