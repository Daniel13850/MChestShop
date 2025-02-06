package me.daniel1385.mchestshop.commands;

import me.daniel1385.mchestshop.MChestShop;
import me.daniel1385.mchestshop.apis.MoneyAPI;
import me.daniel1385.mchestshop.apis.MySQL;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Locale;

public class CBankCommand implements CommandExecutor {
	private MySQL mysql;
	private boolean usebank;
	private MChestShop plugin;
	
	public CBankCommand(MySQL mysql, boolean usebank, MChestShop plugin) {
		this.mysql = mysql;
		this.usebank = usebank;
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(plugin.getPrefix() + "§cDieser Befehl kann nur von einem Spieler ausgeführt werden!");
			return false;
		}
		Player p = (Player) sender;
		if(!usebank) {
			p.sendMessage(plugin.getPrefix() + "§cDieses Feature ist deaktiviert!");
			return false;
		}
		if(args.length == 0) {
			p.sendMessage(plugin.getPrefix());
			p.sendMessage("§c/cbank guthaben");
			p.sendMessage("§c/cbank einzahlen <Betrag>");
			p.sendMessage("§c/cbank abheben <Betrag>");
			return false;
		}
		if(args[0].toLowerCase().equals("guthaben")) {
			try {
				p.sendMessage(plugin.getPrefix() + "§aDein Bankguthaben: §6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(mysql.getBank(p.getUniqueId())) + "$");
			} catch (SQLException e) {
				sender.sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
				e.printStackTrace();
				return false;
			}
			return true;
		}
		if(args[0].toLowerCase().equals("einzahlen")) {
			if(args.length == 1) {
				p.sendMessage(plugin.getPrefix() + "§cSyntax: §6/cbank einzahlen <Betrag>");
				return false;
			}
			double input;
			try {
				input = Double.parseDouble(args[1].replace(".", "").replace(",", "."));;
			} catch(NumberFormatException ex) {
				p.sendMessage(plugin.getPrefix() + "§cKein gültiger Betrag eingegeben!");
				return false;
			}
			input = round(input);
			if(input < 0) {
				p.sendMessage(plugin.getPrefix() + "§cBitte gebe einen positiven Betrag ein!");
				return false;
			}
			if(!MoneyAPI.removeMoney(p.getUniqueId(), input, "ChestShop Bankeinzahlung")) {
				p.sendMessage(plugin.getPrefix() + "§cDu hast nicht genug Geld!");
				return false;
			}
			try {
				mysql.setBank(p.getUniqueId(), mysql.getBank(p.getUniqueId()) + input);
				p.sendMessage(plugin.getPrefix() + "§6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(input) + "$ §awurden eingezahlt.");
				return true;
			} catch(SQLException e) {
				sender.sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
				e.printStackTrace();
				MoneyAPI.addMoney(p.getUniqueId(), input, "ChestShop Bankauszahlung");
				return false;
			}
		}
		if(args[0].toLowerCase().equals("abheben")) {
			if(args.length == 1) {
				p.sendMessage(plugin.getPrefix() + "§cSyntax: §6/cbank abheben <Betrag>");
				return false;
			}
			double input;
			try {
				input = Double.parseDouble(args[1].replace(".", "").replace(",", "."));
			} catch(NumberFormatException ex) {
				p.sendMessage(plugin.getPrefix() + "§cKein gültiger Betrag eingegeben!");
				return false;
			}
			input = round(input);
			if(input < 0) {
				p.sendMessage(plugin.getPrefix() + "§cBitte gebe einen positiven Betrag ein!");
				return false;
			}
			try {
				if(mysql.getBank(p.getUniqueId()) < input) {
					p.sendMessage(plugin.getPrefix() + "§cDu hast nicht genug Geld!");
					return false;
				}
				mysql.setBank(p.getUniqueId(), mysql.getBank(p.getUniqueId()) - input);
				MoneyAPI.addMoney(p.getUniqueId(), input, "ChestShop Bankauszahlung");
				p.sendMessage(plugin.getPrefix() + "§6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(input) + "$ §awurden ausgezahlt.");
				return true;
			} catch (SQLException e) {
				sender.sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
				e.printStackTrace();
				return false;
			}
		}
		p.sendMessage(plugin.getPrefix() + "§cUngültiger Befehl! §6/cbank guthaben§c, §6/cbank einzahlen§c, §6/cbank abheben§c.");
		return false;
	}

	public double round(double value) {
		BigDecimal result = BigDecimal.valueOf(value);
		result = result.setScale(2, RoundingMode.DOWN);
		value = result.doubleValue();
		return value;
	}

}
