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

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Listeners.BungeeMessagingListener;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.SQL.QueryRunnable;
import de.HyChrod.Friends.SQL.UpdateRunnable;

public class Block_Command {
	
	public Block_Command(Friends plugin, Player performer, OfflinePlayer toBlock) {
		new QueryRunnable("SELECT * FROM friends2_0 WHERE UUID= '" + performer.getUniqueId().toString() + "'", new Callback<ResultSet>() {
			
			@Override
			public void call(ResultSet rsSELECT_P) {
				try {
					if(rsSELECT_P.next() && (rsSELECT_P.getString("BLOCKED") != null)) {
						String serialized = rsSELECT_P.getString("BLOCKED");
						if(serialized.contains(toBlock.getUniqueId().toString())) {
							performer.sendMessage(plugin.getString("Messages.Commands.Block.AlreadyBlocked"));
							return;
						}
						
						String requests = rsSELECT_P.getString("REQUESTS") != null ? rsSELECT_P.getString("REQUESTS") : null;
						String friends = rsSELECT_P.getString("FRIENDS") != null ? rsSELECT_P.getString("FRIENDS") : null;
						String newBlocked = serialized + toBlock.getUniqueId().toString() + "//;";
						new UpdateRunnable("UPDATE friends2_0 SET BLOCKED= '" + newBlocked + "' WHERE "
								+ "UUID= '" + performer.getUniqueId().toString() + "'", null).runTaskAsynchronously(plugin);
						if(requests != null && requests.contains(toBlock.getUniqueId().toString())) {
							new UpdateRunnable("UPDATE friends2_0 SET "
									+ "REQUESTS= '" + requests.replace(toBlock.getUniqueId().toString() + "//;", "") + "' WHERE "
											+ "UUID= '" + performer.getUniqueId().toString() + "'", null).runTaskAsynchronously(plugin);
						}
						if(friends != null && friends.contains(toBlock.getUniqueId().toString())) {
							new UpdateRunnable("UPDATE friends2_0 SET "
									+ "FRIENDS= '" + friends.replace(toBlock.getUniqueId().toString() + "//;", "") + "' WHERE "
											+ "UUID= '" + performer.getUniqueId().toString() + "'", null).runTaskAsynchronously(plugin);
						}
						new QueryRunnable("SELECT * FROM friends2_0 WHERE UUID= '" + toBlock.getUniqueId().toString() + "'", new Callback<ResultSet>() {

							@Override
							public void call(ResultSet rsSELECT_T) {
								try {
									if(rsSELECT_T.next()) {
										if(rsSELECT_T.getString("FRIENDS") != null) {
											String serialized = rsSELECT_T.getString("FRIENDS");
											if(serialized.contains(performer.getUniqueId().toString())) {
												new UpdateRunnable("UPDATE friends2_0 SET "
														+ "FRIENDS= '" + serialized.replace(performer.getUniqueId().toString() + "//;", "") + "' WHERE "
														 + "UUID= '" + toBlock.getUniqueId().toString() + "'", null).runTaskAsynchronously(plugin);
											}
										}
										if(rsSELECT_T.getString("REQUESTS") != null) {
											String serialized = rsSELECT_T.getString("REQUESTS");
											if(serialized.contains(performer.getUniqueId().toString())) {
												new UpdateRunnable("UPDATE friends2_0 SET "
														+ "REQUESTS= '" + serialized.replace(performer.getUniqueId().toString() + "//;", "") + "' WHERE "
																+ "UUID= '" + toBlock.getUniqueId().toString(), null).runTaskAsynchronously(plugin);
											}
										}
									}
								} catch (Exception ex) {ex.printStackTrace();}
							}
						}).runTaskAsynchronously(plugin);
						new BukkitRunnable() {
							
							@Override
							public void run() {
								performer.updateInventory();
								if (BungeeMessagingListener.isOnline(toBlock)
										&& friends.contains(toBlock.getUniqueId().toString())) {
									Bukkit.getPlayer(toBlock.getUniqueId()).sendMessage(plugin
											.getString("Messages.Commands.Block.Block.ToBlock").replace("%PLAYER%", performer.getName()));
								}
								performer.sendMessage(plugin.getString("Messages.Commands.Block.Block.Blocker").replace("%PLAYER%",
										toBlock.getName()));
							}
						}.runTask(plugin);
					}
				} catch (Exception ex) {ex.printStackTrace();}
			}
		}).runTaskAsynchronously(plugin);
	}

}
