/*
 *
 * This class was made by HyChrod
 * All rights reserved, 2017
 *
 */
package de.HyChrod.Friends.Commands.SubCommands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.FriendPlayer;
import de.HyChrod.Friends.Utilities.PlayerUtilities;
import de.HyChrod.Friends.Utilities.UUIDFetcher;

public class AcceptAll_Command {

	public AcceptAll_Command(Friends plugin, Player player, String[] args, Callback<Boolean> callback) {
		if(!player.hasPermission("Friends.Commands.Acceptall")) {
			player.sendMessage(plugin.getString("Messages.Commands.NoPerm"));
			callback.call(false);
			return;
		}
		if(args.length != 1) {
			player.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f acceptall"));
			callback.call(false);
			return;
		}
		
		plugin.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				PlayerUtilities pU = PlayerUtilities.getUtilities(player.getUniqueId().toString());
				try {
					while(!pU.isFinished)
						synchronized (this) {
							wait(5L);
						}
					ArrayList<FriendPlayer> REQUESTS = new ArrayList<>(pU.getRequests());
					for(FriendPlayer fp : REQUESTS) {
						OfflinePlayer pp = Bukkit.getOfflinePlayer(UUID.fromString(fp.getUUID()));
						String name = pp != null ? pp.getName() != null ? pp.getName() : UUIDFetcher.getName(UUID.fromString(fp.getUUID())) : "E R R O R";
						new Accept_Command(plugin, player, new String[] {"accept", name}, new Callback<Boolean>() {

							@Override
							public void call(Boolean done) {}
						});
					}
					callback.call(true);
				} catch (Exception ex) {
					callback.call(false);
					ex.printStackTrace();
				}
				
			}
		});
	}
	
}
