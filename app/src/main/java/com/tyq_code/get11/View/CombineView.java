package com.tyq_code.get11.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.tyq_code.get11.Controller.Definition;

public class CombineView extends View {//Combine same cube
    public  int num;
    public  int time = 0;
    public  boolean isClicked = false;

    private int len;
    private boolean orientation;
    private final int total = 25;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public CombineView(Context context, int len, boolean orientation) {
        super(context);
        this.orientation = orientation;
        this.len = len;
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!orientation)//vertical
            setMeasuredDimension(len / 3, len);
        else//horizontal
            setMeasuredDimension(len, len / 3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isClicked) {
            mPaint.setColor(Definition.color[num <= 11 ? num - 1 : 10]);
            if (!orientation) {//vertical
                canvas.drawCircle(0, len / 2, len * time / total, mPaint);
                canvas.drawCircle(len / 3, len / 2, len * time / total, mPaint);
            } else {//horizontal
                canvas.drawCircle(len / 2, 0, len * time / total, mPaint);
                canvas.drawCircle(len / 2, len / 3, len * time / total, mPaint);
            }
            if (time++ <= total) {
                invalidate();
            }
        } else {
            time = 0;
            canvas.drawColor(Definition.noColor);
        }
        super.onDraw(canvas);
    }
}
