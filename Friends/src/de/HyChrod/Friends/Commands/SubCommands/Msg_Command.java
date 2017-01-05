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
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.PlayerUtilities;

public class Msg_Command {

	@SuppressWarnings("deprecation")
	public Msg_Command(Friends plugin, Player player, String[] args, Callback<Boolean> callback) {
		if(!player.hasPermission("Friends.Commands.Msg")) {
			player.sendMessage(plugin.getString("Messages.Commands.NoPerm"));
			callback.call(false);
			return;
		}
		if(args.length < 3) {
			player.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f msg <Player> <Message>"));
			callback.call(false);
			return;
		}
		OfflinePlayer toSend = Bukkit.getOfflinePlayer(args[1]);
		plugin.pool.execute(new Runnable() {
			public void run() {
				try {
					PlayerUtilities pU = PlayerUtilities.getUtilities(player.getUniqueId().toString());
					while(!pU.isFinished)
						synchronized (this) {
							wait(5L);
						}
					if(!pU.isFriend(toSend.getUniqueId().toString())) {
						player.sendMessage(plugin.getString("Messages.Commands.MSG.NoFriends"));
						callback.call(false);
						return;
					}
					if(pU.hasOption("option_noMsg")) {
						player.sendMessage(plugin.getString("Messages.Commands.MSG.DisabledSelf"));
						callback.call(false);
						return;
					}
					
					PlayerUtilities pT = PlayerUtilities.getUtilities(toSend.getUniqueId().toString());
					while(!pT.isFinished)
						synchronized (this) {
							wait(5L);
						}
					if(pT.hasOption("option_noMsg")) {
						player.sendMessage(plugin.getString("Messages.Commands.MSG.Disabled"));
						callback.call(false);
						return;
					}
					
					BungeeMessagingListener.isOnline(toSend, new Callback<Boolean>() {
						
						@Override
						public void call(Boolean result) {
							if(!result) {
								player.sendMessage(plugin.getString("Messages.Commands.MSG.PlayerOffline"));
								callback.call(false);
								return;
							}
							
							String msg = "";
							for(int i = 2; i < args.length; i++) {
								msg = msg + args[i] + " ";
							}
							BungeeMessagingListener.sendMessage(player, toSend.getName(), 
									plugin.getString("Messages.Commands.MSG.Format").replace("%PLAYER%", player.getName()).replace("%MESSAGE%", msg));
							player.sendMessage(plugin.getString("Messages.Commands.MSG.Send").replace("%PLAYER%", toSend.getName()).replace("%MESSAGE%", msg));
							callback.call(true);
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
