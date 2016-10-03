/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.annotation.Nullable;

import org.bukkit.scheduler.BukkitRunnable;

import de.HyChrod.Friends.SQL.ConnectionPool;

public class UpdateBukkitRunnable extends BukkitRunnable {
	private final ConnectionPool dataSource;
	private final String statement;
	private final Callback<Integer, SQLException> callback;
	
	public UpdateBukkitRunnable(ConnectionPool dataSource, String statement, @Nullable Callback<Integer, SQLException> callback) {
		this.dataSource = dataSource;
		this.statement = statement;
		this.callback = callback;
	}
	
	@Override
	public void run() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			try {
				connection = dataSource.borrowConnection();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			preparedStatement = connection.prepareStatement(statement);
			
			if (callback != null) {
				callback.call(preparedStatement.executeUpdate(), null);
			}
		} catch (SQLException e) {
			if (callback != null) {
				callback.call(null, e);
			}
		} finally {
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
