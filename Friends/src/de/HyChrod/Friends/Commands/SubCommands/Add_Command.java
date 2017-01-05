/*
 *
 * This class was made by HyChrod
 * All rights reserved, 2017
 *
 */
package de.HyChrod.Friends.Commands.SubCommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.Listeners.BungeeMessagingListener;
import de.HyChrod.Friends.Listeners.BungeeSpreadEvent;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.PlayerUtilities;
import de.HyChrod.Friends.Utilities.ReflectionsManager;

public class Add_Command {
	
	@SuppressWarnings("deprecation")
	public Add_Command(Friends plugin, Player player, String[] args, Callback<Boolean> callback) {
		if(!player.hasPermission("Friends.Commands.Add")) {
			player.sendMessage(plugin.getString("Messages.Commands.NoPerm"));
			callback.call(false);
			return;
		}
		plugin.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					if(args.length != 2) {
						player.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f add <Player>"));
						callback.call(false);
						return;
					}
					if(args[1].equalsIgnoreCase(player.getName())) {
						player.sendMessage(plugin.getString("Messages.Commands.Add.SendSelf"));
						callback.call(false);
						return;
					}
					
					OfflinePlayer toAdd = Bukkit.getOfflinePlayer(args[1]);
					if(!toAdd.isOnline() && !toAdd.hasPlayedBefore()) {
						player.sendMessage(plugin.getString("Messages.Commands.Add.PlayerOffline"));
						callback.call(false);
						return;
					}
					
					PlayerUtilities pU = PlayerUtilities.getUtilities(player.getUniqueId().toString());
					while(!pU.isFinished)
						synchronized (this) {
							wait(5L);
						}
					if(pU.isFriend(toAdd.getUniqueId().toString())) {
						player.sendMessage(plugin.getString("Messages.Commands.Add.AlreadyFriends"));
						callback.call(false);
						return;
					}
					if(pU.isBlocked(toAdd.getUniqueId().toString())) {
						player.sendMessage(plugin.getString("Messages.Commands.Add.Blocked.Requester"));
						callback.call(false);
						return;
					}
					
					PlayerUtilities pF = PlayerUtilities.getUtilities(toAdd.getUniqueId().toString());
					while(!pF.isFinished)
						synchronized (this) {
							wait(5L);
						}
					
					if(pF.hasRequest(player.getUniqueId().toString())) {
						player.sendMessage(plugin.getString("Messages.Commands.Add.AlreadyRequested"));
						callback.call(false);
						return;
					}
					if(pF.isBlocked(player.getUniqueId().toString())) {
						player.sendMessage(plugin.getString("Messages.Commands.Add.Blocked.ToAdd"));
						callback.call(false);
						return;
					}
					if(pF.getOptions().contains("option_noRequests")) {
						player.sendMessage(plugin.getString("Messages.Commands.Add.NoRequests"));
						callback.call(false);
						return;
					}
					
					if (pU.getFriends().size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit"))
						if (!player.hasPermission("Friends.ExtraFriends") || pU.getFriends().size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
							player.sendMessage(Friends.getInstance().getString("Messages.Commands.Add.LimitReached.Requester"));
							callback.call(false);
							return;
						}
					if (pF.getFriends().size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit"))
						if(toAdd.isOnline()) {
							Player toCheck = Bukkit.getPlayer(toAdd.getName());
							if (!toCheck.hasPermission("Friends.ExtraFriends") || pF.getFriends().size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
								player.sendMessage(Friends.getInstance().getString("Messages.Commands.Add.LimitReached.ToAdd"));
								callback.call(false);
								return;
							}
						}
					pF.addRequest(player.getUniqueId().toString());
					if (Friends.bungeemode) {
						BungeeMessagingListener.isOnline(toAdd, new Callback<Boolean>() {
							
							@Override
							public void call(Boolean result) {
								if(result) {
									String data = toAdd.getName() + "@" + player.getName();
									Bukkit.getServer().getPluginManager().callEvent(new BungeeSpreadEvent(player, toAdd.getName(), "AddingPlayer", data));
								}
							}
						});
					} else {
						if(toAdd.isOnline()) {
							ReflectionsManager.sendRequestMessages(player, Bukkit.getPlayer(toAdd.getUniqueId()));
						}
					}
					player.sendMessage(plugin.getString("Messages.Commands.Add.Add.Requester").replace("%PLAYER%", toAdd.getName()));
					callback.call(true);
				} catch (Exception ex) {
					callback.call(false);
					ex.printStackTrace();
				}
			}
		});
	}
	
}
