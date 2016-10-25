/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Util;

import java.util.concurrent.TimeUnit;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.MySQL;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class AsyncMySQLReconnecter {
	
	public static ScheduledTask scheduler;
	
	public AsyncMySQLReconnecter() {
		scheduler = BungeeCord.getInstance().getScheduler().schedule(Friends.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				while(MySQL.isConnected()) {
					MySQL.disconnect();
				}
				while(!MySQL.isConnected()) {
					MySQL.connect();
				}
			}
		}, 20*60*5, 20*60*5, TimeUnit.SECONDS);
	}

}