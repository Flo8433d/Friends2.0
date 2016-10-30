/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends;

import de.HyChrod.Friends.Listeners.ChangeServerListener;
import de.HyChrod.Friends.Listeners.ChatListener;
import de.HyChrod.Friends.Listeners.LoginListener;
import de.HyChrod.Friends.SQL.BungeeSQL_Manager;
import de.HyChrod.Friends.SQL.MySQL;
import de.HyChrod.Friends.Util.UpdateChecker;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;


public class Friends extends Plugin {
	public String prefix;
	private FileManager mgr = new FileManager();
	public Configuration ConfigCfg = this.mgr.getConfig("", "config.yml");
	public Configuration MessagesCfg = this.mgr.getConfig("", "Messages.yml");
	
	public static Friends instance;

	public void onEnable() {
		this.mgr.createFolders(this);
		this.mgr.setStandartMySQL();
		this.mgr.setStandartConfig();
		if ((this.ConfigCfg == null) || (this.ConfigCfg != null && this.ConfigCfg.getString("Friends.Prefix") == null)) {
			BungeeCord.getInstance().stop();
			return;
		}
		instance = this;
		
		this.prefix = ChatColor.translateAlternateColorCodes('&', this.ConfigCfg.getString("Friends.Prefix"));
		this.mgr.readMySQLData();
		try {
			MySQL.connect();
			MySQL.performBungee();
			if (!MySQL.isConnected()) {
				System.out.println("Friends | ");
				System.out.println("Friends | ");
				System.out.println("Friends | ");
				System.out.println("Friends | ");
				System.out.println("Friends | ");
				System.out.println("Friends | ");
				System.out.println("Friends | Can't connect to MySQL!");
				System.out.println("Friends | Please check your login data and try again!");
				System.out.println("Friends | ");
				System.out.println("Friends | ");
				System.out.println("Friends | ");
				System.out.println("Friends | ");
				System.out.println("Friends | ");
				System.out.println("Friends | ");
				return;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		BungeeCord.getInstance().getPluginManager().registerListener(this, new LoginListener(this));
		BungeeCord.getInstance().getPluginManager().registerListener(this, new ChangeServerListener());
		BungeeCord.getInstance().getPluginManager().registerListener(this, new ChatListener(this));
		if ((!UpdateChecker.check()) && (this.ConfigCfg.getBoolean("Friends.CheckForUpdates"))) {
			System.out.println("Friends | ");
			System.out.println("Friends | ");
			System.out.println("Friends | ");
			System.out.println("Friends | ");
			System.out.println("Friends |  A new update is available!");
			System.out.println("Friends |  Please update your plugin!");
			System.out.println("Friends |  You will get no support for this version!!");
			System.out.println("Friends | ");
			System.out.println("Friends | ");
			System.out.println("Friends | ");
			System.out.println("Friends | ");
		}
		System.out.println("Friends | ");
		System.out.println("Friends | The Plugin was loaded successfully!");
		System.out.println("Friends | ");
	}

	public void onDisable() {
		try {
			if (MySQL.isConnected()) {
				MySQL.disconnect();
			}
			for (ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
				BungeeSQL_Manager.set(player.getUniqueId().toString(),
						Long.valueOf(System.currentTimeMillis()), "LASTONLINE");
				BungeeSQL_Manager.set(player.getUniqueId().toString(), Integer.valueOf(0), "ONLINE");
			}
		} catch (Exception localException) {
		}
	}

	public String getString(String path) {
		return ChatColor.translateAlternateColorCodes('&',
				this.MessagesCfg.getString(path).replace("%PREFIX%", this.prefix));
	}
	
	public static Friends getInstance() {
		return instance;
	}
}
