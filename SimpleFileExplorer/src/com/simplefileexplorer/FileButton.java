package com.simplefileexplorer;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.widget.Button;

public class FileButton extends Button {
    
    public String mAtr = "";
    public String mPath = "";
    
    public FileButton(Context context){
        super(context);
    }
    
    public FileButton(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    
    public FileButton(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }
    
    public final void setButtonText(CharSequence text){
        Spannable wordtoSpan = new SpannableString(text + "\n" + mAtr); 
        
        wordtoSpan.setSpan(new RelativeSizeSpan(1.0f), 
                           0, 
                           text.length(), 
                           Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        wordtoSpan.setSpan(new RelativeSizeSpan(0.5f), 
                           text.length(), 
                           (text.length() + mAtr.length() + 1), 
                           Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        super.setText(wordtoSpan);
    }
}
