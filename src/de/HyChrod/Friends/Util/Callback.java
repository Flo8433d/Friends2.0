/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Friends.Util;

public interface Callback<V extends Object, T extends Throwable> {
	public void call(V result, T thrown);
}