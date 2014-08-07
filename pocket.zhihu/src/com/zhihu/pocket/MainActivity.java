package com.zhihu.pocket;

import java.io.File;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;
import android.view.GestureDetector;

public class MainActivity extends Activity implements GestureDetector.OnGestureListener{
    String mExternalPath = null;
    private boolean mBound = false;
    private GestureDetector mGestureScanner;
	
	@Override
	protected void onCreate(Bundle state){
	    super.onCreate(state);
	    setContentView(R.layout.main_page);
	    mGestureScanner = new GestureDetector(MainActivity.this, MainActivity.this);
	    
	    //initialize external store directories
	    if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
	        Toast.makeText(this, "sdcard not mounted", Toast.LENGTH_SHORT).show();
	        this.finish();
	    } else {
	        mExternalPath = Environment.getExternalStorageDirectory().getPath();
	        initDirectory();
	    }
	    
	    //initialize actionbar tab
	    initTab();

        //initialize downloader
        //ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //if (mWifi.isConnected()) {
            startDownload();
        //} else {
        //    Log.e("wifi", "not connected");
        //}
	}
	
	@Override
	protected void onDestroy(){
	    super.onDestroy();
	    stopDownload();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.service_start_download : {
	        startDownload();
	        return true;
	    }
	    case R.id.service_stop_download : {
	        stopDownload();
	        return true;
	    }
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent ev) {
	    if (mGestureScanner != null) {
	        if (mGestureScanner.onTouchEvent(ev))
	            return true;
	    }
	    return super.onTouchEvent(ev);
    }
	
	@Override
	public boolean onDown (MotionEvent e){
	    return false;
	}
	
	@Override
	public void onLongPress(MotionEvent e){
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
	    return false;
	}
	
	@Override
	public void onShowPress(MotionEvent e){
	    
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e){
	    return false;
	}
	
	@Override
	public boolean onFling (MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
	    int dis = (int) (e2.getX() - e1.getX());
	    int ydis = (int) (e2.getY() - e2.getY());
	    ActionBar actionbar = this.getActionBar();
	    int pos = actionbar.getSelectedTab().getPosition();
	    System.out.println("tab dis " + dis + " ydis" + ydis);
	    if(ydis>50||ydis<-50||Math.abs(velocityX)< Math.abs(velocityY)){
	        return false;
	    }
	    if(dis>100){
	        pos += 1;
	        if(pos==3){
	            pos = 0;
	        }
	        System.out.println("tab pos " + pos);
	        actionbar.selectTab(actionbar.getTabAt(pos));
	        return true;
	    }
	    if(dis<-100){
	        pos -= 1;
	        if(pos==-1){
	            pos = 2;
	        }
	        System.out.println("tab pos " + pos);
	        actionbar.selectTab(actionbar.getTabAt(pos));
	        return true;
	    }
	    return false;
	}
	
	private void initDirectory(){
	    int[] dirList = new int[]{R.string.dir_app,
	                              R.string.dir_index,
	                              R.string.dir_topic,
	                              R.string.dir_explore,
	                              R.string.dir_dailyhot,
	                              R.string.dir_monthlyhot,
	                             };
	    for(int n : dirList){
	        File mDir = new File(mExternalPath, getResources().getString(n));
	        if(!mDir.exists()){
	            if(!mDir.mkdir()){
	                Log.e("Dir", "make directory failed " + mDir.getPath());
	                MainActivity.this.finish();
	            }
	        }
	    }
	}
	
	private void initTab(){
	    ActionBar actionbar = this.getActionBar();
	    actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    actionbar.setDisplayShowTitleEnabled(false);
	    Tab indexTab = actionbar.newTab()
	                   .setText(R.string.tab_index)
	                   .setTabListener(new TabListener<IndexFragment>(this, "index", IndexFragment.class));
	    Tab topicTab = actionbar.newTab()
	                   .setText(R.string.tab_topic)
                       .setTabListener(new TabListener<TopicFragment>(this, "topic", TopicFragment.class));
	    Tab exploreTab = actionbar.newTab()
	                   .setText(R.string.tab_explore)
                       .setTabListener(new TabListener<Fragment>(this, "explore", Fragment.class));
	    actionbar.addTab(indexTab);
	    actionbar.addTab(topicTab);
	    actionbar.addTab(exploreTab);
	}
	
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            DownloadService.MsgBinder binder = (DownloadService.MsgBinder) service;
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
	
	private void startDownload(){
	    ProgressDialog progress = null;
        if(mBound){
            stopDownload();
            progress = new ProgressDialog(this);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setTitle("正在重启下载...");
            progress.show();
        }
        Intent intent = new Intent(MainActivity.this, DownloadService.class);
        intent.putExtra("receiver", new DownloadReceiver(new Handler()));
        intent.putExtra("externalpath", mExternalPath);
        this.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        if(progress!=null){
            progress.dismiss();
        }
	}
	
	private void stopDownload(){
        if(mBound){
            this.unbindService(mConnection);
            System.out.println("mBound" + mBound);
            mBound = false;
        } else{
            Toast.makeText(this, "下载已经停止", Toast.LENGTH_SHORT).show();
        }
	}
	
	
    private void updateFragment(String fragmentTag){
        switch(fragmentTag){
            case "index" : {
                IndexFragment fragment = (IndexFragment) this.getFragmentManager().findFragmentByTag(fragmentTag);
                if(fragment!=null){
                    fragment.UpdateData();
                }
                break;
            }
            case "topic" : {
                TopicFragment fragment = (TopicFragment) this.getFragmentManager().findFragmentByTag(fragmentTag);
                if(fragment!=null){
                    fragment.UpdateData();
                }
                break;
            }
            case "explore" : {
                break;
            }
            default : {
                
            }
        }
    }
    
    private Notification.Builder initNotifyMessage(int count){
        Notification.Builder mBuilder = new Notification.Builder(this)
                                                        .setContentTitle("Downloading...")
                                                        .setContentText("0/" + count)
                                                        .setSmallIcon(R.drawable.ic_launcher)
                                                        .setProgress(count, 0, false);
        return mBuilder;
    }
    
    private void errorNotifyMessage(String url){
        Notification.Builder mBuilder = new Notification.Builder(this)
                                                        .setContentTitle("Download Failed")
                                                        .setContentText(url)
                                                        .setSmallIcon(R.drawable.ic_launcher)
                                                        .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(url, 1, mBuilder.getNotification());
    }
    
    private class DownloadReceiver extends ResultReceiver{
        Notification.Builder mBuilder;
        int mCount;
        int mDownloadedCount;
        
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            switch(resultCode){
                case 0 : {
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    
                    if(mBuilder==null){
                        mCount = 0;
                        mCount += resultData.getInt("totalcount");
                        mBuilder = initNotifyMessage(mCount);
                        mDownloadedCount = 0;
                    } else {
                        if(resultData.containsKey("dismiss")){
                            mBuilder.setAutoCancel(true);
                            mBuilder.setContentTitle("Stop Download");
                            mNotificationManager.notify(0, mBuilder.getNotification());
                            mBuilder = null;
                        } else {
                            if(resultData.containsKey("url")){
                                errorNotifyMessage(resultData.getString("url"));
                            }
                            mCount += resultData.getInt("totalcount");
                            mDownloadedCount += resultData.getInt("incr");
                            if(mDownloadedCount % 10 == 0 || mCount == mDownloadedCount){
                                mBuilder.setContentText(mDownloadedCount + "/" + mCount);
                                mNotificationManager.notify(0, mBuilder.getNotification());
                            }
                        }
                    }
                    break;
                }
                case 1 : {
                    Toast.makeText(MainActivity.this, resultData.getString("url") + " download failed!", Toast.LENGTH_SHORT).show();
                    break;
                }
                default : {
                    Log.e("receiver", "error resultcode");
                }
            }
        }
    }
}
