package com.yeyu.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.yeyu.weather.R;

public class CircleLayout extends ViewGroup {
	
	private float mRadius;
	private float mStartAngle;
	private float mSweepAngle;

	public CircleLayout(Context context) {
		this(context, null);
	}
	
	public CircleLayout(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public CircleLayout(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		TypedArray typed = context.obtainStyledAttributes(attrs, R.styleable.CircleLayout);
		try{
			mRadius = typed.getFloat(R.styleable.CircleLayout_radius, 0f);
			mStartAngle = typed.getFloat(R.styleable.CircleLayout_startAngle, 0f);
			mSweepAngle = typed.getFloat(R.styleable.CircleLayout_sweepAngle, 0f);
		} finally {
			typed.recycle();
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int count = getChildCount();
		int halfwidth = getWidth() / 3;
		int halfheight = getHeight() / 3;
		float interAngle = mSweepAngle / (count - 1);
		if(count<1){
			return;
		}
		View centerchild = getChildAt(0);
		ViewGroup.LayoutParams para = centerchild.getLayoutParams();
		int centerwidth = para.width;
		int centerheight = para.height;
		int centerL = halfwidth - centerwidth / 2;
		int centerR = halfwidth + centerwidth / 2;
		int centerT = halfheight - centerheight / 2;
		int centerB = halfheight + centerheight / 2;
		centerchild.layout(centerL, centerT, centerR, centerB);
		
		int[] pos;
		for(int i=1;i<count;i++){
			View child = getChildAt(i);
			if(child==null){
				continue;
			}
			pos = getChildPos(halfwidth, 
							  halfheight, 
							  child.getLayoutParams().width,
							  child.getLayoutParams().height,
							  mStartAngle + interAngle * (i - 1));
			child.layout(pos[0], pos[1], pos[2], pos[3]);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		for(int i=0;i<getChildCount();i++){
			measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
		}
	}
	
	private int[] getChildPos(int centerPosX, int centerPosY, int childwidth, int childheight, float Angle){
		int[] pos = new int[4];
		int childPosX = (int)(centerPosX + mRadius * Math.cos(angleToRadian(Angle)));
		int childPosY = (int)(centerPosY + mRadius * Math.sin(angleToRadian(Angle)));
		pos[0] = childPosX - childwidth / 2;
		pos[1] = childPosY - childheight / 2;
		pos[2] = childPosX + childwidth / 2;
		pos[3] = childPosY + childheight / 2;
		return pos;
	}

	private double angleToRadian(float angle){
		return (angle * Math.PI / 180);
	}

}
