/*
 *
 * This class was made by HyChrod
 * All rights reserved, 2017
 *
 */
package de.HyChrod.Friends.Listeners;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.HyChrod.Friends.Friends;

public class BungeeSpreadEvent extends Event {

	public static HandlerList handlers = new HandlerList();
	String toSend;
	Player player;
	String data;
	String channel;
	
	public BungeeSpreadEvent(Player player, String toSend, String channel, String someData) {
		this.toSend = toSend;
		this.data = someData;
		this.player = player;
		this.channel = channel;
		
		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("ForwardToPlayer");
			out.writeUTF(toSend);
			out.writeUTF(channel);

			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
			DataOutputStream msgout = new DataOutputStream(msgbytes);
			msgout.writeUTF(someData);
			msgout.writeShort(123);

			out.writeShort(msgbytes.toByteArray().length);
			out.write(msgbytes.toByteArray());
			player.sendPluginMessage(Friends.getInstance(), "BungeeCord", out.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public HandlerList getHandlers() {
		return null;
	}
	
	public static HandlerList getHandlerList() {
		return BungeeSpreadEvent.handlers;
	}
}
