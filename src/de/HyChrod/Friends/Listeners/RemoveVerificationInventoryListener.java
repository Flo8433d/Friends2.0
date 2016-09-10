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

public class RemoveVerificationInventoryListener implements Listener {

	private Friends plugin;
	private FileManager mgr = new FileManager();
	private FileConfiguration cfg = this.mgr.getConfig("", "config.yml");
	
	public static HashMap<Player, OfflinePlayer> confirming = new HashMap<>();
	
	public RemoveVerificationInventoryListener(Friends friends) {
		this.plugin = friends;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {
		final Player p = (Player) e.getWhoClicked();
		if(e.getInventory() != null) {
			if(e.getInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("Friends.GUI.RemoveVerificationInv.Title")))) {
				e.setCancelled(true);
				if(confirming.containsKey(p)) {
					if(e.getCurrentItem() != null) {
						if(e.getCurrentItem().hasItemMeta()) {
							if(e.getCurrentItem().getItemMeta().hasDisplayName()) {
								final OfflinePlayer toConfirm = confirming.get(p);
								PlayerUtilities puT = new PlayerUtilities(toConfirm);
								if(e.getCurrentItem().equals(ItemStacks.REMOVEVERIFICATION_CANCLE.getItem())) {
									Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
										
										@Override
										public void run() {
											p.closeInventory();
											EditInventoryListener.editing.put(p, toConfirm);
											InventoryBuilder.EDIT_INVENTORY(p);
										}
									}, 2);
									return;
								}
								if(e.getCurrentItem().equals(ItemStacks.REMOVEVERIFICATION_CONFIRM.getItem())) {
									PlayerUtilities pu = new PlayerUtilities(p);
									pu.removeFriend(toConfirm);
									puT.removeFriend(p);
									p.sendMessage(plugin.getString("Messages.Commands.Remove.Remove.Remover").replace("%PLAYER%", toConfirm.getName()));
									if(Friends.bungeeMode && !toConfirm.isOnline()) {
										ByteArrayOutputStream b = new ByteArrayOutputStream();
										DataOutputStream out = new DataOutputStream(b);	
										try {
											out.writeUTF("Message");
											out.writeUTF(toConfirm.getName());
											out.writeUTF(plugin.getString("Messages.Commands.Remove.Remove.ToRemove")
													.replace("%PLAYER%", p.getName()));
										} catch (IOException ex) {
										}
										p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
									}
									if(toConfirm.isOnline()) {
										Bukkit.getPlayer(toConfirm.getUniqueId()).sendMessage(plugin.getString("Messages.Commands.Remove.Remove.ToRemove")
												.replace("%PLAYER%", p.getName()));
									}
									InventoryBuilder.MAIN_INVENTORY(plugin, p);
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
			if(e.getInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("Friends.GUI.RemoveVerificationInv.Title")))) {
				if(confirming.containsKey(p)) {
					confirming.remove(p);
				}
			}
		}
	}
	
}
