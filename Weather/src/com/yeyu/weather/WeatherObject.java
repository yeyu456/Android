package com.yeyu.weather;

public class WeatherObject {
	public String summary;
	public long time;
	public float precipIntensity;
	public float precipProbability;
	public String precipType;
	public float dewPoint;
	public float humidity;
	public float windSpeed;
	public int windBearing;
	public float visibility;
	public float cloudCover;
	public float ozone;
	public final double latitude;
	public final double longitude;
	
	public WeatherObject(double mlatitude, double mlongitude){
		latitude = mlatitude;
		longitude = mlongitude;
	}
}
