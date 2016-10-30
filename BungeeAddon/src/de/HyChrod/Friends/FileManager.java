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
import java.nio.file.Files;

import de.HyChrod.Friends.SQL.MySQL;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class FileManager {
	
	public File getFile(String path, String name) {
		return new File("plugins/Friends2.0-BungeeAddon" + path, name);
	}
	
	public Configuration getConfig(String path, String name) {
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.getFile(path, name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Configuration getConfig(File file) {
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void saveFile(Configuration cfg, File file) {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save(Configuration cfg, File file, String path, Object obj) {
		cfg.set(path, obj);
		this.save(cfg, file, path, obj);
	}
	
	public void setStandartConfig() {
		Configuration cfg = this.getConfig("", "config.yml");
		
		if(cfg.get("Friends.Prefix") == null) {
			saveFile(cfg, this.getFile("", "config.yml"));
		}
	}
	
	public void setStandartMySQL() {
		Configuration cfg = this.getConfig("", "MySQL.yml");
		
		if(cfg.get("MySQL.Host") == null) {
			cfg.set("MySQL.Host", "yourHost");
			cfg.set("MySQL.Port", "3306");
			cfg.set("MySQL.Database", "yourDatabase");
			cfg.set("MySQL.Username", "yourUsername");
			cfg.set("MySQL.Password", "yourPassword");
			saveFile(cfg, this.getFile("", "MySQL.yml"));
		}
	}
	
	public void readMySQLData() {
		Configuration cfg = this.getConfig("", "MySQL.yml");
		
		MySQL.host = cfg.getString("MySQL.Host");
		MySQL.port = cfg.getString("MySQL.Port");
		MySQL.database = cfg.getString("MySQL.Database");
		MySQL.username = cfg.getString("MySQL.Username");
		MySQL.passwort = cfg.getString("MySQL.Password");
	}
	
	public void createFolders(Friends plugin) {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		
		File MySQLFile = this.getFile("", "MySQL.yml");
		File MessagesFile = this.getFile("", "Messages.yml");
		File ConfigFile = this.getFile("", "config.yml");
		
		if (!MySQLFile.exists()) {
			try {
				MySQLFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (!ConfigFile.exists()) {
            try (InputStream in = plugin.getResourceAsStream("config.yml")) {
                Files.copy(in, ConfigFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		if (!MessagesFile.exists()) {
            try (InputStream in = plugin.getResourceAsStream("Messages.yml")) {
                Files.copy(in, MessagesFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
}
