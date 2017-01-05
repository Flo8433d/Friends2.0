/*
*
* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.Utilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.HyChrod.Friends.DataHandlers.FileManager;

public enum ItemStacks {

	MAIN_PLACEHOLDER("Friends.GUI.PlaceholderItem", null),
	MAIN_OPTIONSITEM("Friends.GUI.OptionsItem", 1),
	MAIN_NEXTPAGEITEM("Friends.GUI.NextPageItem", 1),
	MAIN_PREVIOUSPAGEITEM("Friends.GUI.PreviousPageItem", 1),
	REQUESTS_NEXTPAGE("Friends.GUI.RequestsInv.NextPageItem", 1),
	REQUESTS_PREVIOUSPAGE("Friends.GUI.RequestsInv.PreviousPageItem", 1),
	REQUESTS_BACK("Friends.GUI.RequestsInv.BackItem", 1),
	REQUESTS_ACCEPTALL("Friends.GUI.RequestsInv.AcceptallItem", 1),
	REQUESTS_DENYALL("Friends.GUI.RequestsInv.DenyallItem", 1),
	REQUESTS_PLACEHOLDER("Friends.GUI.RequestsInv.PlaceholderItem", null),
	BLOCKED_NEXTPAGE("Friends.GUI.BlockedInv.NextPageItem", 1),
	BLOCKED_PREVIOUSPAGE("Friends.GUI.BlockedInv.PreviousPageItem", 1),
	BLOCKED_BACK("Friends.GUI.BlockedInv.BackItem", 1),
	BLOCKED_PLACEHOLDER("Friends.GUI.BlockedInv.PlaceholderItem", null),
	OPTIONS_REQUESTS("Friends.GUI.OptionsInv.OptionsRequestsItems", 1),
	OPTIONS_CHAT("Friends.GUI.OptionsInv.OptionsMessagesItems", 1),
	OPTIONS_JUMPING("Friends.GUI.OptionsInv.OptionsJumping", 1),
	OPTIONS_PRIVATEMESSAGES("Friends.GUI.OptionsInv.OptionPrivateMessages", 1),
	OPTIONS_PARTYINVITES("Friends.GUI.OptionsInv.OptionsPartyInvites", 1),
	OPTIONS_STATUSITEM("Friends.GUI.OptionsInv.StatusItem", 1),
	OPTIONS_BACK("Friends.GUI.OptionsInv.BackItem", 1),
	OPTIONS_PLACEHOLDER("Friends.GUI.OptionsInv.PlaceholderItems", null),
	EDIT_REMOVE("Friends.GUI.FriendEditInv.RemoveItem", 1),
	EDIT_JUMP("Friends.GUI.FriendEditInv.JumpItem", 1),
	EDIT_BACK("Friends.GUI.FriendEditInv.BackItem", 1),
	EDIT_PARTY("Friends.GUI.FriendEditInv.PartyItem", 1),
	EDIT_PLACEHOLDER("Friends.GUI.FriendEditInv.PlaceholderItems", null),
	REMOVEVERIFICATION_CONFIRM("Friends.GUI.RemoveVerificationInv.ConfirmItem", 1),
	REMOVEVERIFICATION_CANCLE("Friends.GUI.RemoveVerificationInv.CancelItem", 1),
	REMOVEVERIFICATION_PLACEHOLDER("Friends.GUI.RemoveVerificationInv.PlaceholderItem", null),
	REQUEST_EDIT_ACCEPT("Friends.GUI.RequestEditInv.Accept", 1),
	REQUEST_EDIT_DENY("Friends.GUI.RequestEditInv.Deny", 1),
	REQUEST_EDIT_BLOCK("Friends.GUI.RequestEditInv.Block", 1),
	REQUEST_EDIT_BACK("Friends.GUI.RequestEditInv.BackItem", 1),
	REQUESTS_EDIT_PLACEHOLDER("Friends.GUI.RequestEditInv.PlaceholderItems", null),
	BLOCKED_EDIT_UNBLOCK("Friends.GUI.BlockedEditInv.UnblockItem", 1),
	BLOCKED_EDIT_BACK("Friends.GUI.BlockedEditInv.BackItem", 1),
	BLOCKED_EDIT_PLACEHOLDER("Friends.GUI.BlockedEditInv.PlaceholderItem", null);
	
	private String name;
	private List<String> lore;
	private String[] itemID;
	private Integer amount = 1;
	private Integer invSlot = 0;
	
	ItemStacks(String path, Integer amount) {
		this.name = amount == null ? "§r" : ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString(path + ".Name"));
		this.lore = amount == null ? new ArrayList<>() : FileManager.ConfigCfg.getString(path + ".Lore") != null ? FileManager.ConfigCfg.getStringList(path + ".Lore") : new ArrayList<>();
		this.itemID = FileManager.ConfigCfg.getString(path + ".ItemID").split(":");
		this.amount = amount == null ? 1 : amount;
		this.invSlot = amount == null ? 0 : Integer.valueOf(FileManager.ConfigCfg.getString(path + ".InventorySlot"));
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
	
	public ItemStack getItem() {
		return getItem(name, lore, itemID, amount);
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getItem(String name, List<String> lore, String[] itemID, Integer amount) {
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
	
}
