/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import de.HyChrod.Friends.Commands.FriendCommands;
import de.HyChrod.Friends.Listeners.BlockedEditInventoryListener;
import de.HyChrod.Friends.Listeners.BlockedInventoryListener;
import de.HyChrod.Friends.Listeners.BungeeMessagingListener;
import de.HyChrod.Friends.Listeners.ChangeWorldListener;
import de.HyChrod.Friends.Listeners.ChatListener;
import de.HyChrod.Friends.Listeners.DamageListener;
import de.HyChrod.Friends.Listeners.EditInventoryListener;
import de.HyChrod.Friends.Listeners.InteractListener;
import de.HyChrod.Friends.Listeners.ItemListener;
import de.HyChrod.Friends.Listeners.JoinListener;
import de.HyChrod.Friends.Listeners.MainInventoryListener;
import de.HyChrod.Friends.Listeners.OptionsInventoryListener;
import de.HyChrod.Friends.Listeners.PlayerSwapHandItemsListener;
import de.HyChrod.Friends.Listeners.QuitListener;
import de.HyChrod.Friends.Listeners.RemoveVerificationInventoryListener;
import de.HyChrod.Friends.Listeners.RequestEditInventoryListener;
import de.HyChrod.Friends.Listeners.RequestsInventoryListener;
import de.HyChrod.Friends.SQL.MySQL;
import de.HyChrod.Friends.SQL.SQL_Manager;
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
 * Friends.Commands.List
 * Friends.Commands.Jump
 * Friends.Commands.Msg
 * Friends.Commands.Reload
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
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeMessagingListener(this));
		registerClasses();

		if (!UpdateChecker.check() && FileManager.ConfigCfg.getBoolean("Friends.CheckForUpdates")) {
			Bukkit.getConsoleSender().sendMessage(this.prefix + " §cA new update is available!");
			Bukkit.getConsoleSender().sendMessage(this.prefix + " §cPlease update your plugin!");
			Bukkit.getConsoleSender().sendMessage(this.prefix + " §cYou will get no support for this version!!");
		}
		Bukkit.getConsoleSender().sendMessage(this.prefix + " §aPlugin was loaded successfully!");
		if (MySQL.isConnected()) {
			Bukkit.getConsoleSender().sendMessage(this.prefix + " §aMode: §2MySQL");
		} else {
			Bukkit.getConsoleSender().sendMessage(this.prefix + " §aMode: §3FlatFile");
		}
		if (bungeeMode) {
			Bukkit.getConsoleSender().sendMessage(this.prefix + " §9§n< BungeeMode >");
		}
		return;
	}

	private void registerClasses() {
		this.getCommand("Friends").setExecutor(new FriendCommands(this));

		this.getServer().getPluginManager().registerEvents(new QuitListener(this), this);
		this.getServer().getPluginManager().registerEvents(new JoinListener(this), this);
		this.getServer().getPluginManager().registerEvents(new ChatListener(this), this);
		this.getServer().getPluginManager().registerEvents(new InteractListener(this), this);
		this.getServer().getPluginManager().registerEvents(new MainInventoryListener(this), this);
		this.getServer().getPluginManager().registerEvents(new OptionsInventoryListener(this), this);
		this.getServer().getPluginManager().registerEvents(new RequestsInventoryListener(this), this);
		this.getServer().getPluginManager().registerEvents(new BlockedInventoryListener(this), this);
		this.getServer().getPluginManager().registerEvents(new EditInventoryListener(this), this);
		this.getServer().getPluginManager().registerEvents(new RemoveVerificationInventoryListener(this), this);
		this.getServer().getPluginManager().registerEvents(new RequestEditInventoryListener(this), this);
		this.getServer().getPluginManager().registerEvents(new BlockedEditInventoryListener(this), this);
		this.getServer().getPluginManager().registerEvents(new ItemListener(), this);
		this.getServer().getPluginManager().registerEvents(new ChangeWorldListener(), this);
		this.getServer().getPluginManager().registerEvents(new DamageListener(this), this);

		if (this.getServer().getBukkitVersion().startsWith("1.10")
				|| this.getServer().getBukkitVersion().startsWith("1.9")) {
			this.getServer().getPluginManager().registerEvents(new PlayerSwapHandItemsListener(), this);
		}

		new SQL_Manager();
	}

	@Override
	public void onDisable() {
		try {
			PlayerUtilities.fullSave(true);
			if (MySQL.isConnected()) {
				MySQL.disconnect();
			}
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
