/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import de.HyChrod.Friends.Commands.FriendCommands;
import de.HyChrod.Friends.Commands.StatusCommand;
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
import de.HyChrod.Friends.SQL.BungeeSQL_Manager;
import de.HyChrod.Friends.SQL.MySQL;
import de.HyChrod.Friends.SQL.SQL_Manager;
import de.HyChrod.Friends.Util.AsyncMySQLReconnecter;
import de.HyChrod.Friends.Util.Metrics;
import de.HyChrod.Friends.Util.PlayerUtilities;
import de.HyChrod.Friends.Util.UpdateChecker;

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
	public static boolean bungeeMode = false;
	private static Friends instance;

	private FileManager mgr = new FileManager();

	@Override
	public void onEnable() {
		this.mgr.setupFiles(this);
		if (FileManager.ConfigCfg.getString("Friends.Prefix") == null) {
			getServer().reload();
			return;
		}
		try {
			if (FileManager.MySQLCfg.getBoolean("MySQL.Enable")) {
				MySQL.perform();
				if (FileManager.ConfigCfg.getBoolean("Friends.BungeeMode"))
					MySQL.performBungee();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		instance = this;
		this.prefix = ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Friends.Prefix"));
		if (FileManager.ConfigCfg.getBoolean("Friends.BungeeMode"))
			bungeeMode = true;
		if (!MySQL.isConnected() && FileManager.ConfigCfg.getBoolean("Friends.BungeeMode")) {
			Bukkit.getConsoleSender().sendMessage(this.prefix + " §cTo use BungeeMode you have to use MySQL!");
			Bukkit.getConsoleSender().sendMessage(this.prefix + " §cPlease set up your MySQL Database and try again!");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeMessagingListener());
		registerClasses();

		if (!UpdateChecker.check() && FileManager.ConfigCfg.getBoolean("Friends.CheckForUpdates")) {
			Bukkit.getConsoleSender().sendMessage(this.prefix + " §cA new update is available!");
			Bukkit.getConsoleSender().sendMessage(this.prefix + " §cPlease update your plugin!");
			Bukkit.getConsoleSender().sendMessage(this.prefix + " §cYou will get no support for this version!!");
		}
		Bukkit.getConsoleSender().sendMessage(this.prefix + " §aPlugin was loaded successfully!");
		if (MySQL.isConnected()) {
			Bukkit.getConsoleSender().sendMessage(this.prefix + " §aMode: §2MySQL");
			new AsyncMySQLReconnecter();
		} else 
			Bukkit.getConsoleSender().sendMessage(this.prefix + " §aMode: §3FlatFile");
		if (bungeeMode)
			Bukkit.getConsoleSender().sendMessage(this.prefix + " §9§n< BungeeMode >");
		
		try {
	        Metrics metrics = new Metrics(this);
	        metrics.start();
	    } catch (IOException e) {
	    }
		return;
	}

	private void registerClasses() {
		this.getCommand("Friends").setExecutor(new FriendCommands(this));
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
				|| this.getServer().getBukkitVersion().startsWith("1.9"))
			this.getServer().getPluginManager().registerEvents(new PlayerSwapHandItemsListener(), this);

		new SQL_Manager();
		new BungeeSQL_Manager();
	}

	@Override
	public void onDisable() {
		try {
			PlayerUtilities.fullSave(true);
			if (MySQL.isConnected())
				MySQL.disconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return;
	}

	public String getString(String path) {
		return ChatColor.translateAlternateColorCodes('&',
				FileManager.MessagesCfg.getString(path).replace("%PREFIX%", this.prefix));
	}

	public static Friends getInstance() {
		return instance;
	}

}
