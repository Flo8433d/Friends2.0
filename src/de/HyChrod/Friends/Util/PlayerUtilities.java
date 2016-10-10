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

	private static String[] values = { "FRIENDS", "REQUESTS", "BLOCKED", "OPTIONS" };
	private static HashMap<OfflinePlayer, LinkedList<LinkedList<String>>> userdata = new HashMap<>();

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

	public LinkedList<String> get(Integer i) {
		if (Friends.bungeeMode) {
			return SQL_Manager.get(this.player.getUniqueId().toString(), values[i]);
		}

		LinkedList<String> current = new LinkedList<>();
		if (PlayerUtilities.userdata.containsKey(this.player)) {
			LinkedList<LinkedList<String>> hash = PlayerUtilities.userdata.get(this.player);
			if (hash.size() > i)
				current = PlayerUtilities.userdata.get(this.player).get(i);
		}
		return current;
	}

	public void update(String obj, Integer i, boolean add) {
		LinkedList<String> current = this.get(i);

		if (current.contains(obj) && !add)
			current.remove(obj);
		if (!current.contains(obj) && add)
			current.add(obj);

		if (Friends.bungeeMode) {
			SQL_Manager.set(current, this.player.getUniqueId().toString(), values[i]);
			return;
		}

		LinkedList<LinkedList<String>> hash = new LinkedList<>();
		if (PlayerUtilities.userdata.containsKey(this.player))
			hash = PlayerUtilities.userdata.get(this.player);
		if (hash.size() <= i) {
			while (hash.size() <= i) {
				hash.add(new LinkedList<String>());
			}
		}
		hash.set(i, current);
		PlayerUtilities.userdata.put(this.player, hash);
	}

	public void toggleOption(String option) {
		LinkedList<String> currentOptions = this.get(3);
		if (currentOptions.contains(option)) {
			this.update(option, 3, false);
			return;
		}
		this.update(option, 3, true);
		return;
	}

	public void setLastOnline(Long timestamp) {
		if (sql) {
			SQL_Manager.setLastOnline(this.player.getUniqueId().toString(), System.currentTimeMillis());
			return;
		}
		this.mgr.save(file, cfg, "Players." + this.player.getUniqueId().toString() + ".LastOnline",
				System.currentTimeMillis());
		return;
	}

	public Long getLastOnline() {
		if (sql) {
			return SQL_Manager.getLastOnline(this.player.getUniqueId().toString());
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
			for (int i = 0; i <= 3; i++)
				SQL_Manager.set(this.get(i), this.player.getUniqueId().toString(), values[i]);
			return;
		}
		if (PlayerUtilities.userdata.containsKey(this.player)) {
			for (int i = 0; i <= 3; i++) {
				LinkedList<String> serialized = new LinkedList<>();
				if (PlayerUtilities.userdata.get(this.player).size() > i) {
					serialized = PlayerUtilities.userdata.get(this.player).get(i);
				}
				this.mgr.save(file, cfg, "Players." + this.player.getUniqueId().toString() + "." + values[i],
						serialized);
			}
		}
	}

	public void loadData() {
		if (Friends.bungeeMode)
			return;
		if (sql) {
			for (int i = 0; i <= 3; i++) {
				LinkedList<LinkedList<String>> hash = new LinkedList<>();
				if (PlayerUtilities.userdata.containsKey(this.player))
					hash = PlayerUtilities.userdata.get(this.player);
				if (hash.size() <= i) {
					while (hash.size() <= i) {
						hash.add(new LinkedList<String>());
					}
				}
				hash.set(i, SQL_Manager.get(this.player.getUniqueId().toString(), values[i]));
				PlayerUtilities.userdata.put(this.player, hash);
			}
			return;
		}

		for (int i = 0; i <= 3; i++) {
			if (this.cfg.getString("Players." + this.player.getUniqueId().toString() + "." + values[i]) != null) {
				LinkedList<String> current = new LinkedList<>();
				if (PlayerUtilities.userdata.containsKey(this.player)) {
					LinkedList<LinkedList<String>> hash = PlayerUtilities.userdata.get(this.player);
					if (hash.size() > i)
						current = PlayerUtilities.userdata.get(this.player).get(i);
				}
				for (String uuid : this.cfg
						.getStringList("Players." + this.player.getUniqueId().toString() + "." + values[i])) {
					if (!current.contains(uuid))
						current.add(uuid);
				}

				LinkedList<LinkedList<String>> hash = new LinkedList<>();
				if (PlayerUtilities.userdata.containsKey(this.player))
					hash = PlayerUtilities.userdata.get(this.player);
				if (hash.size() <= i) {
					while (hash.size() <= i) {
						hash.add(new LinkedList<String>());
					}
				}
				hash.set(i, current);

				PlayerUtilities.userdata.put(this.player, hash);
			}
		}
	}

}
