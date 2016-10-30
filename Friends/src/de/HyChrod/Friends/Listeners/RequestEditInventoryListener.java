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
								PlayerUtilities puT = new PlayerUtilities(inEdit);
								PlayerUtilities puP = new PlayerUtilities(p);
								if (e.getCurrentItem().equals(ItemStacks.REQUEST_EDIT_ACCEPT.getItem())) {
									if (puP.get(0, true).size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit")) {
										if (!p.hasPermission("Friends.ExtraFriends") || puP.get(0, true).size() 
												> FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
											p.sendMessage(
													plugin.getString("Messages.Commands.Accept.LimitReached.Accepter"));
											return;
										}
									}
									if (puT.get(0, true).size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit")) {
										if(inEdit.isOnline()) {
											Player toCheck = Bukkit.getPlayer(inEdit.getName());
											if (!toCheck.hasPermission("Friends.ExtraFriends") || puT.get(0, true).size() 
													> FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
												p.sendMessage(plugin.getString("Messages.Commands.Accept.LimitReached.Requester"));
												return;
											}
										}
									}

									puP.update(inEdit.getUniqueId().toString(), 0, true);
									puT.update(p.getUniqueId().toString(), 0, true);
									puP.update(inEdit.getUniqueId().toString(), 1, false);
									p.sendMessage(plugin.getString("Messages.Commands.Accept.Accept.Accepter")
											.replace("%PLAYER%", inEdit.getName()));
									if (Friends.bungeeMode && !inEdit.isOnline()) {
										BungeeMessagingListener.sendToBungeeCord(p, "Message", inEdit.getName(),
												plugin.getString("Messages.Commands.Accept.Accept.ToAccept")
														.replace("%PLAYER%", p.getName()));
									}
									if (inEdit.isOnline()) {
										Bukkit.getPlayer(inEdit.getUniqueId()).sendMessage(
												plugin.getString("Messages.Commands.Accept.Accept.ToAccept")
														.replace("%PLAYER%", p.getName()));
									}
									InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.REQUEST, true);
									return;
								}
								if (e.getCurrentItem().equals(ItemStacks.REQUEST_EDIT_DENY.getItem())) {
									puP.update(inEdit.getUniqueId().toString(), 1, false);
									p.sendMessage(plugin.getString("Messages.Commands.Deny.Deny.Denier")
											.replace("%PLAYER%", inEdit.getName()));
									if (Friends.bungeeMode && !inEdit.isOnline()) {
										BungeeMessagingListener.sendToBungeeCord(p, "Message", inEdit.getName(),
												plugin.getString("Messages.Commands.Deny.Deny.ToDeny")
														.replace("%PLAYER%", p.getName()));
									}
									if (inEdit.isOnline()) {
										Bukkit.getPlayer(inEdit.getUniqueId())
												.sendMessage(plugin.getString("Messages.Commands.Deny.Deny.ToDeny")
														.replace("%PLAYER%", p.getName()));
									}
									InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.REQUEST, true);
									return;
								}
								if (e.getCurrentItem().equals(ItemStacks.REQUEST_EDIT_BLOCK.getItem())) {
									if (inEdit.isOnline() && puP.get(0, false).contains(inEdit.getUniqueId().toString())) {
										Bukkit.getPlayer(inEdit.getUniqueId())
												.sendMessage(plugin.getString("Messages.Commands.Block.Block.ToBlock")
														.replace("%PLAYER%", p.getName()));
									}

									puP.update(inEdit.getUniqueId().toString(), 2, true);
									puP.update(inEdit.getUniqueId().toString(), 0, false);
									puP.update(inEdit.getUniqueId().toString(), 1, false);
									puT.update(p.getUniqueId().toString(), 0, false);
									puT.update(p.getUniqueId().toString(), 1, false);

									p.sendMessage(plugin.getString("Messages.Commands.Block.Block.Blocker")
											.replace("%PLAYER%", inEdit.getName()));
									InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.REQUEST, true);
									return;
								}
								if (e.getCurrentItem().equals(ItemStacks.REQUEST_EDIT_BACK.getItem())) {
									InventoryBuilder.openInv(p,
											InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.REQUEST, false));
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
