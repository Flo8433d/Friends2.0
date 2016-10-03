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

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.BungeeSQL_Manager;
import de.HyChrod.Friends.Util.InventoryBuilder;
import de.HyChrod.Friends.Util.PlayerUtilities;
import de.HyChrod.Friends.Util.ReflectionsManager;
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
		if(args.length > 0) {
			if(args[0].equalsIgnoreCase("reload")) {
				if(sender.hasPermission("Friends.Commands.Reload")) {
					if(args.length != 1) {
						sender.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/f reload"));
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
					InventoryBuilder.MAIN_INVENTORY(plugin, p);
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

					if (puP.getFriends().contains(toRemove)) {

						puP.removeFriend(toRemove, false);
						puT.removeFriend(p, false);
						p.sendMessage(plugin.getString("Messages.Commands.Remove.Remove.Remover").replace("%PLAYER%",
								toRemove.getName()));
						if (isOnline(toRemove)) {
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
				if (puP.getFriends().contains(toSend)) {
					PlayerUtilities puT = new PlayerUtilities(toSend);
					if (isOnline(toSend)) {
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

					if (puP.getBlocked().contains(toBlock)) {
						p.sendMessage(plugin.getString("Messages.Commands.Block.AlreadyBlocked"));
						return false;
					}
					if (isOnline(toBlock) && puP.getFriends().contains(toBlock)) {
						Bukkit.getPlayer(toBlock.getUniqueId()).sendMessage(plugin
								.getString("Messages.Commands.Block.Block.ToBlock").replace("%PLAYER%", p.getName()));
					}

					puP.addBlocked(toBlock, false);
					puP.removeFriend(toBlock, false);
					puP.removeRequest(toBlock, false);
					puT.removeFriend(p, false);
					puT.removeRequest(p, false);

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

					if (puP.getBlocked().contains(toUnblock)) {
						puP.removeBlocked(toUnblock, false);
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

					if (puP.getRequests().contains(toAccept)) {
						if (puP.getFriends().size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit")) {
							if (!p.hasPermission("Friends.ExtraFriends") || puP.getFriends().size() > FileManager.ConfigCfg
									.getInt("Friends.Options.FriendLimit+")) {
								p.sendMessage(plugin.getString("Messages.Commands.Accept.LimitReached.Accepter"));
								return false;
							}
						}
						if (puT.getFriends().size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit")) {
							if (!p.hasPermission("Friends.ExtraFriends") || puT.getFriends().size() > FileManager.ConfigCfg
									.getInt("Friends.Options.FriendLimit+")) {
								p.sendMessage(plugin.getString("Messages.Commands.Accept.LimitReached.Requester"));
								return false;
							}
						}

						puP.addFriend(toAccept, false);
						puT.addFriend(p, false);
						puP.removeRequest(toAccept, false);
						p.sendMessage(plugin.getString("Messages.Commands.Accept.Accept.Accepter").replace("%PLAYER%",
								toAccept.getName()));
						if (isOnline(toAccept)) {
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

					if (puP.getRequests().contains(toDeny)) {

						puP.removeRequest(toDeny, false);
						p.sendMessage(plugin.getString("Messages.Commands.Deny.Deny.Denier").replace("%PLAYER%",
								toDeny.getName()));
						if (isOnline(toDeny)) {
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
						pu.toggleOption("option_noRequests", false);
						p.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleRequests"));
						return true;
					}
					if (args[1].equalsIgnoreCase("chat")) {
						if (!checkPerm(p, "Friends.Commands.Toggle.Chat"))
							return false;
						pu.toggleOption("option_noChat", false);
						p.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleChat"));
						return true;
					}
					if (args[1].equalsIgnoreCase("jumping")) {
						if (!checkPerm(p, "Friends.Commands.Toggle.Jumping"))
							return false;
						pu.toggleOption("option_noJumping", false);
						p.sendMessage(plugin.getString("Messages.Commands.Toggle.ToggleJumping"));
						return true;
					}
					if (args[1].equalsIgnoreCase("msg")) {
						if (!checkPerm(p, "Friends.Commands.Toggle.Msg"))
							return false;
						pu.toggleOption("option_noMsg", false);
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
					for (OfflinePlayer friend : pu.getFriends()) {
						if (isOnline(friend))
							online = online + friend.getName() + ", ";
						if (!isOnline(friend))
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
						if (!isOnline(Bukkit.getOfflinePlayer(args[1]))) {
							p.sendMessage(plugin.getString("Messages.Commands.Jumping.PlayerOffline"));
							return false;
						}
						OfflinePlayer toJump = Bukkit.getOfflinePlayer(args[1]);
						PlayerUtilities puP = new PlayerUtilities(p);
						PlayerUtilities puT = new PlayerUtilities(toJump);

						if (puP.getFriends().contains(toJump)) {
							if (puT.getOptions().contains("option_noJumping")) {
								p.sendMessage(plugin.getString("Messages.Commands.Jumping.Disabled"));
								return false;
							}

							p.sendMessage(plugin.getString("Messages.Commands.Jumping.Jump.Jumper").replace("%PLAYER%",
									toJump.getName()));
							sendMessage(p, toJump.getName(), plugin.getString("Messages.Commands.Jumping.Jump.ToJump")
									.replace("%PLAYER%", p.getName()));
							
							if(Friends.bungeeMode) {
								String server = BungeeSQL_Manager.getServer(toJump.getUniqueId().toString());
								ByteArrayDataOutput out = ByteStreams.newDataOutput();
								try {
									out.writeUTF("Connect");
									out.writeUTF(server);
								} catch (Exception ee) {
									ee.printStackTrace();
								}
								p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
								return true;
							}
							p.teleport((Player)toJump);
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
				if (!checkPerm(p, "Friends.Commands.Info"))
					return false;
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

					final OfflinePlayer toAdd = Bukkit.getOfflinePlayer(args[1]);
					if (toAdd.equals(p)) {
						p.sendMessage(plugin.getString("Messages.Commands.Add.SendSelf"));
						return false;
					}
					if (!isOnline(toAdd)) {
						p.sendMessage(plugin.getString("Messages.Commands.Add.PlayerOffline"));
						return false;
					}

					PlayerUtilities puP = new PlayerUtilities(p);
					PlayerUtilities puT = new PlayerUtilities(toAdd);

					if (puT.getFriends().contains(p)) {
						p.sendMessage(plugin.getString("Messages.Commands.Add.AlreadyFriends"));
						return false;
					}
					if (puT.getRequests().contains(p)) {
						p.sendMessage(plugin.getString("Messages.Commands.Add.AlreadyRequested"));
						return false;
					}
					if (puT.getBlocked().contains(p)) {
						p.sendMessage(plugin.getString("Messages.Commands.Add.Blocked.ToAdd"));
						return false;
					}
					if (puP.getBlocked().contains(toAdd)) {
						p.sendMessage(plugin.getString("Messages.Commands.Add.Blocked.Requester"));
						return false;
					}
					if (puT.getOptions().contains("option_noRequests")) {
						p.sendMessage(plugin.getString("Messages.Commands.Add.NoRequests"));
						return false;
					}
					if (puP.getFriends().size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit")) {
						if (!p.hasPermission("Friends.ExtraFriends")
								|| puP.getFriends().size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
							p.sendMessage(plugin.getString("Messages.Commands.Add.LimitReached.Requester"));
							return false;
						}
					}
					if (puT.getFriends().size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit")) {
						if (!p.hasPermission("Friends.ExtraFriends")
								|| puT.getFriends().size() > FileManager.ConfigCfg.getInt("Friends.Options.FriendLimit+")) {
							p.sendMessage(plugin.getString("Messages.Commands.Add.LimitReached.ToAdd"));
							return false;
						}
					}
					puT.addRequest(p, false);
					if (Friends.bungeeMode) {
						Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
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

									p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}, 20);
					} else {
						Player adding = Bukkit.getPlayer(toAdd.getName());
						adding.sendMessage(this.plugin.getString("Messages.Commands.Add.Add.ToAdd.0")
								.replace("%PLAYER%", p.getName()));
						adding.sendMessage(this.plugin.getString("Messages.Commands.Add.Add.ToAdd.1")
								.replace("%PLAYER%", p.getName()));

						String[] msgs = new String[2];
						msgs[0] = this.plugin.getString("Messages.Commands.Add.Add.AcceptButton");
						msgs[1] = this.plugin.getString("Messages.Commands.Add.Add.DenyButton");

						String[] hover = new String[2];
						hover[0] = this.plugin.getString("Messages.Commands.Add.Add.AcceptHover");
						hover[1] = this.plugin.getString("Messages.Commands.Add.Add.DenyHover");

						String[] command = new String[2];
						command[0] = "/friend accept %name%";
						command[1] = "/friend deny %name%";
						ReflectionsManager.sendHoverMessage(adding, p.getName(),
								this.plugin.getString("Messages.Commands.Add.Add.ToAdd.2")
										.replace("%ACCEPT_BUTTON%", "").replace("%DENY_BUTTON%", ""),
								msgs, hover, command);
						adding.sendMessage(this.plugin.getString("Messages.Commands.Add.Add.ToAdd.3")
								.replace("%PLAYER%", p.getName()));
					}
					p.sendMessage(plugin.getString("Messages.Commands.Add.Add.Requester").replace("%PLAYER%",
							toAdd.getName()));
					return true;
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

	public boolean isOnline(OfflinePlayer player) {
		if (Friends.bungeeMode) {
			return BungeeSQL_Manager.isOnline(player.getUniqueId().toString());
		}
		return player.isOnline();
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
