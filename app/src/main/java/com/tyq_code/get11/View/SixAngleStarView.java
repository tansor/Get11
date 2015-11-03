package com.tyq_code.get11.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.tyq_code.get11.Controller.Definition;

public class SixAngleStarView extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int len = Definition.mScreenWidth / 20;
    private double sqrt3 = (double) Math.sqrt(3);

    public SixAngleStarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(3);
        Path path = new Path();
        path.moveTo(len / 2 + 2, 2);
        path.lineTo(2, len / 2 + (int) (len / 2 / sqrt3) + 2);
        path.lineTo(len + 2, len / 2 + (int) (len / 2 / sqrt3) + 2);
        path.close();
        path.moveTo(2, len / 2 - (int) (len / 2 / sqrt3) + 2);
        path.lineTo(len / 2 + 2, len + 2);
        path.lineTo(len + 2, len / 2 - (int) (len / 2 / sqrt3) + 2);
        path.close();
        canvas.drawPath(path, mPaint);
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(len + 4, len + 4);
    }
}
