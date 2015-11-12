package com.tyq_code.get11.View;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tyq_code.get11.Activity.GameActivity;
import com.tyq_code.get11.Controller.Definition;

import java.util.ArrayList;
import java.util.Random;

public class Board extends RelativeLayout {
    private int     score_backup     []     = new int[4];
    public  int     board            [][][] = new int[7][7][2];
    private int     board_backup_temp[][][] = new int[7][7][2];
    private int     board_backup1    [][][] = new int[7][7][2];
    private int     board_backup2    [][][] = new int[7][7][2];
    private int     board_backup3    [][][] = new int[7][7][2];

    public  int     score         = 0;
    public  int     restore_time  = 0;
    private int     gcTime        = 0;
    private int     fall_max      = 3;
    public  int     max           = 3;
    private int     squareLen     = 120;
    private int     mScreenWidth  = Definition.mScreenWidth ;
    private int     mScreenHeight = Definition.mScreenHeight;
    private boolean SHOW_11_ONCE  = false;
    private boolean REMOVE_ONCE   = false;
    private boolean FALL_ONCE     = false;

    private GameOverView gameOverView;
    public  BoardHandler myHandler = new BoardHandler();
    private GameActivity.MessageHandler mainHandler;

    private ArrayList<Integer>     readyToMove  = new ArrayList<>();
    public  ArrayList<CombineView> combineViews = new ArrayList<>();

    public Board(Context context) {
        super(context);
        init();
    }

    public Board(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.squareLen = mScreenWidth / 6;

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                board[i][j][0] = -1;
                board[i][j][1] = 0;
            }
        }
        char[] board_backup = getContext().getSharedPreferences("data", Activity.MODE_PRIVATE).getString("board", "").toCharArray();
        if (board_backup.length == 0) {
            Definition.gameIsEnd = false;
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    board[i + 1][j + 1][0] = random();
                    addSquareView(i, j);
                }
            }
        } else {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    board[i + 1][j + 1][0] = board_backup[i * 5 + j] - '0';
                    addSquareView(i, j);
                }
            }
            max = getContext().getSharedPreferences("data", 0).getInt("max", 3);
        }
        if (Definition.willCombine) {
            addCombineView();
            combineSameView();
        }
        setAllSquareViewClickable(true);
    }

    public class BoardHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Definition.MOVE_IS_OK:
                    SquareView sv1 = (SquareView) findViewById(msg.arg1);
                    sv1.setOnClickListener(null);
                    sv1.setHandler(null);
                    removeView(sv1);
                    if (Definition.willBeat) fall();
                    if (Definition.willUndo) restore_step2();
                    if (Definition.willRearrange) rearrange();
                    break;
                case Definition.REMOVE_IS_OK:
                    if (!REMOVE_ONCE) {
                        int i = msg.arg1, j = msg.arg2;
                        board[i + 1][j + 1][0]++;
                        SquareView sv2 = (SquareView) findViewById(i * 5 + j);
                        sv2.setOnClickListener(null);
                        sv2.setHandler(null);
                        removeView(sv2);
                        addSquareView(i, j);
                        score += board[i + 1][j + 1][0] - 1;
                        sendMsg(mainHandler, Definition.SCORE, score, -1, 0);
                        REMOVE_ONCE = true;
                        if (!SHOW_11_ONCE) checkIsWin();
                        fall();
                    }
                    break;
                case Definition.FALL_IS_OK:
                    SquareView sv3 = (SquareView) findViewById(msg.getData().getInt("ID"));
                    sv3.setOnClickListener(null);
                    sv3.setHandler(null);
                    removeView(sv3);
                    addSquareView(msg.getData().getInt("i"), msg.getData().getInt("j"));
                    if (!FALL_ONCE) {
                        fallNewSquareView();
                        FALL_ONCE = true;
                    }
                    break;
                case Definition.FALL_NEW_SQUARE_VIEW_IS_OK:
                    if (Definition.willCombine) combineSameView();
                    checkIsEnd();
                    break;
                case Definition.GAME_IS_END:
                    gameIsOver();
                    break;
                case Definition.GAME_OVER_VIEW_IS_SHOWN:
                    removeView(gameOverView);
                    break;
                case Definition.YOU_GET_11:
                    youGet11();
                    break;
            }
        }
    }

    public class SquareViewListener implements View.OnClickListener {
        private int i;
        private int j;

        public SquareViewListener(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public void onClick(View v) {
            int temp = board[i + 1][j + 1][0];
            boardCopy(board_backup_temp, board);
            score_backup[0] = score;
            if (Definition.willCombine) cancelSameView();
            if (!Definition.willBeat) {
                gcTime++;
                searchSameSquareView(i, j);
                board[i + 1][j + 1][0] = temp;
                if (readyToMove.size() == 1) {
                    shake(v, 300);
                    if (Definition.willCombine) combineSameView();
                } else {
                    if (restore_time < 3) restore_time++;
                    backup();
                    sendMsg(mainHandler, Definition.SET_HINT_VIEW, -1, -1, 0);
                    setAllSquareViewClickable(false);
                    SquareView sv = (SquareView) findViewById(i * 5 + j);
                    sv.setClicked(true);
                    sv.invalidate();
                    moveToClickedSquareView();
                }
                readyToMove.clear();
                setTagFree();
            } else {
                if (restore_time < 3) restore_time++;
                backup();
                board[i + 1][j + 1][0] = 0;
                setAllSquareViewClickable(false);
                SquareView sv = (SquareView) findViewById(i * 5 + j);
                sv.setClicked(true);
                sv.invalidate();
            }
        }
    }

    public void addCombineView() {
//        Log.e("addCombineView", "getIN");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                CombineView cv = new CombineView(getContext(), squareLen * 9 / 10, false);//vertical
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(squareLen * 3 / 10, squareLen * 9 / 10);
                lp.topMargin = squareLen / 2 + i * squareLen + squareLen / 20;
                lp.leftMargin = squareLen / 2 + (j + 1) * squareLen - squareLen * 3 / 20;
                combineViews.add(cv);
                this.addView(cv, lp);
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                CombineView cv = new CombineView(getContext(), squareLen * 9 / 10, true);//horizontal
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(squareLen * 9 / 10, squareLen * 3 / 10);
                lp.topMargin = squareLen / 2 + (i + 1) * squareLen - squareLen * 3 / 20;
                lp.leftMargin = squareLen / 2 + j * squareLen + squareLen / 20;
                combineViews.add(cv);
                this.addView(cv, lp);
            }
        }
    }

    private SquareView addSquareView(int i, int j) {
//        Log.e("addSquareView", i + " " + j);
        SquareView sv = new SquareView(getContext(), i, j, board[i + 1][j + 1][0], squareLen, myHandler);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(squareLen, squareLen);
        lp.topMargin = squareLen / 2 + i * squareLen;
        lp.leftMargin = squareLen / 2 + j * squareLen;
        sv.setId(i * 5 + j);
        sv.setOnClickListener(new SquareViewListener(i, j));
        sv.setClickable(false);
        this.addView(sv, lp);
        return sv;
    }

    public void combineSameView() {
//        Log.e("combineSameView", "getIN");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                if (board[i + 1][j + 1][0] == board[i + 1][j + 2][0]) {
                    combineViews.get(i * 4 + j).isClicked = true;
                    combineViews.get(i * 4 + j).num = board[i + 1][j + 1][0];
                    combineViews.get(i * 4 + j).invalidate();
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                if (board[i + 1][j + 1][0] == board[i + 2][j + 1][0]) {
                    combineViews.get(20 + i * 5 + j).isClicked = true;
                    combineViews.get(20 + i * 5 + j).num = board[i + 1][j + 1][0];
                    combineViews.get(20 + i * 5 + j).invalidate();
                }
            }
        }
    }

    public void cancelSameView() {
//        Log.e("cancelSameView", "getIN");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                combineViews.get(i * 4 + j).isClicked = false;
                combineViews.get(i * 4 + j).invalidate();
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                combineViews.get(20 + i * 5 + j).isClicked = false;
                combineViews.get(20 + i * 5 + j).invalidate();
            }
        }
//        for (int i = 0; i < 5; i++) {
//            for (int j = 0; j < 5; j++) {
//                SquareView sv = (SquareView) findViewById(i * 5 + j);
//                sv.invalidate();
//            }
//        }
    }

    private void searchSameSquareView(int i, int j) {
//        Log.e("searchSameSquareView", "getIN");
        board[i + 1][j + 1][1] = 1;
        readyToMove.add(i * 5 + j);
        if (j - 1 >= 0 && board[i + 1][j - 1 + 1][0] == board[i + 1][j + 1][0] && board[i + 1][j - 1 + 1][1] == 0) {
            score += board[i + 1][j + 1][0];
            searchSameSquareView(i, j - 1);
        }
        if (j + 1 <= 4 && board[i + 1][j + 1 + 1][0] == board[i + 1][j + 1][0] && board[i + 1][j + 1 + 1][1] == 0) {
            score += board[i + 1][j + 1][0];
            searchSameSquareView(i, j + 1);
        }
        if (i - 1 >= 0 && board[i - 1 + 1][j + 1][0] == board[i + 1][j + 1][0] && board[i - 1 + 1][j + 1][1] == 0) {
            score += board[i + 1][j + 1][0];
            searchSameSquareView(i - 1, j);
        }
        if (i + 1 <= 4 && board[i + 1 + 1][j + 1][0] == board[i + 1][j + 1][0] && board[i + 1 + 1][j + 1][1] == 0) {
            score += board[i + 1][j + 1][0];
            searchSameSquareView(i + 1, j);
        }
        board[i + 1][j + 1][0] = 0;
    }

    private void moveToClickedSquareView() {
//        Log.e("moveToClickedSquareView", "getIN");
        final SquareView child_main = (SquareView) findViewById(readyToMove.get(0));
        for (int i = 1; i < readyToMove.size(); i++) {
            final int index = readyToMove.get(i);
            final SquareView child_part = (SquareView) findViewById(index);
            float mx = child_main.getX(), my = child_main.getY(), px = child_part.getX(), py = child_part.getY();
            ObjectAnimator oa1 = ObjectAnimator.ofFloat(child_part, "TranslationX", 0F, mx - px).setDuration(100);
            ObjectAnimator oa2 = ObjectAnimator.ofFloat(child_part, "TranslationY", 0F, my - py).setDuration(100);
            AnimatorSet as = new AnimatorSet();
            as.play(oa1).with(oa2);
            sendMsg(myHandler, Definition.MOVE_IS_OK, index, -1, 150);
            as.start();
        }
    }

    private void fall() {
//        Log.e("fall", "getIN");
        boolean notFall = true;
        for (int j = 0; j <= 4; j++) {
            int index = 4;
            for (int i = 4; i >= 0; i--) {
                if (board[i + 1][j + 1][0] != 0) {
                    if (index - i > 0) {
                        notFall = false;
                        final SquareView sv = (SquareView) findViewById(i * 5 + j);
                        AnimatorSet as = new AnimatorSet();
                        if (Definition.willCombine) {
                            ObjectAnimator oa1 = ObjectAnimator.ofFloat(sv, "TranslationY", 0F, (index - i) * squareLen).setDuration(100);
                            as.play(oa1);
                        } else {
                            ObjectAnimator oa1 = ObjectAnimator.ofFloat(sv, "TranslationY", 0F, (index - i) * squareLen + squareLen / 20).setDuration(100);
                            ObjectAnimator oa2 = ObjectAnimator.ofFloat(sv, "TranslationY", (index - i) * squareLen + squareLen / 20, (index - i) * squareLen).setDuration(50);
                            as.play(oa2).after(oa1);
                        }
                        as.start();

                        board[index + 1][j + 1][0] = board[i + 1][j + 1][0];
                        board[i + 1][j + 1][0] = 0;

                        Message msg = new Message();
                        msg.what = Definition.FALL_IS_OK;
                        Bundle b = new Bundle();
                        b.putInt("ID", i * 5 + j);
                        b.putInt("i", index);
                        b.putInt("j", j);
                        msg.setData(b);
                        if (Definition.willCombine) myHandler.sendMessageDelayed(msg, 100);
                        else myHandler.sendMessageDelayed(msg, 150);
                    }
                    index--;
                }
            }
        }
        if (notFall) {
            fallNewSquareView();
        }
    }

    private void fallNewSquareView() {
//        Log.e("fallNewSquareView", "getIN");
        boolean FALL_NEW_SQUAREVIEW_ONCE = false;
        for (int j = 0; j <= 4; j++) {
            for (int i = 4; i >= 0; i--) {
                if (board[i + 1][j + 1][0] == 0) {
                    board[i + 1][j + 1][0] = random();

                    SquareView sv = addSquareView(i, j);
                    AnimatorSet as = new AnimatorSet();
                    ObjectAnimator oa1 = ObjectAnimator.ofFloat(sv, "TranslationY", -squareLen / 5, 0).setDuration(150);
                    ObjectAnimator oa2 = ObjectAnimator.ofFloat(sv, "Alpha", 0F, 1F).setDuration(150);
                    as.play(oa1).with(oa2);
                    as.start();

                    if (!FALL_NEW_SQUAREVIEW_ONCE) {
                        sendMsg(myHandler, Definition.FALL_NEW_SQUARE_VIEW_IS_OK, -1, -1, 50);
                        FALL_NEW_SQUAREVIEW_ONCE = true;
                    }
                }
            }
        }
    }

    private void checkIsEnd() {
//        Log.e("checkIsEnd", "getIN");
        if(gcTime >= 10) {
            System.gc();
            gcTime = 0;
        }
        REMOVE_ONCE = false;
        FALL_ONCE = false;
        if (Definition.willBeat) {
            sendMsg(mainHandler, Definition.UPDATE_STAR_VIEW, Definition.BEAT, -1, 0);
            Definition.willBeat = false;
        }
        sendMsg(mainHandler, Definition.SET_HINT_VIEW, -1, -1, 0);
        updateMax();

        int up, down, left, right, middle;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                up = board[i][j + 1][0];
                down = board[i + 2][j + 1][0];
                left = board[i + 1][j][0];
                right = board[i + 1][j + 2][0];
                middle = board[i + 1][j + 1][0];
                if (middle == up || middle == down || middle == left || middle == right) {
                    setAllSquareViewClickable(true);
                    if (Definition.gameIsEnd) Definition.gameIsEnd = false;
                    return;
                }
            }
        }
        sendMsg(mainHandler, Definition.GAME_IS_END, -1, -1, 0);
        sendMsg(myHandler, Definition.GAME_IS_END, -1, -1, 0);
    }

    private void checkIsWin() {
//        Log.e("checkIsWin", "getIN");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (board[i + 1][j + 1][0] == 11) {
                    shake(findViewById(i * 5 + j), 400);
                    sendMsg(mainHandler, Definition.YOU_GET_11, -1, -1, 0);
                    sendMsg(myHandler, Definition.YOU_GET_11, -1, -1, 450);
                    SHOW_11_ONCE = true;
                    return;
                }
            }
        }
    }

    private void gameIsOver() {
//        Log.e("gameIsOver", "");
//        combineViews.clear();

//        for (int i = 0; i < 5; i++) {
//            for (int j = 0; j < 5; j++) {
//                SquareView sv = (SquareView) findViewById(i * 5 + j);
//                sv.setOnClickListener(null);
//            }
//        }
        setAllSquareViewClickable(false);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if ((i * 5 + j) % 2 == 0) {
                    SquareView sv = (SquareView) findViewById(i * 5 + j);
                    shake(sv, 400);
                }
            }
        }
        gameOverView = new GameOverView(getContext(), "Game Over!");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = mScreenWidth / 2 - mScreenHeight / 16;
        lp.leftMargin = 0;
        addView(gameOverView, lp);
        AnimatorSet as = new AnimatorSet();
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(gameOverView, "Alpha", 0F, 1F).setDuration(500);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(gameOverView, "Alpha", 1F, 0F).setDuration(3000);
        as.play(oa2).after(oa1);
        as.start();
        sendMsg(myHandler, Definition.GAME_OVER_VIEW_IS_SHOWN, -1, -1, 3500);
    }

    private void youGet11() {
        gameOverView = new GameOverView(getContext(), "U~ GET 11!");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = mScreenWidth / 2 - mScreenHeight / 16;
        lp.leftMargin = 0;
        addView(gameOverView, lp);
        AnimatorSet as = new AnimatorSet();
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(gameOverView, "Alpha", 0F, 1F).setDuration(500);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(gameOverView, "Alpha", 1F, 0F).setDuration(3000);
        as.play(oa2).after(oa1);
        as.start();
        sendMsg(myHandler, Definition.GAME_OVER_VIEW_IS_SHOWN, -1, -1, 3500);
    }

    public static AnimatorSet shake(View v, int time) {
        AnimatorSet as = new AnimatorSet();
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(v, "ScaleX", 1F, 1.2F).setDuration(time / 4);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(v, "ScaleY", 1F, 1.2F).setDuration(time / 4);
        ObjectAnimator oa3 = ObjectAnimator.ofFloat(v, "ScaleX", 1.2F, 1F).setDuration(time / 4);
        ObjectAnimator oa4 = ObjectAnimator.ofFloat(v, "ScaleY", 1.2F, 1F).setDuration(time / 4);
        ObjectAnimator oa5 = ObjectAnimator.ofFloat(v, "TranslationX", 0F, -10F).setDuration(time / 16);
        ObjectAnimator oa6 = ObjectAnimator.ofFloat(v, "TranslationX", -10F, 10F).setDuration(time / 8);
        ObjectAnimator oa7 = ObjectAnimator.ofFloat(v, "TranslationX", 10F, 0F).setDuration(time / 16);
        ObjectAnimator oa8 = ObjectAnimator.ofFloat(v, "TranslationX", 0F, -10F).setDuration(time / 16);
        ObjectAnimator oa9 = ObjectAnimator.ofFloat(v, "TranslationX", -10F, 10F).setDuration(time / 8);
        ObjectAnimator oa10 = ObjectAnimator.ofFloat(v, "TranslationX", 10F, 0F).setDuration(time / 16);
        as.play(oa1).with(oa2);
        as.play(oa3).with(oa4).after(time / 4);
        as.play(oa5).after(time / 2);
        as.play(oa6).after(time * 9 / 16);
        as.play(oa7).after(time * 11 / 16);
        as.play(oa8).after(time * 3 / 4);
        as.play(oa9).after(time * 13 / 16);
        as.play(oa10).after(time * 15 / 16);
        as.start();
        return as;
    }

    public void setAllSquareViewClickable(boolean b) {
//        Log.e("AllSquareViewClickable", ""+ b);
        if (b) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    findViewById(i * 5 + j).setClickable(true);
                }
            }
        } else {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    findViewById(i * 5 + j).setClickable(false);
                }
            }
        }
    }

    private void setTagFree() {
//        Log.e("setTagFree", "getIN");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                board[i + 1][j + 1][1] = 0;
            }
        }
    }

    private int random() {
        return (int) (Math.random() * (fall_max <= 7 ? fall_max : 7)) % fall_max + 1;
    }

    public void setHandler(GameActivity.MessageHandler handler) {
        mainHandler = handler;
    }

    private void sendMsg(Handler handler, int messageTag, int arg1, int arg2, int delayMillis) {
        Message message = new Message();
        message.what = messageTag;
        if (arg1 != -1) message.arg1 = arg1;
        if (arg2 != -1) message.arg2 = arg2;
        if (delayMillis == 0) handler.sendMessage(message);
        else handler.sendMessageDelayed(message, delayMillis);
    }

    private void updateMax() {
        int max_backup = max;
        if (score >= 500) {
            if (Definition.hardLevel && score >= 2000) fall_max = 5;
            else fall_max = 4;
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                max = max > board[i + 1][j + 1][0] ? max : board[i + 1][j + 1][0];
            }
        }
        if (max > max_backup) {
            if (max == 9) sendMsg(mainHandler, Definition.UPDATE_STAR_VIEW, Definition.ADD_STAR, 5, 0);
            else if (max == 10) sendMsg(mainHandler, Definition.UPDATE_STAR_VIEW, Definition.ADD_STAR, 10, 0);
            else if (max >= 11) sendMsg(mainHandler, Definition.UPDATE_STAR_VIEW, Definition.ADD_STAR, 30 * (max-10), 0);
        }
    }

    private void backup() {
        switch (restore_time){
            case 3:
//                Log.e("backup", "boardCopy(board_backup3, board_backup2);");
//                Log.e("backup", "boardCopy(board_backup2, board_backup1);");
//                Log.e("backup", "boardCopy(board_backup1, board_backup_temp);");
                boardCopy(board_backup3, board_backup2);
                boardCopy(board_backup2, board_backup1);
                boardCopy(board_backup1, board_backup_temp);
                score_backup[3] = score_backup[2];
                score_backup[2] = score_backup[1];
                score_backup[1] = score_backup[0];
                break;
            case 2:
//                Log.e("backup", "boardCopy(board_backup2, board_backup1);");
//                Log.e("backup", "boardCopy(board_backup1, board_backup_temp);");
                boardCopy(board_backup2, board_backup1);
                boardCopy(board_backup1, board_backup_temp);
                score_backup[2] = score_backup[1];
                score_backup[1] = score_backup[0];
                break;
            case 1:
//                Log.e("backup", "boardCopy(board_backup1, board_backup_temp);");
                boardCopy(board_backup1, board_backup_temp);
                score_backup[1] = score_backup[0];
                break;
        }
    }

    public void restore_step1() {
//        Log.e("restore_step1", "time = " + restore_time);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                SquareView sv = (SquareView) findViewById(i * 5 + j);
                ObjectAnimator.ofFloat(sv, "Alpha", 1F, 0F).setDuration(500).start();
                sendMsg(myHandler, Definition.MOVE_IS_OK, i * 5 + j, -1, 1000);
            }
        }
    }

    private void restore_step2() {
        switch (restore_time) {
            case 3:
//                Log.e("restore_step2", "boardCopy(board, board_backup1);");
//                Log.e("restore_step2", "boardCopy(board_backup1, board_backup2);");
//                Log.e("restore_step2", "boardCopy(board_backup2, board_backup3);");
                boardCopy(board, board_backup1);
                boardCopy(board_backup1, board_backup2);
                boardCopy(board_backup2, board_backup3);
                score = score_backup[1];
                score_backup[1] = score_backup[2];
                score_backup[2] = score_backup[3];
                break;
            case 2:
//                Log.e("restore_step2", "boardCopy(board, board_backup1);");
//                Log.e("restore_step2", "boardCopy(board_backup1, board_backup2);");
                boardCopy(board, board_backup1);
                boardCopy(board_backup1, board_backup2);
                score = score_backup[1];
                score_backup[1] = score_backup[2];
                break;
            case 1:
//                Log.e("restore_step2", "boardCopy(board, board_backup1);");
                boardCopy(board, board_backup1);
                score = score_backup[1];
                break;
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                addSquareView(i, j).setClickable(true);
            }
        }
        Definition.willUndo = false;
        restore_time--;
        if (Definition.willCombine) combineSameView();
        checkIsEnd();
        sendMsg(mainHandler, Definition.SET_HINT_VIEW, -1, -1, 0);
        sendMsg(mainHandler, Definition.UPDATE_STAR_VIEW, Definition.UNDO, -1, 0);
        sendMsg(mainHandler, Definition.SCORE, score, -1, 0);
    }

    private void boardCopy (int board1[][][], int board2[][][]){
        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 7; j++)
                board1[i][j][0] = board2[i][j][0];
    }

    private void rearrange() {
        int temp[] = new int[25];
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                temp[i * 5 + j] = board[i+1][j+1][0];
            }
        }
        Random random = new Random();
        for (int i = 0; i < 25; i++){
            int p = random.nextInt(25);
            int tmp = temp[i];
            temp[i] = temp[p];
            temp[p] = tmp;
        }
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                board[i+1][j+1][0] = temp[i * 5 + j];
            }
        }
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                addSquareView(i, j).setClickable(true);
            }
        }
        restore_time = 0;
        Definition.willRearrange = false;
        sendMsg(mainHandler, Definition.UPDATE_STAR_VIEW, Definition.REARRANGE, -1, 0);
        if (Definition.willCombine) combineSameView();
        sendMsg(mainHandler, Definition.SET_HINT_VIEW, -1, -1, 0);
        checkIsEnd();
    }

    public void removeAllSquareViews() {
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                SquareView sv = (SquareView) findViewById(i * 5 + j);
                sv.setHandler(null);
                sv.setOnClickListener(null);
                removeView(sv);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(mScreenWidth, mScreenWidth);
    }
}