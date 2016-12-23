/*
 *
 * This class was made by HyChrod
 * All rights reserved, 2017
 *
 */
package de.HyChrod.Friends.Commands.BungeeSubCommands;

import java.sql.ResultSet;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.FriendCommands;
import de.HyChrod.Friends.Listeners.BungeeMessagingListener;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.SQL.QueryRunnable;
import de.HyChrod.Friends.SQL.UpdateRunnable;

public class Remove_Command {
	
	public Remove_Command(Friends plugin, Player performer, OfflinePlayer toRemove) {
		new QueryRunnable("SELECT * FROM friends2_0 WHERE UUID= '" + performer.getUniqueId().toString() + "'", new Callback<ResultSet>() {
			
			@Override
			public void call(ResultSet rs) {
				try {
					if(rs.next() && (rs.getString("FRIENDS") != null)) {
						String serialized = rs.getString("FRIENDS");
						if(serialized.contains(toRemove.getUniqueId().toString())) {
							String replaced = serialized.replace(toRemove.getUniqueId().toString() + "//;", "");
							new UpdateRunnable("UPDATE friends2_0 SET "
									+ "FRIENDS='" + replaced + "' WHERE "
											+ "UUID='" + performer.getUniqueId().toString() + "'", null).runTaskAsynchronously(plugin);
							
							new QueryRunnable("SELECT * FROM friends2_0 WHERE UUID='" + toRemove.getUniqueId().toString() + "'", new Callback<ResultSet>() {

								@Override
								public void call(ResultSet rs2) {
									try {
										if(rs2.next() && (rs2.getString("FRIENDS") != null)) {
											String serialized2 = rs2.getString("FRIENDS");											
											if(serialized2.contains(performer.getUniqueId().toString())) {
												new UpdateRunnable("UPDATE friends2_0 SET "
														+ "FRIENDS='" + serialized2.replace(performer.getUniqueId().toString() + "//;", "") + "' WHERE "
																+ "UUID='" + toRemove.getUniqueId().toString() + "'", null).runTaskAsynchronously(plugin);
											}
										}
									} catch (Exception ex) {ex.printStackTrace();}
								}
							}).runTaskAsynchronously(plugin);
							new BukkitRunnable() {
								
								@Override
								public void run() {
									performer.updateInventory();
									performer.sendMessage(plugin.getString("Messages.Commands.Remove.Remove.Remover").replace("%PLAYER%",
											toRemove.getName()));
									if (BungeeMessagingListener.isOnline(toRemove)) {
										FriendCommands.sendMessage(performer, toRemove.getName(),
												plugin.getString("Messages.Commands.Remove.Remove.ToRemove").replace("%PLAYER%",
														performer.getName()));
									}
								}
							}.runTask(plugin);
							return;
						}
					}
					performer.sendMessage(plugin.getString("Messages.Commands.Remove.NoFriends"));
					return;
				} catch (Exception ex) {ex.printStackTrace();}
			}
		}).runTaskAsynchronously(plugin);
	}
	
}
