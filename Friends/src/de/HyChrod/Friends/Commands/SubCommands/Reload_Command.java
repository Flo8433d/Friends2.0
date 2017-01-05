/*
 *
 * This class was made by HyChrod
 * All rights reserved, 2017
 *
 */
package de.HyChrod.Friends.Commands.SubCommands;

import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.SQL.Callback;

public class Reload_Command {

	public Reload_Command(Friends plugin, Player player, String[] args, Callback<Boolean> callback) {
		callback.call(true);
		if (player.hasPermission("Friends.Commands.Reload")) {
			if (args.length != 1) {
				player.sendMessage(
						plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f reload"));
				return;
			}
			FileManager.reloadConfigs(plugin, true);
			player.sendMessage(plugin.getString("Messages.Commands.Reload.Reloaded"));
			return;
		}
		player.sendMessage(plugin.getString("Messages.Commands.NoPerm"));
		return;
	}

}
