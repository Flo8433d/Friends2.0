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
import de.HyChrod.Friends.Commands.SubCommands.Unblock_Command;
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.InventoryTypes;
import de.HyChrod.Friends.Utilities.ItemStacks;

public class BlockedEditInventoryListener implements Listener {

	private Friends plugin;

	public static HashMap<Player, OfflinePlayer> editing = new HashMap<>();

	public BlockedEditInventoryListener(Friends friends) {
		this.plugin = friends;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) throws Exception {
		final Player p = (Player) e.getWhoClicked();
		if (e.getInventory() != null) {
			if (editing.containsKey(p)) {
				if (e.getInventory().getTitle()
						.equals(ChatColor.translateAlternateColorCodes('&',
								FileManager.ConfigCfg.getString("Friends.GUI.BlockedEditInv.Title").replace("%PLAYER%",
										editing.get(p).getName())))) {
					e.setCancelled(true);
					if (e.getCurrentItem() != null) {
						if (e.getCurrentItem().hasItemMeta()) {
							if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
								if (e.getCurrentItem().equals(ItemStacks.BLOCKED_EDIT_UNBLOCK.getItem())) {
									try {
										new Unblock_Command(plugin, p, new String[] { "unblock", editing.get(p).getName() },
												new Callback<Boolean>() {

													@Override
													public void call(Boolean isSuccessful) {
														if (isSuccessful)
															InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.BLOCKED,
																	true);
														else
															p.closeInventory();
													}
												});
									} catch (Exception ex) {ex.printStackTrace();}
									p.closeInventory();
									return;
								}
								if (e.getCurrentItem().equals(ItemStacks.BLOCKED_EDIT_BACK.getItem())) {
									InventoryBuilder.openInv(p,
											InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.BLOCKED, false));
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	public static boolean simplyNameCheck(Friends friends) {
		if (!friends.getDescription().getAuthors().get(0).equals("HyChrod")) {
			Bukkit.getConsoleSender()
					.sendMessage(friends.prefix + " §cYou're using an unofficial version of this plugin");
			Bukkit.getConsoleSender().sendMessage(friends.prefix + " §cSomeone changed the authors name!");
			Bukkit.getConsoleSender().sendMessage(friends.prefix + " §cThe official author is: HyChrod");
			friends.getServer().getPluginManager().disablePlugin(friends);
			return false;
		}
		return true;
	}

}
