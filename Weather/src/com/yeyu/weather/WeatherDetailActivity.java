package com.yeyu.weather;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import static com.yeyu.weather.WeatherConstant.*;
import com.yeyu.widget.PartlyFillCircle;

public class WeatherDetailActivity extends Activity {
	
	private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				onDown(v);
				return true;
			}
			if(event.getAction() == MotionEvent.ACTION_UP){
				onUp(v);
			}
			return false;
		}
		
		private void onDown(View v){
			animate(v, R.anim.weather_detail_button_down);
			int newTag = ((int) v.getTag()) + 1;
			if(type.equals(TYPE_WEATHER_DAILY)&&newTag > TYPE_WEATHER_DETAIL_SUN_MOON){
				newTag = 1;
			}
			if(type.equals(TYPE_WEATHER_HOURLY)&&newTag > TYPE_WEATHER_DETAIL_CLOUD){
				newTag = 1;
			}
			v.setTag(newTag);
			setData(newTag);
			((PartlyFillCircle) v).setColorFill(typeColorMap.get(newTag));
		}
		
		private void onUp(View v){
			animate(v, R.anim.weather_detail_button_up);
		}
	};
	
	private ArrayList<WeatherObject> mData;
	private String type;
	
	@Override
	protected void onCreate(Bundle state){
		super.onCreate(state);
		Bundle bundle = this.getIntent().getExtras();
		type = bundle.getString("type");
		mData = bundle.getParcelableArrayList("data");
		if(type.equals(TYPE_WEATHER_DAILY)){
			setContentView(R.layout.activity_detail);
		}
		if(type.equals(TYPE_WEATHER_HOURLY)){
			setContentView(R.layout.activity_detail);
		}
		setData(TYPE_WEATHER_DETAIL_TEM);
		View director = findViewById(R.id.director);
		director.setTag(TYPE_WEATHER_DETAIL_TEM);
		director.setOnTouchListener(mTouchListener);
	}
	
	private void setData(int weatherType){
		PartlyFillCircle circle = (PartlyFillCircle) findViewById(R.id.director);
		ListView lv = (ListView) findViewById(R.id.listview);
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		ArrayList<Spannable> texts = new ArrayList<Spannable>();
		int yAixsInter = 0;
		String sign = "";
		
		String text = "";
		int len = 0;
		
		switch(weatherType){
			case TYPE_WEATHER_DETAIL_TEM : {
				for(WeatherObject obj:mData){
					if(type.equals(TYPE_WEATHER_DAILY)){
						numbers.add((int)((WeatherObjectDaily) obj).temperatureMin);
						text = "\t" + getResources().getStringArray(R.array.time_week)[Tool.toWeek(obj.time)]+ "\n";
						len = text.length();
						text += getDailyTemperatureText((WeatherObjectDaily) obj);
					} else if(type.equals(TYPE_WEATHER_HOURLY)){
						numbers.add((int)((WeatherObjectHourly) obj).temperature);
						text = "\t" + Tool.toDate(obj.time)+ "\t";
						len = text.length();
						text += getHourlyTemperatureText((WeatherObjectHourly) obj);
					}
					texts.add(getSpan(text, len));
				}
				yAixsInter = 3;
				sign = TEMPERATURE_SIGN;
				circle.setText("温");
				break;
			}
			case TYPE_WEATHER_DETAIL_WATER : {
				for(WeatherObject obj:mData){
					if(type.equals(TYPE_WEATHER_DAILY)){
						numbers.add((int)((WeatherObjectDaily) obj).precipIntensityMax);
						text = "\t" + getResources().getStringArray(R.array.time_week)[Tool.toWeek(obj.time)];
						len = text.length();
						text += getDailyWaterText((WeatherObjectDaily) obj);
					} else if(type.equals(TYPE_WEATHER_HOURLY)){
						numbers.add((int)((WeatherObjectHourly) obj).precipIntensity);
						text = "\t" + Tool.toDate(obj.time)+ "\t";
						len = text.length();
						text += getHourlyWaterText((WeatherObjectHourly) obj);
					}
					texts.add(getSpan(text, len));
				}
				yAixsInter = 3;
				sign = WATER_SIGN;
				circle.setText("水");
				break;
			}
			case TYPE_WEATHER_DETAIL_WIND : {
				for(WeatherObject obj:mData){
					numbers.add((int)(obj.windSpeed));
					if(type.equals(TYPE_WEATHER_DAILY)){
						text = "\t" + getResources().getStringArray(R.array.time_week)[Tool.toWeek(obj.time)];
						len = text.length();
					} else if(type.equals(TYPE_WEATHER_HOURLY)){
						text = "\t" + Tool.toDate(obj.time)+ "\t";
						len = text.length();
					}
					text += getWindText(obj);
					texts.add(getSpan(text, len));
				}
				yAixsInter = 3;
				sign = WIND_SIGN;
				circle.setText("风");
				break;
			}
			case TYPE_WEATHER_DETAIL_CLOUD : {
				for(WeatherObject obj:mData){
					numbers.add((int)(obj.cloudCover * 100));
					if(type.equals(TYPE_WEATHER_DAILY)){
						text = "\t" + getResources().getStringArray(R.array.time_week)[Tool.toWeek(obj.time)];
						len = text.length();
					} else if(type.equals(TYPE_WEATHER_HOURLY)){
						text = "\t" + Tool.toDate(obj.time)+ "\t";
						len = text.length();
					}
					text += getCloudText(obj);
					texts.add(getSpan(text, len));
				}
				yAixsInter = 2;
				sign = CLOUD_SIGN;
				circle.setText("云");
				break;
			}
			case TYPE_WEATHER_DETAIL_SUN_MOON : {
				if(type.equals(TYPE_WEATHER_DAILY)){
					for(WeatherObject obj:mData){
						numbers.add((int)(((WeatherObjectDaily) obj).moonPhase * 100));
						text = "\t" + getResources().getStringArray(R.array.time_week)[Tool.toWeek(obj.time)];
						len = text.length();
						text += getSunMoonText((WeatherObjectDaily) obj);
						texts.add(getSpan(text, len));
					}
				}
				yAixsInter = 3;
				sign = MOON_SIGN;
				circle.setText("日月");
				break;
			}
		}
		if(texts.size()>0){
			ArrayAdapter<Spannable> mAdapter = new ArrayAdapter<Spannable>(WeatherDetailActivity.this,
												 	 			     R.layout.activity_detail_card_view,
												 	 			     R.id.data,
												 	 			     texts);
			lv.setAdapter(mAdapter);
		}
		setGraphicFragment(numbers, yAixsInter, sign);
	}
	
	private void setGraphicFragment(ArrayList<Integer> numbers, int yAixsInter, String sign){
		WeatherDetailFragment wf = WeatherDetailFragment.newInstance(numbers, yAixsInter, sign);
		getFragmentManager().beginTransaction().replace(R.id.table, wf).commit();
	}
	
	private Spannable getSpan(String text, int len){
		Spannable wordtoSpan = new SpannableString(text);
		wordtoSpan.setSpan(new RelativeSizeSpan(1.0f), 0, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		wordtoSpan.setSpan(new RelativeSizeSpan(0.7f), len, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return wordtoSpan;
	}
	
	private String getDailyTemperatureText(WeatherObjectDaily dayobj){
		StringBuilder text = new StringBuilder();
		text.append("\t最低温度:\t").append(dayobj.temperatureMin).append(TEMPERATURE_SIGN).append("\t")
			.append("[").append(Tool.toDate(dayobj.temperatureMinTime)).append("]\t");
		text.append("\t体表最低:\t").append(dayobj.apparentTemperatureMin).append(TEMPERATURE_SIGN).append("\t")
			.append("[").append(Tool.toDate(dayobj.apparentTemperatureMinTime)).append("]\n");
		
		text.append("\t最高温度:\t").append(dayobj.temperatureMax).append(TEMPERATURE_SIGN).append("\t")
			.append("[").append(Tool.toDate(dayobj.temperatureMaxTime)).append("]\t");
		text.append("\t体表最高:\t").append(dayobj.apparentTemperatureMax).append(TEMPERATURE_SIGN).append("\t")
			.append("[").append(Tool.toDate(dayobj.apparentTemperatureMaxTime)).append("]");
		return text.toString();
	}
	
	private String getHourlyTemperatureText(WeatherObjectHourly hourobj){
		StringBuilder text = new StringBuilder();
		text.append("\t温度:\t").append(hourobj.temperature).append(TEMPERATURE_SIGN).append("\t\t")
			.append("\t体表:\t").append(hourobj.apparentTemperature).append(TEMPERATURE_SIGN);
		return text.toString();
	}
	
	private String getDailyWaterText(WeatherObjectDaily dayobj){
		StringBuilder text = new StringBuilder("\n");
		text.append("\t平均降雨量:\t").append(Tool.fillZero(String.valueOf(dayobj.precipIntensity), PRECIPINTENSITY_LENGTH, true))
			.append(WATER_SIGN).append("\t").append("\t最大降雨量:\t")
			.append(Tool.fillZero(String.valueOf(dayobj.precipIntensityMax), PRECIPINTENSITY_LENGTH, true)).append(WATER_SIGN)
			.append("\t[").append(Tool.toDate(dayobj.precipIntensityMaxTime)).append("]\n");
		text.append("\t降雨概率:\t").append(Tool.fillZero(String.valueOf((int)(dayobj.precipProbability * 100)), PERCENTAGE_LENGTH, false))
			.append(WATER_PROBABILITY_SIGN).append("\t\t\t\t")
			.append("\t空气湿度:\t" + (int)(dayobj.humidity * 100)).append(WATER_HUMDITY);
		return text.toString();
	}
	
	private String getHourlyWaterText(WeatherObjectHourly hourobj){
		StringBuilder text = new StringBuilder("\n");
		text.append("\t降雨量:\t").append(Tool.fillZero(String.valueOf(hourobj.precipIntensity), PRECIPINTENSITY_LENGTH, true))
			.append(WATER_SIGN).append("\t").append("\t降雨概率:\t")
			.append(Tool.fillZero(String.valueOf((int)(hourobj.precipProbability * 100)), PERCENTAGE_LENGTH, false))
			.append(WATER_PROBABILITY_SIGN).append("\t");
		text.append("\t空气湿度:\t").append((int)(hourobj.humidity * 100)).append(WATER_HUMDITY);
		return text.toString();
	}
	
	private String getWindText(WeatherObject obj){
		StringBuilder text = new StringBuilder();
		text.append("\t风速:\t").append(obj.windSpeed).append(WIND_SIGN).append("\t\t").append("\t风向:\t").append(obj.windBearing);
		return text.toString();
	}
	
	private String getCloudText(WeatherObject obj){
		StringBuilder text = new StringBuilder();
		text.append("\t云量覆盖:\t").append(Tool.fillZero(String.valueOf((int)(obj.cloudCover * 100)),  PERCENTAGE_LENGTH, false))
			.append(CLOUD_SIGN).append("\t\t").append("\t能见度:\t").append(obj.visibility).append(VISIBLE_SIGN);
		return text.toString();
	}
	
	private String getSunMoonText(WeatherObjectDaily dayobj){
		StringBuilder text = new StringBuilder();
		text.append("\t日出时间:[").append(Tool.toDate(dayobj.sunriseTime))
			.append("]\t\t日落时间:[").append(Tool.toDate(dayobj.sunsetTime))
			.append("]\t\t月相:\t").append((int)(dayobj.moonPhase * 100)).append(MOON_SIGN);
		return text.toString();
	}
	
	private void animate(View v, int animid){
		Animation sa = AnimationUtils.loadAnimation(WeatherDetailActivity.this, animid); 
		v.startAnimation(sa);
	}
	
}
