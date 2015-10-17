package net.bitcores.wakelockpanel.Common;

/**
 * Created by bitcores on 2015-08-12.
 */
public class WakeLockPanelCommon {

    public static final String WAKELOCK_TAG = "net.bitcores.wakelockpanel.TAG";
    public static final String WAKELOCK_UPDATE = "net.bitcores.wakelockpanel.COCKTAIL_UPDATE";

    public static final int WAKELOCK_ACTION_TOGGLENOW = 10;
    public static final int WAKELOCK_ACTION_TOGGLEMODE = 20;
    public static final int WAKELOCK_ACTION_INCREMENT = 30;
    public static final int WAKELOCK_ACTION_TIMER = 40;
    public static final int WAKELOCK_ACTION_INPUT = 50;

    public static int WAKELOCK_MODE = 0;
    public static Integer minutes = 10;
    public static Integer seconds = 0;
    public static Integer timerSelect = 1;
    public static Boolean timerMode = false;
    public static long endTime = 0;


}
