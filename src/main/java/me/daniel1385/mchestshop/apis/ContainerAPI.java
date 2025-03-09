package me.daniel1385.mchestshop.apis;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ContainerAPI {

    public static int getSpace(Location loc, ItemStack stack) {
        Container container = getContainer(loc);
        if(container == null) {
            return 0;
        }
        Plugin ubarrels = Bukkit.getPluginManager().getPlugin("UltraBarrels");
        if(ubarrels != null) {
            if (UltraBarrelsAPI.isLager(loc)) {
                ItemStack da = UltraBarrelsAPI.getLagerItem(loc);
                if (da != null) {
                    if (!da.isSimilar(stack)) {
                        return 0;
                    }
                }
                return Integer.MAX_VALUE;
            }
        }
        return checkInvSpace(stack, container.getInventory().getStorageContents());
    }

    public static int getAmount(Location loc, ItemStack stack) {
        Container container = getContainer(loc);
        if(container == null) {
            return 0;
        }
        Plugin ubarrels = Bukkit.getPluginManager().getPlugin("UltraBarrels");
        if(ubarrels != null) {
            if (UltraBarrelsAPI.isLager(loc)) {
                ItemStack da = UltraBarrelsAPI.getLagerItem(loc);
                if (da != null) {
                    if (da.isSimilar(stack)) {
                        long amount = UltraBarrelsAPI.getLagerAmount(loc);
                        if (amount > Integer.MAX_VALUE) {
                            return Integer.MAX_VALUE;
                        }
                        return (int) amount;
                    }
                }
                return 0;
            }
        }
        return checkInvItems(stack, container.getInventory().getStorageContents());
    }

    public static boolean removeItems(Location loc, ItemStack stack, int anzahl) {
        Container container = getContainer(loc);
        if(container == null) {
            return false;
        }
        Plugin ubarrels = Bukkit.getPluginManager().getPlugin("UltraBarrels");
        if(ubarrels != null) {
            if (UltraBarrelsAPI.isLager(loc)) {
                return UltraBarrelsAPI.removeLager(loc, stack, anzahl);
            }
        }
        int amount = anzahl;
        if(getAmount(loc, stack) < amount) {
            return false;
        }
        Inventory inv = container.getInventory();
        if(!(inv.getHolder() instanceof DoubleChest)) {
            inv = container.getSnapshotInventory();
        }
        for(ItemStack item : inv.getStorageContents()) {
            if(item == null) {
                continue;
            }
            if(item.isSimilar(stack)) {
                if(item.getAmount() < amount) {
                    amount -= item.getAmount();
                    item.setAmount(0);
                } else {
                    ItemStack neu = new ItemStack(item);
                    neu.setAmount(amount);
                    item.setAmount(item.getAmount()-amount);
                    break;
                }
            }
        }
        if(!(inv.getHolder() instanceof DoubleChest)) {
            container.update();
        } else {
            updateDoubleChest(container);
        }
        return true;
    }

    private static void updateDoubleChest(Container container) {
        DoubleChest chest = (DoubleChest) container.getInventory().getHolder();
        Chest half = (Chest) chest.getLeftSide();
        Inventory snap = half.getSnapshotInventory();
        for(int i = 0; i < 27; i++) {
            snap.setItem(i, chest.getInventory().getItem(i));
        }
        half.update();

        half = (Chest) chest.getRightSide();
        snap = half.getSnapshotInventory();
        for(int i = 27; i < 54; i++) {
            snap.setItem(i-27, chest.getInventory().getItem(i));
        }
        half.update();
    }

    public static boolean addItems(Location loc, ItemStack stack, int anzahl) {
        Container container = getContainer(loc);
        if(container == null) {
            return false;
        }
        Plugin ubarrels = Bukkit.getPluginManager().getPlugin("UltraBarrels");
        if(ubarrels != null) {
            if (UltraBarrelsAPI.isLager(loc)) {
                return UltraBarrelsAPI.addLager(loc, stack, anzahl);
            }
        }
        int amount = anzahl;
        if(getSpace(loc, stack) < amount) {
            return false;
        }
        Inventory inv = container.getInventory();
        if(!(inv.getHolder() instanceof DoubleChest)) {
            inv = container.getSnapshotInventory();
        }
        while(amount > 0) {
            int max = stack.getMaxStackSize();
            ItemStack neu = new ItemStack(stack);
            if(amount > max) {
                neu.setAmount(max);
            } else {
                neu.setAmount(amount);
            }
            inv.addItem(neu);
            amount -= neu.getAmount();
        }
        if(!(inv.getHolder() instanceof DoubleChest)) {
            container.update();
        } else {
            updateDoubleChest(container);
        }
        return true;
    }

    private static Container getContainer(Location loc) {
        if(loc == null) {
            return null;
        }
        if(!(loc.getBlock().getState() instanceof Container)) {
            return null;
        }
        return ((Container) loc.getBlock().getState());
    }

    private static int checkInvSpace(ItemStack stack, ItemStack[] inv) {
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

    private static int checkInvItems(ItemStack stack, ItemStack[] inv) {
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