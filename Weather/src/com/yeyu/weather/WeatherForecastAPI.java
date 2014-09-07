package com.yeyu.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;

public class WeatherForecastAPI {
	
	public static final String FORECAST_REQUEST_URI_PREFIX = "https://api.forecast.io/forecast/";
	public static final String FORECAST_REQUEST_URI_POSTFIX = "?units=si";
	public static final int MAX_COUNT_HOURLY_DATA = 6;
	public static final int MAX_COUNT_DAILY_DATA = 7;

	private static final String FORECAST_KEY = "a6b57ff137f21b8bf5c8cefd8a0e8f7e";
	
	public static ArrayList<WeatherObject> getDefaultWeather(double latitude, double longitude){
		String FORECAST_RQUEST_URI = FORECAST_REQUEST_URI_PREFIX + 
				 FORECAST_KEY + "/" + 
				 latitude + "," + longitude + 
				 FORECAST_REQUEST_URI_POSTFIX;
		try {
			URL url = new URL(FORECAST_RQUEST_URI);
			String data = connect(url);
			if(data!=null){
				ForecastJson gsonData = forecastParseJson(data);
				return generateWeather(gsonData);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String connect(URL url){
		try {
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.connect();
			InputStream is=conn.getInputStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            String line=null;
            StringBuffer sb=new StringBuffer();
            while((line=br.readLine())!=null){
                sb.append(line);
            }
            br.close();
            return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static ForecastJson forecastParseJson(String data){
		Gson gsonData = new Gson();
		return gsonData.fromJson(data, ForecastJson.class);
	}
	
	private static ArrayList<WeatherObject> generateWeather(ForecastJson gsonData){
		double latitude = gsonData.latitude;
		double longitude = gsonData.longitude;
		if(latitude==0.0 || longitude==0.0){
			return null;
		}
		
		ArrayList<WeatherObject> weatherList = new ArrayList<WeatherObject>();
		WeatherObject cur = gsonData.currently;
		addLocation(latitude, longitude, cur);
		weatherList.add(cur);
		
		getData(1 + MAX_COUNT_HOURLY_DATA, cur.time, latitude, longitude, weatherList, gsonData.hourly.data);
		getData(1 + MAX_COUNT_HOURLY_DATA + MAX_COUNT_DAILY_DATA, cur.time, latitude, longitude, weatherList, gsonData.daily.data);

		for(WeatherObject obj:weatherList){
			System.out.println(obj.time);
		}
		return weatherList;
	}
	
	private static void isNotFull(int count, ArrayList<WeatherObject> obj){
		while(obj.size()<count){
			obj.add(null);
		}
	}
	
	private static void getData(int maxcount, 
								long timelimit, 
								double latitude, 
								double longitude, 
								ArrayList<WeatherObject> obj, 
								WeatherObject[] data){
		int count = obj.size();
		for(WeatherObject d:data){
			if(timelimit>d.time){
				continue;
			}
			if(count>=maxcount){
				break;
			}
			addLocation(latitude, longitude, d);
			obj.add(d);
			count++;
		}
		isNotFull(maxcount, obj);
	}
	
	private static void addLocation(double latitude, double longitude, WeatherObject obj){
		obj.latitude = latitude;
		obj.longitude = longitude;
	}
	
	class ForecastJson {
		double latitude;
		double longitude;
		WeatherHourlyObject currently;
		hour hourly;
		day daily;
		class hour {
			WeatherHourlyObject[] data;
		}
		class day {
			WeatherDailyObject[] data;
		}
	}
}
