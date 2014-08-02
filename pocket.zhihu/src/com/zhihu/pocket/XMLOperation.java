package com.zhihu.pocket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class XMLOperation {
    
    public static void xmlParse(File f, ArrayList<Map<String, String>> mData){
        System.out.println("start parse");
        if(f.isDirectory()){
            String[] files = null;
            if(mData.size()>0){
                files = new String[mData.size()];
                for(int n=0;n<mData.size();n++){
                    files[n] = mData.get(n).get("file");
                    Log.e("file", files[n]);
                }
            }
            
            for(File subfile : f.listFiles()){
                if(subfile.getName().matches(".*\\.html")){
                    if(files!=null){
                        if(Arrays.binarySearch(files, subfile.getPath()) != -1){
                            continue;
                        }
                    }
                    if(subfile.length()==0){
                        continue;
                    }
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("file", subfile.getPath());
                    String title = getTitle(subfile);
                    map.put("title", title);
                    mData.add(map);
                }
            }
        } else {
            Log.e("xml", f.getPath() + "is not directory");
        }
    }
    
    public static String getTitle(File f){
        if(f.exists()){
            try{
                BufferedReader bf = new BufferedReader(new FileReader(f));
                String titleText;
                do{
                    titleText = bf.readLine();
                }while(titleText!=null&&!titleText.matches("<title>.*</title>"));
                if(titleText==null){
                    Log.e("titletext file", f.getPath());
                }
                bf.close();
                Document doc = Jsoup.parseBodyFragment(titleText, "UTF-8");
                Element element = doc.body();
                if(element==null){
                    Log.e("jsoup", "more than one tag title");
                } else {
                    return element.text();
                }
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        return "ERROR";
    }
    
    public static Elements getElements(File f){
        if(f.exists()){
            try{
                Document doc = Jsoup.parse(f, "UTF-8");
                Elements elements = doc.getElementsByTag("detail");
                Elements answers = doc.getElementsByTag("answer");
                if(answers.size()==0){
                    Log.e("jsoup", "no answer");
                } else{
                    if(!elements.addAll(answers)){
                        Log.e("jsoup", "add answer failed");
                    }
                }
                return elements;
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
