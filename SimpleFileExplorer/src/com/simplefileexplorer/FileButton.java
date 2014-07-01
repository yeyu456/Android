package com.simplefileexplorer;

import java.util.Map;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.widget.Button;

public class FileButton extends Button {
    
    public FileButton(Context context){
        super(context);
    }
    
    public FileButton(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    
    public FileButton(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }
    
    public final void setButtonText(Map<String, String> mProperty){
        Spannable wordtoSpan = new SpannableString(mProperty.get("name") + "\n" + mProperty.get("atr")); 
        
        wordtoSpan.setSpan(new RelativeSizeSpan(1.0f), 
                           0, 
                           mProperty.get("name").length(), 
                           Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        wordtoSpan.setSpan(new RelativeSizeSpan(0.5f), 
        				   mProperty.get("name").length(), 
                          (mProperty.get("name").length() + mProperty.get("atr").length() + 1), 
                           Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        super.setText(wordtoSpan);
    }
}
