package com.yeyu.weather;

import android.content.Context;

public class PolicyGetLocation {
	
	private Context mContext;
	
	public PolicyGetLocation(Context context){
		mContext = context;
	}
	
	public LocationObject getLocation(){
		if(CheckRequireSetting.isNetworkEnable(mContext)){
			return LocationBaiduAPI.baiduLocationByIp();
		} else {
			return null;
		}
	}

}
