package com.yeyu.weather;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

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
		}
		
		private void onUp(View v){
			animate(v, R.anim.weather_detail_button_up);
			switch(v.getId()){
				case R.id.temperature : {
					break;
				}
				case R.id.wind : {
					break;
				}
				case R.id.water : {
					break;
				}
				case R.id.cloud : {
					break;
				}
			}
		}
	};
	
	private View.OnClickListener mClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
		}
	};
	
	@Override
	protected void onCreate(Bundle state){
		super.onCreate(state);
		Bundle bundle = this.getIntent().getExtras();
		String type = bundle.getString("type");
		if(type.equals(MainActivity.TYPE_WEATHER_DAILY)){
			WeatherObjectDaily dayobj = bundle.getParcelable("data");
			setContentView(R.layout.activity_detail);
			setDayData(dayobj);
		}
		if(type.equals(MainActivity.TYPE_WEATHER_HOURLY)){
			WeatherObjectHourly hourobj = bundle.getParcelable("data");
			setContentView(R.layout.activity_detail);
			setHourData(hourobj);
		}
		Log.e("detail", type);
	}
	
	private void setDayData(WeatherObjectDaily obj){
		float tem = obj.temperatureMin;
		float wind = obj.windSpeed;
		float rain = obj.precipIntensityMax;
		float cloud = obj.cloudCover;
		setTemperature(tem);
		setWind(wind);
		setWater(rain);
		setCloud(cloud);
	}
	
	private void setHourData(WeatherObjectHourly obj){
		float tem = obj.temperature;
		float wind = obj.windSpeed;
		float rain = obj.precipIntensity;
		float cloud = obj.cloudCover;
		setTemperature(tem);
		setWind(wind);
		setWater(rain);
		setCloud(cloud);
	}
	
	private void setTemperature(float tem){
		Log.e("tem", ""+tem);
		PartlyFillCircle v = (PartlyFillCircle) this.findViewById(R.id.temperature);
		v.setOnTouchListener(mTouchListener);
		if(tem>0){
			v.setColorPercentageAndMax(tem, TEMPERATURE_POSITIVE_MAX);
		} else {
			v.setColorPercentageAndMax(tem, TEMPERATURE_NEGATIVE_MAX);
			v.setColorFill(getResources().getColor(R.color.temperature_negative));
		}
		v.setText(((int)tem) + TEMPERATURE_CHAR);
	}
	
	private void setWind(float windSpeed){
		Log.e("wind", ""+windSpeed);
		PartlyFillCircle v = (PartlyFillCircle) this.findViewById(R.id.wind);
		v.setOnTouchListener(mTouchListener);
		int windLevel;
		if(windSpeed>WIND_TORNADO_SPEED){
			windLevel = (int)Math.round(Math.pow(Math.pow(windSpeed, 2.0), 1.0/3.0));
			v.setColorPercentageAndMax(windLevel, WIND_LEVEL_TORNADO_MAX);
			v.setColorFill(getResources().getColor(R.color.tornado));
		} else {
			windLevel = (int)Math.round((Math.pow(Math.pow(windSpeed, 2.0), 1.0/3.0) / WIND_CALCULATE_CONSTANT));
			v.setColorPercentageAndMax(windLevel, WIND_LEVEL_MAX);
		}
		Log.e("wind", ""+windLevel);
		v.setText(windLevel + "¼¶");
	}
	
	private void setWater(float rain){
		Log.e("water", ""+rain);
		PartlyFillCircle v = (PartlyFillCircle) this.findViewById(R.id.water);
		v.setOnTouchListener(mTouchListener);
		v.setColorPercentageAndMax(rain, PRECIPITATION_RATE_MAX);
		v.setText((float)Math.round(rain*100)/100.0 +"mm");
	}
	
	private void setCloud(float cloud){
		Log.e("cloud", ""+cloud);
		PartlyFillCircle v = (PartlyFillCircle) this.findViewById(R.id.cloud);
		v.setOnTouchListener(mTouchListener);
		v.setColorPercentageAndMax(cloud, CLOUD_COVER_PERCENTAGE_MAX);
		v.setText((int)(cloud * 100) + "%");
	}
	
	private void animate(View v, int animid){
		Animation sa = AnimationUtils.loadAnimation(WeatherDetailActivity.this, animid); 
		v.startAnimation(sa);
	}
}
