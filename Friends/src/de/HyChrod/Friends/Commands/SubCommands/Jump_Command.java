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
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.PlayerUtilities;

public class Jump_Command {

	@SuppressWarnings("deprecation")
	public Jump_Command(Friends plugin, Player player, String[] args, Callback<Boolean> callback) {
		if(!player.hasPermission("Friends.Commands.Jump")) {
			player.sendMessage(plugin.getString("Messages.Commands.NoPerm"));
			callback.call(false);
			return;
		}
		if(args.length != 2) {
			player.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f jump <Player>"));
			callback.call(false);
			return;
		}
		OfflinePlayer toJump = Bukkit.getOfflinePlayer(args[1]);
		plugin.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					PlayerUtilities pU = PlayerUtilities.getUtilities(player.getUniqueId().toString());
					while(!pU.isFinished)
						synchronized (this) {
							wait(5L);
						}
					if(!pU.isFriend(toJump.getUniqueId().toString())) {
						player.sendMessage(plugin.getString("Messages.Commands.Jumping.NoFriend"));
						callback.call(false);
						return;
					}
					
					BungeeMessagingListener.isOnline(toJump, new Callback<Boolean>() {

						@Override
						public void call(Boolean isOnline) {
							if(!isOnline) {
								player.sendMessage(plugin.getString("Messages.Commands.Jumping.PlayerOffline"));
								callback.call(false);
								return;
							}
							PlayerUtilities pT = PlayerUtilities.getUtilities(toJump.getUniqueId().toString());
							if(pT.getOptions().contains("option_noJumping")) {
								player.sendMessage(plugin.getString("Messages.Commands.Jumping.Disabled"));
								callback.call(false);
								return;
							}
							
							player.sendMessage(plugin.getString("Messages.Commands.Jumping.Jump.Jumper").replace("%PLAYER%", toJump.getName()));
							BungeeMessagingListener.sendMessage(player, toJump.getName(), plugin.getString("Messages.Commands.Jumping.Jump.ToJump")
									.replace("%PLAYER%", player.getName()));
							if(Friends.bungeemode) {
								BungeeMessagingListener.getServer(toJump, new Callback<String>() {
									
									@Override
									public void call(String server) {
										if(FileManager.ConfigCfg.getBoolean("Friends.DisabledServers.Enable"))
											if(FileManager.ConfigCfg.getStringList("Friends.DisabledServers.Servers").contains(server)) {
												player.sendMessage(plugin.getString("Messages.Commands.Jumping.DisabledServer"));
												callback.call(false);
												return;
											}
										if(FileManager.ConfigCfg.getBoolean("Friends.EnabledServers.Enable"))
											if(!FileManager.ConfigCfg.getStringList("Friends.EnabledServers.Servers").contains(server)) {
												player.sendMessage(plugin.getString("Messages.Commands.Jumping.DisabledServer"));
												callback.call(false);
												return;
											}
										BungeeMessagingListener.sendToBungeeCord(player, "Connect", server, null);
										callback.call(true);
									}
								});
								return;
							}
							if(FileManager.ConfigCfg.getBoolean("Friends.DisabledWorlds.Enable")) {
								if(FileManager.ConfigCfg.getStringList("Friends.DisabledWorlds.Worlds").contains(Bukkit.getPlayer(toJump.getName())
										.getWorld().getName())) {
									player.sendMessage(plugin.getString("Messages.Commands.Jumping.DisabledWorld"));
									callback.call(false);
									return;
								}
							}
							
							player.teleport((Player) toJump);
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
