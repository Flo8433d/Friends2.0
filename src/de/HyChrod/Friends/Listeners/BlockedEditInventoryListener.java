/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Util.InventoryBuilder;
import de.HyChrod.Friends.Util.ItemStacks;
import de.HyChrod.Friends.Util.PlayerUtilities;

public class BlockedEditInventoryListener implements Listener {
	
	private Friends plugin;
	
	public static HashMap<Player, OfflinePlayer> editing = new HashMap<>();
	
	public BlockedEditInventoryListener(Friends friends) {
		this.plugin = friends;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {
		final Player p = (Player) e.getWhoClicked();
		if(e.getInventory() != null) {
			if(editing.containsKey(p)) {
				if(e.getInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', 
						FileManager.ConfigCfg.getString("Friends.GUI.BlockedEditInv.Title").replace("%PLAYER%", editing.get(p).getName())))) {
					e.setCancelled(true);
					if(e.getCurrentItem() != null) {
						if(e.getCurrentItem().hasItemMeta()) {
							if(e.getCurrentItem().getItemMeta().hasDisplayName()) {
								if(e.getCurrentItem().equals(ItemStacks.BLOCKED_EDIT_UNBLOCK.getItem())) {
									PlayerUtilities pu = new PlayerUtilities(p);
									pu.removeBlocked(editing.get(p));
									p.sendMessage(plugin.getString("Messages.Commands.Unblock.Unblock").replace("%PLAYER%", editing.get(p).getName()));
									InventoryBuilder.BLOCKED_INVENTORY(plugin, p);
									return;
								}
								if(e.getCurrentItem().equals(ItemStacks.BLOCKED_EDIT_BACK.getItem())) {
									Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
										public void run() {
											p.closeInventory();
											InventoryBuilder.BLOCKED_INVENTORY(plugin, p);
										}
									}, 2);
									return;
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if(e.getInventory() != null) {
			if(editing.containsKey(p)) {
				if(e.getInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', 
						FileManager.ConfigCfg.getString("Friends.GUI.BlockedEditInv.Title").replace("%PLAYER%", editing.get(p).getName())))) {
					editing.remove(p);
				}
			}
		}
	}

}
