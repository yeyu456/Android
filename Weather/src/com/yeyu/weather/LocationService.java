package com.yeyu.weather;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;

public class LocationService extends IntentService {
	
	public LocationService() {
		super("LocationService");
	}

	public static final String EXTRA_PENDING_RESULT = "pending result";
	public static final int RESULT_OK = 0;
	public static final int RESULT_FAIL = 1;
	
	private LocationObject mLocation;
	
	@Override
    protected void onHandleIntent(Intent intent) {
		PendingIntent resultIntent = intent.getParcelableExtra(EXTRA_PENDING_RESULT);
		requestUpdate(resultIntent);
	}
	
	private void requestUpdate(PendingIntent resultIntent){
		PolicyGetLocation get = new PolicyGetLocation(LocationService.this);
		setLocation(get.getLocation(), resultIntent);
	}
	
	private synchronized void setLocation(LocationObject location, PendingIntent resultIntent){
		try {
			if(location==null){
				resultIntent.send(LocationService.this, RESULT_FAIL, null);
			} else {
				mLocation = locationCompare(mLocation, location);
				Intent result = new Intent();
				result.putExtra(MainActivity.RESULT_LOCATION, mLocation);
				resultIntent.send(LocationService.this, RESULT_OK, result);
			}
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}
	
	private LocationObject locationCompare(LocationObject oldloc, LocationObject newloc){
		if(!(oldloc instanceof LocationObject && newloc instanceof LocationObject)){
			if(oldloc instanceof LocationObject){
				return oldloc;
			}
			if(newloc instanceof LocationObject){
				return newloc;
			}
			throw new IllegalArgumentException("Method $execute() cannot compare two non-Location type");
		}
		return (oldloc.time - newloc.time)>0 ? oldloc : newloc;
	}
}
