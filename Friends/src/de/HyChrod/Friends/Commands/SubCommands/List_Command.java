/*
 *
 * This class was made by HyChrod
 * All rights reserved, 2017
 *
 */
package de.HyChrod.Friends.Commands.SubCommands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Listeners.BungeeMessagingListener;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.FriendPlayer;
import de.HyChrod.Friends.Utilities.PlayerUtilities;

public class List_Command {

	String ONLINE = "";
	String OFFLINE = "";
	int counter = 0;
	
	public List_Command(Friends plugin, Player player, String[] args, Callback<Boolean> callback) {
		if(!player.hasPermission("Friends.Commands.List")) {
			player.sendMessage(plugin.getString("Messages.Commands.NoPerm"));
			callback.call(false);
			return;
		}
		if(args.length != 1) {
			player.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f list"));
			callback.call(false);
			return;
		}
		
		plugin.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					PlayerUtilities pU = PlayerUtilities.getUtilities(player.getUniqueId().toString());
					while(!pU.isFinished)
						synchronized (this) {
							wait(5L);
						}
					
					for(FriendPlayer fp : pU.getFriends()) {
						OfflinePlayer friend = Bukkit.getOfflinePlayer(UUID.fromString(fp.getUUID()));
						BungeeMessagingListener.isOnline(friend, new Callback<Boolean>() {
							
							@Override
							public void call(Boolean isOnline) {
								counter++;
								ONLINE = isOnline ? ONLINE + friend.getName() + ", " : ONLINE;
								OFFLINE = !isOnline ? OFFLINE + friend.getName() + ", " : OFFLINE;
							}
						});
					}
					while(counter != pU.getFriends().size())
						synchronized (this) {
							wait(5L);
						}
					
					String ONLINE_CUTTET = ONLINE.length() > 3 ? ONLINE.substring(0, ONLINE.length() - 2) : ONLINE;
					String OFFLINE_CUTTET = OFFLINE.length() > 3 ? OFFLINE.substring(0, OFFLINE.length() - 2) : OFFLINE;
					
					for(String subString : plugin.getString("Messages.Commands.List").split("//"))
						player.sendMessage(subString
								.replace("%ONLINE_COUNT%", String.valueOf(ONLINE.split(",").length-1))
								.replace("%OFFLINE_COUNT%", String.valueOf(OFFLINE.split(",").length-1))
								.replace("%ONLINE%", ONLINE_CUTTET)
								.replace("%OFFLINE%", OFFLINE_CUTTET));
					callback.call(true);
				} catch (Exception ex) {
					callback.call(false);
					ex.printStackTrace();
				}
			}
		});
	}
	
}
