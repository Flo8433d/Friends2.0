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

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.BungeeSQL_Manager;
import de.HyChrod.Friends.Util.InventoryBuilder;
import de.HyChrod.Friends.Util.InventoryTypes;
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
								PlayerUtilities puT = new PlayerUtilities(editPlayer);

								if (e.getCurrentItem().equals(ItemStacks.EDIT_JUMP.getItem())) {

									if (!FileManager.ConfigCfg.getBoolean("Friends.Options.EnableJumping"))
										return;

									if (Friends.bungeeMode) {

										if (puT.get(3, false).contains("option_noJumping")) {
											p.sendMessage(plugin.getString("Messages.Commands.Jumping.Disabled"));
											return;
										}

										if (!BungeeSQL_Manager.isOnline(editPlayer)) {
											p.sendMessage(plugin.getString("Messages.Commands.Jumping.PlayerOffline"));
										}
										String server = String.valueOf(BungeeSQL_Manager.get(editPlayer, "SERVER"));
										if(FileManager.ConfigCfg.getBoolean("Friends.DisabledServers.Enable"))
											if(FileManager.ConfigCfg.getStringList("Friends.DisabledServers.Servers").contains(server)) {
												p.sendMessage(plugin.getString("Messages.Commands.Jumping.DisabledServer"));
												return;
											}
										if(FileManager.ConfigCfg.getBoolean("Friends.EnabledServers.Enable"))
											if(!FileManager.ConfigCfg.getStringList("Friends.EnabledServers.Servers").contains(server)) {
												p.sendMessage(plugin.getString("Messages.Commands.Jumping.DisabledServer"));
												return;
											}
										BungeeMessagingListener.sendToBungeeCord(p, "Connect", server, null);
										return;
									}
									if (!editPlayer.isOnline()) {
										p.sendMessage(plugin.getString("Messages.Commands.Jumping.PlayerOffline"));
										return;
									}
									if (puT.get(3, false).contains("option_noJumping")) {
										p.sendMessage(plugin.getString("Messages.Commands.Jumping.Disabled"));
										return;
									}
									Player toJump = Bukkit.getPlayer(editPlayer.getUniqueId());
									if(FileManager.ConfigCfg.getBoolean("Friends.DisabledWorlds.Enable")) {
										if(FileManager.ConfigCfg.getStringList("Friends.DisabledWorlds.Worlds").contains(toJump.getWorld().getName())) {
											p.sendMessage(plugin.getString("Messages.Commands.Jumping.DisabledWorld"));
											return;
										}
									}
									
									p.teleport(toJump);
									p.sendMessage(plugin.getString("Messages.Commands.Jumping.Jump.Jumper")
											.replace("%PLAYER%", editPlayer.getName()));
									toJump.sendMessage(plugin.getString("Messages.Commands.Jumping.Jump.ToJump")
											.replace("%PLAYER%", p.getName()));
									p.closeInventory();
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
									PlayerUtilities pu = new PlayerUtilities(p);
									pu.update(editPlayer.getUniqueId().toString(), 0, false);
									puT.update(p.getUniqueId().toString(), 0, false);
									p.sendMessage(plugin.getString("Messages.Commands.Remove.Remove.Remover")
											.replace("%PLAYER%", editPlayer.getName()));
									if (Friends.bungeeMode && !editPlayer.isOnline()) {
										BungeeMessagingListener.sendToBungeeCord(p, "Message", editPlayer.getName(),
												plugin.getString("Messages.Commands.Remove.Remove.ToRemove")
														.replace("%PLAYER%", p.getName()));
									}
									if (editPlayer.isOnline()) {
										Bukkit.getPlayer(editPlayer.getUniqueId()).sendMessage(
												plugin.getString("Messages.Commands.Remove.Remove.ToRemove")
														.replace("%PLAYER%", p.getName()));
									}
									InventoryBuilder.openInv(p, InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.MAIN, false));
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
