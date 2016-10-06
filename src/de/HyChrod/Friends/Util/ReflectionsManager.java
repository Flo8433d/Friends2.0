/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;

public class ReflectionsManager {

	public static void sendHoverMessage(Player player, String adder, String message, String[] msgs, String[] hover,
			String[] command) {
		try {

			String format = "{\"text\":\"" + message + "\",\"extra\":[";
			for (int i = 0; i < msgs.length; i++) {
				format = format + "{\"text\":\"" + msgs[i] + "   "
						+ "\",\"hoverEvent\":{\"action\":\"show_text\", \"value\":\"" + "" + hover[i]
						+ "\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\""
						+ command[i].replace("%name%", adder) + "\"}},";
			}
			format = format.substring(0, (format.length() - 1)) + "]}";

			Object msg = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
					.invoke(null, format);
			Constructor<?> msgConstructor = getNMSClass("PacketPlayOutChat")
					.getConstructor(getNMSClass("IChatBaseComponent"));
			Object packet = msgConstructor.newInstance(msg);
			Field field = packet.getClass().getDeclaredField("b");
			field.setAccessible(true);
			sendPacket(player, packet);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void sendRequestMessages(Player p, Player adding) {
		adding.sendMessage(
				Friends.getInstance().getString("Messages.Commands.Add.Add.ToAdd.0").replace("%PLAYER%", p.getName()));
		adding.sendMessage(
				Friends.getInstance().getString("Messages.Commands.Add.Add.ToAdd.1").replace("%PLAYER%", p.getName()));

		String[] msgs = new String[2];
		msgs[0] = Friends.getInstance().getString("Messages.Commands.Add.Add.AcceptButton");
		msgs[1] = Friends.getInstance().getString("Messages.Commands.Add.Add.DenyButton");

		String[] hover = new String[2];
		hover[0] = Friends.getInstance().getString("Messages.Commands.Add.Add.AcceptHover");
		hover[1] = Friends.getInstance().getString("Messages.Commands.Add.Add.DenyHover");

		String[] command = new String[2];
		command[0] = "/friend accept %name%";
		command[1] = "/friend deny %name%";
		sendHoverMessage(adding, p.getName(), Friends.getInstance().getString("Messages.Commands.Add.Add.ToAdd.2")
				.replace("%ACCEPT_BUTTON%", "").replace("%DENY_BUTTON%", ""), msgs, hover, command);
		adding.sendMessage(
				Friends.getInstance().getString("Messages.Commands.Add.Add.ToAdd.3").replace("%PLAYER%", p.getName()));
	}

	private static Class<?> getNMSClass(String name) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchFieldException ex) {
			ex.printStackTrace();
		}
	}

}
