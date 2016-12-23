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

public class Deny_Command {
	
	public Deny_Command(Friends plugin, Player performer, OfflinePlayer toDeny) {
		new QueryRunnable("SELECT * FROM friends2_0 WHERE UUID= '" + performer.getUniqueId().toString() + "'", new Callback<ResultSet>() {
			
			@Override
			public void call(ResultSet rsSELECT_P) {
				try {
					if(rsSELECT_P.next() && (rsSELECT_P.getString("REQUESTS") != null)) {
						String serialized = rsSELECT_P.getString("REQUESTS");
						if(serialized.contains(toDeny.getUniqueId().toString())) {
							new UpdateRunnable("UPDATE friends2_0 SET "
									+ "REQUESTS= '" + serialized.replace(toDeny.getUniqueId().toString() + "//;", "") + "' WHERE "
											+ "UUID= '" + performer.getUniqueId().toString() + "'", null).runTaskAsynchronously(plugin);
							new BukkitRunnable() {
								
								@Override
								public void run() {
									performer.updateInventory();
									performer.sendMessage(plugin.getString("Messages.Commands.Deny.Deny.Denier").replace("%PLAYER%",
											toDeny.getName()));
									if (BungeeMessagingListener.isOnline(toDeny)) {
										FriendCommands.sendMessage(performer, toDeny.getName(), plugin.getString("Messages.Commands.Deny.Deny.ToDeny")
												.replace("%PLAYER%", performer.getName()));
									}
								}
							}.runTask(plugin);
							return;
						}
					}
				} catch (Exception ex) {ex.printStackTrace();}
				performer.sendMessage(plugin.getString("Messages.Commands.Deny.NoRequest"));
				return;
			}
		}).runTaskAsynchronously(plugin);
	}

}
