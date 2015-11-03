package com.tyq_code.get11.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.tyq_code.get11.Activity.GameActivity;
import com.tyq_code.get11.Controller.Definition;

public class CheckView extends View {
    public  boolean isYes     = false;//switcher on/off
    public  boolean isClicked = false;
    private final int total = 5;
    private       int time  = 0;
    private int mScreenWidth  = Definition.mScreenWidth;
    private int mScreenHeight = Definition.mScreenHeight;
    private RectF smallRectF, bigRectF;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public CheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint.setStyle(Paint.Style.FILL);
        bigRectF   = new RectF(0, 0, mScreenWidth / 7    , mScreenHeight / 20);
        smallRectF = new RectF(4, 4, mScreenWidth / 7 - 4, mScreenHeight / 20 - 4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //bigRectF
        mPaint.setColor(0xff99b717);
        canvas.drawRoundRect(bigRectF, mScreenHeight / 40, mScreenHeight / 40, mPaint);
        //smallRectF
        mPaint.setColor(Definition.nightMode ? Definition.darkColor : Definition.lightColor);
        canvas.drawRoundRect(smallRectF, mScreenHeight / 40 - 2, mScreenHeight / 40 - 2, mPaint);
        //switcher
        mPaint.setColor(0xff99b717);
        if(!isClicked){
            if (!isYes)
                canvas.drawCircle(2 + mScreenHeight / 40, mScreenHeight / 40, mScreenHeight / 40 - 6, mPaint);
            else
                canvas.drawCircle(mScreenWidth / 7 - mScreenHeight / 40 - 2, mScreenHeight / 40, mScreenHeight / 40 - 6, mPaint);
        } else {
            if (isYes)
                canvas.drawCircle(2 + mScreenHeight / 40 + time * (mScreenWidth / 7 - mScreenHeight / 20 - 4) / total, mScreenHeight / 40, mScreenHeight / 40 - 6, mPaint);
            else
                canvas.drawCircle(mScreenWidth / 7 - mScreenHeight / 40 - 2 - time * (mScreenWidth / 7 - mScreenHeight / 20 - 4) / total, mScreenHeight / 40, mScreenHeight / 40 - 6, mPaint);
            if (time++ < total) invalidate();
            else time = 0;
        }
        super.onDraw(canvas);
    }
}
