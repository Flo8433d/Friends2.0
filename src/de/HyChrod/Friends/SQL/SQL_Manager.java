package de.HyChrod.Friends.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Util.Callback;
import de.HyChrod.Friends.Util.UpdateBukkitRunnable;

@SuppressWarnings({"unchecked","rawtypes"})
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

	public static LinkedList<OfflinePlayer> getFriends(OfflinePlayer player) {
		final LinkedList<OfflinePlayer> friends = new LinkedList();
		if (playerExists(player).booleanValue()) {
			try {
				Connection con = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					con = MySQL.pool.borrowConnection();
					ps = con.prepareStatement("SELECT * FROM friends2_0 WHERE UUID= '" + player.getUniqueId().toString() + "';");
					rs = ps.executeQuery();
					
					if ((rs.next()) && (String.valueOf(rs.getString("FRIENDS")) == null)) {
					}
					String[] uuids = rs.getString("FRIENDS").split("//;");
					for (int i = 0; i < uuids.length; i++) {
						if (uuids[i].length() > 20) {
							friends.add(Bukkit.getOfflinePlayer(UUID.fromString(uuids[i])));
						}
					}
				} catch (Exception ex) {
				} finally {
					if(rs != null) rs.close();
					if(ps != null) ps.close();
					if(con != null) con.close();
				}
			} catch (Exception ex) {}
		}
		return friends;
	}

	public static Long getLastOnline(OfflinePlayer player) {
		Long timeStamp = Long.valueOf(0L);
		if (playerExists(player).booleanValue()) {
			try {
				Connection con = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					con = MySQL.pool.borrowConnection();
					ps = con.prepareStatement("SELECT * FROM friends2_0 WHERE UUID= '" + player.getUniqueId().toString() + "';");
					rs = ps.executeQuery();
					
					if ((rs.next()) && (String.valueOf(rs.getString("LASTONLINE")) == null)) {
					}
					timeStamp = Long.valueOf(Long.parseLong(rs.getString("LASTONLINE")));
				} catch (Exception ex) {
				} finally {
					if(rs != null) rs.close();
					if(ps != null) ps.close();
					if(con != null) con.close();
				}
			} catch (Exception ex) {}
		}
		return timeStamp;
	}

	public static void setLastOnline(OfflinePlayer player, Long timeStamp, boolean sync) {
		if (playerExists(player).booleanValue()) {
			if(!sync) {
				new UpdateBukkitRunnable(MySQL.pool, "UPDATE friends2_0 SET LASTONLINE='" + timeStamp.toString() + "' WHERE UUID='"
					+ player.getUniqueId().toString() + "';", new Callback<Integer, SQLException>() {

						@Override
						public void call(Integer result, SQLException thrown) {
						}
					
				}).runTaskAsynchronously(Friends.getInstance());
				return;
			}
			MySQL.update("UPDATE friends2_0 SET LASTONLINE='" + timeStamp.toString() + "' WHERE UUID='"
					+ player.getUniqueId().toString() + "';");
			return;
		}
		createPlayer(player);
		setLastOnline(player, timeStamp, sync);
	}

	public static LinkedList<OfflinePlayer> getRequests(OfflinePlayer player) {
		LinkedList<OfflinePlayer> friends = new LinkedList();
		if (playerExists(player).booleanValue()) {
			try {
				Connection con = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					con = MySQL.pool.borrowConnection();
					ps = con.prepareStatement("SELECT * FROM friends2_0 WHERE UUID= '" + player.getUniqueId().toString() + "';");
					rs = ps.executeQuery();
					
					if ((rs.next()) && (String.valueOf(rs.getString("REQUESTS")) == null)) {
					}
					String[] uuids = rs.getString("REQUESTS").split("//;");
					for (int i = 0; i < uuids.length; i++) {
						if (uuids[i].length() > 20) {
							friends.add(Bukkit.getOfflinePlayer(UUID.fromString(uuids[i])));
						}
					}
				} catch (Exception ex) {
				} finally {
					if(rs != null) rs.close();
					if(ps != null) ps.close();
					if(con != null) con.close();
				}
			} catch (Exception ex) {}
		}
		return friends;
	}

	public static LinkedList<OfflinePlayer> getBlocked(OfflinePlayer player) {
		LinkedList<OfflinePlayer> friends = new LinkedList();
		if (playerExists(player).booleanValue()) {
			try {
				Connection con = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					con = MySQL.pool.borrowConnection();
					ps = con.prepareStatement("SELECT * FROM friends2_0 WHERE UUID= '" + player.getUniqueId().toString() + "';");
					rs = ps.executeQuery();
					
					if ((rs.next()) && (String.valueOf(rs.getString("BLOCKED")) == null)) {
					}
					String[] uuids = rs.getString("BLOCKED").split("//;");
					for (int i = 0; i < uuids.length; i++) {
						if (uuids[i].length() > 20) {
							friends.add(Bukkit.getOfflinePlayer(UUID.fromString(uuids[i])));
						}
					}
				} catch (Exception ex) {
				} finally {
					if(rs != null) rs.close();
					if(ps != null) ps.close();
					if(con != null) con.close();
				}
			} catch (Exception ex) {}
		}
		return friends;
	}

	public static void setBlocked(List<OfflinePlayer> currentBlocked, OfflinePlayer player, boolean sync) {
		if (playerExists(player).booleanValue()) {
			String serializedBlocked = "";
			for (OfflinePlayer blocked : currentBlocked) {
				serializedBlocked = serializedBlocked + blocked.getUniqueId().toString() + "//;";
			}
			if(!sync) {
				new UpdateBukkitRunnable(MySQL.pool, "UPDATE friends2_0 SET BLOCKED='" + serializedBlocked + "' WHERE UUID='"
					+ player.getUniqueId().toString() + "';", new Callback<Integer, SQLException>() {

						@Override
						public void call(Integer result, SQLException thrown) {
						}
					
				}).runTaskAsynchronously(Friends.getInstance());
				return;
			}
			MySQL.update("UPDATE friends2_0 SET BLOCKED='" + serializedBlocked + "' WHERE UUID='"
					+ player.getUniqueId().toString() + "';");
		} else {
			createPlayer(player);
			setBlocked(currentBlocked, player, sync);
		}
	}

	public static void setRequests(List<OfflinePlayer> currentRequests, OfflinePlayer player, boolean sync) {
		if (playerExists(player).booleanValue()) {
			String serializedRequests = "";
			for (OfflinePlayer requests : currentRequests) {
				serializedRequests = serializedRequests + requests.getUniqueId().toString() + "//;";
			}
			if(!sync) {
				new UpdateBukkitRunnable(MySQL.pool, "UPDATE friends2_0 SET REQUESTS='" + serializedRequests + "' WHERE UUID='"
					+ player.getUniqueId().toString() + "';", new Callback<Integer, SQLException>() {

						@Override
						public void call(Integer result, SQLException thrown) {
						}
					
				}).runTaskAsynchronously(Friends.getInstance());
				return;
			}
			MySQL.update("UPDATE friends2_0 SET REQUESTS='" + serializedRequests + "' WHERE UUID='"
					+ player.getUniqueId().toString() + "';");
		} else {
			createPlayer(player);
			setRequests(currentRequests, player, sync);
		}
	}

	public static void setFriends(List<OfflinePlayer> currentFriends, OfflinePlayer player, boolean sync) {
		if (playerExists(player).booleanValue()) {
			String serializedFriends = "";
			for (OfflinePlayer friends : currentFriends) {
				serializedFriends = serializedFriends + friends.getUniqueId().toString() + "//;";
			}
			if(!sync) {
				new UpdateBukkitRunnable(MySQL.pool, "UPDATE friends2_0 SET FRIENDS='" + serializedFriends + "' WHERE UUID='"
						+ player.getUniqueId().toString() + "';", new Callback<Integer, SQLException>() {

							@Override
							public void call(Integer result, SQLException thrown) {
							}
					
				}).runTaskAsynchronously(Friends.getInstance());
				return;
			}
			MySQL.update("UPDATE friends2_0 SET FRIENDS='" + serializedFriends + "' WHERE UUID='"
					+ player.getUniqueId().toString() + "';");
		} else {
			createPlayer(player);
			setFriends(currentFriends, player, sync);
		}
	}
	
	public static void setOptions(OfflinePlayer player, List<String> options, boolean sync) {
		if (playerExists(player).booleanValue()) {
			String serializedOptions = "";
			for (String option : options) {
				if (option.length() > 5) {
					serializedOptions = serializedOptions + "//;" + option;
				}
			}
			if(!sync) {
				new UpdateBukkitRunnable(MySQL.pool, "UPDATE friends2_0 SET OPTIONS='" + serializedOptions + "' WHERE UUID='"
					+ player.getUniqueId().toString() + "';", new Callback<Integer, SQLException>() {

						@Override
						public void call(Integer result, SQLException thrown) {
						}
					
				}).runTaskAsynchronously(Friends.getInstance());
				return;
			}
			MySQL.update("UPDATE friends2_0 SET OPTIONS='" + serializedOptions + "' WHERE UUID='"
					+ player.getUniqueId().toString() + "';");
		} else {
			createPlayer(player);
			setOptions(player, options, sync);
		}
	}

	public static LinkedList<String> getOptions(OfflinePlayer player) {
		LinkedList<String> options = new LinkedList();
		if (playerExists(player).booleanValue()) {
			try {
				Connection con = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					con = MySQL.pool.borrowConnection();
					ps = con.prepareStatement("SELECT * FROM friends2_0 WHERE UUID= '" + player.getUniqueId().toString() + "';");
					rs = ps.executeQuery();
					
					if ((rs.next()) && (String.valueOf(rs.getString("OPTIONS")) == null)) {
					}
					String[] option = rs.getString("OPTIONS").split("//;");
					for (int i = 0; i < option.length; i++) {
						options.add(option[i]);
					}
				} catch (Exception ex) {
				} finally {
					if(rs != null) rs.close();
					if(ps != null) ps.close();
					if(con != null) con.close();
				}
			} catch (Exception ex) {}
		}
		return options;
	}
}
