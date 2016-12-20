/*
*
* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.Commands;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Util.PlayerUtilities;

public class StatusCommand implements CommandExecutor {

	private Friends plugin;
	private HashMap<String, Long> lastUsed = new HashMap<>();
	
	public StatusCommand(Friends friends) {
		this.plugin = friends;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(plugin.getString("Messages.Commands.NoPlayer"));
			return false;
		}
		Player p = (Player) sender;
		if(!p.hasPermission("Friends.Commands.Status")) {
			p.sendMessage(plugin.getString("Messages.Commands.NoPerm"));
			return false;
		}
		
		if(args.length == 0) {
			p.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/status <Message>"));
			return false;
		}
		
		if(args[0].equalsIgnoreCase("clear") && p.hasPermission("Friends.Commands.Status.Clear")) {
			if(args.length != 2) {
				p.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/status clear <Player>"));
				return false;
			}
			if(!Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
				p.sendMessage(plugin.getString("Messages.Status.Clear.UnknownPlayer"));
				return false;
			}
			OfflinePlayer toClear = Bukkit.getOfflinePlayer(args[1]);
			PlayerUtilities tU = new PlayerUtilities(toClear);
			tU.setStatus("");
			p.sendMessage(plugin.getString("Messages.Status.Clear.Clear").replace("%PLAYER%", toClear.getName()));
			return true;
		}
		String msg = "";
		for(int i = 0; i < args.length; i++) {
			msg = msg + args[i] + " ";
		}
		msg = msg.substring(0, msg.length()-1);
		
		if(FileManager.ConfigCfg.getBoolean("Friends.Options.Status.Delay.Enable"))
			if(lastUsed.containsKey(p.getUniqueId().toString()))
				if((System.currentTimeMillis() - lastUsed.get(p.getUniqueId().toString())) < (FileManager.ConfigCfg.getLong("Friends.Options.Status.Delay.TimeStamp")*1000)) {
					p.sendMessage(plugin.getString("Messages.Status.TooFast"));
					return false;
				}
		lastUsed.put(p.getUniqueId().toString(), System.currentTimeMillis());
		PlayerUtilities pu = new PlayerUtilities(p);
		pu.setStatus(msg);
		p.sendMessage(plugin.getString("Messages.Status.ChangeStatus"));
		return false;
	}

}
