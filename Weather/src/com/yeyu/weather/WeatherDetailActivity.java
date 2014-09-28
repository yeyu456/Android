package com.yeyu.weather;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import static com.yeyu.weather.WeatherConstant.*;
import com.yeyu.widget.PartlyFillCircle;

public class WeatherDetailActivity extends Activity {
	
	private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				onDown(v);
				return true;
			}
			if(event.getAction() == MotionEvent.ACTION_UP){
				onUp(v);
			}
			return false;
		}
		
		private void onDown(View v){
			animate(v, R.anim.weather_detail_button_down);
			int newTag = ((int) v.getTag()) + 1;
			if(type.equals(TYPE_WEATHER_DAILY)&&newTag > TYPE_WEATHER_DETAIL_SUN_MOON){
				newTag = 1;
			}
			if(type.equals(TYPE_WEATHER_HOURLY)&&newTag > TYPE_WEATHER_DETAIL_CLOUD){
				newTag = 1;
			}
			v.setTag(newTag);
			setData(newTag);
			((PartlyFillCircle) v).setColorFill(typeColorMap.get(newTag));
		}
		
		private void onUp(View v){
			animate(v, R.anim.weather_detail_button_up);
		}
	};
	
	private ArrayList<WeatherObject> mData;
	private String type;
	
	@Override
	protected void onCreate(Bundle state){
		super.onCreate(state);
		Bundle bundle = this.getIntent().getExtras();
		type = bundle.getString("type");
		mData = bundle.getParcelableArrayList("data");
		if(type.equals(TYPE_WEATHER_DAILY)){
			setContentView(R.layout.activity_detail);
		}
		if(type.equals(TYPE_WEATHER_HOURLY)){
			setContentView(R.layout.activity_detail);
		}
		setData(TYPE_WEATHER_DETAIL_TEM);
		View director = findViewById(R.id.director);
		director.setTag(TYPE_WEATHER_DETAIL_TEM);
		director.setOnTouchListener(mTouchListener);
	}
	
	private void setData(int weatherType){
		PartlyFillCircle circle = (PartlyFillCircle) findViewById(R.id.director);
		ListView lv = (ListView) findViewById(R.id.listview);
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		ArrayList<Spannable> texts = new ArrayList<Spannable>();
		int yAixsInter = 0;
		String sign = "";
		
		String text = "";
		int len = 0;
		
		switch(weatherType){
			case TYPE_WEATHER_DETAIL_TEM : {
				for(WeatherObject obj:mData){
					if(type.equals(TYPE_WEATHER_DAILY)){
						numbers.add((int)((WeatherObjectDaily) obj).temperatureMin);
						text = "\t" + getResources().getStringArray(R.array.time_week)[Tool.toWeek(obj.time)]+ "\n";
						len = text.length();
						text += getDailyTemperatureText((WeatherObjectDaily) obj);
					} else if(type.equals(TYPE_WEATHER_HOURLY)){
						numbers.add((int)((WeatherObjectHourly) obj).temperature);
						text = "\t" + Tool.toDate(obj.time)+ "\t";
						len = text.length();
						text += getHourlyTemperatureText((WeatherObjectHourly) obj);
					}
					texts.add(getSpan(text, len));
				}
				yAixsInter = 3;
				sign = TEMPERATURE_SIGN;
				circle.setText("温");
				break;
			}
			case TYPE_WEATHER_DETAIL_WATER : {
				for(WeatherObject obj:mData){
					if(type.equals(TYPE_WEATHER_DAILY)){
						numbers.add((int)((WeatherObjectDaily) obj).precipIntensityMax);
						text = "\t" + getResources().getStringArray(R.array.time_week)[Tool.toWeek(obj.time)]+ "\n";
						len = text.length();
						text += getDailyWaterText((WeatherObjectDaily) obj);
					} else if(type.equals(TYPE_WEATHER_HOURLY)){
						numbers.add((int)((WeatherObjectHourly) obj).precipIntensity);
						text = "\t" + Tool.toDate(obj.time)+ "\t";
						len = text.length();
						text += getHourlyWaterText((WeatherObjectHourly) obj);
					}
					texts.add(getSpan(text, len));
				}
				yAixsInter = 3;
				sign = WATER_SIGN;
				circle.setText("水");
				break;
			}
			case TYPE_WEATHER_DETAIL_WIND : {
				for(WeatherObject obj:mData){
					numbers.add((int)(obj.windSpeed));
				}
				yAixsInter = 3;
				sign = WIND_SIGN;
				circle.setText("风");
				break;
			}
			case TYPE_WEATHER_DETAIL_CLOUD : {
				for(WeatherObject obj:mData){
					numbers.add((int)(obj.cloudCover * 100));
				}
				yAixsInter = 2;
				sign = CLOUD_SIGN;
				circle.setText("云");
				break;
			}
			case TYPE_WEATHER_DETAIL_SUN_MOON : {
				if(type.equals(TYPE_WEATHER_DAILY)){
					for(WeatherObject obj:mData){
					numbers.add((int)(((WeatherObjectDaily) obj).moonPhase * 100));
					}
				}
				yAixsInter = 3;
				sign = SUN_MOON_SIGN;
				circle.setText("日月");
				break;
			}
		}
		if(texts.size()>0){
			ArrayAdapter<Spannable> mAdapter = new ArrayAdapter<Spannable>(WeatherDetailActivity.this,
												 	 			     R.layout.activity_detail_card_view,
												 	 			     R.id.data,
												 	 			     texts);
			lv.setAdapter(mAdapter);
		}
		setGraphicFragment(numbers, yAixsInter, sign);
	}
	
	private void setGraphicFragment(ArrayList<Integer> numbers, int yAixsInter, String sign){
		WeatherDetailFragment wf = WeatherDetailFragment.newInstance(numbers, yAixsInter, sign);
		getFragmentManager().beginTransaction().replace(R.id.table, wf).commit();
	}
	
	private Spannable getSpan(String text, int len){
		Spannable wordtoSpan = new SpannableString(text);
		wordtoSpan.setSpan(new RelativeSizeSpan(1.0f), 0, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		wordtoSpan.setSpan(new RelativeSizeSpan(0.7f), len, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return wordtoSpan;
	}
	
	private String getDailyTemperatureText(WeatherObjectDaily dayobj){
		String text = "";
		text += "\t最低温度:\t" + dayobj.temperatureMin + TEMPERATURE_SIGN + "\t" + 
				"[" + Tool.toDate(dayobj.temperatureMinTime) + "]\t\t";
		text += "\t体表最低:\t" + dayobj.apparentTemperatureMin + TEMPERATURE_SIGN + "\t" +
				"[" + Tool.toDate(dayobj.apparentTemperatureMinTime) + "]\n";
		
		text += "\t最高温度:\t" + dayobj.temperatureMax + TEMPERATURE_SIGN + "\t" +
				"[" + Tool.toDate(dayobj.temperatureMaxTime) + "]\t\t";
		text += "\t体表最高:\t" +  dayobj.apparentTemperatureMax + TEMPERATURE_SIGN + "\t" +
				"[" + Tool.toDate(dayobj.apparentTemperatureMaxTime) + "]";
		return text;
	}
	
	private String getHourlyTemperatureText(WeatherObjectHourly hourobj){
		String text = "";
		text += "\t温度:\t" + hourobj.temperature + TEMPERATURE_SIGN + "\t\t";
		text += "\t体表:\t" + hourobj.apparentTemperature + TEMPERATURE_SIGN;
		return text;
	}
	
	private String getDailyWaterText(WeatherObjectDaily dayobj){
		String text = "";
		text += "\t平均降雨量:\t" + dayobj.precipIntensity + WATER_SIGN + "\t\t" +
				"\t降雨概率:\t" + (int)(dayobj.precipProbability * 100) + "%\n";
		text += "\t最大降雨量:\t" + dayobj.precipIntensityMax + WATER_SIGN + "\t" +
				"[" + Tool.toDate(dayobj.precipIntensityMaxTime) + "]\t\t";
		return text;
	}
	
	private String getHourlyWaterText(WeatherObjectHourly hourobj){
		String text = "";
		text += "\t降雨量:\t" + hourobj.precipIntensity + WATER_SIGN + "\t\t" +
				"\t降雨概率:\t" + (int)(hourobj.precipProbability * 100) + "%";
		return text;
	}
	
	private void animate(View v, int animid){
		Animation sa = AnimationUtils.loadAnimation(WeatherDetailActivity.this, animid); 
		v.startAnimation(sa);
	}
	
}
