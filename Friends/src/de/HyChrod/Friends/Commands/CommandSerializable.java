/*
 *
 * This class was made by HyChrod
 * All rights reserved, 2017
 *
 */
package de.HyChrod.Friends.Commands;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.SubCommands.AcceptAll_Command;
import de.HyChrod.Friends.Commands.SubCommands.Accept_Command;
import de.HyChrod.Friends.Commands.SubCommands.Add_Command;
import de.HyChrod.Friends.Commands.SubCommands.Block_Command;
import de.HyChrod.Friends.Commands.SubCommands.DenyAll_Command;
import de.HyChrod.Friends.Commands.SubCommands.Deny_Command;
import de.HyChrod.Friends.Commands.SubCommands.Help_Command;
import de.HyChrod.Friends.Commands.SubCommands.Info_Command;
import de.HyChrod.Friends.Commands.SubCommands.Jump_Command;
import de.HyChrod.Friends.Commands.SubCommands.List_Command;
import de.HyChrod.Friends.Commands.SubCommands.Msg_Command;
import de.HyChrod.Friends.Commands.SubCommands.Reload_Command;
import de.HyChrod.Friends.Commands.SubCommands.Remove_Command;
import de.HyChrod.Friends.Commands.SubCommands.Toggle_Command;
import de.HyChrod.Friends.Commands.SubCommands.Unblock_Command;
import de.HyChrod.Friends.SQL.Callback;

public class CommandSerializable {

	private static HashMap<String, Constructor<?>> SUB_COMMANDS = new HashMap<>();
	
	public CommandSerializable() {
		try {
			SUB_COMMANDS.put("accept", getConstructor(Accept_Command.class));
			SUB_COMMANDS.put("acceptall", getConstructor(AcceptAll_Command.class));
			SUB_COMMANDS.put("add", getConstructor(Add_Command.class));
			SUB_COMMANDS.put("block", getConstructor(Block_Command.class));
			SUB_COMMANDS.put("deny", getConstructor(Deny_Command.class));
			SUB_COMMANDS.put("denyall", getConstructor(DenyAll_Command.class));
			SUB_COMMANDS.put("help", getConstructor(Help_Command.class));
			SUB_COMMANDS.put("info", getConstructor(Info_Command.class));
			SUB_COMMANDS.put("msg", getConstructor(Msg_Command.class));
			SUB_COMMANDS.put("reload", getConstructor(Reload_Command.class));
			SUB_COMMANDS.put("remove", getConstructor(Remove_Command.class));
			SUB_COMMANDS.put("toggle", getConstructor(Toggle_Command.class));
			SUB_COMMANDS.put("unblock", getConstructor(Unblock_Command.class));
			SUB_COMMANDS.put("list", getConstructor(List_Command.class));
			SUB_COMMANDS.put("jump", getConstructor(Jump_Command.class));
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private Constructor<?> getConstructor(Class<?> clazz) throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		return clazz.getConstructor(Friends.class, Player.class, String[].class, Callback.class);
	}
	
	public static HashMap<String, Constructor<?>> getSubCommands() {
		return SUB_COMMANDS;
	}
	
}
