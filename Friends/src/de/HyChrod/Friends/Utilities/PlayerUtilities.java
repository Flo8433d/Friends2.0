/*
 * 
 * This class was made by HyChrod
 * All rights reserved, 2017
 *
 */
package de.HyChrod.Friends.Utilities;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.Listeners.BungeeMessagingListener;
import de.HyChrod.Friends.Listeners.BungeeSpreadEvent;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.SQL.MySQL;
import de.HyChrod.Friends.SQL.QueryRunnable;
import de.HyChrod.Friends.SQL.UpdateRunnable;

public class PlayerUtilities {
	
	private static HashMap<String, PlayerUtilities> UTILITIES = new HashMap<>();
	
	public String uuid;
	
	public List<FriendPlayer> FRIENDS = Collections.synchronizedList(new ArrayList<>());
	public List<FriendPlayer> REQUESTS = Collections.synchronizedList(new ArrayList<>());
	public List<FriendPlayer> BLOCKED = Collections.synchronizedList(new ArrayList<>());
	public List<String> OPTIONS = Collections.synchronizedList(new ArrayList<>());
	public String STATUS = null;
	public boolean isFinished = false;
	
	private FileConfiguration cfg;
	private File file;
	private String path;
	
	public PlayerUtilities(String uuid) {
		UTILITIES.put(uuid, this);
		this.uuid = uuid;
		
		if(MySQL.isConnected()) {
			new QueryRunnable("SELECT * FROM friends2_0 WHERE UUID= '" + uuid + "'", new Callback<ResultSet>() {
				
				@Override
				public void call(ResultSet result) {
					try {
						if(result.next()) {
							if(result.getString("FRIENDS") != null) {
								String serializedFriends = result.getString("FRIENDS");
								for(String UUIDS : serializedFriends.split("//;")) {
									if(UUIDS != null && !UUIDS.equalsIgnoreCase("") && (UUIDS.length() > 5))
										FRIENDS.add(FriendPlayer.getPlayer(UUIDS));
								}
							}
							if(result.getString("REQUESTS") != null) {
								String serializedFriends = result.getString("REQUESTS");
								for(String UUIDS : serializedFriends.split("//;")) {
									if(UUIDS != null && !UUIDS.equalsIgnoreCase("") && (UUIDS.length() > 5))
										REQUESTS.add(FriendPlayer.getPlayer(UUIDS));
								}
							}
							if(result.getString("BLOCKED") != null) {
								String serializedFriends = result.getString("BLOCKED");
								for(String UUIDS : serializedFriends.split("//;")) {
									if(UUIDS != null && !UUIDS.equalsIgnoreCase("") && (UUIDS.length() > 5))
										BLOCKED.add(FriendPlayer.getPlayer(UUIDS));
								}
							}
							if(result.getString("STATUS") != null) {
								String stat = result.getString("STATUS");
								STATUS = stat != null ? !stat.equalsIgnoreCase("") ? (stat.length() > 0) ? stat : null : null : null;
							}
							if(result.getString("OPTIONS") != null) {
								String options = result.getString("OPTIONS");
								for(String op : options.split("//;"))
									if(op != null && !op.equalsIgnoreCase("") && (op.length() > 5))
										OPTIONS.add(op);
							}
						}
					} catch (Exception ex) {ex.printStackTrace();}
					isFinished = true;
				}
			}).runTaskAsynchronously(Friends.getInstance());
			return;
		}
		file = FileManager.getFile("/Util", "Userdata.dat");
		cfg = FileManager.getConfig(file);
		path = "Players." + uuid;
		
		if(cfg.getString(path) != null) {
			if(cfg.getString(path + ".Status") != null)
				STATUS = cfg.getString(path + ".Status");
			if(cfg.getString(path + ".OPTIONS") != null) {
				for(String op : cfg.getStringList(path + ".OPTIONS"))
					OPTIONS.add(op);
			}
			if(cfg.getString(path + ".FRIENDS") != null) {
				for(String uuids : cfg.getStringList(path + ".FRIENDS"))
					FRIENDS.add(FriendPlayer.getPlayer(uuids));
			}
			if(cfg.getString(path + ".REQUESTS") != null) {
				for(String uuids : cfg.getStringList(path + ".REQUESTS"))
					REQUESTS.add(FriendPlayer.getPlayer(uuids));
			}
			if(cfg.getString(path + ".BLOCKED") != null) {
				for(String uuids : cfg.getStringList(path + ".BLOCKED"))
					BLOCKED.add(FriendPlayer.getPlayer(uuids));
			}
		}
	}
	
	public void addFriend(String uuid) {
		List<FriendPlayer> FF = Collections.synchronizedList(new ArrayList<>(getFriends()));
		for(FriendPlayer fp : FF)
			if(fp.getUUID().equals(uuid))
				return;
		FRIENDS.add(FriendPlayer.getPlayer(uuid));
		flush("FRIENDS", serializeList(FRIENDS), true);
		return;
	}
	
	public void removeFriend(String uuid) {
		List<FriendPlayer> FF = Collections.synchronizedList(new ArrayList<>(getFriends()));
		for(FriendPlayer fp : FF)
			if(fp.getUUID().equals(uuid))
				FRIENDS.remove(fp);
		flush("FRIENDS", serializeList(FRIENDS), true);
		return;
	}
	
	public  void addRequest(String uuid) {
		List<FriendPlayer> RR = Collections.synchronizedList(new ArrayList<>(getRequests()));
		for(FriendPlayer fp : RR)
			if(fp.getUUID().equals(uuid))
				return;
		REQUESTS.add(FriendPlayer.getPlayer(uuid));
		flush("REQUESTS", serializeList(REQUESTS), true);
		return;
	}
	
	public void removeRequest(String uuid) {
		List<FriendPlayer> RR = Collections.synchronizedList(new ArrayList<>(getRequests()));
		for(FriendPlayer fp : RR)
			if(fp.getUUID().equals(uuid))
				REQUESTS.remove(fp);
		flush("REQUESTS", serializeList(REQUESTS), true);
		return;
	}
	
	public void addBlocked(String uuid) {
		List<FriendPlayer> BB = Collections.synchronizedList(new ArrayList<>(getBlocked()));
		for(FriendPlayer fp : BB)
			if(fp.getUUID().equals(uuid))
				return;
		BLOCKED.add(FriendPlayer.getPlayer(uuid));
		flush("BLOCKED", serializeList(BLOCKED), true);
		return;
	}
	
	public void removeBlocked(String uuid) {
		List<FriendPlayer> BB = Collections.synchronizedList(new ArrayList<>(getBlocked()));
		for(FriendPlayer fp : BB)
			if(fp.getUUID().equals(uuid))
				BLOCKED.remove(fp);
		flush("BLOCKED", serializeList(BLOCKED), true);
		return;
	}
	
	public void addOption(String option) {
		if(!OPTIONS.contains(option))
			OPTIONS.add(option);
		flush("OPTIONS", serializeOptions(OPTIONS), true);
	}
	
	public void removeOption(String option) {
		if(OPTIONS.contains(option))
			OPTIONS.remove(option);
		flush("OPTIONS", serializeOptions(OPTIONS), true);
	}
	
	public void toggleOption(String option) {
		if(hasOption(option)) {
			removeOption(option);
			flush("OPTIONS", serializeOptions(OPTIONS), true);
			return;
		}
		addOption(option);
	}
	
	public  void setStatus(String s) {
		STATUS = ChatColor.translateAlternateColorCodes('&', s);
		flush("STATUS", s, false);
	}
	
	public List<FriendPlayer> getFriends() {
		return FRIENDS;
	}
	
	public List<FriendPlayer> getRequests() {
		return REQUESTS;
	}
	
	public List<FriendPlayer> getBlocked() {
		return BLOCKED;
	}
	
	public List<String> getOptions() {
		return OPTIONS;
	}
	
	public String getStatus() {
		return STATUS;
	}
	
	public boolean isFriend(String uuid) {
		for(FriendPlayer fp : getFriends())
			if(fp.getUUID().equals(uuid))
				return true;
		return false;
	}
	
	public boolean hasRequest(String uuid) {
		for(FriendPlayer fp : getRequests())
			if(fp.getUUID().equals(uuid))
				return true;
		return false;
	}
	
	public boolean isBlocked(String uuid) {
		for(FriendPlayer fp : getBlocked())
			if(fp.getUUID().equals(uuid))
				return true;
		return false;
	}
	
	public boolean hasOption(String option) {
		return OPTIONS.contains(option);
	}
	
	public static PlayerUtilities getUtilities(String uuid) {
		return UTILITIES.containsKey(uuid) ? UTILITIES.get(uuid) : new PlayerUtilities(uuid);
	}
	
	public void flushLastOnline() {
		String db = Friends.bungeemode ? "friends2_0_BUNGEE" : "friends2_0";
		String values = Friends.bungeemode ? "UUID, ONLINE, SERVER, LASTONLINE" : "UUID, FRIENDS, BLOCKED, REQUESTS, OPTIONS, LASTONLINE, STATUS";
		String v_s = Friends.bungeemode ? "'" + uuid + "', '', '', '" + System.currentTimeMillis() + "'" : "'" + uuid + "', '', '', '', '', '" + System.currentTimeMillis() + "',''";
		
		FriendPlayer FP = FriendPlayer.getPlayer(uuid);
		FP.setLastonline(System.currentTimeMillis());
		String data = uuid + "@" + System.currentTimeMillis();
		for(FriendPlayer fp : getFriends()) {
			OfflinePlayer pp = Bukkit.getOfflinePlayer(UUID.fromString(fp.getUUID()));
			BungeeMessagingListener.isOnline(pp, new Callback<Boolean>() {

				@Override
				public void call(Boolean done) {
					if(done) {
						Bukkit.getServer().getPluginManager().callEvent(
								new BungeeSpreadEvent(Bukkit.getPlayer(UUID.fromString(uuid)), pp.getName(), "UpdateLastonline", data));
					}
				}
			});
		}
		
		new QueryRunnable("SELECT * FROM " + db + " WHERE UUID= '" + uuid + "'", new Callback<ResultSet>() {

			@Override
			public void call(ResultSet rs) {
				try {
					if(rs.next()) {
						new UpdateRunnable("UPDATE " + db + " SET LASTONLINE= '" + System.currentTimeMillis() + "' WHERE UUID= '" + uuid + "'", null)
						.runTaskAsynchronously(Friends.getInstance());
						return;
					}
					new UpdateRunnable("INSERT INTO " + db + "(" + values + ") VALUES (" + v_s + ")", null).runTaskAsynchronously(Friends.getInstance());
				} catch (Exception ex) {ex.printStackTrace();}
			}
		}).runTaskAsynchronously(Friends.getInstance());
	}
	
	private void flush(String value, String objects, boolean serialize) {
		if(MySQL.isConnected()) {
			new QueryRunnable("SELECT * FROM friends2_0 WHERE UUID= '" + uuid + "'", new Callback<ResultSet>() {

				@Override
				public void call(ResultSet rs) {
					try {
						if(rs.next()) {
							new UpdateRunnable("UPDATE friends2_0 SET " + value + "= '" + objects + "' WHERE UUID= '" + uuid + "'", null)
							.runTaskAsynchronously(Friends.getInstance());
							return;
						}
						new UpdateRunnable("INSERT INTO friends2_0(UUID, NAME, FRIENDS, BLOCKED, REQUESTS, OPTIONS, LASTONLINE, STATUS) "
								+ "VALUES ('" + uuid + "','" + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + "','','','','','','')", new Callback<Integer>() {

									@Override
									public void call(Integer done) {
										Bukkit.broadcastMessage(value+"5");
										flush(value, objects, serialize);
									} 
								})
						.runTaskAsynchronously(Friends.getInstance());
					} catch (Exception ex) {ex.printStackTrace();}
				}
			}).runTaskAsynchronously(Friends.getInstance());
			return;
		}
		Object toSave = serialize ? deserializeString(objects) : objects;
		FileManager.save(file, cfg, "Players." + uuid + "." + value, toSave);
	}
	
	private String serializeList(List<FriendPlayer> ll) {
		String STRING = "";
		ArrayList<FriendPlayer> SR = new ArrayList<>(ll);
		for(FriendPlayer sub : SR)
			STRING = STRING + sub.getUUID() + "//;";
		return STRING.length() > 3 ? STRING.substring(0, STRING.length() - 3) : STRING;
	}
	
	private String serializeOptions(List<String> ll) {
		String STRING = "";
		for(String sub : ll)
			STRING = STRING + sub + "//;";
		return STRING.length() > 3 ? STRING.substring(0, STRING.length() - 3) : STRING;
	}
	
	private List<String> deserializeString(String s) {
		List<String> STRING = Collections.synchronizedList(new ArrayList<>());
		for(String sub : s.split("//;")) {
			if(sub != null && !sub.equals("") && sub.length() > 3)
				STRING.add(sub);
		}
		return STRING;
	}
	
}