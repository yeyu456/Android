package com.simplefileexplorer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends Activity {
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
    
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.main_activity);

        sdf.setTimeZone(TimeZone.getDefault());
        ListView v = (ListView) findViewById(R.id.main_activity_listview);
        File rootFile;
        
        if(getIntent().hasExtra("path")){
            rootFile = new File(getIntent().getExtras().getString("path"));
        }
        else{
            rootFile = new File("/");
        }
        if(rootFile.isDirectory()){
            if(rootFile.canExecute()&&rootFile.canRead()){
                SimpleButtonAdapter simpleButtonAdapter 
                    = new SimpleButtonAdapter(this, 
                                              generateData(rootFile),
                                              R.layout.file_list,
                                              new String[]{"text", "image", "atr", "path"},
                                              new int[]{R.id.file_list_button});
                v.setAdapter(simpleButtonAdapter);
            }
            else{
                Toast.makeText(this, 
                               "No authority to open directory " + rootFile.getName(), 
                               Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
        else{
            Intent intent = new Intent("android.intent.action.VIEW");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }
    
    private List<Map<String, String>> generateData(File rootFile){
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        
        for(File f:rootFile.listFiles()){
            Map<String, String> mFile = new HashMap<String, String>();
            
            Integer drawId = f.isDirectory() ? R.drawable.isdirectory : R.drawable.isfile;
            String atr = sdf.format(f.lastModified()) + " " + getFileLen(f.length());
            String path = f.getAbsolutePath();
            
            mFile.put("text", f.getName());
            mFile.put("image", drawId.toString());
            mFile.put("atr", atr);
            mFile.put("path", path);
            
            list.add(mFile);
        }
        Collections.sort(list, new fileNameCompare());
        return list;
    }
    
    class fileNameCompare implements Comparator<Map<String, String>> {
        public int compare(Map<String, String> t1, Map<String, String> t2){
            return t1.get("text").compareTo(t2.get("text"));
        }
    }
    
    private String getFileLen(long len){
        final String[] units = new String[]{"Bytes", "K", "M", "G", "T"};
        double result = (double) len;
        for(int n=0;n<units.length;n++){
            if(result/1024>1){
                result = result / 1024;
                continue;
            }
            else{
                return String.format("%.2f", result) + units[n];
            }
        }
        return Long.toString(len) + units[4];
    }
    
    
}
