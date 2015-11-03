package com.tyq_code.get11.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.tyq_code.get11.Activity.MainActivity;
import com.tyq_code.get11.Controller.Definition;

public class DynamicLinearLayout extends LinearLayout{
    private int time  = 0;
    private int total = 500;
    private int len;
    private int percent;
    private int mScreenWidth  = Definition.mScreenWidth;
    private int mScreenHeight = Definition.mScreenHeight;
    private final int x = 0;
    private final int y = 1;
    private float location[][] = new float[12][2];
    private Path path[] = new Path[12];
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public DynamicLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        len = mScreenWidth / 25;
        //rectangle
        for (int i = 0; i < 12; i++) {
            location[i][x] = (float) (Math.random() * mScreenWidth);
            location[i][y] = (float) (Math.random() * mScreenHeight);
        }
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Definition.color[(int)(Math.random() * 11) % 11]);
        for (int i = 0; i < 12; i++)
            path[i] = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        percent = mScreenWidth * time / total / 3;
        if (time * 2 > total)
            mPaint.setAlpha((total - time) * 2 * 150 / total);
        else mPaint.setAlpha(time * 2 * 150 / total);

        path[0].moveTo(location[0][x] + percent / 2, location[0][y] + percent / 5);
        path[0].lineTo(location[0][x] - len / 2 + percent / 2, location[0][y] - (float) (len * Math.sqrt(3) / 2) + percent / 5);
        path[0].lineTo(location[0][x] + len / 2 + percent / 2, location[0][y] - (float) (len * Math.sqrt(3) / 2) + percent / 5);
        path[0].close();

        path[1].moveTo(location[1][x] - percent / 3, location[1][y] - percent / 2);
        path[1].lineTo(location[1][x] - len / 2 - percent / 3, location[1][y] + (float) (len * Math.sqrt(3) / 2) - percent / 2);
        path[1].lineTo(location[1][x] + len / 2 - percent / 3, location[1][y] + (float) (len * Math.sqrt(3) / 2) - percent / 2);
        path[1].close();

        path[2].moveTo(location[2][x] - percent / 3, location[2][y] + percent / 2);
        path[2].lineTo(location[2][x] - len / 2 - percent / 3, location[2][y] - (float) (len * Math.sqrt(3) / 2) + percent / 2);
        path[2].lineTo(location[2][x] + len / 2 - percent / 3, location[2][y] - (float) (len * Math.sqrt(3) / 2) + percent / 2);
        path[2].close();

        path[3].moveTo(location[3][x] + percent / 5, location[3][y] - percent / 3);
        path[3].lineTo(location[3][x] - len / 2 + percent / 5, location[3][y] + (float) (len * Math.sqrt(3) / 2) - percent / 3);
        path[3].lineTo(location[3][x] + len / 2 + percent / 5, location[3][y] + (float) (len * Math.sqrt(3) / 2) - percent / 3);
        path[3].close();

        path[4].moveTo(location[4][x] - percent / 3, location[4][y] - percent);
        path[4].lineTo(location[4][x] - len / 2 - percent / 3, location[4][y] - (float) (len * Math.sqrt(3) / 2) - percent);
        path[4].lineTo(location[4][x] + len / 2 - percent / 3, location[4][y] - (float) (len * Math.sqrt(3) / 2) - percent);
        path[4].close();

        path[5].moveTo(location[5][x] + percent, location[5][y] - percent / 2);
        path[5].lineTo(location[5][x] - len / 2 + percent, location[5][y] + (float) (len * Math.sqrt(3) / 2) - percent / 2);
        path[5].lineTo(location[5][x] + len / 2 + percent, location[5][y] + (float) (len * Math.sqrt(3) / 2) - percent / 2);
        path[5].close();

        path[6].moveTo(location[6][x] + percent, location[6][y] + percent / 2);
        path[6].lineTo(location[6][x] - len / 2 + percent, location[6][y] + (float) (len * Math.sqrt(3) / 2) + percent / 2);
        path[6].lineTo(location[6][x] + len / 2 + percent, location[6][y] + (float) (len * Math.sqrt(3) / 2) + percent / 2);
        path[6].close();

        path[7].moveTo(location[7][x] - percent, location[7][y] + percent / 2);
        path[7].lineTo(location[7][x] - len / 2 - percent, location[7][y] - (float) (len * Math.sqrt(3) / 2) + percent / 2);
        path[7].lineTo(location[7][x] + len / 2 - percent, location[7][y] - (float) (len * Math.sqrt(3) / 2) + percent / 2);
        path[7].close();

        path[8].moveTo(location[8][x]       + percent * 7 / 3, location[8][y]       + percent);
        path[8].lineTo(location[8][x] + len + percent * 7 / 3, location[8][y]       + percent);
        path[8].lineTo(location[8][x] + len + percent * 7 / 3, location[8][y] + len + percent);
        path[8].lineTo(location[8][x]       + percent * 7 / 3, location[8][y] + len + percent);
        path[8].close();

        path[9].moveTo(location[9][x]       - percent, location[9][y]       - percent * 7 / 3);
        path[9].lineTo(location[9][x] + len - percent, location[9][y]       - percent * 7 / 3);
        path[9].lineTo(location[9][x] + len - percent, location[9][y] + len - percent * 7 / 3);
        path[9].lineTo(location[9][x]       - percent, location[9][y] + len - percent * 7 / 3);
        path[9].close();

        path[10].moveTo(location[10][x]       + percent, location[10][y]       - percent * 7 / 4);
        path[10].lineTo(location[10][x] + len + percent, location[10][y]       - percent * 7 / 4);
        path[10].lineTo(location[10][x] + len + percent, location[10][y] + len - percent * 7 / 4);
        path[10].lineTo(location[10][x]       + percent, location[10][y] + len - percent * 7 / 4);
        path[10].close();

        path[11].moveTo(location[11][x]       - percent * 4 / 3, location[11][y]       + percent * 7 / 6);
        path[11].lineTo(location[11][x] + len - percent * 4 / 3, location[11][y]       + percent * 7 / 6);
        path[11].lineTo(location[11][x] + len - percent * 4 / 3, location[11][y] + len + percent * 7 / 6);
        path[11].lineTo(location[11][x]       - percent * 4 / 3, location[11][y] + len + percent * 7 / 6);
        path[11].close();

        for (int i = 0; i < 12; i++)
            canvas.drawPath(path[i], mPaint);

        for (int i = 0; i < 12; i++)
            path[i].reset();

        if (time++ < total) {
            invalidate();
        }
        else {
            time = 0;
            for (int i = 0; i < 12; i++) {
                location[i][x] = (float) (Math.random() * mScreenWidth);
                location[i][y] = (float) (Math.random() * mScreenHeight);
            }
            mPaint.setColor(Definition.color[(int)(Math.random() * 11) % 11]);
            invalidate();
        }
    }
}
