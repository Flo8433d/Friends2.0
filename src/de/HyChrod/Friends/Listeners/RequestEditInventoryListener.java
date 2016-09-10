/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Listeners;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Util.InventoryBuilder;
import de.HyChrod.Friends.Util.ItemStacks;
import de.HyChrod.Friends.Util.PlayerUtilities;

public class RequestEditInventoryListener implements Listener {

	private Friends plugin;
	private FileManager mgr = new FileManager();
	private FileConfiguration cfg = this.mgr.getConfig("", "config.yml");
	
	public static HashMap<Player, OfflinePlayer> editing = new HashMap<>();
	
	public RequestEditInventoryListener(Friends friends) {
		this.plugin = friends;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {
		final Player p = (Player) e.getWhoClicked();
		if(e.getInventory() != null) {
			if(editing.containsKey(p)) {
				if(e.getInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', 
						this.cfg.getString("Friends.GUI.RequestEditInv.Title").replace("%PLAYER%", editing.get(p).getName())))) {
					e.setCancelled(true);
					if(e.getCurrentItem() != null) {
						if(e.getCurrentItem().hasItemMeta()) {
							if(e.getCurrentItem().getItemMeta().hasDisplayName()) {
								OfflinePlayer inEdit = editing.get(p);
								PlayerUtilities puT = new PlayerUtilities(inEdit);
								PlayerUtilities puP = new PlayerUtilities(p);
								if(e.getCurrentItem().equals(ItemStacks.REQUEST_EDIT_ACCEPT.getItem())) {
									if(puP.getFriends().size() > this.cfg.getInt("Friends.Options.FriendLimit")) {
										if(!p.hasPermission("Friends.ExtraFriends") || puP.getFriends().size() > this.cfg.getInt("Friends.Options.FriendLimit+")) {
											p.sendMessage(plugin.getString("Messages.Commands.Accept.LimitReached.Accepter"));
											return;
										}
									}
									if(puT.getFriends().size() > this.cfg.getInt("Friends.Options.FriendLimit")) {
										if(!p.hasPermission("Friends.ExtraFriends") || puT.getFriends().size() > this.cfg.getInt("Friends.Options.FriendLimit+")) {
											p.sendMessage(plugin.getString("Messages.Commands.Accept.LimitReached.Requester"));
											return;
										}
									}
									
									puP.addFriend(inEdit);
									puT.addFriend(p);
									puP.removeRequest(inEdit);
									p.sendMessage(plugin.getString("Messages.Commands.Accept.Accept.Accepter").replace("%PLAYER%", inEdit.getName()));
									if(Friends.bungeeMode && !inEdit.isOnline()) {
										ByteArrayOutputStream b = new ByteArrayOutputStream();
										DataOutputStream out = new DataOutputStream(b);	
										try {
											out.writeUTF("Message");
											out.writeUTF(inEdit.getName());
											out.writeUTF(plugin.getString("Messages.Commands.Accept.Accept.ToAccept")
													.replace("%PLAYER%", p.getName()));
										} catch (IOException ex) {
										}
										p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
									}
									if(inEdit.isOnline()) {
										Bukkit.getPlayer(inEdit.getUniqueId()).sendMessage(plugin.getString("Messages.Commands.Accept.Accept.ToAccept")
												.replace("%PLAYER%", p.getName()));
									}
									InventoryBuilder.REQUESTS_INVENTORY(plugin, p);
									return;
								}
								if(e.getCurrentItem().equals(ItemStacks.REQUEST_EDIT_DENY.getItem())) {
									puP.removeRequest(inEdit);
									p.sendMessage(plugin.getString("Messages.Commands.Deny.Deny.Denier").replace("%PLAYER%", inEdit.getName()));
									if(Friends.bungeeMode && !inEdit.isOnline()) {
										ByteArrayOutputStream b = new ByteArrayOutputStream();
										DataOutputStream out = new DataOutputStream(b);	
										try {
											out.writeUTF("Message");
											out.writeUTF(inEdit.getName());
											out.writeUTF(plugin.getString("Messages.Commands.Deny.Deny.ToDeny")
													.replace("%PLAYER%", p.getName()));
										} catch (IOException ex) {
										}
										p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
									}
									if(inEdit.isOnline()) {
										Bukkit.getPlayer(inEdit.getUniqueId()).sendMessage(plugin.getString("Messages.Commands.Deny.Deny.ToDeny")
												.replace("%PLAYER%", p.getName()));
									}
									InventoryBuilder.REQUESTS_INVENTORY(plugin, p);
									return;
								}
								if(e.getCurrentItem().equals(ItemStacks.REQUEST_EDIT_BLOCK.getItem())) {
									if(inEdit.isOnline() && puP.getFriends().contains(inEdit)) {
										Bukkit.getPlayer(inEdit.getUniqueId()).sendMessage(plugin.getString("Messages.Commands.Block.Block.ToBlock")
												.replace("%PLAYER%", p.getName()));
									}
									
									puP.addBlocked(inEdit);
									puP.removeFriend(inEdit);
									puP.removeRequest(inEdit);
									puT.removeFriend(p);
									puT.removeRequest(p);
									
									p.sendMessage(plugin.getString("Messages.Commands.Block.Block.Blocker").replace("%PLAYER%", inEdit.getName()));
									InventoryBuilder.REQUESTS_INVENTORY(plugin, p);
									return;
								}
								if(e.getCurrentItem().equals(ItemStacks.REQUEST_EDIT_BACK.getItem())) {
									Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
										
										@Override
										public void run() {
											p.closeInventory();
											InventoryBuilder.REQUESTS_INVENTORY(plugin, p);
										}
									}, 2);
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
		if(e.getInventory() != null) {
			if(editing.containsKey(p)) {
				if(e.getInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', 
						this.cfg.getString("Friends.GUI.RequestEditInv.Title").replace("%PLAYER%", editing.get(p).getName())))) {
					editing.remove(p);
				}
			}
		}
	}
	
}
