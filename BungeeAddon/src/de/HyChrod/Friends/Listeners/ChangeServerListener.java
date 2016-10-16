/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Listeners;

import de.HyChrod.Friends.SQL.BungeeSQL_Manager;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChangeServerListener implements Listener {
	@EventHandler(priority = 64)
	public void onChange(ServerSwitchEvent e) {
		BungeeSQL_Manager.setServer(e.getPlayer().getUniqueId().toString(),
				e.getPlayer().getServer().getInfo().getName());
	}
}
