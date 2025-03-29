package me.daniel1385.mchestshop.apis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class MySQL
{
	private String host = null;
	private int port = 0;
	private String db = null;
	private String user = null;
	private String pass = null;
	private String prefix = null;
	private String file = null;
	private Connection con;

	public MySQL(String host, int port, String db, String user, String pass, String prefix) {
		this.host = host;
		this.port = port;
		this.db = db;
		this.user = user;
		this.pass = pass;
		this.prefix = prefix;
	}

	public MySQL(String file) {
		this.file = file;
		this.prefix = "";
	}

	private void connect() throws SQLException {
		if (con == null || con.isClosed()) {
			if (file == null) {
				con = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.db, this.user, this.pass);
			} else {
				con = DriverManager.getConnection("jdbc:sqlite:" + this.file);
			}
		}
	}

    public void init() throws SQLException {
        connect();
		if(file == null) {
			con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "chestshops` (`id` int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT, `owner` varchar(36) NOT NULL, `sign_world` text NOT NULL, `sign_x` int(11) NOT NULL, `sign_y` int(11) NOT NULL, `sign_z` int(11) NOT NULL, `chest_world` text DEFAULT NULL, `chest_x` int(11) DEFAULT NULL, `chest_y` int(11) DEFAULT NULL, `chest_z` int(11) DEFAULT NULL, `item` text DEFAULT NULL, `mode` int(11) NOT NULL, `price` double NOT NULL, `description` text NOT NULL, `rank` text DEFAULT NULL, `delay` int(11) DEFAULT NULL);").execute();
		} else {
			con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "chestshops` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `owner` varchar(36) NOT NULL, `sign_world` text NOT NULL, `sign_x` int(11) NOT NULL, `sign_y` int(11) NOT NULL, `sign_z` int(11) NOT NULL, `chest_world` text DEFAULT NULL, `chest_x` int(11) DEFAULT NULL, `chest_y` int(11) DEFAULT NULL, `chest_z` int(11) DEFAULT NULL, `item` text DEFAULT NULL, `mode` int(11) NOT NULL, `price` double NOT NULL, `description` text NOT NULL, `rank` text DEFAULT NULL, `delay` int(11) DEFAULT NULL);").execute();
		}
		con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "freeshop_uses` (`id` varchar(50) PRIMARY KEY, `time` BIGINT NOT NULL);").execute();
    }
	
	public int createChestShop(UUID uuid, Location sign, Location chest, ItemStack item, int mode, double price, String desc) throws Exception {
		connect();
		PreparedStatement stat = con.prepareStatement("INSERT INTO " + prefix + "chestshops (owner, sign_world, sign_x, sign_y, sign_z, chest_world, chest_x, chest_y, chest_z, item, mode, price, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stat.setString(1, uuid.toString());
		stat.setString(2, sign.getWorld().getName());
		stat.setInt(3, sign.getBlockX());
		stat.setInt(4, sign.getBlockY());
		stat.setInt(5, sign.getBlockZ());
		stat.setString(6, chest.getWorld().getName());
		stat.setInt(7, chest.getBlockX());
		stat.setInt(8, chest.getBlockY());
		stat.setInt(9, chest.getBlockZ());
		stat.setString(10, toString(item));
		stat.setInt(11, mode);
		stat.setDouble(12, price);
		stat.setString(13, desc);
		stat.execute();
		ResultSet set = stat.getGeneratedKeys();
		set.next();
        return set.getInt(1);
	}

	public int createFreeShop(UUID uuid, Location sign, Location chest, ItemStack item, int mode, String rank, int time, String desc) throws Exception {
		connect();
		PreparedStatement stat = con.prepareStatement("INSERT INTO " + prefix + "chestshops (owner, sign_world, sign_x, sign_y, sign_z, chest_world, chest_x, chest_y, chest_z, item, mode, price, description, rank, delay) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stat.setString(1, uuid.toString());
		stat.setString(2, sign.getWorld().getName());
		stat.setInt(3, sign.getBlockX());
		stat.setInt(4, sign.getBlockY());
		stat.setInt(5, sign.getBlockZ());
		stat.setString(6, chest.getWorld().getName());
		stat.setInt(7, chest.getBlockX());
		stat.setInt(8, chest.getBlockY());
		stat.setInt(9, chest.getBlockZ());
		stat.setString(10, toString(item));
		stat.setInt(11, mode);
		stat.setDouble(12, 0);
		stat.setString(13, desc);
		stat.setString(14, rank);
		stat.setInt(15, time);
		stat.execute();
		ResultSet set = stat.getGeneratedKeys();
		set.next();
        return set.getInt(1);
	}
	
	public int createAdminChestShop(UUID uuid, Location sign, ItemStack item, int mode, double price, String desc) throws Exception {
		connect();
		PreparedStatement stat = con.prepareStatement("INSERT INTO " + prefix + "chestshops (owner, sign_world, sign_x, sign_y, sign_z, item, mode, price, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stat.setString(1, uuid.toString());
		stat.setString(2, sign.getWorld().getName());
		stat.setInt(3, sign.getBlockX());
		stat.setInt(4, sign.getBlockY());
		stat.setInt(5, sign.getBlockZ());
		stat.setString(6, toString(item));
		stat.setInt(7, mode);
		stat.setDouble(8, price);
		stat.setString(9, desc);
		stat.execute();
		ResultSet set = stat.getGeneratedKeys();
		set.next();
        return set.getInt(1);
	}

	public int createAdminFreeShop(UUID uuid, Location sign, ItemStack item, int mode, String rank, int time, String desc) throws Exception {
		connect();
		PreparedStatement stat = con.prepareStatement("INSERT INTO " + prefix + "chestshops (owner, sign_world, sign_x, sign_y, sign_z, item, mode, price, description, rank, delay) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stat.setString(1, uuid.toString());
		stat.setString(2, sign.getWorld().getName());
		stat.setInt(3, sign.getBlockX());
		stat.setInt(4, sign.getBlockY());
		stat.setInt(5, sign.getBlockZ());
		stat.setString(6, toString(item));
		stat.setInt(7, mode);
		stat.setDouble(8, 0);
		stat.setString(9, desc);
		stat.setString(10, rank);
		stat.setInt(11, time);
		stat.execute();
		ResultSet set = stat.getGeneratedKeys();
		set.next();
        return set.getInt(1);
	}

	public long getRestTime(UUID uuid, int id) throws SQLException {
		long delay = getFreeShopDelay(id) * 1000L;
		connect();
		PreparedStatement stat = con.prepareStatement("SELECT * FROM `" + prefix + "freeshop_uses` WHERE id = ?");
		stat.setString(1, uuid.toString() + "_" + id);
		ResultSet set = stat.executeQuery();
		if(set.next()) {
			if(delay < 0) {
				return -1;
			}
			long time = set.getLong("time");
			long now = System.currentTimeMillis();
			if(now >= time + delay) {
				return 0;
			}
			return (time + delay - now);
		}
		return 0;
	}

	public int useFreeShop(UUID uuid, int id) throws SQLException {
		connect();
		PreparedStatement stat = con.prepareStatement("SELECT * FROM `" + prefix + "freeshop_uses` WHERE id = ?");
		stat.setString(1, uuid.toString() + "_" + id);
		ResultSet set = stat.executeQuery();
		if(set.next()) {
			stat = con.prepareStatement("DELETE FROM `" + prefix + "freeshop_uses` WHERE id = ?");
			stat.setString(1, uuid.toString() + "_" + id);
			stat.execute();
		}
		stat = con.prepareStatement("INSERT INTO `" + prefix + "freeshop_uses` (id, time) VALUES (?, ?)");
		stat.setString(1, uuid.toString() + "_" + id);
		stat.setLong(2, System.currentTimeMillis());
		stat.execute();
		return getFreeShopDelay(id);
	}
	
	public int createPlotChestShop(UUID uuid, Location sign, int mode, double price, String desc) throws Exception {
		connect();
		PreparedStatement stat = con.prepareStatement("INSERT INTO " + prefix + "chestshops (owner, sign_world, sign_x, sign_y, sign_z, mode, price, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stat.setString(1, uuid.toString());
		stat.setString(2, sign.getWorld().getName());
		stat.setInt(3, sign.getBlockX());
		stat.setInt(4, sign.getBlockY());
		stat.setInt(5, sign.getBlockZ());
		stat.setInt(6, mode);
		stat.setDouble(7, price);
		stat.setString(8, desc);
		stat.execute();
		ResultSet set = stat.getGeneratedKeys();
		set.next();
        return set.getInt(1);
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
	
	public boolean idExist(int id) throws SQLException {
		connect();
		PreparedStatement stat = con.prepareStatement("SELECT * FROM " + prefix + "chestshops WHERE id = ?");
		stat.setInt(1, id);
		ResultSet set = stat.executeQuery();
		if(set.next()) {
			return true;
		}
		return false;
	}
	
	public Location getChestLocation(int id) throws SQLException {
		connect();
		PreparedStatement stat = con.prepareStatement("SELECT * FROM " + prefix + "chestshops WHERE id = ?");
		stat.setInt(1, id);
		ResultSet set = stat.executeQuery();
		if(set.next()) {
			String world = set.getString("chest_world");
			int x = set.getInt("chest_x");
			int y= set.getInt("chest_y");
			int z = set.getInt("chest_z");
			if(world == null) {
				return null;
			} else {
				return new Location(Bukkit.getWorld(world), x, y, z);
			}
		}
		return null;
	}
	
	public Location getSignLocation(int id) throws SQLException {
		connect();
		PreparedStatement stat = con.prepareStatement("SELECT * FROM " + prefix + "chestshops WHERE id = ?");
		stat.setInt(1, id);
		ResultSet set = stat.executeQuery();
		if(set.next()) {
			String world = set.getString("sign_world");
			int x = set.getInt("sign_x");
			int y= set.getInt("sign_y");
			int z = set.getInt("sign_z");
			return new Location(Bukkit.getWorld(world), x, y, z);
		}
		return null;
	}
	
	public UUID getOwner(int id) throws SQLException {
		connect();
		PreparedStatement stat = con.prepareStatement("SELECT * FROM " + prefix + "chestshops WHERE id = ?");
		stat.setInt(1, id);
		ResultSet set = stat.executeQuery();
		if(set.next()) {
            return UUID.fromString(set.getString("owner"));
		}
		return null;
	}
	
	public ItemStack getItem(int id) throws Exception {
		connect();
		PreparedStatement stat = con.prepareStatement("SELECT * FROM " + prefix + "chestshops WHERE id = ?");
		stat.setInt(1, id);
		ResultSet set = stat.executeQuery();
		if(set.next()) {
			String string = set.getString("item");
			if(string == null) {
				return null;
			}
            return fromString(string);
		}
		return null;
	}
	
	public int getMode(int id) throws SQLException {
		connect();
		PreparedStatement stat = con.prepareStatement("SELECT * FROM " + prefix + "chestshops WHERE id = ?");
		stat.setInt(1, id);
		ResultSet set = stat.executeQuery();
		if(set.next()) {
            return set.getInt("mode");
		}
		return 0;
	}
	
	public double getPrice(int id) throws SQLException {
		connect();
		PreparedStatement stat = con.prepareStatement("SELECT * FROM " + prefix + "chestshops WHERE id = ?");
		stat.setInt(1, id);
		ResultSet set = stat.executeQuery();
		if(set.next()) {
            return set.getDouble("price");
		}
		return 0;
	}

	public String getFreeShopRank(int id) throws SQLException {
		connect();
		PreparedStatement stat = con.prepareStatement("SELECT * FROM " + prefix + "chestshops WHERE id = ?");
		stat.setInt(1, id);
		ResultSet set = stat.executeQuery();
		if(set.next()) {
            return set.getString("rank");
		}
		return null;
	}

	public int getFreeShopDelay(int id) throws SQLException {
		connect();
		PreparedStatement stat = con.prepareStatement("SELECT * FROM " + prefix + "chestshops WHERE id = ?");
		stat.setInt(1, id);
		ResultSet set = stat.executeQuery();
		if(set.next()) {
            return set.getInt("delay");
		}
		return -1;
	}

	public String getDescription(int id) throws SQLException {
		connect();
		PreparedStatement stat = con.prepareStatement("SELECT * FROM " + prefix + "chestshops WHERE id = ?");
		stat.setInt(1, id);
		ResultSet set = stat.executeQuery();
		if(set.next()) {
            return set.getString("description");
		}
		return null;
	}

	public void disconnect() throws SQLException {
		if (con == null || con.isClosed()) {
			return;
		}
		con.close();
	}
}
