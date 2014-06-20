package com.simplecontacts;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

public class AddContact extends Activity {
    private final ContactDB db = new ContactDB(this);
    private TextView name;
    private TextView tel;
    private TextView addr;
    
    @Override
    protected void onCreate(Bundle state){
        super.onCreate(state);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        ViewGroup vg = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.add_contact_title, null);
        getActionBar().setCustomView(vg, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setContentView(R.layout.add_contact);

        name = (TextView) findViewById(R.id.edit_name);
        tel = (TextView) findViewById(R.id.edit_tel);
        addr = (TextView) findViewById(R.id.edit_addr);
        
        Intent intent = this.getIntent();
        if(intent.getBundleExtra("profile")!=null){
            state = intent.getBundleExtra("profile");
        }
        
        if(state!=null){
            name.setText(state.getCharSequence("name"));
            tel.setText(state.getCharSequence("tel"));
            addr.setText(state.getCharSequence("addr"));
        }
        
    }
    
    @Override
    protected void onSaveInstanceState(Bundle state){
       super.onSaveInstanceState(state);
       state.putCharSequence("name", name.getText());
       state.putCharSequence("tel", tel.getText());
       state.putCharSequence("addr", addr.getText());
    }
    
    public void onSaveData(View v){
        SQLiteDatabase sq = db.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(!name.getText().toString().trim().isEmpty()
           &&tel.getText().toString().matches("\\d+")){
            values.put("name", name.getText().toString().trim());
            values.put("telephone", tel.getText().toString());
            values.put("address", addr.getText().toString());
            values.put("picture", R.drawable.ic_launcher);
            sq.insert(ContactDB.TABLE_NAME, null, values);
            this.finish();
        }
        else{
            Toast.makeText(this, "ERROR INPUT", Toast.LENGTH_SHORT).show();
        }
    }
    
    public void onCancel(View v){
        this.finish();
    }
}
