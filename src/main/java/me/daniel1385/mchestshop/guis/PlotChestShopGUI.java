package me.daniel1385.mchestshop.guis;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.plot.Plot;
import me.daniel1385.mchestshop.MChestShop;
import me.daniel1385.mchestshop.apis.InventoryGUI;
import me.daniel1385.mchestshop.apis.MoneyAPI;
import me.daniel1385.mchestshop.apis.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.UUID;

public class PlotChestShopGUI extends InventoryGUI {
	private String desc;
	private MySQL mysql;
	private double price;
	private String owner;
	private int id;
	private String economy;
	private boolean usebank;
	private Location loc;
	private MChestShop plugin;
	
	public PlotChestShopGUI(int id, MySQL mysql, MChestShop plugin, String desc, double price, String owner, String economy, boolean usebank, Location loc) {
		super(plugin, desc, 3);
		this.id = id;
		this.mysql = mysql;
		this.plugin = plugin;
		this.desc = desc;
		this.price = price;
		this.owner = owner;
		this.usebank = usebank;
		setItem(13, Material.OAK_SIGN, "§aGrundstück kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price) + economy);
		this.economy = economy;
		this.loc = loc;
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
			if(paramInt != 13) {
				return;
			}
			int rest = BukkitUtil.adapt(paramPlayer).getAllowedPlots() - BukkitUtil.adapt(paramPlayer).getPlotCount();
			if(rest < plot.getConnectedPlots().size()) {
				paramPlayer.sendMessage(plugin.getPrefix() + "§cDu kannst keine weiteren Grundstücke besitzen!");
				return;
			}
			if(!MoneyAPI.removeMoney(paramPlayer.getUniqueId(), price, "ChestShop Nutzung #" + id + " (" + desc + ") " + 1 + "x")) {
				paramPlayer.sendMessage(plugin.getPrefix() + "§cDu hast nicht genug Geld!");
				return;
			}
			plot.setOwner(paramPlayer.getUniqueId());
			Player powner = Bukkit.getPlayer(UUID.fromString(owner));
			if(!usebank) {
				MoneyAPI.addMoney(UUID.fromString(owner), price, "Eigener ChestShop #" + id + " (" + desc + ") " + 1 + "x" + " (" + paramPlayer.getUniqueId().toString() + ")");
			} else {
				mysql.setBank(UUID.fromString(owner), mysql.getBank(UUID.fromString(owner)) + price);
			}
			paramPlayer.sendMessage(plugin.getPrefix() + "§aDas Grundstück gehört nun dir!");
			paramPlayer.closeInventory();
			if(powner != null) {
				powner.sendMessage(plugin.getPrefix() + "§9" + paramPlayer.getName() + " §7hat §6" + desc + " §7gekauft (§a+" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(price) + economy + "§7).");
			}
		} catch(SQLException e) {
			e.printStackTrace();
			paramPlayer.sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
			return;
		}
	}

}
