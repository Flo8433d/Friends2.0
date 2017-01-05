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
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.FriendPlayer;
import de.HyChrod.Friends.Utilities.PlayerUtilities;

public class DenyAll_Command {

	public DenyAll_Command(Friends plugin, Player player, String[] args, Callback<Boolean> callback) {
		if(!player.hasPermission("Friends.Commands.Denyall")) {
			player.sendMessage(plugin.getString("Messages.Commands.NoPerm"));
			callback.call(false);
			return;
		}
		if(args.length != 1) {
			player.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f denyall"));
			callback.call(false);
			return;
		}
		plugin.pool.execute(new Runnable() {
			public void run() {
				try {
					PlayerUtilities pU = PlayerUtilities.getUtilities(player.getUniqueId().toString());
					while(!pU.isFinished)
						synchronized (this) {
							wait(5L);
						}
					ArrayList<FriendPlayer> REQUESTS = new ArrayList<>(pU.getRequests());
					for(FriendPlayer fp : REQUESTS)
						new Deny_Command(plugin, player, new String[] {"deny", Bukkit.getOfflinePlayer(UUID.fromString(fp.getUUID())).getName()}, new Callback<Boolean>() {

							@Override
							public void call(Boolean done) {}
						});
					callback.call(true);
				} catch (Exception ex) {
					callback.call(false);
					ex.printStackTrace();
				}
			}
		});
	}
	
}
