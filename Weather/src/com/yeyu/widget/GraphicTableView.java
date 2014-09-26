package com.yeyu.widget;

import java.util.ArrayList;

import com.yeyu.weather.R;
import static com.yeyu.weather.WeatherConstant.*;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

public class GraphicTableView extends View {
	
	private int mX;
	private int mY;
	private int mXInter;
	private int mYInter;
	
	private Path mPath;
	private Paint axisPaint;
	private Paint pathPaint;
	private ArrayList<Point> mData;
	private ArrayList<Integer> mNo;
	private String mSign;
	

	public GraphicTableView(Context context) {
		this(context, null);
	}
	
	public GraphicTableView(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}

	public GraphicTableView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.GraphicTableView);
		
		try{
			mX = type.getDimensionPixelSize(R.styleable.GraphicTableView_axisX, 0);
			mY = type.getDimensionPixelSize(R.styleable.GraphicTableView_axisY, 0);
			mXInter = type.getDimensionPixelSize(R.styleable.GraphicTableView_axisXInternal, 0);
			mYInter = type.getDimensionPixelSize(R.styleable.GraphicTableView_axisYInternal, 0);
		} finally {
			type.recycle();
		}
		
		init();
	}
	
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		int w = getWidth();
		int h = getHeight();
		isLegal(w, h);
		
		String text;
		int left = (w - mX) / 2;
		int top = (h - mY) / 2;
		int right = left + mX;
		int bottom = top + mY;
		
		canvas.drawLine(left, top, left, bottom, axisPaint);
		for(Point p:mData){
			canvas.drawCircle(p.x+left, bottom-p.y, 2f, pathPaint);
			canvas.drawText(mNo.get(mData.indexOf(p)).intValue() + mSign, p.x+left, bottom-p.y-5, pathPaint);
		}
	}
	
	public void drawPoints(ArrayList<Integer> number, String sign){
		mNo = number;
		for(int n=0;n<number.size();n++){
			Point p = new Point((n + 1) * mXInter, number.get(n) * mYInter);
			mData.add(p);
		}
		mSign = sign;
		invalidate();
	}
	
	public void setYInter(int inter){
		mYInter = inter;
		invalidate();
	}
	
	private void init(){
		axisPaint = new Paint();
		axisPaint.setAntiAlias(true);
		axisPaint.setColor(0x88000000);
		mPath = new Path();
		pathPaint = new Paint();
		pathPaint.setAntiAlias(true);
		pathPaint.setColor(0xFF000000);
		mData = new ArrayList<Point>();
	}
	
	private void isLegal(int width, int height){
		if(mX<mXInter||mY<mYInter){
			throw new IllegalArgumentException("Internal Length must be less than Axis Length");
		}
		if(mX<=0 || mY<=0){
			throw new IllegalArgumentException("Axis Length must be larger than 0");
		}
		if(mX>width){
			throw new IllegalArgumentException("Axis X's length is too large");
		}
		if(mY>height){
			throw new IllegalArgumentException("Axis Y's length is too large");
		}
	}
}
