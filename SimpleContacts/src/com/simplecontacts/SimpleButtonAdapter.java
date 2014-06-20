package com.simplecontacts;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.SimpleAdapter;

public class SimpleButtonAdapter extends SimpleAdapter {
    private Context mContext;
    private int mResource;
    
    public SimpleButtonAdapter(Context context, 
                               List<? extends Map<String, ?>> data, 
                               int resource, 
                               String[] from, 
                               int[] to){
        super(context, data, resource, from, to);
        mContext = context;
        mResource = resource;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        Map<String, Object> p = (Map<String, Object>) this.getItem(position);
        if(v==null){
            v = LayoutInflater.from(mContext).inflate(mResource, null);
        }
        
        if(p!=null){
            Button btn = (Button) v.findViewById(R.id.contact_button);
            btn.setText(p.get("name").toString());
            btn.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v){
                    return false;
                }
            });

            btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    ContactDB db = new ContactDB(mContext);
                    String name = ((Button) v).getText().toString();
                    Intent editContact = new Intent(mContext, AddContact.class);
                    Bundle profile = new Bundle();
                    SQLiteDatabase sq = db.getReadableDatabase();
                    Cursor cursor = sq.rawQuery("SELECT name, telephone, address FROM person WHERE name=?;", 
                                                new String[]{name});
                    if(cursor.moveToNext()){
                        profile.putCharSequence("name", cursor.getString(cursor.getColumnIndex("name")));
                        profile.putCharSequence("tel", cursor.getString(cursor.getColumnIndex("telephone")));
                        profile.putCharSequence("addr", cursor.getString(cursor.getColumnIndex("address")));
                        editContact.putExtra("profile", profile);
                    }
                    else{
                        Log.e("1", "NO DATA " + name);
                    }
                    cursor.close();
                    db.close();
                    mContext.startActivity(editContact);
                }
            });
        }
        return v;
    }
}
