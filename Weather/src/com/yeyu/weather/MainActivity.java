package com.yeyu.weather;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;
import android.location.Location;

public class MainActivity extends Activity {
	
	private static final int REQUEST_LOCATION = 0;
	private static final int REQUEST_WEATHER = 1;
	public static final String RESULT_LOCATION = "location";
	public static final String RESULT_WEATHER = "weather";
	
	@Override
	protected void onCreate(Bundle state){
		super.onCreate(state);
		this.setContentView(R.layout.main);
		
		TextView degreedview = (TextView) this.findViewById(R.id.climate_celsius);
		String degree = "27" + " \u2103\n";
		String climate = "“ıÃÏ";
		Spannable wordtoSpan = new SpannableString(degree + climate); 
        wordtoSpan.setSpan(new RelativeSizeSpan(1.0f), 
                           0, 
                           degree.length(), 
                           Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        wordtoSpan.setSpan(new RelativeSizeSpan(0.5f), 
                           degree.length(), 
                           climate.length() + degree.length(), 
                           Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		degreedview.setText(wordtoSpan);
		
		TextView forcast1 = (TextView) this.findViewById(R.id.forcast_1).findViewById(R.id.forcast_celsius);
		TextView forcast2 = (TextView) this.findViewById(R.id.forcast_2).findViewById(R.id.forcast_celsius);
		TextView forcast3 = (TextView) this.findViewById(R.id.forcast_3).findViewById(R.id.forcast_celsius);
		forcast1.setText(wordtoSpan);
		forcast2.setText(wordtoSpan);
		forcast3.setText(wordtoSpan);
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		requestLocation();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
			case REQUEST_LOCATION : {
				if(resultCode == LocationService.RESULT_OK){
					Location point = data.getParcelableExtra(RESULT_LOCATION);
					updateLocation(point);
				}
				break;
			}
			case REQUEST_WEATHER : {
				if(resultCode == WeatherService.RESULT_OK){
					ArrayList<WeatherObject> weather = data.getParcelableArrayListExtra(RESULT_WEATHER);
					updateWeather(weather);
				}
				break;
			}
		}
	}
	
	private void updateLocation(Location point){
		System.out.println("point " + point.getLatitude() + " " + point.getLongitude());
		double latitude = point.getLatitude();
		double longitude = point.getLongitude();
		requestWeather(latitude, longitude);
	}
	
	private void updateWeather(ArrayList<WeatherObject> weather){
		for(WeatherObject obj:weather){
			System.out.println(obj.time);
		}
	}
	
	private void requestLocation(){
		Intent locationIntent = new Intent(MainActivity.this, LocationService.class);
		locationIntent.putExtra(LocationService.EXTRA_PENDING_RESULT, this.createPendingResult(REQUEST_LOCATION, new Intent(), 0));
		this.startService(locationIntent);
	}
	
	private void requestWeather(double latitude, double longitude){
		Intent weatherIntent = new Intent(MainActivity.this, WeatherService.class);
		weatherIntent.putExtra(WeatherService.EXTRA_PENDING_RESULT, this.createPendingResult(REQUEST_WEATHER, new Intent(), 0));
		weatherIntent.putExtra(WeatherService.EXTRA_LOCATION_LATITUDE, latitude);
		weatherIntent.putExtra(WeatherService.EXTRA_LOCATION_LONGITUDE, longitude);
		this.startService(weatherIntent);
	}
}
