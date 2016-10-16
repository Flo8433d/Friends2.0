/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Listeners;

import java.util.UUID;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Util.PlayerUtilities;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

public class ChatListener implements Listener {
	private Friends plugin;
	private FileManager mgr = new FileManager();
	private Configuration cfg = this.mgr.getConfig("", "config.yml");

	public ChatListener(Friends friends) {
		this.plugin = friends;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = 64)
	public void onChat(ChatEvent e) {
		ProxiedPlayer p = (ProxiedPlayer) e.getSender();
		if ((this.cfg.getBoolean("Friends.FriendChat.Enable"))
				&& (e.getMessage().startsWith(this.cfg.getString("Friends.FriendChat.Code")))) {
			e.setCancelled(true);

			PlayerUtilities pu = new PlayerUtilities(p.getUniqueId().toString());
			for (String player : pu.getFriends()) {
				if (BungeeCord.getInstance().getPlayer(UUID.fromString(player)) != null) {
					PlayerUtilities puT = new PlayerUtilities(player);
					if (!puT.getOptions().contains("option_noChat")) {
						BungeeCord.getInstance().getPlayer(UUID.fromString(player))
								.sendMessage(this.plugin.getString("Messages.FriendChatFormat")
										.replace("%PLAYER%", p.getName()).replace("%MESSAGE%", e.getMessage())
										.replace(this.cfg.getString("Friends.FriendChat.Code"), ""));
					}
				}
			}
			p.sendMessage(this.plugin.getString("Messages.FriendChatFormat").replace("%PLAYER%", p.getName())
					.replace("%MESSAGE%", e.getMessage()).replace(this.cfg.getString("Friends.FriendChat.Code"), ""));
		}
	}
}
