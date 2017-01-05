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
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.Listeners.BungeeMessagingListener;
import de.HyChrod.Friends.Listeners.BungeeSpreadEvent;
import de.HyChrod.Friends.Listeners.ChatListener;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.FriendPlayer;
import de.HyChrod.Friends.Utilities.PlayerUtilities;

public class Toggle_Command {

	public Toggle_Command(Friends plugin, Player player, String[] args, Callback<Boolean> callback) {
		if(args.length != 2) {
			player.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f toggle <requests|chat|jumping|msg>"));
			callback.call(false);
			return;
		}
		
		plugin.pool.execute(new Runnable() {
			public void run() {
				try {
					PlayerUtilities pu = PlayerUtilities.getUtilities(player.getUniqueId().toString());
					while(!pu.isFinished)
						synchronized (this) {
							wait(5L);
						}
					callback.call(true);
					if (args[1].equalsIgnoreCase("requests")) {
						if (!checkPerm(player, "Friends.Commands.Toggle.Requests"))
							return;
						pu.toggleOption("option_noRequests");
						player.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleRequests"));
						updateOnBungee(plugin, pu, player, "option_noRequests");
						return;
					}
					if (args[1].equalsIgnoreCase("chat")) {
						if (!checkPerm(player, "Friends.Commands.Toggle.Chat"))
							return;
						pu.toggleOption("option_noChat");
						player.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleChat"));
						updateOnBungee(plugin, pu, player, "option_noChat");
						return;
					}
					if (args[1].equalsIgnoreCase("jumping")) {
						if (!checkPerm(player, "Friends.Commands.Toggle.Jumping"))
							return;
						pu.toggleOption("option_noJumping");
						player.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleJumping"));
						updateOnBungee(plugin, pu, player, "option_noJumping");
						return;
					}
					if (args[1].equalsIgnoreCase("msg")) {
						if (!checkPerm(player, "Friends.Commands.Toggle.Msg"))
							return;
						pu.toggleOption("option_noMsg");
						updateOnBungee(plugin, pu, player, "option_noMsg");
						player.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleMsg"));
						return;
					}
					if (args[1].equalsIgnoreCase("spychat")) {
						if (!checkPerm(player, "Friends.Commands.SpyChat"))
							return;

						if (!FileManager.ConfigCfg.getBoolean("Friends.FriendChat.SpyChat.Enable")) {
							player.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleSpyChat.Disabled"));
							return;
						}
						if (!ChatListener.spy.contains(player)) {
							ChatListener.spy.add(player);
							player.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleSpyChat.Toggle"));
						} else {
							ChatListener.spy.remove(player);
							player.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleSpyChat.Disabled"));
						}
						return;
					}
				} catch (Exception ex) {ex.printStackTrace();}
			}
		});
	}
	
	private void updateOnBungee(Friends plugin, PlayerUtilities pu, Player player, String option) {
		String data = player.getName() + "@" + option;	
		for(FriendPlayer fp : pu.getFriends()) {
			OfflinePlayer pp = Bukkit.getOfflinePlayer(UUID.fromString(fp.getUUID()));
			BungeeMessagingListener.isOnline(pp, new Callback<Boolean>() {

				@Override
				public void call(Boolean done) {
					if(done)
						Bukkit.getServer().getPluginManager().callEvent(new BungeeSpreadEvent(player, Bukkit.getOfflinePlayer(UUID.fromString(fp.getUUID())).getName(), "ToggleOption", data));
				}
			});
		}
	}
	
	private boolean checkPerm(Player player, String permission) {
		if(!player.hasPermission(permission))
			player.sendMessage(Friends.getInstance().getString("Messages.Commands.NoPerm"));
		return player.hasPermission(permission);
	}
	
}
