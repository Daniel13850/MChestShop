package me.daniel1385.mchestshop.guis;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.plot.Plot;
import me.daniel1385.mchestshop.MChestShop;
import me.daniel1385.mchestshop.apis.ContainerAPI;
import me.daniel1385.mchestshop.apis.InventoryGUI;
import me.daniel1385.mchestshop.apis.LuckPermsAPI;
import me.daniel1385.mchestshop.apis.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.UUID;

public class FreeShopGUI extends InventoryGUI {
	private String desc;
	private MySQL mysql;
	private ItemStack item;
	private Location chestLoc;
	private String owner;
	private int id;
	private MChestShop plugin;
	private Location loc;
	private String rank;

	public FreeShopGUI(int id, MySQL mysql, MChestShop plugin, String desc, ItemStack item, Location chestLoc, String owner, boolean haspermission, long rest, String rank, int delay, Location loc) {
		super(plugin, desc, 3);
		this.id = id;
		this.desc = desc;
		this.mysql = mysql;
		this.item = item;
		this.chestLoc = chestLoc;
		this.owner = owner;
		this.plugin = plugin;
		this.loc = loc;
		this.rank = rank;
		setItem(4, item);
		if(!haspermission) {
			setItem(4+9, Material.RED_STAINED_GLASS_PANE, "§cKeine Berechtigung!");
		} else {
			if(rest == 0) {
				setItem(4+9, Material.LIME_STAINED_GLASS_PANE, "§aJetzt abholen!");
			} else if(rest < 0) {
				setItem(4+9, Material.ORANGE_STAINED_GLASS_PANE, "§6Bereits abgeholt!");
			} else {
				setItem(4+9, Material.YELLOW_STAINED_GLASS_PANE, "§eAbholbar in:", "§7" + dauer(rest));
			}
		}
		String display = getDisplayNameOfGroup(rank);
		if(display == null) {
			display = rank;
		}
		String dauer;
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
		try {
			Plot plot = Plot.getPlot(BukkitUtil.adapt(loc));
			if(plot == null) {
				paramPlayer.closeInventory();
				return;
			}
			if(plot.getOwner() == null) {
				paramPlayer.closeInventory();
				return;
			}
			if(!plot.getOwner().toString().equals(owner)) {
				paramPlayer.closeInventory();
				return;
			}
			if(paramInt == 4+9) {
				if(!isInGroup(paramPlayer, rank)) {
					setItem(4+9, Material.RED_STAINED_GLASS_PANE, "§cKeine Berechtigung!");
					return;
				}
				long rest = mysql.getRestTime(paramPlayer.getUniqueId(), id);
				if(rest != 0) {
					if(rest > 0) {
						setItem(4+9, Material.YELLOW_STAINED_GLASS_PANE, "§eAbholbar in:", "§7" + dauer(rest));
					} else {
						setItem(4+9, Material.ORANGE_STAINED_GLASS_PANE, "§6Bereits abgeholt!");
					}
					return;
				}
				if(checkInvSpace(item, paramPlayer.getInventory().getStorageContents()) < (item.getAmount())) {
					paramPlayer.sendMessage(plugin.getPrefix() + "§cDein Inventar ist voll!");
					setItem(4+9, Material.LIME_STAINED_GLASS_PANE, "§aJetzt abholen!");
					return;
				}
				int entfernen = item.getAmount();
				if(chestLoc != null) { // Kein AdminShop
					if(ContainerAPI.getAmount(chestLoc, new ItemStack(item)) < entfernen) {
						paramPlayer.sendMessage(plugin.getPrefix() + "§cDer Behälter ist leer!");
						setItem(4+9, Material.LIME_STAINED_GLASS_PANE, "§aJetzt abholen!");
						return;
					}
				}
				rest = mysql.useFreeShop(paramPlayer.getUniqueId(), id);
				if(chestLoc != null) { // Kein AdminShop
					ContainerAPI.removeItems(chestLoc, new ItemStack(item), entfernen);
				}
				if(rest == 0) {
					setItem(4+9, Material.LIME_STAINED_GLASS_PANE, "§aJetzt abholen!");
				} else if(rest < 0) {
					setItem(4+9, Material.ORANGE_STAINED_GLASS_PANE, "§6Bereits abgeholt!");
				} else {
					setItem(4+9, Material.YELLOW_STAINED_GLASS_PANE, "§eAbholbar in:", "§7" + dauer(rest*1000L));
				}
				paramPlayer.getInventory().addItem(new ItemStack(item));
				if(chestLoc != null) { // Kein AdminShop
					Player powner = Bukkit.getPlayer(UUID.fromString(owner));
					if(powner != null) {
						powner.sendMessage(plugin.getPrefix() + "§9" + paramPlayer.getName() + " §7hat §e" + item.getAmount() + " Stück §7von §6" + desc + " §7abgeholt.");
					}
				} else {
					Bukkit.broadcast(plugin.getPrefix() + "§9" + paramPlayer.getName() + " §7hat §e" + item.getAmount() + " Stück §7von §6" + desc + " §7abgeholt.", "chestshop.admin");
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
			paramPlayer.sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
			return;
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
