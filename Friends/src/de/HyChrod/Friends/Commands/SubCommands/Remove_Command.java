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

public class Remove_Command {

	@SuppressWarnings("deprecation")
	public Remove_Command(Friends plugin, Player player, String[] args, Callback<Boolean> callback) {
		if(!player.hasPermission("Friends.Commands.Remove")) {
			player.sendMessage(plugin.getString("Messages.Commands.NoPerm"));
			callback.call(false);
			return;
		}
		if(args.length != 2) {
			player.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f remove <Player>"));
			callback.call(false);
			return;
		}
		OfflinePlayer toRemove = Bukkit.getOfflinePlayer(args[1]);
		plugin.pool.execute(new Runnable() {
			public void run() {
				try {
					PlayerUtilities pU = PlayerUtilities.getUtilities(player.getUniqueId().toString());
					while(!pU.isFinished)
						synchronized (this) {
							wait(5L);
						}
					if(!pU.isFriend(toRemove.getUniqueId().toString())) {
						player.sendMessage(plugin.getString("Messages.Commands.Remove.NoFriends"));
						callback.call(false);
						return;
					}
					
					PlayerUtilities pT = PlayerUtilities.getUtilities(toRemove.getUniqueId().toString());
					while(!pT.isFinished)
						synchronized (this) {
							wait(5L);
						}
					
					pU.removeFriend(toRemove.getUniqueId().toString());
					pT.removeFriend(player.getUniqueId().toString());
					
					player.sendMessage(plugin.getString("Messages.Commands.Remove.Remove.Remover").replace("%PLAYER%", toRemove.getName()));
					callback.call(true);
					BungeeMessagingListener.isOnline(toRemove, new Callback<Boolean>() {
						
						@Override
						public void call(Boolean result) {
							if(result) {
								BungeeMessagingListener.sendMessage(player, toRemove.getName(), plugin.getString("Messages.Commands.Remove.Remove.ToRemove")
										.replace("%PLAYER%", player.getName()));
								
								String data = toRemove.getName() + "@" + player.getName();
								Bukkit.getServer().getPluginManager().callEvent(new BungeeSpreadEvent(player, toRemove.getName(), "RemovePlayer", data));
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
