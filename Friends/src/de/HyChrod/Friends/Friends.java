/*
 *
 * This class was made by HyChrod
 * All rights reserved, 2017
 *
 */
package de.HyChrod.Friends;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import de.HyChrod.Friends.Commands.CommandSerializable;
import de.HyChrod.Friends.Commands.FriendCommand;
import de.HyChrod.Friends.Commands.StatusCommand;
import de.HyChrod.Friends.DataHandlers.FileManager;
import de.HyChrod.Friends.Listeners.BlockedEditInventoryListener;
import de.HyChrod.Friends.Listeners.BungeeMessagingListener;
import de.HyChrod.Friends.Listeners.ChangeWorldListener;
import de.HyChrod.Friends.Listeners.ChatListener;
import de.HyChrod.Friends.Listeners.DamageListener;
import de.HyChrod.Friends.Listeners.EditInventoryListener;
import de.HyChrod.Friends.Listeners.InventoryUtilListener;
import de.HyChrod.Friends.Listeners.ItemListener;
import de.HyChrod.Friends.Listeners.JoinQuitListener;
import de.HyChrod.Friends.Listeners.OptionsInventoryListener;
import de.HyChrod.Friends.Listeners.PageListener;
import de.HyChrod.Friends.Listeners.PlayerSwapHandItemsListener;
import de.HyChrod.Friends.Listeners.RemoveVerificationInventoryListener;
import de.HyChrod.Friends.Listeners.RequestEditInventoryListener;
import de.HyChrod.Friends.SQL.MySQL;
import de.HyChrod.Friends.Utilities.Metrics;
import de.HyChrod.Friends.Utilities.UpdateChecker;

/*
 * Friends.Use
 * Friends.ExtraFriends
 * Friends.Commands.*
 * Friends.Commands.Add
 * Friends.Commands.Remove
 * Friends.Commands.Block
 * Friends.Commands.Accept
 * Friends.Commands.Deny
 * Friends.Commands.Unblock
 * Friends.Commands.Toggle.Requests
 * Friends.Commands.Toggle.Chat
 * Friends.Commands.Toggle.Msg
 * Friends.Commands.Toggle.Jumping
 * Friends.Commands.SpyChat (/f toggle spychat)
 * Friends.Commands.List
 * Friends.Commands.Jump
 * Friends.Commands.Msg
 * Friends.Commands.Reload
 * Friends.Commands.Acceptall
 * Friends.Commands.Status
 * Friends.Commands.Status.Clear
 * 
 */

public class Friends extends JavaPlugin {

	public String prefix;
	private static Friends instance;
	public static boolean bungeemode;
	
	public ExecutorService pool = Executors.newCachedThreadPool();
	
	@Override
	public void onEnable() {
		FileManager.setupFiles(this);
		if(FileManager.ConfigCfg.getString("Friends.Prefix") == null) {
			this.getServer().reload();
			return;
		}
		bungeemode = FileManager.ConfigCfg.getString("Friends.BungeeMode") != null ? FileManager.ConfigCfg.getBoolean("Friends.BungeeMode") : false;
		instance = this;
		if(FileManager.MySQLCfg.getBoolean("MySQL.Enable") || bungeemode) {
			MySQL.perform();
			if(!MySQL.isConnected()) {
				Bukkit.getConsoleSender().sendMessage(this.prefix + " §cCant connect to MySQL!");
				Bukkit.getConsoleSender().sendMessage(this.prefix + " §cPlease check your data and try again!");
				this.getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}
		registerClasses();
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeMessagingListener());
		if(!BlockedEditInventoryListener.simplyNameCheck(this))
			return;
		
		if(!UpdateChecker.check() && FileManager.ConfigCfg.getBoolean("Friends.CheckForUpdates")) {
			Bukkit.getConsoleSender().sendMessage(this.prefix + " §cA new update is available!");
			Bukkit.getConsoleSender().sendMessage(this.prefix + " §cPlease update your plugin!");
			Bukkit.getConsoleSender().sendMessage(this.prefix + " §cYou will get no support for this version!!");
		}
		Bukkit.getConsoleSender().sendMessage(this.prefix + " §aThe plugin was loaded successfully!");
		String MODE = MySQL.isConnected() ? bungeemode ? "§9- BungeeMode -" : "§d- MySQL -" : "§3- FlatFile -";
		Bukkit.getConsoleSender().sendMessage(this.prefix + " §aMode: " + MODE);
		return;
	}
	
	private void registerClasses() {
		try {
			new Metrics(this).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		new CommandSerializable();
		this.getCommand("Friends").setExecutor(new FriendCommand(this));
		this.getCommand("Status").setExecutor(new StatusCommand(this));
		
		this.getServer().getPluginManager().registerEvents(new InventoryUtilListener(this), this);
		this.getServer().getPluginManager().registerEvents(new JoinQuitListener(this), this);
		this.getServer().getPluginManager().registerEvents(new ChatListener(this), this);
		this.getServer().getPluginManager().registerEvents(new OptionsInventoryListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PageListener(this), this);
		this.getServer().getPluginManager().registerEvents(new EditInventoryListener(this), this);
		this.getServer().getPluginManager().registerEvents(new RemoveVerificationInventoryListener(this), this);
		this.getServer().getPluginManager().registerEvents(new RequestEditInventoryListener(this), this);
		this.getServer().getPluginManager().registerEvents(new BlockedEditInventoryListener(this), this);
		this.getServer().getPluginManager().registerEvents(new ItemListener(), this);
		this.getServer().getPluginManager().registerEvents(new ChangeWorldListener(), this);
		this.getServer().getPluginManager().registerEvents(new DamageListener(this), this);

		if (this.getServer().getBukkitVersion().startsWith("1.10")
				|| this.getServer().getBukkitVersion().startsWith("1.9") || this.getServer().getBukkitVersion().startsWith("1.11"))
			this.getServer().getPluginManager().registerEvents(new PlayerSwapHandItemsListener(), this);
	}

	@Override
	public void onDisable() {
		if(MySQL.isConnected())
			MySQL.disconnect();
		return;
	}
	
	public String getString(String path) {
		return ChatColor.translateAlternateColorCodes('&', FileManager.MessagesCfg.getString(path).replace("%PREFIX%", this.prefix));
	}
	
	public static Friends getInstance() {
		return instance;
	}
	
}
