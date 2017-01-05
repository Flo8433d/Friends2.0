/*
*
* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.Listeners.BungeeMessagingListener;
import de.HyChrod.Friends.SQL.Callback;

public class InventoryPage {

	public Player player;
	public int site;
	public PlayerUtilities pu;
	private int onlineCounter = 0;
	private int serverCounter = 0;
	
	private List<FriendPlayer> ONLINE = Collections.synchronizedList(new ArrayList<>());
	private List<FriendPlayer> OFFLINE = Collections.synchronizedList(new ArrayList<>());
	private ConcurrentHashMap<FriendPlayer, String> SERVER = new ConcurrentHashMap<>();

	private Friends plugin;
	private InventoryTypes type = null;

	public InventoryPage(Friends plugin, Player player, int site, PlayerUtilities pu, InventoryTypes type) {
		this.player = player;
		this.site = site;
		this.pu = pu;
		this.plugin = plugin;
		this.type = type;
		this.type.applyPlayer(player);
	}

	public Inventory open(boolean open) {
		Inventory inv = Bukkit.createInventory(null,
				FileManager.ConfigCfg.getInt("Friends.GUI" + type.getS() + ".InventorySize"),
				ChatColor.translateAlternateColorCodes('&',
						FileManager.ConfigCfg.getString("Friends.GUI" + type.getS() + ".Title")));
		plugin.pool.execute(new Runnable() {
			public void run() {
				try {
					while(!pu.isFinished)
						synchronized (this) {
							wait(5L);
						}
					for (String placeholder : FileManager.ConfigCfg
							.getStringList("Friends.GUI" + type.getS() + ".PlaceholderItem.InventorySlots")) {
						inv.setItem(Integer.valueOf(placeholder) - 1, ((ItemStacks) type.getItems().get(0)).getItem());
					}
					inv.setItem(((ItemStacks) type.getItems().get(1)).getInvSlot() - 1,
							((ItemStacks) type.getItems().get(1)).getItem());
					inv.setItem(((ItemStacks) type.getItems().get(2)).getInvSlot() - 1,
							((ItemStacks) type.getItems().get(2)).getItem());
					inv.setItem(((ItemStacks) type.getItems().get(3)).getInvSlot() - 1,
							((ItemStacks) type.getItems().get(3)).getItem());
					if(type.equals(InventoryTypes.REQUEST)) {
						inv.setItem(ItemStacks.REQUESTS_ACCEPTALL.getInvSlot()-1, ItemStacks.REQUESTS_ACCEPTALL.getItem());
						inv.setItem(ItemStacks.REQUESTS_DENYALL.getInvSlot()-1, ItemStacks.REQUESTS_DENYALL.getItem());
					}
					if (type.equals(InventoryTypes.MAIN)) {
						inv.setItem(FileManager.ConfigCfg.getInt("Friends.GUI.BlockedItem.InventorySlot") - 1,
								((ItemStack) type.getItems().get(4)));
						inv.setItem(FileManager.ConfigCfg.getInt("Friends.GUI.RequestsItem.InventorySlot") - 1,
								((ItemStack) type.getItems().get(5)));
					}

					int freeSlots = 0;
					for (int i = 0; i < inv.getSize(); i++) {
						if (inv.getItem(i) == null || (inv.getItem(i) != null && inv.getItem(i).getType().equals(Material.AIR))) {
							freeSlots++;
						}
					}
					freeSlots = freeSlots * site;
					List<ItemStack> items = new ArrayList<>();
					
					if(type.equals(InventoryTypes.MAIN)) {
						for(FriendPlayer fp : type.getGet()) {
							BungeeMessagingListener.isOnline(Bukkit.getOfflinePlayer(UUID.fromString(fp.getUUID())), new Callback<Boolean>() {
								
								@Override
								public void call(Boolean isOnline) {
									onlineCounter++;
									if(isOnline) {
										ONLINE.add(fp);
										if(Friends.bungeemode) {
											BungeeMessagingListener.getServer(Bukkit.getOfflinePlayer(UUID.fromString(fp.getUUID())), new Callback<String>() {

												@Override
												public void call(String server) {
													serverCounter++;
													SERVER.put(fp, server);
												}
											});
										}
									} else {
										OFFLINE.add(fp);
									}
								}
							});
						}
						while(onlineCounter < pu.getFriends().size() || (Friends.bungeemode && ((serverCounter+OFFLINE.size()) < pu.getFriends().size())))
							synchronized (this) {
								wait(5L);
							}
					} else {
						ArrayList<FriendPlayer> FF = new ArrayList<>(type.getGet());
						for(FriendPlayer fp : FF)
							ONLINE.add(fp);
					}
					
					for(FriendPlayer OFriends : ONLINE) {
						OfflinePlayer pp = Bukkit.getOfflinePlayer(UUID.fromString(OFriends.getUUID()));
						while(!OFriends.isFinshed)
							synchronized (this) {
								wait(5L);
							}
						if(type.equals(InventoryTypes.MAIN))
							items.add(getHead(pp, OFriends, true, SERVER.get(OFriends)));
						else
							items.add(getItem(pp));
					}
					for(FriendPlayer OFriends : OFFLINE) {
						OfflinePlayer pp = Bukkit.getOfflinePlayer(UUID.fromString(OFriends.getUUID()));
						if(type.equals(InventoryTypes.MAIN))
							items.add(getHead(pp, OFriends, false, ""));
						else
							items.add(getItem(pp));
					}

					if (items.size() > freeSlots) {
						for (int i = 0; i < freeSlots; i++) {
							items.remove(0);
						}
					} else {
						items.clear();
					}
					for (ItemStack item : items) {
						inv.addItem(item);
					}
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

						@Override
						public void run() {
							player.updateInventory();
						}
					}, 5);
				} catch (Exception ex) {ex.printStackTrace();}
			}
		});
		if (open)
			player.openInventory(inv);
		return inv;
	}

	private ItemStack getHead(OfflinePlayer player, FriendPlayer fp, boolean online, String server) {
		ItemStack IS = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta SM = (SkullMeta) IS.getItemMeta();
		SM.setOwner(player.getName());
		
		String name = player != null ? player.getName() != null ? player.getName() : UUIDFetcher.getName(player.getUniqueId()) : "E R R O R";
		List<String> lore = new ArrayList<>();
		
		boolean b_online = false;
		if(player != null) {
			if (Friends.bungeemode) {
				if (online) {
					SM.setDisplayName(ChatColor.translateAlternateColorCodes('&', 
							FileManager.ConfigCfg.getString("Friends.GUI.FriendHead.NameOnline").replace("%PLAYER%", name)));
					if (FileManager.ConfigCfg.getBoolean("Friends.ShowServer.Enable")) {
						lore = new ArrayList<String>(Arrays.asList(ChatColor.translateAlternateColorCodes('&',FileManager.ConfigCfg.getString("Friends.ShowServer.Lore")
								.replace("%SERVER%", server))));
					}
					b_online = true;
				}
			}
			int[] time = fp.getLastOnline();
			if (!player.isOnline() && !b_online) {
				if (FileManager.ConfigCfg.getBoolean("Friends.GUI.FriendHead.ChangeHeadIfOffline")) {
					IS = new ItemStack(Material.SKULL_ITEM, 1, (short) 0);
					SM = (SkullMeta) IS.getItemMeta();
				}
				SM.setDisplayName(ChatColor.translateAlternateColorCodes('&', 
						FileManager.ConfigCfg.getString("Friends.GUI.FriendHead.NameOffline").replace("%PLAYER%", name)));
				if (FileManager.ConfigCfg.getBoolean("Friends.Options.LastOnline.Enable") && time != null
						&& time.length >= 3 && fp.getLastonline() != 0) {
					lore = new ArrayList<String>(Arrays.asList(ChatColor.translateAlternateColorCodes('&',
							FileManager.ConfigCfg.getString("Friends.Options.LastOnline.Format")
									.replace("%days%", "" + time[3]).replace("%hours%", time[2] + "")
									.replace("%minutes%", "" + time[1]).replace("%seconds%", "" + time[0]))));
				}
			} else if (player.isOnline() && !Friends.bungeemode) {
				SM.setDisplayName(ChatColor.translateAlternateColorCodes('&', 
						FileManager.ConfigCfg.getString("Friends.GUI.FriendHead.NameOnline").replace("%PLAYER%", name)));
				if (FileManager.ConfigCfg.getBoolean("Friends.Options.ShowWorld.Enable"))
					lore = new ArrayList<String>(Arrays.asList(ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.Options.ShowWorld.Lore")
							.replace("%WORLD%", Bukkit.getPlayer(player.getUniqueId()).getWorld().getName()))));
			}
			if(fp.getStatus() != null && fp.getStatus().length() >= 1) {
				lore.add("");
				for(String s : splitStatus(fp.getStatus())) {
					lore.add(s);
				}
			}
		}
		SM.setLore(lore);
		IS.setItemMeta(SM);
		return IS;
	}
	
	private ItemStack getItem(OfflinePlayer player) {
		ItemStack IS = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta SM = (SkullMeta) IS.getItemMeta();
		if(player != null && type != null && SM != null && type.getName() != null && player.getName() != null) {
			SM.setDisplayName(type.getName().replace("%PLAYER%", player.getName()));
			SM.setOwner(player.getName());
		}
		IS.setItemMeta(SM);
		return IS;
	}
	
	private List<String> splitStatus(String s) {
		List<String> splitted = new ArrayList<>();
		String substring = "§e§o''";
		int counter = 0;
		for(int i = 0; i < s.length(); i++) {
			substring = substring + s.charAt(i);
			counter++;
			if(counter >= 30 && !Character.isAlphabetic(s.charAt(i)) || counter >= 45) {
				counter = 0;
				splitted.add(substring);
				substring = "§e";
			}
		}
		splitted.add(substring + "''");
		return splitted;
	}

}
