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
import java.util.HashMap;

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

	private static HashMap<Player, Object> obj = new HashMap<Player, Object>();

	@Override
	public synchronized void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}

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
					ReflectionsManager.sendRequestMessages(Bukkit.getPlayer(players[1]), p);
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return;
		}
	}

	public static boolean isOnline(OfflinePlayer player) {
		if (Friends.bungeeMode) {
			return BungeeSQL_Manager.isOnline(player);
		}
		return player.isOnline();
	}

	public synchronized Object get(Player p, boolean integer) {
		sendToBungeeCord(p, "get", integer ? "points" : "nickname", null);

		try {
			wait();
		} catch (InterruptedException e) {
		}

		return obj.get(p);
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
