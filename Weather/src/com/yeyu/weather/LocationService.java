package com.yeyu.weather;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.location.Location;

public class LocationService extends IntentService {
	
	public LocationService() {
		super("LocationService");
	}

	public static final String EXTRA_PENDING_RESULT = "pending result";
	public static final int RESULT_OK = 0;
	public static final int RESULT_FAIL = 1;
	
	private Location mLocation;
	
	@Override
    protected void onHandleIntent(Intent intent) {
		PendingIntent resultIntent = intent.getParcelableExtra(EXTRA_PENDING_RESULT);
		requestUpdate(resultIntent);
	}
	
	private void requestUpdate(PendingIntent resultIntent){
		PolicyGetLocation get = new PolicyGetLocation(LocationService.this);
		setLocation(get.getLocation(), resultIntent);
	}
	
	private synchronized void setLocation(Location location, PendingIntent resultIntent){
		mLocation = locationCompare(mLocation, location);
		try {
			if(location==null){
				resultIntent.send(LocationService.this, RESULT_FAIL, null);
			} else {
				Intent result = new Intent();
				result.putExtra(MainActivity.RESULT_LOCATION, mLocation);
				resultIntent.send(LocationService.this, RESULT_OK, result);
			}
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}
	
	private Location locationCompare(Location oldloc, Location newloc){
		if(!(oldloc instanceof Location && newloc instanceof Location)){
			if(oldloc instanceof Location){
				return oldloc;
			}
			if(newloc instanceof Location){
				return newloc;
			}
			throw new IllegalArgumentException("Method $execute() cannot compare two non-Location type");
		}
		return (oldloc.getTime() - newloc.getTime())>0 ? oldloc : newloc;
	}
}
