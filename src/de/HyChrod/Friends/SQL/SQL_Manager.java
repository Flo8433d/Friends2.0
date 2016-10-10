package de.HyChrod.Friends.SQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class SQL_Manager {
	
	public static Boolean playerExists(String uuid) {
		try {
			ResultSet rs = MySQL
					.query("SELECT * FROM friends2_0 WHERE UUID= '" + uuid + "'");
			if (rs.next()) {
				if (rs.getString("UUID") != null) {
					return Boolean.valueOf(true);
				}
				return Boolean.valueOf(false);
			}
			return Boolean.valueOf(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Boolean.valueOf(false);
	}

	public static Boolean createPlayer(String uuid) {
		if (!playerExists(uuid).booleanValue()) {
			MySQL.update("INSERT INTO friends2_0(UUID, FRIENDS, BLOCKED, REQUESTS, OPTIONS, LASTONLINE) VALUES ('"
					+ uuid + "', '', '', '','','');");
			if (playerExists(uuid)) {
				return Boolean.valueOf(true);
			}
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(true);
	}
	
	public static void set(LinkedList<String> data, String uuid, String value) {
		if (playerExists(uuid)) {
			String serialized = "";
			for (String players : data) {
				serialized = serialized + players + "//;";
			}
			MySQL.update("UPDATE friends2_0 SET " + value + "='" + serialized + "' WHERE UUID='"
					+ uuid + "';");
		} else {
			createPlayer(uuid);
			set(data, uuid, value);
		}
	}

	public static LinkedList<String> get(String uuid, String value) {
		LinkedList<String> data = new LinkedList<>();
		
		if (playerExists(uuid)) {		
			try {
				ResultSet rs = MySQL.query("SELECT * FROM friends2_0 WHERE UUID= '" + uuid + "';");
				if ((rs.next()) && (String.valueOf(rs.getString(value))) == null) {

				}
				String[] uuids = rs.getString(value).split("//;");
				for (int i = 0; i < uuids.length; i++) {
					if (uuids[i].length() > 6)
						data.add(uuids[i]);
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
