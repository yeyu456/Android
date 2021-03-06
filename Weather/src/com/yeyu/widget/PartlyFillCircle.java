package com.yeyu.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.Button;

import com.yeyu.weather.R;

public class PartlyFillCircle extends Button {
	
	private int mColorFill;
	private float mColorPercentage;
	private float mColorPercentageMax;
	Paint paint;
	Paint paint2;
	
	public PartlyFillCircle(Context context) {
		this(context, null);
	}

	public PartlyFillCircle(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public PartlyFillCircle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.PartlyFillCircle);
		
		try{
			mColorFill = type.getColor(R.styleable.PartlyFillCircle_colorFill, 0xFFFFFF);
			mColorPercentage = type.getFloat(R.styleable.PartlyFillCircle_colorPercentage, 0f);
			mColorPercentageMax = type.getFloat(R.styleable.PartlyFillCircle_colorPercentageMax, 100f);
		} finally {
			type.recycle();
		}
		isLegal();
		init();
	}
	
	private void init(){
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.FILL);
		paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.FILL);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		float diameter = (float) this.getWidth();
		float sweepAngle = getSweepAngle();
		float startAngle = getStartAngle(sweepAngle);
		RectF rec = new RectF(0f, 0f, diameter, diameter);
		paint.setColor(mColorFill);
		paint2.setColor(0xFFFFFF);
		canvas.drawArc(rec, startAngle, sweepAngle, false, paint);
		float start2 = startAngle + sweepAngle;
		float sweep2 = 360 - sweepAngle;
		canvas.drawArc(rec, start2, sweep2, false, paint2);
		super.onDraw(canvas);
	}
	
	public void setColorFill(int color){
		mColorFill = color;
		invalidate();
	}
	
	public void setColorPercentageAndMax(float p, float m){
		mColorPercentage = p;
		mColorPercentageMax = m;
		isLegal();
		invalidate();
	}
	
	private void isLegal(){
		if(mColorPercentage>mColorPercentageMax||mColorPercentage<0){
			throw new IllegalArgumentException("colorPercentage must larger than 0 and less than the tha max");
		}
		if(mColorPercentageMax<=0){
			throw new IllegalArgumentException("colorPercentageMax must larger than 0");
		}
	}
	
	private float getSweepAngle(){
		return (360f * mColorPercentage / mColorPercentageMax);
	}
	
	private float getStartAngle(float sweepAngle){
		if(sweepAngle!=180){
			return (90 - sweepAngle / 2);
		} else {
			return 0f;
		}
	}
}
