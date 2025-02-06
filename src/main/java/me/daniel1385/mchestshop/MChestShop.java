package me.daniel1385.mchestshop;

import me.daniel1385.mchestshop.apis.Metrics;
import me.daniel1385.mchestshop.apis.MySQL;
import me.daniel1385.mchestshop.commands.CBankCommand;
import me.daniel1385.mchestshop.commands.ChestShopCommand;
import me.daniel1385.mchestshop.listener.ChestShopListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MChestShop extends JavaPlugin {
	private String prefix;

	@Override
	public void onEnable() {
		FileConfiguration config = getConfig();
		if(!config.contains("prefix")) {
			config.set("prefix", "&7[&9MChestShop&7] ");
		}
		if(!config.contains("mysql")) {
			config.set("mysql.use", false);
			config.set("mysql.host", "localhost");
			config.set("mysql.port", 3306);
			config.set("mysql.database", "minecraft");
			config.set("mysql.username", "minecraft");
			config.set("mysql.password", "aA1234Aa");
			config.set("mysql.prefix", "");
		}
		if(!config.contains("format")) {
			config.set("format.line1", "&6ChestShop &e#{ID}");
			config.set("format.line2", "&7{Description}");
			config.set("format.line3", "&8{Type}");
			config.set("format.line4", "&a{Price}");
			config.set("format.economy", "$");
		}
		if(!config.contains("enable-offline-bank")) {
			config.set("enable-offline-bank", false);
		}
		saveConfig();
		prefix = translateAllCodes(config.getString("prefix")) + "§r";
		boolean usemysql = config.getBoolean("mysql.use");
		boolean usebank = config.getBoolean("enable-offline-bank");
		MySQL mysql;
		if(usemysql) {
			mysql = new MySQL(config.getString("mysql.host"), config.getInt("mysql.port"), config.getString("mysql.database"), config.getString("mysql.username"), config.getString("mysql.password"), config.getString("mysql.prefix"));
		} else {
			File dbfile = new File(getDataFolder(), "storage.db");
			mysql = new MySQL(dbfile.getAbsolutePath());
		}
		try {
			mysql.init(usebank);
		} catch(SQLException e) {
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		List<String> lines = new ArrayList<>();
		lines.add(translateAllCodes(config.getString("format.line1")));
		lines.add(translateAllCodes(config.getString("format.line2")));
		lines.add(translateAllCodes(config.getString("format.line3")));
		lines.add(translateAllCodes(config.getString("format.line4")));
		String economy = config.getString("format.economy");
		getCommand("chestshop").setExecutor(new ChestShopCommand(mysql, lines, economy, usebank, this));
		Bukkit.getPluginManager().registerEvents(new ChestShopListener(this, mysql, economy, usebank), this);
		getCommand("cbank").setExecutor(new CBankCommand(mysql, usebank, this));
		Metrics metrics = new Metrics(this, 24660);
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

}
