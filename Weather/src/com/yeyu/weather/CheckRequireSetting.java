package com.yeyu.weather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

public class CheckRequireSetting {
	
	public static boolean isNetworkEnable(Context mContext){
		ConnectivityManager conMan = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		
		if(mobile==State.CONNECTED||wifi==State.CONNECTED){
			return true;
		} else {
			return false;
		}
	}
}
