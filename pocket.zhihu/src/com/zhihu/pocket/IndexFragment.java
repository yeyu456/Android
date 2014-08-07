package com.zhihu.pocket;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class IndexFragment extends Fragment{
    SimpleAdapter mAdapter;
    ListView mListView;
    ArrayList<Map<String, String>> mData = new ArrayList<Map<String, String>>();
    String mPath;
    SwipeRefreshLayout mSwipeLayout;
    
    @Override
    public void onCreate(Bundle state){
        super.onCreate(state);
        this.UpdateData();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state){
        View v = inflater.inflate(R.layout.index_list, container, false);
        mListView = (ListView) v.findViewById(R.id.index_list);
        mListView.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return IndexFragment.this.getActivity().onTouchEvent(event);
            }
        });
        mSwipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                UpdateData();
                mSwipeLayout.setRefreshing(false);
            }
        });
        mSwipeLayout.setColorSchemeResources(R.color.blue, R.color.green, R.color.orange, R.color.red);
        mListView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id){
                String path = ((Map<String, String>) parent.getItemAtPosition(position)).get("file");
                System.out.println(path);
                String title = ((Map<String, String>) parent.getItemAtPosition(position)).get("title");
                System.out.println(title);
                Intent intent = new Intent(IndexFragment.this.getActivity(), QuestionActivity.class);
                intent.putExtra("file", path);
                intent.putExtra("title", title);
                IndexFragment.this.startActivity(intent);
            }
        });
        if(mData!=null){
            setAdapter();
        }
        return v;
    }
    
    public void setAdapter(){
        mAdapter = new SimpleAdapter(this.getActivity(), 
                                     mData, 
                                     R.layout.index_list_item, 
                                     new String[]{"file", "title"}, 
                                     new int[]{R.id.list_item_file, R.id.list_item_title});
        mListView.setAdapter(mAdapter);
    }
    
    public void UpdateData(){
        mPath = ((MainActivity) this.getActivity()).mExternalPath + "/" + this.getActivity().getResources().getString(R.string.dir_index);
        File dir = new File(mPath);
        if(dir.exists()){
            HTMLOperation.xmlParse(dir, mData);
        }
        if(mData!=null&&mListView!=null){
            if(mAdapter!=null){
                mAdapter.notifyDataSetChanged();
            } else {
                setAdapter();
            }
        }
    }
}
