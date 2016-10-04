/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Util.InventoryBuilder;
import de.HyChrod.Friends.Util.ItemStacks;

public class InteractListener implements Listener {

	private Friends plugin;

	public InteractListener(Friends friends) {
		this.plugin = friends;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (FileManager.ConfigCfg.getBoolean("Friends.GUI.Enable")) {
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
				if (e.getItem() != null) {
					if (e.getItem().hasItemMeta()) {
						if (e.getItem().getItemMeta().hasDisplayName()) {
							if (e.getItem().getItemMeta().getDisplayName()
									.equals(ChatColor.translateAlternateColorCodes('&',
											FileManager.ConfigCfg.getString("Friends.FriendItem.Displayname")))
									|| e.getItem().equals(ItemStacks.FRIENDITEM(((Player) e.getPlayer())))) {
								InventoryBuilder.MAIN_INVENTORY(plugin, p);
								return;
							}
						}
					}
				}
			}
		}
	}

}
