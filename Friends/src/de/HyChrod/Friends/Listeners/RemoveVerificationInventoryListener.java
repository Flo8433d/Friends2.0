/*
*
* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.Listeners;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.SubCommands.Remove_Command;
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.InventoryTypes;
import de.HyChrod.Friends.Utilities.ItemStacks;

public class RemoveVerificationInventoryListener implements Listener {

	private Friends plugin;

	public static HashMap<Player, OfflinePlayer> confirming = new HashMap<>();

	public RemoveVerificationInventoryListener(Friends friends) {
		this.plugin = friends;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {
		final Player p = (Player) e.getWhoClicked();
		if (e.getInventory() != null) {
			if (e.getInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&',
					FileManager.ConfigCfg.getString("Friends.GUI.RemoveVerificationInv.Title")))) {
				e.setCancelled(true);
				if (confirming.containsKey(p)) {
					if (e.getCurrentItem() != null) {
						if (e.getCurrentItem().hasItemMeta()) {
							if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
								final OfflinePlayer toConfirm = confirming.get(p);
								if (e.getCurrentItem().equals(ItemStacks.REMOVEVERIFICATION_CANCLE.getItem())) {
									EditInventoryListener.editing.put(p, toConfirm);
									InventoryBuilder.openInv(p, InventoryBuilder.EDIT_INVENTORY(p, false));
									return;
								}
								if (e.getCurrentItem().equals(ItemStacks.REMOVEVERIFICATION_CONFIRM.getItem())) {
									new Remove_Command(plugin, p, new String[] {"remove", toConfirm.getName()}, new Callback<Boolean>() {
										
										@Override
										public void call(Boolean isSuccessful) {
											if(isSuccessful)
												InventoryBuilder.openInv(p, InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.MAIN, false));
											else
												p.closeInventory();
										}
									});
									return;
								}
							}
						}
					}
				}
			}
		}
	}

}
