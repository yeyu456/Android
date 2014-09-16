package com.yeyu.weather;

import android.os.Parcel;
import android.os.Parcelable;

public class WeatherObject implements Parcelable {
	public String summary;
	public String icon;
	public long time;
	public float precipIntensity;
	public float precipProbability;
	public String precipType;
	public float humidity;
	public float windSpeed;
	public int windBearing;
	public float visibility;
	public float cloudCover;
	public float ozone;
	public double latitude;
	public double longitude;
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(summary);
		dest.writeString(icon);
		dest.writeLong(time);
		dest.writeFloat(precipIntensity);
		dest.writeFloat(precipProbability);
		dest.writeString(precipType);
		dest.writeFloat(humidity);
		dest.writeFloat(windSpeed);
		dest.writeInt(windBearing);
		dest.writeFloat(visibility);
		dest.writeFloat(cloudCover);
		dest.writeFloat(ozone);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
	}
	
	public static final Parcelable.Creator<WeatherObject> CREATOR = new Parcelable.Creator<WeatherObject>(){

		@Override
		public WeatherObject createFromParcel(Parcel source) {
			return new WeatherObject(source);
		}

		@Override
		public WeatherObject[] newArray(int size) {
			return new WeatherObject[size];
		}
		
	};
	
	protected WeatherObject(){}
	
	protected WeatherObject(Parcel src){
		summary = src.readString();
		icon = src.readString();
		time = src.readLong();
		precipIntensity = src.readFloat();
		precipProbability = src.readFloat();
		precipType = src.readString();
		humidity = src.readFloat();
		windSpeed = src.readFloat();
		windBearing = src.readInt();
		visibility = src.readFloat();
		cloudCover = src.readFloat();
		ozone = src.readFloat();
		latitude = src.readDouble();
		longitude = src.readDouble();
	}
}
