package com.tyq_code.get11.Activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tyq_code.get11.Controller.Definition;
import com.tyq_code.get11.R;
import com.tyq_code.get11.View.Board;
import com.tyq_code.get11.View.ButtonView;
import com.tyq_code.get11.View.CheckView;
import com.tyq_code.get11.View.SixAngleStarView;

public class GameActivity extends Activity implements View.OnClickListener {
    private int                      star;
    private int                      mScreenWidth   = Definition.mScreenWidth;
    private int                      mScreenHeight  = Definition.mScreenHeight;
    private MessageHandler           messageHandler = new MessageHandler();
    private Context                  context        = this;
    private Board                    boardView;
    private Button                   restartButton;
    private ButtonView               question;
    private ButtonView               pause;
    private ButtonView               undo;
    private ButtonView               beat;
    private ButtonView               rearrange;
    private SharedPreferences        mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private SixAngleStarView         sixAngleStar;
    private TextView                 scoreView;
    private TextView                 starView;
    private TextView                 hintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //read memory
        mSharedPreferences = getSharedPreferences("data", Activity.MODE_PRIVATE);
        Definition.willCombine = mSharedPreferences.getBoolean("willCombine",  true);
        Definition.nightMode   = mSharedPreferences.getBoolean("nightMode"  , false);
        Definition.hasGot11    = mSharedPreferences.getBoolean("hasGot11"   , false);
        Definition.hardLevel   = mSharedPreferences.getBoolean("hardLevel"  , false);
        this.star              = mSharedPreferences.getInt    ("star"       ,    30);
        //set view
        setContentView(R.layout.activity_game);
        initUI();
    }

    private void initUI() {
        //dynamically set background color
        findViewById(R.id.gameViewFather).setBackgroundColor(Definition.nightMode ? Definition.darkColor : Definition.lightColor);

        {//ToolView1 contains Button_QUESTION, starView and Button_PAUSE
            RelativeLayout tool1 = (RelativeLayout) findViewById(R.id.main_tool1);
            LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(mScreenWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp0.bottomMargin = mScreenHeight / 40;
            tool1.setLayoutParams(lp0);

            question = new ButtonView(this, Definition.QUESTION);
            question.setTag(Definition.QUESTION);
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(mScreenWidth / 8, mScreenWidth / 8);
            lp1.leftMargin = mScreenWidth / 15;
            lp1.topMargin = mScreenWidth / 15;
            tool1.addView(question, lp1);
            question.setOnClickListener(this);

            LinearLayout starView_father = (LinearLayout) findViewById(R.id.starView_father);
            sixAngleStar = (SixAngleStarView) findViewById(R.id.six_angle_star);
            starView = (TextView) findViewById(R.id.starView);
            starView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenWidth / 15);
            starView.setText("" + star);
            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(mScreenWidth * 9 / 32, mScreenWidth / 10);
            lp2.topMargin = mScreenWidth / 15 + mScreenWidth / 16 - mScreenWidth / 20;
            lp2.leftMargin = mScreenWidth / 2 - mScreenWidth * 9 / 64;
            starView_father.setLayoutParams(lp2);

            pause = new ButtonView(this, Definition.PAUSE);
            pause.setTag(Definition.PAUSE);
            RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(mScreenWidth / 8, mScreenWidth / 8);
            lp3.leftMargin = mScreenWidth - mScreenWidth / 15 - mScreenWidth / 8;
            lp3.topMargin = mScreenWidth / 15;
            tool1.addView(pause, lp3);
            pause.setOnClickListener(this);
        }

        scoreView = (TextView) findViewById(R.id.scoreView);
        scoreView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenWidth / 12);
        scoreView.setText(mSharedPreferences.getString("score", "0"));

        hintView = (TextView) findViewById(R.id.hintView);
        hintView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenWidth / 20);
        hintView.setText(Definition.hasGot11 ?
                getString(R.string.congratulations) : getString(R.string.combine_the_line));
        if (Definition.hasGot11) hintView.setTextColor(Definition.color[0]);
        hintView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mScreenHeight / 20));

        restartButton = (Button) findViewById(R.id.main_restart);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mScreenWidth / 6, mScreenHeight / 20);
        layoutParams.leftMargin = mScreenWidth / 30;
        restartButton.setLayoutParams(layoutParams);
        restartButton.setTag(Definition.RESTART);
        restartButton.setOnClickListener(this);
        restartButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenWidth / 20);

        boardView = (Board) findViewById(R.id.main_board);
        boardView.setLayoutParams(new LinearLayout.LayoutParams(mScreenWidth, mScreenWidth * 23 / 24));
        boardView.setHandler(messageHandler);
        boardView.score = Integer.valueOf(mSharedPreferences.getString("score", "0"));

        {//ToolView2 contains Button_UNDO, Button_BEAT and Button_ORDER
            RelativeLayout tool2 = (RelativeLayout) findViewById(R.id.main_tool2);

            undo = new ButtonView(this, Definition.UNDO);
            undo.setTag(Definition.UNDO);
            RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(mScreenWidth / 8, mScreenWidth / 8);
            lp4.leftMargin = mScreenWidth / 6;
            tool2.addView(undo, lp4);
            undo.setOnClickListener(this);

            beat = new ButtonView(this, Definition.BEAT);
            beat.setTag(Definition.BEAT);
            RelativeLayout.LayoutParams lp5 = new RelativeLayout.LayoutParams(mScreenWidth / 8, mScreenWidth / 8);
            lp5.leftMargin = mScreenWidth / 2 - mScreenWidth / 16;
            tool2.addView(beat, lp5);
            beat.setOnClickListener(this);

            rearrange = new ButtonView(this, Definition.REARRANGE);
            rearrange.setTag(Definition.REARRANGE);
            RelativeLayout.LayoutParams lp6 = new RelativeLayout.LayoutParams(mScreenWidth / 8, mScreenWidth / 8);
            lp6.leftMargin = mScreenWidth * 5 / 6 - mScreenWidth / 8;
            tool2.addView(rearrange, lp6);
            rearrange.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (Integer.valueOf(v.getTag().toString())) {
            case Definition.QUESTION:
                new MyDialog(context, R.style.MyDialog, Definition.QUESTION).show();
                break;
            case Definition.PAUSE:
                new MyDialog(context, R.style.MyDialog, Definition.PAUSE).show();
                break;
            case Definition.RESTART:
                Definition.gameIsEnd = true;
                startActivity(new Intent(GameActivity.this, GameActivity.class));
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                finish();
                break;
            case Definition.UNDO:
                if (Definition.willUndo) break;
                if (star - Definition.UNDO_COST < 0) {
                    Definition.willUndo = false;
                    hintView.setText(getString(R.string.not_enough_star));
                    return;
                }
                if (boardView.restore_time <= 0) {
                    hintView.setText(getString(R.string.cannot_undo));
                    return;
                }
                Definition.willUndo = true;
                if (Definition.gameIsEnd) {
                    restartButton.setVisibility(View.GONE);
                    boardView.setAllSquareViewClickable(true);
                }
                hintView.setText(getString(R.string.will_undo) + (boardView.restore_time - 1) + "!");
                boardView.cancelSameView();
                boardView.restore_step1();
                break;
            case Definition.BEAT:
                if (star - Definition.BEAT_COST < 0) {
                    Definition.willBeat = false;
                    hintView.setText(getString(R.string.not_enough_star));
                    return;
                }
                Definition.willBeat = !Definition.willBeat;
                if (Definition.willBeat) {
                    if (Definition.gameIsEnd) {
                        restartButton.setVisibility(View.GONE);
                        boardView.setAllSquareViewClickable(true);
                    }
                    hintView.setText(getString(R.string.beat) + Definition.BEAT_COST);
                } else {
                    if (Definition.gameIsEnd) {
                        boardView.setAllSquareViewClickable(false);
                    }
                    sendMsg(Definition.SET_HINT_VIEW, -1, 0);
                }
                break;
            case Definition.REARRANGE:
                if (Definition.willRearrange) break;
                if (star - Definition.REARRANGE_COST < 0) {
                    hintView.setText(getString(R.string.not_enough_star));
                    return;
                }
                Definition.willRearrange = true;
                if (Definition.gameIsEnd) {
                    restartButton.setVisibility(View.GONE);
                    boardView.setAllSquareViewClickable(true);
                }
                hintView.setText(getString(R.string.will_rearrange));
                boardView.cancelSameView();
                boardView.restore_step1();
                break;
        }
    }

    public class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Definition.SCORE:
                    scoreView.setText("" + msg.arg1);
                    ObjectAnimator.ofFloat(sixAngleStar, "rotation", 0F, 360F).setDuration(100).start();
                    break;
                case Definition.GAME_IS_END:
                    Definition.gameIsEnd = true;
                    hintView.setText(getString(R.string.game_over_hint_part1) + boardView.max + getString(R.string.game_over_hint_part2));
                    restartButton.setVisibility(View.VISIBLE);
                    break;
                case Definition.YOU_GET_11:
                    if (!Definition.hasGot11) {
                        Definition.hasGot11 = true;
                        hintView.setText(getString(R.string.congratulations));
                        hintView.setTextColor(Definition.color[0]);
                    }
                    break;
                case Definition.FINISH_ACTIVITY:
                    Definition.nightMode = msg.arg1 == 1;
                    startActivity(new Intent(GameActivity.this, GameActivity.class));
                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                    finish();
                    break;
                case Definition.SET_HINT_VIEW:
                    if (Definition.gameIsEnd) {
                        hintView.setText(getString(R.string.game_over_hint_part1) + boardView.max + getString(R.string.game_over_hint_part2));
                        restartButton.setVisibility(View.VISIBLE);
                    } else if (Definition.hasGot11)
                        hintView.setText(getString(R.string.congratulations));
                    else hintView.setText(getString(R.string.combine_the_line));
                    break;
                case Definition.UPDATE_STAR_VIEW:
                    if (msg.arg1 == Definition.BEAT) star -= Definition.BEAT_COST;
                    else if (msg.arg1 == Definition.UNDO) star -= Definition.UNDO_COST;
                    else if (msg.arg1 == Definition.REARRANGE) star -= Definition.REARRANGE_COST;
                    else if (msg.arg1 == Definition.RECHARGE) star += 30;
                    else if (msg.arg1 == Definition.ADD_STAR) star += msg.arg2;
                    starView.setText("" + star);
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        mEditor = mSharedPreferences.edit();
        if (Definition.gameIsEnd) {
            mEditor.putString("score", "0");
            mEditor.putString("board", "");
            mEditor.putInt("max", 3);
            mEditor.putBoolean("hasGot11", false);
        } else {
            mEditor.putString("score", "" + boardView.score);
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < 25; i++)
                temp.append((char) ('0' + boardView.board[i / 5 + 1][i % 5 + 1][0]));
            mEditor.putString("board", temp.toString());
            mEditor.putBoolean("hasGot11", Definition.hasGot11);
            mEditor.putInt("max", boardView.max);
        }
        mEditor.putInt("star", star);
        mEditor.apply();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        beat                  .recycleBitmap();
        rearrange             .recycleBitmap();
        undo                  .setOnClickListener(null);
        beat                  .setOnClickListener(null);
        pause                 .setOnClickListener(null);
        question              .setOnClickListener(null);
        rearrange             .setOnClickListener(null);
        restartButton         .setOnClickListener(null);
        boardView             .removeAllSquareViews();
        boardView             .removeAllViews();
        boardView.combineViews.clear();
        messageHandler        .removeCallbacksAndMessages(null);
        boardView.myHandler   .removeCallbacksAndMessages(null);

        undo           = null;
        beat           = null;
        pause          = null;
        context        = null;
        question       = null;
        starView       = null;
        hintView       = null;
        boardView      = null;
        rearrange      = null;
        scoreView      = null;
        sixAngleStar   = null;
        restartButton  = null;
        messageHandler = null;

        setContentView(R.layout.null_layout);
        System.gc();
        super.onDestroy();
    }

    public class MyDialog extends Dialog implements android.view.View.OnClickListener {
        private CheckView combine_or_not_checkView;
        private CheckView night_mode_checkView;
        private CheckView hardLevel_checkView;
        private boolean willPause;

        public MyDialog(Context context, int theme, int willPause) {
            super(context, theme);
            this.willPause = willPause == Definition.PAUSE;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (willPause) {
                setContentView(R.layout.dialog_pause);
                LinearLayout dialogViewFather = (LinearLayout) findViewById(R.id.dialogViewFather);
                dialogViewFather.setBackgroundResource(Definition.nightMode ?
                        R.drawable.rounded_dialog_dark : R.drawable.rounded_dialog);

                TextView pause_textView = (TextView) findViewById(R.id.dialog_pause_textView);
                pause_textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenWidth / 8);

                setButton(R.id.dialog_pause_settings, mScreenHeight / 60);
                setButton(R.id.dialog_pause_restart, mScreenHeight / 60);
                setButton(R.id.dialog_pause_recharge, mScreenHeight / 60);
                setButton(R.id.dialog_pause_menu, mScreenHeight / 20);
            } else {
                setContentView(R.layout.dialog_question);

                LinearLayout questionViewFather = (LinearLayout) findViewById(R.id.questionViewFather);
                questionViewFather.setBackgroundResource(Definition.nightMode ?
                        R.drawable.rounded_dialog_dark : R.drawable.rounded_dialog);

                TextView question_textView = (TextView) findViewById(R.id.dialog_question_textView);
                question_textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenWidth / 8);

                setHelp(R.id.dialog_question_undo_father, mScreenHeight / 60, Definition.UNDO, "UNDO!\n" +
                        "The most time is 3.\n" + Definition.UNDO_COST + " star/time");
                setHelp(R.id.dialog_question_beat_father, mScreenHeight / 60, Definition.BEAT, "BEAT a cube!\n" +
                        "Click again to cancel.\n" + Definition.BEAT_COST + " star/time");
                setHelp(R.id.dialog_question_rearrange_father, mScreenHeight / 20, Definition.REARRANGE, "REARRANGE!\n" +
                        Definition.REARRANGE_COST + " star/time");
            }
        }

        private void setButton(int id, int bottomMargin) {
            Button bv = (Button) findViewById(id);
            bv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenWidth / 18);
            bv.setOnClickListener(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mScreenWidth * 2 / 5, mScreenHeight / 12);
            lp.bottomMargin = bottomMargin;
            lp.leftMargin = mScreenWidth / 8;
            lp.rightMargin = mScreenWidth / 8;
            bv.setLayoutParams(lp);
        }

        private void setHelp(int linearLayoutId, int bottomMargin, int definitionTAG, String text) {
            LinearLayout linearLayout = (LinearLayout) findViewById(linearLayoutId);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.leftMargin = mScreenWidth / 16;
            lp1.rightMargin = mScreenWidth / 16;
            lp1.bottomMargin = bottomMargin;
            linearLayout.setLayoutParams(lp1);

            ButtonView buttonView = new ButtonView(getContext(), definitionTAG);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(mScreenWidth / 8, mScreenWidth / 8);
            lp2.rightMargin = mScreenWidth / 32;
            buttonView.setLayoutParams(lp2);
            linearLayout.addView(buttonView);

            TextView textView = new TextView(getContext());
            textView.setTextColor(Definition.grayColor);
            textView.setText(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenWidth / 18);
            linearLayout.addView(textView);
        }

        private void setSetting(int textViewId, int linearLayoutId, int bottomMargin, CheckView checkView, boolean definitionTag) {
            TextView textView = (TextView) findViewById(textViewId);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(mScreenWidth * 2 / 5, mScreenHeight / 12);
            lp1.rightMargin = mScreenWidth / 20;
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenWidth / 18);
            textView.setLayoutParams(lp1);

            LinearLayout combine_or_not_father = (LinearLayout) findViewById(linearLayoutId);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.bottomMargin = bottomMargin;
            lp2.leftMargin = mScreenWidth / 16;
            lp2.rightMargin = mScreenWidth / 16;
            combine_or_not_father.setLayoutParams(lp2);

            checkView.setLayoutParams(new LinearLayout.LayoutParams(mScreenWidth / 7, mScreenHeight / 20));
            checkView.setOnClickListener(this);
            checkView.isYes = definitionTag;
            checkView.invalidate();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_pause_settings:
                    setContentView(R.layout.dialog_setting);
                    findViewById(R.id.settingViewFather).
                            setBackgroundResource(Definition.nightMode ?
                                    R.drawable.rounded_dialog_dark : R.drawable.rounded_dialog);

                    TextView dialog_settings_textView = (TextView) findViewById(R.id.dialog_settings_textView);
                    dialog_settings_textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenWidth / 8);

                    combine_or_not_checkView = (CheckView) findViewById(R.id.dialog_settings_combine_or_not_checkView);
                    setSetting(R.id.dialog_settings_combine_or_not_textView, R.id.dialog_settings_combine_or_not_father, mScreenHeight / 60,
                            combine_or_not_checkView, Definition.willCombine);
                    hardLevel_checkView = (CheckView) findViewById(R.id.dialog_settings_level_checkView);
                    setSetting(R.id.dialog_settings_level_textView, R.id.dialog_settings_level_father, mScreenHeight / 60,
                            hardLevel_checkView, Definition.hardLevel);
                    night_mode_checkView = (CheckView) findViewById(R.id.dialog_settings_night_mode_checkView);
                    setSetting(R.id.dialog_settings_night_mode_textView, R.id.dialog_settings_night_mode_father, mScreenHeight / 20,
                            night_mode_checkView, Definition.nightMode);
                    break;
                case R.id.dialog_pause_restart:
                    Definition.gameIsEnd = true;
                    startActivity(new Intent(GameActivity.this, GameActivity.class));
                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                    finish();
                    break;
                case R.id.dialog_pause_recharge:
                    Message msg = new Message();
                    msg.what = Definition.UPDATE_STAR_VIEW;
                    msg.arg1 = Definition.RECHARGE;
                    messageHandler.sendMessage(msg);
                    dismiss();
                    break;
                case R.id.dialog_pause_menu:
                    startActivity(new Intent(GameActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                    finish();
                    break;
                case R.id.dialog_settings_combine_or_not_checkView:
                    combine_or_not_checkView.isClicked = true;
                    combine_or_not_checkView.invalidate();
                    combine_or_not_checkView.isYes = !combine_or_not_checkView.isYes;
                    Definition.willCombine = combine_or_not_checkView.isYes;
                    mEditor = mSharedPreferences.edit();
                    mEditor.putBoolean("willCombine", combine_or_not_checkView.isYes);
                    mEditor.apply();
                    if (Definition.willCombine) {
                        if (boardView.combineViews.size() == 0) boardView.addCombineView();
                        boardView.combineSameView();
                    } else boardView.cancelSameView();
                    break;
                case R.id.dialog_settings_night_mode_checkView:
                    night_mode_checkView.isClicked = true;
                    night_mode_checkView.invalidate();
                    night_mode_checkView.isYes = !night_mode_checkView.isYes;
                    mEditor = mSharedPreferences.edit();
                    mEditor.putBoolean("nightMode", night_mode_checkView.isYes);
                    mEditor.putString("score", "" + boardView.score);
                    mEditor.putInt("star", star);
                    String temp = "";
                    for (int i = 0; i < 25; i++)
                        temp = temp + (char) ('0' + boardView.board[i / 5 + 1][i % 5 + 1][0]);
                    mEditor.putString("board", temp);
                    mEditor.apply();
                    sendMsg(Definition.FINISH_ACTIVITY, night_mode_checkView.isYes ? 1 : 0, 100);
                    break;
                case R.id.dialog_settings_level_checkView:
                    Definition.hardLevel = !Definition.hardLevel;
                    hardLevel_checkView.isClicked = true;
                    hardLevel_checkView.invalidate();
                    hardLevel_checkView.isYes = Definition.hardLevel;
                    mEditor = mSharedPreferences.edit();
                    mEditor.putBoolean("hardLevel", Definition.hardLevel);
                    mEditor.apply();
            }
        }
    }

    private void sendMsg(int messageTag, int arg1, int delayMillis) {
        Message message = new Message();
        message.what = messageTag;
        if (arg1 != -1) message.arg1 = arg1;
        if (delayMillis == 0) messageHandler.sendMessage(message);
        else messageHandler.sendMessageDelayed(message, delayMillis);
    }
}