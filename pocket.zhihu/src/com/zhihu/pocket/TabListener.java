package com.zhihu.pocket;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class TabListener<T extends Fragment> implements ActionBar.TabListener{
    private Fragment mFragment;
    private final Activity mActivity;
    private final String mTag;
    private final Class<T> mClass;
    
    public TabListener(Activity activity, String tag, Class<T> clz){
        mActivity = activity;
        mTag = tag;
        mClass = clz;
    }
    
    @Override
    public void onTabReselected (ActionBar.Tab tab, FragmentTransaction ft){
        //do nothing
    }
    
    @Override
    public void onTabSelected (ActionBar.Tab tab, FragmentTransaction ft){
        System.out.println("Tab Selected " + tab.getText());
        if (mFragment == null) {
            // If not, instantiate and add it to the activity
            mFragment = Fragment.instantiate(mActivity, mClass.getName());
            ft.add(android.R.id.content, mFragment, mTag);
        } else {
            // If it exists, simply attach it in order to show it
            ft.attach(mFragment);
        }
    }
    
    @Override
    public void onTabUnselected (ActionBar.Tab tab, FragmentTransaction ft){
        System.out.println("Tab Unselected " + tab.getText());
        if (mFragment != null) {
            // Detach the fragment, because another one is being attached
            ft.detach(mFragment);
        }
    }
}
