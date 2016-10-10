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

public class FriendsAPI {

	public LinkedList<String> getFriends(String uuid) {
		return new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).get(0);
	}

	public static LinkedList<String> getRequests(String uuid) {
		return new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).get(1);
	}

	public static LinkedList<String> getBlocked(String uuid) {
		return new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).get(2);
	}

	public static LinkedList<String> getOptions(String uuid) {
		return new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).get(3);
	}

	public static void addFriend(String uuid, String uuidToAdd) {
		new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).update(uuidToAdd, 0, true);
	}

	public static void addRequest(String uuid, String uuidToAdd) {
		new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).update(uuidToAdd, 1, true);
	}

	public static void addBlocked(String uuid, String uuidToAdd) {
		new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).update(uuidToAdd, 2, true);
	}

	public static void toggle(String uuid, String optionToAdd) {
		new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).toggleOption(optionToAdd);
	}

	public static void removeFriend(String uuid, String uuidToRemove) {
		new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).update(uuidToRemove, 0, false);
	}

	public static void removeRequest(String uuid, String uuidToRemove) {
		new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).update(uuidToRemove, 1, false);
	}

	public static void removeBlocked(String uuid, String uuidToRemove) {
		new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).update(uuidToRemove, 2, false);
	}

	public static Long getLastOnline(String uuid) {
		return new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).getLastOnline();
	}

	public static void setLastOnline(String uuid, Long lastOnline) {
		new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).setLastOnline(lastOnline);
	}

}
