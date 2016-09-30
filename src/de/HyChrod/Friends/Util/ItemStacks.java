/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import de.HyChrod.Friends.FileManager;

public enum ItemStacks {

	MAIN_PLACEHOLDER(null, null, "Friends.GUI.PlaceholderItem.ItemID", null, 1),
	MAIN_OPTIONSITEM("Friends.GUI.OptionsItem.Name", "Friends.GUI.OptionsItem.Lore", "Friends.GUI.OptionsItem.ItemID", "Friends.GUI.OptionsItem.InventorySlot", 1),
	MAIN_NEXTPAGEITEM("Friends.GUI.NextPageItem.Name", "Friends.GUI.NextPageItem.Lore", "Friends.GUI.NextPageItem.ItemID", "Friends.GUI.NextPageItem.InventorySlot", 1),
	MAIN_PREVIOUSPAGEITEM("Friends.GUI.PreviousPageItem.Name", "Friends.GUI.PreviousPageItem.Lore", "Friends.GUI.PreviousPageItem.ItemID", "Friends.GUI.PreviousPageItem.InventorySlot", 1),
	REQUESTS_NEXTPAGE("Friends.GUI.RequestsInv.NextPageItem.Name", "Friends.GUI.RequestsInv.NextPageItem.Lore", "Friends.GUI.RequestsInv.NextPageItem.ItemID", "Friends.GUI.RequestsInv.NextPageItem.InventorySlot", 1),
	REQUESTS_PREVIOUSPAGE("Friends.GUI.RequestsInv.PreviousPageItem.Name", "Friends.GUI.RequestsInv.PreviousPageItem.Lore", "Friends.GUI.RequestsInv.PreviousPageItem.ItemID", "Friends.GUI.RequestsInv.PreviousPageItem.InventorySlot", 1),
	REQUESTS_BACK("Friends.GUI.RequestsInv.BackItem.Name", "Friends.GUI.RequestsInv.BackItem.Lore", "Friends.GUI.RequestsInv.BackItem.ItemID", "Friends.GUI.RequestsInv.BackItem.InventorySlot", 1),
	REQUESTS_PLACEHOLDER(null, null, "Friends.GUI.RequestsInv.PlaceholderItem.ItemID", null, 1),
	BLOCKED_NEXTPAGE("Friends.GUI.BlockedInv.NextPageItem.Name", "Friends.GUI.BlockedInv.NextPageItem.Lore", "Friends.GUI.BlockedInv.NextPageItem.ItemID", "Friends.GUI.BlockedInv.NextPageItem.InventorySlot", 1),
	BLOCKED_PREVIOUSPAGE("Friends.GUI.BlockedInv.PreviousPageItem.Name", "Friends.GUI.BlockedInv.PreviousPageItem.Lore", "Friends.GUI.BlockedInv.PreviousPageItem.ItemID", "Friends.GUI.BlockedInv.PreviousPageItem.InventorySlot", 1),
	BLOCKED_BACK("Friends.GUI.BlockedInv.BackItem.Name", "Friends.GUI.BlockedInv.BackItem.Lore", "Friends.GUI.BlockedInv.BackItem.ItemID", "Friends.GUI.BlockedInv.BackItem.InventorySlot", 1),
	BLOCKED_PLACEHOLDER(null, null, "Friends.GUI.BlockedInv.PlaceholderItem.ItemID", null, 1),
	OPTIONS_REQUESTS("Friends.GUI.OptionsInv.OptionsRequestsItems.Name", "Friends.GUI.OptionsInv.OptionsRequestsItems.Lore", "Friends.GUI.OptionsInv.OptionsRequestsItems.ItemID", "Friends.GUI.OptionsInv.OptionsRequestsItems.InventorySlot", 1),
	OPTIONS_CHAT("Friends.GUI.OptionsInv.OptionsMessagesItems.Name", "Friends.GUI.OptionsInv.OptionsMessagesItems.Lore", "Friends.GUI.OptionsInv.OptionsMessagesItems.ItemID", "Friends.GUI.OptionsInv.OptionsMessagesItems.InventorySlot", 1),
	OPTIONS_JUMPING("Friends.GUI.OptionsInv.OptionsJumping.Name", "Friends.GUI.OptionsInv.OptionsJumping.Lore", "Friends.GUI.OptionsInv.OptionsJumping.ItemID", "Friends.GUI.OptionsInv.OptionsJumping.InventorySlot", 1),
	OPTIONS_PRIVATEMESSAGES("Friends.GUI.OptionsInv.OptionPrivateMessages.Name", "Friends.GUI.OptionsInv.OptionPrivateMessages.Lore", "Friends.GUI.OptionsInv.OptionPrivateMessages.ItemID", "Friends.GUI.OptionsInv.OptionPrivateMessages.InventorySlot", 1),
	OPTIONS_BACK("Friends.GUI.OptionsInv.BackItem.Name", "Friends.GUI.OptionsInv.BackItem.Lore", "Friends.GUI.OptionsInv.BackItem.ItemID", "Friends.GUI.OptionsInv.BackItem.InventorySlot", 1),
	OPTIONS_PLACEHOLDER(null, null, "Friends.GUI.OptionsInv.PlaceholderItems.ItemID", null, 1),
	EDIT_REMOVE("Friends.GUI.FriendEditInv.RemoveItem.Name", "Friends.GUI.FriendEditInv.RemoveItem.Lore", "Friends.GUI.FriendEditInv.RemoveItem.ItemID", "Friends.GUI.FriendEditInv.RemoveItem.InventorySlot", 1),
	EDIT_JUMP("Friends.GUI.FriendEditInv.JumpItem.Name", "Friends.GUI.FriendEditInv.JumpItem.Lore", "Friends.GUI.FriendEditInv.JumpItem.ItemID", "Friends.GUI.FriendEditInv.JumpItem.InventorySlot", 1),
	EDIT_BACK("Friends.GUI.FriendEditInv.BackItem.Name", "Friends.GUI.FriendEditInv.BackItem.Lore", "Friends.GUI.FriendEditInv.BackItem.ItemID", "Friends.GUI.FriendEditInv.BackItem.InventorySlot", 1),
	EDIT_PLACEHOLDER(null, null, "Friends.GUI.FriendEditInv.PlaceholderItems.ItemID", null, 1),
	REMOVEVERIFICATION_CONFIRM("Friends.GUI.RemoveVerificationInv.ConfirmItem.Name", "Friends.GUI.RemoveVerificationInv.ConfirmItem.Lore", "Friends.GUI.RemoveVerificationInv.ConfirmItem.ItemID", "Friends.GUI.RemoveVerificationInv.ConfirmItem.InventorySlot", 1),
	REMOVEVERIFICATION_CANCLE("Friends.GUI.RemoveVerificationInv.CancelItem.Name", "Friends.GUI.RemoveVerificationInv.CancelItem.Lore", "Friends.GUI.RemoveVerificationInv.CancelItem.ItemID", "Friends.GUI.RemoveVerificationInv.CancelItem.InventorySlot", 1),
	REMOVEVERIFICATION_PLACEHOLDER(null, null, "Friends.GUI.RemoveVerificationInv.PlaceholderItem.ItemID", null, 1),
	REQUEST_EDIT_ACCEPT("Friends.GUI.RequestEditInv.Accept.Name", "Friends.GUI.RequestEditInv.Accept.Lore", "Friends.GUI.RequestEditInv.Accept.ItemID", "Friends.GUI.RequestEditInv.Accept.InventorySlot", 1),
	REQUEST_EDIT_DENY("Friends.GUI.RequestEditInv.Deny.Name", "Friends.GUI.RequestEditInv.Deny.Lore", "Friends.GUI.RequestEditInv.Deny.ItemID", "Friends.GUI.RequestEditInv.Deny.InventorySlot", 1),
	REQUEST_EDIT_BLOCK("Friends.GUI.RequestEditInv.Block.Name", "Friends.GUI.RequestEditInv.Block.Lore", "Friends.GUI.RequestEditInv.Block.ItemID", "Friends.GUI.RequestEditInv.Block.InventorySlot", 1),
	REQUEST_EDIT_BACK("Friends.GUI.RequestEditInv.BackItem.Name", "Friends.GUI.RequestEditInv.BackItem.Lore", "Friends.GUI.RequestEditInv.BackItem.ItemID", "Friends.GUI.RequestEditInv.BackItem.InventorySlot", 1),
	REQUESTS_EDIT_PLACEHOLDER(null, null, "Friends.GUI.RequestEditInv.PlaceholderItems.ItemID", null, 1),
	BLOCKED_EDIT_UNBLOCK("Friends.GUI.BlockedEditInv.UnblockItem.Name", "Friends.GUI.BlockedEditInv.UnblockItem.Lore", "Friends.GUI.BlockedEditInv.UnblockItem.ItemID", "Friends.GUI.BlockedEditInv.UnblockItem.InventorySlot", 1),
	BLOCKED_EDIT_BACK("Friends.GUI.BlockedEditInv.BackItem.Name", "Friends.GUI.BlockedEditInv.BackItem.Lore", "Friends.GUI.BlockedEditInv.BackItem.ItemID", "Friends.GUI.BlockedEditInv.BackItem.InventorySlot", 1),
	BLOCKED_EDIT_PLACEHOLDER(null, null, "Friends.GUI.BlockedEditInv.PlaceholderItem.ItemID", null, 1);
	
	private String name;
	private List<String> lore;
	private String[] itemID;
	private Integer amount;
	private Integer invSlot = 0;
	
	ItemStacks(String name, String lore, String id, String invSlot, Integer amount) {
		
		if(name == null) {this.name = "§r";} else {
			this.name = ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString(name));
		}
		if(lore == null) {this.lore = null;} else {
			this.lore = Arrays.asList(ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString(lore)).split("//"));
		}
		this.itemID = FileManager.ConfigCfg.getString(id).split(":");
		this.amount = amount;
		if(invSlot != null && invSlot.length() > 5) {
			this.invSlot = Integer.valueOf(FileManager.ConfigCfg.getString(invSlot));
		}
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getLore() {
		return lore;
	}
	
	public String[] getItemID() {
		return itemID;
	}
	
	public Integer getAmount() {
		return amount;
	}
	
	public Integer getInvSlot() {
		return invSlot;
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack getItem() {
		int id = Integer.valueOf(itemID[0]);
		int byt = 0;
		if(itemID.length > 1) {byt = Integer.valueOf(itemID[1]);}
		
		ItemStack IS = new ItemStack(Material.getMaterial(id), amount, (byte) byt);
		ItemMeta IM = IS.getItemMeta();
		IM.setDisplayName(name);
		IM.setLore(lore);
		IM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		IM.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		IM.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		IM.addItemFlags(ItemFlag.HIDE_DESTROYS);
		IM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		IS.setItemMeta(IM);
		return IS;
	}
	
	public static ItemStack FRIENDITEM(Player player) {
		if(FileManager.ConfigCfg.getBoolean("Friends.FriendItem.PlayersHead")) {
			ItemStack IS = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
			SkullMeta SM = (SkullMeta)IS.getItemMeta();
			SM.setOwner(player.getName());
			SM.setDisplayName(ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.FriendItem.Displayname")));
			SM.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.FriendItem.Lore")).split("//")));
			IS.setItemMeta(SM);
			return IS;
		}
		String[] IdByString = FileManager.ConfigCfg.getString("Friends.FriendItem.ItemID").split(":");
		String name =  ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.FriendItem.Displayname"));
		return MainStack(IdByString, 1, name, Arrays.asList(ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.FriendItem.Lore")).split("//")), "§a");
	}
	
	public static ItemStack MAIN_REQUESTS(Integer requests) {
		String[] IdByString = FileManager.ConfigCfg.getString("Friends.GUI.RequestsItem.ItemID").split(":");
		String name =  ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.RequestsItem.Name").replace("%REQUESTS%", ""+requests));
		return MainStack(IdByString, requests, name, Arrays.asList(ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.RequestsItem.Lore")).split("//")), "§a");
	}
	
	public static ItemStack MAIN_BLOCKED(Integer blocked) {
		String[] IdByString = FileManager.ConfigCfg.getString("Friends.GUI.BlockedItem.ItemID").split(":");
		String name =  ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.BlockedItem.Name").replace("%BLOCKED%", ""+blocked));
		return MainStack(IdByString, blocked, name, Arrays.asList(ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.BlockedItem.Lore")).split("//")), "§a");
	}
	
	public static ItemStack OPTIONSBUTTON(List<String> options, String option, String code) {
		String[] IdByString = FileManager.ConfigCfg.getString("Friends.GUI.OptionsInv.ButtonOn.ItemID").split(":");
		String name = ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.OptionsInv.ButtonOn.Name"));
		List<String> lore = Arrays.asList(ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.OptionsInv.ButtonOn.Lore")).split("//"));
		if(options.contains(option)) {
			IdByString = FileManager.ConfigCfg.getString("Friends.GUI.OptionsInv.ButtonOff.ItemID").split(":");
			name = ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.OptionsInv.ButtonOff.Name"));
		}
		return MainStack(IdByString, 1, name, lore, code);
	}
	
	@SuppressWarnings("deprecation")
	private static ItemStack MainStack(String[] IdByString, int anzahl, String name, List<String> lore, String code) {
		int id = Integer.valueOf(IdByString[0]);
		int byt = 0;
		if(IdByString.length > 1) {byt = Integer.valueOf(IdByString[1]);}
		
		ItemStack IS = new ItemStack(Material.getMaterial(id), anzahl, (byte) byt);
		ItemMeta IM = IS.getItemMeta();
		IM.setDisplayName(name + code);
		IM.setLore(lore);
		IS.setItemMeta(IM);
		return IS;
	}
	
}
