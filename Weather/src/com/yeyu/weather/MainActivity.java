package com.yeyu.weather;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import static com.yeyu.weather.WeatherConstant.*;

public class MainActivity extends Activity {
	
	private static final int REQUEST_LOCATION = 0;
	private static final int REQUEST_WEATHER = 1;
	public static final String RESULT_LOCATION = "location";
	public static final String RESULT_WEATHER = "weather";
	
	private ArrayList<WeatherObject> mHourlyData = new ArrayList<WeatherObject>();
	private ArrayList<WeatherObject> mDailyData = new ArrayList<WeatherObject>();
	private ProgressDialog mProgressDialog;
	protected String mAddress = "";
	
	
	@Override
	protected void onCreate(Bundle state){
		super.onCreate(state);
		this.setContentView(R.layout.activity_main);
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
		Tab hourTab = actionBar.newTab()
							   .setText("小时")
							   .setTabListener(new TabListener<WeatherFragment>(this, TYPE_WEATHER_HOURLY, WeatherFragment.class));
		actionBar.addTab(hourTab);
		Tab dayTab = actionBar.newTab()
							  .setText("天")
							  .setTabListener(new TabListener<WeatherFragment>(this, TYPE_WEATHER_DAILY, WeatherFragment.class));
		actionBar.addTab(dayTab);
		requestLocation();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
			case REQUEST_LOCATION : {
				if(resultCode == LocationService.RESULT_OK){
					LocationObject point = data.getParcelableExtra(RESULT_LOCATION);
					updateLocation(point);
				} else if(resultCode == LocationService.RESULT_FAIL){
					Toast.makeText(this, "获取地址失败", Toast.LENGTH_SHORT).show();
					SharedPreferences settings = getSharedPreferences(RESULT_LOCATION, 0);
					String location = settings.getString(RESULT_LOCATION, null);
					if(location!=null){
						Gson gson = new Gson();
						LocationObject loc = gson.fromJson(location, LocationObject.class);
						mAddress = loc.address;
						requestWeather(loc.latitude, loc.longitude);
					}
				}
				break;
			}
			case REQUEST_WEATHER : {
				if(resultCode == WeatherService.RESULT_OK){
					ArrayList<WeatherObject> weather = data.getParcelableArrayListExtra(RESULT_WEATHER);
					updateWeather(weather);
				} else if(resultCode == WeatherService.RESULT_FAIL){
					Toast.makeText(this, "获取天气数据失败", Toast.LENGTH_SHORT).show();
					SharedPreferences settings = getSharedPreferences(RESULT_WEATHER, 0);
					Gson gson = new Gson();
					String hourlydata = settings.getString(TYPE_WEATHER_HOURLY, null);
					String dailydata = settings.getString(TYPE_WEATHER_DAILY, null);
					if(hourlydata!=null){
						mHourlyData = gson.fromJson(hourlydata, new TypeToken<ArrayList<WeatherObjectHourly>>(){}.getType());
					}
					if(dailydata!=null){
						mDailyData = gson.fromJson(dailydata, new TypeToken<ArrayList<WeatherObjectDaily>>(){}.getType());
					}
					setWeatherData();
				}
				break;
			}
		}
	}
	
	private void updateLocation(LocationObject point){
		double latitude = point.latitude;
		double longitude = point.longitude;
		SharedPreferences settings = getSharedPreferences(RESULT_LOCATION, 0);
		SharedPreferences.Editor editor = settings.edit();
		Gson gson = new Gson();
		editor.putString(RESULT_LOCATION, gson.toJson(point));
		editor.apply();
		mAddress = point.address;
		requestWeather(latitude, longitude);
	}
	
	private void updateWeather(ArrayList<WeatherObject> weather){
		for(int n=0;n<PolicyGetWeather.MAX_COUNT_HOURLY_DATA + 1;n++){
			mHourlyData.add(weather.remove(0));
		}
		for(int n=0;n<PolicyGetWeather.MAX_COUNT_DAILY_DATA;n++){
			mDailyData.add(weather.remove(0));
		}
		
		SharedPreferences settings = getSharedPreferences(RESULT_WEATHER, 0);
		SharedPreferences.Editor editor = settings.edit();
		Gson gson = new Gson();
		if(mHourlyData.size()>0){
			editor.putString(TYPE_WEATHER_HOURLY, gson.toJson(mHourlyData));
		}
		if(mDailyData.size()>0){
			editor.putString(TYPE_WEATHER_DAILY, gson.toJson(mDailyData));
		}
		editor.apply();
		setWeatherData();
	}
	
	private void setWeatherData(){
		Fragment hf = this.getFragmentManager().findFragmentByTag(TYPE_WEATHER_HOURLY);
		if(hf!=null){
			((WeatherFragment) hf).setData(mHourlyData);
		}
		
		Fragment df = this.getFragmentManager().findFragmentByTag(TYPE_WEATHER_DAILY);
		if(df!=null){
			((WeatherFragment) df).setData(mDailyData);
		}
		dismissDialog();
	}
	
	private void requestLocation(){
		showDialog();
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
	
	private void showDialog(){
		dismissDialog();
		mProgressDialog = new ProgressDialog(MainActivity.this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setTitle("天气数据");
		mProgressDialog.setMessage("加载中...");
		mProgressDialog.show();
	}
	
	private void dismissDialog(){
		if(mProgressDialog!=null && mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
	}
	
	public class TabListener<T extends Fragment> implements ActionBar.TabListener {
	    private Fragment mFragment;
	    private final Activity mActivity;
	    private final String mTag;
	    private final Class<T> mClass;
	    private Bundle mBundle;
	    
	    public TabListener(Activity activity, String tag, Class<T> clz) {
	        mActivity = activity;
	        mTag = tag;
	        mClass = clz;
	    }
	    
	    @Override
	    public void onTabSelected(Tab tab, FragmentTransaction ft) {
	    	mBundle = new Bundle();
            if(mTag.equals(TYPE_WEATHER_HOURLY)&&mHourlyData.size()>0){
            	mBundle.putParcelableArrayList("data", mHourlyData);
            }
            if(mTag.equals(TYPE_WEATHER_DAILY)&&mDailyData.size()>0){
            	mBundle.putParcelableArrayList("data", mDailyData);
            }
	        if (mFragment == null) {
	            mFragment = Fragment.instantiate(mActivity, mClass.getName(), mBundle);
	            ft.add(R.id.activity_main, mFragment, mTag);
	        } else {
	            mFragment = Fragment.instantiate(mActivity, mClass.getName(), mBundle);
	            ft.replace(R.id.activity_main, mFragment, mTag);
	            ft.attach(mFragment);
	        }
	    }
	    
	    @Override
	    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	        if (mFragment != null) {
	            ft.detach(mFragment);
	        }
	    }

	    @Override
	    public void onTabReselected(Tab tab, FragmentTransaction ft) {}
	}
}
