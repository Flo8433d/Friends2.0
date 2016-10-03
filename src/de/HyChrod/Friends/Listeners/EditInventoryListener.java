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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.BungeeSQL_Manager;
import de.HyChrod.Friends.Util.InventoryBuilder;
import de.HyChrod.Friends.Util.ItemStacks;
import de.HyChrod.Friends.Util.PlayerUtilities;

public class EditInventoryListener implements Listener {

	private Friends plugin;
	
	public static HashMap<Player, OfflinePlayer> editing = new HashMap<>();
	
	public EditInventoryListener(Friends friends) {
		this.plugin = friends;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {
		final Player p = (Player) e.getWhoClicked();
		if(e.getInventory() != null) {
			if(editing.containsKey(p)) {
				if(e.getInventory().getTitle().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', 
						FileManager.ConfigCfg.getString("Friends.GUI.FriendEditInv.Title").replace("%FRIEND%", editing.get(p).getName())))) {
					e.setCancelled(true);
					if(e.getCurrentItem() != null) {
						if(e.getCurrentItem().hasItemMeta()) {
							if(e.getCurrentItem().getItemMeta().hasDisplayName()) {
								OfflinePlayer editPlayer = editing.get(p);
								PlayerUtilities puT = new PlayerUtilities(editPlayer);
								
								if(e.getCurrentItem().equals(ItemStacks.EDIT_JUMP.getItem())) {
									
									if(!FileManager.ConfigCfg.getBoolean("Friends.Options.EnableJumping")) return;
									
									if(Friends.bungeeMode) {
										
										if(puT.getOptions().contains("option_noJumping")) {
											p.sendMessage(plugin.getString("Messages.Commands.Jumping.Disabled"));
											return;
										}
										
										if(!BungeeSQL_Manager.isOnline(editPlayer.getUniqueId().toString())) {
											p.sendMessage(plugin.getString("Messages.Commands.Jumping.PlayerOffline"));
										}
										String server = BungeeSQL_Manager.getServer(editPlayer.getUniqueId().toString());
										ByteArrayDataOutput out = ByteStreams.newDataOutput();
										try {
											out.writeUTF("Connect");
											out.writeUTF(server);
										} catch (Exception ee) {
											ee.printStackTrace();
										}
										p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
										return;
									}
									if(!editPlayer.isOnline()) {
										p.sendMessage(plugin.getString("Messages.Commands.Jumping.PlayerOffline"));
										return;
									}
									if(puT.getOptions().contains("option_noJumping")) {
										p.sendMessage(plugin.getString("Messages.Commands.Jumping.Disabled"));
										return;
									}
									Player toJump = Bukkit.getPlayer(editPlayer.getUniqueId());
									
									p.teleport(toJump);
									p.sendMessage(plugin.getString("Messages.Commands.Jumping.Jump.Jumper").replace("%PLAYER%", editPlayer.getName()));
									toJump.sendMessage(plugin.getString("Messages.Commands.Jumping.Jump.ToJump").replace("%PLAYER%", p.getName()));
									p.closeInventory();
									return;
								}
								if(e.getCurrentItem().equals(ItemStacks.EDIT_REMOVE.getItem())) {
									if(FileManager.ConfigCfg.getBoolean("Friends.Options.RemoveVerification")) {
										RemoveVerificationInventoryListener.confirming.put(p, editPlayer);
										InventoryBuilder.REMOVE_VERIFICATION_INVENTORY(p);
										return;
									}
									PlayerUtilities pu = new PlayerUtilities(p);
									pu.removeFriend(editPlayer, false);
									puT.removeFriend(p, false);
									p.sendMessage(plugin.getString("Messages.Commands.Remove.Remove.Remover").replace("%PLAYER%", editPlayer.getName()));
									if(Friends.bungeeMode && !editPlayer.isOnline()) {
										ByteArrayOutputStream b = new ByteArrayOutputStream();
										DataOutputStream out = new DataOutputStream(b);	
										try {
											out.writeUTF("Message");
											out.writeUTF(editPlayer.getName());
											out.writeUTF(plugin.getString("Messages.Commands.Remove.Remove.ToRemove")
													.replace("%PLAYER%", p.getName()));
										} catch (IOException ex) {
										}
										p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
									}
									if(editPlayer.isOnline()) {
										Bukkit.getPlayer(editPlayer.getUniqueId()).sendMessage(plugin.getString("Messages.Commands.Remove.Remove.ToRemove")
												.replace("%PLAYER%", p.getName()));
									}
									this.openMainInv(p);
									return;
								}
								if(e.getCurrentItem().equals(ItemStacks.EDIT_BACK.getItem())) {
									this.openMainInv(p);
									return;
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void openMainInv(final Player p) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				p.closeInventory();
				InventoryBuilder.MAIN_INVENTORY(plugin, p);
			}
		}, 2);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if(e.getInventory() != null) {
			if(editing.containsKey(p)) {
				if(e.getInventory().getTitle().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', 
						FileManager.ConfigCfg.getString("Friends.GUI.FriendEditInv.Title").replace("%FRIEND%", editing.get(p).getName())))) {
					editing.remove(p);
				}
			}
		}
	}
	
}
