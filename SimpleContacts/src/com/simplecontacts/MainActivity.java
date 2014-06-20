package com.simplecontacts;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;


public class MainActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle state){
        super.onCreate(state);
        setContentView(R.layout.main_activity);
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        SearchContact.setData("", R.id.main_view, this);
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
