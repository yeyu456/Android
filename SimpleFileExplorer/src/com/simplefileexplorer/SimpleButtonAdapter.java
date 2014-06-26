package com.simplefileexplorer;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class SimpleButtonAdapter extends SimpleAdapter {
    
    private Context mContext;
    private int mResource;
    
    public SimpleButtonAdapter(Context context, 
                               List<Map<String, String>> data, 
                               int resource, 
                               String[] from, 
                               int[] to){
        super(context, data, resource, from, to);
        mContext = context;
        mResource = resource;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v;
        
        if(convertView==null){
            v = (LayoutInflater.from(mContext).inflate(mResource, null));
        }
        else{
             v = convertView;
        }
        
        Map<String, String> p = (Map<String, String>) this.getItem(position);
        
        if(p!=null){
            FileButton btn = (FileButton) v.findViewById(R.id.file_list_button);
            btn.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources()
                                                                .getDrawable(Integer.parseInt(p.get("image"))), 
                                                        null, null, null);
            btn.mAtr = p.get("atr");
            btn.mPath = p.get("path");
            btn.setButtonText(p.get("text").toString());
            btn.setOnClickListener(listener);
            btn.setOnLongClickListener(longListener);
        }
        return v;
    }
    
    View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            FileButton fbt = (FileButton) v; 
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("path", fbt.mPath);
            mContext.startActivity(intent);
        }
    };
    
    View.OnLongClickListener longListener = new View.OnLongClickListener() {
        
        @Override
        public boolean onLongClick(View v) {
            
            return true;
        }
    };
    
}
