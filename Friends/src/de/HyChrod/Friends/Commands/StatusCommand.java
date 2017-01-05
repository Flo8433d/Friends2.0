/*
*
* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.Commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.Listeners.BungeeMessagingListener;
import de.HyChrod.Friends.Listeners.BungeeSpreadEvent;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.FriendPlayer;
import de.HyChrod.Friends.Utilities.PlayerUtilities;

public class StatusCommand implements CommandExecutor {

	private Friends plugin;
	public static HashMap<String, Long> lastUsed = new HashMap<>();
	
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
		
		plugin.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					if(args[0].equalsIgnoreCase("clear") && p.hasPermission("Friends.Commands.Status.Clear")) {
						if(args.length != 2) {
							p.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/status clear <Player>"));
							return;
						}
						if(!Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
							p.sendMessage(plugin.getString("Messages.Status.Clear.UnknownPlayer"));
							return;
						}
						OfflinePlayer toClear = Bukkit.getOfflinePlayer(args[1]);
						PlayerUtilities tU = PlayerUtilities.getUtilities(toClear.getUniqueId().toString());
						FriendPlayer FP = FriendPlayer.getPlayer(toClear.getUniqueId().toString());
						while(!tU.isFinished || !FP.isFinshed)
							synchronized (this) {
								wait(5L);
							}
						tU.setStatus("");
						FP.setStatus("");
						p.sendMessage(plugin.getString("Messages.Status.Clear.Clear").replace("%PLAYER%", toClear.getName()));
						return;
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
								return;
							}
					lastUsed.put(p.getUniqueId().toString(), System.currentTimeMillis());
					PlayerUtilities pu = PlayerUtilities.getUtilities(p.getUniqueId().toString());
					FriendPlayer FP = FriendPlayer.getPlayer(p.getUniqueId().toString());
					while(!pu.isFinished || !FP.isFinshed)
						synchronized (this) {
							wait(5L);
						}
					pu.setStatus(msg);
					FP.setStatus(msg);
					for(FriendPlayer fp : pu.getFriends()) {
						OfflinePlayer pp = Bukkit.getOfflinePlayer(UUID.fromString(fp.getUUID()));
						String data = p.getName() + "@" + msg;
						BungeeMessagingListener.isOnline(pp, new Callback<Boolean>() {
							
							@Override
							public void call(Boolean done) {
								if(done)
									Bukkit.getServer().getPluginManager().callEvent(new BungeeSpreadEvent(p, pp.getName(), "UpdateStatus", data));
							}
						});
					}
					p.sendMessage(plugin.getString("Messages.Status.ChangeStatus"));
				} catch (Exception ex) {ex.printStackTrace();}
			}
		});
		return false;
	}

}
