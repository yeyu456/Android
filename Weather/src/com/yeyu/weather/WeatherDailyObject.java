package com.yeyu.weather;

public class WeatherDailyObject extends WeatherObject {
	
	public float precipIntensityMax;
	public long precipIntensityMaxTime;
	public long sunriseTime;
	public long sunsetTime;
    public float moonPhase;
    public float temperatureMin;
    public long temperatureMinTime;
    public float temperatureMax;
    public long temperatureMaxTime;
    public float apparentTemperatureMin;
    public long apparentTemperatureMinTime;
    public float apparentTemperatureMax;
    public long apparentTemperatureMaxTime;
    
	public WeatherDailyObject(double mlatitude, double mlongitude) {
		super(mlatitude, mlongitude);
	}
}
