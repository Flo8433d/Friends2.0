package de.HyChrod.Friends.SQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SQL_Manager {
	
	public static Boolean playerExists(String uuid) {
		if(!MySQL.isConnected()) {
			MySQL.connect();
		}
		try {
			ResultSet rs = MySQL.query("SELECT * FROM friends2_0 WHERE UUID= '" + uuid + "'");
			if (rs.next()) {
				if (rs.getString("UUID") != null) {
					return Boolean.valueOf(true);
				}
				return Boolean.valueOf(false);
			}
			return Boolean.valueOf(false);
		} catch (SQLException localSQLException) {
		}
		return Boolean.valueOf(false);
	}
	public static void createPlayer(String uuid) {
		if(MySQL.isConnected()) {
			if (!playerExists(uuid).booleanValue()) {
				MySQL.update("INSERT INTO friends2_0(UUID, FRIENDS, BLOCKED, REQUESTS, OPTIONS, LASTONLINE) VALUES ('"
						+ uuid + "', '', '', '','','');");
				if (playerExists(uuid).booleanValue()) {
					return;
				}
				return ;
			}
			return;
		} else {
			MySQL.connect();
			createPlayer(uuid);
		}
	}

	public static LinkedList<String> getFriends(String uuid) {
		if(!MySQL.isConnected()) {
			MySQL.connect();
		}
		LinkedList<String> friends = new LinkedList();
		if (playerExists(uuid).booleanValue()) {
			try {
				ResultSet rs = MySQL.query("SELECT * FROM friends2_0 WHERE UUID= '" + uuid + "';");
				if (rs.next()) {
					String.valueOf(rs.getString("FRIENDS"));
				}
				String[] uuids = rs.getString("FRIENDS").split("//;");
				for (int i = 0; i < uuids.length; i++) {
					if (uuids[i].length() > 20) {
						friends.add(uuids[i]);
					}
				}
			} catch (Exception localException) {
			}
		}
		return friends;
	}

	public static List<String> getOptions(String uuid) {
		if(!MySQL.isConnected()) {
			MySQL.connect();
		}
		List<String> options = new ArrayList();
		if (playerExists(uuid).booleanValue()) {
			try {
				ResultSet rs = MySQL.query("SELECT * FROM friends2_0 WHERE UUID= '" + uuid + "';");
				if (rs.next()) {
					String.valueOf(rs.getString("OPTIONS"));
				}
				String[] option = rs.getString("OPTIONS").split("//;");
				for (int i = 0; i < option.length; i++) {
					options.add(option[i]);
				}
			} catch (Exception localException) {
			}
		}
		return options;
	}
}
