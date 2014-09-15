package com.yeyu.weather;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import android.app.Fragment;
import android.content.Intent;
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
	public static HashMap<String, String> imageColorMap = new HashMap<String, String>();
	static {
		imageColorMap.put("sunny", "#FF5722");
		imageColorMap.put("moon", "#FFEB3B");
		imageColorMap.put("rainy_s", "#607D8B");
		imageColorMap.put("rainy_m", "#455A64");
		imageColorMap.put("rainy_h", "#263238");
		imageColorMap.put("snowy", "#FAFAFA");
		imageColorMap.put("rainy_snowy", "#CFD8DC");
		imageColorMap.put("windy", "#259B24");
		imageColorMap.put("fog", "#795548");
		imageColorMap.put("cloudy", "#009688");
		imageColorMap.put("partlycloudy", "#03A9F4");
		imageColorMap.put("partlycloudynight", "#5677FC");
	}
	public static HashMap<String, String> iconImageMap = new HashMap<String, String>();
	static {
		iconImageMap.put("clear-day", "sunny");
		iconImageMap.put("clear-night", "moon");
		iconImageMap.put("rain", "rainy");
		iconImageMap.put("snow", "snowy");
		iconImageMap.put("sleet", "rainy_snowy");
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
	private View.OnClickListener moreListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			WeatherObject obj = (WeatherObject) v.getTag();
			if(obj==null){
				Log.e("click", "empty tag");
				return;
			}
			Log.e("click", "start");
			Bundle bundle = new Bundle();
			bundle.putString("type", mType);
			bundle.putParcelable("data", obj);
			Intent intent = new Intent(WeatherFragment.this.getActivity(), WeatherActivityDetail.class);
			intent.putExtras(bundle);
			WeatherFragment.this.getActivity().startActivity(intent);
		}
	};
	
	
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
	
	private String getImage(String icon, float precipIntensity, boolean isday){
		String image = iconImageMap.get(icon);
		if(image.equals("rainy")){
			image += "_" + getRainfall(precipIntensity);
		}
		if(isday && image.equals("partlycloudynight")){
			image = "partlycloudy";
		}
		return image;
	}
	
	private String getRainfall(float precipIntensity){
		if(precipIntensity < 2.5 && precipIntensity > 0){
			return "s";
		}
		if(precipIntensity > 2.5 && precipIntensity < 10.0){
			return "m";
		}
		if(precipIntensity > 10.0){
			return "h";
		}
		return "";
	}
	
	private void setWeather(CardView v, WeatherObject obj){
		if(obj instanceof WeatherObject){
			if(mType == MainActivity.TYPE_WEATHER_DAILY){
				WeatherObjectDaily dayobj = (WeatherObjectDaily) obj;
				float[] tem = {dayobj.temperatureMin, dayobj.temperatureMax};
				View more = v.findViewById(R.id.more);
				more.setTag(dayobj);
				more.setOnClickListener(moreListener);
				setCelsius(v, tem);
				setCardViewBackgroundAndIcon(v, getImage(dayobj.icon, dayobj.precipIntensityMax, true));
			} else {
				if(mType == MainActivity.TYPE_WEATHER_HOURLY){
					WeatherObjectHourly hourobj = (WeatherObjectHourly) obj;
					float[] tem = {hourobj.temperature};
					View more = v.findViewById(R.id.more);
					more.setTag(hourobj);
					more.setOnClickListener(moreListener);
					setCelsius(v, tem);
					setCardViewBackgroundAndIcon(v, getImage(hourobj.icon, hourobj.precipIntensity, false));
				}
			}
		}
	}
	
	private void setCardViewBackgroundAndIcon(CardView v, String image){
		String color = imageColorMap.get(image);
		v.setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
		ImageView iv = (ImageView) v.findViewById(R.id.climate_icon);
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
		i--;
		i = i<0?i+7:i;
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