/*
 *
 * This class was made by HyChrod
 * All rights reserved, 2017
 *
 */
package de.HyChrod.Friends.Commands.BungeeSubCommands;

import java.sql.ResultSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Listeners.BungeeMessagingListener;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.SQL.QueryRunnable;

public class List_Command {
	
	public List_Command(Friends plugin, Player performer) {
		new QueryRunnable("SELECT * FROM friends2_0 WHERE UUID= '" + performer.getUniqueId().toString() + "'", new Callback<ResultSet>() {
			
			@Override
			public void call(ResultSet rs) {
				try {
					if(rs.next() && (rs.getString("FRIENDS") != null)) {
						String[] serialized = rs.getString("FRIENDS").split("//;");
						String online = "";
						String offline = "";
						for (String uuid : serialized) {
							if(!uuid.equalsIgnoreCase("null")) {
								OfflinePlayer friend = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
								if(friend.getName() != null && !friend.getName().equalsIgnoreCase("null")) {
									if (BungeeMessagingListener.isOnline(friend))
										online = online + friend.getName() + ", ";
									if (!BungeeMessagingListener.isOnline(friend))
										offline = offline + friend.getName() + ", ";
								}
							}
						}
						performer.sendMessage(plugin.getString("Messages.Commands.List")
								.replace("%ONLINE_COUNT%", String.valueOf(online.split(",").length - 1))
								.replace("%OFFLINE_COUNT%", String.valueOf(offline.split(",").length - 1))
								.replace("%ONLINE%", online).replace("%OFFLINE%", offline));
					}
				} catch (Exception ex) {ex.printStackTrace();}
			}
		}).runTaskAsynchronously(plugin);
	}

}
