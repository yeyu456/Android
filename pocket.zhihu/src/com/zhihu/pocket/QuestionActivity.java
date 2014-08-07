package com.zhihu.pocket;

import java.io.File;

import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.MotionEvent;
import android.text.Editable;
import android.text.Html;
import android.text.Html.TagHandler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class QuestionActivity extends Activity {
    File mFile; 
    
    @Override
    protected void onCreate(Bundle state){
        super.onCreate(state);
        this.setContentView(R.layout.question_page);
        String file = this.getIntent().getStringExtra("file");
        String title = this.getIntent().getStringExtra("title");
        mFile = new File(file);
        if(!mFile.exists()){
            Log.e("question", "file not exist " + file);
            this.finish();
            return;
        }
        TextView titleView = (TextView) this.findViewById(R.id.question_title);
        titleView.setText(title);
        (new ViewTask()).execute();
    }
    
    private class ViewTask extends AsyncTask<Void, Void, Elements>{
        ProgressDialog mProgressDialog;
        
        @Override
        protected void onPreExecute(){
            mProgressDialog = new ProgressDialog(QuestionActivity.this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
        
        @Override
        protected Elements doInBackground(Void... params){
            return HTMLOperation.getElements(mFile);
        }
        
        protected void onPostExecute(Elements els) {
            TextView detailView = (TextView) QuestionActivity.this.findViewById(R.id.question_detail);
            String detail = els.remove(0).ownText();
            messureDetailText(detail, detailView);
            final TextView answerView = (TextView) QuestionActivity.this.findViewById(R.id.question_answerlist);
            answerView.setMovementMethod(new ScrollingMovementMethod());
            TextView countView = (TextView) QuestionActivity.this.findViewById(R.id.question_answercounts);
            if(els!=null&&els.size()>0){
                final SetAnswerText set = new SetAnswerText(els, answerView, countView, mFile.getParent());
                answerView.setOnTouchListener(new View.OnTouchListener(){
                    @Override
                    public boolean onTouch(View v, MotionEvent event){
                        int length = answerView.getRight() - answerView.getLeft();
                        int left = length / 4;
                        int right = length / 4 * 3;
                        int X = 0;
                        if(MotionEvent.ACTION_DOWN == event.getAction()){
                            X = (int) event.getX();
                            if(X<left){
                                set.next(-1);
                            }
                            if(X>right){
                                set.next(1);
                            }
                            Log.e("pos","X " + X + " " + left + "," + right);
                        }
                        return false;
                    }
                });
            } else {
                countView.setText("0个回答");
            }
            mProgressDialog.dismiss();
        }
    }
    
    private void messureDetailText(String text, final TextView detailView){
        Rect bounds = new Rect();
        Rect screenbounds = new Rect();
        Paint paint = new Paint();
        float scaledDensity = this.getResources().getDisplayMetrics().scaledDensity;
        paint.setTextSize(13 * scaledDensity);
        paint.getTextBounds(text, 0, text.length(), bounds);
        
        int width = (int) Math.ceil(bounds.width());
        int height = (int) Math.ceil(bounds.height());
        final int line = (int) Math.ceil((double)width / (double)detailView.getWidth());
        
        this.getWindowManager().getDefaultDisplay().getRectSize(screenbounds);
        int screenheight = screenbounds.height();
        final int screenmaxline = screenheight / height;
        
        System.out.println("screen max line " + screenmaxline);
        System.out.println(" line " + line + " text width" + width + " textview width " + detailView.getWidth());
        
        if(line>=5){
            Button button = (Button) this.findViewById(R.id.question_detail_show);
            button.setVisibility(Button.VISIBLE);
            final String display = this.getResources().getString(R.string.question_detail_show);
            final String hide = this.getResources().getString(R.string.question_detail_hide);
            button.setOnClickListener(new View.OnClickListener() {
                boolean isdisplay = false;
                @Override
                public void onClick(View v) {
                    if(!isdisplay){
                        if(line>((int)screenmaxline/2)){
                            detailView.setMovementMethod(ScrollingMovementMethod.getInstance());
                        }
                        detailView.setMaxLines(screenmaxline / 2);
                        isdisplay = true;
                        ((Button) v).setText(hide);
                    } else {
                        if(line>((int)screenmaxline/2)){
                            detailView.setMovementMethod(null);
                        }
                        detailView.setMaxLines(5);
                        isdisplay = false;
                        ((Button) v).setText(display);
                    }
                }
            });
        }
        detailView.setText(text);
    }
    
    private class SetAnswerText{
        int mCount;
        int mCurrentCount;
        Elements mElements;
        TextView mTextView;
        TextView mCountView;
        String mParent;
        
        public SetAnswerText(Elements elements, TextView textview, TextView countView, String parentPath){
            
            mCount = elements.size();
            mElements = elements;
            mTextView = textview;
            mCountView = countView;
            mCurrentCount = 0;
            mParent = parentPath;
            mTextView.setText(Html.fromHtml(mElements.get(mCurrentCount).html(), new ImageGetter(mParent), null));
            mCountView.setText(mCount + "个回答 " + "当前：" + (mCurrentCount+1));
            mTextView.setScrollY(0);
        }
        
        public void next(int value){
            mCurrentCount += value;
            Log.e("count", " " + mCurrentCount);
            if(mCurrentCount==mCount){
                mCurrentCount -= mCount;
            } else {
                if(mCurrentCount<0){
                    mCurrentCount += mCount;
                }
            }
            mCountView.setText(mCount + "个回答 当前：" + (mCurrentCount+1));
            mTextView.setText(Html.fromHtml(mElements.get(mCurrentCount).html(), new ImageGetter(mParent), null));
            mTextView.setScrollY(0);
        }
    }
    
    private class ImageGetter implements Html.ImageGetter {
        String mParent;
        
        public ImageGetter(String parent){
            mParent = parent;
        }
        
        @Override
        public Drawable getDrawable(String source) {
            File draw = new File(mParent, source);
            Log.e("getter", mParent + " " + source);
            Log.e("getter", draw.getPath());
            if (draw.exists()&&draw.length()!=0) {
                if(draw.length()>(512 * 1024)){
                    Log.e("getter large image", draw.getPath() + " size " + draw.length()/1024);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(draw.getPath(), options);
                    int tmp_width = options.outWidth;
                    int tmp_height = options.outHeight;
                    Log.e("getter large image", tmp_width + " " + tmp_height);
                    //In case of empty pictures
                    if(tmp_width<0 || tmp_height<0){
                        return null;
                    }
                    int be = 1;
                    while(tmp_width>300 && tmp_height>300){
                        tmp_width /= 2;
                        tmp_height /= 2;
                        be *= 2; 
                    }
                    options.inSampleSize = be;
                    options.inJustDecodeBounds = false;
                    Bitmap bm = BitmapFactory.decodeFile(draw.getPath(), options);
                    Drawable d = new BitmapDrawable(QuestionActivity.this.getResources(), bm);
                    d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
                    return d;
                } else {
                    Drawable d = Drawable.createFromPath(mParent + "/" + source); 
                    d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
                    return d;
                }
            }
            else {
                Drawable d  = QuestionActivity.this.getResources().getDrawable(R.drawable.image_not_found);
                d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
                return d;
            }
        }
    }
    
}
