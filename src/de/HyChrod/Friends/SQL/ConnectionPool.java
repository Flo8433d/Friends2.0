/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionPool {

	private static final Logger logger = Logger.getLogger(ConnectionPool.class.getCanonicalName());

	private BlockingQueue<Connection> pool;
	private int maxPoolSize;
	private int initialPoolSize;
	private int currentPoolSize;

	private String dbUrl;
	private String dbUser;
	private String dbPassword;

	public ConnectionPool(int maxPoolSize, int initialPoolSize, String url, String username, String password,
			String driverClassName) throws ClassNotFoundException, SQLException {

		if ((initialPoolSize > maxPoolSize) || initialPoolSize < 1 || maxPoolSize < 1) {
			throw new IllegalArgumentException("Invalid pool size parameters");
		}

		this.maxPoolSize = maxPoolSize > 0 ? maxPoolSize : 10;
		this.initialPoolSize = initialPoolSize;
		this.dbUrl = url;
		this.dbUser = username;
		this.dbPassword = password;
		this.pool = new LinkedBlockingQueue<Connection>(maxPoolSize);

		initPooledConnections(driverClassName);

		if (pool.size() != initialPoolSize) {
			logger.log(Level.WARNING,
					"Initial sized pool creation failed. InitializedPoolSize={0}, initialPoolSize={1}",
					new Object[] { pool.size(), initialPoolSize });
		}

	}
	
	public BlockingQueue<Connection> getPool() {
		return pool;
	}

	private void initPooledConnections(String driverClassName) throws ClassNotFoundException, SQLException {
		Class.forName(driverClassName);

		for (int i = 0; i < initialPoolSize; i++) {
			openAndPoolConnection();
		}
	}

	private synchronized void openAndPoolConnection() throws SQLException {
		if (currentPoolSize == maxPoolSize) {
			return;
		}

		Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
		pool.offer(new PooledConnection(conn, this));
		currentPoolSize++;

		logger.log(Level.FINE, "Created connection {0}, currentPoolSize={1}, maxPoolSize={2}",
				new Object[] { conn, currentPoolSize, maxPoolSize });
	}

	public Connection borrowConnection() throws InterruptedException, SQLException {
		if (pool.peek() == null && currentPoolSize < maxPoolSize) {
			openAndPoolConnection();
		}
		return pool.take();
	}

	public void surrenderConnection(Connection conn) {
		if (!(conn instanceof PooledConnection)) {
			return;
		}
		pool.offer(conn);
	}
}
