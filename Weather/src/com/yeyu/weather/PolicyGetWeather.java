package com.yeyu.weather;

import java.util.ArrayList;

import android.content.Context;

public class PolicyGetWeather {
	
	private Context mContext;
	public static final int MAX_COUNT_HOURLY_DATA = 6;
	public static final int MAX_COUNT_DAILY_DATA = 7;
	
	public PolicyGetWeather(Context context){
		mContext = context;
	}
	
	public ArrayList<WeatherObject> getWeather(double latitude, double longitude){
		if(CheckRequireSetting.isNetworkEnable(mContext)){
			return WeatherForecastAPI.getDefaultWeather(latitude, longitude);
		} else {
			return null;
		}
	}
	
}
