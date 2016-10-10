/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Util;

import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum InventoryTypes {

	MAIN(""), REQUEST(".RequestsInv"), BLOCKED(".BlockedInv"), FRIENDEDIT(".FriendEditInv"),
	REQUESTEDIT(".RequestEdit"), BLOCKEDEDIT(".BlockedEditInv"), OPTIONS(".OptionsInv"), REMOVEVERIFICATION(".RemoveVerificationInv");

	private String s;
	private String color;
	private LinkedList<Object> items = new LinkedList<>();
	private LinkedList<String> get = new LinkedList<>();

	InventoryTypes(String s) {
		this.s = s;
		if(s != null) {
			if (s.equals(".RequestsInv")) {
				color = "§3";
				items.add(ItemStacks.REQUESTS_PLACEHOLDER);
				items.add(ItemStacks.REQUESTS_NEXTPAGE);
				items.add(ItemStacks.REQUESTS_PREVIOUSPAGE);
				items.add(ItemStacks.REQUESTS_BACK);
			} else if (s.equals(".BlockedInv")) {
				color = "§c";
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

	public String getColor() {
		return color;
	}

	public LinkedList<Object> getItems() {
		return items;
	}

	public void applyPlayer(Player player) {
		if(s != null) {
			if (s.equals(".RequestsInv")) {
				get = new PlayerUtilities(player).get(1);
			}
			if (s.equals(".BlockedInv")) {
				get = new PlayerUtilities(player).get(2);
			}
			if (s.equals("")) {
				PlayerUtilities pu = new PlayerUtilities(player);
				
				if(items.size() <= 5) {
					while(items.size() <= 5)
						items.add(new ItemStack(Material.WOOD));
				}
				items.set(4, ItemStacks.MAIN_BLOCKED(pu.get(2).size()));
				items.set(5, ItemStacks.MAIN_REQUESTS(pu.get(1).size()));
				get = new PlayerUtilities(player).get(0);
			}
			return;
		}
	}

	public LinkedList<String> getGet() {
		return get;
	}

}
