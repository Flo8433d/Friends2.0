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
import de.HyChrod.Friends.Commands.Command_Add;
import de.HyChrod.Friends.Util.ItemStacks;
import de.HyChrod.Friends.Util.PlayerUtilities;

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
			if (pu.get(0).contains(toAdd.getUniqueId().toString())) {
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

								new Command_Add(p, toAdd.getName());
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
