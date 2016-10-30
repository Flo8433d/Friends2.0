/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Util;

import java.util.LinkedList;
import java.util.List;

import de.HyChrod.Friends.SQL.SQL_Manager;

public class PlayerUtilities {
	public String uuid;

	public PlayerUtilities(String uuid) {
		this.uuid = uuid;
	}

	public LinkedList<String> getFriends() {
		return SQL_Manager.getFriends(this.uuid);
	}
	public List<String> getOptions() {
		return SQL_Manager.getOptions(this.uuid);
	}
}
