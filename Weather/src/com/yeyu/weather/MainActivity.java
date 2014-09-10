package com.yeyu.weather;

import java.util.ArrayList;

import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;
import android.location.Location;
import android.support.v7.widget.CardView;

public class MainActivity extends Activity {
	
	private static final int REQUEST_LOCATION = 0;
	private static final int REQUEST_WEATHER = 1;
	public static final String RESULT_LOCATION = "location";
	public static final String RESULT_WEATHER = "weather";
	public static final String TYPE_WEATHER_HOURLY = "hourly";
	public static final String TYPE_WEATHER_DAILY = "daily";
	
	private ArrayList<WeatherObject> mHourlyData = new ArrayList<WeatherObject>();
	private ArrayList<WeatherObject> mDailyData = new ArrayList<WeatherObject>();
	
	@Override
	protected void onCreate(Bundle state){
		super.onCreate(state);
		/*
		this.setContentView(R.layout.main);
		
		TextView degreedview = (TextView) this.findViewById(R.id.climate_celsius);
		String degree = "27" + " \u2103\n";
		String climate = "阴天";
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
		*/
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		Tab hourTab = actionBar.newTab()
							   .setText("小时")
							   .setTabListener(new TabListener<WeatherFragment>(this, TYPE_WEATHER_HOURLY, WeatherFragment.class));
		actionBar.addTab(hourTab);
		Tab dayTab = actionBar.newTab()
							  .setText("天")
							  .setTabListener(new TabListener<WeatherFragment>(this, TYPE_WEATHER_DAILY, WeatherFragment.class));
		actionBar.addTab(dayTab);
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
					for(WeatherObject obj:weather){
						System.out.println("3 "+ obj.summary);
					}
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
		for(int n=0;n<PolicyGetWeather.MAX_COUNT_HOURLY_DATA + 1;n++){
			mHourlyData.add(weather.remove(0));
		}
		for(int n=0;n<PolicyGetWeather.MAX_COUNT_DAILY_DATA;n++){
			mDailyData.add(weather.remove(0));
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
	
	public class TabListener<T extends Fragment> implements ActionBar.TabListener {
	    private Fragment mFragment;
	    private final Activity mActivity;
	    private final String mTag;
	    private final Class<T> mClass;
	    
	    public TabListener(Activity activity, String tag, Class<T> clz) {
	        mActivity = activity;
	        mTag = tag;
	        mClass = clz;
	    }

	    /* The following are each of the ActionBar.TabListener callbacks */
	    @Override
	    public void onTabSelected(Tab tab, FragmentTransaction ft) {
	        // Check if the fragment is already initialized
	        if (mFragment == null) {
	            // If not, instantiate and add it to the activity
	        	Bundle bundle = new Bundle();
	        	bundle.putString("type", mTag);
	            mFragment = Fragment.instantiate(mActivity, mClass.getName(), bundle);
	            ft.add(android.R.id.content, mFragment, mTag);
	            if(mTag.equals("hourly")){
	            	((WeatherFragment) mFragment).setData(mHourlyData);
	            }
	            if(mTag.equals("daily")){
	            	((WeatherFragment) mFragment).setData(mDailyData);
	            }
	        } else {
	            // If it exists, simply attach it in order to show it
	            ft.attach(mFragment);
	        }
	    }
	    
	    @Override
	    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	        if (mFragment != null) {
	            // Detach the fragment, because another one is being attached
	            ft.detach(mFragment);
	        }
	    }

	    @Override
	    public void onTabReselected(Tab tab, FragmentTransaction ft) {
	        // User selected the already selected tab. Usually do nothing.
	    }
	}
}
