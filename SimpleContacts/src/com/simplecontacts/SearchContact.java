package com.simplecontacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.SearchView;
import android.database.sqlite.SQLiteDatabase;  
import android.database.Cursor;

public class SearchContact extends Activity{
    private ContactDB db;
    
    @Override
    protected void onCreate(Bundle state){
        super.onCreate(state);
        getActionBar().setDisplayShowHomeEnabled(false);  
        getActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.search_contact);
        
        db = new ContactDB(this);
        setData("");
        if(Intent.ACTION_SEARCH.equals(getIntent().getAction())){
            SearchView searchview = (SearchView) findViewById(R.id.search_view);
            searchview.setOnQueryTextListener(querytextlistener);
            searchview.setSubmitButtonEnabled(false);
        }
        
    }
    
    private void setData(String projection){
        String columnName = "name";
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        SQLiteDatabase sq = db.getReadableDatabase();
        Cursor cursor = sq.rawQuery("SELECT name, telephone, address FROM person WHERE name LIKE ?;", 
                                    new String[]{"%" + projection + "%"});
        
        while(cursor.moveToNext()){
            Map<String, Object> mapitem = new HashMap<String, Object>();
            mapitem.put("name", cursor.getString(cursor.getColumnIndex(columnName)));
            list.add(mapitem);
        }
        cursor.close();

        SimpleButtonAdapter listAdapter = new SimpleButtonAdapter(this, 
                list, 
                R.layout.listview_item, 
                new String[]{"name"}, 
                new int[]{R.id.contact_button});

        ListView lv = (ListView) findViewById(R.id.contact_view);
        lv.setAdapter(listAdapter);
    }
    
    SearchView.OnQueryTextListener querytextlistener = new SearchView.OnQueryTextListener(){
        @Override
        public boolean onQueryTextChange(String newText){
            if(TextUtils.isEmpty(newText)){
                setData("");
            }
            else{
                setData(newText);
            }
            return true;
        }
        
        @Override
        public boolean onQueryTextSubmit(String query){
            return false;
        }
        
    };
    
}
