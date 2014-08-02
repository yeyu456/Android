package com.zhihu.pocket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

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
        mAnalyseQueue = new ArrayBlockingQueue<HashMap<String, String>>(20);
        enqueueItem("questionlist", "index");
        enqueueItem("imagelist", "index");
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
        if(!mQueue.isEmpty()){
            mQueue.clear();
        }
        if(!mAnalyseQueue.isEmpty()){
            mAnalyseQueue.clear();
        }
        mAnalyseQueue = null;
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
        int sleepCount = 5;
        while(!isInterrupt){
            HashMap<String, String> values;
            try {
                values = mQueue.take();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                break;
            }
            String url = values.get("url");
            String filepath = values.get("filepath");
            String category = values.get("category");
            boolean isupdate = values.get("isupdate")=="0"?false:true;
            boolean islist = values.get("islist")=="0"?false:true;
            File f = new File(filepath);
            try{
                if(f.exists()){
                    if(!isupdate&&!islist){
                        continue;
                    }
                } else {
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
                    enqueueItem(f.getName(), category);
                    if(islist){
                        Bundle bundle = new Bundle();
                        bundle.putString("toast", url);
                        if(mReceiver!=null){
                            mReceiver.send(1, bundle);
                        }
                    }
                    try{
                        Thread.sleep(sleepCount);
                    } catch(InterruptedException e){
                        e.printStackTrace();
                        break;
                    }
                }
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    
    private boolean DownloadItem(String url, File f){
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try{
            URL urlitem = new URL(url);
            connection = (HttpURLConnection) urlitem.openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            Log.e("len", (long) connection.getContentLength() + " " + f.length());
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
                    values = mAnalyseQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
            String listfile = values.get("file");
            String category = values.get("category");
            try {
                System.out.println("listfile " + listfile);
                BufferedReader br = new BufferedReader(new FileReader(listfile));
                String file;
                while((file = br.readLine()) != null){
                    enqueueItem(file, category);
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void enqueueItem(String name, String category){
        String url ="http://192.168.1.161/"+ category + "/" + name.trim();
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
        if(name.trim()=="questionlist"||name.trim()=="imagelist"){
            islist = "1";
        }
        if(name.trim().matches(".*\\.html")){
            isupdate = "1";
        }
        filePath += "/" + name.trim();
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
    
    public class MsgBinder extends Binder{  
        public DownloadService getService(){  
            return DownloadService.this;  
        }  
    }  
}
