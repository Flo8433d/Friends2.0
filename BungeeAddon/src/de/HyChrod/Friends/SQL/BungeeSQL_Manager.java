package de.HyChrod.Friends.SQL;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BungeeSQL_Manager {
	public static Boolean playerExists(String uuid) {
		try {
			ResultSet rs = MySQL.query("SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + uuid + "'");
			if (rs.next()) {
				if (rs.getString("UUID") != null) {
					return Boolean.valueOf(true);
				}
				return Boolean.valueOf(false);
			}
			return Boolean.valueOf(true);
		} catch (SQLException localSQLException) {
		}
		return Boolean.valueOf(false);
	}

	public static Boolean createPlayer(String uuid) {
		if (!playerExists(uuid).booleanValue()) {
			MySQL.update("INSERT INTO friends2_0_BUNGEE(UUID, ONLINE, SERVER, LASTONLINE) VALUES ('" + uuid
					+ "', '0', 'NOTHING', '');");
			if (playerExists(uuid).booleanValue()) {
				return Boolean.valueOf(true);
			}
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(true);
	}

	public static Long getLastOnline(String uuid) {
		Long timeStamp = Long.valueOf(0L);
		if (playerExists(uuid).booleanValue()) {
			try {
				ResultSet rs = MySQL.query("SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + uuid + "';");
				if (rs.next()) {
					String.valueOf(rs.getString("LASTONLINE"));
				}
				timeStamp = Long.valueOf(Long.parseLong(rs.getString("LASTONLINE")));
			} catch (Exception localException) {
			}
		}
		return timeStamp;
	}

	public static void setLastOnline(String uuid, Long timeStamp) {
		if (playerExists(uuid).booleanValue()) {
			MySQL.update("UPDATE friends2_0_BUNGEE SET LASTONLINE='" + timeStamp.toString() + "' WHERE UUID='" + uuid
					+ "';");
			return;
		}
		createPlayer(uuid);
		setLastOnline(uuid, timeStamp);
	}

	public static void setServer(String uuid, String server) {
		if (playerExists(uuid).booleanValue()) {
			MySQL.update("UPDATE friends2_0_BUNGEE SET SERVER='" + server + "' WHERE UUID='" + uuid + "';");
		} else {
			createPlayer(uuid);
			setServer(uuid, server);
		}
	}

	public static String getServer(String uuid) {
		if (playerExists(uuid).booleanValue()) {
			try {
				ResultSet rs = MySQL.query("SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + uuid + "';");
				if (rs.next()) {
					String.valueOf(rs.getString("SERVER"));
				}
				return rs.getString("SERVER");
			} catch (Exception localException) {
			}
		}
		return "OFFLINE";
	}

	public static void setOnline(String uuid, Integer value) {
		if (playerExists(uuid).booleanValue()) {
			MySQL.update("UPDATE friends2_0_BUNGEE SET ONLINE='" + value + "' WHERE UUID='" + uuid + "';");
		} else {
			createPlayer(uuid);
			setOnline(uuid, value);
		}
	}

	public static boolean isOnline(String uuid) {
		if (playerExists(uuid).booleanValue()) {
			try {
				ResultSet rs = MySQL.query("SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + uuid + "';");
				if (rs.next()) {
					Integer.valueOf(rs.getInt("ONLINE"));
				}
				Integer value = Integer.valueOf(rs.getInt("ONLINE"));
				return value.intValue() != 0;
			} catch (Exception localException) {
			}
		}
		return false;
	}
}
