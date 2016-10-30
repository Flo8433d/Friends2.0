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
import de.HyChrod.Friends.SQL.BungeeSQL_Manager;
import de.HyChrod.Friends.SQL.MySQL;
import de.HyChrod.Friends.SQL.SQL_Manager;

public class PlayerUtilities {

	public OfflinePlayer player;
	public FileManager mgr;
	public FileConfiguration cfg;
	public File file;
	public boolean sql = false;
	
	private static String[] values = { "FRIENDS", "REQUESTS", "BLOCKED", "OPTIONS" };
	private static HashMap<OfflinePlayer, LinkedList<LinkedList<Object>>> userdata = new HashMap<>();
	private static HashMap<OfflinePlayer, String> status = new HashMap<>();
	
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
		for(OfflinePlayer player : status.keySet()) {
			new PlayerUtilities(player).saveData(sync);
		}
	}
	
	public String getStatus() {
		if(Friends.bungeeMode)
			return SQL_Manager.getStatus(this.player.getUniqueId().toString());
		return status.containsKey(this.player) ? status.get(this.player) : null;
	}
	
	public void setStatus(String s) {
		if(Friends.bungeeMode) {
			SQL_Manager.setStatus(this.player.getUniqueId().toString(), s);
			return;
		}
		status.put(this.player, s);
	}

	public LinkedList<Object> get(Integer i, boolean players) {
		if (Friends.bungeeMode) 
			return SQL_Manager.get(this.player.getUniqueId().toString(), values[i], players);

		LinkedList<Object> current = new LinkedList<>();
		if (PlayerUtilities.userdata.containsKey(this.player)) {
			LinkedList<LinkedList<Object>> hash = PlayerUtilities.userdata.get(this.player);
			if (hash.size() > i)
				current = PlayerUtilities.userdata.get(this.player).get(i);
		}
		return current;
	}

	public void update(String obj, Integer i, boolean add) {
		LinkedList<Object> current = this.get(i, false);

		if (current.contains(obj) && !add)
			current.remove(obj);
		if (!current.contains(obj) && add)
			current.add(obj);

		if (Friends.bungeeMode) {
			SQL_Manager.set(current, this.player.getUniqueId().toString(), values[i]);
			return;
		}

		LinkedList<LinkedList<Object>> hash = new LinkedList<>();
		if (PlayerUtilities.userdata.containsKey(this.player))
			hash = PlayerUtilities.userdata.get(this.player);
		if (hash.size() <= i) {
			while (hash.size() <= i) {
				hash.add(new LinkedList<Object>());
			}
		}
		hash.set(i, current);
		PlayerUtilities.userdata.put(this.player, hash);
	}

	public void toggleOption(String option) {
		LinkedList<Object> currentOptions = this.get(3, false);
		if (currentOptions.contains(option)) {
			this.update(option, 3, false);
			return;
		}
		this.update(option, 3, true);
		return;
	}

	public void setLastOnline(Long timestamp) {
		if (sql)
			SQL_Manager.setLastOnline(this.player.getUniqueId().toString(), System.currentTimeMillis());
		else
			this.mgr.save(file, cfg, "Players." + this.player.getUniqueId().toString() + ".LastOnline", System.currentTimeMillis());
	}

	public Long getLastOnline() {
		if(Friends.bungeeMode)
			return Long.valueOf(String.valueOf(BungeeSQL_Manager.get(player, "LASTONLINE")));
		return sql ? SQL_Manager.getLastOnline(this.player.getUniqueId().toString()) 
				: (this.cfg.getString("Players." + this.player.getUniqueId().toString() + ".LastOnline") != null)
				? this.cfg.getLong("Players." + this.player.getUniqueId().toString() + ".LastOnline") : (long) 0;
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
				SQL_Manager.set(this.get(i, false), this.player.getUniqueId().toString(), values[i]);
			if(status.containsKey(this.player))
				SQL_Manager.setStatus(this.player.getUniqueId().toString(), status.get(this.player));
			return;
		}
		if (PlayerUtilities.userdata.containsKey(this.player)) {
			for (int i = 0; i <= 3; i++) {
				LinkedList<Object> serialized = new LinkedList<>();
				if (PlayerUtilities.userdata.get(this.player).size() > i) {
					serialized = PlayerUtilities.userdata.get(this.player).get(i);
				}
				this.mgr.save(file, cfg, "Players." + this.player.getUniqueId().toString() + "." + values[i],
						serialized);
			}
			if(status.containsKey(this.player))
				this.mgr.save(file, cfg, "Players." + this.player.getUniqueId().toString() + ".Status", status.get(this.player));
		}
	}

	public void loadData() {
		if (Friends.bungeeMode)
			return;
		if (sql) {
			for (int i = 0; i <= 3; i++) {
				LinkedList<LinkedList<Object>> hash = new LinkedList<>();
				if (PlayerUtilities.userdata.containsKey(this.player))
					hash = PlayerUtilities.userdata.get(this.player);
				if (hash.size() <= i) {
					while (hash.size() <= i) {
						hash.add(new LinkedList<Object>());
					}
				}
				hash.set(i, SQL_Manager.get(this.player.getUniqueId().toString(), values[i], false));
				PlayerUtilities.userdata.put(this.player, hash);
			}
			if(SQL_Manager.getStatus(this.player.getUniqueId().toString()) != null)
				status.put(this.player, SQL_Manager.getStatus(this.player.getUniqueId().toString()));
			return;
		}

		for (int i = 0; i <= 3; i++) {
			if (this.cfg.getString("Players." + this.player.getUniqueId().toString() + "." + values[i]) != null) {
				LinkedList<Object> current = new LinkedList<>();
				if (PlayerUtilities.userdata.containsKey(this.player)) {
					LinkedList<LinkedList<Object>> hash = PlayerUtilities.userdata.get(this.player);
					if (hash.size() > i)
						current = PlayerUtilities.userdata.get(this.player).get(i);
				}
				for (String uuid : this.cfg
						.getStringList("Players." + this.player.getUniqueId().toString() + "." + values[i])) {
					if (!current.contains(uuid))
						current.add(uuid);
				}

				LinkedList<LinkedList<Object>> hash = new LinkedList<>();
				if (PlayerUtilities.userdata.containsKey(this.player))
					hash = PlayerUtilities.userdata.get(this.player);
				if (hash.size() <= i) {
					while (hash.size() <= i) {
						hash.add(new LinkedList<Object>());
					}
				}
				hash.set(i, current);

				PlayerUtilities.userdata.put(this.player, hash);
			}
		}
		if(this.cfg.getString("Players." + this.player.getUniqueId().toString() + ".Status") != null)
			status.put(this.player, this.cfg.getString("Players." + this.player.getUniqueId().toString() + ".Status"));
	}

}
