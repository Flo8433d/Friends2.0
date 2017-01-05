/*
*
* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.Listeners;

import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.Utilities.FriendPlayer;
import de.HyChrod.Friends.Utilities.PlayerUtilities;

public class ChatListener implements Listener {

	public Friends plugin;
	public static LinkedList<Player> spy  = new LinkedList<>();

	public ChatListener(Friends friends) {
		this.plugin = friends;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (Friends.bungeemode) {
			return;
		}
		if (FileManager.ConfigCfg.getBoolean("Friends.FriendChat.Enable")) {
			if (e.getMessage().startsWith(FileManager.ConfigCfg.getString("Friends.FriendChat.Code"))) {
				e.setCancelled(true);
				
				plugin.pool.execute(new Runnable() {
					
					@Override
					public void run() {
						try {
							PlayerUtilities pu = PlayerUtilities.getUtilities(p.getUniqueId().toString());
							while(!pu.isFinished)
								synchronized (this) {
									wait(5L);
								}
							if(FileManager.ConfigCfg.getBoolean("Friends.FriendChat.SpyChat.Enable")) {
								for(Player spyer : spy) {
									if(!p.equals(spyer) && (!pu.getFriends().contains(spyer.getUniqueId().toString()) || pu.getOptions().contains("options_noChat"))) {
										spyer.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.FriendChat.SpyChat.Format"))
												.replace("%PLAYER%", p.getName()).replace("%MESSAGE%", e.getMessage()
														.replace(FileManager.ConfigCfg.getString("Friends.FriendChat.Code"), "")));
									}
								}
							}
							if(pu.getOptions().contains("option_noChat")) {
								p.sendMessage(plugin.getString("Messages.FriendChatDisabled"));
								return;
							}
							for (FriendPlayer FP : pu.getFriends()) {
								OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(FP.getUUID()));
								if (player.isOnline()) {
									PlayerUtilities puT = PlayerUtilities.getUtilities(player.getUniqueId().toString());
									while(!puT.isFinished)
										synchronized (this) {
											wait(5L);
										}
									if (!puT.getOptions().contains("option_noChat")) {
										Bukkit.getPlayer(player.getUniqueId())
												.sendMessage(plugin.getString("Messages.FriendChatFormat")
														.replace("%PLAYER%", p.getName()).replace("%MESSAGE%", e.getMessage())
														.replace(FileManager.ConfigCfg.getString("Friends.FriendChat.Code"), ""));
									}
								}
							}
						} catch (Exception ex) {ex.printStackTrace();}
					}
				});
				p.sendMessage(plugin.getString("Messages.FriendChatFormat").replace("%PLAYER%", p.getName())
						.replace("%MESSAGE%", e.getMessage())
						.replace(FileManager.ConfigCfg.getString("Friends.FriendChat.Code"), ""));
			}
		}
	}

}
