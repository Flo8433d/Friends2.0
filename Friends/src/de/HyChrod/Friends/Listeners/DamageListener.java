/*
*
* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.SubCommands.Add_Command;
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.PlayerUtilities;
import de.HyChrod.Friends.Utilities.UtilitieItems;

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
			
			PlayerUtilities pu = PlayerUtilities.getUtilities(p.getUniqueId().toString());
			if (pu.getFriends().contains(toAdd.getUniqueId().toString())) {
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
									.equals(new UtilitieItems().FRIENDITEM(p).getItemMeta().getDisplayName())) {
								e.setCancelled(true);

								new Add_Command(plugin, p, new String[] {"add",toAdd.getName()}, new Callback<Boolean>() {
									
									@Override
									public void call(Boolean done) {}
								});
								return;
							}
						}
					}
				}
			}
		}
	}

}
