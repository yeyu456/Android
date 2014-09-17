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
		if(tem>0){
			v.setColorPercentageAndMax(tem, WeatherConstant.TEMPERATURE_POSITIVE_MAX);
		} else {
			v.setColorPercentageAndMax(tem, WeatherConstant.TEMPERATURE_NEGATIVE_MAX);
			v.setColorFill(getResources().getColor(R.color.temperature_negative));
		}
		v.setText(((int)tem) + WeatherConstant.TEMPERATURE_CHAR);
	}
	
	private void setWind(float windSpeed){
		Log.e("wind", ""+windSpeed);
		PartlyFillCircle v = (PartlyFillCircle) this.findViewById(R.id.wind);
		int windLevel;
		if(windSpeed>WeatherConstant.WIND_TORNADO_SPEED){
			windLevel = (int)Math.round(Math.pow(Math.pow(windSpeed, 2.0), 1.0/3.0));
			v.setColorPercentageAndMax(windLevel, WeatherConstant.WIND_LEVEL_TORNADO_MAX);
			v.setColorFill(getResources().getColor(R.color.tornado));
		} else {
			windLevel = (int)Math.round((Math.pow(Math.pow(windSpeed, 2.0), 1.0/3.0) / WeatherConstant.WIND_CALCULATE_CONSTANT));
			v.setColorPercentageAndMax(windLevel, WeatherConstant.WIND_LEVEL_MAX);
		}
		Log.e("wind", ""+windLevel);
		v.setText(windLevel + "¼¶");
	}
	
	private void setWater(float rain){
		Log.e("water", ""+rain);
		PartlyFillCircle v = (PartlyFillCircle) this.findViewById(R.id.water);
		v.setColorPercentageAndMax(rain, WeatherConstant.PRECIPITATION_RATE_MAX);
		v.setText((float)Math.round(rain*100)/100.0 +"mm");
	}
	
	private void setCloud(float cloud){
		Log.e("cloud", ""+cloud);
		PartlyFillCircle v = (PartlyFillCircle) this.findViewById(R.id.cloud);
		v.setColorPercentageAndMax(cloud, WeatherConstant.CLOUD_COVER_PERCENTAGE_MAX);
		v.setText((int)(cloud * 100) + "%");
	}
}
