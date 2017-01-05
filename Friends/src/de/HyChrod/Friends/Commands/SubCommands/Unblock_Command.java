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
import de.HyChrod.Friends.Utilities.PlayerUtilities;

public class Unblock_Command {

	@SuppressWarnings("deprecation")
	public Unblock_Command(Friends plugin, Player player, String[] args, Callback<Boolean> callback) {
		if(!player.hasPermission("Friends.Commands.Unblock")) {
			player.sendMessage(plugin.getString("Messages.Commands.NoPerm"));
			callback.call(false);
			return;
		}
		if(args.length != 2) {
			player.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f unblock <Player>"));
			callback.call(false);
			return;
		}
		OfflinePlayer toUnblock = Bukkit.getOfflinePlayer(args[1]);
		plugin.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					PlayerUtilities pU = PlayerUtilities.getUtilities(player.getUniqueId().toString());
					while(!pU.isFinished)
						synchronized (this) {
							wait(5L);
						}
					if(!pU.isBlocked(toUnblock.getUniqueId().toString())) {
						player.sendMessage(plugin.getString("Messages.Commands.Unblock.NotBlocked"));
						callback.call(false);
						return;
					}
					pU.removeBlocked(toUnblock.getUniqueId().toString());
					player.sendMessage(plugin.getString("Messages.Commands.Unblock.Unblock").replace("%PLAYER%", toUnblock.getName()));
					callback.call(true);
					BungeeMessagingListener.isOnline(toUnblock, new Callback<Boolean>() {
						
						@Override
						public void call(Boolean result) {
							if(result) {
								String data = toUnblock.getName() + "@" + player.getName();
								Bukkit.getServer().getPluginManager().callEvent(new BungeeSpreadEvent(player, toUnblock.getName(), "UnblockPlayer", data));
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
