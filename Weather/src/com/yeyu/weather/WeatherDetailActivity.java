package com.yeyu.weather;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;

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
			if(newTag > 6){
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
		ArrayList<Integer> numbers = new ArrayList<Integer>();
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
				}
				yAixsInter = 3;
				sign = TEMPERATURE_SIGN;
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
			}
		}
		setGraphicFragment(numbers, yAixsInter, sign);
	}
	
	private void setGraphicFragment(ArrayList<Integer> numbers, int yAixsInter, String sign){
		WeatherDetailFragment wf = WeatherDetailFragment.newInstance(numbers, yAixsInter, sign);
		getFragmentManager().beginTransaction().replace(R.id.table, wf).commit();
	}
	
	private void animate(View v, int animid){
		Animation sa = AnimationUtils.loadAnimation(WeatherDetailActivity.this, animid); 
		v.startAnimation(sa);
	}
	
}
