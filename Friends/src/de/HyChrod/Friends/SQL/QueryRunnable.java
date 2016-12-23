/*
 *
 * This class was made by HyChrod
 * All rights reserved, 2017
 *
 */
package de.HyChrod.Friends.SQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class QueryRunnable extends BukkitRunnable {

	private final String statement;
	private final Callback<ResultSet> callback;

	public QueryRunnable(String statement, Callback<ResultSet> callback) {
		this.callback = callback;
		this.statement = statement;
	}

	@Override
	public void run() {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			preparedStatement = MySQL.getConnection().prepareStatement(statement);
			resultSet = preparedStatement.executeQuery();
			callback.call(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
			callback.call(null);
			Bukkit.getConsoleSender().sendMessage("§cERROR");
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
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
