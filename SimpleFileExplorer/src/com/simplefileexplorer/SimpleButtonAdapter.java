package com.simplefileexplorer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.SimpleAdapter;

public class SimpleButtonAdapter extends SimpleAdapter {
    
    private Context mContext;
    private int mResource;
    private List<Map<String, String>> mData;
    private LruCache<String, Bitmap> mMemoryCache;
    
    public SimpleButtonAdapter(Context context,
                               List<Map<String, String>> data, 
                               int resource, 
                               String[] from, 
                               int[] to){
        super(context, data, resource, from, to);
        mContext = context;
        mResource = resource;
        mData = data;
        final int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
        Collections.sort(mData, new fileNameCompare());
    }
    
    class fileNameCompare implements Comparator<Map<String, String>> {
        public int compare(Map<String, String> t1, Map<String, String> t2){
            return t1.get("name").compareTo(t2.get("name"));
        }
    }
    
    public class Holder {
        public String path;
        public Button b;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Holder h = new Holder();
        if(convertView==null){
            convertView = (LayoutInflater.from(mContext).inflate(mResource, null));
            convertView.setTag(h);
        } else {
            h = (Holder) convertView.getTag();
        }
        
        Map<String, String> p = (Map<String, String>) this.getItem(position);
        if(p!=null){
            h.path = p.get("path");
            h.b = (Button) convertView.findViewById(R.id.file_list_button);
            Spannable wordtoSpan = new SpannableString(p.get("name") + "\n" + p.get("atr")); 
            wordtoSpan.setSpan(new RelativeSizeSpan(1.0f), 
                               0, 
                               p.get("name").length(), 
                               Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            
            wordtoSpan.setSpan(new RelativeSizeSpan(0.5f), 
                               p.get("name").length(), 
                              (p.get("name").length() + p.get("atr").length() + 1), 
                               Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            h.b.setText(wordtoSpan);
            h.b.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources()
                                                                .getDrawable(Integer.parseInt(p.get("image"))), 
                                                        null, null, null);
            if(p.get("ext").matches("image/.*")){
                if(mMemoryCache.get(h.path)!=null){
                    h.b.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(mContext.getResources(), mMemoryCache.get(h.path)), 
                                                                null, null, null);
                } else {
                    new GetBitmapTask(convertView, h.path).execute();
                }
            }
        }
        return convertView;
    }
    
    class GetBitmapTask extends AsyncTask<Void, Void, Bitmap>{
        private View mView;
        private String mPath;
        public GetBitmapTask(View v, String path){
            mView = v;
            mPath = path;
        }
        
        @Override
        protected Bitmap doInBackground(Void... params){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mPath, options);
            int tmp_width = options.outWidth;
            int tmp_height = options.outHeight;
            
            //In case of empty pictures
            if(tmp_width<0 || tmp_height<0){
                return null;
            }
            int be = 1;
            while(tmp_width>100 || tmp_height>100){
                tmp_width /= 2;
                tmp_height /= 2;
                be *= 2; 
            }
            options.inSampleSize = be;
            options.inJustDecodeBounds = false;
            Bitmap bm = BitmapFactory.decodeFile(mPath, options);
            mMemoryCache.put(mPath, bm);
            return bm;
        }
        
        @Override
        protected void onPostExecute(Bitmap bm){
            if(((Holder) mView.getTag()).path == mPath
               && bm!=null){
                ((Holder) mView.getTag()).b
                                         .setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(mContext.getResources(), bm), 
                                                                                  null, null, null);
            }
        }
    }
    
    public void remove(int position){
        mData.remove(position);
        this.notifyDataSetChanged();
    }
    
    public void edit(int position, String path, String name){
        mData.get(position).put("path", path);
        mData.get(position).put("name", name);
        this.notifyDataSetChanged();
    }
    
    public void add(Map<String, String> item){
        mData.add(item);
        this.notifyDataSetChanged();
    }
    
    public void empty(){
        mData.clear();
    }
}
