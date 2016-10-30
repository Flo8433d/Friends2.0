/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.SQL;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BungeeSQL_Manager {

	public static Boolean playerExists(String uuid) {
		if(!MySQL.isConnected()) {
			MySQL.connect();
		}
		try {
			ResultSet rs = MySQL
					.query("SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + uuid + "'");

			if (rs.next()) {
				return rs.getString("UUID") != null;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void createPlayer(String uuid) {
		if(MySQL.isConnected()) {
			if (!(playerExists(uuid))) {
				MySQL.update("INSERT INTO friends2_0_BUNGEE(UUID, ONLINE, SERVER, LASTONLINE) VALUES ('"
						+ uuid + "', '0', 'NOTHING', '');");
				if (playerExists(uuid)) {
					return;
				}
				return;
			}
			return;
		} else {
			MySQL.connect();
			createPlayer(uuid);
		}
	}

	public static void set(String uuid, Object obj, String value) {
		if(MySQL.isConnected()) {
			if (playerExists(uuid)) {
				MySQL.update("UPDATE friends2_0_BUNGEE SET " + value + "='" + obj + "' WHERE UUID='"
						+ uuid + "';");
			} else {
				createPlayer(uuid);
				set(uuid, obj, value);
			}
		} else {
			MySQL.connect();
			set(uuid, obj, value);
		}
	}

	public static Object get(String uuid, String value) {
		if(!MySQL.isConnected()) {
			MySQL.connect();
		}
		if (playerExists(uuid)) {
			try {
				ResultSet rs = MySQL.query(
						"SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + uuid + "';");
				if ((!rs.next()) || (String.valueOf(rs.getString(value)) == null))
					;
				
				if(value.equals("LASTONLINE") && rs.getObject(value).equals(""))
					return 0;
				return rs.getObject(value);

			} catch (Exception ex) {
			}
		}
		if (value.equals("LASTONLINE"))
			return 0;
		return "OFFLINE";
	}

	public static boolean isOnline(String uuid) {
		if(!MySQL.isConnected()) {
			MySQL.connect();
		}
		if (playerExists(uuid)) {

			try {
				ResultSet rs = MySQL.query(
						"SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + uuid + "';");
				if ((!rs.next()) || (Integer.valueOf(rs.getInt("ONLINE")) == null))
					;
				Integer value = rs.getInt("ONLINE");

				return value != 0;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
