/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.HyChrod.Friends.SQL.MySQL;

public class FileManager {
	
	public File getFile(String path, String name) {
		return new File("plugins/Friends2_0" + path, name);
	}
	
	public FileConfiguration getConfig(String path, String name) {
		return YamlConfiguration.loadConfiguration(this.getFile(path, name));
	}
	
	public FileConfiguration getConfig(File file) {
		return YamlConfiguration.loadConfiguration(file);
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
	
	@SuppressWarnings("deprecation")
	public void loadFile(Friends friends, String name, String checkPath) {
		File messages = new File(friends.getDataFolder(), name);
		YamlConfiguration defConfig;
		if (messages.exists()) {
			if(getConfig(messages).getString(checkPath) != null) {
				return;
			}
	    }
		try {
			friends.getDataFolder().mkdir();
			messages.createNewFile();
			InputStream defConfigStream = friends.getResource(name);
			if (defConfigStream != null) {
				defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				defConfig.save(messages);
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setupFiles(Friends friends) {
		friends.saveDefaultConfig();
		FileConfiguration cfg = this.getConfig("", "config.yml");
		if(cfg.getString("Friends.FriendChat.FriendMSG") == null) {
			this.getFile("", "config.yml").delete();
			setupFiles(friends);
			return;
		}
		
		this.loadFile(friends, "Messages.yml", "Messages.Commands.WrongUsage");
		this.loadFile(friends, "MySQL.yml", "MySQL.Enable");
		
		FileConfiguration Mcfg = this.getConfig("", "MySQL.yml");
		MySQL.host = Mcfg.getString("MySQL.Host");
		MySQL.port = Mcfg.getString("MySQL.Port");
		MySQL.database = Mcfg.getString("MySQL.Database");
		MySQL.username = Mcfg.getString("MySQL.Username");
		MySQL.passwort = Mcfg.getString("MySQL.Password");
	}

}
