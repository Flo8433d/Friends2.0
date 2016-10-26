/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Listeners.BungeeMessagingListener;
import de.HyChrod.Friends.Util.PlayerUtilities;
import de.HyChrod.Friends.Util.ReflectionsManager;

public class Command_Add {
	
	private final Player p;
	private final String arg;
	
	public Command_Add(Player player, String arg) {
		this.p = player;
		this.arg = arg;
	}
	
	@SuppressWarnings("deprecation")
	public Boolean addPlayer() {
		final OfflinePlayer toAdd = Bukkit.getOfflinePlayer(arg);
		if (toAdd.equals(p)) {
			p.sendMessage(Friends.getInstance().getString("Messages.Commands.Add.SendSelf"));
			return false;
		}
		PlayerUtilities puP = new PlayerUtilities(p);
		PlayerUtilities puT = new PlayerUtilities(toAdd);

		if (puT.get(0, false).contains(p.getUniqueId().toString())) {
			p.sendMessage(Friends.getInstance().getString("Messages.Commands.Add.AlreadyFriends"));
			return false;
		}
		if (puT.get(1, false).contains(p.getUniqueId().toString())) {
			p.sendMessage(Friends.getInstance().getString("Messages.Commands.Add.AlreadyRequested"));
			return false;
		}
		if (puT.get(2, false).contains(p.getUniqueId().toString())) {
			p.sendMessage(Friends.getInstance().getString("Messages.Commands.Add.Blocked.ToAdd"));
			return false;
		}
		if (puP.get(2, false).contains(toAdd.getUniqueId().toString())) {
			p.sendMessage(Friends.getInstance().getString("Messages.Commands.Add.Blocked.Requester"));
			return false;
		}
		if (puT.get(3, false).contains("option_noRequests")) {
			p.sendMessage(Friends.getInstance().getString("Messages.Commands.Add.NoRequests"));
			return false;
		}
		if (puP.get(0, true).size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit"))
			if (!p.hasPermission("Friends.ExtraFriends") || puP.get(0, true).size() > FileManager.ConfigCfg
					.getInt("Friends.Options.FriendLimit+")) {
				p.sendMessage(Friends.getInstance().getString("Messages.Commands.Add.LimitReached.Requester"));
				return false;
			}
		if (puT.get(0,true).size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit"))
			if(toAdd.isOnline()) {
				Player toCheck = Bukkit.getPlayer(toAdd.getName());
				if (!toCheck.hasPermission("Friends.ExtraFriends") || puT.get(0, true).size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
					p.sendMessage(Friends.getInstance().getString("Messages.Commands.Add.LimitReached.ToAdd"));
					return false;
				}
			}
		puT.update(p.getUniqueId().toString(), 1, true);
		if (Friends.bungeeMode) {
			if(BungeeMessagingListener.isOnline(toAdd))
				Bukkit.getScheduler().runTaskLaterAsynchronously(Friends.getInstance(), new Runnable() {
					public void run() {
						try {
							ByteArrayDataOutput out = ByteStreams.newDataOutput();
							out.writeUTF("ForwardToPlayer");
							out.writeUTF(toAdd.getName());
							out.writeUTF("AddingPlayer");

							ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
							DataOutputStream msgout = new DataOutputStream(msgbytes);
							msgout.writeUTF(toAdd.getName() + "@" + p.getName());
							msgout.writeShort(123);

							out.writeShort(msgbytes.toByteArray().length);
							out.write(msgbytes.toByteArray());

							p.sendPluginMessage(Friends.getInstance(), "BungeeCord", out.toByteArray());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}, 20);
		} else {
			if(toAdd.isOnline()) {
				ReflectionsManager.sendRequestMessages(p, Bukkit.getPlayer(toAdd.getUniqueId()));
			}
		}
		p.sendMessage(Friends.getInstance().getString("Messages.Commands.Add.Add.Requester").replace("%PLAYER%", toAdd.getName()));
		return true;
	}

}
