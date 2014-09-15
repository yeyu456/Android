package com.yeyu.weather;

import android.os.Parcel;
import android.os.Parcelable;

public class WeatherObjectHourly extends WeatherObject implements Parcelable {
	
	public float temperature;
	public float apparentTemperature;
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeFloat(temperature);
		dest.writeFloat(apparentTemperature);
	}
	
	public static final Parcelable.Creator<WeatherObjectHourly> CREATOR = new Parcelable.Creator<WeatherObjectHourly>(){

		@Override
		public WeatherObjectHourly createFromParcel(Parcel source) {
			return new WeatherObjectHourly(source);
		}

		@Override
		public WeatherObjectHourly[] newArray(int size) {
			return new WeatherObjectHourly[size];
		}
		
	};
	
	protected WeatherObjectHourly(Parcel src){
		super(src);
		temperature = src.readFloat();
		apparentTemperature = src.readFloat();
	}
}
