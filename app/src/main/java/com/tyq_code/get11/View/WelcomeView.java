package com.tyq_code.get11.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import com.tyq_code.get11.Controller.Definition;

public class WelcomeView extends View {
    private final int mScreenWidth  = Definition.mScreenWidth;
    private final int mScreenHeight = Definition.mScreenHeight;
    private final String str = "Can You Get 11?";
    private final int textLen;

    private Path mPath = new Path();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public WelcomeView(Context context) {
        super(context);
        mPaint.setTextSize(mScreenWidth / 10);
        mPaint.setStyle(Paint.Style.FILL);
        textLen = (int) mPaint.measureText(str);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Definition.color[5]);
        mPath.moveTo(0, mScreenHeight / 6);
        mPath.lineTo(mScreenWidth, 0);
        mPath.lineTo(mScreenWidth, mScreenHeight / 3);
        mPath.lineTo(0, mScreenHeight / 3);
        mPath.close();
        canvas.drawPath(mPath, mPaint);

        mPaint.setColor(Color.WHITE);
        canvas.drawText(str, mScreenWidth / 2 - textLen / 2, mScreenHeight * 7 / 32, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mScreenWidth, mScreenHeight / 3);
    }
}
