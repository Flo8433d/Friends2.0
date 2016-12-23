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

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Util.InventoryBuilder;
import de.HyChrod.Friends.Util.InventoryTypes;
import de.HyChrod.Friends.Util.ItemStacks;
import de.HyChrod.Friends.Util.PlayerUtilities;

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
								PlayerUtilities puT = new PlayerUtilities(toConfirm);
								if (e.getCurrentItem().equals(ItemStacks.REMOVEVERIFICATION_CANCLE.getItem())) {
									EditInventoryListener.editing.put(p, toConfirm);
									InventoryBuilder.openInv(p, InventoryBuilder.EDIT_INVENTORY(p, false));
									return;
								}
								if (e.getCurrentItem().equals(ItemStacks.REMOVEVERIFICATION_CONFIRM.getItem())) {
									PlayerUtilities pu = new PlayerUtilities(p);
									pu.update(toConfirm.getUniqueId().toString(), 0, false);
									puT.update(p.getUniqueId().toString(), 0, false);
									p.sendMessage(plugin.getString("Messages.Commands.Remove.Remove.Remover")
											.replace("%PLAYER%", toConfirm.getName()));
									if (Friends.bungeeMode && !toConfirm.isOnline()) {
										BungeeMessagingListener.sendToBungeeCord(p, "Message", toConfirm.getName(),
												plugin.getString("Messages.Commands.Remove.Remove.ToRemove")
														.replace("%PLAYER%", p.getName()));
									}
									if (toConfirm.isOnline()) {
										Bukkit.getPlayer(toConfirm.getUniqueId()).sendMessage(
												plugin.getString("Messages.Commands.Remove.Remove.ToRemove")
														.replace("%PLAYER%", p.getName()));
									}
									InventoryBuilder.openInv(p, InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.MAIN, false));
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
