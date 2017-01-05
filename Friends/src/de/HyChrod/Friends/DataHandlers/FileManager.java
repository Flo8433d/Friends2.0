/*
*
* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.DataHandlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.HyChrod.Friends.Friends;

public class FileManager {

	public static FileConfiguration ConfigCfg, MySQLCfg, MessagesCfg;
	public static File ConfigFile, MySQLFile, MessagesFile;
	public static String[] SQL_DATA = new String[5];

	public static File getFile(String path, String name) {
		return new File("plugins/Friends2_0" + path, name);
	}

	public static FileConfiguration getConfig(String path, String name) {
		return new UTF8YamlConfiguration(getFile(path, name));
	}

	public static FileConfiguration getConfig(File file) {
		return new UTF8YamlConfiguration(file);
	}

	private static void saveFile(File file, FileConfiguration cfg) {
		try {
			cfg.save(file);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void save(File file, FileConfiguration cfg, String path, Object obj) {
		cfg.set(path, obj);
		saveFile(file, cfg);
	}

	public static void reloadConfigs(Friends plugin, boolean prefix) {
		ConfigFile = getFile("", "config.yml");
		ConfigCfg = getConfig(ConfigFile);
		MySQLFile = getFile("", "MySQL.yml");
		MySQLCfg = getConfig(MySQLFile);
		MessagesFile = getFile("", "Messages.yml");
		MessagesCfg = getConfig(MessagesFile);

		if (prefix)
			plugin.prefix = ChatColor.translateAlternateColorCodes('&', ConfigCfg.getString("Friends.Prefix"));
	}

	@SuppressWarnings("deprecation")
	private static void loadFile(Friends friends, String name) {
		File file = new File(friends.getDataFolder(), name);
		if (file.exists()) {
			return;
		}
		try {
			InputStream defConfigStream = friends.getResource(name);
			if (defConfigStream != null)
				YamlConfiguration.loadConfiguration(defConfigStream).save(file);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void setupFiles(Friends friends) {
		friends.saveDefaultConfig();
		loadFile(friends, "Messages.yml");
		loadFile(friends, "MySQL.yml");
		flashNewData();
		
		FileConfiguration Mcfg = getConfig("", "MySQL.yml");
		SQL_DATA[0] = Mcfg.getString("MySQL.Host");
		SQL_DATA[1] = Mcfg.getString("MySQL.Port");
		SQL_DATA[2] = Mcfg.getString("MySQL.Database");
		SQL_DATA[3] = Mcfg.getString("MySQL.Username");
		SQL_DATA[4] = Mcfg.getString("MySQL.Password");

		reloadConfigs(friends, true);
	}

	private static void flashNewData() {
		File file = getFile("", "Messages.yml");
		FileConfiguration cfg = getConfig(file);

		if (cfg.getString("Messages.Commands.Block.PlayerOffline") == null) {
			try {
				createBackup(file);
				if(cfg.getString("Messages.Commands.Jumping.DisabledServer") == null) {
					if(cfg.getString("Messages.Commands.Jumping.DisabledWorlds") == null) {
						cfg.set("Messages.Commands.Jumping.DisabledWorld", "%PREFIX% &cYou can't jump to this player at the moment!");
						cfg.set("Messages.Commands.Help.WrongSite", "%PREFIX% &cThis site does not exist!");
						cfg.set("Messages.Status.TooFast", "%PREFIX% &cYou can only change your status every 10 minutes!");
						cfg.set("Messages.Status.ChangeStatus", "%PREFIX% &aYou successfully changed your status!");
						cfg.set("Messages.Status.Clear.UnknownPlayer", "%PREFIX% &cThis player has'nt choosed a status yet!");
						cfg.set("Messages.Status.Clear.Clear", "%PREFIX% &3%PLAYER%&a's status was cleared!");
					}
					cfg.set("Messages.Commands.Denyall.NoRequests", "%PREFIX% &cYou don't have any request!");
					cfg.set("Messages.Commands.Denyall.Deny", "%PREFIX% &cYou denied &3%COUNT% &crequests!");
					cfg.set("Messages.Commands.Jumping.DisabledServer", "%PREFIX% &cYou can't jump to the server this player is currently on!");
				}
				cfg.set("Messages.Commands.Block.PlayerOffline", "%PREFIX% &cThis player hasn't played on this server before!");
				cfg.set("Messages.GUI.LoadData", "%PREFIX% &cPlease wait a moment, your data is being loaded..");
				cfg.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		File Cfile = getFile("", "config.yml");
		FileConfiguration Ccfg = getConfig(Cfile);
		
		if(Ccfg.getString("Friends.EnabledServers.Enable") == null) {
			try {
				createBackup(Cfile);
				if(Ccfg.getString("Friends.GUI.OptionsInv.ButtonOff.Lore") == null) {
					if(Ccfg.getString("Friends.GUI.OptionsInv.ButtonOff.Lore") == null) {
						Ccfg.set("Friends.GUI.OptionsInv.StatusItem.Enable", true);
						Ccfg.set("Friends.GUI.OptionsInv.StatusItem.Name", "&7Your current Status:");
						Ccfg.set("Friends.GUI.OptionsInv.StatusItem.ItemID", "421:0");
						Ccfg.set("Friends.GUI.OptionsInv.StatusItem.NoStatusLore", "&cNo status set!");
						Ccfg.set("Friends.GUI.OptionsInv.StatusItem.InventorySlot", 37);
						Ccfg.set("Friends.GUI.OptionsInv.ButtonOff.Lore", "");
					}
					Ccfg.set("Friends.GUI.RequestsInv.AcceptallItem.Name", "&aAcceptall");
					Ccfg.set("Friends.GUI.RequestsInv.AcceptallItem.ItemID", "35:5");
					Ccfg.set("Friends.GUI.RequestsInv.AcceptallItem.Lore", "&7Click to accept all requests!");
					Ccfg.set("Friends.GUI.RequestsInv.AcceptallItem.InventorySlot", 50);
					Ccfg.set("Friends.GUI.RequestsInv.DenyallItem.Name", "&cDenyall");
					Ccfg.set("Friends.GUI.RequestsInv.DenyallItem.ItemID", "35:14");
					Ccfg.set("Friends.GUI.RequestsInv.DenyallItem.Lore", "&7Click to deny all requests!");
					Ccfg.set("Friends.GUI.RequestsInv.DenyallItem.InventorySlot", 49);
					Ccfg.set("Friends.DisabledServer.Enable", true);
					Ccfg.set("Friends.DisabledServers.Servers", new ArrayList<>(Arrays.asList("silent_hub", "premium_lobby")));
				}
				Ccfg.set("Friends.EnabledServers.Enable", false);
				Ccfg.set("Friends.EnabledServers.Servers", new ArrayList<>(Arrays.asList("lobby_1")));
				Ccfg.save(Cfile);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void createBackup(File file) throws FileNotFoundException, IOException {
		File fDes = getFile("", file.getName() + ".bak");
		FileInputStream fis = new FileInputStream(file);
		FileOutputStream fos = new FileOutputStream(fDes);

		byte buf[] = new byte[1024];
		while (fis.read(buf) != -1) {
			fos.write(buf);
		}
		fis.close();
		fos.flush();
		fos.close();
	}

}
