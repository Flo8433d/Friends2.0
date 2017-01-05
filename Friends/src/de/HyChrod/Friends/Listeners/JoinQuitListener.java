/*
*
* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.Listeners;

import java.sql.ResultSet;
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

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.SQL.QueryRunnable;
import de.HyChrod.Friends.SQL.UpdateRunnable;
import de.HyChrod.Friends.Utilities.FriendPlayer;
import de.HyChrod.Friends.Utilities.PlayerUtilities;
import de.HyChrod.Friends.Utilities.UpdateChecker;
import de.HyChrod.Friends.Utilities.UtilitieItems;

public class JoinQuitListener implements Listener {

	private Friends plugin;

	public JoinQuitListener(Friends friends) {
		this.plugin = friends;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();

		plugin.pool.execute(new Runnable() {

			@Override
			public void run() {
				try {
					PlayerUtilities pu = new PlayerUtilities(p.getUniqueId().toString());
					while (!pu.isFinished)
						synchronized (this) {
							wait(5L);
						}

					if (FileManager.ConfigCfg.getBoolean("Friends.Options.RequestNotification")
							&& !pu.getRequests().isEmpty()) {
						p.sendMessage(plugin.getString("Messages.RequestNotification").replace("%REQUESTS%",
								String.valueOf(pu.getRequests().size())));
					}
					if (!Friends.bungeemode)
						if (FileManager.ConfigCfg.getBoolean("Friends.Options.JoinQuitMessages"))
							for (FriendPlayer FP : pu.getFriends()) {
								OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(FP.getUUID()));
								if (player.isOnline()) {
									PlayerUtilities puT = PlayerUtilities.getUtilities(player.getUniqueId().toString());
									while (!puT.isFinished)
										synchronized (this) {
											wait(5L);
										}
									if (!puT.getOptions().contains("option_noChat")) {
										Bukkit.getPlayer(player.getUniqueId()).sendMessage(plugin
												.getString("Messages.FriendJoin").replace("%PLAYER%", p.getName()));
									}
								}
							}
				} catch (Exception ex) {
				}
			}
		});

		if (p.hasPermission("Friends.Admin")) {
			if (FileManager.ConfigCfg.getBoolean("Friends.CheckForUpdates") && !UpdateChecker.check()) {
				p.sendMessage(plugin.prefix + " §cA new §6Spigot §cupdate is available!");
				p.sendMessage(plugin.prefix + " §cPlease update your plugin!");

			}
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

			if (FileManager.ConfigCfg.getBoolean("Friends.DisabledWorlds.Enable")) {
				if (!FileManager.ConfigCfg.getStringList("Friends.DisabledWorlds.Worlds")
						.contains(p.getWorld().getName())) {
					p.getInventory().setItem(FileManager.ConfigCfg.getInt("Friends.FriendItem.InventorySlot") - 1,
							new UtilitieItems().FRIENDITEM(p));
				}
			} else if (FileManager.ConfigCfg.getBoolean("Friends.EnabledWorlds.Enable")) {
				if (FileManager.ConfigCfg.getStringList("Friends.EnabledWorlds.Worlds")
						.contains(p.getWorld().getName())) {
					p.getInventory().setItem(FileManager.ConfigCfg.getInt("Friends.FriendItem.InventorySlot") - 1,
							new UtilitieItems().FRIENDITEM(p));
				}
			}
		}

		if (Friends.bungeemode) {
			new QueryRunnable("SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + p.getUniqueId().toString() + "'",
					new Callback<ResultSet>() {

						@Override
						public void call(ResultSet rs) {
							try {
								if (rs.next()) {
									new UpdateRunnable("UPDATE friends2_0_BUNGEE SET ONLINE= '1' WHERE UUID= '"
											+ p.getUniqueId().toString() + "'", null).runTaskAsynchronously(plugin);
									return;
								}
								new UpdateRunnable(
										"INSERT INTO friends2_0_BUNGEE(UUID, ONLINE, SERVER, LASTONLINE) VALUES ('"
												+ p.getUniqueId().toString() + "', '1', '', '')",
										null).runTaskAsynchronously(plugin);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}).runTaskAsynchronously(plugin);
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (Friends.bungeemode) {
			new QueryRunnable("SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + p.getUniqueId().toString() + "'",
					new Callback<ResultSet>() {

						@Override
						public void call(ResultSet rs) {
							try {
								if (rs.next()) {
									new UpdateRunnable("UPDATE friends2_0_BUNGEE SET (ONLINE, LASTONLINE) VALUES ('" + 0
											+ "', '" + System.currentTimeMillis() + "') WHERE UUID= '"
											+ p.getUniqueId().toString() + "'", null).runTaskAsynchronously(plugin);
									return;
								}
								new UpdateRunnable(
										"INSERT INTO friends2_0_BUNGEE(UUID, ONLINE, SERVER, LASTONLINE) VALUES ('"
												+ p.getUniqueId().toString() + "', '0', '', '"
												+ System.currentTimeMillis() + "')",
										null).runTaskAsynchronously(plugin);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}).runTaskAsynchronously(plugin);
		}
		plugin.pool.execute(new Runnable() {

			@Override
			public void run() {
				try {
					PlayerUtilities pu = PlayerUtilities.getUtilities(p.getUniqueId().toString());
					while (!pu.isFinished)
						synchronized (this) {
							wait(5L);
						}
					pu.flushLastOnline();
					if (Friends.bungeemode)
						return;
					if (FileManager.ConfigCfg.getBoolean("Friends.Options.JoinQuitMessages"))
						for (FriendPlayer FP : pu.getFriends()) {
							OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(FP.getUUID()));
							if (player.isOnline()) {
								PlayerUtilities puT = PlayerUtilities.getUtilities(player.getUniqueId().toString());

								if (!puT.getOptions().contains("option_noChat")) {
									Bukkit.getPlayer(player.getUniqueId()).sendMessage(
											plugin.getString("Messages.FriendQuit").replace("%PLAYER%", p.getName()));
								}
							}
						}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

}
