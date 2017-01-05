/*
*
* This class was made by HyChrod
* All rights reserved, 2017
*
*/
package de.HyChrod.Friends.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.HyChrod.Friends.DataHandlers.FileManager;

public class MySQL {

	public static Connection con;

	public static void connect() {
		if (!isConnected()) {
			try {
				con = DriverManager.getConnection("jdbc:mysql://" + FileManager.SQL_DATA[0] + ":" + FileManager.SQL_DATA[1] + "/" + FileManager.SQL_DATA[2], 
						FileManager.SQL_DATA[3], FileManager.SQL_DATA[4]);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void perform() {
		connect();
		if (isConnected()) {
			try {
				PreparedStatement ps = getConnection().prepareStatement(
						"CREATE TABLE IF NOT EXISTS friends2_0(UUID VARCHAR(50), NAME VARCHAR(20), FRIENDS TEXT, BLOCKED TEXT, REQUESTS TEXT, OPTIONS TEXT, LASTONLINE TEXT, STATUS TEXT)");
				ps.executeUpdate();
				
				try {
					PreparedStatement ps1 = getConnection().prepareStatement("ALTER TABLE friends2_0 ADD COLUMN STATUS TEXT AFTER LASTONLINE;");
					ps1.executeUpdate();
				} catch (Exception ex) {}
				
				try {
					PreparedStatement ps2 = getConnection().prepareStatement("ALTER TABLE friends2_0 ADD COLUMN NAME VARCHAR(20) AFTER UUID;");
					ps2.executeUpdate();
				} catch (Exception ex) {}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void performBungee() {
		if (isConnected()) {
			try {
				PreparedStatement ps = getConnection().prepareStatement(
						"CREATE TABLE IF NOT EXISTS friends2_0_BUNGEE(UUID VARCHAR(50), ONLINE INT, SERVER TEXT, LASTONLINE TEXT)");
				ps.executeUpdate();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void disconnect() {
		if (isConnected()) {
			try {
				con.close();
				con = null;
			} catch (SQLException e) {
				e.printStackTrace();
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