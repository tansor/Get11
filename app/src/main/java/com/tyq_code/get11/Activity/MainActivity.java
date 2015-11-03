package com.tyq_code.get11.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.tyq_code.get11.Controller.Definition;
import com.tyq_code.get11.R;
import com.tyq_code.get11.View.Board;
import com.tyq_code.get11.View.DynamicRelativeLayout;
import com.tyq_code.get11.View.SquareView;
import com.tyq_code.get11.View.WelcomeView;

public class MainActivity extends Activity implements View.OnClickListener {
    private int mScreenWidth;
    private int mScreenHeight;
    private MyHandler myHandler;
    private SquareView sv[] = new SquareView[11];
    private DynamicRelativeLayout rl;
    private RelativeLayout.LayoutParams lp[] = new RelativeLayout.LayoutParams[11];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //get screenWidth and screenHeight
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMatrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMatrics);
        Definition.mScreenWidth  = mScreenWidth  = outMatrics.widthPixels;
        Definition.mScreenHeight = mScreenHeight = outMatrics.heightPixels;
        //init UI
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        rl = (DynamicRelativeLayout) findViewById(R.id.welcomeView);
        rl.setBackgroundColor(getSharedPreferences("data",
                Activity.MODE_PRIVATE).getBoolean("nightMode", false) ? Definition.darkColor : Definition.lightColor);
        int squareLen = mScreenWidth / 6;

        WelcomeView welcomeView = new WelcomeView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mScreenWidth, mScreenHeight / 3);
        layoutParams.topMargin = mScreenHeight * 2 / 3;
        rl.addView(welcomeView, layoutParams);

        for (int i = 0; i < 11; i++) {
            sv[i] = new SquareView(this, i + 1, squareLen, null);
            lp[i] = new RelativeLayout.LayoutParams(squareLen, squareLen);
        }

        sv[10].setOnClickListener(this);

        int r = squareLen * 2;
        double pi = Math.PI;
        int sin1 = (int) (Math.sin(pi * 15 / 80) * r);
        int cos1 = (int) (Math.cos(pi * 15 / 80) * r);
        int sin2 = (int) (Math.sin(pi / 10) * r);
        int cos2 = (int) (Math.cos(pi / 10) * r);

        lp[ 0].leftMargin = mScreenWidth  / 2 - squareLen / 2 - r;
        lp[ 0].topMargin  = mScreenHeight / 2 - squareLen / 2;
        lp[ 1].leftMargin = mScreenWidth  / 2 - squareLen / 2 - cos1;
        lp[ 1].topMargin  = mScreenHeight / 2 - squareLen / 2 - sin1;
        lp[ 2].leftMargin = mScreenWidth  / 2 - squareLen / 2 - sin2;
        lp[ 2].topMargin  = mScreenHeight / 2 - squareLen / 2 - cos2;
        lp[ 3].leftMargin = mScreenWidth  / 2 - squareLen / 2 + sin2;
        lp[ 3].topMargin  = mScreenHeight / 2 - squareLen / 2 - cos2;
        lp[ 4].leftMargin = mScreenWidth  / 2 - squareLen / 2 + cos1;
        lp[ 4].topMargin  = mScreenHeight / 2 - squareLen / 2 - sin1;
        lp[ 5].leftMargin = mScreenWidth  / 2 - squareLen / 2 + r;
        lp[ 5].topMargin  = mScreenHeight / 2 - squareLen / 2;
        lp[ 6].leftMargin = mScreenWidth  / 2 - squareLen / 2 + cos1;
        lp[ 6].topMargin  = mScreenHeight / 2 - squareLen / 2 + sin1;
        lp[ 7].leftMargin = mScreenWidth  / 2 - squareLen / 2 + sin2;
        lp[ 7].topMargin  = mScreenHeight / 2 - squareLen / 2 + cos2;
        lp[ 8].leftMargin = mScreenWidth  / 2 - squareLen / 2 - sin2;
        lp[ 8].topMargin  = mScreenHeight / 2 - squareLen / 2 + cos2;
        lp[ 9].leftMargin = mScreenWidth  / 2 - squareLen / 2 - cos1;
        lp[ 9].topMargin  = mScreenHeight / 2 - squareLen / 2 + sin1;
        lp[10].leftMargin = mScreenWidth  / 2 - squareLen / 2;
        lp[10].topMargin  = mScreenHeight / 2 - squareLen / 2;

        for (int i = 0; i < 11; i++)
            rl.addView(sv[i], lp[i]);

        myHandler = new MyHandler(sv);
        for (int i = 0; i < 10; i++)
            sendMsg(i, 300 * i);
    }

    @Override
    public void onClick(View v) {
        if (v == sv[10]) {
            for (int i = 0; i < 10; i++)
                ObjectAnimator.ofFloat(sv[i], "Alpha", 1F, 0F).setDuration(400).start();
            Board.shake(sv[10], 400).addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    startActivity(new Intent(MainActivity.this, GameActivity.class));
                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                    animation.removeAllListeners();
                    finish();
                    super.onAnimationEnd(animation);
                }
            });
        }
    }

    private static class MyHandler extends Handler {

        public SquareView sv[] = new SquareView[11];

        public MyHandler(SquareView squareView[]) {
            System.arraycopy(squareView, 0, sv, 0, 11);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Board.shake(sv[msg.arg1], 300);
            sendMsg(msg.arg1, 3000);
        }

        private void sendMsg(int tag, int delayMillis) {
            Message msg = new Message();
            msg.arg1 = tag;
            this.sendMessageDelayed(msg, delayMillis);
        }
    }

    private void sendMsg(int tag, int delayMillis) {
        Message msg = new Message();
        msg.arg1 = tag;
        myHandler.sendMessageDelayed(msg, delayMillis);
    }

    @Override
    protected void onDestroy() {
        setContentView(R.layout.null_layout);
        for (int i = 0; i < 11; i++) {
            rl.removeView(sv[i]);
            sv[i] = null;
            lp[i] = null;
        }
        sv = null;
        rl.removeAllViews();
        myHandler.removeCallbacksAndMessages(null);
        myHandler = null;
        System.gc();
        super.onDestroy();
    }
}