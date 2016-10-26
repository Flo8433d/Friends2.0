/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Listeners.BungeeMessagingListener;
import de.HyChrod.Friends.SQL.BungeeSQL_Manager;

public class InventoryPage {

	public Player player;
	public int site;
	public PlayerUtilities pu;

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

		HashMap<Integer, LinkedList<OfflinePlayer>> hash = new HashMap<>();
		hash.put(0, new LinkedList<OfflinePlayer>());
		hash.put(1, new LinkedList<OfflinePlayer>());
		for (Object uuid : type.getGet()) {
			OfflinePlayer player = null;
			if (Friends.bungeeMode)
				player = ((OfflinePlayer) uuid);
			else
				player = Bukkit.getOfflinePlayer(UUID.fromString(((String) uuid)));

			if (BungeeMessagingListener.isOnline(player)) {
				hash.get(0).add(player);
			} else {
				hash.get(1).add(player);
			}
			if (!type.equals(InventoryTypes.MAIN)) {
				ItemStack IS = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				SkullMeta SM = (SkullMeta) IS.getItemMeta();
				SM.setDisplayName(type.getName().replace("%PLAYER%", player.getName()));
				SM.setOwner(player.getName());
				IS.setItemMeta(SM);
				items.add(IS);
			}
		}
		if (type.equals(InventoryTypes.MAIN)) {
			for (int i = 0; i <= 1; i++) {
				for (OfflinePlayer player : hash.get(i))
					items.add(this.getHead(player, new PlayerUtilities(player)));
			}
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
		if (open)
			player.openInventory(inv);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			@Override
			public void run() {
				player.updateInventory();
			}
		}, 5);
		return inv;
	}

	private ItemStack getHead(OfflinePlayer player, PlayerUtilities pu) {
		ItemStack IS = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta SM = (SkullMeta) IS.getItemMeta();
		SM.setOwner(player.getName());
		
		List<String> lore = new ArrayList<>();
		
		boolean b_online = false;
		if (Friends.bungeeMode) {
			if (BungeeSQL_Manager.isOnline(player)) {
				SM.setDisplayName(ChatColor.translateAlternateColorCodes('&', 
						FileManager.ConfigCfg.getString("Friends.GUI.FriendHead.NameOnline").replace("%PLAYER%", player.getName())));
				if (FileManager.ConfigCfg.getBoolean("Friends.ShowServer.Enable")) {
					lore = new ArrayList<String>(Arrays.asList(ChatColor.translateAlternateColorCodes('&',FileManager.ConfigCfg.getString("Friends.ShowServer.Lore")
							.replace("%SERVER%", String.valueOf(BungeeSQL_Manager.get(player, "SERVER"))))));
				}
				b_online = true;
			}
		}
		int[] time = PlayerUtilities.getLastOnline(pu.getLastOnline());
		if (!player.isOnline() && !b_online) {
			if (FileManager.ConfigCfg.getBoolean("Friends.GUI.FriendHead.ChangeHeadIfOffline")) {
				IS = new ItemStack(Material.SKULL_ITEM, 1, (short) 0);
				SM = (SkullMeta) IS.getItemMeta();
			}
			SM.setDisplayName(ChatColor.translateAlternateColorCodes('&', 
					FileManager.ConfigCfg.getString("Friends.GUI.FriendHead.NameOffline").replace("%PLAYER%", player.getName())));
			if (FileManager.ConfigCfg.getBoolean("Friends.Options.LastOnline.Enable") && time != null
					&& time.length >= 3 && pu.getLastOnline() != 0) {
				lore = new ArrayList<String>(Arrays.asList(ChatColor.translateAlternateColorCodes('&',
						FileManager.ConfigCfg.getString("Friends.Options.LastOnline.Format")
								.replace("%days%", "" + time[3]).replace("%hours%", time[2] + "")
								.replace("%minutes%", "" + time[1]).replace("%seconds%", "" + time[0]))));
			}
		} else if (player.isOnline() && !Friends.bungeeMode) {
			SM.setDisplayName(ChatColor.translateAlternateColorCodes('&', 
					FileManager.ConfigCfg.getString("Friends.GUI.FriendHead.NameOnline").replace("%PLAYER%", player.getName())));
			if (FileManager.ConfigCfg.getBoolean("Friends.Options.ShowWorld.Enable"))
				lore = new ArrayList<String>(Arrays.asList(ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.Options.ShowWorld.Lore")
						.replace("%WORLD%", Bukkit.getPlayer(player.getUniqueId()).getWorld().getName()))));
		}
		if(pu.getStatus() != null && pu.getStatus().length() >= 1) {
			lore.add("");
			for(String s : splitStatus(pu.getStatus())) {
				lore.add(s);
			}
		}
		SM.setLore(lore);
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
