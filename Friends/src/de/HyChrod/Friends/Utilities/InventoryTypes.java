/*
*
* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.HyChrod.Friends.DataHandlers.FileManager;

public enum InventoryTypes {

	MAIN(""), REQUEST(".RequestsInv"), BLOCKED(".BlockedInv"), FRIENDEDIT(".FriendEditInv"),
	REQUESTEDIT(".RequestEdit"), BLOCKEDEDIT(".BlockedEditInv"), OPTIONS(".OptionsInv"), REMOVEVERIFICATION(".RemoveVerificationInv");

	private String s;
	private String name;
	private List<Object> items = Collections.synchronizedList(new ArrayList<>());
	private List<FriendPlayer> get = Collections.synchronizedList(new ArrayList<>());

	InventoryTypes(String s) {
		this.s = s;
		if(s != null) {
			if (s.equals(".RequestsInv")) {
				name = ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.RequestsInv.PlayerHead.Name"));
				items.add(ItemStacks.REQUESTS_PLACEHOLDER);
				items.add(ItemStacks.REQUESTS_NEXTPAGE);
				items.add(ItemStacks.REQUESTS_PREVIOUSPAGE);
				items.add(ItemStacks.REQUESTS_BACK);
				items.add(ItemStacks.REQUESTS_ACCEPTALL);
				items.add(ItemStacks.REQUESTS_DENYALL);
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

	public List<Object> getItems() {
		return items;
	}

	public void applyPlayer(Player player) {
		if(s != null) {
			PlayerUtilities pu = PlayerUtilities.getUtilities(player.getUniqueId().toString());
			if (s.equals(".RequestsInv")) {
				get = pu.getRequests();
			}
			if (s.equals(".BlockedInv")) {
				get = pu.getBlocked();
			}
			if (s.equals("")) {
				if(items.size() <= 6) {
					while(items.size() <= 6)
						items.add(new ItemStack(Material.WOOD));
				}
				items.set(4, new UtilitieItems().MAIN_BLOCKED(pu.getBlocked().size()));
				items.set(5, new UtilitieItems().MAIN_REQUESTS(pu.getRequests().size()));
				get = pu.getFriends();
			}
			return;
		}
	}

	public List<FriendPlayer> getGet() {
		return get;
	}

}
