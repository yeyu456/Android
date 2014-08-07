package com.zhihu.pocket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

public class DownloadService extends Service {
    private boolean isInterrupt;
    private ResultReceiver mReceiver;
    private Thread mDownloadThread;
    private Thread mEnqueueThread;
    private String mExternalPath;
    private ArrayBlockingQueue<HashMap<String, String>> mQueue;
    private ArrayBlockingQueue<HashMap<String, String>> mAnalyseQueue;
    
    @Override
    public IBinder onBind(Intent intent){
        Log.e("download","start");
        isInterrupt = false;
        mReceiver = (ResultReceiver) intent.getParcelableExtra("receiver");
        mExternalPath = intent.getStringExtra("externalpath");
        mQueue = new ArrayBlockingQueue<HashMap<String, String>>(20);
        mAnalyseQueue = new ArrayBlockingQueue<HashMap<String, String>>(100);
        enqueueItem("questionlist", "index", null);
        enqueueItem("imagelist", "index", null);
        enqueueItem("topiclist", "topic", null);
        mDownloadThread = new Thread(){
            @Override
            public void run(){
                Download();
            }
        };
        mEnqueueThread = new Thread(){
            @Override
            public void run(){
                Analyse();
            }
        };
        mDownloadThread.start();
        mEnqueueThread.start();
        return new MsgBinder();
    }
    
    @Override
    public boolean onUnbind(Intent intent){
        if(mEnqueueThread!=null){
            mEnqueueThread.interrupt();
            mEnqueueThread = null;
        }
        if(mEnqueueThread!=null){
            mEnqueueThread.interrupt();
            mEnqueueThread = null;
        }
        mReceiver = null;
        return super.onUnbind(intent);
    }
    
    private void Download(){
        while(!isInterrupt){
            HashMap<String, String> values;
            try {
                values = mQueue.poll(5, TimeUnit.MINUTES);
                if(values==null){
                    Log.e("download", "break");
                    break;
                }
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                break;
            }
            String url = values.get("url");
            String filepath = values.get("filepath");
            String category = values.get("category");
            boolean islist = values.get("islist")=="0"?false:true;
            File f = new File(filepath);
            try{
                if(!f.exists()){
                    f.createNewFile();
                }
                if(DownloadItem(url, f)){
                    if(islist){
                        HashMap<String, String> analyseItem = new HashMap<String, String>();
                        analyseItem.put("file", filepath);
                        analyseItem.put("category", category);
                        Log.e("analyse", filepath + " " + category);
                        if(mAnalyseQueue!=null&&!mAnalyseQueue.offer(analyseItem)){
                            Log.e("analyse queue full", url);
                        }
                    }
                } else {
                    if(islist){
                        Bundle bundle = new Bundle();
                        bundle.putString("toast", url);
                        if(mReceiver!=null){
                            mReceiver.send(1, bundle);
                        }
                    }
                    try{
                        Thread.sleep(5);
                    } catch(InterruptedException e){
                        e.printStackTrace();
                        break;
                    }
                }
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        if(!mQueue.isEmpty()){
            mQueue.clear();
        }
    }
    
    private boolean DownloadItem(String url, File f){
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try{
            URL urlitem = new URL(url);
            connection = (HttpURLConnection) urlitem.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            Log.e("download", url);
            if(((long) connection.getContentLength())!=f.length()){
                input = connection.getInputStream();
                output = new FileOutputStream(f);
                byte data[] = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }
                output.flush();
            }
        } catch (IOException e){
            e.printStackTrace();
            return false;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        } finally{
            try{
                if(output!=null){
                    output.close();
                }
                if(input!=null){
                    input.close();
                }
                if(connection!=null){
                    connection.disconnect();
                }
            } catch(IOException e){
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }
    

    private void Analyse(){
        while(!isInterrupt){
            HashMap<String, String> values = null;
            if(mAnalyseQueue!=null){
                try {
                    values = mAnalyseQueue.poll(5, TimeUnit.MINUTES);
                    if(values==null){
                        Log.e("analyse", "break");
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
            String listfile = values.get("file");
            String category = values.get("category");
            try {
                System.out.println("listfile " + listfile);
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(listfile), "UTF8"));
                String file;
                int filecount = 0;
                while((file = br.readLine()) != null){
                    if(category.equals("topic")){
                        if(listfile.contains("topiclist")){
                            File topicDir = new File(listfile.replace("topiclist", file));
                            if(!topicDir.exists()){
                                topicDir.mkdir();
                            }
                            Log.e("enqueue", listfile);
                            enqueueItem("questionlist", "topic", file);
                            Log.e("enqueue", listfile);
                            enqueueItem("imagelist", "topic", file);
                        } else {
                            String[] name= file.split("/");
                            enqueueItem(name[1], category, name[0]);
                        }
                    } else {
                        enqueueItem(file, category, null);
                    }
                    filecount += 1;
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!mAnalyseQueue.isEmpty()){
            mAnalyseQueue.clear();
        }
    }
    
    private void enqueueItem(String name, String category, String subcategory){
        String url;
        try {
            if(subcategory!=null){
                url = "http://192.168.1.161/"+ category + "/" + URLEncoder.encode(subcategory, "UTF-8").replace("+", "%20") + "/" + name.trim();
            } else {
                url = "http://192.168.1.161/"+ category + "/" + name.trim();
            }
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            Log.e("enqueue", category);
            return;
        }
        String filePath = "";
        String isupdate = "0";
        String islist = "0";
        switch(category){
            case "index" : {
                filePath = mExternalPath + "/" + this.getResources().getString(R.string.dir_index);
                break;
            }
            case "topic" : {
                filePath = mExternalPath + "/" + this.getResources().getString(R.string.dir_topic);
                break;
            }
            case "explore" : {
                filePath = mExternalPath + "/" + this.getResources().getString(R.string.dir_explore);
                break;
            }
        }
        if(name.trim().contains("questionlist")||name.trim().contains("imagelist")||name.trim()=="topiclist"){
            islist = "1";
        }
        if(name.trim().matches(".*\\.html")){
            isupdate = "1";
        }
        if(subcategory!=null){
            filePath += "/" + subcategory + "/" + name.trim();
        } else {
            filePath += "/" + name.trim();
        }
        if((new File(filePath).exists())&&islist=="0"){
            return;
        }
        Log.e("enqueue filepath", filePath);
        HashMap<String, String> values = new HashMap<String, String>();
        values.put("url", url);
        values.put("filepath", filePath);
        values.put("category", category);
        values.put("isupdate", isupdate);
        values.put("islist", islist);
        if(mQueue!=null){
            try {
                mQueue.put(values);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void sendNotify(int totalcount, int incr, String url, boolean dismiss){
        Bundle bundle = new Bundle();
        bundle.putInt("totalcount", totalcount);
        bundle.putInt("incr", incr);
        if(url!=null){
            bundle.putString("url", url);
        }
        if(dismiss){
            bundle.putInt("dismiss", 1);
        }
        if(mReceiver!=null){
            mReceiver.send(0, bundle);
        }
    }
    
    public class MsgBinder extends Binder{  
        public DownloadService getService(){  
            return DownloadService.this;  
        }  
    }  
}
