package com.tyq_code.get11.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.tyq_code.get11.Controller.Definition;
import com.tyq_code.get11.R;

public class ButtonView extends View {

    private int len = Definition.mScreenWidth / 8;
    private int num;
    private Bitmap bitmap;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ButtonView(Context context, int num) {
        super(context);
        this.num = num;
    }

    public ButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        switch (num){
            case Definition.PAUSE:
                mPaint.setColor(Definition.greenColor);
                canvas.drawCircle(len / 2, len / 2, len / 2 - 3, mPaint);
                Path path = new Path();
                path.moveTo(len / 3, len / 4);
                path.lineTo(len / 3, len * 3 / 4);
                path.moveTo(len * 2 / 3, len / 4);
                path.lineTo(len * 2 / 3, len * 3 / 4);
                canvas.drawPath(path, mPaint);
                break;
            case Definition.QUESTION:
                mPaint.setColor(Definition.greenColor);
                canvas.drawCircle(len / 2, len / 2, len / 2 - 3, mPaint);
                canvas.drawCircle(len / 2, len * 3 / 16 + len / 6, len / 6, mPaint);
                canvas.drawLine(len / 2, len * 3 / 16 + len / 3 - 5 / 2, len / 2, len * 3 / 16 + len / 3 + len / 8, mPaint);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(Definition.nightMode ? Definition.darkColor : Definition.lightColor);
                canvas.drawRect(len / 3 - 3, len * 3 / 16 + len / 6, len / 2 - 2.5F, len * 3 / 16 + len / 3 + 2.5F, mPaint);
                mPaint.setColor(Definition.greenColor);
                canvas.drawCircle(len / 2, len * 3 / 4, 4, mPaint);
                break;
            case Definition.UNDO:
                mPaint.setColor(Definition.color[0]);
                canvas.drawCircle(len / 2, len / 2, len / 2 - 3, mPaint);
                canvas.drawCircle(len / 2, len / 2, len / 4, mPaint);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(Definition.nightMode ? Definition.darkColor : Definition.lightColor);
                canvas.drawRect(new RectF(len / 6, len / 3, len / 3, len / 2), mPaint);
                mPaint.setColor(Definition.color[0]);
                canvas.drawRect(new RectF(len * 5 / 18, len * 2 / 9, len * 6 / 18, len * 7 / 18), mPaint);
                canvas.drawRect(new RectF(len * 5 / 18, len * 6 / 18, len * 8 / 18, len * 7 / 18), mPaint);
                break;
            case Definition.BEAT:
                mPaint.setColor(Definition.color[8]);
                canvas.drawCircle(len / 2, len / 2, len / 2 - 3, mPaint);
                bitmap = Bitmap.createScaledBitmap(((BitmapDrawable)getResources().getDrawable(R.drawable.beat)).getBitmap(), len, len, true);
                canvas.drawBitmap(bitmap, 0, 0, mPaint);
                break;
            case Definition.REARRANGE:
                mPaint.setColor(Definition.color[2]);
                canvas.drawCircle(len / 2, len / 2, len / 2 - 3, mPaint);
                bitmap = Bitmap.createScaledBitmap(((BitmapDrawable)getResources().getDrawable(R.drawable.rearrange)).getBitmap(), len, len, true);
                canvas.drawBitmap(bitmap, 0, 0, mPaint);
                break;
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(len, len);
    }

    public void recycleBitmap(){
        bitmap.recycle();
        bitmap = null;
    }
}
