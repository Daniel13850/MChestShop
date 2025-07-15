package me.daniel1385.mchestshop.guis;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.plot.Plot;
import me.daniel1385.mchestshop.MChestShop;
import me.daniel1385.mchestshop.apis.InventoryGUI;
import me.daniel1385.mchestshop.apis.MoneyAPI;
import me.daniel1385.mchestshop.objects.ChestShopInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.UUID;

public class PlotChestShopGUI extends InventoryGUI {
	private MChestShop plugin;
	private Location loc;
	private ChestShopInfo info;
	
	public PlotChestShopGUI(MChestShop plugin, Sign sign, ChestShopInfo info) {
		super(plugin, info.getDescription(), 3);
		this.plugin = plugin;
		this.loc = sign.getLocation();
		this.info = info;
		setItem(13, Material.OAK_SIGN, "§aGrundstück kaufen", "§7" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice()) + plugin.getEconomySuffix());
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
		Plot plot = Plot.getPlot(BukkitUtil.adapt(loc));
		if(plot == null) {
			paramPlayer.closeInventory();
			return;
		}
		if(plot.getOwner() == null) {
			paramPlayer.closeInventory();
			return;
		}
		if(!plot.getOwner().equals(info.getOwner())) {
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
		if(!MoneyAPI.removeMoney(paramPlayer.getUniqueId(), info.getPrice(), "ChestShop Nutzung (" + info.getDescription() + ") " + 1 + "x")) {
			paramPlayer.sendMessage(plugin.getPrefix() + "§cDu hast nicht genug Geld!");
			return;
		}
		plot.setOwner(paramPlayer.getUniqueId());
		Player powner = Bukkit.getPlayer(info.getOwner());
		MoneyAPI.addMoney(info.getOwner(), info.getPrice(), "Eigener ChestShop (" + info.getDescription() + ") " + 1 + "x" + " (" + paramPlayer.getName() + ")");
		paramPlayer.sendMessage(plugin.getPrefix() + "§aDas Grundstück gehört nun dir!");
		paramPlayer.closeInventory();
		if(powner != null) {
			powner.sendMessage(plugin.getPrefix() + "§9" + paramPlayer.getName() + " §7hat §6" + info.getDescription() + " §7gekauft (§a+" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(info.getPrice()) + plugin.getEconomySuffix() + "§7).");
		}
	}

}
