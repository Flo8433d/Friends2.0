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
import de.HyChrod.Friends.Commands.SubCommands.Accept_Command;
import de.HyChrod.Friends.Commands.SubCommands.Block_Command;
import de.HyChrod.Friends.Commands.SubCommands.Deny_Command;
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.InventoryTypes;
import de.HyChrod.Friends.Utilities.ItemStacks;

public class RequestEditInventoryListener implements Listener {

	private Friends plugin;

	public static HashMap<Player, OfflinePlayer> editing = new HashMap<>();

	public RequestEditInventoryListener(Friends friends) {
		this.plugin = friends;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) throws Exception {
		final Player p = (Player) e.getWhoClicked();
		if (e.getInventory() != null) {

			if (editing.containsKey(p)) {

				if (e.getInventory().getTitle()
						.equals(ChatColor.translateAlternateColorCodes('&',
								FileManager.ConfigCfg.getString("Friends.GUI.RequestEditInv.Title").replace("%PLAYER%",
										editing.get(p).getName())))) {
					e.setCancelled(true);
					if (e.getCurrentItem() != null) {
						if (e.getCurrentItem().hasItemMeta()) {
							if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
								OfflinePlayer inEdit = editing.get(p);
								plugin.pool.execute(new Runnable() {
									
									@Override
									public void run() {
										if (e.getCurrentItem().equals(ItemStacks.REQUEST_EDIT_ACCEPT.getItem())) {
											new Accept_Command(plugin, p, new String[] {"accept", inEdit.getName()}, new Callback<Boolean>() {
												
												@Override
												public void call(Boolean isSuccessful) {
													if(isSuccessful)
														InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.REQUEST, true);
													else
														p.closeInventory();
												}
											});
											return;
										}
										if (e.getCurrentItem().equals(ItemStacks.REQUEST_EDIT_DENY.getItem())) {
											new Deny_Command(plugin, p, new String[] {"deny", inEdit.getName()}, new Callback<Boolean>() {

												@Override
												public void call(Boolean isSuccessful) {
													if(isSuccessful)
														InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.REQUEST, true);
													else
														p.closeInventory();
												}
											});
											return;
										}
										if (e.getCurrentItem().equals(ItemStacks.REQUEST_EDIT_BLOCK.getItem())) {
											new Block_Command(plugin, p, new String[] {"block", inEdit.getName()}, new Callback<Boolean>() {

												@Override
												public void call(Boolean isSuccessful) {
													if(isSuccessful)
														InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.REQUEST, true);
													else
														p.closeInventory();
												}
											});
											return;
										}
										if (e.getCurrentItem().equals(ItemStacks.REQUEST_EDIT_BACK.getItem())) {
											InventoryBuilder.openInv(p,
													InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.REQUEST, false));
											return;
										}
									}
								});
							}
						}
					}
				}
			}
		}
	}

}
