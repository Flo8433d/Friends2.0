/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Util.UtilitieItems;

public class ChangeWorldListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldChange(PlayerChangedWorldEvent e) {
		String from = e.getFrom().getName();
		String to = e.getPlayer().getWorld().getName();
		Player p = e.getPlayer();

		if (FileManager.ConfigCfg.getBoolean("Friends.FriendItem.GiveOnJoin")) {
			if(FileManager.ConfigCfg.getBoolean("Friends.EnabledWorlds.Enable")) {
				if(FileManager.ConfigCfg.getStringList("Friends.EnabledWorlds.Worlds").contains(to) 
						&& !FileManager.ConfigCfg.getString("Friends.EnabledWorlds.Worlds").contains(from)) {
					p.getInventory().setItem(FileManager.ConfigCfg.getInt("Friends.FriendItem.InventorySlot") - 1,
							new UtilitieItems().FRIENDITEM(p));
					return;
				}
				if(!FileManager.ConfigCfg.getStringList("Friends.EnabledWorlds.Worlds").contains(to)
						&& FileManager.ConfigCfg.getStringList("Friends.EnabledWorlds.Worlds").contains(from)) {
					clear(p);
					return;
				}
				
			}
			
			if(!FileManager.ConfigCfg.getBoolean("Friends.DisabledWorlds.Enable")) return;
			if (FileManager.ConfigCfg.getStringList("Friends.DisabledWorlds.Worlds").contains(from)
					&& !FileManager.ConfigCfg.getString("Friends.DisabledWorlds.Worlds").contains(to)) {
				p.getInventory().setItem(FileManager.ConfigCfg.getInt("Friends.FriendItem.InventorySlot") - 1,
						new UtilitieItems().FRIENDITEM(p));
				return;
			}
			if (!FileManager.ConfigCfg.getStringList("Friends.DisabledWorlds.Worlds").contains(from)
					&& FileManager.ConfigCfg.getString("Friends.DisabledWorlds.Worlds").contains(to)) {
				clear(p);
				return;
			}
		}
	}
	
	public void clear(Player p) {
		for (int i = 0; i < p.getInventory().getSize(); i++) {
			if (p.getInventory().getItem(i) != null) {
				if (p.getInventory().getItem(i).hasItemMeta()) {
					if (p.getInventory().getItem(i).getItemMeta().hasDisplayName()) {
						if (p.getInventory().getItem(i).getItemMeta().getDisplayName()
								.equals(new UtilitieItems().FRIENDITEM(p).getItemMeta().getDisplayName())) {
							p.getInventory().setItem(i, new ItemStack(Material.AIR));
						}
					}
				}
			}
		}
	}

}
