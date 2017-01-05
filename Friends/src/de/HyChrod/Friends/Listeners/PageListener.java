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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.SubCommands.AcceptAll_Command;
import de.HyChrod.Friends.Commands.SubCommands.DenyAll_Command;
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.InventoryPage;
import de.HyChrod.Friends.Utilities.InventoryTypes;
import de.HyChrod.Friends.Utilities.ItemStacks;
import de.HyChrod.Friends.Utilities.PlayerUtilities;
import de.HyChrod.Friends.Utilities.UtilitieItems;

public class PageListener implements Listener {

	private Friends plugin;

	public static HashMap<Player, Integer> currentSite = new HashMap<>();

	public PageListener(Friends friends) {
		this.plugin = friends;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) throws Exception {
		final Player p = (Player) e.getWhoClicked();
		if (e.getInventory() != null) {
			InventoryTypes type = checkInventory(e.getInventory().getTitle());
			try {
				type.applyPlayer(p);
			} catch (NullPointerException ex) {}
			
			if (type != null) {
				e.setCancelled(true);
				if (e.getCurrentItem() != null) {
					if (e.getCurrentItem().hasItemMeta()) {
						if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
							if (e.getCurrentItem().equals(((ItemStacks)type.getItems().get(1)).getItem())) {
								Inventory inv = p.getOpenInventory().getTopInventory();
								this.nextPage(p, PlayerUtilities.getUtilities(p.getUniqueId().toString()), inv, type);
								return;
							}
							if (e.getCurrentItem().equals(((ItemStacks)type.getItems().get(2)).getItem())) {

								if (currentSite.containsKey(p)) {
									if (currentSite.get(p) > 0) {
										int page = currentSite.get(p) - 1;
										new InventoryPage(plugin, p, page, PlayerUtilities.getUtilities(p.getUniqueId().toString()), type).open(true);
										currentSite.put(p, page);
										return;
									}
								}
								p.sendMessage(plugin.getString("Messages.GUI" + type.getS() + ".FirstPage"));
								return;
							}
							
							if (e.getCurrentItem().getType().equals(Material.SKULL)
									|| e.getCurrentItem().getType().equals(Material.SKULL_ITEM)
											&& !e.getCurrentItem().getItemMeta().getDisplayName()
													.equals(new UtilitieItems().FRIENDITEM(p).getItemMeta().getDisplayName())) {
								
								String friendsName = e.getCurrentItem().getItemMeta().getDisplayName();
								HashMap<Integer, String[]> toReplace = new HashMap<>();
								toReplace.put(0, FileManager.ConfigCfg.getString("Friends.GUI.FriendHead.NameOnline").split("%PLAYER%"));
								toReplace.put(1, FileManager.ConfigCfg.getString("Friends.GUI.FriendHead.NameOffline").split("%PLAYER%"));
								toReplace.put(2, FileManager.ConfigCfg.getString("Friends.GUI.RequestsInv.PlayerHead.Name").split("%PLAYER%"));
								toReplace.put(3, FileManager.ConfigCfg.getString("Friends.GUI.RequestsInv.PlayerHead.Name").split("%PLAYER%"));
								for(int i = 0; i <= 3; i++) {
									for(int a = 0; a < toReplace.get(i).length; a++) {
										String replaceString = toReplace.get(i)[a];
										friendsName = friendsName.replace(ChatColor.translateAlternateColorCodes('&', replaceString), "");
										
										for(int b = 0; b <= 3; b++) {
											for(int c = 0; c < toReplace.get(b).length; c++) {
												String[] contraString = toReplace.get(b);
												String othersString = contraString[c];											
												if(othersString.contains(replaceString)) {
													othersString = othersString.replace(replaceString, "");
												}	
												contraString[c] = othersString;
												toReplace.put(b, contraString);
											}
										}
									}
								}
								String[] cSplit = friendsName.split("§");
								String serialized = "";
								for(int i = 0; i < cSplit.length; i++) {
									String toAdd = cSplit[i];
									if(cSplit[i].length() >= 1 && (i != 0))
										toAdd = cSplit[i].substring(1, cSplit[i].length());
									serialized = serialized + toAdd;
								}

								if(type.equals(InventoryTypes.MAIN)) {
									EditInventoryListener.editing.put(p, Bukkit.getOfflinePlayer(serialized));
									InventoryBuilder.openInv(p, InventoryBuilder.EDIT_INVENTORY(p, false));
									return;
								}
								
								if (type.equals(InventoryTypes.REQUEST)) {
									RequestEditInventoryListener.editing.put(p, Bukkit.getOfflinePlayer(serialized));
									InventoryBuilder.openInv(p, InventoryBuilder.REQUESTEDIT_INVENTORY(p, false));
								}
								if (type.equals(InventoryTypes.BLOCKED)) {
									BlockedEditInventoryListener.editing.put(p, Bukkit.getOfflinePlayer(serialized));
									InventoryBuilder.openInv(p, InventoryBuilder.BLOCKEDEDIT_INVENOTRY(p, false));
								}
								if (currentSite.containsKey(p)) {
									currentSite.remove(p);
								}
								return;
							}
							
							if(type.getItems().size() >= 5) {
								if(type.equals(InventoryTypes.MAIN)) {
									if(e.getCurrentItem().equals(((ItemStacks)type.getItems().get(3)).getItem())) {
										InventoryBuilder.openInv(p, InventoryBuilder.OPTIONS_INVENTORY(p, false));
										return;
									}
									if(e.getCurrentItem().equals(((ItemStack)type.getItems().get(4)))) {
										InventoryBuilder.openInv(p, InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.BLOCKED, false));
										return;
									}
									if(e.getCurrentItem().equals(((ItemStack)type.getItems().get(5)))) {
										InventoryBuilder.openInv(p, InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.REQUEST, false));
										return;
									}
									return;
								}
								if (e.getCurrentItem().equals(((ItemStacks)type.getItems().get(3)).getItem())) {
									InventoryBuilder.openInv(p, InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.MAIN, false));
									return;
								}
								if(e.getCurrentItem().equals(((ItemStacks)type.getItems().get(4)).getItem())) {
									new AcceptAll_Command(plugin, p, new String[] {"acceptall"}, new Callback<Boolean>() {
										
										@Override
										public void call(Boolean done) {p.closeInventory();}
									});
									return;
								}
								if(e.getCurrentItem().equals(((ItemStacks)type.getItems().get(5)).getItem())) {
									new DenyAll_Command(plugin, p, new String[] {"denyall"}, new Callback<Boolean>() {

										@Override
										public void call(Boolean done) {p.closeInventory();}
									});
									return;
								}
								
							} else {
								if (e.getCurrentItem().equals(((ItemStacks)type.getItems().get(3)).getItem())) {
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

	public InventoryTypes checkInventory(String title) {
		if (title.equals(ChatColor.translateAlternateColorCodes('&',
				FileManager.ConfigCfg.getString("Friends.GUI.RequestsInv.Title"))))
			return InventoryTypes.REQUEST;
		if (title.equals(ChatColor.translateAlternateColorCodes('&',
				FileManager.ConfigCfg.getString("Friends.GUI.BlockedInv.Title"))))
			return InventoryTypes.BLOCKED;
		if(title.equals(ChatColor.translateAlternateColorCodes('&', 
				FileManager.ConfigCfg.getString("Friends.GUI.Title"))))
			return InventoryTypes.MAIN;
		return null;
	}

	private void nextPage(Player player, PlayerUtilities pu, Inventory inv, InventoryTypes type) throws Exception {
		int freeSlots = 0;
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) == null) {
				freeSlots++;
			}
		}
		int page = 1;
		if (currentSite.containsKey(player))
			page = currentSite.get(player) + 1;
		if (freeSlots > 0) {
			player.sendMessage(plugin.getString("Messages.GUI" + type.getS() + ".NoMorePages"));
			new InventoryPage(plugin, player, page, pu, type).open(true);
			currentSite.put(player, page);
			return;
		}
		new InventoryPage(plugin, player, page, pu, type).open(true);
		currentSite.put(player, page);
	}

}
