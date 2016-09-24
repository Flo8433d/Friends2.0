/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Listeners.BlockedEditInventoryListener;
import de.HyChrod.Friends.Listeners.EditInventoryListener;
import de.HyChrod.Friends.Listeners.RequestEditInventoryListener;

public class InventoryBuilder {
	
	private static FileManager mgr = new FileManager();
	private static FileConfiguration cfg = mgr.getConfig("", "config.yml");
	
	public static void MAIN_INVENTORY(Friends plugin, Player p) {
		new InventoryPage(plugin, p, 0, new PlayerUtilities(p)).open();
	}
	
	public static void REQUESTS_INVENTORY(Friends plugin, Player p) {
		new RequestsPage(plugin, p, 0, new PlayerUtilities(p)).open();
	}
	
	public static void BLOCKED_INVENTORY(Friends plugin, Player p) {
		new BlockedPage(plugin, p, 0, new PlayerUtilities(p)).open();
	}
	
	public static void OPTIONS_INVENTORY(Player p) {
		Inventory inv = Bukkit.createInventory(null, cfg.getInt("Friends.GUI.OptionsInv.InventorySize"), 
				ChatColor.translateAlternateColorCodes('&', cfg.getString("Friends.GUI.OptionsInv.Title")));
		
		PlayerUtilities pu = new PlayerUtilities(p);
		for(String placeholder : cfg.getStringList("Friends.GUI.OptionsInv.PlaceholderItems.InventorySlots")) {
			inv.setItem(Integer.valueOf(placeholder)-1, ItemStacks.OPTIONS_PLACEHOLDER.getItem());
		}
		inv.setItem(ItemStacks.OPTIONS_BACK.getInvSlot()-1, ItemStacks.OPTIONS_BACK.getItem());
		inv.setItem(ItemStacks.OPTIONS_CHAT.getInvSlot()-1, ItemStacks.OPTIONS_CHAT.getItem());
		inv.setItem(ItemStacks.OPTIONS_REQUESTS.getInvSlot()-1, ItemStacks.OPTIONS_REQUESTS.getItem());
		if(cfg.getBoolean("Friends.FriendChat.FriendMSG")) {
			inv.setItem(ItemStacks.OPTIONS_PRIVATEMESSAGES.getInvSlot()-1, ItemStacks.OPTIONS_PRIVATEMESSAGES.getItem());
			inv.setItem(cfg.getInt("Friends.GUI.OptionsInv.OptionPrivateMessages.ButtonInventorySlot")-1, ItemStacks.OPTIONSBUTTON(pu.getOptions(), "option_noMsg", "§d"));
		}
		if(cfg.getBoolean("Friends.Options.EnableJumping")) {
			inv.setItem(ItemStacks.OPTIONS_JUMPING.getInvSlot()-1, ItemStacks.OPTIONS_JUMPING.getItem());
			inv.setItem(cfg.getInt("Friends.GUI.OptionsInv.OptionsJumping.ButtonInventorySlot")-1, ItemStacks.OPTIONSBUTTON(pu.getOptions(), "option_noJumping", "§c"));
		}
		
		inv.setItem(cfg.getInt("Friends.GUI.OptionsInv.OptionsRequestsItems.ButtonInventorySlot")-1, ItemStacks.OPTIONSBUTTON(pu.getOptions(), "option_noRequests", "§a"));
		inv.setItem(cfg.getInt("Friends.GUI.OptionsInv.OptionsMessagesItems.ButtonInventorySlot")-1, ItemStacks.OPTIONSBUTTON(pu.getOptions(), "option_noChat", "§b"));
		
		p.openInventory(inv);
	}
	
	public static void EDIT_INVENTORY(Player p) {
		Inventory inv = Bukkit.createInventory(null, cfg.getInt("Friends.GUI.FriendEditInv.InventorySize"),
				ChatColor.translateAlternateColorCodes('&', cfg.getString("Friends.GUI.FriendEditInv.Title").replace("%FRIEND%", EditInventoryListener.editing.get(p).getName())));
		
		for(String placeholder : cfg.getStringList("Friends.GUI.FriendEditInv.PlaceholderItems.InventorySlots")) {
			inv.setItem(Integer.valueOf(placeholder)-1, ItemStacks.EDIT_PLACEHOLDER.getItem());
		}
		
		inv.setItem(ItemStacks.EDIT_REMOVE.getInvSlot()-1, ItemStacks.EDIT_REMOVE.getItem());
		if(cfg.getBoolean("Friends.Options.EnableJumping")) {
			inv.setItem(ItemStacks.EDIT_JUMP.getInvSlot()-1, ItemStacks.EDIT_JUMP.getItem());
		}
		inv.setItem(ItemStacks.EDIT_BACK.getInvSlot()-1, ItemStacks.EDIT_BACK.getItem());
		p.openInventory(inv);
	}
	
	public static void REMOVE_VERIFICATION_INVENTORY(Player p) {
		Inventory inv = Bukkit.createInventory(null, cfg.getInt("Friends.GUI.RemoveVerificationInv.InventorySize"),
				ChatColor.translateAlternateColorCodes('&', cfg.getString("Friends.GUI.RemoveVerificationInv.Title")));
		
		for(String placeholder : cfg.getStringList("Friends.GUI.RemoveVerificationInv.PlaceholderItem.ItemID")) {
			inv.setItem(Integer.valueOf(placeholder)-1, ItemStacks.REMOVEVERIFICATION_PLACEHOLDER.getItem());
		}
		inv.setItem(ItemStacks.REMOVEVERIFICATION_CANCLE.getInvSlot()-1, ItemStacks.REMOVEVERIFICATION_CANCLE.getItem());
		inv.setItem(ItemStacks.REMOVEVERIFICATION_CONFIRM.getInvSlot()-1, ItemStacks.REMOVEVERIFICATION_CONFIRM.getItem());
		p.openInventory(inv);
	}
	
	public static void REQUESTEDIT_INVENTORY(Player p) {
		Inventory inv = Bukkit.createInventory(null, cfg.getInt("Friends.GUI.RequestEditInv.InventorySize"), 
				ChatColor.translateAlternateColorCodes('&', cfg.getString("Friends.GUI.RequestEditInv.Title").replace("%PLAYER%", RequestEditInventoryListener.editing.get(p).getName())));
		
		for(String placeholder : cfg.getStringList("Friends.GUI.RequestEditInv.PlaceholderItems.ItemID")) {
			inv.setItem(Integer.valueOf(placeholder)-1, ItemStacks.REQUESTS_EDIT_PLACEHOLDER.getItem());
		}
		inv.setItem(ItemStacks.REQUEST_EDIT_ACCEPT.getInvSlot()-1, ItemStacks.REQUEST_EDIT_ACCEPT.getItem());
		inv.setItem(ItemStacks.REQUEST_EDIT_DENY.getInvSlot()-1, ItemStacks.REQUEST_EDIT_DENY.getItem());
		inv.setItem(ItemStacks.REQUEST_EDIT_BLOCK.getInvSlot()-1, ItemStacks.REQUEST_EDIT_BLOCK.getItem());
		inv.setItem(ItemStacks.REQUEST_EDIT_BACK.getInvSlot()-1, ItemStacks.REQUEST_EDIT_BACK.getItem());
		p.openInventory(inv);
	}
	
	public static void BLOCKEDEDIT_INVENOTRY(Player p) {
		Inventory inv = Bukkit.createInventory(null, cfg.getInt("Friends.GUI.BlockedEditInv.InventorySize"), 
				ChatColor.translateAlternateColorCodes('&', cfg.getString("Friends.GUI.BlockedEditInv.Title").replace("%PLAYER%", BlockedEditInventoryListener.editing.get(p).getName())));
		
		for(String placeholder : cfg.getStringList("Friends.GUI.BlockedEditInv.PlaceholderItem.InventorySlots")) {
			inv.setItem(Integer.valueOf(placeholder)-1, ItemStacks.BLOCKED_EDIT_PLACEHOLDER.getItem());
		}
		inv.setItem(ItemStacks.BLOCKED_EDIT_UNBLOCK.getInvSlot()-1, ItemStacks.BLOCKED_EDIT_UNBLOCK.getItem());
		inv.setItem(ItemStacks.BLOCKED_EDIT_BACK.getInvSlot()-1, ItemStacks.BLOCKED_EDIT_BACK.getItem());
		p.openInventory(inv);
	}

}
