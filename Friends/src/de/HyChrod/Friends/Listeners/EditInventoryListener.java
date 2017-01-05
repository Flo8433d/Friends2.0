/*
*
* This class was made by HyChrod
* All rights reserved, 2017
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

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.SubCommands.Jump_Command;
import de.HyChrod.Friends.Commands.SubCommands.Remove_Command;
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.InventoryTypes;
import de.HyChrod.Friends.Utilities.ItemStacks;

public class EditInventoryListener implements Listener {

	private Friends plugin;

	public static HashMap<Player, OfflinePlayer> editing = new HashMap<>();

	public EditInventoryListener(Friends friends) {
		this.plugin = friends;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {
		final Player p = (Player) e.getWhoClicked();
		if (e.getInventory() != null) {
			if (editing.containsKey(p)) {
				if (e.getInventory().getTitle()
						.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
								FileManager.ConfigCfg.getString("Friends.GUI.FriendEditInv.Title").replace("%FRIEND%",
										editing.get(p).getName())))) {
					e.setCancelled(true);
					if (e.getCurrentItem() != null) {
						if (e.getCurrentItem().hasItemMeta()) {
							if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
								final OfflinePlayer editPlayer = editing.get(p);
								if (e.getCurrentItem().equals(ItemStacks.EDIT_JUMP.getItem())) {
									if (!FileManager.ConfigCfg.getBoolean("Friends.Options.EnableJumping"))
										return;
									
									new Jump_Command(plugin, p, new String[] {"jump",editPlayer.getName()}, new Callback<Boolean>() {
										
										@Override
										public void call(Boolean isSuccessfull) {
											p.closeInventory();
										}
									});
									return;
								}
								if(e.getCurrentItem().equals(ItemStacks.EDIT_PARTY.getItem())) {
									Bukkit.getScheduler().runTaskLaterAsynchronously(Friends.getInstance(), new Runnable() {
										public void run() {
											BungeeMessagingListener.sendToBungeeCord(p, "InvitePlayer::" + editPlayer.getName() + "@" + p.getName(), p.getName(), null);
										}
									}, 5);
									p.closeInventory();
									return;
								}
								
								if (e.getCurrentItem().equals(ItemStacks.EDIT_REMOVE.getItem())) {
									if (FileManager.ConfigCfg.getBoolean("Friends.Options.RemoveVerification")) {
										RemoveVerificationInventoryListener.confirming.put(p, editPlayer);
										InventoryBuilder.REMOVE_VERIFICATION_INVENTORY(p, true);
										return;
									}
									new Remove_Command(plugin, p, new String[] {"remove", editPlayer.getName()}, new Callback<Boolean>() {

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
								if (e.getCurrentItem().equals(ItemStacks.EDIT_BACK.getItem())) {
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
