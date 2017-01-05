/*
*
* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.Listeners;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.SQL.QueryRunnable;
import de.HyChrod.Friends.Utilities.FriendPlayer;
import de.HyChrod.Friends.Utilities.PlayerUtilities;
import de.HyChrod.Friends.Utilities.ReflectionsManager;

public class BungeeMessagingListener implements PluginMessageListener {

	@SuppressWarnings("deprecation")
	@Override
	public synchronized void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord"))
			return;

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();

		try {
			if (subchannel.equals("AcceptPlayer") || subchannel.equals("AddingPlayer")
					|| subchannel.equals("BlockPlayer") || subchannel.equals("RemovePlayer") || subchannel.equals("DenyPlayer") 
					|| subchannel.equals("UnblockPlayer") || subchannel.equals("ToggleOption") || subchannel.equals("UpdateStatus")
					|| subchannel.equals("UpdateLastonline")) {
				
				short len = in.readShort();
				byte[] msgbytes = new byte[len];
				in.readFully(msgbytes);

				DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
				String somedata = msgin.readUTF();

				String[] players = somedata.split("@");
				if (Bukkit.getPlayer(players[0]) != null) {
					if(subchannel.equals("UpdateLastonline")) {
						OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(players[0]));
						Friends.getInstance().pool.execute(new Runnable() {
							
							@Override
							public void run() {
								try {
									FriendPlayer FP = FriendPlayer.getPlayer(p.getUniqueId().toString());
									while(!FP.isFinshed)
										synchronized (this) {
											wait(5L);
										}
									FP.setLastonline(Long.parseLong(players[1]));
								} catch (Exception ex) {ex.printStackTrace();}
							}
						});
					}
					if(subchannel.equals("UpdateStatus")) {
						OfflinePlayer p = Bukkit.getOfflinePlayer(players[0]);
						String status = players[1];
						Friends.getInstance().pool.execute(new Runnable() {
							
							@Override
							public void run() {
								try {
									FriendPlayer FP = FriendPlayer.getPlayer(p.getUniqueId().toString());
									while(!FP.isFinshed)
										synchronized (this) {
											wait(5L);
										}
									FP.setStatus(status);
								} catch (Exception ex) {ex.printStackTrace();}
							}
						});
						return;
					}
					Player p = Bukkit.getPlayer(players[0]);
					if(subchannel.equals("ToggleOption")) {
						String option = players[1];
						Friends.getInstance().pool.execute(new Runnable() {
							public void run() {
								try {
									PlayerUtilities pU = PlayerUtilities.getUtilities(p.getUniqueId().toString());
									while(!pU.isFinished)
										synchronized (this) {
											wait(5L);
										}
									pU.toggleOption(option);
								} catch (Exception ex) {ex.printStackTrace();}
							}
						});
					}
					if(subchannel.equals("UnblockPlayer")) {
						OfflinePlayer toUnblock = Bukkit.getOfflinePlayer(players[1]);
						Friends.getInstance().pool.execute(new Runnable() {
							
							@Override
							public void run() {
								try {
									PlayerUtilities pT = PlayerUtilities.getUtilities(toUnblock.getUniqueId().toString());
									while(!pT.isFinished)
										synchronized (this) {
											wait(5L);
										}
									if(pT.isBlocked(p.getUniqueId().toString()))
										pT.removeBlocked(p.getUniqueId().toString());
								} catch (Exception ex) {ex.printStackTrace();}
							}
						});
					}
					if(subchannel.equals("DenyPlayer")) {
						OfflinePlayer toDeny = Bukkit.getOfflinePlayer(players[1]);
						Friends.getInstance().pool.execute(new  Runnable() {
							
							@Override
							public void run() {
								try {
									PlayerUtilities pT = PlayerUtilities.getUtilities(toDeny.getUniqueId().toString());
									while(!pT.isFinished)
										synchronized (this) {
											wait(5L);
										}
									if(pT.hasRequest(p.getUniqueId().toString()))
										pT.removeRequest(p.getUniqueId().toString());
								} catch (Exception ex) {ex.printStackTrace();}
							}
						});
					}
					if(subchannel.equals("RemovePlayer")) {
						OfflinePlayer toRemove = Bukkit.getOfflinePlayer(players[1]);
						
						Friends.getInstance().pool.execute(new Runnable() {
							
							@Override
							public void run() {
								try {
									PlayerUtilities pU = PlayerUtilities.getUtilities(p.getUniqueId().toString());
									while(!pU.isFinished)
										synchronized (this) {
											wait(5L);
										}
									if(pU.isFriend(toRemove.getUniqueId().toString()))
										pU.removeFriend(toRemove.getUniqueId().toString());
								} catch (Exception ex) {ex.printStackTrace();}
							}
						});
					}
					if (subchannel.equals("BlockPlayer")) {
						OfflinePlayer toBlock = Bukkit.getOfflinePlayer(players[1]);

						Friends.getInstance().pool.execute(new Runnable() {

							@Override
							public void run() {
								try {
									PlayerUtilities pU = PlayerUtilities.getUtilities(p.getUniqueId().toString());
									while(!pU.isFinished)
										synchronized (this) {
											wait(5L);
										}
									PlayerUtilities pT = PlayerUtilities.getUtilities(toBlock.getUniqueId().toString());
									while(!pT.isFinished)
										synchronized (this) {
											wait(5L);
										}
									if(!pT.isBlocked(p.getUniqueId().toString()))
										pT.addBlocked(p.getUniqueId().toString());
									if (pU.hasRequest(toBlock.getUniqueId().toString())) {
										pU.removeRequest(toBlock.getUniqueId().toString());
									}
									if (pU.isFriend(toBlock.getUniqueId().toString())) {
										pU.removeFriend(toBlock.getUniqueId().toString());
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						});
					}
					if (subchannel.equals("AcceptPlayer")) {
						OfflinePlayer toAdd = Bukkit.getOfflinePlayer(players[1]);

						Friends.getInstance().pool.execute(new Runnable() {

							@Override
							public void run() {
								PlayerUtilities pU = PlayerUtilities.getUtilities(p.getUniqueId().toString());
								try {
									while (!pU.isFinished)
										synchronized (this) {
											wait(5L);
										}
									pU.addFriend(toAdd.getUniqueId().toString());
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						});
					}
					if (subchannel.equals("AddingPlayer")) {

						p.sendMessage(Friends.getInstance().getString("Messages.Commands.Add.Add.ToAdd.0")
								.replace("%PLAYER%", players[1]));
						p.sendMessage(Friends.getInstance().getString("Messages.Commands.Add.Add.ToAdd.1")
								.replace("%PLAYER%", players[1]));

						String[] msgs = new String[2];
						msgs[0] = Friends.getInstance().getString("Messages.Commands.Add.Add.AcceptButton");
						msgs[1] = Friends.getInstance().getString("Messages.Commands.Add.Add.DenyButton");

						String[] hover = new String[2];
						hover[0] = Friends.getInstance().getString("Messages.Commands.Add.Add.AcceptHover");
						hover[1] = Friends.getInstance().getString("Messages.Commands.Add.Add.DenyHover");

						String[] command = new String[2];
						command[0] = "/friend accept %name%";
						command[1] = "/friend deny %name%";

						ReflectionsManager.sendHoverMessage(p, players[1],
								Friends.getInstance().getString("Messages.Commands.Add.Add.ToAdd.2")
										.replace("%ACCEPT_BUTTON%", "").replace("%DENY_BUTTON%", ""),
								msgs, hover, command);
						p.sendMessage(Friends.getInstance().getString("Messages.Commands.Add.Add.ToAdd.3")
								.replace("%PLAYER%", players[1]));

						PlayerUtilities pU = PlayerUtilities.getUtilities(p.getUniqueId().toString());
						pU.addRequest(Bukkit.getOfflinePlayer(players[1]).getUniqueId().toString());
						return;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void sendMessage(Player p, String toSend, String message) {
		if (!Friends.bungeemode) {
			Player send = Bukkit.getPlayer(toSend);
			send.sendMessage(message);
			return;
		}
		sendToBungeeCord(p, "Message", toSend, message);
	}
	
	public static void getServer(OfflinePlayer player, Callback<String> callback) {
		new QueryRunnable("SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + player.getUniqueId().toString() + "'", new Callback<ResultSet>() {

			@Override
			public void call(ResultSet rs) {
				try  {
					if(rs.next() && rs.getString("SERVER") != null)
						callback.call(rs.getString("SERVER"));
				} catch (Exception ex) {
					ex.printStackTrace();
					callback.call(null);
				}
			}
		}).runTaskAsynchronously(Friends.getInstance());
	}

	public static void isOnline(OfflinePlayer player, Callback<Boolean> callback) {
		if (!Friends.bungeemode) {
			try {
				callback.call(player != null && player.isOnline());
			} catch (Exception ex) {callback.call(false);}
			return;
		}
		new QueryRunnable("SELECT * FROM friends2_0_BUNGEE WHERE UUID= '" + player.getUniqueId().toString() + "';",
				new Callback<ResultSet>() {

					@Override
					public void call(ResultSet rs) {
						try {
							if ((!rs.next()) || (Integer.valueOf(rs.getInt("ONLINE")) == null))
								;
							Integer value = rs.getInt("ONLINE");
							callback.call(value != 0);
						} catch (Exception ex) {
							callback.call(false);
						}
					}
				}).runTaskAsynchronously(Friends.getInstance());
		return;
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
