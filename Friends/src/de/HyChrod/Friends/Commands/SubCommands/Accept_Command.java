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
import de.HyChrod.Friends.Utilities.UUIDFetcher;

public class Accept_Command {

	public Accept_Command(Friends plugin, Player player, String[] args, Callback<Boolean> callback) {
		if(!player.hasPermission("Friends.Commands.Accept")) {
			player.sendMessage(plugin.getString("Messages.Commands.Accept"));
			callback.call(false);
			return;
		}
		if(args.length != 2) {
			player.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f accept <Player>"));
			callback.call(false);
			return;
		}
		
		plugin.pool.execute(new Runnable() {		
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				OfflinePlayer toAccept = Bukkit.getOfflinePlayer(args[1]);
				String name = (toAccept.isOnline() || toAccept.hasPlayedBefore()) ? toAccept.getName() : UUIDFetcher.getName(toAccept.getUniqueId());
				try {
					PlayerUtilities pU = PlayerUtilities.getUtilities(player.getUniqueId().toString());
					while(!pU.isFinished)
						synchronized (this) {
							wait(5L);
						}
					
					if(!pU.hasRequest(toAccept.getUniqueId().toString())) {
						player.sendMessage(plugin.getString("Messages.Commands.Accept.NoRequest"));
						callback.call(false);
						return;
					}
					PlayerUtilities pT = PlayerUtilities.getUtilities(toAccept.getUniqueId().toString());
					while(!pT.isFinished)
						synchronized (this) {
							wait(5L);
						}
					if (pU.getFriends().size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit"))
						if (!player.hasPermission("Friends.ExtraFriends") || pU.getFriends().size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
							player.sendMessage(Friends.getInstance().getString("Messages.Commands.Add.LimitReached.Requester"));
							callback.call(false);
							return;
						}
					if (pT.getFriends().size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit"))
						if(toAccept.isOnline()) {
							Player toCheck = Bukkit.getPlayer(name);
							if (!toCheck.hasPermission("Friends.ExtraFriends") || pT.getFriends().size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
								player.sendMessage(Friends.getInstance().getString("Messages.Commands.Add.LimitReached.ToAdd"));
								callback.call(false);
								return;
							}
						}
					pT.addFriend(player.getUniqueId().toString());
					pU.addFriend(toAccept.getUniqueId().toString());
					pU.removeRequest(toAccept.getUniqueId().toString());
					player.sendMessage(plugin.getString("Messages.Commands.Accept.Accept.Accepter").replace("%PLAYER%", name));
					callback.call(true);
					
					BungeeMessagingListener.isOnline(toAccept, new Callback<Boolean>() {
						
						@Override
						public void call(Boolean result) {
							if(result) {
								String data = name + "@" + player.getName();
								Bukkit.getServer().getPluginManager().callEvent(new BungeeSpreadEvent(player, name, "AcceptPlayer", data));
								BungeeMessagingListener.sendMessage(player, name, plugin.getString("Messages.Commands.Accept.Accept.ToAccept").replace("%PLAYER%", player.getName()));
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
