package com.zhihu.pocket;

import java.io.File;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
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
	    ActionBar actionbar = this.getActionBar();
	    int pos = actionbar.getSelectedTab().getPosition();
	    System.out.println("tab pos " + pos);
	    if(dis>150){
	        pos += 1;
	        if(pos==3){
	            pos = 0;
	        }
	        actionbar.selectTab(actionbar.getTabAt(pos));
	        return true;
	    }
	    if(dis<-150){
	        pos -= 1;
	        if(pos==-1){
	            pos = 2;
	        }
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
                       .setTabListener(new TabListener<Fragment>(this, "topic", Fragment.class));
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
        if(!mBound){
            Intent intent = new Intent(MainActivity.this, DownloadService.class);
            intent.putExtra("receiver", new DownloadReceiver(new Handler()));
            intent.putExtra("externalpath", mExternalPath);
            this.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        } else {
            Toast.makeText(this, "下载已经启动", Toast.LENGTH_SHORT).show();
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
        IndexFragment fragment = (IndexFragment) this.getFragmentManager().findFragmentByTag(fragmentTag);
        switch(fragmentTag){
            case "index" : {
                
                break;
            }
            case "topic" : {
                break;
            }
            case "explore" : {
                break;
            }
            default : {
                
            }
        }
        
        if(fragment!=null){
            fragment.UpdataData();
        }
    }
    
    private class DownloadReceiver extends ResultReceiver{
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            switch(resultCode){
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
