package com.yeyu.weather;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationObject implements Parcelable  {
	public long time;
	public double latitude;
	public double longitude;
	public String address;
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(time);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeString(address);
	}
	
	public static final Parcelable.Creator<LocationObject> CREATOR = new Parcelable.Creator<LocationObject>(){

		@Override
		public LocationObject createFromParcel(Parcel source) {
			return new LocationObject(source);
		}

		@Override
		public LocationObject[] newArray(int size) {
			return new LocationObject[size];
		}
		
	};
	
	protected LocationObject(){
		
	}
	
	protected LocationObject(Parcel src){
		time = src.readLong();
		latitude = src.readDouble();
		longitude = src.readDouble();
		address = src.readString();
	}
}
