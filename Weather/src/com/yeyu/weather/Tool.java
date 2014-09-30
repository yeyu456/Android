package com.yeyu.weather;

import java.util.Calendar;
import java.util.TimeZone;
import android.util.Log;

import static com.yeyu.weather.WeatherConstant.*;

public class Tool {
	public static String toDate(long time){
		Calendar t = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		t.setTimeInMillis(time * 1000);
		int hour = t.get(Calendar.HOUR_OF_DAY);
		String text = "";
		if(hour<10){
			text += "0";
		}
		text += String.valueOf(hour) + ":";
		int min = t.get((Calendar.MINUTE));
		if(min<10){
			text += "0";
		}
		text += String.valueOf(min);
		return text;
	}
	
	public static int toWeek(long time){
		Calendar t = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		t.setTimeInMillis(time * 1000);
		int i = t.get(Calendar.DAY_OF_WEEK)-1;
		i--;
		i = i<0?i+7:i;
		Log.e("week", "" + i);
		return i;
	}
	
	public static String fillZero(String text, int len, boolean addbackward){
		StringBuilder str = new StringBuilder(text);
		while(str.length()<len){
			if(addbackward){
				str.append("0");
			} else {
				str.insert(0, "0");
			}
		}
		return str.toString();
	}
	
	public static int getWindScale(float windSpeed){
		int windScale;
		if(windSpeed >= WIND_TORNADO_SPEED){
			windScale = (int) Math.round(Math.pow((Math.pow(windSpeed, 2.0)), 1.0/3.0));
			if((windScale<WIND_SCALE_MAX) || (windScale>WIND_SCALE_TORNADO_MAX)){
				throw new IllegalArgumentException("Error Tornado Scale Number.");
			}
		} else {
			windScale = (int) Math.round(Math.pow((Math.pow(windSpeed / WIND_CALCULATE_CONSTANT, 2.0)), 1.0/3.0));
			if((windScale<WIND_SCALE_MIN) || (windScale>WIND_SCALE_MAX)){
				throw new IllegalArgumentException("Error Wind Scale Number.");
			}
		}
		return windScale;
	}
	
	public static String getWindDirection(int windBearing){
		if(windBearing<23||windBearing>337){
			return "北风 " + windBearing;
		} else if (windBearing>=23 && windBearing <58){
			return "东北风 " + windBearing;
		} else if (windBearing >=58 && windBearing <113){
			return "东风 " + windBearing;
		} else if (windBearing>=113 && windBearing <158){
			return "东南风 " + windBearing;
		} else if (windBearing>=158 && windBearing <203){
			return "南风 " + windBearing;
		} else if (windBearing>=203 && windBearing <248){
			return "西南风 " + windBearing;
		} else if (windBearing>=248 && windBearing <293){
			return "西风 " + windBearing;
		} else {
			return "西北风 " + windBearing;
		}
	}
}
