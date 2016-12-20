/*
*
* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import de.HyChrod.Friends.FileManager;

public class UtilitieItems {
	
	public ItemStack FRIENDITEM(Player player) {
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
	
	public ItemStack MAIN_REQUESTS(Integer requests) {
		int rqs = requests == 0 ? 1 : requests;
		if(rqs > 64) 
			rqs = 1;
		String[] IdByString = FileManager.ConfigCfg.getString("Friends.GUI.RequestsItem.ItemID").split(":");
		String name =  ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.RequestsItem.Name").replace("%REQUESTS%", ""+requests));
		return MainStack(IdByString, rqs, name, Arrays.asList(ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.RequestsItem.Lore")).split("//")), "§a");
	}
	
	public ItemStack MAIN_BLOCKED(Integer blocked) {
		int blq = blocked == 0 ? 1 : blocked;
		if(blq > 64) 
			blq = 1;
		String[] IdByString = FileManager.ConfigCfg.getString("Friends.GUI.BlockedItem.ItemID").split(":");
		String name =  ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.BlockedItem.Name").replace("%BLOCKED%", ""+blocked));
		return MainStack(IdByString, blq, name, Arrays.asList(ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.BlockedItem.Lore")).split("//")), "§a");
	}
	
	public ItemStack OPTIONSBUTTON(LinkedList<Object> linkedList, String option, String code) {
		String[] IdByString = FileManager.ConfigCfg.getString("Friends.GUI.OptionsInv.ButtonOn.ItemID").split(":");
		String name = ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.OptionsInv.ButtonOn.Name"));
		List<String> lore = new ArrayList<>();
		if(FileManager.ConfigCfg.getString("Friends.GUI.OptionsInv.ButtonOn.Lore") != null 
				&& !FileManager.ConfigCfg.getString("Friends.GUI.OptionsInv.ButtonOn.Lore").equalsIgnoreCase("")) {
			lore = Arrays.asList(ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.OptionsInv.ButtonOn.Lore")).split("//"));
		}
		if(linkedList.contains(option)) {
			IdByString = FileManager.ConfigCfg.getString("Friends.GUI.OptionsInv.ButtonOff.ItemID").split(":");
			name = ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.OptionsInv.ButtonOff.Name"));
			if(FileManager.ConfigCfg.getString("Friends.GUI.OptionsInv.ButtonOff.Lore") != null 
					&& !FileManager.ConfigCfg.getString("Friends.GUI.OptionsInv.ButtonOff.Lore").equalsIgnoreCase("")) {
				lore = Arrays.asList(ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.GUI.OptionsInv.ButtonOff.Lore")).split("//"));
			}
		}
		return MainStack(IdByString, 1, name, lore, code);
	}
	
	@SuppressWarnings("deprecation")
	private ItemStack MainStack(String[] IdByString, int anzahl, String name, List<String> lore, String code) {
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
