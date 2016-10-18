package de.HyChrod.Friends.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.Bukkit;


public class UpdateChecker {
	
	public static boolean check() {
		try {
            HttpURLConnection con = (HttpURLConnection) new URL(
                    "http://www.spigotmc.org/api/general.php").openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream()
                    .write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + "12063")
                            .getBytes("UTF-8"));
            String version = new BufferedReader(new InputStreamReader(
                    con.getInputStream())).readLine();
            if(!Bukkit.getPluginManager().getPlugin("Friends2_0").getDescription().getVersion().equals(version)) {
        		return false;
        	}
        } catch (Exception ex) {ex.printStackTrace();}
		return true;
	}

}