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
import de.HyChrod.Friends.Listeners.BungeeMessagingListener;
import de.HyChrod.Friends.Listeners.BungeeSpreadEvent;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.FriendPlayer;
import de.HyChrod.Friends.Utilities.PlayerUtilities;
import de.HyChrod.Friends.Utilities.UUIDFetcher;

public class Deny_Command {

	@SuppressWarnings("deprecation")
	public Deny_Command(Friends plugin, Player player, String[] args, Callback<Boolean> callback) {
		if(!player.hasPermission("Friends.Commands.Deny")) {
			player.sendMessage(plugin.getString("Messages.Commands.NoPerm"));
			callback.call(false);
			return;
		}
		if(args.length != 2) {
			player.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f deny <Player>"));
			callback.call(false);
			return;
		}
		
		plugin.pool.execute(new Runnable() {
			public void run() {
				try {
					OfflinePlayer toDeny = Bukkit.getOfflinePlayer(args[1]);
					String name = (toDeny.isOnline() || toDeny.hasPlayedBefore()) ? toDeny.getName() : UUIDFetcher.getName(toDeny.getUniqueId());
					PlayerUtilities pU = PlayerUtilities.getUtilities(player.getUniqueId().toString());
					while(!pU.isFinished)
						synchronized (this) {
							wait(5L);
						}
					boolean v = false;
					for(FriendPlayer fp : pU.getRequests()) {
						if(fp.getUUID().toString().equals(toDeny.getUniqueId().toString()))
							v = true;
					}
					if(!v) {
						player.sendMessage(plugin.getString("Messages.Commands.Deny.NoRequest"));
						callback.call(false);
						return;
					}
					pU.removeRequest(toDeny.getUniqueId().toString());
					player.sendMessage(plugin.getString("Messages.Commands.Deny.Deny.Denier").replace("%PLAYER%", name));
					callback.call(true);
					BungeeMessagingListener.isOnline(toDeny, new Callback<Boolean>() {
						
						@Override
						public void call(Boolean result) {
							if(result) {
								BungeeMessagingListener.sendMessage(player, name, plugin.getString("Messages.Commands.Deny.Deny.ToDeny")
										.replace("%PLAYER%", player.getName()));
								
								String data = name + "@" + player.getName();
								Bukkit.getServer().getPluginManager().callEvent(new BungeeSpreadEvent(player, name, "DenyPlayer", data));
							}
						}
					});
				} catch (Exception ex) {
					callback.call(false);
					ex.printStackTrace();
				}
			}
		});
	}
	
}
