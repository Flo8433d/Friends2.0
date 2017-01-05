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

public class Block_Command {

	@SuppressWarnings("deprecation")
	public Block_Command(Friends plugin, Player player, String[] args, Callback<Boolean> callback) {
		if(!player.hasPermission("Friends.Commands.Block")) {
			player.sendMessage(plugin.getString("Messages.Commands.NoPerm"));
			callback.call(false);
			return;
		}
		if(args.length != 2) {
			player.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f block <Player>"));
			callback.call(false);
			return;
		}
		OfflinePlayer toBlock = Bukkit.getOfflinePlayer(args[1]);
		if(!toBlock.isOnline() && !toBlock.hasPlayedBefore()) {
			player.sendMessage(plugin.getString("Messages.Commands.Block.PlayerOffline"));
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
					for(FriendPlayer fp : pU.getBlocked()) {
						if(fp.getUUID().equals(toBlock.getUniqueId().toString())) {
							player.sendMessage(plugin.getString("Messages.Commands.Block.AlreadyBlocked"));
							return;
						}
					}
					
					PlayerUtilities pT = PlayerUtilities.getUtilities(toBlock.getUniqueId().toString());
					while(!pT.isFinished)
						synchronized (this) {
							wait(5L);
						}
					if(pU.isFriend(toBlock.getUniqueId().toString())) {
						pT.removeFriend(player.getUniqueId().toString());
						pU.removeFriend(toBlock.getUniqueId().toString());
					}
					if(pU.hasRequest(toBlock.getUniqueId().toString())) {
						pU.removeRequest(toBlock.getUniqueId().toString());
					}
					if(pT.hasRequest(player.getUniqueId().toString())) {
						pT.removeRequest(player.getUniqueId().toString());
					}
					pU.addBlocked(toBlock.getUniqueId().toString());
					player.sendMessage(plugin.getString("Messages.Commands.Block.Block.Blocker").replace("%PLAYER%",
							toBlock.getName()));
					callback.call(true);
					BungeeMessagingListener.isOnline(toBlock, new Callback<Boolean>() {
						
						@Override
						public void call(Boolean result) {
							if(result) {
								if(pU.isFriend(toBlock.getUniqueId().toString()))
									BungeeMessagingListener.sendMessage(player, toBlock.getName(), plugin.getString("Messages.Commands.Block.Block.ToBlock")
											.replace("%PLAYER%", player.getName()));
								
								String data = toBlock.getName() + "@" + player.getName();
								Bukkit.getServer().getPluginManager().callEvent(new BungeeSpreadEvent(player, toBlock.getName(), "BlockPlayer", data));
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
