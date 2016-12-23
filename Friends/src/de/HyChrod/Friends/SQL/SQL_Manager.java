package de.HyChrod.Friends.SQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class SQL_Manager {
	
	public static Boolean playerExists(String uuid) {
		try {
			ResultSet rs = MySQL
					.query("SELECT * FROM friends2_0 WHERE UUID= '" + uuid + "'");
			if (rs.next()) {
				if (rs.getString("UUID") != null) {
					return true;
				}
				return false;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void createPlayer(String uuid) {
		if (!playerExists(uuid).booleanValue()) {
			MySQL.update("INSERT INTO friends2_0(UUID, FRIENDS, BLOCKED, REQUESTS, OPTIONS, LASTONLINE) VALUES ('"
					+ uuid + "', '', '', '','','');");
		}
		return;
	}
	
	public static void set(LinkedList<Object> data, String uuid, String value) {
		if (playerExists(uuid)) {
			String serialized = "";
			for (Object players : data) {
				serialized = serialized + String.valueOf(players) + "//;";
			}
			MySQL.update("UPDATE friends2_0 SET " + value + "='" + serialized + "' WHERE UUID='"
					+ uuid + "';");
		} else {
			createPlayer(uuid);
			set(data, uuid, value);
		}
	}

	public static LinkedList<Object> get(String uuid, String value, boolean players) {
		LinkedList<Object> data = new LinkedList<>();
		
		if (playerExists(uuid)) {		
			try {
				ResultSet rs = MySQL.query("SELECT * FROM friends2_0 WHERE UUID= '" + uuid + "';");
				if ((rs.next()) && (String.valueOf(rs.getString(value))) == null) {

				}
				Integer valueToCheck = 20;
				if(value.equals("OPTIONS")) 
					valueToCheck = 6;
				String[] uuids = rs.getString(value).split("//;");
				for (int i = 0; i < uuids.length; i++) {
					if (uuids[i].length() > valueToCheck) {
						if(players) {
							OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuids[i]));
							if(player != null && player.getName() != null && !player.getName().equalsIgnoreCase("null"))
								data.add(player);
						} else
							data.add(uuids[i]);
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}	
		return data;
	}

	public static Long getLastOnline(String uuid) {
		Long timeStamp = Long.valueOf(0L);
		if (playerExists(uuid)) {
			try {
				ResultSet rs = MySQL
						.query("SELECT * FROM friends2_0 WHERE UUID= '" + uuid + "';");
				if ((rs.next()) && (String.valueOf(rs.getString("LASTONLINE")) == null)) {
				}
				timeStamp = Long.valueOf(Long.parseLong(rs.getString("LASTONLINE")));
			} catch (Exception localException) {
			}
		}
		return timeStamp;
	}

	public static void setLastOnline(String uuid, Long timeStamp) {
		if (playerExists(uuid)) {
			MySQL.update("UPDATE friends2_0 SET LASTONLINE='" + timeStamp.toString() + "' WHERE UUID='"
					+ uuid + "';");
			return;
		}
		createPlayer(uuid);
		setLastOnline(uuid, timeStamp);
	}
}
