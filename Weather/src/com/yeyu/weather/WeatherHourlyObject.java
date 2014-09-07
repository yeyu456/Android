package com.yeyu.weather;

import android.os.Parcel;
import android.os.Parcelable;

public class WeatherHourlyObject extends WeatherObject implements Parcelable {
	
	public float temperature;
	public float apparentTemperature;
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeFloat(temperature);
		dest.writeFloat(apparentTemperature);
	}
	
	public static final Parcelable.Creator<WeatherHourlyObject> CREATOR = new Parcelable.Creator<WeatherHourlyObject>(){

		@Override
		public WeatherHourlyObject createFromParcel(Parcel source) {
			return new WeatherHourlyObject(source);
		}

		@Override
		public WeatherHourlyObject[] newArray(int size) {
			return new WeatherHourlyObject[size];
		}
		
	};
	
	protected WeatherHourlyObject(Parcel src){
		super(src);
		temperature = src.readFloat();
		apparentTemperature = src.readFloat();
	}
}
