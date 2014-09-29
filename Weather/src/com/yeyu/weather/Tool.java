package com.yeyu.weather;

import java.util.Calendar;
import java.util.TimeZone;

import android.util.Log;

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
}
