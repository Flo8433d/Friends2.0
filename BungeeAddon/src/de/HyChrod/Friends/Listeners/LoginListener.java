/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Listeners;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.BungeeSQL_Manager;
import de.HyChrod.Friends.Util.PlayerUtilities;
import de.HyChrod.Friends.Util.UpdateChecker;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@SuppressWarnings("deprecation")
public class LoginListener implements Listener {
	
	private Friends plugin;

	public LoginListener(Friends friends) {
		this.plugin = friends;
	}

	@EventHandler(priority = 64)
	public void onJoin(PostLoginEvent e) {	
		plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
			
			@Override
			public void run() {
				try {
					BungeeSQL_Manager.set(e.getPlayer().getUniqueId().toString(), Integer.valueOf(1), "ONLINE");
					BungeeSQL_Manager.set(e.getPlayer().getUniqueId().toString(), BungeeCord.getInstance().getPlayer(e.getPlayer().getUniqueId()).getServer().getInfo().getName(), "SERVER");
				} catch (Exception ex) {}
			}
		}, 1, TimeUnit.SECONDS);
		
		PlayerUtilities pu = new PlayerUtilities(e.getPlayer().getUniqueId().toString());
		if ((e.getPlayer().hasPermission("Friends.Admin")) && (!UpdateChecker.check())
				&& (this.plugin.ConfigCfg.getBoolean("Friends.CheckForUpdates"))) {
			e.getPlayer().sendMessage(this.plugin.prefix + " §cA new §6BungeeCord §cupdate is available!");
			e.getPlayer().sendMessage(this.plugin.prefix + " §cPlease update your plugin!");
		}
		for (String player : pu.getFriends()) {
			if (BungeeCord.getInstance().getPlayer(UUID.fromString(player)) != null) {
				PlayerUtilities puT = new PlayerUtilities(player);
				if (!puT.getOptions().contains("option_noChat")) {
					BungeeCord.getInstance().getPlayer(UUID.fromString(player)).sendMessage(
							this.plugin.getString("Messages.FriendJoin").replace("%PLAYER%", e.getPlayer().getName()));
				}
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerDisconnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		BungeeSQL_Manager.set(p.getUniqueId().toString(), Integer.valueOf(0), "ONLINE");
		BungeeSQL_Manager.set(p.getUniqueId().toString(), Long.valueOf(System.currentTimeMillis()), "LASTONLINE");
		PlayerUtilities pu = new PlayerUtilities(p.getUniqueId().toString());
		for (String player : pu.getFriends()) {
			if (BungeeCord.getInstance().getPlayer(UUID.fromString(player)) != null) {
				PlayerUtilities puT = new PlayerUtilities(player);
				if (!puT.getOptions().contains("option_noChat")) {
					BungeeCord.getInstance().getPlayer(UUID.fromString(player))
							.sendMessage(this.plugin.getString("Messages.FriendQuit").replace("%PLAYER%", p.getName()));
				}
			}
		}
	}
}
