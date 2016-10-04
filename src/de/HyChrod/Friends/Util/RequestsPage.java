/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Util;

import java.util.ArrayList;
import java.util.List;

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

public class RequestsPage {

	public Player player;
	public int site;
	public PlayerUtilities pu;

	private Friends plugin;

	public RequestsPage(Friends plugin, Player player, int site, PlayerUtilities pu) {
		this.player = player;
		this.site = site;
		this.pu = pu;
		this.plugin = plugin;
	}

	public void open() {
		Inventory inv = Bukkit.createInventory(null,
				FileManager.ConfigCfg.getInt("Friends.GUI.RequestsInv.InventorySize"),
				ChatColor.translateAlternateColorCodes('&',
						FileManager.ConfigCfg.getString("Friends.GUI.RequestsInv.Title")));

		for (String placeholder : FileManager.ConfigCfg
				.getStringList("Friends.GUI.RequestsInv.PlaceholderItem.InventorySlots")) {
			inv.setItem(Integer.valueOf(placeholder) - 1, ItemStacks.REQUESTS_PLACEHOLDER.getItem());
		}
		inv.setItem(ItemStacks.REQUESTS_NEXTPAGE.getInvSlot() - 1, ItemStacks.REQUESTS_NEXTPAGE.getItem());
		inv.setItem(ItemStacks.REQUESTS_PREVIOUSPAGE.getInvSlot() - 1, ItemStacks.REQUESTS_PREVIOUSPAGE.getItem());
		inv.setItem(ItemStacks.REQUESTS_BACK.getInvSlot() - 1, ItemStacks.REQUESTS_BACK.getItem());

		int freeSlots = 0;
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) == null || (inv.getItem(i) != null && inv.getItem(i).getType().equals(Material.AIR))) {
				freeSlots++;
			}
		}
		freeSlots = freeSlots * site;
		List<ItemStack> items = new ArrayList<>();
		for (OfflinePlayer player : pu.getRequests()) {
			ItemStack IS = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta SM = (SkullMeta) IS.getItemMeta();
			SM.setDisplayName("§3" + player.getName());
			SM.setOwner(player.getName());
			IS.setItemMeta(SM);
			items.add(IS);
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
		player.openInventory(inv);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			@Override
			public void run() {
				player.updateInventory();
			}
		}, 5);
	}

}
