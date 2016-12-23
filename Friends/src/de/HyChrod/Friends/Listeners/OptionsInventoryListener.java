/*
*
* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.Listeners;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.HyChrod.Friends.FileManager;
import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.StatusCommand;
import de.HyChrod.Friends.Util.AnvilGUI;
import de.HyChrod.Friends.Util.AnvilGUI.AnvilClickEvent;
import de.HyChrod.Friends.Util.AnvilGUI.AnvilClickEventHandler;
import de.HyChrod.Friends.Util.AnvilGUI.AnvilSlot;
import de.HyChrod.Friends.Util.InventoryBuilder;
import de.HyChrod.Friends.Util.InventoryTypes;
import de.HyChrod.Friends.Util.ItemStacks;
import de.HyChrod.Friends.Util.PlayerUtilities;
import de.HyChrod.Friends.Util.UtilitieItems;

public class OptionsInventoryListener implements Listener {

	private Friends plugin;

	public OptionsInventoryListener(Friends friends) {
		this.plugin = friends;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {
		final Player p = (Player) e.getWhoClicked();

		if (e.getInventory() != null) {
			if (e.getInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&',
					FileManager.ConfigCfg.getString("Friends.GUI.OptionsInv.Title")))) {
				e.setCancelled(true);
				if (e.getCurrentItem() != null) {
					if (e.getCurrentItem().hasItemMeta()) {
						if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
							PlayerUtilities pu = new PlayerUtilities(p);
							if (e.getCurrentItem().getItemMeta().getDisplayName()
									.equals(new UtilitieItems().OPTIONSBUTTON(pu.get(3, false), "option_noRequests", "§a")
											.getItemMeta().getDisplayName())) {
								pu.toggleOption("option_noRequests");
								this.reOpenInv(p);
								return;
							}
							if (e.getCurrentItem().getItemMeta().getDisplayName()
									.equals(new UtilitieItems().OPTIONSBUTTON(pu.get(3, false), "option_noChat", "§b")
											.getItemMeta().getDisplayName())) {
								pu.toggleOption("option_noChat");
								this.reOpenInv(p);
								return;
							}
							if(e.getCurrentItem().getItemMeta().getDisplayName()
									.equals(new UtilitieItems().OPTIONSBUTTON(pu.get(3, false), "option_noParty", "§e")
											.getItemMeta().getDisplayName())) {
								pu.toggleOption("option_noParty");
								this.reOpenInv(p);
								return;
							}
							if (e.getCurrentItem().getItemMeta().getDisplayName()
									.equals(new UtilitieItems().OPTIONSBUTTON(pu.get(3, false), "option_noJumping", "§c")
											.getItemMeta().getDisplayName())) {
								pu.toggleOption("option_noJumping");
								this.reOpenInv(p);
								return;
							}
							if (e.getCurrentItem().getItemMeta().getDisplayName()
									.equals(new UtilitieItems().OPTIONSBUTTON(pu.get(3, false), "option_noMsg", "§d")
											.getItemMeta().getDisplayName())) {
								pu.toggleOption("option_noMsg");
								this.reOpenInv(p);
								return;
							}
							if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.OPTIONS_STATUSITEM.getItem().getItemMeta().getDisplayName())) {
								AnvilGUI anvilGUI = new AnvilGUI(p, new AnvilClickEventHandler() {
									
									@Override
									public void onAnvilClick(AnvilClickEvent event) {
										if(event.getSlot().equals(AnvilSlot.OUTPUT)) {
											event.setWillClose(true);
											event.setWillDestroy(true);
											if(FileManager.ConfigCfg.getBoolean("Friends.Options.Status.Delay.Enable"))
												if(StatusCommand.lastUsed.containsKey(p.getUniqueId().toString()))
													if((System.currentTimeMillis() - StatusCommand.lastUsed.get(p.getUniqueId().toString())) 
															< (FileManager.ConfigCfg.getLong("Friends.Options.Status.Delay.TimeStamp")*1000)) {
														p.sendMessage(plugin.getString("Messages.Status.TooFast"));
														return;
													}
											StatusCommand.lastUsed.put(p.getUniqueId().toString(), System.currentTimeMillis());
											pu.setStatus(event.getName());
											p.sendMessage(plugin.getString("Messages.Status.ChangeStatus"));
											return;
										}
										event.setWillClose(false);
										event.setWillDestroy(false);
										return;
									}
								});
								
								ItemStack ITEM = new ItemStack(Material.NAME_TAG);
								ItemMeta META = ITEM.getItemMeta();
								String STATUS = pu.getStatus() != null ? pu.getStatus() : "Status";
								META.setDisplayName(STATUS);
								ITEM.setItemMeta(META);
								
								anvilGUI.setSlot(AnvilSlot.INPUT_LEFT, ITEM);
								anvilGUI.setSlot(AnvilSlot.OUTPUT, new ItemStack(Material.PAPER));
								try {
									anvilGUI.open();
								} catch (IllegalAccessException | InvocationTargetException
										| InstantiationException e1) {
									e1.printStackTrace();
								}
								return;
							}
							if (e.getCurrentItem().equals(ItemStacks.OPTIONS_BACK.getItem())) {
								InventoryBuilder.openInv(p, InventoryBuilder.INVENTORY(plugin, p, InventoryTypes.MAIN, false));
								return;
							}
						}
					}
				}
			}
		}
	}

	public void reOpenInv(final Player player) {
		InventoryBuilder.openInv(player, InventoryBuilder.OPTIONS_INVENTORY(player, false));
	}

}
