package com.yeyu.weather;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class WeatherActivityDetail extends Activity {
	
	@Override
	protected void onCreate(Bundle state){
		super.onCreate(state);
		Bundle bundle = this.getIntent().getExtras();
		String type = bundle.getString("type");
		Log.e("detail", type);
	}
}
