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

	private static HashMap<OfflinePlayer, LinkedList<LinkedList<OfflinePlayer>>> userdata = new HashMap<>();
	private static HashMap<OfflinePlayer, LinkedList<String>> options = new HashMap<>();

	public PlayerUtilities(OfflinePlayer player) {
		this.player = player;
		this.mgr = new FileManager();
		this.file = this.mgr.getFile("/Util", "Userdata.dat");
		this.cfg = this.mgr.getConfig(this.file);
		if (MySQL.isConnected())
			this.sql = true;
		if (!PlayerUtilities.userdata.containsKey(player)) {
			this.loadData();
		}
	}

	public static void fullSave(boolean sync) {
		for (OfflinePlayer player : userdata.keySet()) {
			new PlayerUtilities(player).saveData(sync);
		}
	}

	public LinkedList<OfflinePlayer> get(Integer i) {
		if (Friends.bungeeMode) {
			return null;
		}
		LinkedList<OfflinePlayer> current = new LinkedList<>();
		if (PlayerUtilities.userdata.containsKey(this.player)) {
			LinkedList<LinkedList<OfflinePlayer>> hash = PlayerUtilities.userdata.get(this.player);
			if (hash.size() > i)
				current = PlayerUtilities.userdata.get(this.player).get(i);
		}
		return current;
	}

	public void update(OfflinePlayer player, Integer i, boolean add) {
		if (Friends.bungeeMode) {
			return;
		}
		LinkedList<OfflinePlayer> current = new LinkedList<>();
		if (PlayerUtilities.userdata.containsKey(this.player)) {
			LinkedList<LinkedList<OfflinePlayer>> hash = PlayerUtilities.userdata.get(this.player);
			if (hash.size() > i)
				current = PlayerUtilities.userdata.get(this.player).get(i);
		}
		if (current.contains(player) && !add)
			current.remove(player);
		if (!current.contains(player) && add)
			current.add(player);

		LinkedList<LinkedList<OfflinePlayer>> hash = new LinkedList<>();
		if (PlayerUtilities.userdata.containsKey(this.player))
			hash = PlayerUtilities.userdata.get(this.player);
		if (hash.size() <= i) {
			while (hash.size() <= i) {
				hash.add(new LinkedList<OfflinePlayer>());
			}
		}
		hash.set(i, current);

		PlayerUtilities.userdata.put(this.player, hash);
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
		if (Friends.bungeeMode)
			return;
		if (sql) {
			SQL_Manager.set(this.get(0), this.player, "FRIENDS");
			SQL_Manager.set(this.get(1), this.player, "REQUESTS");
			SQL_Manager.set(this.get(2), this.player, "BLOCKED");
			SQL_Manager.setOptions(this.player, this.getOptions());
			return;
		}
		if (PlayerUtilities.userdata.containsKey(this.player)) {
			for (int i = 0; i <= 2; i++) {
				LinkedList<String> serialized = new LinkedList<>();
				if (PlayerUtilities.userdata.get(this.player).size() > i) {
					for (OfflinePlayer random : PlayerUtilities.userdata.get(this.player).get(i)) {
						serialized.add(random.getUniqueId().toString());
					}
				}

				String saveData = "";
				if (i == 0)
					saveData = "Friends";
				if (i == 1)
					saveData = "Requests";
				if (i == 2)
					saveData = "Blocked";

				this.mgr.save(file, cfg, "Players." + this.player.getUniqueId().toString() + "." + saveData,
						serialized);
			}
		}
		if (PlayerUtilities.options.containsKey(this.player)) {
			this.mgr.save(file, cfg, "Players." + this.player.getUniqueId().toString() + ".Options",
					PlayerUtilities.options.get(this.player));
		}
	}

	public void loadData() {
		if (Friends.bungeeMode)
			return;
		if (sql) {
			String[] values = { "FRIENDS", "REQUESTS", "BLOCKED" };
			for (int i = 0; i <= 2; i++) {
				LinkedList<LinkedList<OfflinePlayer>> hash = new LinkedList<>();
				if (PlayerUtilities.userdata.containsKey(this.player))
					hash = PlayerUtilities.userdata.get(this.player);

				hash.set(i, SQL_Manager.get(this.player, values[i]));
				PlayerUtilities.userdata.put(this.player, hash);
			}
			return;
		}

		for (int i = 0; i <= 2; i++) {
			String saveData = "";
			if (i == 0)
				saveData = "Friends";
			if (i == 1)
				saveData = "Requests";
			if (i == 2)
				saveData = "Blocked";

			if (this.cfg.getString("Players." + this.player.getUniqueId().toString() + "." + saveData) != null) {
				LinkedList<OfflinePlayer> current = new LinkedList<>();
				if (PlayerUtilities.userdata.containsKey(this.player)) {
					LinkedList<LinkedList<OfflinePlayer>> hash = PlayerUtilities.userdata.get(this.player);
					if (hash.size() > i)
						current = PlayerUtilities.userdata.get(this.player).get(i);
				}
				for (String uuid : this.cfg
						.getStringList("Players." + this.player.getUniqueId().toString() + "." + saveData)) {
					OfflinePlayer random = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
					if (!current.contains(random))
						current.add(random);
				}

				LinkedList<LinkedList<OfflinePlayer>> hash = new LinkedList<>();
				if (PlayerUtilities.userdata.containsKey(this.player))
					hash = PlayerUtilities.userdata.get(this.player);
				if (hash.size() <= i) {
					while (hash.size() <= i) {
						hash.add(new LinkedList<OfflinePlayer>());
					}
				}
				hash.set(i, current);

				PlayerUtilities.userdata.put(this.player, hash);
			}
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
