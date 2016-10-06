/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Util;

import java.util.LinkedList;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public enum InventoryTypes {

	REQUEST("RequestsInv"), BLOCKED("BlockedInv"), FRIENDEDIT("FriendEditInv"), BLOCKEDEDIT("BlockedEditInv");

	private String s;
	private String color;
	private LinkedList<ItemStacks> items = new LinkedList<>();
	private LinkedList<OfflinePlayer> get = new LinkedList<>();

	InventoryTypes(String s) {
		this.s = s;
		if (s.equals("RequestsInv")) {
			color = "§3";
			items.add(ItemStacks.REQUESTS_PLACEHOLDER);
			items.add(ItemStacks.REQUESTS_NEXTPAGE);
			items.add(ItemStacks.REQUESTS_PREVIOUSPAGE);
			items.add(ItemStacks.REQUESTS_BACK);
		} else if (s.equals("BlockedInv")) {
			color = "§c";
			items.add(ItemStacks.BLOCKED_PLACEHOLDER);
			items.add(ItemStacks.BLOCKED_NEXTPAGE);
			items.add(ItemStacks.BLOCKED_PREVIOUSPAGE);
			items.add(ItemStacks.BLOCKED_BACK);
		}
	}

	public String getS() {
		return s;
	}

	public String getColor() {
		return color;
	}

	public LinkedList<ItemStacks> getItems() {
		return items;
	}

	public void applyPlayer(Player player) {
		if (s.equals("RequestsInv")) {
			get = new PlayerUtilities(player).get(1);
		}
		if (s.equals("BlockedInv")) {
			get = new PlayerUtilities(player).get(2);
		}
	}

	public LinkedList<OfflinePlayer> getGet() {
		return get;
	}

}
