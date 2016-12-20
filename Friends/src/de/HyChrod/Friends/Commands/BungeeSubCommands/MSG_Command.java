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

public class MSG_Command {
	
	public MSG_Command(Friends plugin, Player performer, OfflinePlayer toSend, String[] args) {
		new QueryRunnable("SELECT * FROM friends2_0 WHERE UUID= '" + performer.getUniqueId().toString() + "'", new Callback<ResultSet>() {
			
			@Override
			public void call(ResultSet rsSELECT_P) {
				try {
					if(rsSELECT_P.next()) {
						if(rsSELECT_P.getString("OPTIONS") != null) {
							String options_P = rsSELECT_P.getString("OPTIONS");
							if(options_P.contains("option_noMsg")) {
								performer.sendMessage(plugin.getString("Messages.Commands.MSG.Disabled"));
								return;
							}
						}
						if(rsSELECT_P.getString("FRIENDS") != null) {
							String serialized = rsSELECT_P.getString("FRIENDS");
							if(serialized.contains(toSend.getUniqueId().toString())) {
								if(!BungeeMessagingListener.isOnline(toSend)) {
									performer.sendMessage(plugin.getString("Messages.Commands.MSG.PlayerOffline"));
									return;
								}
								new QueryRunnable("SELECT * FROM friends2_0 WHERE UUID= '" + toSend.getUniqueId().toString() + "'", new Callback<ResultSet>() {

									@Override
									public void call(ResultSet rsSELECT_T) {
										try {
											if(rsSELECT_T.next() && (rsSELECT_T.getString("OPTIONS") != null)) {
												String options_T = rsSELECT_T.getString("OPTIONS");
												if(options_T.contains("option_noMsg")) {
													performer.sendMessage(plugin.getString("Messages.Commands.MSG.Disabled"));
													return;
												}
											}
										} catch (Exception ex) {ex.printStackTrace();}
										new BukkitRunnable() {
											
											@Override
											public void run() {
												String msg = "";
												for (int i = 2; i < args.length; i++) {
													msg = msg + args[i] + " ";
												}
												FriendCommands.sendMessage(performer, toSend.getName(),
														plugin.getString("Messages.Commands.MSG.Format").replace("%PREFIX%", plugin.prefix)
																.replace("%PLAYER%", performer.getName()).replace("%MESSAGE%", msg));
												performer.sendMessage(plugin.getString("Messages.Commands.MSG.Send")
														.replace("%PLAYER%", toSend.getName()).replace("%MESSAGE%", msg));
											}
										}.runTask(plugin);
									}
								}).runTaskAsynchronously(plugin);
								return;
							}
						}
					}
					performer.sendMessage(plugin.getString("Messages.Commands.MSG.NoFriends"));
					return;
				} catch (Exception ex) {ex.printStackTrace();}
			}
		}).runTaskAsynchronously(plugin);
	}

}
