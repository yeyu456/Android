package com.simplecontacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.ListView;
import android.database.sqlite.SQLiteDatabase;  
import android.database.Cursor;


public class MainActivity extends Activity {
    private ContactDB db;
    
    @Override
    protected void onCreate(Bundle state){
        super.onCreate(state);
        setContentView(R.layout.main_activity);
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        db = new ContactDB(this);
        SimpleButtonAdapter listAdapter = new SimpleButtonAdapter(this, 
                this.getData(), 
                R.layout.listview_item, 
                new String[]{"name"}, 
                new int[]{R.id.contact_button});
        
        ListView lv = (ListView) findViewById(R.id.main_view);
        lv.setAdapter(listAdapter);
        db.close();
    }
    
    private List<Map<String, Object>> getData(){
        String columnName = "name";
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        SQLiteDatabase sq = db.getReadableDatabase();
        Cursor cursor = sq.query(ContactDB.TABLE_NAME, 
                                 new String[]{columnName}, 
                                 null, null, null, null, null, null);
        
        while(cursor.moveToNext()){
            Map<String, Object> mapitem = new HashMap<String, Object>();
            mapitem.put("name", cursor.getString(cursor.getColumnIndex(columnName)));
            list.add(mapitem);
        }
        cursor.close();
        return list;
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.add_contact :
                onAddContact();
                break;
            case R.id.search_contact :
                onSearchContact();
                break;
            default :
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    
    public void onAddContact(){
        Intent addContact = new Intent(this, AddContact.class);
        startActivity(addContact);
    }
    
    public void onSearchContact(){
        Intent searchContact = new Intent(this, SearchContact.class);
        searchContact.setAction(Intent.ACTION_SEARCH);
        startActivity(searchContact);
    }
}
