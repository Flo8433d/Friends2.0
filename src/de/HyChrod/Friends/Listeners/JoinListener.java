/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Util.ItemStacks;
import de.HyChrod.Friends.Util.PlayerUtilities;
import de.HyChrod.Friends.Util.UpdateChecker;

public class JoinListener implements Listener {

	private Friends plugin;
	private FileManager mgr = new FileManager();
	private FileConfiguration cfg = this.mgr.getConfig("", "config.yml");
	
	public JoinListener(Friends friends) {
		this.plugin = friends;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		PlayerUtilities pu = new PlayerUtilities(p);
		
		if(p.hasPermission("Friends.Admin")) {
			if(this.cfg.getBoolean("Friends.CheckForUpdates") && !UpdateChecker.check()) {
				p.sendMessage(plugin.prefix + " §cA new §6Spigot §cupdate is available!");
				p.sendMessage(plugin.prefix + " §cPlease update your plugin!");
				
			}
		}
		
		if(this.cfg.getBoolean("Friends.FriendItem.GiveOnJoin")) {
			if(p.getInventory().getItem(this.cfg.getInt("Friends.FriendItem.InventorySlot")-1) == null ||
					(p.getInventory().getItem(this.cfg.getInt("Friends.FriendItem.InventorySlot")-1) != null && !p.getInventory().getItem(this.cfg.getInt("Friends.FriendItem.InventorySlot")-1).hasItemMeta()
					|| !p.getInventory().getItem(this.cfg.getInt("Friends.FriendItem.InventorySlot")-1).getItemMeta().hasDisplayName()
					|| !p.getInventory().getItem(this.cfg.getInt("Friends.FriendItem.InventorySlot")-1).getItemMeta().getDisplayName().equals(ItemStacks.FRIENDITEM(p).getItemMeta().getDisplayName()))) {
				for(int i = 0; i < p.getInventory().getSize(); i++) {
					if(p.getInventory().getItem(i) != null) {
						if(p.getInventory().getItem(i).hasItemMeta()) {
							if(p.getInventory().getItem(i).getItemMeta().hasDisplayName()) {
								if(p.getInventory().getItem(i).getItemMeta().getDisplayName().equals(ItemStacks.FRIENDITEM(p).getItemMeta().getDisplayName())) {
									p.getInventory().setItem(i, new ItemStack(Material.AIR));
								}
							}
						}
					}
				}
			}
			p.getInventory().setItem(this.cfg.getInt("Friends.FriendItem.InventorySlot")-1, ItemStacks.FRIENDITEM(p));
		}
		
		if(Friends.bungeeMode) {
			return;
		}
		for(OfflinePlayer player : pu.getFriends()) {
			if(player.isOnline()) {
				PlayerUtilities puT = new PlayerUtilities(player);
				if(!puT.getOptions().contains("option_noChat")) {
					Bukkit.getPlayer(player.getUniqueId()).sendMessage(plugin.getString("Messages.FriendJoin").replace("%PLAYER%", p.getName()));
				}
			}
		}
	}
	
}
