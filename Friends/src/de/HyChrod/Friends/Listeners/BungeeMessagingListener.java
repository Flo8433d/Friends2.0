/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Listeners;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.BungeeSQL_Manager;
import de.HyChrod.Friends.Util.ReflectionsManager;

public class BungeeMessagingListener implements PluginMessageListener {

	@Override
	public synchronized void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord"))
				return;

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();

		if (subchannel.equals("AddingPlayer")) {
			try {
				short len = in.readShort();
				byte[] msgbytes = new byte[len];
				in.readFully(msgbytes);

				DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
				String somedata = msgin.readUTF();

				String[] players = somedata.split("@");
				if (Bukkit.getPlayer(players[0]) != null) {
					Player p = Bukkit.getPlayer(players[0]);

					p.sendMessage(
							Friends.getInstance().getString("Messages.Commands.Add.Add.ToAdd.0").replace("%PLAYER%", players[1]));
					p.sendMessage(
							Friends.getInstance().getString("Messages.Commands.Add.Add.ToAdd.1").replace("%PLAYER%", players[1]));

					String[] msgs = new String[2];
					msgs[0] = Friends.getInstance().getString("Messages.Commands.Add.Add.AcceptButton");
					msgs[1] = Friends.getInstance().getString("Messages.Commands.Add.Add.DenyButton");

					String[] hover = new String[2];
					hover[0] = Friends.getInstance().getString("Messages.Commands.Add.Add.AcceptHover");
					hover[1] = Friends.getInstance().getString("Messages.Commands.Add.Add.DenyHover");

					String[] command = new String[2];
					command[0] = "/friend accept %name%";
					command[1] = "/friend deny %name%";

					ReflectionsManager
							.sendHoverMessage(p, players[1],
									Friends.getInstance().getString("Messages.Commands.Add.Add.ToAdd.2")
											.replace("%ACCEPT_BUTTON%", "").replace("%DENY_BUTTON%", ""),
									msgs, hover, command);
					p.sendMessage(Friends.getInstance().getString("Messages.Commands.Add.Add.ToAdd.3").replace("%PLAYER%", players[1]));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return;
		}
	}

	public static boolean isOnline(OfflinePlayer player) {
		return Friends.bungeeMode ? BungeeSQL_Manager.isOnline(player) : player.isOnline();
	}

	public static void sendToBungeeCord(Player p, String first, String second, String third) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF(first);
			out.writeUTF(second);
			if (third != null)
				out.writeUTF(third);
		} catch (IOException e) {
			e.printStackTrace();
		}
		p.sendPluginMessage(Friends.getInstance(), "BungeeCord", b.toByteArray());
	}

}
