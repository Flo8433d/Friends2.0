/*
*
* This class was made by VortexTM
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {

	public static String host, port, database, username, passwort;
	public static Connection con;

	public static void connect() {
		if(!isConnected()) {
			try {
				con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, passwort);
			} catch (SQLException e) {
			}
		}
	}
	
	public static void perform() {
		connect();
		if(isConnected()) {
			try {
				PreparedStatement ps = getConnection()
						.prepareStatement("CREATE TABLE IF NOT EXISTS friends2_0(UUID VARCHAR(50), FRIENDS TEXT, BLOCKED TEXT, REQUESTS TEXT, OPTIONS TEXT, LASTONLINE TEXT)");
				ps.executeUpdate();
			} catch (Exception ex) {
			}
		}
	}
	
	public static void performBungee() {
		if(isConnected()) {
			try {
				PreparedStatement ps = getConnection()
						.prepareStatement("CREATE TABLE IF NOT EXISTS friends2_0_BUNGEE(UUID VARCHAR(50), ONLINE INT, SERVER TEXT, LASTONLINE TEXT)");
				ps.executeUpdate();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void disconnect() {
		if(isConnected()) {
			try {
				con.close();
			} catch (SQLException e) {
			}
		}
	}
	
	public static boolean isConnected() {
		if(con == null) {return false;}
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
		} catch (SQLException e) {e.printStackTrace();}
	}

    public static ResultSet query(String qry) {
    	ResultSet rs = null;
       
    	try {
    		Statement st = con.createStatement();
    		rs = st.executeQuery(qry);
    	} catch (SQLException e) {connect();}
    	return rs;
    }
}