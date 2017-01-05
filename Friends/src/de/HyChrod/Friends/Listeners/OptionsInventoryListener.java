/*
*


* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.Listeners;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.StatusCommand;
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.SQL.Callback;
import de.HyChrod.Friends.Utilities.AnvilGUI;
import de.HyChrod.Friends.Utilities.AnvilGUI.AnvilClickEvent;
import de.HyChrod.Friends.Utilities.AnvilGUI.AnvilClickEventHandler;
import de.HyChrod.Friends.Utilities.AnvilGUI.AnvilSlot;
import de.HyChrod.Friends.Utilities.FriendPlayer;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.InventoryTypes;
import de.HyChrod.Friends.Utilities.ItemStacks;
import de.HyChrod.Friends.Utilities.PlayerUtilities;
import de.HyChrod.Friends.Utilities.UtilitieItems;

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
							
							plugin.pool.execute(new Runnable() {
								
								@Override
								public void run() {
									try {
										PlayerUtilities pu = PlayerUtilities.getUtilities(p.getUniqueId().toString());
										FriendPlayer FP = FriendPlayer.getPlayer(p.getUniqueId().toString());
										while(!pu.isFinished || !FP.isFinshed)
											synchronized (this) {
												wait(5L);
											}
										if (e.getCurrentItem().getItemMeta().getDisplayName()
												.equals(new UtilitieItems().OPTIONSBUTTON(pu.getOptions(), "option_noRequests", "§a")
														.getItemMeta().getDisplayName())) {
											pu.toggleOption("option_noRequests");
											reOpenInv(p);
											return;
										}
										if (e.getCurrentItem().getItemMeta().getDisplayName()
												.equals(new UtilitieItems().OPTIONSBUTTON(pu.getOptions(), "option_noChat", "§b")
														.getItemMeta().getDisplayName())) {
											pu.toggleOption("option_noChat");
											reOpenInv(p);
											return;
										}
										if(e.getCurrentItem().getItemMeta().getDisplayName()
												.equals(new UtilitieItems().OPTIONSBUTTON(pu.getOptions(), "option_noParty", "§e")
														.getItemMeta().getDisplayName())) {
											pu.toggleOption("option_noParty");
											reOpenInv(p);
											return;
										}
										if (e.getCurrentItem().getItemMeta().getDisplayName()
												.equals(new UtilitieItems().OPTIONSBUTTON(pu.getOptions(), "option_noJumping", "§c")
														.getItemMeta().getDisplayName())) {
											pu.toggleOption("option_noJumping");
											reOpenInv(p);
											return;
										}
										if (e.getCurrentItem().getItemMeta().getDisplayName()
												.equals(new UtilitieItems().OPTIONSBUTTON(pu.getOptions(), "option_noMsg", "§d")
														.getItemMeta().getDisplayName())) {
											pu.toggleOption("option_noMsg");
											reOpenInv(p);
											return;
										}
										if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.OPTIONS_STATUSITEM.getItem().getItemMeta().getDisplayName())) {
											if(!p.hasPermission("Friends.Commands.Status")) {
												return;
											}
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
														FP.setStatus(event.getName());
														
														for(FriendPlayer fp : pu.getFriends()) {
															OfflinePlayer pp = Bukkit.getOfflinePlayer(UUID.fromString(fp.getUUID()));
															String data = p.getName() + "@" + event.getName();
															BungeeMessagingListener.isOnline(pp, new Callback<Boolean>() {
																
																@Override
																public void call(Boolean done) {
																	if(done)
																		Bukkit.getServer().getPluginManager().callEvent(new BungeeSpreadEvent(p, pp.getName(), "UpdateStatus", data));
																}
															});
														}
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
									} catch (Exception ex) {ex.printStackTrace();}
								}
							});
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
