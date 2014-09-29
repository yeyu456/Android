package com.yeyu.weather;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static com.yeyu.weather.WeatherConstant.*;
import com.yeyu.widget.cardview.CardView;

public class WeatherFragment extends Fragment {
	
	private View mView;
	private String mType;
	private String cur;
	private String[] time_day;
	private String[] time_week;
	private ArrayList<WeatherObject> mData;
	private View.OnClickListener moreListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			/*
			WeatherObject obj = (WeatherObject) v.getTag();
			*/
			if(mData==null){
				Log.e("click", "empty tag");
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putString("type", mType);
			//bundle.putParcelable("data", obj);
			bundle.putParcelableArrayList("data", mData);
			Intent intent = new Intent(WeatherFragment.this.getActivity(), WeatherDetailActivity.class);
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
		mData = data;
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
		return this.getActivity().getResources().getIdentifier(name, type, WeatherFragment.this.getActivity().getPackageName());
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
			if(mType == TYPE_WEATHER_DAILY){
				WeatherObjectDaily dayobj = (WeatherObjectDaily) obj;
				float[] tem = {dayobj.temperatureMin, dayobj.temperatureMax};
				View more = v.findViewById(R.id.more);
				more.setTag(dayobj);
				more.setOnClickListener(moreListener);
				setCelsius(v, tem);
				setCardViewBackgroundAndIcon(v, getImage(dayobj.icon, dayobj.precipIntensityMax, true));
			} else {
				if(mType == TYPE_WEATHER_HOURLY){
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
		v.setBackgroundColor(Color.parseColor(color));
		v.setRadius(4f);
		ImageView iv = (ImageView) v.findViewById(R.id.climate_icon);
		int id = getResourceId(image, "drawable");
		if(id!=0){
			iv.setBackgroundResource(id);
		} else {
			Log.e("error", "cannot find image id " + image);
		}
	}
	
	private void setTime(CardView v, long time, int index){
		if(mType==TYPE_WEATHER_HOURLY){
			if(index==0){
				setTimeString(v, cur + "\n");
			} else {
				setTimeString(v, Tool.toDate(time) + "\n");
			}
		} else {
			if(mType==TYPE_WEATHER_DAILY){
				if(index<time_day.length){
					setTimeString(v, time_day[index] + "\n");
				} else {
					setTimeString(v, time_week[Tool.toWeek(time)] + "\n");
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
	
	private void setCelsius(CardView v, float[] celsius) throws IllegalArgumentException {
		TextView tv = (TextView) v.findViewById(R.id.climate_celsius);
		switch(celsius.length){
			case 1: {
				tv.append(String.valueOf((int)celsius[0]) + TEMPERATURE_SIGN);
				break;
			}
			case 2: {
				tv.append(String.valueOf((int)celsius[0]) + TEMPERATURE_SIGN + "/" + String.valueOf((int)celsius[1]) + TEMPERATURE_SIGN);
				break;
			}
			default : {
				tv.append("-" + TEMPERATURE_SIGN);
			}
		}
	}
}