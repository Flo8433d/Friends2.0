/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.BungeeSQL_Manager;
import de.HyChrod.Friends.Util.PlayerUtilities;
import de.HyChrod.Friends.Util.UpdateChecker;
import de.HyChrod.Friends.Util.UtilitieItems;

public class JoinQuitListener implements Listener {

	private Friends plugin;

	public JoinQuitListener(Friends friends) {
		this.plugin = friends;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		PlayerUtilities pu = new PlayerUtilities(p);

		if (p.hasPermission("Friends.Admin")) {
			if (FileManager.ConfigCfg.getBoolean("Friends.CheckForUpdates") && !UpdateChecker.check()) {
				p.sendMessage(plugin.prefix + " §cA new §6Spigot §cupdate is available!");
				p.sendMessage(plugin.prefix + " §cPlease update your plugin!");

			}
		}

		if (FileManager.ConfigCfg.getBoolean("Friends.Options.RequestNotification") && !pu.get(1, true).isEmpty()) {
			p.sendMessage(plugin.getString("Messages.RequestNotification").replace("%REQUESTS%",
					String.valueOf(pu.get(1, true).size())));
		}

		if (FileManager.ConfigCfg.getBoolean("Friends.FriendItem.GiveOnJoin")) {
			if (p.getInventory()
					.getItem(
							FileManager.ConfigCfg.getInt("Friends.FriendItem.InventorySlot")
									- 1) == null
					|| (p.getInventory()
							.getItem(FileManager.ConfigCfg.getInt("Friends.FriendItem.InventorySlot") - 1) != null
							&& !p.getInventory()
									.getItem(FileManager.ConfigCfg.getInt("Friends.FriendItem.InventorySlot") - 1)
									.hasItemMeta()
							|| !p.getInventory()
									.getItem(FileManager.ConfigCfg.getInt("Friends.FriendItem.InventorySlot") - 1)
									.getItemMeta().hasDisplayName()
							|| !p.getInventory()
									.getItem(FileManager.ConfigCfg.getInt("Friends.FriendItem.InventorySlot") - 1)
									.getItemMeta().getDisplayName()
									.equals(new UtilitieItems().FRIENDITEM(p).getItemMeta().getDisplayName()))) {
				for (int i = 0; i < p.getInventory().getSize(); i++) {
					if (p.getInventory().getItem(i) != null) {
						if (p.getInventory().getItem(i).hasItemMeta()) {
							if (p.getInventory().getItem(i).getItemMeta().hasDisplayName()) {
								if (p.getInventory().getItem(i).getItemMeta().getDisplayName()
										.equals(new UtilitieItems().FRIENDITEM(p).getItemMeta().getDisplayName())) {
									p.getInventory().setItem(i, new ItemStack(Material.AIR));
								}
							}
						}
					}
				}
			}
			p.getInventory().setItem(FileManager.ConfigCfg.getInt("Friends.FriendItem.InventorySlot") - 1,
					new UtilitieItems().FRIENDITEM(p));
		}

		if (Friends.bungeeMode) {
			BungeeSQL_Manager.set(p, 1, "ONLINE");
			return;
		}
		for (Object uuid : pu.get(0, true)) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(String.valueOf(uuid)));
			if(Friends.bungeeMode)
				player = ((OfflinePlayer)uuid);
			if (player.isOnline()) {
				PlayerUtilities puT = new PlayerUtilities(player);
				if (!puT.get(3, false).contains("option_noChat")) {
					Bukkit.getPlayer(player.getUniqueId())
							.sendMessage(plugin.getString("Messages.FriendJoin").replace("%PLAYER%", p.getName()));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (Friends.bungeeMode) {
			BungeeSQL_Manager.set(p, System.currentTimeMillis(), "LASTONLINE");
			return;
		}

		PlayerUtilities pu = new PlayerUtilities(p);
		pu.setLastOnline(System.currentTimeMillis());
		pu.saveData(false);

		for (Object uuid : pu.get(0, true)) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(String.valueOf(uuid)));
			if(Friends.bungeeMode)
				player = ((OfflinePlayer)uuid);
			if (player.isOnline()) {
				PlayerUtilities puT = new PlayerUtilities(player);
				if (!puT.get(3, false).contains("option_noChat")) {
					Bukkit.getPlayer(player.getUniqueId())
							.sendMessage(plugin.getString("Messages.FriendQuit").replace("%PLAYER%", p.getName()));
				}
			}
		}
	}

}
