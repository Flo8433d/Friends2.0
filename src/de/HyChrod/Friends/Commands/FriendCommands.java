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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Listeners.BungeeMessagingListener;
import de.HyChrod.Friends.SQL.BungeeSQL_Manager;
import de.HyChrod.Friends.Util.InventoryBuilder;
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
					InventoryBuilder.MAIN_INVENTORY(plugin, p, true);
					return true;
				}
				p.performCommand("friends help");
				return true;
			}
			if (args.length == 1 && args[0].equalsIgnoreCase("help")
					|| (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("1"))) {
				for (int i = 0; i < 9; i++) {
					p.sendMessage(plugin.getString("Messages.Commands.Help.Page1." + i));
				}
				return true;
			}
			if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("2")) {
				for (int i = 0; i < 9; i++) {
					p.sendMessage(plugin.getString("Messages.Commands.Help.Page2." + i));
				}
				return true;
			}

			/*
			 * REMOVE
			 */
			if (args[0].equalsIgnoreCase("remove")) {
				if (!checkPerm(p, "Friends.Commands.Remove"))
					return false;
				if (args.length == 2) {
					OfflinePlayer toRemove = Bukkit.getOfflinePlayer(args[1]);
					PlayerUtilities puP = new PlayerUtilities(p);
					PlayerUtilities puT = new PlayerUtilities(toRemove);

					if (puP.get(0).contains(toRemove)) {

						puP.update(toRemove, 0, false);
						puT.update(p, 0, false);
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
				PlayerUtilities puP = new PlayerUtilities(p);
				if (puP.getOptions().contains("option_noMsg")) {
					p.sendMessage(plugin.getString("Messages.Commands.MSG.DisabledSelf"));
					return false;
				}
				if (puP.get(0).contains(toSend)) {
					PlayerUtilities puT = new PlayerUtilities(toSend);
					if (BungeeMessagingListener.isOnline(toSend)) {
						if (puT.getOptions().contains("option_noMsg")) {
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
					PlayerUtilities puP = new PlayerUtilities(p);
					PlayerUtilities puT = new PlayerUtilities(toBlock);

					if (puP.get(2).contains(toBlock)) {
						p.sendMessage(plugin.getString("Messages.Commands.Block.AlreadyBlocked"));
						return false;
					}
					if (BungeeMessagingListener.isOnline(toBlock) && puP.get(0).contains(toBlock)) {
						Bukkit.getPlayer(toBlock.getUniqueId()).sendMessage(plugin
								.getString("Messages.Commands.Block.Block.ToBlock").replace("%PLAYER%", p.getName()));
					}

					puP.update(toBlock, 2, true);
					puP.update(toBlock, 0, false);
					puP.update(toBlock, 1, false);
					puT.update(p, 0, false);
					puT.update(p, 1, false);

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
					PlayerUtilities puP = new PlayerUtilities(p);

					if (puP.get(2).contains(toUnblock)) {
						puP.update(toUnblock, 2, false);
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
					PlayerUtilities puP = new PlayerUtilities(p);
					PlayerUtilities puT = new PlayerUtilities(toAccept);

					if (puP.get(1).contains(toAccept)) {
						if (puP.get(0).size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit")) {
							if (!p.hasPermission("Friends.ExtraFriends") || puP.get(0).size() > FileManager.ConfigCfg
									.getInt("Friends.Options.FriendLimit+")) {
								p.sendMessage(plugin.getString("Messages.Commands.Accept.LimitReached.Accepter"));
								return false;
							}
						}
						if (puT.get(0).size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit")) {
							if (!p.hasPermission("Friends.ExtraFriends") || puT.get(0).size() > FileManager.ConfigCfg
									.getInt("Friends.Options.FriendLimit+")) {
								p.sendMessage(plugin.getString("Messages.Commands.Accept.LimitReached.Requester"));
								return false;
							}
						}

						puP.update(toAccept, 0, true);
						puT.update(p, 0, true);
						puP.update(toAccept, 1, false);
						p.sendMessage(plugin.getString("Messages.Commands.Accept.Accept.Accepter").replace("%PLAYER%",
								toAccept.getName()));
						if (BungeeMessagingListener.isOnline(toAccept)) {
							sendMessage(p, toAccept.getName(),
									plugin.getString("Messages.Commands.Accept.Accept.ToAccept").replace("%PLAYER%",
											p.getName()));
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
			 * DENY
			 */
			if (args[0].equalsIgnoreCase("deny")) {
				if (!checkPerm(p, "Friends.Commands.Deny"))
					return false;
				if (args.length == 2) {
					OfflinePlayer toDeny = Bukkit.getOfflinePlayer(args[1]);
					PlayerUtilities puP = new PlayerUtilities(p);

					if (puP.get(1).contains(toDeny)) {

						puP.update(toDeny, 1, false);
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
					PlayerUtilities pu = new PlayerUtilities(p);

					String online = "";
					String offline = "";
					for (OfflinePlayer friend : pu.get(0)) {
						if (BungeeMessagingListener.isOnline(friend))
							online = online + friend.getName() + ", ";
						if (!BungeeMessagingListener.isOnline(friend))
							offline = offline + friend.getName() + ", ";
					}

					p.sendMessage(plugin.getString("Messages.Commands.List.List")
							.replace("%ONLINE_COUNT%", String.valueOf(online.split(",").length - 1))
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

						if (puP.get(0).contains(toJump)) {
							if (puT.getOptions().contains("option_noJumping")) {
								p.sendMessage(plugin.getString("Messages.Commands.Jumping.Disabled"));
								return false;
							}

							p.sendMessage(plugin.getString("Messages.Commands.Jumping.Jump.Jumper").replace("%PLAYER%",
									toJump.getName()));
							sendMessage(p, toJump.getName(), plugin.getString("Messages.Commands.Jumping.Jump.ToJump")
									.replace("%PLAYER%", p.getName()));

							if (Friends.bungeeMode) {
								String server = String.valueOf(BungeeSQL_Manager.get(toJump, "SERVER"));
								BungeeMessagingListener.sendToBungeeCord(p, "Connect", server, null);
								return true;
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

	public void sendMessage(Player p, String toSend, String message) {
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
		p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
	}

}
