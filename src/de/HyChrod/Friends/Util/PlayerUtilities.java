/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Util;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.MySQL;
import de.HyChrod.Friends.SQL.SQL_Manager;

public class PlayerUtilities {

	public OfflinePlayer player;
	public FileManager mgr;
	public FileConfiguration cfg;
	public File file;
	public boolean sql = false;

	private static HashMap<OfflinePlayer, LinkedList<OfflinePlayer>> friends = new HashMap<>();
	private static HashMap<OfflinePlayer, LinkedList<OfflinePlayer>> requests = new HashMap<>();
	private static HashMap<OfflinePlayer, LinkedList<OfflinePlayer>> blocked = new HashMap<>();
	private static HashMap<OfflinePlayer, LinkedList<String>> options = new HashMap<>();

	public PlayerUtilities(OfflinePlayer player) {
		this.player = player;
		this.mgr = new FileManager();
		this.file = this.mgr.getFile("/Util", "Userdata.dat");
		this.cfg = this.mgr.getConfig(this.file);
		if (MySQL.isConnected())
			this.sql = true;
		if (!PlayerUtilities.friends.containsKey(player) && !PlayerUtilities.requests.containsKey(player)
				&& !PlayerUtilities.blocked.containsKey(player)) {
			this.loadData();
		}
	}

	public static void fullSave(boolean sync) {
		for (OfflinePlayer player : friends.keySet()) {
			new PlayerUtilities(player).saveData(sync);
		}
	}

	public LinkedList<OfflinePlayer> getFriends() {
		if (Friends.bungeeMode) {
			return SQL_Manager.getFriends(player);
		}
		LinkedList<OfflinePlayer> currentFriends = new LinkedList<>();
		if (PlayerUtilities.friends.containsKey(this.player))
			currentFriends = PlayerUtilities.friends.get(this.player);
		return currentFriends;
	}

	public LinkedList<OfflinePlayer> getRequests() {
		if (Friends.bungeeMode) {
			return SQL_Manager.getRequests(player);
		}
		LinkedList<OfflinePlayer> currentRequests = new LinkedList<>();
		if (PlayerUtilities.requests.containsKey(this.player))
			currentRequests = PlayerUtilities.requests.get(this.player);
		return currentRequests;
	}

	public LinkedList<OfflinePlayer> getBlocked() {
		if (Friends.bungeeMode) {
			return SQL_Manager.getBlocked(player);
		}
		LinkedList<OfflinePlayer> currentBlocked = new LinkedList<>();
		if (PlayerUtilities.blocked.containsKey(this.player))
			currentBlocked = PlayerUtilities.blocked.get(this.player);
		return currentBlocked;
	}

	public LinkedList<String> getOptions() {
		if (Friends.bungeeMode) {
			return SQL_Manager.getOptions(player);
		}
		LinkedList<String> currentOptions = new LinkedList<>();
		if (PlayerUtilities.options.containsKey(this.player))
			currentOptions = PlayerUtilities.options.get(this.player);
		return currentOptions;
	}

	public void addFriend(OfflinePlayer player) {
		if (Friends.bungeeMode) {
			LinkedList<OfflinePlayer> friends = this.getFriends();
			friends.add(player);
			SQL_Manager.setFriends(friends, this.player);
			return;
		}
		LinkedList<OfflinePlayer> currentFriends = new LinkedList<>();
		if (PlayerUtilities.friends.containsKey(this.player))
			currentFriends = PlayerUtilities.friends.get(this.player);
		if (!currentFriends.contains(player))
			currentFriends.add(player);
		PlayerUtilities.friends.put(this.player, currentFriends);
	}

	public void removeFriend(OfflinePlayer player) {
		if (Friends.bungeeMode) {
			LinkedList<OfflinePlayer> friends = this.getFriends();
			if (friends.contains(player))
				friends.remove(player);
			SQL_Manager.setFriends(friends, this.player);
			return;
		}
		LinkedList<OfflinePlayer> currentFriends = new LinkedList<>();
		if (PlayerUtilities.friends.containsKey(this.player))
			currentFriends = PlayerUtilities.friends.get(this.player);
		if (currentFriends.contains(player))
			currentFriends.remove(player);
		PlayerUtilities.friends.put(this.player, currentFriends);
	}

	public void addRequest(OfflinePlayer player) {
		if (Friends.bungeeMode) {
			LinkedList<OfflinePlayer> requests = this.getRequests();
			requests.add(player);
			SQL_Manager.setRequests(requests, this.player);
			return;
		}
		LinkedList<OfflinePlayer> currentRequest = new LinkedList<>();
		if (PlayerUtilities.requests.containsKey(this.player))
			currentRequest = PlayerUtilities.requests.get(this.player);
		if (!currentRequest.contains(player))
			currentRequest.add(player);
		PlayerUtilities.requests.put(this.player, currentRequest);
	}

	public void removeRequest(OfflinePlayer player) {
		if (Friends.bungeeMode) {
			LinkedList<OfflinePlayer> requests = this.getRequests();
			if (requests.contains(player))
				requests.remove(player);
			SQL_Manager.setRequests(requests, this.player);
			return;
		}
		LinkedList<OfflinePlayer> currentRequest = new LinkedList<>();
		if (PlayerUtilities.requests.containsKey(this.player))
			currentRequest = PlayerUtilities.requests.get(this.player);
		if (currentRequest.contains(player))
			currentRequest.remove(player);
		PlayerUtilities.requests.put(this.player, currentRequest);

	}

	public void addBlocked(OfflinePlayer player) {
		if (Friends.bungeeMode) {
			LinkedList<OfflinePlayer> blocked = this.getBlocked();
			blocked.add(player);
			SQL_Manager.setBlocked(blocked, this.player);
			return;
		}
		LinkedList<OfflinePlayer> currentBlocked = new LinkedList<>();
		if (PlayerUtilities.blocked.containsKey(this.player))
			currentBlocked = PlayerUtilities.blocked.get(this.player);
		if (!currentBlocked.contains(player))
			currentBlocked.add(player);
		PlayerUtilities.blocked.put(this.player, currentBlocked);
	}

	public void removeBlocked(OfflinePlayer player) {
		if (Friends.bungeeMode) {
			LinkedList<OfflinePlayer> blocked = this.getBlocked();
			if (blocked.contains(player))
				blocked.remove(player);
			SQL_Manager.setBlocked(blocked, this.player);
			return;
		}
		LinkedList<OfflinePlayer> currentBlocked = new LinkedList<>();
		if (PlayerUtilities.blocked.containsKey(this.player))
			currentBlocked = PlayerUtilities.blocked.get(this.player);
		if (currentBlocked.contains(player))
			currentBlocked.remove(player);
		PlayerUtilities.blocked.put(this.player, currentBlocked);
	}

	public void toggleOption(String option) {
		if (Friends.bungeeMode) {
			LinkedList<String> currentOptions = this.getOptions();
			if (currentOptions.contains(option)) {
				currentOptions.remove(option);
			} else {
				currentOptions.add(option);
			}
			SQL_Manager.setOptions(this.player, currentOptions);
			return;
		}
		LinkedList<String> currentOptions = new LinkedList<>();
		if (PlayerUtilities.options.containsKey(this.player))
			currentOptions = PlayerUtilities.options.get(this.player);
		if (currentOptions.contains(option)) {
			currentOptions.remove(option);
		} else {
			currentOptions.add(option);
		}
		PlayerUtilities.options.put(this.player, currentOptions);
	}

	public void setLastOnline(Long timestamp) {
		if (sql) {
			SQL_Manager.setLastOnline(this.player, System.currentTimeMillis());
			return;
		}
		this.mgr.save(file, cfg, "Players." + this.player.getUniqueId().toString() + ".LastOnline",
				System.currentTimeMillis());
		return;
	}

	public Long getLastOnline() {
		if (sql) {
			return SQL_Manager.getLastOnline(this.player);
		}
		if (this.cfg.getString("Players." + this.player.getUniqueId().toString() + ".LastOnline") != null) {
			return this.cfg.getLong("Players." + this.player.getUniqueId().toString() + ".LastOnline");
		}
		return (long) 0;
	}

	public static int[] getLastOnline(Long value) {
		int[] timestamps = new int[4];

		Double timeMillies = (double) System.currentTimeMillis() - (double) value;
		int seconds = (int) (timeMillies / 1000);
		int minutes = 0;
		int hours = 0;
		int days = 0;

		if (value != 0) {
			if (seconds > 60) {
				while (seconds > 60) {
					seconds = seconds - 60;
					minutes++;
				}
			}
			if (minutes > 60) {
				while (minutes > 60) {
					minutes = minutes - 60;
					hours++;
				}
			}
			if (hours > 24) {
				while (hours > 24) {
					hours = hours - 24;
					days++;
				}
			}
		}
		timestamps[0] = seconds;
		timestamps[1] = minutes;
		timestamps[2] = hours;
		timestamps[3] = days;
		return timestamps;
	}

	public void saveData(boolean sync) {
		if (Friends.bungeeMode) {
			return;
		}
		if (sql) {
			SQL_Manager.setFriends(this.getFriends(), this.player);
			SQL_Manager.setRequests(this.getRequests(), this.player);
			SQL_Manager.setBlocked(this.getBlocked(), this.player);
			SQL_Manager.setOptions(this.player, this.getOptions());
			return;
		}
		if (PlayerUtilities.friends.containsKey(this.player)) {
			LinkedList<String> serializedFriends = new LinkedList<>();
			for (OfflinePlayer randomFriend : PlayerUtilities.friends.get(this.player)) {
				serializedFriends.add(randomFriend.getUniqueId().toString());
			}
			this.mgr.save(file, cfg, "Players." + this.player.getUniqueId().toString() + ".Friends", serializedFriends);
		}
		if (PlayerUtilities.requests.containsKey(this.player)) {
			LinkedList<String> serializedRequests = new LinkedList<>();
			for (OfflinePlayer randomRequest : PlayerUtilities.requests.get(this.player)) {
				serializedRequests.add(randomRequest.getUniqueId().toString());
			}
			this.mgr.save(file, cfg, "Players." + this.player.getUniqueId().toString() + ".Requests",
					serializedRequests);
		}
		if (PlayerUtilities.blocked.containsKey(this.player)) {
			LinkedList<String> serializedBlocked = new LinkedList<>();
			for (OfflinePlayer randomBlocked : PlayerUtilities.blocked.get(this.player)) {
				serializedBlocked.add(randomBlocked.getUniqueId().toString());
			}
			this.mgr.save(file, cfg, "Players." + this.player.getUniqueId().toString() + ".Blocked", serializedBlocked);
		}
		if (PlayerUtilities.options.containsKey(this.player)) {
			this.mgr.save(file, cfg, "Players." + this.player.getUniqueId().toString() + ".Options",
					PlayerUtilities.options.get(this.player));
		}
	}

	public void loadData() {
		if (Friends.bungeeMode) {
			return;
		}
		if (sql) {
			LinkedList<OfflinePlayer> currentFriends = new LinkedList<>();
			if (PlayerUtilities.friends.containsKey(this.player))
				currentFriends = PlayerUtilities.friends.get(this.player);
			for (OfflinePlayer players : SQL_Manager.getFriends(this.player)) {
				if (!currentFriends.contains(players)) {
					currentFriends.add(players);
				}
			}
			PlayerUtilities.friends.put(this.player, currentFriends);
			LinkedList<OfflinePlayer> currentRequests = new LinkedList<>();
			if (PlayerUtilities.requests.containsKey(this.player))
				currentRequests = PlayerUtilities.requests.get(this.player);
			for (OfflinePlayer players : SQL_Manager.getRequests(this.player)) {
				if (!currentRequests.contains(players)) {
					currentRequests.add(players);
				}
			}
			PlayerUtilities.requests.put(this.player, currentRequests);
			LinkedList<OfflinePlayer> currentBlocked = new LinkedList<>();
			if (PlayerUtilities.blocked.containsKey(this.player))
				currentBlocked = PlayerUtilities.blocked.get(this.player);
			for (OfflinePlayer players : SQL_Manager.getBlocked(this.player)) {
				if (!currentBlocked.contains(players)) {
					currentBlocked.add(players);
				}
			}
			PlayerUtilities.blocked.put(this.player, currentBlocked);
			LinkedList<String> currentOptions = new LinkedList<>();
			if (PlayerUtilities.options.containsKey(this.player))
				currentOptions = PlayerUtilities.options.get(this.player);
			for (String option : SQL_Manager.getOptions(this.player)) {
				if (!currentOptions.contains(option)) {
					currentOptions.add(option);
				}
			}
			PlayerUtilities.options.put(this.player, currentOptions);
			return;
		}
		if (this.cfg.getString("Players." + this.player.getUniqueId().toString() + ".Friends") != null) {
			LinkedList<OfflinePlayer> currentFriends = new LinkedList<>();
			if (PlayerUtilities.friends.containsKey(this.player))
				currentFriends = PlayerUtilities.friends.get(this.player);
			for (String uuid : this.cfg.getStringList("Players." + this.player.getUniqueId().toString() + ".Friends")) {
				OfflinePlayer randomFriend = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
				if (!currentFriends.contains(randomFriend)) {
					currentFriends.add(randomFriend);
				}
			}
			PlayerUtilities.friends.put(this.player, currentFriends);
		}
		if (this.cfg.getString("Players." + this.player.getUniqueId().toString() + ".Requests") != null) {
			LinkedList<OfflinePlayer> currentRequests = new LinkedList<>();
			if (PlayerUtilities.requests.containsKey(this.player))
				currentRequests = PlayerUtilities.requests.get(this.player);
			for (String uuid : this.cfg
					.getStringList("Players." + this.player.getUniqueId().toString() + ".Requests")) {
				OfflinePlayer randomRequest = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
				if (!currentRequests.contains(randomRequest)) {
					currentRequests.add(randomRequest);
				}
			}
			PlayerUtilities.requests.put(this.player, currentRequests);
		}
		if (this.cfg.getString("Players." + this.player.getUniqueId().toString() + ".Blocked") != null) {
			LinkedList<OfflinePlayer> currentBlocked = new LinkedList<>();
			if (PlayerUtilities.blocked.containsKey(this.player))
				currentBlocked = PlayerUtilities.blocked.get(this.player);
			for (String uuid : this.cfg.getStringList("Players." + this.player.getUniqueId().toString() + ".Blocked")) {
				OfflinePlayer randomBlocked = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
				if (!currentBlocked.contains(randomBlocked)) {
					currentBlocked.add(randomBlocked);
				}
			}
			PlayerUtilities.blocked.put(this.player, currentBlocked);
		}
		if (this.cfg.getString("Players." + this.player.getUniqueId().toString() + ".Options") != null) {
			LinkedList<String> currentOptions = new LinkedList<>();
			if (PlayerUtilities.options.containsKey(this.player))
				currentOptions = PlayerUtilities.options.get(this.player);
			for (String option : this.cfg
					.getStringList("Players." + this.player.getUniqueId().toString() + ".Options")) {
				if (!currentOptions.contains(option)) {
					currentOptions.add(option);
				}
			}
			PlayerUtilities.options.put(this.player, currentOptions);
		}
	}

}
