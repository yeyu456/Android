package com.yeyu.weather;

import java.util.ArrayList;

import android.content.Context;

public class PolicyGetWeather {
	
	private Context mContext;
	
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
