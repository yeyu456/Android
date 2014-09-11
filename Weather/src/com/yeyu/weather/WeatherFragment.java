package com.yeyu.weather;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherFragment extends Fragment {
	
	public static String Celsius = "\u2103";
	public static HashMap<String, String> iconBackgroundMap = new HashMap<String, String>();
	static {
		iconBackgroundMap.put("clear-day", "#FF5722");
		iconBackgroundMap.put("clear-night", "#FFEB3B");
		iconBackgroundMap.put("rain", "#607D8B");
		iconBackgroundMap.put("snow", "#FAFAFA");
		iconBackgroundMap.put("sleet", "#CFD8DC");
		iconBackgroundMap.put("wind", "#259B24");
		iconBackgroundMap.put("fog", "#795548");
		iconBackgroundMap.put("cloudy", "#009688");
		iconBackgroundMap.put("partly-cloudy-day", "#03A9F4");
		iconBackgroundMap.put("partly-cloudy-night", "#5677FC");
	}
	public static HashMap<String, String> iconImageMap = new HashMap<String, String>();
	static {
		iconImageMap.put("clear-day", "sunny");
		iconImageMap.put("clear-night", "moon");
		iconImageMap.put("rain", "rainy");
		iconImageMap.put("snow", "snowy");
		iconImageMap.put("sleet", "rany_snowy");
		iconImageMap.put("wind", "windy");
		iconImageMap.put("fog", "fog");
		iconImageMap.put("cloudy", "cloudy");
		iconImageMap.put("partly-cloudy-day", "partlycloudy");
		iconImageMap.put("partly-cloudy-night", "partlycloudynight");
	}
	
	
	private View mView;
	private String mType;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
							 ViewGroup container,
							 Bundle state){
		mView = inflater.inflate(R.layout.fragment_main, container, false);
		Log.e("add", "" + this.isAdded());
		mType = this.getTag();
		ArrayList<WeatherObject> data = this.getArguments().getParcelableArrayList("data");
		Log.e("call",mType);
		if(data!=null){
			setData(data);
		}
		return mView;
	}
	
	public void setData(ArrayList<WeatherObject> data){
		if(data.size()<=0){
			return;
		}
		for(WeatherObject obj:data){
			int id = getResourceId("cardview_" + String.valueOf(data.indexOf(obj) + 1), "id");
			if(id==0){
				//do something
				Log.e("error", "cannot find id" + data.indexOf(obj));
			} else {
				CardView cardView = (CardView) mView.findViewById(id);
				setWeather(cardView, obj);
			}
		}
	}
	
	private int getResourceId(String name, String type){
		return this.getActivity().getResources().getIdentifier(name, type, "com.yeyu.weather");
	}
	
	private void setWeather(CardView v, WeatherObject obj){
		if(obj instanceof WeatherObject){
			if(mType == MainActivity.TYPE_WEATHER_DAILY){
				WeatherDailyObject dayobj = (WeatherDailyObject) obj;
				float[] tem = {dayobj.temperatureMin, dayobj.temperatureMax};
				v.setTag(dayobj);
				setCelsius(v, tem);
				setCardViewBackgroundAndIcon(v, dayobj.icon);
			} else {
				if(mType == MainActivity.TYPE_WEATHER_HOURLY){
					WeatherHourlyObject hourobj = (WeatherHourlyObject) obj;
					float[] tem = {hourobj.temperature};
					v.setTag(hourobj);
					setCelsius(v, tem);
					setCardViewBackgroundAndIcon(v, hourobj.icon);
				}
			}
		}
	}
	
	private void setCardViewBackgroundAndIcon(CardView v, String icon){
		v.setBackgroundDrawable(new ColorDrawable(Color.parseColor(iconBackgroundMap.get(icon))));
		ImageView iv = (ImageView) v.findViewById(R.id.climate_icon);
		String image = iconImageMap.get(icon);
		if(image.equals("rainy")||image.equals("snowy")){
			//analyse which image to be used
		}
		int id = getResourceId(image, "drawable");
		if(id!=0){
			iv.setBackgroundResource(id);
		} else {
			Log.e("error", "cannot find image id " + image);
		}
	}
	
	private void setCelsius(CardView v, float[] celsius) throws IllegalArgumentException {
		TextView tv = (TextView) v.findViewById(R.id.climate_celsius);
		switch(celsius.length){
			case 1: {
				tv.setText(String.valueOf((int)celsius[0]) + Celsius);
				break;
			}
			case 2: {
				tv.setText(String.valueOf((int)celsius[0]) + Celsius + "/" + String.valueOf((int)celsius[1]) + Celsius);
				break;
			}
			default : {
				tv.setText("-" + Celsius);
			}
		}
	}
}