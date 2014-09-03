package com.yeyu.weather;

import android.app.IntentService;
import android.content.Intent;

public class WeatherService extends IntentService {

	public static final String EXTRA_PENDING_RESULT = "request";
	public static final String EXTRA_LOCATION_LATITUDE = "latitude";
	public static final String EXTRA_LOCATION_LONGITUDE = "longitude";
	public static final int RESULT_OK = 0;
	public static final int RESULT_FAIL = 1;

	public WeatherService() {
		super("WeatherService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		

	}

}
