/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Util.PlayerUtilities;

public class ChatListener implements Listener {

	public Friends plugin;

	public ChatListener(Friends friends) {
		this.plugin = friends;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (Friends.bungeeMode) {
			return;
		}
		if (FileManager.ConfigCfg.getBoolean("Friends.FriendChat.Enable")) {
			if (e.getMessage().startsWith(FileManager.ConfigCfg.getString("Friends.FriendChat.Code"))) {
				e.setCancelled(true);

				PlayerUtilities pu = new PlayerUtilities(p);
				for (OfflinePlayer player : pu.getFriends()) {
					if (player.isOnline()) {
						PlayerUtilities puT = new PlayerUtilities(player);
						if (!puT.getOptions().contains("option_noChat")) {
							Bukkit.getPlayer(player.getUniqueId())
									.sendMessage(plugin.getString("Messages.FriendChatFormat")
											.replace("%PLAYER%", p.getName()).replace("%MESSAGE%", e.getMessage())
											.replace(FileManager.ConfigCfg.getString("Friends.FriendChat.Code"), ""));
						}
					}
				}
				p.sendMessage(plugin.getString("Messages.FriendChatFormat").replace("%PLAYER%", p.getName())
						.replace("%MESSAGE%", e.getMessage())
						.replace(FileManager.ConfigCfg.getString("Friends.FriendChat.Code"), ""));
			}
		}
	}

}
