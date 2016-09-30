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
import org.bukkit.event.player.PlayerQuitEvent;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.BungeeSQL_Manager;
import de.HyChrod.Friends.Util.PlayerUtilities;

public class QuitListener implements Listener {
	
	public Friends plugin;
	
	public QuitListener(Friends friends) {
		this.plugin = friends;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(Friends.bungeeMode) {
			BungeeSQL_Manager.setLastOnline(p, System.currentTimeMillis());
			return;
		}
		
		PlayerUtilities pu = new PlayerUtilities(p);
		pu.setLastOnline(System.currentTimeMillis());
		pu.saveData(false);
		
		for(OfflinePlayer player : pu.getFriends()) {
			if(player.isOnline()) {
				PlayerUtilities puT = new PlayerUtilities(player);
				if(!puT.getOptions().contains("option_noChat")) {
					Bukkit.getPlayer(player.getUniqueId()).sendMessage(plugin.getString("Messages.FriendQuit").replace("%PLAYER%", p.getName()));
				}
			}
		}
	}

}
