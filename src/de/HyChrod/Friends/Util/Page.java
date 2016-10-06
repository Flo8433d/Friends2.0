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

public class Page {

	public Player player;
	public int site;
	public PlayerUtilities pu;

	private Friends plugin;
	private InventoryTypes type = null;

	public Page(Friends plugin, Player player, int site, PlayerUtilities pu, InventoryTypes type) {
		this.player = player;
		this.site = site;
		this.pu = pu;
		this.plugin = plugin;
		this.type = type;
		this.type.applyPlayer(player);
	}

	public Inventory open(boolean open) {
		Inventory inv = Bukkit.createInventory(null,
				FileManager.ConfigCfg.getInt("Friends.GUI." + type.getS() + ".InventorySize"),
				ChatColor.translateAlternateColorCodes('&',
						FileManager.ConfigCfg.getString("Friends.GUI." + type.getS() + ".Title")));

		for (String placeholder : FileManager.ConfigCfg
				.getStringList("Friends.GUI." + type.getS() + ".PlaceholderItem.InventorySlots")) {
			inv.setItem(Integer.valueOf(placeholder) - 1, type.getItems().get(0).getItem());
		}
		inv.setItem(type.getItems().get(1).getInvSlot() - 1, type.getItems().get(1).getItem());
		inv.setItem(type.getItems().get(2).getInvSlot() - 1, type.getItems().get(2).getItem());
		inv.setItem(type.getItems().get(3).getInvSlot() - 1, type.getItems().get(3).getItem());

		int freeSlots = 0;
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) == null || (inv.getItem(i) != null && inv.getItem(i).getType().equals(Material.AIR))) {
				freeSlots++;
			}
		}
		freeSlots = freeSlots * site;
		List<ItemStack> items = new ArrayList<>();
		for (OfflinePlayer player : type.getGet()) {
			ItemStack IS = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta SM = (SkullMeta) IS.getItemMeta();
			SM.setDisplayName(type.getColor() + player.getName());
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

}
