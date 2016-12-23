/*
*
* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.Util;

import org.bukkit.Bukkit;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.MySQL;

public class AsyncMySQLReconnecter {
	
	public static Integer scheduler;
	
	@SuppressWarnings("deprecation")
	public AsyncMySQLReconnecter() {
		scheduler = Bukkit.getScheduler().scheduleAsyncRepeatingTask(Friends.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				while(MySQL.isConnected()) {
					MySQL.disconnect();
				}
				while(!MySQL.isConnected()) {
					MySQL.connect();
				}
			}
		}, 20*5, 20*60*5);
	}

}