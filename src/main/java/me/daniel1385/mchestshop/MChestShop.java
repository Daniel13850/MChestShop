package me.daniel1385.mchestshop;

import me.daniel1385.mchestshop.apis.Metrics;
import me.daniel1385.mchestshop.commands.ChestShopCommand;
import me.daniel1385.mchestshop.listener.ChestShopListener;
import me.daniel1385.mchestshop.objects.ChestShopInfo;
import me.daniel1385.mchestshop.objects.ChestShopType;
import me.daniel1385.mchestshop.objects.FreeShopInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MChestShop extends JavaPlugin {
	private String prefix;
	private String economy;
	private List<String> lines;

	@Override
	public void onEnable() {
		FileConfiguration config = getConfig();
		if(!config.contains("prefix")) {
			config.set("prefix", "&7[&9MChestShop&7] ");
		}
		if(!config.contains("format")) {
			config.set("format.line1", "&6Chest&eShop");
			config.set("format.line2", "&7{Description}");
			config.set("format.line3", "&8{Type}");
			config.set("format.line4", "&a{Price}");
			config.set("format.economy", "$");
		}
		saveConfig();
		prefix = translateAllCodes(config.getString("prefix")) + "§r";
		lines = new ArrayList<>();
		lines.add(translateAllCodes(config.getString("format.line1")));
		lines.add(translateAllCodes(config.getString("format.line2")));
		lines.add(translateAllCodes(config.getString("format.line3")));
		lines.add(translateAllCodes(config.getString("format.line4")));
		economy = config.getString("format.economy");
		getCommand("chestshop").setExecutor(new ChestShopCommand(this));
		Bukkit.getPluginManager().registerEvents(new ChestShopListener(this), this);
		Metrics metrics = new Metrics(this, 24660);
	}

	public List<String> getSignLines() {
		return lines;
	}

	public String getEconomySuffix() {
		return economy;
	}

	public String getPrefix() {
		return prefix;
	}

	private String translateHexCodes (String text) {
		Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
		Matcher matcher = pattern.matcher(text);

		while(matcher.find()) {
			net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.of(text.substring(matcher.start()+1, matcher.end()));
			text = text.replace(text.substring(matcher.start(), matcher.end()), color.toString());
			matcher = pattern.matcher(text);
		}

		return text;
	}

	private String translateAllCodes (String text) {
		return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', translateHexCodes(text));
	}

	public boolean update(Sign sign, ChestShopInfo info) {
		PersistentDataContainer cont = sign.getPersistentDataContainer();

		NamespacedKey keyType = new NamespacedKey(this, "type");
		cont.set(keyType, PersistentDataType.STRING, info.getType().toString());

		NamespacedKey keyOwner = new NamespacedKey(this, "owner");
		cont.set(keyOwner, PersistentDataType.STRING, info.getOwner().toString());

		NamespacedKey keyChestLoc = new NamespacedKey(this, "chest");
		if(info.getChestLocation() != null) {
			PersistentDataContainer chestLocTag = cont.getAdapterContext().newPersistentDataContainer();
			NamespacedKey keyX = new NamespacedKey(this, "X");
			NamespacedKey keyY = new NamespacedKey(this, "Y");
			NamespacedKey keyZ = new NamespacedKey(this, "Z");
			chestLocTag.set(keyX, PersistentDataType.INTEGER, info.getChestLocation().getBlockX());
			chestLocTag.set(keyY, PersistentDataType.INTEGER, info.getChestLocation().getBlockY());
			chestLocTag.set(keyZ, PersistentDataType.INTEGER, info.getChestLocation().getBlockZ());
			cont.set(keyChestLoc, PersistentDataType.TAG_CONTAINER, chestLocTag);
		} else {
			cont.remove(keyChestLoc);
		}

		NamespacedKey keyItem = new NamespacedKey(this, "item");
		if(info.getItem() != null) {
			String s;
			try {
				s = toString(info.getItem());
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			cont.set(keyItem, PersistentDataType.STRING, s);
		} else {
			cont.remove(keyItem);
		}

		NamespacedKey keyPrice = new NamespacedKey(this, "price");
		if(info.getPrice() != null) {
			cont.set(keyPrice, PersistentDataType.DOUBLE, info.getPrice());
		} else {
			cont.remove(keyPrice);
		}

		NamespacedKey keyDesc = new NamespacedKey(this, "description");
		cont.set(keyDesc, PersistentDataType.STRING, info.getDescription());

		NamespacedKey keyFree = new NamespacedKey(this, "freeshop");
		if(info.getFreeShopInfo() != null) {
			PersistentDataContainer freeTag = cont.getAdapterContext().newPersistentDataContainer();
			NamespacedKey keyRank = new NamespacedKey(this, "rank");
			NamespacedKey keyDelay = new NamespacedKey(this, "delay");
			NamespacedKey keyLastUses = new NamespacedKey(this, "last");
			freeTag.set(keyRank, PersistentDataType.STRING, info.getFreeShopInfo().getRank());
			freeTag.set(keyDelay, PersistentDataType.INTEGER, info.getFreeShopInfo().getDelay());
			freeTag.set(keyLastUses, PersistentDataType.TAG_CONTAINER, freeTag.getAdapterContext().newPersistentDataContainer());
			cont.set(keyFree, PersistentDataType.TAG_CONTAINER, freeTag);
		} else {
			cont.remove(keyFree);
		}

		sign.update();
		return true;
	}

	public long getFreeShopRestTime(Sign sign, UUID uuid) {
		long time = 0;
		int delay = 0;

		PersistentDataContainer cont = sign.getPersistentDataContainer();

		NamespacedKey keyFree = new NamespacedKey(this, "freeshop");
		if(cont.has(keyFree, PersistentDataType.TAG_CONTAINER)) {
			PersistentDataContainer freeTag = cont.get(keyFree, PersistentDataType.TAG_CONTAINER);
			NamespacedKey keyDelay = new NamespacedKey(this, "delay");
			delay = freeTag.get(keyDelay, PersistentDataType.INTEGER);
			NamespacedKey keyLastUses = new NamespacedKey(this, "last");
			PersistentDataContainer freeLastTag = freeTag.get(keyLastUses, PersistentDataType.TAG_CONTAINER);
			NamespacedKey keyUUID = new NamespacedKey(this, uuid.toString());
			if(freeLastTag.has(keyUUID, PersistentDataType.LONG)) {
				time = freeLastTag.get(keyUUID, PersistentDataType.LONG);
			}
		} else {
			return 0;
		}

		if(delay < 0) {
			return -1;
		}
		long now = System.currentTimeMillis();
		if(now >= time + (delay * 1000L)) {
			return 0;
		}
		return time + (delay * 1000L) - now;
	}

	public void useFreeShop(Sign sign, UUID uuid) {
		long time = 0;
		int delay = 0;

		PersistentDataContainer cont = sign.getPersistentDataContainer();

		NamespacedKey keyFree = new NamespacedKey(this, "freeshop");
		if(cont.has(keyFree, PersistentDataType.TAG_CONTAINER)) {
			PersistentDataContainer freeTag = cont.get(keyFree, PersistentDataType.TAG_CONTAINER);
			NamespacedKey keyLastUses = new NamespacedKey(this, "last");
			PersistentDataContainer freeLastTag = freeTag.get(keyLastUses, PersistentDataType.TAG_CONTAINER);
			NamespacedKey keyUUID = new NamespacedKey(this, uuid.toString());
			freeLastTag.set(keyUUID, PersistentDataType.LONG, System.currentTimeMillis());
			freeTag.set(keyLastUses, PersistentDataType.TAG_CONTAINER, freeLastTag);
			cont.set(keyFree, PersistentDataType.TAG_CONTAINER, freeTag);
		}

		sign.update();
	}

	public ChestShopInfo getChestShopInfo(Sign sign) {
		PersistentDataContainer cont = sign.getPersistentDataContainer();

		NamespacedKey keyType = new NamespacedKey(this, "type");
		if(cont.has(keyType, PersistentDataType.STRING)) {
			ChestShopType type = ChestShopType.valueOf(cont.get(keyType, PersistentDataType.STRING));

			NamespacedKey keyFree = new NamespacedKey(this, "freeshop");
			FreeShopInfo finfo = null;
			if(cont.has(keyFree, PersistentDataType.TAG_CONTAINER)) {
				PersistentDataContainer freeTag = cont.get(keyFree, PersistentDataType.TAG_CONTAINER);
				NamespacedKey keyRank = new NamespacedKey(this, "rank");
				NamespacedKey keyDelay = new NamespacedKey(this, "delay");
				String rank = freeTag.get(keyRank, PersistentDataType.STRING);
				int delay = freeTag.get(keyDelay, PersistentDataType.INTEGER);
				finfo = new FreeShopInfo(rank, delay);
			}

			NamespacedKey keyDesc = new NamespacedKey(this, "description");
			String desc = cont.get(keyDesc, PersistentDataType.STRING);

			NamespacedKey keyPrice = new NamespacedKey(this, "price");
			Double price = null;
			if(cont.has(keyPrice, PersistentDataType.DOUBLE)) {
				price = cont.get(keyPrice, PersistentDataType.DOUBLE);
			}

			NamespacedKey keyChestLoc = new NamespacedKey(this, "chest");
			Location chest = null;
			if(cont.has(keyChestLoc, PersistentDataType.TAG_CONTAINER)) {
				PersistentDataContainer chestLocTag = cont.get(keyChestLoc, PersistentDataType.TAG_CONTAINER);
				NamespacedKey keyX = new NamespacedKey(this, "X");
				NamespacedKey keyY = new NamespacedKey(this, "Y");
				NamespacedKey keyZ = new NamespacedKey(this, "Z");
				int x = chestLocTag.get(keyX, PersistentDataType.INTEGER);
				int y = chestLocTag.get(keyY, PersistentDataType.INTEGER);
				int z = chestLocTag.get(keyZ, PersistentDataType.INTEGER);
				chest = new Location(sign.getLocation().getWorld(), x, y, z);
			}

			NamespacedKey keyItem = new NamespacedKey(this, "item");
			ItemStack item = null;
			if(cont.has(keyItem, PersistentDataType.STRING)) {
				try {
					item = fromString(cont.get(keyItem, PersistentDataType.STRING));
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			} else {
				cont.remove(keyItem);
			}

			NamespacedKey keyOwner = new NamespacedKey(this, "owner");
			UUID owner = UUID.fromString(cont.get(keyOwner, PersistentDataType.STRING));

			return new ChestShopInfo(type, chest, item, price, desc, finfo, owner);
		} else {
			return null;
		}
	}

	private String toString(ItemStack stack) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BukkitObjectOutputStream bukkitOut = new BukkitObjectOutputStream(out);
		bukkitOut.writeObject(stack);
		bukkitOut.close();
		return Base64.getEncoder().encodeToString(out.toByteArray());
	}

	private ItemStack fromString(String string) throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(string));
		BukkitObjectInputStream bukkitIn = new BukkitObjectInputStream(in);
		ItemStack result = (ItemStack)bukkitIn.readObject();
		bukkitIn.close();
		return result;
	}

}
