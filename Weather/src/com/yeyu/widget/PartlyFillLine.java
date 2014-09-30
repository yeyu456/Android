package com.yeyu.widget;

import com.yeyu.weather.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class PartlyFillLine extends View {

    private int mBackgroundColor;
    private int mLineColor;
    private int mLineLength;
    private int mFillLength;
    private String mTextHeader;
    private String mTextTailer;

	public PartlyFillLine(Context context) {
		this(context, null);
	}
	
	public PartlyFillLine(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public PartlyFillLine(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.PartlyFillLine);
		try{
			mBackgroundColor = type.getColor(R.styleable.PartlyFillLine_backgroundColor, 0xFF848484);
		    mLineColor = type.getColor(R.styleable.PartlyFillLine_lineColor, 0x51D05A);
		    mLineLength = type.getDimensionPixelSize(R.styleable.PartlyFillLine_lineLength, 100);
		    mFillLength = type.getDimensionPixelSize(R.styleable.PartlyFillLine_fillLength, 0);
		    mTextHeader = type.getString(R.styleable.PartlyFillLine_textHeader);
		    mTextTailer = type.getString(R.styleable.PartlyFillLine_textTailer);
		} finally {
			type.recycle();
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) getLayoutParams();
		Paint p = new Paint();
		p.setColor(0xFF848484);
		p.setTextSize(canvas.getHeight()/3);
		p.setTextAlign(Align.LEFT);
		p.setTypeface(Typeface.DEFAULT);
		int headTextLength;
		if(mTextHeader!=null){
			canvas.drawText(mTextHeader, lp.leftMargin, canvas.getHeight()/3, p);
			headTextLength = (int)p.measureText(mTextHeader) + lp.leftMargin + 1;
		} else {
			headTextLength = lp.leftMargin + 1;
		}
		
		Rect rect = new Rect(headTextLength, 0, mLineLength+headTextLength, canvas.getHeight()/3);
		Rect rect2 = new Rect(headTextLength, 0, mFillLength+headTextLength, canvas.getHeight()/3);
		Paint p2 = new Paint();
		p2.setColor(mBackgroundColor);
		Paint p3 = new Paint();
		p3.setColor(mLineColor);
		canvas.drawRect(rect, p2);
		canvas.drawRect(rect2, p3);
		if(mTextTailer!=null){
			canvas.drawText(mTextTailer, mLineLength+headTextLength+10, canvas.getHeight()/3, p);
		}
	}
	
	public void setText(String header, String tailer){
		mTextHeader = header;
		mTextTailer = tailer;
		invalidate();
	}
	
	public void setFillPercentage(float percentage){
		mFillLength = (int)(mLineLength * percentage);
		invalidate();
	}
}
