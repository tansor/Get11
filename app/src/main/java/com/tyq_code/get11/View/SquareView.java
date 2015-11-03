package com.tyq_code.get11.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.tyq_code.get11.Activity.GameActivity;
import com.tyq_code.get11.Controller.Definition;

import java.util.logging.Handler;

public class SquareView extends View {
    private final int     TOTAL_TIME = 18;
    private final double  sin18 = (double)Math.sin(Math.PI / 10);
    private final double  cos18 = (double)Math.cos(Math.PI / 10);
    private final double  sin36 = (double)Math.sin(Math.PI / 5);
    private final int     max;
    private final int     len;
    private final int     i;
    private final int     j;
    private       int     time = 0;
    private       boolean isClicked = false;
    private       Path    fiveAngleStarPath = new Path();
    private       Paint   bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private       Paint   textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF   mRectF;
    private final RectF   arcRectF;

    private Board.BoardHandler boardHandler;
    private Message msg = new Message();

    public SquareView(Context context) {
        this(context, 0, 0, 11, 120, null);
    }

    public SquareView(Context context, int max, int len, Board.BoardHandler boardHandler) {
        this(context, 0, 0, max, len, boardHandler);
    }

    public SquareView(Context context, int i, int j, int max, int len, Board.BoardHandler boardHandler) {
        super(context);
        this.i = i;
        this.j = j;
        this.max = max;
        this.len = len;
        this.mRectF = new RectF(len / 20, len / 20, len * 19 / 20, len * 19 / 20);
        this.boardHandler = boardHandler;
        int sqrtNum = (int) (Math.sqrt(2) * len);
        arcRectF = new RectF(len / 2 - sqrtNum, len / 2 - sqrtNum, len / 2 + sqrtNum, len / 2 + sqrtNum);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //background
        bgPaint.setColor(Definition.color[(max - 1 <= 10 ? max - 1 : 10)]);
        bgPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(mRectF, 15, 15, bgPaint);

        //text
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(len / 2);
        textPaint.setFakeBoldText(true);
        if (max >= 11) {
            bgPaint.setTextSize(len * 5 / 6);
            bgPaint.setColor(0xfff3a10d);
            fiveAngleStarPath.moveTo(len / 2, len / 2 - (float) (len * 5 * cos18 / 12));
            fiveAngleStarPath.lineTo(len / 2 - (float) (len * 5 * sin18 / 6), len / 2 + (float) (len * 5 * cos18 / 12));
            fiveAngleStarPath.lineTo(len * 11 / 12, len / 2 + (float) (len * 5 * cos18 / 12) - (float) (len * 5 * sin36 / 6));
            fiveAngleStarPath.lineTo(len / 12, len / 2 + (float) (len * 5 * cos18 / 12) - (float) (len * 5 * sin36 / 6));
            fiveAngleStarPath.lineTo(len / 2 + (float) (len * 5 * sin18 / 6), len / 2 + (float) (len * 5 * cos18 / 12));
            fiveAngleStarPath.close();
            canvas.drawPath(fiveAngleStarPath, bgPaint);
        }
        canvas.drawText(Integer.toString(max), len / 2 - textPaint.measureText(Integer.toString(max)) / 2, len * 23 / 33, textPaint);

        //arc animation
        if (isClicked) {
            bgPaint.setColor(Definition.nightMode ? Definition.darkColor : Definition.lightColor);
            time++;
            canvas.drawArc(arcRectF, -90F, 360F * time / TOTAL_TIME, true, bgPaint);
            if (time < TOTAL_TIME) invalidate();
            if (time == TOTAL_TIME) {
                if (!Definition.willBeat) {
                    msg.what = Definition.REMOVE_IS_OK;
                    msg.arg1 = i;
                    msg.arg2 = j;
                }
                else {
                    msg.what = Definition.MOVE_IS_OK;
                    msg.arg1 = i * 5 + j;
                }
                boardHandler.sendMessage(msg);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(len, len);
    }

    public void setHandler(Board.BoardHandler handler){
        this.boardHandler = handler;
    }

    public void setClicked(boolean isClicked){
        this.isClicked = isClicked;
    }
}