package com.tyq_code.get11.Controller;

/**
 * THE MAIN DEFINITION OF GLOBAL VARIABLES IN THE GAME
 */
public class Definition {
    //ThemeColor
    public static final int lightColor = 0xffefefef;
    public static final int darkColor  = 0xff000000;
    public static final int greenColor = 0xff99b717;
    public static final int grayColor  = 0xff666666;
    public static final int noColor    = 0x00000000;
    public static final int color[] = {0xfffd5971, 0xffffb730, 0xff2cafdb, 0xffbc6dfa,
                                       0xffa5c61f, 0xff15b1a4, 0xff0a5191, 0xffc31960,
                                       0xffd85624, 0xff115955, 0xff44125b};
    //Width and Height of Device
    public static       int mScreenWidth ;
    public static       int mScreenHeight;
    //Tools Tag
    public static final int PAUSE     = 1;
    public static final int QUESTION  = 2;
    public static final int UNDO      = 3;
    public static final int BEAT      = 4;
    public static final int REARRANGE = 5;
    public static final int RESTART   = 6;
    public static final int RECHARGE  = 7;
    //Handler Message Tag
    public static final int SCORE                      =  1;
    public static final int GAME_IS_END                =  2;
    public static final int YOU_GET_11                 =  3;
    public static final int REMOVE_IS_OK               =  4;
    public static final int MOVE_IS_OK                 =  5;
    public static final int FALL_IS_OK                 =  6;
    public static final int FALL_NEW_SQUARE_VIEW_IS_OK =  7;
    public static final int GAME_OVER_VIEW_IS_SHOWN    =  8;
    public static final int FINISH_ACTIVITY            =  9;
    public static final int SET_HINT_VIEW              = 10;
    public static final int UPDATE_STAR_VIEW           = 11;
    public static final int ADD_STAR                   = 12;
    //Money Cost
    public static final int UNDO_COST      = 5;
    public static final int BEAT_COST      = 10;
    public static final int REARRANGE_COST = 10;
    //GameActivity static Variables
    public static boolean willCombine   = true;
    public static boolean gameIsEnd     = false;
    public static boolean hasGot11      = false;
    public static boolean nightMode     = false;
    public static boolean hardLevel     = false;
    public static boolean willBeat      = false;
    public static boolean willUndo      = false;
    public static boolean willRearrange = false;
}
