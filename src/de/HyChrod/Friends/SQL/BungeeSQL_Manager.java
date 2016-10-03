/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Util.Callback;
import de.HyChrod.Friends.Util.UpdateBukkitRunnable;

public class BungeeSQL_Manager {

	public static Boolean playerExists(String uuid) {
		try {
			
			ResultSet rs = MySQL.query("SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + uuid + "'");
			
			if(rs.next()) {
				return rs.getString("UUID") != null;
			}
			
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static Boolean createPlayer(String uuid) {
		if(!(playerExists(uuid))) {
			MySQL.update("INSERT INTO friends2_0_BUNGEE(UUID, ONLINE, SERVER, LASTONLINE) VALUES ('" + uuid + "', '0', 'NOTHING', '');");
			if(playerExists(uuid)) {return true;}
			return false;
		}
		return true;
	}
	
	public static Long getLastOnline(String uuid) {
		Long timeStamp = (long)0;
		if(playerExists(uuid)) {			
			try {
				Connection con = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					con = MySQL.pool.borrowConnection();
					ps = con.prepareStatement("SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + uuid + "';");
					rs = ps.executeQuery();
					
					if((!rs.next()) || (String.valueOf(rs.getString("LASTONLINE")) == null));
					
					timeStamp = Long.parseLong(rs.getString("LASTONLINE"));
				} catch (Exception ex) {
				} finally {
					if(rs != null) rs.close();
					if(ps != null) ps.close();
					if(con != null) con.close();
				}
			} catch (Exception ex) {}
		}
		if(timeStamp < 5) {
			return SQL_Manager.getLastOnline(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
		}
		return timeStamp;
	}
	
	public static void setLastOnline(String uuid, Long timeStamp) {
		if(playerExists(uuid)) {
			new UpdateBukkitRunnable(MySQL.pool, "UPDATE friends2_0_BUNGEE SET LASTONLINE='" + timeStamp.toString() + "' WHERE UUID='" + uuid + "';", 
					new Callback<Integer, SQLException>() {

						@Override
						public void call(Integer result, SQLException thrown) {
						}
				
			}).runTaskAsynchronously(Friends.getInstance());
			return;
		} else {
			createPlayer(uuid);
			setLastOnline(uuid, timeStamp);
		};
	}
	
	public static void setServer(String uuid, String server) {
		if(playerExists(uuid)) {
			new UpdateBukkitRunnable(MySQL.pool, "UPDATE friends2_0_BUNGEE SET SERVER='" + server + "' WHERE UUID='" + uuid + "';", 
					new Callback<Integer, SQLException>() {

						@Override
						public void call(Integer result, SQLException thrown) {
						}
				
			}).runTaskAsynchronously(Friends.getInstance());
		} else {
			createPlayer(uuid);
			setServer(uuid, server);
		}
		return;
	}
	
	public static String getServer(String uuid) {
		if(playerExists(uuid)) {
			try {
				Connection con = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					con = MySQL.pool.borrowConnection();
					ps = con.prepareStatement("SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" +uuid + "';");
					rs = ps.executeQuery();
					
					if((!rs.next()) || (String.valueOf(rs.getString("SERVER")) == null));
					String server = rs.getString("SERVER");
					
					return server;
				} catch (Exception ex) {
				} finally {
					if(rs != null) rs.close();
					if(ps != null) ps.close();
					if(con != null) con.close();
				}
			} catch (Exception ex) {}
		}
		return "OFFLINE";
	}
	
	public static void setOnline(String uuid, Integer value) {
		if(playerExists(uuid)) {
			new UpdateBukkitRunnable(MySQL.pool, "UPDATE friends2_0_BUNGEE SET ONLINE='" + value + "' WHERE UUID='" + uuid + "';", 
					new Callback<Integer, SQLException>() {

						@Override
						public void call(Integer result, SQLException thrown) {
						}
				
			}).runTaskAsynchronously(Friends.getInstance());
		} else {
			createPlayer(uuid);
			setOnline(uuid, value);
		}
		return;
	}
	
	public static boolean isOnline(String uuid) {
		if(playerExists(uuid)) {
			try {
				Connection con = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					
					con = MySQL.pool.borrowConnection();
					ps = con.prepareStatement("SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + uuid + "';");
					rs = ps.executeQuery();
					
					if((!rs.next()) || (Integer.valueOf(rs.getInt("ONLINE")) == null));
					
					Integer value =  rs.getInt("ONLINE");
					return value != 0;
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					if(rs != null) rs.close();
					if(ps != null) ps.close();
					if(con != null) con.close();
				}
				
			} catch (Exception ex) {}
		}
		return false;
	}
	
}
