package com.yeyu.weather;

import android.content.Context;

public class PolicyGetWeather {
	
	private Context mContext;
	
	public PolicyGetWeather(Context context){
		mContext = context;
	}
	
	public WeatherObject[] getWeather(double latitude, double longitude){
		if(CheckRequireSetting.isNetworkEnable(mContext)){
			return WeatherForecastAPI.getDefaultWeather(latitude, longitude);
		} else {
			return null;
		}
	}
	
}
