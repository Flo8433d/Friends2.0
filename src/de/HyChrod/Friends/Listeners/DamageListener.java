/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Util.ItemStacks;
import de.HyChrod.Friends.Util.PlayerUtilities;
import de.HyChrod.Friends.Util.ReflectionsManager;

public class DamageListener implements Listener {

	private Friends plugin;

	public DamageListener(Friends friends) {
		this.plugin = friends;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player toAdd = (Player) e.getEntity();
			Player p = (Player) e.getDamager();

			PlayerUtilities pu = new PlayerUtilities(p);
			if (pu.getFriends().contains(toAdd)) {
				if (FileManager.ConfigCfg.getBoolean("Friends.Options.FriendCanPvP")) {
					return;
				}
				e.setCancelled(true);
				return;
			}

			if (FileManager.ConfigCfg.getBoolean("Friends.Options.HitToRequest")) {
				if (p.getItemInHand() != null) {
					if (p.getItemInHand().hasItemMeta()) {
						if (p.getItemInHand().getItemMeta().hasDisplayName()) {
							if (p.getItemInHand().getItemMeta().getDisplayName()
									.equals(ItemStacks.FRIENDITEM(p).getItemMeta().getDisplayName())) {
								e.setCancelled(true);
								PlayerUtilities puT = new PlayerUtilities(toAdd);

								if (puT.getFriends().contains(p)) {
									p.sendMessage(plugin.getString("Messages.Commands.Add.AlreadyFriends"));
									return;
								}
								if (puT.getRequests().contains(p)) {
									p.sendMessage(plugin.getString("Messages.Commands.Add.AlreadyRequested"));
									return;
								}
								if (puT.getBlocked().contains(p)) {
									p.sendMessage(plugin.getString("Messages.Commands.Add.Blocked.ToAdd"));
									return;
								}
								if (pu.getBlocked().contains(toAdd)) {
									p.sendMessage(plugin.getString("Messages.Commands.Add.Blocked.Requester"));
									return;
								}
								if (puT.getOptions().contains("option_noRequests")) {
									p.sendMessage(plugin.getString("Messages.Commands.Add.NoRequests"));
									return;
								}
								if (pu.getFriends().size() > FileManager.ConfigCfg
										.getInt("Friends.Options.FriendLimit")) {
									if (!p.hasPermission("Friends.ExtraFriends") || pu.getFriends()
											.size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
										p.sendMessage(plugin.getString("Messages.Commands.Add.LimitReached.Requester"));
										return;
									}
								}
								if (puT.getFriends().size() > FileManager.ConfigCfg
										.getInt("Friends.Options.FriendLimit")) {
									if (!p.hasPermission("Friends.ExtraFriends") || puT.getFriends()
											.size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
										p.sendMessage(plugin.getString("Messages.Commands.Add.LimitReached.ToAdd"));
										return;
									}
								}
								puT.addRequest(p);
								toAdd.sendMessage(plugin.getString("Messages.Commands.Add.Add.ToAdd.0")
										.replace("%PLAYER%", p.getName()));
								toAdd.sendMessage(plugin.getString("Messages.Commands.Add.Add.ToAdd.1")
										.replace("%PLAYER%", p.getName()));

								String[] msgs = new String[2];
								msgs[0] = plugin.getString("Messages.Commands.Add.Add.AcceptButton");
								msgs[1] = plugin.getString("Messages.Commands.Add.Add.DenyButton");

								String[] hover = new String[2];
								hover[0] = plugin.getString("Messages.Commands.Add.Add.AcceptHover");
								hover[1] = plugin.getString("Messages.Commands.Add.Add.DenyHover");

								String[] command = new String[2];
								command[0] = "/friend accept %name%";
								command[1] = "/friend deny %name%";
								ReflectionsManager.sendHoverMessage(toAdd, p.getName(),
										plugin.getString("Messages.Commands.Add.Add.ToAdd.2")
												.replace("%ACCEPT_BUTTON%", "").replace("%DENY_BUTTON%", ""),
										msgs, hover, command);
								toAdd.sendMessage(plugin.getString("Messages.Commands.Add.Add.ToAdd.3")
										.replace("%PLAYER%", p.getName()));
								p.sendMessage(plugin.getString("Messages.Commands.Add.Add.Requester")
										.replace("%PLAYER%", toAdd.getName()));
								return;
							}
						}
					}
				}
			}
		}
	}

}
