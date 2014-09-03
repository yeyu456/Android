package com.yeyu.weather;

import android.content.Context;
import android.location.Location;

public class PolicyGetLocation {
	
	private Context mContext;
	
	public PolicyGetLocation(Context context){
		mContext = context;
	}
	
	public Location getLocation(){
		if(CheckRequireSetting.isNetworkEnable(mContext)){
			return LocationBaiduAPI.baiduLocationByIp();
		} else {
			return null;
		}
	}

}
