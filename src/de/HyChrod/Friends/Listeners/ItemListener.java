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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Util.UtilitieItems;

public class ItemListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if (FileManager.ConfigCfg.getBoolean("Friends.Options.CanDropItem"))
			return;

		if (e.getItemDrop() != null) {
			if (e.getItemDrop().getItemStack().hasItemMeta()) {
				if (e.getItemDrop().getItemStack().getItemMeta().hasDisplayName()) {
					if (e.getItemDrop().getItemStack().getItemMeta().getDisplayName()
							.equals(new UtilitieItems().FRIENDITEM(p).getItemMeta().getDisplayName())) {
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoyClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (FileManager.ConfigCfg.getBoolean("Friends.Options.CanMoveItem"))
			return;
		if (e.getCurrentItem() != null) {
			if (e.getCurrentItem().hasItemMeta()) {
				if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
					if (e.getCurrentItem().getItemMeta().getDisplayName()
							.equals(new UtilitieItems().FRIENDITEM(p).getItemMeta().getDisplayName())) {
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlce(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (FileManager.ConfigCfg.getBoolean("Friends.Options.CanPlaceItem"))
			return;
		if (e.getItemInHand() != null) {
			if (e.getItemInHand().hasItemMeta()) {
				if (e.getItemInHand().getItemMeta().hasDisplayName()) {
					if (e.getItemInHand().getItemMeta().getDisplayName()
							.equals(new UtilitieItems().FRIENDITEM(p).getItemMeta().getDisplayName())) {
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if (FileManager.ConfigCfg.getBoolean("Friends.Options.CanDropItem"))
			return;

		for (int i = 0; i < e.getDrops().size(); i++) {
			if (e.getDrops().get(i) != null) {
				if (e.getDrops().get(i).hasItemMeta()) {
					if (e.getDrops().get(i).getItemMeta().hasDisplayName()) {
						if (e.getDrops().get(i).getItemMeta().getDisplayName()
								.equals(new UtilitieItems().FRIENDITEM(p).getItemMeta().getDisplayName())) {
							e.getDrops().remove(i);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		if (FileManager.ConfigCfg.getBoolean("Friends.FriendItem.GiveOnJoin")) {
			if (FileManager.ConfigCfg.getStringList("Friends.DisabledWorlds").contains(p.getWorld().getName()))
				return;
			for (int i = 0; i < p.getInventory().getSize(); i++) {
				if (p.getInventory().getItem(i) != null) {
					if (p.getInventory().getItem(i).hasItemMeta()) {
						if (p.getInventory().getItem(i).getItemMeta().hasDisplayName()) {
							if (p.getInventory().getItem(i).getItemMeta().getDisplayName()
									.equals(new UtilitieItems().FRIENDITEM(p).getItemMeta().getDisplayName())) {
								return;
							}
						}
					}
				}
			}
			p.getInventory().setItem(FileManager.ConfigCfg.getInt("Friends.FriendItem.InventorySlot"),
					new UtilitieItems()	.FRIENDITEM(p));
		}
	}

}
