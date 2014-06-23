package com.simplecontacts;

import java.util.List;
import java.util.Map;

import  android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    private List<? extends Map<String, ?>> mData;
    private SimpleAdapter mAdapter = this;
    
    
    public SimpleButtonAdapter(Context context, 
                               List<? extends Map<String, ?>> data, 
                               int resource, 
                               String[] from, 
                               int[] to){
        super(context, data, resource, from, to);
        mContext = context;
        mResource = resource;
        mData = data;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        final Map<String, Object> p = (Map<String, Object>) this.getItem(position);
        if(v==null){
            v = LayoutInflater.from(mContext).inflate(mResource, null);
        }
        
        if(p!=null){
            final AlertDialog.Builder dg = new AlertDialog.Builder(mContext)
                                        .setTitle("删除")
                                        .setMessage("确定要删除吗？");
            Button btn = (Button) v.findViewById(R.id.contact_button);
            btn.setText(p.get("name").toString());
            btn.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v){
                    final String name = ((Button) v).getText().toString();
                    dg.setPositiveButton("确定", new DialogInterface.OnClickListener(){
                       @Override
                       public void onClick(DialogInterface dialog, int which){

                           ContactDB db = new ContactDB(mContext);
                           SQLiteDatabase sq = db.getReadableDatabase();
                           sq.execSQL("DELETE FROM person WHERE name=?;", 
                                      new String[]{name});
                           sq.close();
                           db.close();
                           mData.remove(mData.indexOf(p));
                           mAdapter.notifyDataSetChanged();
                       }
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
                    return true;
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
