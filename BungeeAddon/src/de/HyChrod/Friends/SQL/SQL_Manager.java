package de.HyChrod.Friends.SQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SQL_Manager {
	public static Boolean playerExists(String uuid) {
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

	public static Boolean createPlayer(String uuid) {
		if (!playerExists(uuid).booleanValue()) {
			MySQL.update("INSERT INTO friends2_0(UUID, FRIENDS, BLOCKED, REQUESTS, OPTIONS, LASTONLINE) VALUES ('"
					+ uuid + "', '', '', '','','');");
			if (playerExists(uuid).booleanValue()) {
				return Boolean.valueOf(true);
			}
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(true);
	}

	public static LinkedList<String> getFriends(String uuid) {
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

	public static Long getLastOnline(String uuid) {
		Long timeStamp = Long.valueOf(0L);
		if (playerExists(uuid).booleanValue()) {
			try {
				ResultSet rs = MySQL.query("SELECT * FROM friends2_0 WHERE UUID= '" + uuid + "';");
				if (rs.next()) {
					String.valueOf(rs.getString("LASTONLINE"));
				}
				timeStamp = Long.valueOf(Long.parseLong(rs.getString("LASTONLINE")));
			} catch (Exception localException) {
			}
		}
		return timeStamp;
	}

	public static Boolean setLastOnline(String uuid, Long timeStamp) {
		if (playerExists(uuid).booleanValue()) {
			MySQL.update("UPDATE friends2_0 SET LASTONLINE='" + timeStamp.toString() + "' WHERE UUID='" + uuid + "';");
			return Boolean.valueOf(true);
		}
		createPlayer(uuid);
		setLastOnline(uuid, timeStamp);

		return Boolean.valueOf(false);
	}

	public static LinkedList<String> getRequests(String uuid) {
		LinkedList<String> friends = new LinkedList();
		if (playerExists(uuid).booleanValue()) {
			try {
				ResultSet rs = MySQL.query("SELECT * FROM friends2_0 WHERE UUID= '" + uuid + "';");
				if (rs.next()) {
					String.valueOf(rs.getString("REQUESTS"));
				}
				String[] uuids = rs.getString("REQUESTS").split("//;");
				for (int i = 0; i < uuids.length; i++) {
					if (uuids[i].length() > 20) {
						friends.add(uuids[i]);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return friends;
	}

	public static LinkedList<String> getBlocked(String uuid) {
		LinkedList<String> friends = new LinkedList();
		if (playerExists(uuid).booleanValue()) {
			try {
				ResultSet rs = MySQL.query("SELECT * FROM friends2_0 WHERE UUID= '" + uuid + "';");
				if (rs.next()) {
					String.valueOf(rs.getString("BLOCKED"));
				}
				String[] uuids = rs.getString("BLOCKED").split("//;");
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

	public static void setBlocked(List<String> currentBlocked, String uuid) {
		if (playerExists(uuid).booleanValue()) {
			String serializedBlocked = "";
			for (String blocked : currentBlocked) {
				serializedBlocked = serializedBlocked + blocked + "//;";
			}
			MySQL.update("UPDATE friends2_0 SET BLOCKED='" + serializedBlocked + "' WHERE UUID='" + uuid + "';");
		} else {
			createPlayer(uuid);
			setBlocked(currentBlocked, uuid);
		}
	}

	public static void setRequests(List<String> currentRequests, String uuid) {
		if (playerExists(uuid).booleanValue()) {
			String serializedRequests = "";
			for (String requests : currentRequests) {
				serializedRequests = serializedRequests + requests + "//;";
			}
			MySQL.update("UPDATE friends2_0 SET REQUESTS='" + serializedRequests + "' WHERE UUID='" + uuid + "';");
		} else {
			createPlayer(uuid);
			setRequests(currentRequests, uuid);
		}
	}

	public static void setFriends(List<String> currentFriends, String uuid) {
		if (playerExists(uuid).booleanValue()) {
			String serializedFriends = "";
			for (String friends : currentFriends) {
				serializedFriends = serializedFriends + friends + "//;";
			}
			MySQL.update("UPDATE friends2_0 SET FRIENDS='" + serializedFriends + "' WHERE UUID='" + uuid + "';");
		} else {
			createPlayer(uuid);
			setFriends(currentFriends, uuid);
		}
	}

	public static void setOptions(String uuid, List<String> options) {
		if (playerExists(uuid).booleanValue()) {
			String serializedOptions = "";
			for (String option : options) {
				if (option.length() > 5) {
					serializedOptions = serializedOptions + "//;" + option;
				}
			}
			MySQL.update("UPDATE friends2_0 SET OPTIONS='" + serializedOptions + "' WHERE UUID='" + uuid + "';");
		} else {
			createPlayer(uuid);
			setOptions(uuid, options);
		}
	}

	public static List<String> getOptions(String uuid) {
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
