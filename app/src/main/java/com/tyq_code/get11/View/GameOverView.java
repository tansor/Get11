package com.tyq_code.get11.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.*;
import android.util.AttributeSet;
import android.graphics.Paint;

import com.tyq_code.get11.Controller.Definition;

public class GameOverView extends View {
    private int screenWidth  = Definition.mScreenWidth;
    private int screenHeight = Definition.mScreenHeight;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF rectF;
    private String str = "Game Over!";

    public GameOverView(Context context) {
        super(context);
        init();
    }

    public GameOverView(Context context, String string) {
        super(context);
        init();
        this.str = string;
    }

    public GameOverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        rectF = new RectF(0, 0, screenWidth, screenHeight / 8);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(screenWidth, screenHeight / 8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Definition.color[2]);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rectF, paint);
        paint.setColor(Definition.color[0]);
        paint.setTextSize(screenWidth / 10);
        paint.setFakeBoldText(true);
        canvas.drawText(str, screenWidth / 2 - paint.measureText(str) / 2, screenHeight / 16 + screenWidth / 40, paint);
        super.onDraw(canvas);
    }
}
