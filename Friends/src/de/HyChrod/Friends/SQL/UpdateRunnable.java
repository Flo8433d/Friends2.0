/*
 *
 * This class was made by HyChrod
 * All rights reserved, 2017
 *
 */
package de.HyChrod.Friends.SQL;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

public class UpdateRunnable extends BukkitRunnable {

	private final String statement;
	private final Callback<Integer> callback;

	public UpdateRunnable(String statement, Callback<Integer> callback) {
		this.statement = statement;
		this.callback = callback;
	}

	@Override
	public void run() {
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = MySQL.getConnection().prepareStatement(statement);
			int a = preparedStatement.executeUpdate();
			if(callback != null)
				callback.call(a);
		} catch (SQLException e) {
			if(callback != null)
				callback.call(null);
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
