/*
 *
 * This class was made by HyChrod
 * All rights reserved, 2017
 *
 */
package de.HyChrod.Friends.Commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.InventoryTypes;
import de.HyChrod.Friends.Utilities.PlayerUtilities;

public class FriendCommand implements CommandExecutor {

	private Friends plugin;
	
	public FriendCommand(Friends friends) {
		this.plugin = friends;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(plugin.getString("Messages.Commands.NoPlayer"));
			return false;
		}
		Player player = (Player) sender;
		if(args.length == 0) {
			if (FileManager.ConfigCfg.getBoolean("Friends.GUI.Enable")) {
				plugin.pool.execute(new Runnable() {
					
					@Override
					public void run() {
						try {
							PlayerUtilities pu = PlayerUtilities.getUtilities(player.getUniqueId().toString());
							if(!pu.isFinished) {
								player.sendMessage(plugin.getString("Messages.GUI.LoadData"));
								return;
							}
							InventoryBuilder.openInv(player, InventoryBuilder.INVENTORY(plugin, player, InventoryTypes.MAIN, false));
						} catch (Exception ex) {ex.printStackTrace();}
					}
				});
				return true;
			}
			player.performCommand("friends help");
			return true;
		}
		for(String s : CommandSerializable.getSubCommands().keySet()) {
			if(args[0].equalsIgnoreCase(s)) {
				try {
					Constructor<?> construct = CommandSerializable.getSubCommands().get(s);
					construct.newInstance(Friends.getInstance(), player, args, new Callback<Boolean>() {

						@Override
						public void call(Boolean done) {}
					});
				} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
					e.printStackTrace();
				}
				return true;
			}
		}
		player.sendMessage(plugin.getString("Messages.Commands.Unknown"));
		return false;
	}

}
