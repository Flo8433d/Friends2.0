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

public class Help_Command {

	public Help_Command(Friends plugin, Player player, String[] args, Callback<Boolean> callback) {
		callback.call(true);
		if(args.length == 1) {
			for(String value : FileManager.MessagesCfg.getConfigurationSection("Messages.Commands.Help.Page1").getKeys(false))
				player.sendMessage(plugin.getString("Messages.Commands.Help.Page1." + value));
			return;
		}
		String page = args[1];
		if(FileManager.MessagesCfg.getString("Messages.Commands.Help.Page" + page) == null) {
			player.sendMessage(plugin.getString("Messages.Commands.Help.WrongSite"));
			return;
		}
		for(String value : FileManager.MessagesCfg.getConfigurationSection("Messages.Commands.Help.Page" + page).getKeys(false))
			player.sendMessage(plugin.getString("Messages.Commands.Help.Page" + page + "." + value));
		return;
	}
	
}
