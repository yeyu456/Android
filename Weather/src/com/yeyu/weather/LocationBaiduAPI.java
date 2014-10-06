package com.yeyu.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import com.google.gson.Gson;

public class LocationBaiduAPI {

	public static final String BAIDU_REQUEST_URI_PREFIX = "http://api.map.baidu.com/location/";
	public static final String BAIDU_REQUEST_URI_POSTFIX = "&coor=bd09ll";
	public static final String BAIDU_API_VERSION = "v1.1";
	
	private static final String BAIDU_AK = "D77442960b2c0d7d890d3003b922f093";
	
	public static LocationObject baiduLocationByIp(){
		String BAIDU_REQUEST_URI = BAIDU_REQUEST_URI_PREFIX + "ip?ak=" + BAIDU_AK + BAIDU_REQUEST_URI_POSTFIX;
		try {
			URL url = new URL(BAIDU_REQUEST_URI);
			String result = connect(url);
			if(result!=null){
				return baiduParseJson(result);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String connect(URL url){
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
		}
		return null;
	}
	
	private static LocationObject baiduParseJson(String respond){
		LocationObject location = new LocationObject();
		Gson gson = new Gson();
		BaiduJson baiduObject = gson.fromJson(respond, BaiduJson.class);
		if(baiduObject.status==0){
			location.longitude = Double.parseDouble(baiduObject.content.point.x);
			location.latitude = Double.parseDouble(baiduObject.content.point.y);
			location.address = baiduObject.content.address;
			location.time = Calendar.getInstance().getTimeInMillis();
		}
		return location;
	}
	
	class BaiduJson {
		String address;
		Content content;
		int status;
		
		class Content {
			String address;
			Detail address_detail;
			Point point;
			
			class Detail {
				String city;  
				int city_code;
				String district;
				String province;
				String street;
				String street_number;
			}
			
			class Point {
				String x;
				String y;
			}
		}
	}
}
