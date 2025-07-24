package me.daniel1385.mchestshop.guis;

import me.daniel1385.mchestshop.MChestShop;
import me.daniel1385.mchestshop.apis.ContainerAPI;
import me.daniel1385.mchestshop.apis.InventoryGUI;
import me.daniel1385.mchestshop.apis.LuckPermsAPI;
import me.daniel1385.mchestshop.apis.RegionsAPI;
import me.daniel1385.mchestshop.objects.ChestShopInfo;
import me.daniel1385.mchestshop.objects.FreeShopInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;

public class FreeShopGUI extends InventoryGUI {
	private MChestShop plugin;
	private Location loc;
	private ChestShopInfo info;

	public FreeShopGUI(MChestShop plugin, Sign sign, ChestShopInfo info, Player p) {
		super(plugin, info.getDescription(), 3);
		this.plugin = plugin;
		this.loc = sign.getLocation();
		this.info = info;
		setItem(4, info.getItem());
		if(!isInGroup(p, info.getFreeShopInfo().getRank())) {
			setItem(4+9, Material.RED_STAINED_GLASS_PANE, "§cKeine Berechtigung!");
		} else {
			long rest = plugin.getFreeShopRestTime(sign, p.getUniqueId());
			if(rest == 0) {
				setItem(4+9, Material.LIME_STAINED_GLASS_PANE, "§aJetzt abholen!");
			} else if(rest < 0) {
				setItem(4+9, Material.ORANGE_STAINED_GLASS_PANE, "§6Bereits abgeholt!");
			} else {
				setItem(4+9, Material.YELLOW_STAINED_GLASS_PANE, "§eAbholbar in:", "§7" + dauer(rest));
			}
		}
		String display = getDisplayNameOfGroup(info.getFreeShopInfo().getRank());
		if(display == null) {
			display = info.getFreeShopInfo().getRank();
		}
		String dauer;
		int delay = info.getFreeShopInfo().getDelay();
		if(delay < 0) {
			dauer = "Einmalig";
		} else if(delay == 0) {
			dauer = "Unendlich";
		} else {
			dauer = dauer(delay * 1000L);
		}
		setItem(4+9+9, Material.OAK_SIGN, "§5FreeShop", "§8Mindestrang: §7" + display, "§8Zeitabstand: §7" + dauer);
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
		if(!info.isAdminShop()) {
			if(!RegionsAPI.isSamePlot(loc, info.getChestLocation())) {
				paramPlayer.closeInventory();
				return;
			}
		}
		if(paramInt == 4+9) {
			if(!isInGroup(paramPlayer, info.getFreeShopInfo().getRank())) {
				setItem(4+9, Material.RED_STAINED_GLASS_PANE, "§cKeine Berechtigung!");
				return;
			}
			long rest = plugin.getFreeShopRestTime(sign, paramPlayer.getUniqueId());
			if(rest != 0) {
				if(rest > 0) {
					setItem(4+9, Material.YELLOW_STAINED_GLASS_PANE, "§eAbholbar in:", "§7" + dauer(rest));
				} else {
					setItem(4+9, Material.ORANGE_STAINED_GLASS_PANE, "§6Bereits abgeholt!");
				}
				return;
			}
			if(checkInvSpace(info.getItem(), paramPlayer.getInventory().getStorageContents()) < (info.getItem().getAmount())) {
				paramPlayer.sendMessage(plugin.getPrefix() + "§cDein Inventar ist voll!");
				setItem(4+9, Material.LIME_STAINED_GLASS_PANE, "§aJetzt abholen!");
				return;
			}
			int entfernen = info.getItem().getAmount();
			if(!info.isAdminShop()) {
				if(ContainerAPI.getAmount(info.getChestLocation(), new ItemStack(info.getItem())) < entfernen) {
					paramPlayer.sendMessage(plugin.getPrefix() + "§cDer Behälter ist leer!");
					setItem(4+9, Material.LIME_STAINED_GLASS_PANE, "§aJetzt abholen!");
					return;
				}
			}
			plugin.useFreeShop(sign, paramPlayer.getUniqueId());
			rest = info.getFreeShopInfo().getDelay();
			if(!info.isAdminShop()) {
				ContainerAPI.removeItems(info.getChestLocation(), new ItemStack(info.getItem()), entfernen);
			}
			if(rest == 0) {
				setItem(4+9, Material.LIME_STAINED_GLASS_PANE, "§aJetzt abholen!");
			} else if(rest < 0) {
				setItem(4+9, Material.ORANGE_STAINED_GLASS_PANE, "§6Bereits abgeholt!");
			} else {
				setItem(4+9, Material.YELLOW_STAINED_GLASS_PANE, "§eAbholbar in:", "§7" + dauer(rest*1000L));
			}
			paramPlayer.getInventory().addItem(new ItemStack(info.getItem()));
			if(!info.isAdminShop()) {
				Player owner = Bukkit.getPlayer(info.getOwner());
				if(owner != null) {
					owner.sendMessage(plugin.getPrefix() + "§9" + paramPlayer.getName() + " §7hat §e" + info.getItem().getAmount() + " Stück §7von §6" + info.getDescription() + " §7abgeholt.");
				}
			} else {
				Bukkit.broadcast(plugin.getPrefix() + "§9" + paramPlayer.getName() + " §7hat §e" + info.getItem().getAmount() + " Stück §7von §6" + info.getDescription() + " §7abgeholt.", "chestshop.admin");
			}
		}
	}

	private boolean isInGroup(Player p, String group) {
		Plugin luckperms = Bukkit.getPluginManager().getPlugin("LuckPerms");
		if(luckperms != null) {
			return LuckPermsAPI.isInGroup(p, group);
		}
		return false;
	}

	public static String getDisplayNameOfGroup(String s) {
		Plugin luckperms = Bukkit.getPluginManager().getPlugin("LuckPerms");
		if(luckperms != null) {
			return LuckPermsAPI.getDisplayNameOfGroup(s);
		}
		return null;
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

	private String dauer(long time) {
		if (time < 1000L) {
			return "Jetzt";
		}
		StringBuilder builder = new StringBuilder();
		if (time >= 31536000000L) {
			long h = time / 31536000000L;
			time -= h * 31536000000L;
			builder.append(String.valueOf(h) + ((h == 1L) ? " Jahr " : " Jahre "));
		}
		if (time >= 2592000000L) {
			long h = time / 2592000000L;
			time -= h * 2592000000L;
			builder.append(String.valueOf(h) + ((h == 1L) ? " Monat " : " Monate "));
		}
		if (time >= 604800000L) {
			long h = time / 604800000L;
			time -= h * 604800000L;
			builder.append(String.valueOf(h) + ((h == 1L) ? " Woche " : " Wochen "));
		}
		if (time >= 86400000L) {
			long h = time / 86400000L;
			time -= h * 86400000L;
			builder.append(String.valueOf(h) + ((h == 1L) ? " Tag " : " Tage "));
		}
		if (time >= 3600000L) {
			long h = time / 3600000L;
			time -= h * 3600000L;
			builder.append(String.valueOf(h) + ((h == 1L) ? " Stunde " : " Stunden "));
		}
		if (time >= 60000L) {
			long h = time / 60000L;
			time -= h * 60000L;
			builder.append(String.valueOf(h) + ((h == 1L) ? " Minute " : " Minuten "));
		}
		if (time >= 1000L) {
			long h = time / 1000L;
			time -= h * 1000L;
			builder.append(String.valueOf(h) + ((h == 1L) ? " Sekunde " : " Sekunden "));
		}
		String string = builder.toString();
		return string.substring(0, string.length() - 1);
	}

}
