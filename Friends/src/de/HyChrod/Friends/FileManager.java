/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.HyChrod.Friends.SQL.MySQL;

public class FileManager {

	public static FileConfiguration ConfigCfg, MySQLCfg, MessagesCfg;
	public static File ConfigFile, MySQLFile, MessagesFile;

	public File getFile(String path, String name) {
		return new File("plugins/Friends2_0" + path, name);
	}

	public FileConfiguration getConfig(String path, String name) {
		return new UTF8YamlConfiguration(getFile(path, name));
	}

	public FileConfiguration getConfig(File file) {
		return new UTF8YamlConfiguration(file);
	}

	public void saveFile(File file, FileConfiguration cfg) {
		try {
			cfg.save(file);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void save(File file, FileConfiguration cfg, String path, Object obj) {
		cfg.set(path, obj);
		this.saveFile(file, cfg);
	}

	public void reloadConfigs(Friends plugin, boolean prefix) {
		ConfigFile = this.getFile("", "config.yml");
		ConfigCfg = this.getConfig(ConfigFile);
		MySQLFile = this.getFile("", "MySQL.yml");
		MySQLCfg = this.getConfig(MySQLFile);
		MessagesFile = this.getFile("", "Messages.yml");
		MessagesCfg = this.getConfig(MessagesFile);

		if (prefix)
			plugin.prefix = ChatColor.translateAlternateColorCodes('&', ConfigCfg.getString("Friends.Prefix"));
	}

	@SuppressWarnings("deprecation")
	private void loadFile(Friends friends, String name, String toCheck) {
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

	public void setupFiles(Friends friends) {
		friends.saveDefaultConfig();
		this.loadFile(friends, "Messages.yml", "Messages.Commands.Reload.Reloaded");
		this.loadFile(friends, "MySQL.yml", "MySQL.Enable");
		setNewMessages();

		FileConfiguration Mcfg = this.getConfig("", "MySQL.yml");
		MySQL.host = Mcfg.getString("MySQL.Host");
		MySQL.port = Mcfg.getString("MySQL.Port");
		MySQL.database = Mcfg.getString("MySQL.Database");
		MySQL.username = Mcfg.getString("MySQL.Username");
		MySQL.passwort = Mcfg.getString("MySQL.Password");

		this.reloadConfigs(friends, false);
	}

	public void setNewMessages() {
		File file = this.getFile("", "Messages.yml");
		FileConfiguration cfg = this.getConfig(file);

		if (cfg.getString("Messages.Commands.Jumping.DisabledWorld") == null) {
			try {
				createBackup(file);
				cfg.set("Messages.Commands.Jumping.DisabledWorld", "%PREFIX% &cYou can't jump to this player at the moment!");
				cfg.set("Messages.Commands.Help.WrongSite", "%PREFIX% &cThis site does not exist!");
				cfg.set("Messages.Status.TooFast", "%PREFIX% &cYou can only change your status every 10 minutes!");
				cfg.set("Messages.Status.ChangeStatus", "%PREFIX% &aYou successfully changed your status!");
				cfg.set("Messages.Status.Clear.UnknownPlayer", "%PREFIX% &cThis player has'nt choosed a status yet!");
				cfg.set("Messages.Status.Clear.Clear", "%PREFIX% &3%PLAYER%&a's status was cleared!");
				cfg.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		File Cfile = this.getFile("", "config.yml");
		FileConfiguration Ccfg = this.getConfig(Cfile);
		
		if(Ccfg.getString("Friends.GUI.OptionsInv.ButtonOff.Lore") == null) {
			try {
				createBackup(Cfile);
				if(Ccfg.getString("Friends.GUI.OptionsInv.StatusItem.InventorySlot") == null) {
					Ccfg.set("Friends.GUI.OptionsInv.StatusItem.Enable", true);
					Ccfg.set("Friends.GUI.OptionsInv.StatusItem.Name", "&7Your current Status:");
					Ccfg.set("Friends.GUI.OptionsInv.StatusItem.ItemID", "421:0");
					Ccfg.set("Friends.GUI.OptionsInv.StatusItem.NoStatusLore", "&cNo status set!");
					Ccfg.set("Friends.GUI.OptionsInv.StatusItem.InventorySlot", 37);
				}
				Ccfg.set("Friends.GUI.OptionsInv.ButtonOff.Lore", "");
				Ccfg.save(Cfile);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void createBackup(File file) throws FileNotFoundException, IOException {
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
