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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Util.InventoryBuilder;
import de.HyChrod.Friends.Util.ItemStacks;

public class InventoryUtilListener implements Listener {

	private Friends plugin;

	public InventoryUtilListener(Friends friends) {
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
								InventoryBuilder.MAIN_INVENTORY(plugin, p, true);
								return;
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (e.getInventory() != null) {

			if (BlockedEditInventoryListener.editing.containsKey(p)) {
				if (e.getInventory().getTitle()
						.equals(ChatColor.translateAlternateColorCodes('&',
								FileManager.ConfigCfg.getString("Friends.GUI.BlockedEditInv.Title").replace("%PLAYER%",
										BlockedEditInventoryListener.editing.get(p).getName())))) {
					BlockedEditInventoryListener.editing.remove(p);
				}
			}
			if (EditInventoryListener.editing.containsKey(p)) {
				if (e.getInventory().getTitle()
						.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
								FileManager.ConfigCfg.getString("Friends.GUI.FriendEditInv.Title").replace("%FRIEND%",
										EditInventoryListener.editing.get(p).getName()))))
					EditInventoryListener.editing.remove(p);
			}
			if (MainInventoryListener.currentSite.containsKey(p)) {
				if (e.getInventory().getTitle().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
						FileManager.ConfigCfg.getString("Friends.GUI.Title")))) {
					MainInventoryListener.currentSite.remove(p);
				}
			}
			if (PageListener.currentSite.containsKey(p)) {
				if (e.getInventory().getTitle()
						.equals(ChatColor.translateAlternateColorCodes('&',
								FileManager.ConfigCfg.getString("Friends.GUI.RequestsInv.Title")))
						|| e.getInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&',
								FileManager.ConfigCfg.getString("Friends.GUI.BlockedInv.Title"))))
					PageListener.currentSite.remove(p);
			}
			if (RemoveVerificationInventoryListener.confirming.containsKey(p)) {
				if (e.getInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&',
						FileManager.ConfigCfg.getString("Friends.GUI.RemoveVerificationInv.Title")))) {
					RemoveVerificationInventoryListener.confirming.remove(p);
				}
			}
			if (RequestEditInventoryListener.editing.containsKey(p)) {
				if (e.getInventory().getTitle()
						.equals(ChatColor.translateAlternateColorCodes('&',
								FileManager.ConfigCfg.getString("Friends.GUI.RequestEditInv.Title").replace("%PLAYER%",
										RequestEditInventoryListener.editing.get(p).getName())))) {
					RequestEditInventoryListener.editing.remove(p);
				}
			}
		}
	}

}
