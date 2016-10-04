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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Util.InventoryBuilder;
import de.HyChrod.Friends.Util.InventoryPage;
import de.HyChrod.Friends.Util.ItemStacks;
import de.HyChrod.Friends.Util.PlayerUtilities;

public class MainInventoryListener implements Listener {

	private Friends plugin;

	private static HashMap<Player, Integer> currentSite = new HashMap<>();

	public MainInventoryListener(Friends friends) {
		this.plugin = friends;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();

		if (e.getInventory() != null) {
			if (e.getInventory().getTitle() != null) {
				if (e.getInventory().getTitle().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
						FileManager.ConfigCfg.getString("Friends.GUI.Title")))) {
					e.setCancelled(true);

					if (e.getCurrentItem() != null) {
						if (e.getCurrentItem().hasItemMeta()) {
							if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
								if (e.getCurrentItem().equals(ItemStacks.MAIN_NEXTPAGEITEM.getItem())) {
									PlayerUtilities pu = new PlayerUtilities(p);
									Inventory inv = p.getOpenInventory().getTopInventory();
									this.nextPage(p, pu, inv);
									return;
								}
								if (e.getCurrentItem().equals(ItemStacks.MAIN_PREVIOUSPAGEITEM.getItem())) {
									PlayerUtilities pu = new PlayerUtilities(p);

									if (currentSite.containsKey(p)) {
										if (currentSite.get(p) > 0) {
											int page = currentSite.get(p) - 1;
											new InventoryPage(plugin, p, page, pu).open();
											currentSite.put(p, page);
											return;
										}
									}
									p.sendMessage(plugin.getString("Messages.GUI.FirstPage"));
									return;
								}
								if (e.getCurrentItem().getItemMeta().getDisplayName()
										.equals(ItemStacks.MAIN_REQUESTS(new PlayerUtilities(p).getRequests().size())
												.getItemMeta().getDisplayName())) {
									InventoryBuilder.REQUESTS_INVENTORY(plugin, p);
									return;
								}
								if (e.getCurrentItem().getItemMeta().getDisplayName()
										.equals(ItemStacks.MAIN_BLOCKED(new PlayerUtilities(p).getBlocked().size())
												.getItemMeta().getDisplayName())) {
									InventoryBuilder.BLOCKED_INVENTORY(plugin, p);
									return;
								}
								if (e.getCurrentItem().equals(ItemStacks.MAIN_OPTIONSITEM.getItem())) {
									InventoryBuilder.OPTIONS_INVENTORY(p);
									return;
								}
								if (e.getCurrentItem().getType().equals(Material.SKULL)
										|| e.getCurrentItem().getType().equals(Material.SKULL_ITEM)
												&& !e.getCurrentItem().getItemMeta().getDisplayName().equals(
														ItemStacks.FRIENDITEM(p).getItemMeta().getDisplayName())) {
									String friendsName = e.getCurrentItem().getItemMeta().getDisplayName()
											.replace(" §7(§aOnline§7)", "").replace(" §7(§8Offline§7)", "");
									EditInventoryListener.editing.put(p, Bukkit.getOfflinePlayer(friendsName));
									InventoryBuilder.EDIT_INVENTORY(p);
									if (currentSite.containsKey(p)) {
										currentSite.remove(p);
									}
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

		if (e.getInventory() != null) {
			if (e.getInventory().getTitle().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
					FileManager.ConfigCfg.getString("Friends.GUI.Title")))) {
				if (currentSite.containsKey(p)) {
					currentSite.remove(p);
				}
			}
		}
	}

	private void nextPage(Player player, PlayerUtilities pu, Inventory inv) {
		int freeSlots = 0;
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) == null) {
				freeSlots++;
			}
		}
		if (freeSlots > 0) {
			player.sendMessage(plugin.getString("Messages.GUI.NoMorePages"));
			return;
		}
		int page = 1;
		if (currentSite.containsKey(player))
			page = currentSite.get(player) + 1;
		new InventoryPage(plugin, player, page, pu).open();
		currentSite.put(player, page);
	}

}
