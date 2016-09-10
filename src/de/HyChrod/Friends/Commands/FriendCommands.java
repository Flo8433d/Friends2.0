/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Util.InventoryBuilder;
import de.HyChrod.Friends.Util.PlayerUtilities;
import de.HyChrod.Friends.Util.ReflectionsManager;
import de.HyChrod.Friends.Util.UpdateChecker;

public class FriendCommands implements CommandExecutor {

	private Friends plugin;
	private FileManager mgr = new FileManager();
	private FileConfiguration ConfigCfg = this.mgr.getConfig("", "config.yml");

	public FriendCommands(Friends friends) {
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
		if(p.hasPermission("Friends.Use")) {
			
			if(args.length == 0) {
				if(this.ConfigCfg.getBoolean("Friends.GUI.Enable")) {
					InventoryBuilder.MAIN_INVENTORY(plugin, p);
					return true;
				}
				p.performCommand("friends help");
				return true;
			}
			if(args.length == 1 && args[0].equalsIgnoreCase("help") || (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("1"))) {
				for(int i = 0; i < 9; i++) {
					p.sendMessage(plugin.getString("Messages.Commands.Help.Page1." + i));
				}
				return true;
			}
			if(args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("2")) {
				for(int i = 0; i < 9; i++) {
					p.sendMessage(plugin.getString("Messages.Commands.Help.Page2." + i));
				}
				return true;
			}
			
			if(!Friends.bungeeMode) {
				
				/*
				 * ADD
				 */
				if(args[0].equalsIgnoreCase("add")) {
					
					p.sendMessage("PERFORMED B");
					
					if(args.length == 2) {
						if(Bukkit.getPlayer(args[1]) == null) {
							p.sendMessage(plugin.getString("Messages.Commands.Add.PlayerOffline"));
							return false;
						}
						Player toAdd = Bukkit.getPlayer(args[1]);
						if(toAdd.equals(p)) {
							p.sendMessage(plugin.getString("Messages.Commands.Add.SendSelf"));
							return false;
						}
						
						PlayerUtilities puP = new PlayerUtilities(p);
						PlayerUtilities puT = new PlayerUtilities(toAdd);
						
						if(puT.getFriends().contains(p)) {
							p.sendMessage(plugin.getString("Messages.Commands.Add.AlreadyFriends"));
							return false;
						}
						if(puT.getRequests().contains(p)) {
							p.sendMessage(plugin.getString("Messages.Commands.Add.AlreadyRequested"));
							return false;
						}
						if(puT.getBlocked().contains(p)) {
							p.sendMessage(plugin.getString("Messages.Commands.Add.Blocked.ToAdd"));
							return false;
						}
						if(puP.getBlocked().contains(toAdd)) {
							p.sendMessage(plugin.getString("Messages.Commands.Add.Blocked.Requester"));
							return false;
						}
						if(puT.getOptions().contains("option_noRequests")) {
							p.sendMessage(plugin.getString("Messages.Commands.Add.NoRequests"));
							return false;
						}
						if(puP.getFriends().size() > this.ConfigCfg.getInt("Friends.Options.FriendLimit")) {
							if(!p.hasPermission("Friends.ExtraFriends") || puP.getFriends().size() > this.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
								p.sendMessage(plugin.getString("Messages.Commands.Add.LimitReached.Requester"));
								return false;
							}
						}
						if(puT.getFriends().size() > this.ConfigCfg.getInt("Friends.Options.FriendLimit")) {
							if(!p.hasPermission("Friends.ExtraFriends") || puT.getFriends().size() > this.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
								p.sendMessage(plugin.getString("Messages.Commands.Add.LimitReached.ToAdd"));
								return false;
							}
						}
						puT.addRequest(p);
						toAdd.sendMessage(plugin.getString("Messages.Commands.Add.Add.ToAdd.0").replace("%PLAYER%", p.getName()));
						toAdd.sendMessage(plugin.getString("Messages.Commands.Add.Add.ToAdd.1").replace("%PLAYER%", p.getName()));
						
						String[] msgs = new String[2];
						msgs[0] = plugin.getString("Messages.Commands.Add.Add.AcceptButton");
						msgs[1] = plugin.getString("Messages.Commands.Add.Add.DenyButton");
						
						String[] hover = new String[2];
						hover[0] = plugin.getString("Messages.Commands.Add.Add.AcceptHover");
						hover[1] = plugin.getString("Messages.Commands.Add.Add.DenyHover");
						
						String[] command = new String[2];
						command[0] = "/friend accept %name%";
						command[1] = "/friend deny %name%";
						ReflectionsManager.sendHoverMessage(toAdd, p, plugin.getString("Messages.Commands.Add.Add.ToAdd.2")
								.replace("%ACCEPT_BUTTON%", "").replace("%DENY_BUTTON%", ""), msgs, hover, command);
						toAdd.sendMessage(plugin.getString("Messages.Commands.Add.Add.ToAdd.3").replace("%PLAYER%", p.getName()));
						p.sendMessage(plugin.getString("Messages.Commands.Add.Add.Requester").replace("%PLAYER%", toAdd.getName()));
						return true;
					}
					p.sendMessage(plugin.getString("Messages.Commands.Add.WrongUsage"));
					return false;
				}
				
				/*
				 * REMOVE
				 */
				if(args[0].equalsIgnoreCase("remove")) {
					if(args.length == 2) {
						OfflinePlayer toRemove = Bukkit.getOfflinePlayer(args[1]);
						PlayerUtilities puP = new PlayerUtilities(p);
						PlayerUtilities puT = new PlayerUtilities(toRemove);
						
						if(puP.getFriends().contains(toRemove)) {
							
							puP.removeFriend(toRemove);
							puT.removeFriend(p);
							p.sendMessage(plugin.getString("Messages.Commands.Remove.Remove.Remover").replace("%PLAYER%", toRemove.getName()));
							if(toRemove.isOnline()) {
								Bukkit.getPlayer(toRemove.getUniqueId()).sendMessage(plugin.getString("Messages.Commands.Remove.Remove.ToRemove")
										.replace("%PLAYER%", p.getName()));
							}
							return true;
							
						}
						p.sendMessage(plugin.getString("Messages.Commands.Remove.NoFriends"));
						return false;
					}
					p.sendMessage(plugin.getString("Messages.Commands.Remove.WrongUsage"));
					return false;
				}
				
				/*
				 * BLOCK
				 */
				if(args[0].equalsIgnoreCase("block")) {
					if(args.length == 2) {
						OfflinePlayer toBlock = Bukkit.getOfflinePlayer(args[1]);
						PlayerUtilities puP = new PlayerUtilities(p);
						PlayerUtilities puT = new PlayerUtilities(toBlock);
						
						if(puP.getBlocked().contains(toBlock)) {
							p.sendMessage(plugin.getString("Messages.Commands.Block.AlreadyBlocked"));
							return false;
						}
						if(toBlock.isOnline() && puP.getFriends().contains(toBlock)) {
							Bukkit.getPlayer(toBlock.getUniqueId()).sendMessage(plugin.getString("Messages.Commands.Block.Block.ToBlock")
									.replace("%PLAYER%", p.getName()));
						}
						
						puP.addBlocked(toBlock);
						puP.removeFriend(toBlock);
						puP.removeRequest(toBlock);
						puT.removeFriend(p);
						puT.removeRequest(p);
						
						p.sendMessage(plugin.getString("Messages.Commands.Block.Block.Blocker").replace("%PLAYER%", toBlock.getName()));
						return true;
						
					}
					p.sendMessage(plugin.getString("Messages.Commands.Block.WrongUsage"));
					return false;
				}
				
				/*
				 * UNBLOCK
				 */
				if(args[0].equalsIgnoreCase("unblock")) {
					if(args.length == 2) {
						OfflinePlayer toUnblock = Bukkit.getOfflinePlayer(args[1]);
						PlayerUtilities puP = new PlayerUtilities(p);
						
						if(puP.getBlocked().contains(toUnblock)) {
							puP.removeBlocked(toUnblock);
							p.sendMessage(plugin.getString("Messages.Commands.Unblock.Unblock").replace("%PLAYER%", toUnblock.getName()));
							return true;
						}
						p.sendMessage(plugin.getString("Messages.Commands.Unblock.NotBlocked"));
						return false;
					}
					p.sendMessage(plugin.getString("Messages.Commands.Unblock.WrongUsage"));
					return false;
				}
				
				/*
				 * ACCEPT
				 */
				if(args[0].equalsIgnoreCase("accept")) {
					if(args.length == 2) {
						OfflinePlayer toAccept = Bukkit.getOfflinePlayer(args[1]);
						PlayerUtilities puP = new PlayerUtilities(p);
						PlayerUtilities puT = new PlayerUtilities(toAccept);
						
						if(puP.getRequests().contains(toAccept)) {
							if(puP.getFriends().size() > this.ConfigCfg.getInt("Friends.Options.FriendLimit")) {
								if(!p.hasPermission("Friends.ExtraFriends") || puP.getFriends().size() > this.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
									p.sendMessage(plugin.getString("Messages.Commands.Accept.LimitReached.Accepter"));
									return false;
								}
							}
							if(puT.getFriends().size() > this.ConfigCfg.getInt("Friends.Options.FriendLimit")) {
								if(!p.hasPermission("Friends.ExtraFriends") || puT.getFriends().size() > this.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
									p.sendMessage(plugin.getString("Messages.Commands.Accept.LimitReached.Requester"));
									return false;
								}
							}
							
							puP.addFriend(toAccept);
							puT.addFriend(p);
							puP.removeRequest(toAccept);
							p.sendMessage(plugin.getString("Messages.Commands.Accept.Accept.Accepter").replace("%PLAYER%", toAccept.getName()));
							if(toAccept.isOnline()) {
								Bukkit.getPlayer(toAccept.getUniqueId()).sendMessage(plugin.getString("Messages.Commands.Accept.Accept.ToAccept")
										.replace("%PLAYER%", p.getName()));
							}
							return true;
						}
						p.sendMessage(plugin.getString("Messages.Commands.Accept.NoRequest"));
						return false;
					}
					p.sendMessage(plugin.getString("Messages.Commands.Accept.WrongUsage"));
					return false;
				}
				
				/*
				 * DENY
				 */
				if(args[0].equalsIgnoreCase("deny")) {
					if(args.length == 2) {
						OfflinePlayer toDeny = Bukkit.getOfflinePlayer(args[1]);
						PlayerUtilities puP = new PlayerUtilities(p);
						
						if(puP.getRequests().contains(toDeny)) {
							
							puP.removeRequest(toDeny);
							p.sendMessage(plugin.getString("Messages.Commands.Deny.Deny.Denier").replace("%PLAYER%", toDeny.getName()));
							if(toDeny.isOnline()) {
								Bukkit.getPlayer(toDeny.getUniqueId()).sendMessage(plugin.getString("Messages.Commands.Deny.Deny.ToDeny")
										.replace("%PLAYER%", p.getName()));
							}
							return true;
							
						}
						p.sendMessage(plugin.getString("Messages.Commands.Deny.NoRequest"));
						return false;
					}
					p.sendMessage(plugin.getString("Messages.Commands.Deny.WrongUsage"));
					return false;
				}
				
				/*
				 * TOGGLE OPTION
				 */
				if(args[0].equalsIgnoreCase("toggle")) {
					if(args.length == 2) {
						
						PlayerUtilities pu = new PlayerUtilities(p);
						if(args[1].equalsIgnoreCase("requests")) {
							pu.toggleOption("option_noRequests");
							p.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleRequests"));
							return true;
						}
						if(args[1].equalsIgnoreCase("chat")) {
							pu.toggleOption("option_noChat");
							p.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleChat"));
							return true;
						}
						if(args[1].equalsIgnoreCase("jumping")) {
							pu.toggleOption("option_noJumping");
							p.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleJumping"));
							return true;
						}
						p.sendMessage(plugin.getString("Messages.Commands.Toggle.NoOption"));
						return false;
					}
					p.sendMessage(plugin.getString("Messages.Commands.Toggle.WrongUsage"));
					return false;
				}
				
				/*
				 * LIST
				 */
				if(args[0].equalsIgnoreCase("list")) {
					if(args.length == 1) {
						PlayerUtilities pu = new PlayerUtilities(p);
						
						String online = "";
						String offline = "";
						for(OfflinePlayer friend : pu.getFriends()) {
							if(friend.isOnline()) online = online + friend.getName() + ", ";
							if(!friend.isOnline()) offline = offline + friend.getName() + ", ";
						}
						
						p.sendMessage(plugin.getString("Messages.Commands.List.List").replace("%ONLINE_COUNT%", String.valueOf(online.split(",").length-1))
								.replace("%OFFLINE_COUNT%", String.valueOf(offline.split(",").length-1)).replace("%ONLINE%", online).replace("%OFFLINE%", offline));
						return true;
						
					}
					p.sendMessage(plugin.getString("Messages.Commands.List.WrongUsage"));
					return false;
				}
				
				/*
				 * JUMP
				 */
				if(args[0].equalsIgnoreCase("jump")) {
					if(this.ConfigCfg.getBoolean("Friends.Options.EnableJumping")) {
						if(args.length == 2) {
							if(Bukkit.getPlayer(args[1]) == null) {
								p.sendMessage(plugin.getString("Messages.Commands.Jumping.PlayerOffline"));
								return false;
							}
							Player toJump = Bukkit.getPlayer(args[1]);
							PlayerUtilities puP = new PlayerUtilities(p);
							PlayerUtilities puT = new PlayerUtilities(toJump);
							
							if(puP.getFriends().contains(toJump)) {
								if(puT.getOptions().contains("option_noJumping")) {
									p.sendMessage(plugin.getString("Messages.Commands.Jumping.Disabled"));
									return false;
								}
								
								p.teleport(toJump);
								p.sendMessage(plugin.getString("Messages.Commands.Jumping.Jump.Jumper").replace("%PLAYER%", toJump.getName()));
								toJump.sendMessage(plugin.getString("Messages.Commands.Jumping.Jump.ToJump").replace("%PLAYER%", p.getName()));
								return true;
								
							}
							p.sendMessage(plugin.getString("Messages.Commands.Jumping.NoFriend"));
							return false;
						}
						p.sendMessage(plugin.getString("Messages.Commands.Jumping.WrongUsage"));
					}
					return false;
				}
				
				/*
				 * INFO
				 */
				if(args[0].equalsIgnoreCase("info")) {
					p.sendMessage(plugin.prefix + " §9--------------| §6Friends §9|--------------");
					p.sendMessage(plugin.prefix + " §3Author: §fHyChrod");
					if(!UpdateChecker.check()) {
						p.sendMessage(plugin.prefix + " §3Version: §f" + plugin.getDescription().getVersion() + " §4(Outdated)");
					} else {
						p.sendMessage(plugin.prefix + " §3Version: §f" + plugin.getDescription().getVersion() + " §2(Newest)");
					}
					p.sendMessage(plugin.prefix + " §9--------------| §6Friends §9|--------------");
					return true;
				}
				
				p.sendMessage(plugin.getString("Messages.Commands.Unknown"));
				return false;
			}
			
		} else {
			p.sendMessage(plugin.getString("Messages.Commands.NoPerm"));
			return false;
		}
		return false;
	}

}
