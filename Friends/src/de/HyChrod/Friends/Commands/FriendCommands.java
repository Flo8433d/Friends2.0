/*
*
#* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.Commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.BungeeSubCommands.Accept_Command;
import de.HyChrod.Friends.Commands.BungeeSubCommands.Block_Command;
import de.HyChrod.Friends.Commands.BungeeSubCommands.Deny_Command;
import de.HyChrod.Friends.Commands.BungeeSubCommands.List_Command;
import de.HyChrod.Friends.Commands.BungeeSubCommands.MSG_Command;
import de.HyChrod.Friends.Commands.BungeeSubCommands.Remove_Command;
import de.HyChrod.Friends.Commands.BungeeSubCommands.Unblock_Command;
import de.HyChrod.Friends.Listeners.BungeeMessagingListener;
import de.HyChrod.Friends.Listeners.ChatListener;
import de.HyChrod.Friends.SQL.BungeeSQL_Manager;
import de.HyChrod.Friends.Util.InventoryBuilder;
import de.HyChrod.Friends.Util.InventoryTypes;
import de.HyChrod.Friends.Util.PlayerUtilities;
import de.HyChrod.Friends.Util.UpdateChecker;

public class FriendCommands implements CommandExecutor {

	private Friends plugin;
	private FileManager mgr = new FileManager();

	public FriendCommands(Friends friends) {
		this.plugin = friends;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("Friends.Commands.Reload")) {
					if (args.length != 1) {
						sender.sendMessage(
								plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f reload"));
						return false;
					}
					this.mgr.reloadConfigs(plugin, true);
					sender.sendMessage(plugin.getString("Messages.Commands.Reload.Reloaded"));
					return true;
				}
				sender.sendMessage(plugin.getString("Messages.Commands.NoPerm"));
				return false;
			}
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage(plugin.getString("Messages.Commands.NoPlayer"));
			return false;
		}
		final Player p = (Player) sender;
		if (p.hasPermission("Friends.Use")) {
			
			if (args.length == 0) {
				if (FileManager.ConfigCfg.getBoolean("Friends.GUI.Enable")) {
					InventoryBuilder.openInv(p, InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.MAIN, false));
					return true;
				}
				p.performCommand("friends help");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("help")) {
				if(args.length == 1) {
					for(String value : FileManager.MessagesCfg.getConfigurationSection("Messages.Commands.Help.Page1").getKeys(false))
						p.sendMessage(plugin.getString("Messages.Commands.Help.Page1." + value));
					return true;
				}
				String page = args[1];
				if(FileManager.MessagesCfg.getString("Messages.Commands.Help.Page" + page) == null) {
					p.sendMessage(plugin.getString("Messages.Commands.Help.WrongSite"));
					return false;
				}
				for(String value : FileManager.MessagesCfg.getConfigurationSection("Messages.Commands.Help.Page" + page).getKeys(false))
					p.sendMessage(plugin.getString("Messages.Commands.Help.Page" + page + "." + value));
				return false;
			}

			/*
			 * REMOVE
			 */
			if (args[0].equalsIgnoreCase("remove")) {
				if (!checkPerm(p, "Friends.Commands.Remove"))
					return false;
				if (args.length == 2) {
					OfflinePlayer toRemove = Bukkit.getOfflinePlayer(args[1]);
					if(Friends.bungeeMode) {
						new Remove_Command(plugin, p, toRemove);
						return false;
					}
					PlayerUtilities puP = new PlayerUtilities(p);
					PlayerUtilities puT = new PlayerUtilities(toRemove);

					if (puP.get(0, false).contains(toRemove.getUniqueId().toString())) {

						puP.update(toRemove.getUniqueId().toString(), 0, false);
						puT.update(p.getUniqueId().toString(), 0, false);
						p.sendMessage(plugin.getString("Messages.Commands.Remove.Remove.Remover").replace("%PLAYER%",
								toRemove.getName()));
						if (BungeeMessagingListener.isOnline(toRemove)) {
							sendMessage(p, toRemove.getName(),
									plugin.getString("Messages.Commands.Remove.Remove.ToRemove").replace("%PLAYER%",
											p.getName()));
						}
						return true;

					}
					p.sendMessage(plugin.getString("Messages.Commands.Remove.NoFriends"));
					return false;
				}
				p.sendMessage(
						plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f remove <Player>"));
				return false;
			}

			/*
			 * MSG
			 */
			if (args[0].equalsIgnoreCase("msg")) {
				if (!checkPerm(p, "Friends.Commands.Msg"))
					return false;
				if (!FileManager.ConfigCfg.getBoolean("Friends.FriendChat.FriendMSG")) {
					p.sendMessage(plugin.getString("Messages.Commands.MSG.Denied"));
					return false;
				}
				if (args.length < 3) {
					p.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%",
							"/f msg <Player> <Message>"));
					return false;
				}
				OfflinePlayer toSend = Bukkit.getOfflinePlayer(args[1]);
				if(Friends.bungeeMode) {
					new MSG_Command(plugin, p, toSend, args);
					return false;
				}
				PlayerUtilities puP = new PlayerUtilities(p);
				if (puP.get(3, false).contains("option_noMsg")) {
					p.sendMessage(plugin.getString("Messages.Commands.MSG.DisabledSelf"));
					return false;
				}
				if (puP.get(0, false).contains(toSend.getUniqueId().toString())) {
					PlayerUtilities puT = new PlayerUtilities(toSend);
					if (BungeeMessagingListener.isOnline(toSend)) {
						if (puT.get(3, false).contains("option_noMsg")) {
							p.sendMessage(plugin.getString("Messages.Commands.MSG.Disabled"));
							return false;
						}

						String msg = "";
						for (int i = 2; i < args.length; i++) {
							msg = msg + args[i] + " ";
						}
						sendMessage(p, toSend.getName(),
								plugin.getString("Messages.Commands.MSG.Format").replace("%PREFIX%", plugin.prefix)
										.replace("%PLAYER%", p.getName()).replace("%MESSAGE%", msg));
						p.sendMessage(plugin.getString("Messages.Commands.MSG.Send")
								.replace("%PLAYER%", toSend.getName()).replace("%MESSAGE%", msg));
						return true;
					}
					p.sendMessage(plugin.getString("Messages.Commands.MSG.PlayerOffline"));
					return false;
				}
				p.sendMessage(plugin.getString("Messages.Commands.MSG.NoFriends"));
				return false;
			}

			/*
			 * BLOCK
			 */
			if (args[0].equalsIgnoreCase("block")) {
				if (!checkPerm(p, "Friends.Commands.Block"))
					return false;
				if (args.length == 2) {
					OfflinePlayer toBlock = Bukkit.getOfflinePlayer(args[1]);
					if(Friends.bungeeMode) {
						new Block_Command(plugin, p, toBlock);
						return false;
					}
					PlayerUtilities puP = new PlayerUtilities(p);
					PlayerUtilities puT = new PlayerUtilities(toBlock);

					if (puP.get(2, false).contains(toBlock.getUniqueId().toString())) {
						p.sendMessage(plugin.getString("Messages.Commands.Block.AlreadyBlocked"));
						return false;
					}
					if (BungeeMessagingListener.isOnline(toBlock)
							&& puP.get(0, false).contains(toBlock.getUniqueId().toString())) {
						Bukkit.getPlayer(toBlock.getUniqueId()).sendMessage(plugin
								.getString("Messages.Commands.Block.Block.ToBlock").replace("%PLAYER%", p.getName()));
					}

					puP.update(toBlock.getUniqueId().toString(), 2, true);
					puP.update(toBlock.getUniqueId().toString(), 0, false);
					puP.update(toBlock.getUniqueId().toString(), 1, false);
					puT.update(p.getUniqueId().toString(), 0, false);
					puT.update(p.getUniqueId().toString(), 1, false);

					p.sendMessage(plugin.getString("Messages.Commands.Block.Block.Blocker").replace("%PLAYER%",
							toBlock.getName()));
					return true;

				}
				p.sendMessage(
						plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f block <Player>"));
				return false;
			}

			/*
			 * UNBLOCK
			 */
			if (args[0].equalsIgnoreCase("unblock")) {
				if (!checkPerm(p, "Friends.Commands.Unblock"))
					return false;
				if (args.length == 2) {
					OfflinePlayer toUnblock = Bukkit.getOfflinePlayer(args[1]);
					if(Friends.bungeeMode) {
						new Unblock_Command(plugin, p, toUnblock);
						return false;
					}
					PlayerUtilities puP = new PlayerUtilities(p);

					if (puP.get(2, false).contains(toUnblock.getUniqueId().toString())) {
						puP.update(toUnblock.getUniqueId().toString(), 2, false);
						p.sendMessage(plugin.getString("Messages.Commands.Unblock.Unblock").replace("%PLAYER%",
								toUnblock.getName()));
						return true;
					}
					p.sendMessage(plugin.getString("Messages.Commands.Unblock.NotBlocked"));
					return false;
				}
				p.sendMessage(
						plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f unblock <Player>"));
				return false;
			}

			/*
			 * ACCEPT
			 */
			if (args[0].equalsIgnoreCase("accept")) {
				if (!checkPerm(p, "Friends.Commands.Accept"))
					return false;
				if (args.length == 2) {
					OfflinePlayer toAccept = Bukkit.getOfflinePlayer(args[1]);
					if(Friends.bungeeMode) {
						new Accept_Command(plugin, p, toAccept);
						return false;
					}
					PlayerUtilities puP = new PlayerUtilities(p);
					PlayerUtilities puT = new PlayerUtilities(toAccept);

					if (puP.get(1, false).contains(toAccept.getUniqueId().toString())) {
						if (puP.get(0, true).size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit")) {
							if (!p.hasPermission("Friends.ExtraFriends") || puP.get(0, true)
									.size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
								p.sendMessage(plugin.getString("Messages.Commands.Accept.LimitReached.Accepter"));
								return false;
							}
						}
						if (puT.get(0, true).size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit"))
							if(toAccept.isOnline()) {
								Player toCheck = Bukkit.getPlayer(toAccept.getName());
								if (!toCheck.hasPermission("Friends.ExtraFriends") || puT.get(0, true)
										.size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
									p.sendMessage(plugin.getString("Messages.Commands.Accept.LimitReached.Requester"));
									return false;
								}
							}

						puP.update(toAccept.getUniqueId().toString(), 0, true);
						puT.update(p.getUniqueId().toString(), 0, true);
						puP.update(toAccept.getUniqueId().toString(), 1, false);
						p.sendMessage(plugin.getString("Messages.Commands.Accept.Accept.Accepter").replace("%PLAYER%",
								toAccept.getName()));
						if (BungeeMessagingListener.isOnline(toAccept)) {
							sendMessage(p, toAccept.getName(), plugin.getString("Messages.Commands.Accept.Accept.ToAccept").replace("%PLAYER%", p.getName()));
						}
						return true;
					}
					p.sendMessage(plugin.getString("Messages.Commands.Accept.NoRequest"));
					return false;
				}
				p.sendMessage(
						plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f accept <Player>"));
				return false;
			}

			/*
			 * ACCEPTALL
			 */
			if (args[0].equalsIgnoreCase("acceptall")) {
				if (!checkPerm(p, "Friends.Commands.Acceptall"))
					return false;
				if (args.length == 1) {
					PlayerUtilities pu = new PlayerUtilities(p);
					if (pu.get(1, true).isEmpty()) {
						p.sendMessage(plugin.getString("Messages.Commands.Acceptall.NoRequests"));
						return false;
					}

					int i = 0;
					for (Object requests : pu.get(1, true)) {
						i++;
						OfflinePlayer ToAccept = null;
						if (Friends.bungeeMode)
							ToAccept = ((OfflinePlayer) requests);
						else
							ToAccept = Bukkit.getOfflinePlayer(UUID.fromString(((String) requests)));
						
						if(Friends.bungeeMode) {
							new Accept_Command(plugin, p, ToAccept);
						} else {
							PlayerUtilities tu = new PlayerUtilities(ToAccept);
							pu.update(ToAccept.getUniqueId().toString(), 0, true);
							tu.update(p.getUniqueId().toString(), 0, true);
							pu.update(ToAccept.getUniqueId().toString(), 1, false);
							if (BungeeMessagingListener.isOnline(ToAccept)) {
								sendMessage(p, ToAccept.getName(),plugin.getString("Messages.Commands.Accept.Accept.ToAccept").replace("%PLAYER%", p.getName()));
							}
						}
					}
					p.sendMessage(plugin.getString("Messages.Commands.Acceptall.Accept").replace("%COUNT%",
							String.valueOf(i)));
					return true;
				}
				p.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f acceptall"));
				return false;
			}

			/*
			 * DENY
			 */
			if (args[0].equalsIgnoreCase("deny")) {
				if (!checkPerm(p, "Friends.Commands.Deny"))
					return false;
				if (args.length == 2) {
					OfflinePlayer toDeny = Bukkit.getOfflinePlayer(args[1]);
					if(Friends.bungeeMode) {
						new Deny_Command(plugin, p, toDeny);
						return false;
					}
					PlayerUtilities puP = new PlayerUtilities(p);

					if (puP.get(1, false).contains(toDeny.getUniqueId().toString())) {

						puP.update(toDeny.getUniqueId().toString(), 1, false);
						p.sendMessage(plugin.getString("Messages.Commands.Deny.Deny.Denier").replace("%PLAYER%",
								toDeny.getName()));
						if (BungeeMessagingListener.isOnline(toDeny)) {
							sendMessage(p, toDeny.getName(), plugin.getString("Messages.Commands.Deny.Deny.ToDeny")
									.replace("%PLAYER%", p.getName()));
						}
						return true;

					}
					p.sendMessage(plugin.getString("Messages.Commands.Deny.NoRequest"));
					return false;
				}
				p.sendMessage(
						plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f deny <Player>"));
				return false;
			}

			/*
			 * TOGGLE OPTION
			 */
			if (args[0].equalsIgnoreCase("toggle")) {
				if (args.length == 2) {

					PlayerUtilities pu = new PlayerUtilities(p);
					if (args[1].equalsIgnoreCase("requests")) {
						if (!checkPerm(p, "Friends.Commands.Toggle.Requests"))
							return false;
						pu.toggleOption("option_noRequests");
						p.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleRequests"));
						return true;
					}
					if (args[1].equalsIgnoreCase("chat")) {
						if (!checkPerm(p, "Friends.Commands.Toggle.Chat"))
							return false;
						pu.toggleOption("option_noChat");
						p.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleChat"));
						return true;
					}
					if (args[1].equalsIgnoreCase("jumping")) {
						if (!checkPerm(p, "Friends.Commands.Toggle.Jumping"))
							return false;
						pu.toggleOption("option_noJumping");
						p.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleJumping"));
						return true;
					}
					if (args[1].equalsIgnoreCase("msg")) {
						if (!checkPerm(p, "Friends.Commands.Toggle.Msg"))
							return false;
						pu.toggleOption("option_noMsg");
						p.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleMsg"));
						return true;
					}
					if (args[1].equalsIgnoreCase("spychat")) {
						if (!checkPerm(p, "Friends.Commands.SpyChat"))
							return false;

						if (!FileManager.ConfigCfg.getBoolean("Friends.FriendChat.SpyChat.Enable")) {
							p.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleSpyChat.Disabled"));
							return false;
						}
						if (!ChatListener.spy.contains(p)) {
							ChatListener.spy.add(p);
							p.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleSpyChat.Toggle"));
						} else {
							ChatListener.spy.remove(p);
							p.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleSpyChat.Disabled"));
						}
						return true;
					}
					p.sendMessage(plugin.getString("Messages.Commands.Toggle.NoOption"));
					return false;
				}
				p.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%",
						"/f toggle <requests|chat|jumping|msg>"));
				return false;
			}

			/*
			 * LIST
			 */
			if (args[0].equalsIgnoreCase("list")) {
				if (!checkPerm(p, "Friends.Commands.List"))
					return false;
				if (args.length == 1) {
					if(Friends.bungeeMode) {
						new List_Command(plugin, p);
						return false;
					}
					PlayerUtilities pu = new PlayerUtilities(p);

					String online = "";
					String offline = "";
					for (Object uuid : pu.get(0, true)) {
						OfflinePlayer friend = null;
						if(Friends.bungeeMode)
							friend = ((OfflinePlayer) uuid);
						else
							friend = Bukkit.getOfflinePlayer(UUID.fromString(String.valueOf(uuid)));
						if (BungeeMessagingListener.isOnline(friend))
							online = online + friend.getName() + ", ";
						if (!BungeeMessagingListener.isOnline(friend))
							offline = offline + friend.getName() + ", ";
					}
					
					for(String subString : plugin.getString("Messages.Commands.List").split("//"))
						p.sendMessage(subString.replace("%ONLINE_COUNT%", String.valueOf(online.split(",").length - 1))
								.replace("%OFFLINE_COUNT%", String.valueOf(offline.split(",").length - 1))
								.replace("%ONLINE%", online).replace("%OFFLINE%", offline));
					return true;

				}
				p.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f list"));
				return false;
			}

			/*
			 * JUMP
			 */
			if (args[0].equalsIgnoreCase("jump")) {
				if (!checkPerm(p, "Friends.Commands.Jump"))
					return false;
				if (FileManager.ConfigCfg.getBoolean("Friends.Options.EnableJumping")) {
					if (args.length == 2) {
						if (!BungeeMessagingListener.isOnline(Bukkit.getOfflinePlayer(args[1]))) {
							p.sendMessage(plugin.getString("Messages.Commands.Jumping.PlayerOffline"));
							return false;
						}
						OfflinePlayer toJump = Bukkit.getOfflinePlayer(args[1]);
						PlayerUtilities puP = new PlayerUtilities(p);
						PlayerUtilities puT = new PlayerUtilities(toJump);

						if (puP.get(0, false).contains(toJump.getUniqueId().toString())) {
							if (puT.get(3, false).contains("option_noJumping")) {
								p.sendMessage(plugin.getString("Messages.Commands.Jumping.Disabled"));
								return false;
							}

							p.sendMessage(plugin.getString("Messages.Commands.Jumping.Jump.Jumper").replace("%PLAYER%",
									toJump.getName()));
							sendMessage(p, toJump.getName(), plugin.getString("Messages.Commands.Jumping.Jump.ToJump")
									.replace("%PLAYER%", p.getName()));

							if (Friends.bungeeMode) {
								String server = String.valueOf(BungeeSQL_Manager.get(toJump, "SERVER"));
								if(FileManager.ConfigCfg.getBoolean("Friends.DisabledServers.Enable"))
									if(FileManager.ConfigCfg.getStringList("Friends.DisabledServers.Servers").contains(server)) {
										p.sendMessage(plugin.getString("Messages.Commands.Jumping.DisabledServer"));
										return false;
									}
								if(FileManager.ConfigCfg.getBoolean("Friends.EnabledServers.Enable"))
									if(!FileManager.ConfigCfg.getStringList("Friends.EnabledServers.Servers").contains(server)) {
										p.sendMessage(plugin.getString("Messages.Commands.Jumping.DisabledServer"));
										return false;
									}
								BungeeMessagingListener.sendToBungeeCord(p, "Connect", server, null);
								return true;
							}
							if(FileManager.ConfigCfg.getBoolean("Friends.DisabledWorlds.Enable")) {
								if(FileManager.ConfigCfg.getStringList("Friends.DisabledWorlds.Worlds").contains(Bukkit.getPlayer(toJump.getName())
										.getWorld().getName())) {
									p.sendMessage(plugin.getString("Messages.Commands.Jumping.DisabledWorld"));
									return false;
								}
							}
							
							p.teleport((Player) toJump);
							return true;

						}
						p.sendMessage(plugin.getString("Messages.Commands.Jumping.NoFriend"));
						return false;
					}
					p.sendMessage(
							plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f jump <Player>"));
				}
				return false;
			}

			/*
			 * INFO
			 */
			if (args[0].equalsIgnoreCase("info")) {
				p.sendMessage(plugin.prefix + " §9--------------| §6Friends §9|--------------");
				p.sendMessage(plugin.prefix + " §3Author: §fHyChrod");
				if (!UpdateChecker.check()) {
					p.sendMessage(
							plugin.prefix + " §3Version: §f" + plugin.getDescription().getVersion() + " §4(Outdated)");
				} else {
					p.sendMessage(
							plugin.prefix + " §3Version: §f" + plugin.getDescription().getVersion() + " §2(Newest)");
				}
				p.sendMessage(plugin.prefix + " §9--------------| §6Friends §9|--------------");
				return true;
			}
			/*
			 * ADD
			 */
			if (args[0].equalsIgnoreCase("add")) {
				if (!checkPerm(p, "Friends.Commands.Add"))
					return false;
				if (args.length == 2) {
					return new Command_Add(p, args[1]).addPlayer();
				}
				p.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f add <Player>"));
				return false;
			}

			p.sendMessage(plugin.getString("Messages.Commands.Unknown"));
			return false;

		} else {
			p.sendMessage(plugin.getString("Messages.Commands.NoPerm"));
			return false;
		}
	}

	public boolean checkPerm(Player player, String perm) {
		if (player.hasPermission(perm) || player.hasPermission("Friends.Commands.*")) {
			return true;
		}
		player.sendMessage(plugin.getString("Messages.Commands.NoPerm"));
		return false;
	}

	public static void sendMessage(Player p, String toSend, String message) {
		if (!Friends.bungeeMode) {
			Player send = Bukkit.getPlayer(toSend);
			send.sendMessage(message);
			return;
		}
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("Message");
			out.writeUTF(toSend);
			out.writeUTF(message);
		} catch (IOException ex) {
		}
		p.sendPluginMessage(Friends.getInstance(), "BungeeCord", b.toByteArray());
	}

}
