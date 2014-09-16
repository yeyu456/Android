package com.yeyu.weather;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.yeyu.widget.PartlyFillCircle;

public class WeatherActivityDetail extends Activity {
	
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
		setTemperature(tem);
	}
	
	private void setHourData(WeatherObjectHourly obj){
		float tem = obj.temperature;
		float wind = obj.windSpeed;
		setTemperature(tem);
		setWind(wind);
	}
	
	private void setTemperature(float tem){
		PartlyFillCircle v = (PartlyFillCircle) this.findViewById(R.id.temperature);
		if(tem>0){
			int percentage = (int)(tem / WeatherConstant.TEMPERATURE_POSITIVE_MAX * 100);
			v.setColorPercentageAndMax(percentage, WeatherConstant.TEMPERATURE_POSITIVE_MAX);
		} else {
			int percentage = (int)(tem / WeatherConstant.TEMPERATURE_NEGATIVE_MAX * 100);
			v.setColorPercentageAndMax(percentage, WeatherConstant.TEMPERATURE_NEGATIVE_MAX);
			v.setColorFill(getResources().getColor(R.color.temperature_negative));
		}
		v.setText(((int)tem) + WeatherConstant.TEMPERATURE_CHAR);
	}
	
	private void setWind(float windspeed){
		PartlyFillCircle v = (PartlyFillCircle) this.findViewById(R.id.wind);
	}
	
	
}
