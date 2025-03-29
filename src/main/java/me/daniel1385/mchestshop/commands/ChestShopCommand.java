package me.daniel1385.mchestshop.commands;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.plot.Plot;
import me.daniel1385.mchestshop.MChestShop;
import me.daniel1385.mchestshop.apis.LuckPermsAPI;
import me.daniel1385.mchestshop.apis.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.sign.Side;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class ChestShopCommand implements CommandExecutor {
	private MySQL mysql;
	private List<String> lines;
	private String economy;
	private MChestShop plugin;
	
	public ChestShopCommand(MySQL mysql, List<String> lines, String economy, MChestShop plugin) {
		this.mysql = mysql;
		this.lines = lines;
		this.economy = economy;
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(plugin.getPrefix() + "§cDieser Befehl kann nur von einem Spieler ausgeführt werden!");
			return false;
		}
		Player p = (Player) sender;
		if(args.length == 0) {
			p.sendMessage(plugin.getPrefix() + "§6§lChestShop Tutorial");
			p.sendMessage("§aVerkauf erstellen:");
			p.sendMessage("§91. §dPlatziere ein Schild an einer Truhe und schaue es an.");
			p.sendMessage("§92. §dHalte das Item das du verkaufen möchtest in der entsprechenden Anzahl in der Hand.");
			p.sendMessage("§93. §dGebe ein: §c/csh verkauf <Verkaufspreis> <Beschreibung>");
			p.sendMessage("§aAnkauf erstellen:");
			p.sendMessage("§91. §dPlatziere ein Schild an einer Truhe und schaue es an.");
			p.sendMessage("§92. §dHalte das Item das du ankaufen möchtest in der entsprechenden Anzahl in der Hand.");
			p.sendMessage("§93. §dGebe ein: §c/csh ankauf <Ankaufspreis> <Beschreibung>");
			p.sendMessage("§aNächste Seite: §9/csh help 2");
			return false;
		}
		if(args[0].toLowerCase().equals("help")) {
			int seite = 1;
			if(args.length > 1) {
				try {
					seite = Integer.parseInt(args[1]);
				} catch(NumberFormatException e) {
					p.sendMessage(plugin.getPrefix() + "§cKeine gültige Nummer!");
					return false;
				}
			}
			if(seite < 1 || seite > 4) {
				p.sendMessage(plugin.getPrefix() + "§cKeine gültige Seite!");
				return false;
			}
			if(seite >= 3 && !p.hasPermission("chestshop.admin")) {
				p.sendMessage(plugin.getPrefix() + "§cKeine gültige Seite!");
				return false;
			}
			if(seite == 1) {
				p.sendMessage(plugin.getPrefix() + "§6§lChestShop Tutorial");
				p.sendMessage("§aVerkauf erstellen:");
				p.sendMessage("§91. §dPlatziere ein Schild an einer Truhe und schaue es an.");
				p.sendMessage("§92. §dHalte das Item das du verkaufen möchtest in der entsprechenden Anzahl in der Hand.");
				p.sendMessage("§93. §dGebe ein: §c/csh verkauf <Verkaufspreis> <Beschreibung>");
				p.sendMessage("§aAnkauf erstellen:");
				p.sendMessage("§91. §dPlatziere ein Schild an einer Truhe und schaue es an.");
				p.sendMessage("§92. §dHalte das Item das du ankaufen möchtest in der entsprechenden Anzahl in der Hand.");
				p.sendMessage("§93. §dGebe ein: §c/csh ankauf <Ankaufspreis> <Beschreibung>");
				p.sendMessage("§aNächste Seite: §9/csh help 2");
				return true;
			}
			if(seite == 2) {
				p.sendMessage(plugin.getPrefix() + "§6§lChestShop Tutorial");
				p.sendMessage("§aGrundstück verkaufen:");
				p.sendMessage("§91. §dPlatziere ein Schild auf dem Grundstück und schaue es an.");
				p.sendMessage("§92. §dGebe ein: §c/csh sellplot <Preis>");
				p.sendMessage("§aItems verschenken:");
				p.sendMessage("§91. §dPlatziere ein Schild an einer Truhe und schaue es an.");
				p.sendMessage("§92. §dHalte das Item das du verschenken möchtest in der entsprechenden Anzahl in der Hand.");
				p.sendMessage("§93. §dGebe ein: §c/csh free [Zeitabstand]<Einheit> <Mindestrang> <Beschreibung>");
				p.sendMessage("§dfür eine Liste von gültigen Einheiten gebe §c/csh free §dein.");
				if(p.hasPermission("chestshop.admin")) {
					p.sendMessage("§aNächste Seite: §9/csh help 3 (nur für Admins sichtbar)");
				}
			}
			if(seite == 3) {
				p.sendMessage(plugin.getPrefix() + "§6§lChestShop Tutorial §c(Adminshops)");
				p.sendMessage("§eBei Adminshops sind keine Behälter notwendig. Items und Geldbeträge kommen vom Server.");
				p.sendMessage("§aAdminverkauf erstellen:");
				p.sendMessage("§91. §dPlatziere ein Schild auf einem Grundstück und schaue es an.");
				p.sendMessage("§92. §dHalte das Item das du verkaufen möchtest in der entsprechenden Anzahl in der Hand.");
				p.sendMessage("§93. §dGebe ein: §c/csh adminverkauf <Verkaufspreis> <Beschreibung>");
				p.sendMessage("§aAdminankauf erstellen:");
				p.sendMessage("§91. §dPlatziere ein Schild auf einem Grundstück und schaue es an.");
				p.sendMessage("§92. §dHalte das Item das du ankaufen möchtest in der entsprechenden Anzahl in der Hand.");
				p.sendMessage("§93. §dGebe ein: §c/csh adminankauf <Ankaufspreis> <Beschreibung>");
				p.sendMessage("§aNächste Seite: §9/csh help 4 (nur für Admins sichtbar)");
				return true;
			}
			if(seite == 4) {
				p.sendMessage(plugin.getPrefix() + "§6§lChestShop Tutorial §c(Adminshops)");
				p.sendMessage("§eBei Adminshops sind keine Behälter notwendig. Items und Geldbeträge kommen vom Server.");
				p.sendMessage("§aItems verschenken:");
				p.sendMessage("§91. §dPlatziere ein Schild auf einem Grundstück und schaue es an.");
				p.sendMessage("§92. §dHalte das Item das du verschenken möchtest in der entsprechenden Anzahl in der Hand.");
				p.sendMessage("§93. §dGebe ein: §c/csh free [Zeitabstand]<Einheit> <Mindestrang> <Beschreibung>");
				p.sendMessage("§dfür eine Liste von gültigen Einheiten gebe §c/csh adminfree §dein.");
				return true;
			}
			return true;
		}
		if(args[0].toLowerCase().equals("verkauf")) {
			if(args.length < 3) {
				p.sendMessage(plugin.getPrefix() + "§cSyntax: §6/csh verkauf <Verkaufspreis> <Beschreibung>");
				return false;
			}
			double betrag;
			try {
				betrag = Double.parseDouble(args[1].replace(".", "").replace(",", "."));
			} catch(NumberFormatException ex) {
				p.sendMessage(plugin.getPrefix() + "§cKein gültiger Betrag eingegeben!");
				return false;
			}
			betrag = round(betrag);
			if(betrag < 0) {
				p.sendMessage(plugin.getPrefix() + "§cBitte gebe einen positiven Betrag ein!");
				return false;
			}
			StringBuilder desc = new StringBuilder();
			for(int i = 0; i < args.length; i++) {
				if(i < 2) {
					continue;
				}
				if(i == 2) {
					desc.append(args[i]);
				} else {
					desc.append(" " + args[i]);
				}
			}
			Block block = p.getTargetBlockExact(5);
			if(block == null) {
				p.sendMessage(plugin.getPrefix() + "§cDu schaust kein Schild an!");
				return false;
			}
			if(!(block.getState() instanceof Sign)) {
				p.sendMessage(plugin.getPrefix() + "§cDu schaust kein Schild an!");
				return false;
			}
			Sign sign = (Sign) block.getState();
			Location loc = block.getLocation();
			Plot plot = Plot.getPlot(BukkitUtil.adapt(loc));
			if(plot == null) {
				p.sendMessage(plugin.getPrefix() + "§cShops dürfen nur auf Plots erstellt werden!");
				return false;
			}
			if(plot.getOwner() == null) {
				p.sendMessage(plugin.getPrefix() + "§cDas Grundstück hat keinen Besitzer!");
				return false;
			}
			if(!plot.getOwner().toString().equals(p.getUniqueId().toString())) {
				if(!p.hasPermission("chestshop.admin")) {
					p.sendMessage(plugin.getPrefix() + "§cDas ist nicht dein Grundstück!");
					return false;
				}
			}
			ItemStack hand = p.getInventory().getItemInMainHand();
			if(hand == null) {
				p.sendMessage(plugin.getPrefix() + "§cDu hälst kein Item in der Hand!");
				return false;
			}
			if(hand.getType().equals(Material.AIR)) {
				p.sendMessage(plugin.getPrefix() + "§cDu hälst kein Item in der Hand!");
				return false;
			}
			hand = new ItemStack(hand);
			Location cloc = getContainerAttached(loc, plot);
			if(cloc == null) {
				p.sendMessage(plugin.getPrefix() + "§cEs ist keine Truhe an dem Schild!");
				return false;
			}
			try {
				int id = mysql.createChestShop(plot.getOwner(), loc, cloc, hand, 1, betrag, desc.toString());
				sign.getSide(Side.FRONT).setLine(0, getSignText(lines.get(0), id, desc.toString(), "BUY", betrag));
				sign.getSide(Side.FRONT).setLine(1, getSignText(lines.get(1), id, desc.toString(), "BUY", betrag));
				sign.getSide(Side.FRONT).setLine(2, getSignText(lines.get(2), id, desc.toString(), "BUY", betrag));
				sign.getSide(Side.FRONT).setLine(3, getSignText(lines.get(3), id, desc.toString(), "BUY", betrag));
				PersistentDataContainer cont = sign.getPersistentDataContainer();
				NamespacedKey key = new NamespacedKey(plugin, "shopid");
				cont.set(key, PersistentDataType.INTEGER, id);
				sign.update();
				p.sendMessage(plugin.getPrefix() + "§aDein Shop wurde erstellt!");
				return true;
			} catch(Exception e) {
				e.printStackTrace();
				p.sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
				return false;
			}
		}
		if(args[0].toLowerCase().equals("ankauf")) {
			if(args.length < 3) {
				p.sendMessage(plugin.getPrefix() + "§cSyntax: §6/csh ankauf <Ankaufspreis> <Beschreibung>");
				return false;
			}
			double betrag;
			try {
				betrag = Double.parseDouble(args[1].replace(".", "").replace(",", "."));
			} catch(NumberFormatException ex) {
				p.sendMessage(plugin.getPrefix() + "§cKein gültiger Betrag eingegeben!");
				return false;
			}
			betrag = round(betrag);
			if(betrag < 0) {
				p.sendMessage(plugin.getPrefix() + "§cBitte gebe einen positiven Betrag ein!");
				return false;
			}
			StringBuilder desc = new StringBuilder();
			for(int i = 0; i < args.length; i++) {
				if(i < 2) {
					continue;
				}
				if(i == 2) {
					desc.append(args[i]);
				} else {
					desc.append(" " + args[i]);
				}
			}
			Block block = p.getTargetBlockExact(5);
			if(block == null) {
				p.sendMessage(plugin.getPrefix() + "§cDu schaust kein Schild an!");
				return false;
			}
			if(!(block.getState() instanceof Sign)) {
				p.sendMessage(plugin.getPrefix() + "§cDu schaust kein Schild an!");
				return false;
			}
			Sign sign = (Sign) block.getState();
			Location loc = block.getLocation();
			Plot plot = Plot.getPlot(BukkitUtil.adapt(loc));
			if(plot == null) {
				p.sendMessage(plugin.getPrefix() + "§cShops dürfen nur auf Plots erstellt werden!");
				return false;
			}
			if(plot.getOwner() == null) {
				p.sendMessage(plugin.getPrefix() + "§cDas Grundstück hat keinen Besitzer!");
				return false;
			}
			if(!plot.getOwner().toString().equals(p.getUniqueId().toString())) {
				if(!p.hasPermission("chestshop.admin")) {
					p.sendMessage(plugin.getPrefix() + "§cDas ist nicht dein Grundstück!");
					return false;
				}
			}
			ItemStack hand = p.getInventory().getItemInMainHand();
			if(hand == null) {
				p.sendMessage(plugin.getPrefix() + "§cDu hälst kein Item in der Hand!");
				return false;
			}
			if(hand.getType().equals(Material.AIR)) {
				p.sendMessage(plugin.getPrefix() + "§cDu hälst kein Item in der Hand!");
				return false;
			}
			hand = new ItemStack(hand);
			Location cloc = getContainerAttached(loc, plot);
			if(cloc == null) {
				p.sendMessage(plugin.getPrefix() + "§cEs ist keine Truhe an dem Schild!");
				return false;
			}
			try {
				int id = mysql.createChestShop(plot.getOwner(), loc, cloc, hand, 2, betrag, desc.toString());
				sign.getSide(Side.FRONT).setLine(0, getSignText(lines.get(0), id, desc.toString(), "SELL", betrag));
				sign.getSide(Side.FRONT).setLine(1, getSignText(lines.get(1), id, desc.toString(), "SELL", betrag));
				sign.getSide(Side.FRONT).setLine(2, getSignText(lines.get(2), id, desc.toString(), "SELL", betrag));
				sign.getSide(Side.FRONT).setLine(3, getSignText(lines.get(3), id, desc.toString(), "SELL", betrag));
				PersistentDataContainer cont = sign.getPersistentDataContainer();
				NamespacedKey key = new NamespacedKey(plugin, "shopid");
				cont.set(key, PersistentDataType.INTEGER, id);
				sign.update();
				p.sendMessage(plugin.getPrefix() + "§aDein Shop wurde erstellt!");
				return true;
			} catch(Exception e) {
				e.printStackTrace();
				p.sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
				return false;
			}
		}
		if(args[0].toLowerCase().equals("free")) {
			if(args.length < 4) {
				p.sendMessage(plugin.getPrefix() + "§cSyntax: §6/csh free [Zeitabstand]<Einheit> <Mindestrang> <Beschreibung>");
				sender.sendMessage("§6Gültige Einheiten:");
				sender.sendMessage("§es: Sekunden");
				sender.sendMessage("§em: Minuten");
				sender.sendMessage("§eh: Stunden");
				sender.sendMessage("§ed: Tage");
				sender.sendMessage("§eW: Wochen");
				sender.sendMessage("§eM: Monate");
				sender.sendMessage("§eY: Jahre");
				sender.sendMessage("§eP: §lEINMALIG");
				return false;
			}
			int delay;
			if (args[1].equals("P")) {
				delay = -1;
			} else {
				int i;
				if (args[1].length() == 1) {
					sender.sendMessage(plugin.getPrefix() + "§cKein gültiger Zeitabstand!");
					return false;
				}
				try {
					i = Integer.parseInt(args[1].substring(0, args[1].length() - 1));
				} catch (NumberFormatException ex) {
					sender.sendMessage(plugin.getPrefix() + "§cKein gültiger Zeitabstand!");
					return false;
				}
				if (i < 0) {
					sender.sendMessage(plugin.getPrefix() + "§cKein gültiger Zeitabstand!");
					return false;
				}
				char c = args[1].charAt(args[1].length() - 1);
				if (c == 's') {
					delay = i;
				} else if (c == 'm') {
					delay = i * 60;
				} else if (c == 'h') {
					delay = i * 60 * 60;
				} else if (c == 'd') {
					delay = i * 60 * 60 * 24;
				} else if (c == 'W') {
					delay = i * 60 * 60 * 24 * 7;
				} else if (c == 'M') {
					delay = i * 60 * 60 * 24 * 30;
				} else if (c == 'Y') {
					delay = i * 60 * 60 * 24 * 365;
				} else if (c == 'P') {
					delay = -1;
				} else {
					sender.sendMessage(plugin.getPrefix() + "§cKein gültiger Zeitabstand!");
					return false;
				}
			}
			String rank = args[2];
			if(!groupExist(rank)) {
				p.sendMessage(plugin.getPrefix() + "§cDieser Rang existiert nicht!");
				return false;
			}
			StringBuilder desc = new StringBuilder();
			for(int i = 0; i < args.length; i++) {
				if(i < 3) {
					continue;
				}
				if(i == 3) {
					desc.append(args[i]);
				} else {
					desc.append(" " + args[i]);
				}
			}
			Block block = p.getTargetBlockExact(5);
			if(block == null) {
				p.sendMessage(plugin.getPrefix() + "§cDu schaust kein Schild an!");
				return false;
			}
			if(!(block.getState() instanceof Sign)) {
				p.sendMessage(plugin.getPrefix() + "§cDu schaust kein Schild an!");
				return false;
			}
			Sign sign = (Sign) block.getState();
			Location loc = block.getLocation();
			Plot plot = Plot.getPlot(BukkitUtil.adapt(loc));
			if(plot == null) {
				p.sendMessage(plugin.getPrefix() + "§cShops dürfen nur auf Plots erstellt werden!");
				return false;
			}
			if(plot.getOwner() == null) {
				p.sendMessage(plugin.getPrefix() + "§cDas Grundstück hat keinen Besitzer!");
				return false;
			}
			if(!plot.getOwner().toString().equals(p.getUniqueId().toString())) {
				if(!p.hasPermission("chestshop.admin")) {
					p.sendMessage(plugin.getPrefix() + "§cDas ist nicht dein Grundstück!");
					return false;
				}
			}
			ItemStack hand = p.getInventory().getItemInMainHand();
			if(hand == null) {
				p.sendMessage(plugin.getPrefix() + "§cDu hälst kein Item in der Hand!");
				return false;
			}
			if(hand.getType().equals(Material.AIR)) {
				p.sendMessage(plugin.getPrefix() + "§cDu hälst kein Item in der Hand!");
				return false;
			}
			hand = new ItemStack(hand);
			Location cloc = getContainerAttached(loc, plot);
			if(cloc == null) {
				p.sendMessage(plugin.getPrefix() + "§cEs ist keine Truhe an dem Schild!");
				return false;
			}
			try {
				int id = mysql.createFreeShop(plot.getOwner(), loc, cloc, hand, 3, rank, delay, desc.toString());
				sign.getSide(Side.FRONT).setLine(0, getFreeShopSignText(lines.get(0), id, desc.toString(), rank));
				sign.getSide(Side.FRONT).setLine(1, getFreeShopSignText(lines.get(1), id, desc.toString(), rank));
				sign.getSide(Side.FRONT).setLine(2, getFreeShopSignText(lines.get(2), id, desc.toString(), rank));
				sign.getSide(Side.FRONT).setLine(3, getFreeShopSignText(lines.get(3), id, desc.toString(), rank));
				PersistentDataContainer cont = sign.getPersistentDataContainer();
				NamespacedKey key = new NamespacedKey(plugin, "shopid");
				cont.set(key, PersistentDataType.INTEGER, id);
				sign.update();
				p.sendMessage(plugin.getPrefix() + "§aDein Shop wurde erstellt!");
				return true;
			} catch(Exception e) {
				e.printStackTrace();
				p.sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
				return false;
			}
		}
		if(args[0].toLowerCase().equals("adminverkauf") && p.hasPermission("chestshop.admin")) {
			if(args.length < 3) {
				p.sendMessage(plugin.getPrefix() + "§cSyntax: §6/csh adminverkauf <Verkaufspreis> <Beschreibung>");
				return false;
			}
			double betrag;
			try {
				betrag = Double.parseDouble(args[1].replace(".", "").replace(",", "."));
			} catch(NumberFormatException ex) {
				p.sendMessage(plugin.getPrefix() + "§cKein gültiger Betrag eingegeben!");
				return false;
			}
			betrag = round(betrag);
			if(betrag < 0) {
				p.sendMessage(plugin.getPrefix() + "§cBitte gebe einen positiven Betrag ein!");
				return false;
			}
			StringBuilder desc = new StringBuilder();
			for(int i = 0; i < args.length; i++) {
				if(i < 2) {
					continue;
				}
				if(i == 2) {
					desc.append(args[i]);
				} else {
					desc.append(" " + args[i]);
				}
			}
			Block block = p.getTargetBlockExact(5);
			if(block == null) {
				p.sendMessage(plugin.getPrefix() + "§cDu schaust kein Schild an!");
				return false;
			}
			if(!(block.getState() instanceof Sign)) {
				p.sendMessage(plugin.getPrefix() + "§cDu schaust kein Schild an!");
				return false;
			}
			Sign sign = (Sign) block.getState();
			Location loc = block.getLocation();
			Plot plot = Plot.getPlot(BukkitUtil.adapt(loc));
			if(plot == null) {
				p.sendMessage(plugin.getPrefix() + "§cShops dürfen nur auf Plots erstellt werden!");
				return false;
			}
			if(plot.getOwner() == null) {
				p.sendMessage(plugin.getPrefix() + "§cDas Grundstück hat keinen Besitzer!");
				return false;
			}
			ItemStack hand = p.getInventory().getItemInMainHand();
			if(hand == null) {
				p.sendMessage(plugin.getPrefix() + "§cDu hälst kein Item in der Hand!");
				return false;
			}
			if(hand.getType().equals(Material.AIR)) {
				p.sendMessage(plugin.getPrefix() + "§cDu hälst kein Item in der Hand!");
				return false;
			}
			hand = new ItemStack(hand);
			try {
				int id = mysql.createAdminChestShop(plot.getOwner(), loc, hand, 4, betrag, desc.toString());
				sign.getSide(Side.FRONT).setLine(0, getSignText(lines.get(0), id, desc.toString(), "BUY", betrag));
				sign.getSide(Side.FRONT).setLine(1, getSignText(lines.get(1), id, desc.toString(), "BUY", betrag));
				sign.getSide(Side.FRONT).setLine(2, getSignText(lines.get(2), id, desc.toString(), "BUY", betrag));
				sign.getSide(Side.FRONT).setLine(3, getSignText(lines.get(3), id, desc.toString(), "BUY", betrag));
				PersistentDataContainer cont = sign.getPersistentDataContainer();
				NamespacedKey key = new NamespacedKey(plugin, "shopid");
				cont.set(key, PersistentDataType.INTEGER, id);
				sign.update();
				p.sendMessage(plugin.getPrefix() + "§aDein Shop wurde erstellt!");
				return true;
			} catch(Exception e) {
				e.printStackTrace();
				p.sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
				return false;
			}
		}
		if(args[0].toLowerCase().equals("adminankauf") && p.hasPermission("chestshop.admin")) {
			if(args.length < 3) {
				p.sendMessage(plugin.getPrefix() + "§cSyntax: §6/csh adminankauf <Ankaufspreis> <Beschreibung>");
				return false;
			}
			double betrag;
			try {
				betrag = Double.parseDouble(args[1].replace(".", "").replace(",", "."));
			} catch(NumberFormatException ex) {
				p.sendMessage(plugin.getPrefix() + "§cKein gültiger Betrag eingegeben!");
				return false;
			}
			betrag = round(betrag);
			if(betrag < 0) {
				p.sendMessage(plugin.getPrefix() + "§cBitte gebe einen positiven Betrag ein!");
				return false;
			}
			StringBuilder desc = new StringBuilder();
			for(int i = 0; i < args.length; i++) {
				if(i < 2) {
					continue;
				}
				if(i == 2) {
					desc.append(args[i]);
				} else {
					desc.append(" " + args[i]);
				}
			}
			Block block = p.getTargetBlockExact(5);
			if(block == null) {
				p.sendMessage(plugin.getPrefix() + "§cDu schaust kein Schild an!");
				return false;
			}
			if(!(block.getState() instanceof Sign)) {
				p.sendMessage(plugin.getPrefix() + "§cDu schaust kein Schild an!");
				return false;
			}
			Sign sign = (Sign) block.getState();
			Location loc = block.getLocation();
			Plot plot = Plot.getPlot(BukkitUtil.adapt(loc));
			if(plot == null) {
				p.sendMessage(plugin.getPrefix() + "§cShops dürfen nur auf Plots erstellt werden!");
				return false;
			}
			if(plot.getOwner() == null) {
				p.sendMessage(plugin.getPrefix() + "§cDas Grundstück hat keinen Besitzer!");
				return false;
			}
			ItemStack hand = p.getInventory().getItemInMainHand();
			if(hand == null) {
				p.sendMessage(plugin.getPrefix() + "§cDu hälst kein Item in der Hand!");
				return false;
			}
			if(hand.getType().equals(Material.AIR)) {
				p.sendMessage(plugin.getPrefix() + "§cDu hälst kein Item in der Hand!");
				return false;
			}
			hand = new ItemStack(hand);
			try {
				int id = mysql.createAdminChestShop(plot.getOwner(), loc, hand, 5, betrag, desc.toString());
				sign.getSide(Side.FRONT).setLine(0, getSignText(lines.get(0), id, desc.toString(), "SELL", betrag));
				sign.getSide(Side.FRONT).setLine(1, getSignText(lines.get(1), id, desc.toString(), "SELL", betrag));
				sign.getSide(Side.FRONT).setLine(2, getSignText(lines.get(2), id, desc.toString(), "SELL", betrag));
				sign.getSide(Side.FRONT).setLine(3, getSignText(lines.get(3), id, desc.toString(), "SELL", betrag));
				PersistentDataContainer cont = sign.getPersistentDataContainer();
				NamespacedKey key = new NamespacedKey(plugin, "shopid");
				cont.set(key, PersistentDataType.INTEGER, id);
				sign.update();
				p.sendMessage(plugin.getPrefix() + "§aDein Shop wurde erstellt!");
				return true;
			} catch(Exception e) {
				e.printStackTrace();
				p.sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
				return false;
			}
		}
		if(args[0].toLowerCase().equals("sellplot")) {
			if(args.length < 2) {
				p.sendMessage(plugin.getPrefix() + "§cSyntax: §6/csh sellplot <Preis>");
				return false;
			}
			double betrag;
			try {
				betrag = Double.parseDouble(args[1].replace(".", "").replace(",", "."));
			} catch(NumberFormatException ex) {
				p.sendMessage(plugin.getPrefix() + "§cKein gültiger Betrag eingegeben!");
				return false;
			}
			betrag = round(betrag);
			if(betrag < 0) {
				p.sendMessage(plugin.getPrefix() + "§cBitte gebe einen positiven Betrag ein!");
				return false;
			}
			Block block = p.getTargetBlockExact(5);
			if(block == null) {
				p.sendMessage(plugin.getPrefix() + "§cDu schaust kein Schild an!");
				return false;
			}
			if(!(block.getState() instanceof Sign)) {
				p.sendMessage(plugin.getPrefix() + "§cDu schaust kein Schild an!");
				return false;
			}
			Sign sign = (Sign) block.getState();
			Location loc = block.getLocation();
			Plot plot = Plot.getPlot(BukkitUtil.adapt(loc));
			if(plot == null) {
				p.sendMessage(plugin.getPrefix() + "§cShops dürfen nur auf Plots erstellt werden!");
				return false;
			}
			if(plot.getOwner() == null) {
				p.sendMessage(plugin.getPrefix() + "§cDas Grundstück hat keinen Besitzer!");
				return false;
			}
			if(!plot.getOwner().toString().equals(p.getUniqueId().toString())) {
				if(!p.hasPermission("chestshop.admin")) {
					p.sendMessage("§cDas ist nicht dein Grundstück!");
					return false;
				}
			}
			try {
				int id = mysql.createPlotChestShop(plot.getOwner(), loc, 6, betrag, "Plot " + plot.getId().getX() + ";" + plot.getId().getY());
				sign.getSide(Side.FRONT).setLine(0, getSignText(lines.get(0), id, "Plot " + plot.getId().getX() + ";" + plot.getId().getY(), "BUYPLOT", betrag));
				sign.getSide(Side.FRONT).setLine(1, getSignText(lines.get(1), id, "Plot " + plot.getId().getX() + ";" + plot.getId().getY(), "BUYPLOT", betrag));
				sign.getSide(Side.FRONT).setLine(2, getSignText(lines.get(2), id, "Plot " + plot.getId().getX() + ";" + plot.getId().getY(), "BUYPLOT", betrag));
				sign.getSide(Side.FRONT).setLine(3, getSignText(lines.get(3), id, "Plot " + plot.getId().getX() + ";" + plot.getId().getY(), "BUYPLOT", betrag));
				PersistentDataContainer cont = sign.getPersistentDataContainer();
				NamespacedKey key = new NamespacedKey(plugin, "shopid");
				cont.set(key, PersistentDataType.INTEGER, id);
				sign.update();
				p.sendMessage(plugin.getPrefix() + "§aDein Shop wurde erstellt!");
				return true;
			} catch(Exception e) {
				e.printStackTrace();
				p.sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
				return false;
			}
		}
		if(args[0].toLowerCase().equals("adminfree") && p.hasPermission("chestshop.admin")) {
			if(args.length < 4) {
				p.sendMessage(plugin.getPrefix() + "§cSyntax: §6/csh adminfree [Zeitabstand]<Einheit> <Mindestrang> <Beschreibung>");
				sender.sendMessage("§6Gültige Einheiten:");
				sender.sendMessage("§es: Sekunden");
				sender.sendMessage("§em: Minuten");
				sender.sendMessage("§eh: Stunden");
				sender.sendMessage("§ed: Tage");
				sender.sendMessage("§eW: Wochen");
				sender.sendMessage("§eM: Monate");
				sender.sendMessage("§eY: Jahre");
				sender.sendMessage("§eP: §lEINMALIG");
				return false;
			}
			int delay;
			if (args[1].equals("P")) {
				delay = -1;
			} else {
				int i;
				if (args[1].length() == 1) {
					sender.sendMessage(plugin.getPrefix() + "§cKein gültiger Zeitabstand!");
					return false;
				}
				try {
					i = Integer.parseInt(args[1].substring(0, args[1].length() - 1));
				} catch (NumberFormatException ex) {
					sender.sendMessage(plugin.getPrefix() + "§cKein gültiger Zeitabstand!");
					return false;
				}
				if (i < 0) {
					sender.sendMessage(plugin.getPrefix() + "§cKein gültiger Zeitabstand!");
					return false;
				}
				char c = args[1].charAt(args[1].length() - 1);
				if (c == 's') {
					delay = i;
				} else if (c == 'm') {
					delay = i * 60;
				} else if (c == 'h') {
					delay = i * 60 * 60;
				} else if (c == 'd') {
					delay = i * 60 * 60 * 24;
				} else if (c == 'W') {
					delay = i * 60 * 60 * 24 * 7;
				} else if (c == 'M') {
					delay = i * 60 * 60 * 24 * 30;
				} else if (c == 'Y') {
					delay = i * 60 * 60 * 24 * 365;
				} else if (c == 'P') {
					delay = -1;
				} else {
					sender.sendMessage(plugin.getPrefix() + "§cKein gültiger Zeitabstand!");
					return false;
				}
			}
			String rank = args[2];
			if(!groupExist(rank)) {
				p.sendMessage(plugin.getPrefix() + "§cDieser Rang existiert nicht!");
				return false;
			}
			StringBuilder desc = new StringBuilder();
			for(int i = 0; i < args.length; i++) {
				if(i < 3) {
					continue;
				}
				if(i == 3) {
					desc.append(args[i]);
				} else {
					desc.append(" " + args[i]);
				}
			}
			Block block = p.getTargetBlockExact(5);
			if(block == null) {
				p.sendMessage(plugin.getPrefix() + "§cDu schaust kein Schild an!");
				return false;
			}
			if(!(block.getState() instanceof Sign)) {
				p.sendMessage(plugin.getPrefix() + "§cDu schaust kein Schild an!");
				return false;
			}
			Sign sign = (Sign) block.getState();
			Location loc = block.getLocation();
			Plot plot = Plot.getPlot(BukkitUtil.adapt(loc));
			if(plot == null) {
				p.sendMessage(plugin.getPrefix() + "§cShops dürfen nur auf Plots erstellt werden!");
				return false;
			}
			if(plot.getOwner() == null) {
				p.sendMessage(plugin.getPrefix() + "§cDas Grundstück hat keinen Besitzer!");
				return false;
			}
			ItemStack hand = p.getInventory().getItemInMainHand();
			if(hand == null) {
				p.sendMessage(plugin.getPrefix() + "§cDu hälst kein Item in der Hand!");
				return false;
			}
			if(hand.getType().equals(Material.AIR)) {
				p.sendMessage(plugin.getPrefix() + "§cDu hälst kein Item in der Hand!");
				return false;
			}
			hand = new ItemStack(hand);
			try {
				int id = mysql.createAdminFreeShop(plot.getOwner(), loc, hand, 7, rank, delay, desc.toString());
				sign.getSide(Side.FRONT).setLine(0, getFreeShopSignText(lines.get(0), id, desc.toString(), rank));
				sign.getSide(Side.FRONT).setLine(1, getFreeShopSignText(lines.get(1), id, desc.toString(), rank));
				sign.getSide(Side.FRONT).setLine(2, getFreeShopSignText(lines.get(2), id, desc.toString(), rank));
				sign.getSide(Side.FRONT).setLine(3, getFreeShopSignText(lines.get(3), id, desc.toString(), rank));
				PersistentDataContainer cont = sign.getPersistentDataContainer();
				NamespacedKey key = new NamespacedKey(plugin, "shopid");
				cont.set(key, PersistentDataType.INTEGER, id);
				sign.update();
				p.sendMessage(plugin.getPrefix() + "§aDein Shop wurde erstellt!");
				return true;
			} catch(Exception e) {
				e.printStackTrace();
				p.sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
				return false;
			}
		}
		p.sendMessage(plugin.getPrefix() + "§cUngültiger Befehl! §6/chestshop §cfür Hilfe!");
		return false;
	}

    private String getSignText(String line, int id, String desc, String type, double price) {
		line = line.replace("{ID}", Integer.toString(id));
		line = line.replace("{Description}", desc);
		line = line.replace("{Type}", type);
		line = line.replace("{Price}", DecimalFormat.getNumberInstance(Locale.GERMAN).format(price) + economy);
		return line;
	}

	private String getFreeShopSignText(String line, int id, String desc, String rank) {
		line = line.replace("{ID}", Integer.toString(id));
		line = line.replace("{Description}", desc);
		line = line.replace("{Type}", "FREE");
		line = line.replace("{Price}", rank);
		return line;
	}

	private Location getContainerAttached(Location loc, Plot plot) {
		Block block = getAttached(loc.getBlock());
		if(block == null) {
			return null;
		}
		if(!(block.getState() instanceof Container)) {
			return null;
		}
		Plot plot2 = Plot.getPlot(BukkitUtil.adapt(block.getLocation()));
		if(plot2 == null) {
			return null;
		}
		if(!plot2.equals(plot)) {
			return null;
		}
		return block.getLocation();
	}

	private Block getAttached(Block b) {
		if (!(b.getBlockData() instanceof org.bukkit.block.data.type.Sign) && !(b.getBlockData() instanceof org.bukkit.block.data.type.WallSign)) return null;

		if (b.getBlockData() instanceof Directional) {
			Directional d = (Directional)b.getBlockData();
			return b.getRelative(d.getFacing().getOppositeFace());
		}

		return b.getRelative(BlockFace.DOWN);
	}

	private boolean groupExist(String s) {
		Plugin luckperms = Bukkit.getPluginManager().getPlugin("LuckPerms");
		if(luckperms != null) {
			return LuckPermsAPI.groupExist(s);
		}
		return false;
	}

	public double round(double value) {
		BigDecimal result = BigDecimal.valueOf(value);
		result = result.setScale(2, RoundingMode.DOWN);
		value = result.doubleValue();
		return value;
	}

}
