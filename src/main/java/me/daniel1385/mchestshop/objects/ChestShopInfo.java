package me.daniel1385.mchestshop.objects;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public class ChestShopInfo {
    private ChestShopType type;
    private Location chest;
    private ItemStack item;
    private Double price;
    private String desc;
    private FreeShopInfo finfo;
    private UUID owner;

    public ChestShopInfo(ChestShopType type, Location chest, ItemStack item, Double price, String desc, FreeShopInfo finfo, UUID owner) {
        this.type = type;
        this.chest = chest;
        this.item = item;
        this.price = price;
        this.desc = desc;
        this.finfo = finfo;
        this.owner = owner;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public ChestShopType getType() {
        return type;
    }

    public Location getChestLocation() {
        return chest;
    }

    public ItemStack getItem() {
        return item;
    }

    public Double getPrice() {
        return price;
    }

    public String getDescription() {
        return desc;
    }

    public FreeShopInfo getFreeShopInfo() {
        return finfo;
    }

    public void setFreeShopInfo(FreeShopInfo finfo) {
        this.finfo = finfo;
    }

    public void setType(ChestShopType type) {
        this.type = type;
    }

    public void setChestLocation(Location chest) {
        this.chest = chest;
    }

    public void setDescription(String desc) {
        this.desc = desc;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isAdminShop() {
        return type.equals(ChestShopType.ADMINBUY) || type.equals(ChestShopType.ADMINSELL) || type.equals(ChestShopType.ADMINFREE);
    }

    public boolean isBuyShop() {
        return type.equals(ChestShopType.ADMINBUY) || type.equals(ChestShopType.BUY);
    }

    public boolean isSellShop() {
        return type.equals(ChestShopType.ADMINSELL) || type.equals(ChestShopType.SELL);
    }

    public boolean isPlotShop() {
        return type.equals(ChestShopType.PLOT);
    }

    public boolean isFreeShop() {
        return type.equals(ChestShopType.FREE) || type.equals(ChestShopType.ADMINFREE);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChestShopInfo that = (ChestShopInfo) o;
        return type == that.type && Objects.equals(chest, that.chest) && Objects.equals(item, that.item) && Objects.equals(price, that.price) && Objects.equals(desc, that.desc) && Objects.equals(finfo, that.finfo) && Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, chest, item, price, desc, finfo, owner);
    }
}
