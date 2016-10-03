/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import de.HyChrod.Friends.SQL.ConnectionPool;

public class QueryBukkitRunnable extends BukkitRunnable {
	private final ConnectionPool dataSource;
	private final String statement;
	private final Callback<ResultSet, SQLException> callback;

	public QueryBukkitRunnable(ConnectionPool dataSource, String statement, Callback<ResultSet, SQLException> callback) {
		if (dataSource == null) {
			// TODO: IllegalArgumentException
		}

		if (statement == null) {
			// TODO: IllegalArgumentException
		}

		if (callback == null) {
			// TODO: IllegalArgumentException
		}

		this.dataSource = dataSource;
		this.statement = statement;
		this.callback = callback;
	}

	@Override
	public void run() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			try {
				connection = dataSource.borrowConnection();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			preparedStatement = connection.prepareStatement(statement);
			resultSet = preparedStatement.executeQuery();
			callback.call(resultSet, null);
		} catch (SQLException e) {
			callback.call(null, e);
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

			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}