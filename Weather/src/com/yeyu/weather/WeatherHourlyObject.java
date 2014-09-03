package com.yeyu.weather;

public class WeatherHourlyObject extends WeatherObject {
	
	public float temperature;
	public float apparentTemperature;
	
	public WeatherHourlyObject(double mlatitude, double mlongitude) {
		super(mlatitude, mlongitude);
	}
}
