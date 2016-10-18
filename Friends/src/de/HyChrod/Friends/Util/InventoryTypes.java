/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Util;

import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.HyChrod.Friends.FileManager;

public enum InventoryTypes {

	MAIN(""), REQUEST(".RequestsInv"), BLOCKED(".BlockedInv"), FRIENDEDIT(".FriendEditInv"),
	REQUESTEDIT(".RequestEdit"), BLOCKEDEDIT(".BlockedEditInv"), OPTIONS(".OptionsInv"), REMOVEVERIFICATION(".RemoveVerificationInv");

	private String s;
	private String name;
	private LinkedList<Object> items = new LinkedList<>();
	private LinkedList<Object> get = new LinkedList<>();

	InventoryTypes(String s) {
		this.s = s;
		if(s != null) {
			if (s.equals(".RequestsInv")) {
				name = ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.RequestsInv.PlayerHead.Name"));
				items.add(ItemStacks.REQUESTS_PLACEHOLDER);
				items.add(ItemStacks.REQUESTS_NEXTPAGE);
				items.add(ItemStacks.REQUESTS_PREVIOUSPAGE);
				items.add(ItemStacks.REQUESTS_BACK);
			} else if (s.equals(".BlockedInv")) {
				name = ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.BlockedInv.PlayerHead.Name"));
				items.add(ItemStacks.BLOCKED_PLACEHOLDER);
				items.add(ItemStacks.BLOCKED_NEXTPAGE);
				items.add(ItemStacks.BLOCKED_PREVIOUSPAGE);
				items.add(ItemStacks.BLOCKED_BACK);
			} else if (s.equals("")) {
				items.add(ItemStacks.MAIN_PLACEHOLDER);
				items.add(ItemStacks.MAIN_NEXTPAGEITEM);
				items.add(ItemStacks.MAIN_PREVIOUSPAGEITEM);
				items.add(ItemStacks.MAIN_OPTIONSITEM);
			}
			return;
		}
	}

	public String getS() {
		return s;
	}
	
	public String getName() {
		return name;
	}

	public LinkedList<Object> getItems() {
		return items;
	}

	public void applyPlayer(Player player) {
		if(s != null) {
			if (s.equals(".RequestsInv")) {
				get = new PlayerUtilities(player).get(1, true);
			}
			if (s.equals(".BlockedInv")) {
				get = new PlayerUtilities(player).get(2, true);
			}
			if (s.equals("")) {
				PlayerUtilities pu = new PlayerUtilities(player);
				
				if(items.size() <= 5) {
					while(items.size() <= 5)
						items.add(new ItemStack(Material.WOOD));
				}
				items.set(4, new UtilitieItems().MAIN_BLOCKED(pu.get(2, true).size()));
				items.set(5, new UtilitieItems().MAIN_REQUESTS(pu.get(1, true).size()));
				get = new PlayerUtilities(player).get(0, true);
			}
			return;
		}
	}

	public LinkedList<Object> getGet() {
		return get;
	}

}
