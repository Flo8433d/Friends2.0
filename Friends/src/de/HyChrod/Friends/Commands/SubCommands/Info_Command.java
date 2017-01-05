/*
 *
 * This class was made by HyChrod
 * All rights reserved, 2017
 *
 */
package de.HyChrod.Friends.Commands.SubCommands;

import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.UpdateChecker;

public class Info_Command {

	public Info_Command(Friends plugin, Player player, String[] args, Callback<Boolean> callback) {
		callback.call(true);
		if(args.length != 1) {
			player.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f info"));
			return;
		}
		player.sendMessage(plugin.prefix + " §9--------------| §6Friends §9|--------------");
		player.sendMessage(plugin.prefix + " §3Author: §f" + plugin.getDescription().getAuthors().get(0));
		if (!UpdateChecker.check()) {
			player.sendMessage(
					plugin.prefix + " §3Version: §f" + plugin.getDescription().getVersion() + " §4(Outdated)");
		} else {
			player.sendMessage(
					plugin.prefix + " §3Version: §f" + plugin.getDescription().getVersion() + " §2(Newest)");
		}
		player.sendMessage(plugin.prefix + " §9--------------| §6Friends §9|--------------");	
	}
	
}