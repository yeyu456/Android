package com.yeyu.weather;

import java.util.ArrayList;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;

public class WeatherService extends IntentService {

	public static final String EXTRA_PENDING_RESULT = "request";
	public static final String EXTRA_LOCATION_LATITUDE = "latitude";
	public static final String EXTRA_LOCATION_LONGITUDE = "longitude";
	public static final int RESULT_OK = 0;
	public static final int RESULT_FAIL = 1;
	public static final int RESULT_BAD_LOCATION = 2;


	public WeatherService() {
		super("WeatherService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		PendingIntent pendingIntent = intent.getParcelableExtra(EXTRA_PENDING_RESULT);
		double latitude = intent.getDoubleExtra(EXTRA_LOCATION_LATITUDE, 0.0);
		double longitude = intent.getDoubleExtra(EXTRA_LOCATION_LONGITUDE, 0.0);

		try {
			if(latitude!=0.0 && longitude!=0.0){
				ArrayList<WeatherObject> result = new PolicyGetWeather(this).getWeather(latitude, longitude);
				if(result==null){
					pendingIntent.send(WeatherService.this, RESULT_FAIL, null);
				} else {
					for(WeatherObject obj:result){
						System.out.println("2 " + obj.summary);
					}
					Intent resultIntent = new Intent();
					resultIntent.putParcelableArrayListExtra(MainActivity.RESULT_WEATHER, result);
					pendingIntent.send(WeatherService.this, RESULT_OK, resultIntent);
				}
			} else {
				pendingIntent.send(WeatherService.this, RESULT_BAD_LOCATION, null);
			}
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}

}
