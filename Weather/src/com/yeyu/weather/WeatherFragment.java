package com.yeyu.weather;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
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
	private String cur;
	private String[] time_day;
	private String[] time_week;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
							 ViewGroup container,
							 Bundle state){
		mView = inflater.inflate(R.layout.fragment_main, container, false);
		mType = this.getTag();
		cur = this.getResources().getString(R.string.time_current);
		time_day = this.getResources().getStringArray(R.array.time_day);
		time_week = this.getResources().getStringArray(R.array.time_week);
		ArrayList<WeatherObject> data = this.getArguments().getParcelableArrayList("data");
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
				setTime(cardView, obj.time, data.indexOf(obj));
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
	
	private void setTime(CardView v, long time, int index){
		if(mType==MainActivity.TYPE_WEATHER_HOURLY){
			if(index==0){
				setTimeString(v, cur + "\n");
			} else {
				setTimeString(v, toDate(time) + "\n");
			}
		} else {
			if(mType==MainActivity.TYPE_WEATHER_DAILY){
				if(index<time_day.length){
					setTimeString(v, time_day[index] + "\n");
				} else {
					setTimeString(v, toWeek(time) + "\n");
				}
			}
		}
	}
	
	private void setTimeString(CardView v, String s){
		TextView tv = (TextView) v.findViewById(R.id.climate_celsius);
		Spannable wordtoSpan = new SpannableString(s); 
		wordtoSpan.setSpan(new RelativeSizeSpan(0.5f), 
                		   0, 
                		   s.length(), 
                		   Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(wordtoSpan);
	}
	
	private String toDate(long time){
		Log.e("time", "" + time);
		Calendar t = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		t.setTimeInMillis(time * 1000);
		String text = String.valueOf(t.get(Calendar.HOUR_OF_DAY)) + ":";
		int min = t.get((Calendar.MINUTE));
		if(min<10){
			text += "0";
		}
		text += String.valueOf(min);
		return text;
	}
	
	private String toWeek(long time){
		Calendar t = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		t.setTimeInMillis(time * 1000);
		int i = t.get(Calendar.DAY_OF_WEEK)-1;
		Log.e("week", "" + i);
		return time_week[i];
	}
	
	private void setCelsius(CardView v, float[] celsius) throws IllegalArgumentException {
		TextView tv = (TextView) v.findViewById(R.id.climate_celsius);
		switch(celsius.length){
			case 1: {
				tv.append(String.valueOf((int)celsius[0]) + Celsius);
				break;
			}
			case 2: {
				tv.append(String.valueOf((int)celsius[0]) + Celsius + "/" + String.valueOf((int)celsius[1]) + Celsius);
				break;
			}
			default : {
				tv.append("-" + Celsius);
			}
		}
	}
}