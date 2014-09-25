package com.yeyu.weather;

import java.util.HashMap;

public class WeatherConstant {
	
	public static final String TYPE_WEATHER_HOURLY = "hourly";
	public static final String TYPE_WEATHER_DAILY = "daily";
	
	public static final String TEMPERATURE_SIGN = "\u2103";
	public static final String WATER_SIGN = "mm";
	
	public static final int TYPE_WEATHER_DETAIL_TEM = 1;
	public static final int TYPE_WEATHER_DETAIL_WATER = 2;
	public static final int TYPE_WEATHER_DETAIL_WIND = 3;
	public static final int TYPE_WEATHER_DETAIL_CLOUD = 4;
	public static final int TYPE_WEATHER_DETAIL_SUN = 5;
	public static final int TYPE_WEATHER_DETAIL_MOON = 6;
	
	public static final int TEMPERATURE_POSITIVE_MAX = 70;
	public static final int TEMPERATURE_NEGATIVE_MAX = 90;
	
	public static final int WIND_LEVEL_MAX = 12;
	public static final int WIND_LEVEL_TORNADO_MAX = 18;
	public static final int WIND_LEVEL_MIN = 0;
	public static final float WIND_TORNADO_SPEED = 37f;
	public static final float WIND_CALCULATE_CONSTANT = 0.836f;
	
	public static final float PRECIPITATION_RATE_MAX = 50f;
	public static final float PRECIPITATION_RATE_MIN = 0f;
	
	public static final float CLOUD_COVER_PERCENTAGE_MAX = 1f;
	public static final float CLOUD_COVER_PERCENTAGE_MIN = 0f;
	
	public static HashMap<String, String> imageColorMap = new HashMap<String, String>();
	static {
		imageColorMap.put("sunny", "#FF5722");
		imageColorMap.put("moon", "#FFEB3B");
		imageColorMap.put("rainy_s", "#607D8B");
		imageColorMap.put("rainy_m", "#455A64");
		imageColorMap.put("rainy_h", "#263238");
		imageColorMap.put("snowy", "#FAFAFA");
		imageColorMap.put("rainy_snowy", "#CFD8DC");
		imageColorMap.put("windy", "#259B24");
		imageColorMap.put("fog", "#795548");
		imageColorMap.put("cloudy", "#009688");
		imageColorMap.put("partlycloudy", "#03A9F4");
		imageColorMap.put("partlycloudynight", "#5677FC");
	}
	public static HashMap<String, String> iconImageMap = new HashMap<String, String>();
	static {
		iconImageMap.put("clear-day", "sunny");
		iconImageMap.put("clear-night", "moon");
		iconImageMap.put("rain", "rainy");
		iconImageMap.put("snow", "snowy");
		iconImageMap.put("sleet", "rainy_snowy");
		iconImageMap.put("wind", "windy");
		iconImageMap.put("fog", "fog");
		iconImageMap.put("cloudy", "cloudy");
		iconImageMap.put("partly-cloudy-day", "partlycloudy");
		iconImageMap.put("partly-cloudy-night", "partlycloudynight");
	}
	public static HashMap<Integer, Integer> typeColorMap = new HashMap<Integer, Integer>();
	static {
		typeColorMap.put(TYPE_WEATHER_DETAIL_TEM, 0xFFFF5722);
		typeColorMap.put(TYPE_WEATHER_DETAIL_WATER, 0xFF5677FC);
		typeColorMap.put(TYPE_WEATHER_DETAIL_WIND, 0xFF259B24);
		typeColorMap.put(TYPE_WEATHER_DETAIL_CLOUD, 0xFF009688);
		typeColorMap.put(TYPE_WEATHER_DETAIL_SUN, 0xFFFFEB3B);
		typeColorMap.put(TYPE_WEATHER_DETAIL_MOON, 0xFFFFC107);
	}
}
