package com.yeyu.weather;

import android.os.Parcel;
import android.os.Parcelable;

public class WeatherObjectDaily extends WeatherObject implements Parcelable {
	
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
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	super.writeToParcel(dest, flags);
    	dest.writeFloat(precipIntensityMax);
    	dest.writeLong(precipIntensityMaxTime);
    	dest.writeLong(sunriseTime);
    	dest.writeLong(sunsetTime);
    	dest.writeFloat(moonPhase);
    	dest.writeFloat(temperatureMin);
    	dest.writeLong(temperatureMinTime);
        dest.writeFloat(temperatureMax);
        dest.writeLong(temperatureMaxTime);
        dest.writeFloat(apparentTemperatureMin);
        dest.writeLong(apparentTemperatureMinTime);
        dest.writeFloat(apparentTemperatureMax);
        dest.writeLong(apparentTemperatureMaxTime);
    }
    
    public static final Parcelable.Creator<WeatherObjectDaily> CREATOR = new Parcelable.Creator<WeatherObjectDaily>(){

		@Override
		public WeatherObjectDaily createFromParcel(Parcel source) {
			return new WeatherObjectDaily(source);
		}

		@Override
		public WeatherObjectDaily[] newArray(int size) {
			return new WeatherObjectDaily[size];
		}
		
	};
    
    protected WeatherObjectDaily(Parcel src){
    	super(src);
    	precipIntensityMax = src.readFloat();
    	precipIntensityMaxTime = src.readLong();
    	sunriseTime = src.readLong();
    	sunsetTime = src.readLong();
        moonPhase = src.readFloat();
        temperatureMin = src.readFloat();
        temperatureMinTime = src.readLong();
        temperatureMax = src.readFloat();
        temperatureMaxTime = src.readLong();
        apparentTemperatureMin = src.readFloat();
        apparentTemperatureMinTime = src.readLong();
        apparentTemperatureMax = src.readFloat();
        apparentTemperatureMaxTime = src.readLong();
    }
}
