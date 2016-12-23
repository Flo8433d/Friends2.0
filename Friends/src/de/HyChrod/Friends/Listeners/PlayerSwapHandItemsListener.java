/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import de.HyChrod.Friends.Util.UtilitieItems;

public class PlayerSwapHandItemsListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSpawn(PlayerSwapHandItemsEvent e) {
		Player p = e.getPlayer();
		if (e.getMainHandItem() != null) {
			if (e.getMainHandItem().hasItemMeta()) {
				if (e.getMainHandItem().getItemMeta().hasDisplayName()) {
					if (e.getMainHandItem().getItemMeta().getDisplayName()
							.equals(new UtilitieItems().FRIENDITEM(p).getItemMeta().getDisplayName())) {
						e.setCancelled(true);
					}
				}
			}
		}
		if (e.getOffHandItem() != null) {
			if (e.getOffHandItem().hasItemMeta()) {
				if (e.getOffHandItem().getItemMeta().hasDisplayName()) {
					if (e.getOffHandItem().getItemMeta().getDisplayName()
							.equals(new UtilitieItems().FRIENDITEM(p).getItemMeta().getDisplayName())) {
						e.setCancelled(true);
					}
				}
			}
		}
	}

}
