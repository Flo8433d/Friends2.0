/*
 *
 * This class was made by HyChrod
 * All rights reserved, 2017
 *
 */
package de.HyChrod.Friends.Utilities;

import java.io.File;
import java.sql.ResultSet;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.SQL.MySQL;
import de.HyChrod.Friends.SQL.QueryRunnable;

public class FriendPlayer {

	private static HashMap<String, FriendPlayer> FRIENDPLAYER = new HashMap<>();
	
	private String uuid;
	
	private String server = null;
	private String status = null;
	private Long lastonline = -1L;
	public boolean isFinshed = false;
	
	private FileConfiguration cfg;
	private File file;
	private String path;
	
	public FriendPlayer(String uuid) {
		this.uuid = uuid;
		
		FRIENDPLAYER.put(uuid, this);
		if(!MySQL.isConnected()) {
			file = FileManager.getFile("/Util", "Userdata.dat");
			cfg = FileManager.getConfig(file);
			path = "Players." + uuid;
		}	
		fetch();
	}
	
	public String getUUID() {
		return uuid;
	}
	
	public String getStatus() {
		return status != null ? status : null;
	}
	
	public void setStatus(String s) {
		status = s;
	}
	
	public String getServer() {
		return server;
	}
	
	public boolean hasOption(String option) {
		return option.contains(option);
	}
	
	public Long getLastonline() {
		return lastonline;
	}
	
	public void setLastonline(Long l) {
		lastonline = l;
	}
	
	public int[] getLastOnline() {
		int[] timestamps = new int[4];

		Double timeMillies = (double) System.currentTimeMillis() - (double) getLastonline();
		int seconds = (int) (timeMillies / 1000);
		int minutes = 0;
		int hours = 0;
		int days = 0;

		if (getLastonline() != 0) {
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
	
	private void fetch() {
		if(MySQL.isConnected()) {
			if(Friends.bungeemode) {
				new QueryRunnable("SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + uuid + "'", new Callback<ResultSet>() {

					@Override
					public void call(ResultSet result) {
						try {
							if(result.next()) {
								if(result.getString("LASTONLINE") != null) {
									Long LAST = result.getLong("LASTONLINE");
									lastonline = LAST != null ? LAST : lastonline;
								}
							}
						} catch (Exception ex) {ex.printStackTrace();}
					}
				}).runTaskAsynchronously(Friends.getInstance());
			}
			new QueryRunnable("SELECT * FROM friends2_0 WHERE UUID= '" + uuid + "'", new Callback<ResultSet>() {
				
				@Override
				public void call(ResultSet result) {
					try {
						if(result.next()) {
							if(!Friends.bungeemode) {
								if(result.getString("LASTONLINE") != null) {
									Long LAST = result.getLong("LASTONLINE");
									lastonline = LAST != null ? LAST : lastonline;
								}
							}
							if(result.getString("STATUS") != null) {
								String STATUS = result.getString("STATUS");
								status = STATUS != null ? !STATUS.equalsIgnoreCase("") ? (STATUS.length() > 0) ? STATUS : null : null : null;
							}
						}
					} catch (Exception ex) {ex.printStackTrace();}
					isFinshed = true;
				}
			}).runTaskAsynchronously(Friends.getInstance());
			return;
		}
		
		if(cfg.getString(path) != null) {
			if(cfg.getString(path + ".LastOnline") != null)
				lastonline = cfg.getLong(path + ".LastOnline");
			if(cfg.getString(path + ".Status") != null)
				status = cfg.getString(path + ".Status");
		}
		return;
	}
	
	public static FriendPlayer getPlayer(String uuid) {
		return FRIENDPLAYER.containsKey(uuid) ? FRIENDPLAYER.get(uuid) : new FriendPlayer(uuid);
	}
}
