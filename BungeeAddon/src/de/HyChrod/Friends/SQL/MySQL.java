package de.HyChrod.Friends.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {
	
	public static String host;
	public static String port;
	public static String database;
	public static String username;
	public static String passwort;
	public static Connection con;

	public static void connect() {
		if (!isConnected()) {
			try {
				con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username,
						passwort);
			} catch (SQLException localSQLException) {
			}
		}
	}

	public static void performBungee() {
		if (isConnected()) {
			try {
				PreparedStatement ps = getConnection().prepareStatement(
						"CREATE TABLE IF NOT EXISTS friends2_0_BUNGEE(UUID VARCHAR(50), ONLINE INT, SERVER TEXT, LASTONLINE TEXT)");
				ps.executeUpdate();
			} catch (Exception localException) {
			}
		}
	}

	public static void disconnect() {
		if (isConnected()) {
			try {
				con.close();
			} catch (SQLException localSQLException) {
			}
		}
	}

	public static boolean isConnected() {
		if (con == null) {
			return false;
		}
		return true;
	}

	public static Connection getConnection() {
		return con;
	}

	public static void update(String qry) {
		try {
			Statement st = con.createStatement();
			st.executeUpdate(qry);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ResultSet query(String qry) {
		ResultSet rs = null;
		try {
			Statement st = con.createStatement();
			rs = st.executeQuery(qry);
		} catch (SQLException e) {
			connect();
		}
		return rs;
	}
}
