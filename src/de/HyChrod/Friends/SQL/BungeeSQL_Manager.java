/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.SQL;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.OfflinePlayer;

public class BungeeSQL_Manager {

	public static Boolean playerExists(OfflinePlayer player) {
		try {
			ResultSet rs = MySQL
					.query("SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + player.getUniqueId().toString() + "'");

			if (rs.next()) {
				return rs.getString("UUID") != null;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Boolean createPlayer(OfflinePlayer player) {
		if (!(playerExists(player))) {
			MySQL.update("INSERT INTO friends2_0_BUNGEE(UUID, ONLINE, SERVER, LASTONLINE) VALUES ('"
					+ player.getUniqueId().toString() + "', '0', 'NOTHING', '');");
			if (playerExists(player)) {
				return true;
			}
			return false;
		}
		return true;
	}

	public static void set(OfflinePlayer player, Object obj, String value) {
		if (playerExists(player)) {
			MySQL.update("UPDATE friends2_0_BUNGEE SET " + value + "='" + obj + "' WHERE UUID='"
					+ player.getUniqueId().toString() + "';");
		} else {
			createPlayer(player);
			set(player, obj, value);
		}
	}

	public static Object get(OfflinePlayer player, String value) {
		if (playerExists(player)) {
			try {
				ResultSet rs = MySQL.query(
						"SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + player.getUniqueId().toString() + "';");
				if ((!rs.next()) || (String.valueOf(rs.getString(value)) == null))
					;

				return rs.getObject(value);

			} catch (Exception ex) {
			}
		}
		if (value.equals("LASTONLINE"))
			return 0;
		return "OFFLINE";
	}

	public static boolean isOnline(OfflinePlayer player) {
		if (playerExists(player)) {
			try {
				ResultSet rs = MySQL.query(
						"SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + player.getUniqueId().toString() + "';");
				if ((!rs.next()) || (Integer.valueOf(rs.getInt("ONLINE")) == null))
					;
				Integer value = rs.getInt("ONLINE");
				return value != 0;

			} catch (Exception e) {
			}
		}
		return false;
	}

}
