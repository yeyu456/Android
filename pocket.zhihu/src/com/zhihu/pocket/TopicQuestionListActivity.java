package com.zhihu.pocket;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class TopicQuestionListActivity extends Activity {
    SimpleAdapter mAdapter;
    ListView mListView;
    ArrayList<Map<String, String>> mData = new ArrayList<Map<String, String>>();
    String mPath;
    
    @Override
    protected void onCreate(Bundle state){
        super.onCreate(state);
        this.setContentView(R.layout.index_list);
        mListView = (ListView) findViewById(R.id.index_list);
        mPath = getIntent().getStringExtra("file");
        String actionbarTitle = getIntent().getStringExtra("title");
        this.getActionBar().setTitle(actionbarTitle);
        final SwipeRefreshLayout mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
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
                Intent intent = new Intent(TopicQuestionListActivity.this, QuestionActivity.class);
                intent.putExtra("file", path);
                intent.putExtra("title", title);
                startActivity(intent);
            }
        });
        UpdateData();
    }
    
    public void setAdapter(){
        mAdapter = new SimpleAdapter(this, 
                                     mData, 
                                     R.layout.index_list_item, 
                                     new String[]{"file", "title"}, 
                                     new int[]{R.id.list_item_file, R.id.list_item_title});
        mListView.setAdapter(mAdapter);
    }
    
    public void UpdateData(){
        File dir = new File(mPath);
        if(dir.exists()){
            XMLOperation.xmlParse(dir, mData);
        }
        if(mData!=null){
            if(mAdapter!=null){
                mAdapter.notifyDataSetChanged();
            } else {
                setAdapter();
            }
        }
    }
}
