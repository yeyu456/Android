package com.yeyu.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class WeatherForecastAPI {
	
	public static final String FORECAST_REQUEST_URI_PREFIX = "https://api.forecast.io/forecast/";
	public static final String FORECAST_REQUEST_URI_POSTFIX = "?units=si";
	public static final int ONE_HOUR_IN_SECONDS = 60 * 60;
	public static final int TWO_HOUR_IN£ßSECONDS = 2 * 60 * 60;
	public static final int SIX_HOUR_IN_SECONDS = 6 * 60 * 60;
	public static final int ONE_DAY_IN_SECONDS = 24 * 60 * 60;
	public static final int TWO_DAY_IN_SECONDS = 2 * 24 * 60 * 60;
	public static final int THREE_DAY_IN_SECONDS = 3 * 24 * 60 * 60;

	private static final String FORECAST_KEY = "a6b57ff137f21b8bf5c8cefd8a0e8f7e";
	
	public static String getDefaultWeather(double latitude, double longitude){
		String FORECAST_RQUEST_URI = FORECAST_REQUEST_URI_PREFIX + 
				 FORECAST_KEY + "/" + 
				 latitude + "," + longitude + 
				 FORECAST_REQUEST_URI_POSTFIX;
		try {
			URL url = new URL(FORECAST_RQUEST_URI);
			return connect(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
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
                System.out.println("line " + line + " line");
                sb.append(line);
            }
            br.close();
            return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
