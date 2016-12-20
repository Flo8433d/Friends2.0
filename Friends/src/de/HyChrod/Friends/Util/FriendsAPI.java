/*
*
* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.Util;

import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import de.HyChrod.Friends.Friends;

public class FriendsAPI {
	
	public static LinkedList<String> getFriends(String uuid) {
		LinkedList<String> uuids = new LinkedList<>();
		for(Object obj : new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).get(0, true)) {
			if (Friends.bungeeMode)
				uuids.add(((OfflinePlayer)obj).getUniqueId().toString());
			else
				uuids.add(((String)obj));
		}
		return uuids;
	}
	
	public static LinkedList<String> getRequests(String uuid) {
		LinkedList<String> uuids = new LinkedList<>();
		for(Object obj : new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).get(1, true)) {
			if (Friends.bungeeMode)
				uuids.add(((OfflinePlayer)obj).getUniqueId().toString());
			else
				uuids.add(((String)obj));
		}
		return uuids;
	}
	
	public static LinkedList<String> getBlocked(String uuid) {
		LinkedList<String> uuids = new LinkedList<>();
		for(Object obj : new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).get(2, true)) {
			if (Friends.bungeeMode)
				uuids.add(((OfflinePlayer)obj).getUniqueId().toString());
			else
				uuids.add(((String)obj));
		}
		return uuids;
	}
	
	public static LinkedList<String> getOptions(String uuid) {
		LinkedList<String> uuids = new LinkedList<>();
		for(Object obj : new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).get(3, true))
			uuids.add(((String)obj));
		return uuids;
	}
	
	public static void addFriend(String player, String toAdd) {
		PlayerUtilities pu = new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(player)));
		pu.update(toAdd, 0, true);
	}
	
	public static void addRequest(String player, String toAdd) {
		PlayerUtilities pu = new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(player)));
		pu.update(toAdd, 0, true);
	}
	
	public static void addBlocked(String player, String toAdd) {
		PlayerUtilities pu = new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(player)));
		pu.update(toAdd, 0, true);
	}
	
	public static void toggleOption(String player, String option) {
		new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(player))).toggleOption(option);
	}
	
	public static void removeFriend(String player, String toAdd) {
		PlayerUtilities pu = new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(player)));
		pu.update(toAdd, 0, false);
	}
	
	public static void removeRequest(String player, String toAdd) {
		PlayerUtilities pu = new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(player)));
		pu.update(toAdd, 0, false);
	}
	
	public static void removeBlocked(String player, String toAdd) {
		PlayerUtilities pu = new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(player)));
		pu.update(toAdd, 0, false);
	}
	
	public static void openInventory(String uuid, InventoryTypes type) {
		new InventoryPage(Friends.getInstance(), Bukkit.getPlayer(UUID.fromString(uuid)), 1, 
				new PlayerUtilities(Bukkit.getOfflinePlayer(UUID.fromString(uuid))), type);
	}

}
