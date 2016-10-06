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
import org.bukkit.inventory.Inventory;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Util.InventoryBuilder;
import de.HyChrod.Friends.Util.InventoryPage;
import de.HyChrod.Friends.Util.InventoryTypes;
import de.HyChrod.Friends.Util.ItemStacks;
import de.HyChrod.Friends.Util.PlayerUtilities;

public class MainInventoryListener implements Listener {

	private Friends plugin;

	public static HashMap<Player, Integer> currentSite = new HashMap<>();

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
											new InventoryPage(plugin, p, page, pu).open(true);
											currentSite.put(p, page);
											return;
										}
									}
									p.sendMessage(plugin.getString("Messages.GUI.FirstPage"));
									return;
								}
								if (e.getCurrentItem().getItemMeta().getDisplayName()
										.equals(ItemStacks.MAIN_REQUESTS(new PlayerUtilities(p).get(1).size())
												.getItemMeta().getDisplayName())) {
									InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.REQUEST, true);
									return;
								}
								if (e.getCurrentItem().getItemMeta().getDisplayName()
										.equals(ItemStacks.MAIN_BLOCKED(new PlayerUtilities(p).get(2).size())
												.getItemMeta().getDisplayName())) {
									InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.BLOCKED, true);
									return;
								}
								if (e.getCurrentItem().equals(ItemStacks.MAIN_OPTIONSITEM.getItem())) {
									InventoryBuilder.OPTIONS_INVENTORY(p, true);
									return;
								}
								if (e.getCurrentItem().getType().equals(Material.SKULL)
										|| e.getCurrentItem().getType().equals(Material.SKULL_ITEM)
												&& !e.getCurrentItem().getItemMeta().getDisplayName().equals(
														ItemStacks.FRIENDITEM(p).getItemMeta().getDisplayName())) {
									String friendsName = e.getCurrentItem().getItemMeta().getDisplayName()
											.replace(" §7(§aOnline§7)", "").replace(" §7(§8Offline§7)", "");
									EditInventoryListener.editing.put(p, Bukkit.getOfflinePlayer(friendsName));
									InventoryBuilder.EDIT_INVENTORY(p, true);
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
		new InventoryPage(plugin, player, page, pu).open(true);
		currentSite.put(player, page);
	}

}
