package com.simplefileexplorer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.SimpleAdapter;

public class SimpleButtonAdapter extends SimpleAdapter {
    
    private Context mContext;
    private int mResource;
    private List<Map<String, String>> mData;
    
    public SimpleButtonAdapter(Context context,
                               List<Map<String, String>> data, 
                               int resource, 
                               String[] from, 
                               int[] to){
        super(context, data, resource, from, to);
        mContext = context;
        mResource = resource;
        mData = data;
        Collections.sort(mData, new fileNameCompare());
    }
    
    class fileNameCompare implements Comparator<Map<String, String>> {
        public int compare(Map<String, String> t1, Map<String, String> t2){
            return t1.get("name").compareTo(t2.get("name"));
        }
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v;
        
        if(convertView==null){
            v = (LayoutInflater.from(mContext).inflate(mResource, null));
        } else{
             v = convertView;
        }
        
        Map<String, String> p = (Map<String, String>) this.getItem(position);
        
        if(p!=null){
            FileButton btn = (FileButton) v.findViewById(R.id.file_list_button);
            btn.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources()
                                                                .getDrawable(Integer.parseInt(p.get("image"))), 
                                                        null, null, null);
            btn.setButtonText(p);
        }
        return v;
    }
    
    public void remove(int position){
        mData.remove(position);
        this.notifyDataSetChanged();
    }
    
    public void edit(int position, String path, String name){
        mData.get(position).put("path", path);
        mData.get(position).put("name", name);
        this.notifyDataSetChanged();
    }
    
    public void add(Map<String, String> item){
        mData.add(item);
        this.notifyDataSetChanged();
    }
    
    public void empty(){
        mData.clear();
    }
}
