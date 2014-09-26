package com.yeyu.weather;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
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
	
	private void test(){
	}
	
	private void setData(int weatherType){
		PartlyFillCircle circle = (PartlyFillCircle) findViewById(R.id.director);
		ListView lv = (ListView) findViewById(R.id.listview);
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		ArrayList<String> texts = new ArrayList<String>();
		int yAixsInter = 0;
		String sign = "";
		
		switch(weatherType){
			case TYPE_WEATHER_DETAIL_TEM : {
				for(WeatherObject obj:mData){
					if(type.equals(TYPE_WEATHER_DAILY)){
						numbers.add((int)((WeatherObjectDaily) obj).temperatureMin);
					} 
					if(type.equals(TYPE_WEATHER_HOURLY)){
						numbers.add((int)((WeatherObjectHourly) obj).temperature);
					}
					texts.add(generateTemperatureText(obj));
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
					}
					if(type.equals(TYPE_WEATHER_HOURLY)){
						numbers.add((int)((WeatherObjectHourly) obj).precipIntensity);
					}
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
			ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(WeatherDetailActivity.this,
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
	
	private String generateTemperatureText(WeatherObject obj){
		String text = "";
		if(type.equals(TYPE_WEATHER_DAILY)){
			WeatherObjectDaily dayobj = (WeatherObjectDaily) obj;
			text += getResources().getStringArray(R.array.time_week)[Tool.toWeek(dayobj.time)]+ "\n";
			text += "最低温度: " + Tool.toDate(dayobj.temperatureMinTime) + " " + 
					dayobj.temperatureMin + TEMPERATURE_SIGN + "\n";
			text += "体表最低: " + Tool.toDate(dayobj.apparentTemperatureMinTime) + " " + 
					dayobj.apparentTemperatureMin + TEMPERATURE_SIGN + "\n";
			text += "最高温度: " + Tool.toDate(dayobj.temperatureMaxTime) + " " + 
					dayobj.temperatureMax + TEMPERATURE_SIGN + "\n";
			text += "体表最高: " + Tool.toDate(dayobj.apparentTemperatureMaxTime) + " " + 
					dayobj.apparentTemperatureMax + TEMPERATURE_SIGN + "\n";
		}
		if(type.equals(TYPE_WEATHER_HOURLY)){
			WeatherObjectHourly hourobj = (WeatherObjectHourly) obj;
			text += Tool.toDate(hourobj.time)+ "\n";
			text += "温度: " + hourobj.temperature + TEMPERATURE_SIGN + "\n";
			text += "体表: " + hourobj.apparentTemperature + TEMPERATURE_SIGN + "\n";
		}
		return text;
	}
	
	private void animate(View v, int animid){
		Animation sa = AnimationUtils.loadAnimation(WeatherDetailActivity.this, animid); 
		v.startAnimation(sa);
	}
	
}
