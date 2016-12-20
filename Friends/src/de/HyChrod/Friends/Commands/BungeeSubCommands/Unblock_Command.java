/*
 *
 * This class was made by HyChrod
 * All rights reserved, 2017
 *
 */
package de.HyChrod.Friends.Commands.BungeeSubCommands;

import java.sql.ResultSet;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.SQL.QueryRunnable;
import de.HyChrod.Friends.SQL.UpdateRunnable;

public class Unblock_Command {
	
	public Unblock_Command(Friends plugin, Player performer, OfflinePlayer toUnblock) {
		new QueryRunnable("SELECT * FROM friends2_0 WHERE UUID= '" + performer.getUniqueId().toString() + "'", new Callback<ResultSet>() {
			
			@Override
			public void call(ResultSet rsSELECT_P) {
				try {
					if(rsSELECT_P.next() && (rsSELECT_P.getString("BLOCKED") != null)) {
						String blocked = rsSELECT_P.getString("BLOCKED");
						if(blocked.contains(toUnblock.getUniqueId().toString())) {
							new UpdateRunnable("UPDATE friends2_0 SET "
									+ "BLOCKED= '" + blocked.replace(toUnblock.getUniqueId().toString() + "//;", "") + "' WHERE "
											+ "UUID= '" + performer.getUniqueId().toString() + "'", null).runTaskAsynchronously(plugin);
							performer.sendMessage(plugin.getString("Messages.Commands.Unblock.Unblock").replace("%PLAYER%",
									toUnblock.getName()));
							return;
						}
					}
				} catch (Exception ex) {ex.printStackTrace();}
				performer.sendMessage(plugin.getString("Messages.Commands.Unblock.NotBlocked"));
				return;
			}
		}).runTaskAsynchronously(plugin);
	}

}
