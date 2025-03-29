package me.daniel1385.mchestshop.guis;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.plot.Plot;
import me.daniel1385.mchestshop.MChestShop;
import me.daniel1385.mchestshop.apis.ContainerAPI;
import me.daniel1385.mchestshop.apis.InventoryGUI;
import me.daniel1385.mchestshop.apis.MoneyAPI;
import me.daniel1385.mchestshop.apis.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.UUID;

public class ChestShopGUI extends InventoryGUI {
	private String desc;
	private MySQL mysql;
	private ItemStack item;
	private double price;
	private boolean ankauf;
	private Location chestLoc;
	private String owner;
	private int id;
	private String economy;
	private boolean usebank;
	private Location loc;
	private MChestShop plugin;
	
	public ChestShopGUI(int id, MySQL mysql, MChestShop plugin, String desc, ItemStack item, double price, boolean ankauf, Location chestLoc, String owner, String economy, boolean usebank, Location loc) {
		super(plugin, desc, 3);
		this.id = id;
		this.plugin = plugin;
		this.desc = desc;
		this.mysql = mysql;
		this.item = item;
		this.price = price;
		this.ankauf = ankauf;
		this.chestLoc = chestLoc;
		this.owner = owner;
		this.economy = economy;
		this.usebank = usebank;
		this.loc = loc;
		setItem(4, item);
		if(ankauf) {
			setItem(10, Material.RED_DYE, "§c" + item.getAmount() + " verkaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price) + economy);
			setItem(11, 2, Material.RED_DYE, "§c" + item.getAmount()*2 + " verkaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price * 2) + economy);
			setItem(12, 4, Material.RED_DYE, "§c" + item.getAmount()*4 + " verkaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price * 4) + economy);
			setItem(13, 8, Material.RED_DYE, "§c" + item.getAmount()*8 + " verkaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price * 8) + economy);
			setItem(14, 16, Material.RED_DYE, "§c" + item.getAmount()*16 + " verkaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price * 16) + economy);
			setItem(15, 32, Material.RED_DYE, "§c" + item.getAmount()*32 + " verkaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price * 32) + economy);
			setItem(16, 64, Material.RED_DYE, "§c" + item.getAmount()*64 + " verkaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price * 64) + economy);
			setItem(22, Material.CHEST, "§cMaximale Anzahl verkaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price) + economy + " je " + (item.getAmount() != 1 ? item.getAmount() + "er-Stack" : "Stück"));
		} else {
			setItem(10, Material.LIME_DYE, "§a" + item.getAmount() + " kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price) + economy);
			setItem(11, 2, Material.LIME_DYE, "§a" + item.getAmount()*2 + " kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price * 2) + economy);
			setItem(12, 4, Material.LIME_DYE, "§a" + item.getAmount()*4 + " kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price * 4) + economy);
			setItem(13, 8, Material.LIME_DYE, "§a" + item.getAmount()*8 + " kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price * 8) + economy);
			setItem(14, 16, Material.LIME_DYE, "§a" + item.getAmount()*16 + " kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price * 16) + economy);
			setItem(15, 32, Material.LIME_DYE, "§a" + item.getAmount()*32 + " kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price * 32) + economy);
			setItem(16, 64, Material.LIME_DYE, "§a" + item.getAmount()*64 + " kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price * 64) + economy);
			setItem(22, Material.CHEST, "§aMaximale Anzahl kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price) + economy + " je " + (item.getAmount() != 1 ? item.getAmount() + "er-Stack" : "Stück"));
		}
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
				double kontostand = !ankauf ? MoneyAPI.get(paramPlayer.getUniqueId()) : !usebank ? MoneyAPI.get(UUID.fromString(owner)) : mysql.getBank(UUID.fromString(owner));
				int geld = (int) (kontostand / price);
				int platz = !ankauf ? checkInvSpace(item, paramPlayer.getInventory().getStorageContents()) : ContainerAPI.getSpace(chestLoc, new ItemStack(item));
				platz = platz/item.getAmount();
				int items = !ankauf ? ContainerAPI.getAmount(chestLoc, new ItemStack(item)) : checkInvItems(item, paramPlayer.getInventory().getStorageContents());
				items = items/item.getAmount();
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
			if(!ankauf) {
				if(checkInvSpace(item, paramPlayer.getInventory().getStorageContents()) < (anzahl*item.getAmount())) {
					paramPlayer.sendMessage(plugin.getPrefix() + "§cDein Inventar ist voll!");
					return;
				}
				int entfernen = anzahl*item.getAmount();
				if(ContainerAPI.getAmount(chestLoc, new ItemStack(item)) < entfernen) {
					paramPlayer.sendMessage(plugin.getPrefix() + "§cDer Behälter ist leer!");
					return;
				}
				double betrag = price * anzahl;
				if(!MoneyAPI.removeMoney(paramPlayer.getUniqueId(), betrag, "ChestShop Nutzung #" + id + " (" + desc + ") " + anzahl + "x")) {
					paramPlayer.sendMessage(plugin.getPrefix() + "§cDu hast nicht genug Geld!");
					return;
				}
				ContainerAPI.removeItems(chestLoc, new ItemStack(item), entfernen);
				while(entfernen > 0) {
					int max = item.getMaxStackSize();
					ItemStack neu = new ItemStack(item);
					neu.setAmount(Math.min(entfernen, max));
					entfernen -= neu.getAmount();
					paramPlayer.getInventory().addItem(neu);
				}
				if(!usebank) {
					MoneyAPI.addMoney(UUID.fromString(owner), betrag, "Eigener ChestShop #" + id + " (" + desc + ") " + anzahl + "x" + " (" + paramPlayer.getName() + ")");
				} else {
					mysql.setBank(UUID.fromString(owner), mysql.getBank(UUID.fromString(owner)) + betrag);
				}
				Player powner = Bukkit.getPlayer(UUID.fromString(owner));
				if(powner != null) {
					powner.sendMessage(plugin.getPrefix() + "§9" + paramPlayer.getName() + " §7hat §e" + anzahl*item.getAmount() + " Stück §7von §6" + desc + " §7gekauft (§a+" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(betrag) + economy + "§7).");
				}
			} else {
				if(checkInvItems(item, paramPlayer.getInventory().getStorageContents()) < (anzahl*item.getAmount())) {
					paramPlayer.sendMessage(plugin.getPrefix() + "§cDu hast nicht genügend Items!");
					return;
				}
				int entfernen = anzahl*item.getAmount();
				if(ContainerAPI.getSpace(chestLoc, new ItemStack(item)) < entfernen) {
					paramPlayer.sendMessage(plugin.getPrefix() + "§cDer Behälter ist voll!");
					return;
				}
				double betrag = price * anzahl;
				if(!usebank) {
					if(!MoneyAPI.removeMoney(UUID.fromString(owner), betrag, "Eigener ChestShop #" + id + " (" + desc + ") " + anzahl + "x" + " (" + paramPlayer.getName() + ")")) {
						paramPlayer.sendMessage(plugin.getPrefix() + "§cDer Besitzer hat nicht genug Geld!");
						return;
					}
				} else {
					double bank = mysql.getBank(UUID.fromString(owner));
					if(bank < betrag) {
						paramPlayer.sendMessage(plugin.getPrefix() + "§cDer Besitzer hat nicht genug Geld!");
						return;
					}
					mysql.setBank(UUID.fromString(owner), mysql.getBank(UUID.fromString(owner)) - betrag);
				}
				for(ItemStack item : paramPlayer.getInventory().getStorageContents()) {
					if(item == null) {
						continue;
					}
					if(item.isSimilar(this.item)) {
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
				ContainerAPI.addItems(chestLoc, new ItemStack(item), entfernen);
				MoneyAPI.addMoney(paramPlayer.getUniqueId(), betrag, "ChestShop Nutzung #" + id + " (" + desc + ") " + anzahl + "x");
				Player powner = Bukkit.getPlayer(UUID.fromString(owner));
				if(powner != null) {
					powner.sendMessage(plugin.getPrefix() + "§9" + paramPlayer.getName() + " §7hat §e" + anzahl*item.getAmount() + " Stück §7von §6" + desc + " §7verkauft (§c-" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(betrag) + economy + "§7).");
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
			paramPlayer.sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
			return;
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
