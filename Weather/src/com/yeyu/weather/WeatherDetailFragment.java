package com.yeyu.weather;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yeyu.widget.GraphicTableView;

public class WeatherDetailFragment extends Fragment {
	
	protected static WeatherDetailFragment newInstance(ArrayList<Integer> numbers, 
													   int yAixsInter,
													   String sign){
		WeatherDetailFragment f = new WeatherDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putIntegerArrayList("numbers", numbers);
		bundle.putInt("inter", yAixsInter);
		bundle.putString("sign", sign);
		f.setArguments(bundle);
		return f;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, 
    						 ViewGroup container,
    						 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_detail_table, container, false);
		GraphicTableView gv = (GraphicTableView) v.findViewById(R.id.graphic_table);
		Bundle bundle = getArguments();
		gv.setYInter(bundle.getInt("inter"));
		gv.drawPoints(bundle.getIntegerArrayList("numbers"), bundle.getString("sign"));
		return v;
	}
}
