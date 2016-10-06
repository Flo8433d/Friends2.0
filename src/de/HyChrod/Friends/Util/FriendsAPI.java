/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Util;

import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class FriendsAPI {

	public LinkedList<OfflinePlayer> getFriends(String uuid) {
		return new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).get(0);
	}

	public static LinkedList<OfflinePlayer> getRequests(String uuid) {
		return new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).get(1);
	}

	public static LinkedList<OfflinePlayer> getBlocked(String uuid) {
		return new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).get(2);
	}

	public static LinkedList<String> getOptions(String uuid) {
		return new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).getOptions();
	}

	public static void addFriend(String uuid, String uuidToAdd) {
		new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid)))
				.update(Bukkit.getOfflinePlayer(UUID.fromString(uuidToAdd)), 0, true);
	}

	public static void addRequest(String uuid, String uuidToAdd) {
		new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid)))
				.update(Bukkit.getOfflinePlayer(UUID.fromString(uuidToAdd)), 1, true);
	}

	public static void addBlocked(String uuid, String uuidToAdd) {
		new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid)))
				.update(Bukkit.getOfflinePlayer(UUID.fromString(uuidToAdd)), 2, true);
	}

	public static void toggle(String uuid, String optionToAdd) {
		new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).toggleOption(optionToAdd);
	}

	public static void removeFriend(String uuid, String uuidToRemove) {
		new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid)))
				.update(Bukkit.getOfflinePlayer(UUID.fromString(uuidToRemove)), 0, false);
	}

	public static void removeRequest(String uuid, String uuidToRemove) {
		new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid)))
				.update(Bukkit.getOfflinePlayer(UUID.fromString(uuidToRemove)), 1, false);
	}

	public static void removeBlocked(String uuid, String uuidToRemove) {
		new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid)))
				.update(Bukkit.getOfflinePlayer(UUID.fromString(uuidToRemove)), 2, false);
	}

	public static Long getLastOnline(String uuid) {
		return new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).getLastOnline();
	}

	public static void setLastOnline(String uuid, Long lastOnline) {
		new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).setLastOnline(lastOnline);
	}

}
