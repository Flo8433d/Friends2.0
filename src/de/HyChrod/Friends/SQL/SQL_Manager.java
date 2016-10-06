package de.HyChrod.Friends.SQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SQL_Manager {

	public static Boolean playerExists(OfflinePlayer player) {
		try {
			ResultSet rs = MySQL
					.query("SELECT * FROM friends2_0 WHERE UUID= '" + player.getUniqueId().toString() + "'");
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

	public static Boolean createPlayer(OfflinePlayer player) {
		if (!playerExists(player).booleanValue()) {
			MySQL.update("INSERT INTO friends2_0(UUID, FRIENDS, BLOCKED, REQUESTS, OPTIONS, LASTONLINE) VALUES ('"
					+ player.getUniqueId().toString() + "', '', '', '','','');");
			if (playerExists(player).booleanValue()) {
				return Boolean.valueOf(true);
			}
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(true);
	}

	public static LinkedList<OfflinePlayer> get(OfflinePlayer player, String value) {
		LinkedList<OfflinePlayer> data = new LinkedList<>();
		if (playerExists(player)) {
			try {

				ResultSet rs = MySQL
						.query("SELECT * FROM friends2_0 WHERE UUID= '" + player.getUniqueId().toString() + "';");
				if ((rs.next()) && (String.valueOf(rs.getString(value))) == null) {

				}
				String[] uuids = rs.getString(value).split("//;");
				for (int i = 0; i < uuids.length; i++) {
					if (uuids[i].length() > 20)
						data.add(Bukkit.getOfflinePlayer(UUID.fromString(uuids[i])));
				}

			} catch (Exception ex) {
			}
		}
		return data;
	}

	public static Long getLastOnline(OfflinePlayer player) {
		Long timeStamp = Long.valueOf(0L);
		if (playerExists(player).booleanValue()) {
			try {
				ResultSet rs = MySQL
						.query("SELECT * FROM friends2_0 WHERE UUID= '" + player.getUniqueId().toString() + "';");
				if ((rs.next()) && (String.valueOf(rs.getString("LASTONLINE")) == null)) {
				}
				timeStamp = Long.valueOf(Long.parseLong(rs.getString("LASTONLINE")));
			} catch (Exception localException) {
			}
		}
		return timeStamp;
	}

	public static void setLastOnline(OfflinePlayer player, Long timeStamp) {
		if (playerExists(player).booleanValue()) {
			MySQL.update("UPDATE friends2_0 SET LASTONLINE='" + timeStamp.toString() + "' WHERE UUID='"
					+ player.getUniqueId().toString() + "';");
			return;
		}
		createPlayer(player);
		setLastOnline(player, timeStamp);
	}

	public static void set(LinkedList<OfflinePlayer> data, OfflinePlayer player, String value) {
		if (playerExists(player)) {
			String serialized = "";
			for (OfflinePlayer players : data) {
				serialized = serialized + players.getUniqueId().toString() + "//;";
			}
			MySQL.update("UPDATE friends2_0 SET " + value + "='" + serialized + "' WHERE UUID='"
					+ player.getUniqueId().toString() + "';");
		} else {
			createPlayer(player);
			set(data, player, value);
		}
	}

	public static void setOptions(OfflinePlayer player, List<String> options) {
		if (playerExists(player).booleanValue()) {
			String serializedOptions = "";
			for (String option : options) {
				if (option.length() > 5) {
					serializedOptions = serializedOptions + "//;" + option;
				}
			}
			MySQL.update("UPDATE friends2_0 SET OPTIONS='" + serializedOptions + "' WHERE UUID='"
					+ player.getUniqueId().toString() + "';");
		} else {
			createPlayer(player);
			setOptions(player, options);
		}
	}

	public static LinkedList<String> getOptions(OfflinePlayer player) {
		LinkedList<String> options = new LinkedList();
		if (playerExists(player).booleanValue()) {
			try {
				ResultSet rs = MySQL
						.query("SELECT * FROM friends2_0 WHERE UUID= '" + player.getUniqueId().toString() + "';");
				if ((rs.next()) && (String.valueOf(rs.getString("OPTIONS")) == null)) {
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
