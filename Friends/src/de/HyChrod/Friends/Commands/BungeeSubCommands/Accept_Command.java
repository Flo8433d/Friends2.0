/*
 *
 * This class was made by HyChrod
 * All rights reserved, 2017
 *
 */
package de.HyChrod.Friends.Commands.BungeeSubCommands;

import java.sql.ResultSet;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.FriendCommands;
import de.HyChrod.Friends.Listeners.BungeeMessagingListener;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.SQL.QueryRunnable;
import de.HyChrod.Friends.SQL.UpdateRunnable;

public class Accept_Command {
	
	public Accept_Command(Friends plugin, Player performer, OfflinePlayer toAccept) {
		new QueryRunnable("SELECT * FROM friends2_0 WHERE UUID= '" + performer.getUniqueId().toString() + "'", new Callback<ResultSet>() {
			
			@Override
			public void call(ResultSet rsSELECT_P) {
				try {		
					if(rsSELECT_P.next()) {	
						if(rsSELECT_P.getString("REQUESTS") != null) {
							String serializedRequests = rsSELECT_P.getString("REQUESTS");
							String serializedFriends = rsSELECT_P.getString("FRIENDS");
							if(serializedRequests.contains(toAccept.getUniqueId().toString())) {
								int counter_P = rsSELECT_P.getString("FRIENDS").split("//;").length;
								if (counter_P > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit")) {
									if (!performer.hasPermission("Friends.ExtraFriends") || 
											counter_P > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
										performer.sendMessage(plugin.getString("Messages.Commands.Accept.LimitReached.Accepter"));
										return;
									}
								}
								new QueryRunnable("SELECT * FROM friends2_0 WHERE UUID= '" + toAccept.getUniqueId().toString() + "'", new Callback<ResultSet>() {

									@Override
									public void call(ResultSet rsSELECT_T) {
										try {
											if(rsSELECT_T.next() && (rsSELECT_T.getString("FRIENDS") != null)) {
												String serializedFriends_T = rsSELECT_T.getString("FRIENDS");
												int counter_T = serializedFriends_T.split("//;").length;
												if (counter_T > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit"))
													if(toAccept.isOnline()) {
														Player toCheck = Bukkit.getPlayer(toAccept.getName());
														if (!toCheck.hasPermission("Friends.ExtraFriends") || 
																counter_T > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
															performer.sendMessage(plugin.getString("Messages.Commands.Accept.LimitReached.Requester"));
															return;
														}
													}
												
												serializedFriends_T = serializedFriends_T + performer.getUniqueId().toString() + "//;";
												new UpdateRunnable("UPDATE friends2_0 SET "
														+ "FRIENDS= '" + serializedFriends_T + "' WHERE "
																+ "UUID= '" + toAccept.getUniqueId().toString() + "'", null).runTaskAsynchronously(plugin);
											}
											new UpdateRunnable("UPDATE friends2_0 SET "
													+ "REQUESTS= '" + serializedRequests.replace(toAccept.getUniqueId().toString() + "//;", "") + "' WHERE "
															+ "UUID= '" + performer.getUniqueId().toString() + "'", null).runTaskAsynchronously(plugin);
											
											String friends = serializedFriends + toAccept.getUniqueId().toString() + "//;";
											new UpdateRunnable("UPDATE friends2_0 SET "
													+ "FRIENDS= '" + friends + "' WHERE "
															+ "UUID= '" + performer.getUniqueId().toString() + "'", null).runTaskAsynchronously(plugin);
											new BukkitRunnable() {
												
												@Override
												public void run() {
													performer.updateInventory();
													performer.sendMessage(plugin.getString("Messages.Commands.Accept.Accept.Accepter").replace("%PLAYER%",
															toAccept.getName()));
													if (BungeeMessagingListener.isOnline(toAccept)) {
														FriendCommands.sendMessage(performer, toAccept.getName(), 
																plugin.getString("Messages.Commands.Accept.Accept.ToAccept")
																.replace("%PLAYER%", performer.getName()));
													}
												}
											}.runTask(plugin);
										} catch (Exception ex) {ex.printStackTrace();}
										return;
									}
								}).runTaskAsynchronously(plugin);
								return;
							}
						}
					}
				} catch (Exception ex) {ex.printStackTrace();}
				performer.sendMessage(plugin.getString("Messages.Commands.Accept.NoRequest"));
				return;
			};
		}).runTaskAsynchronously(plugin);
	}

}
