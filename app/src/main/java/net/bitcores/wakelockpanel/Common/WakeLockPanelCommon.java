package net.bitcores.wakelockpanel.Common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import net.bitcores.wakelockpanel.Service.WakeLockNotificationService;

/**
 * Created by bitcores on 2015-08-12.
 */
public class WakeLockPanelCommon {

    public static final String WAKELOCK_LOG = "wakelockpanel";
    public static final String WAKELOCK_PREF_NAME = "wakelock.shared.prefs";
    public static final String WAKELOCK_TAG = "net.bitcores.wakelockpanel.TAG";
    public static final String WAKELOCK_UPDATE = "net.bitcores.wakelockpanel.COCKTAIL_UPDATE";
    public static final String WAKELOCK_WIDGET_UPDATE = "net.bitcores.wakelockpanel.WIDGET_UPDATE";
    public static final Integer WAKELOCK_NOTIFICATION_ID = 493823;

    public static final String WAKELOCK_PREF_USENOTIFICATION = "useNotification";
    public static final String WAKELOCK_PREF_ONBOOT = "onBoot";
    public static final String WAKELOCK_PREF_LOCKTYPE = "lockType";
    public static final String WAKELOCK_PREF_USETIMER = "useTimer";
    public static final String WAKELOCK_PREF_LOCKTIMER = "lockTimer";
    public static final String WAKELOCK_PREF_ACTIVATEIT = "activateIt";

    public static final int WAKELOCK_ACTION_TOGGLENOW = 10;
    public static final int WAKELOCK_ACTION_TOGGLEMODE = 20;
    public static final int WAKELOCK_ACTION_INCREMENT = 30;
    public static final int WAKELOCK_ACTION_TIMER = 40;
    public static final int WAKELOCK_ACTION_INPUT = 50;

    public static long endTime = 0;
    public static Integer minutes = 10;
    public static Integer seconds = 0;
    public static Integer timerSelect = 1;
    public static Boolean coctailEnabled = false;

    private static SharedPreferences prefs = null;
    private Context mContext;

    public WakeLockPanelCommon() {

    }

    public void initSettings(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(WAKELOCK_PREF_NAME, Context.MODE_PRIVATE);
            mContext = context;
        }
    }

    public String getMinutes() {
        String mins = minutes.toString();
        return (mins.length() == 1) ? "0" + mins : mins;
    }

    public String getSeconds() {
        String secs = seconds.toString();
        return (secs.length() == 1) ? "0" + secs : secs;
    }

    public void toggleTimerMode() {
        prefs.edit()
            .putBoolean(WAKELOCK_PREF_USETIMER, !getTimer())
            .apply();
    }

    public void toggleWakeLockMode() {
        int newMode = 0;
        if (getMode() == 0) {
            newMode = 1;
        }
        prefs.edit()
            .putString(WAKELOCK_PREF_LOCKTYPE, String.valueOf(newMode))
            .apply();
    }

    public void selectTimer(int mode) {
        timerSelect = mode;
    }

    public void resetTimer() {
        minutes = 10;
        seconds = 0;
    }

    public int getMode() {
        return Integer.parseInt(prefs.getString(WAKELOCK_PREF_LOCKTYPE, "0"));
    }

    public boolean testMode() {
        return (prefs.getString(WAKELOCK_PREF_LOCKTYPE, "0").equals("1"));
    }

    public boolean testOnBoot() {
        return prefs.getBoolean(WAKELOCK_PREF_ONBOOT, false);
    }

    public boolean testNotification() {
        return prefs.getBoolean(WAKELOCK_PREF_USENOTIFICATION, false);
    }

    public void doNotification(Context context) {
        WakeLockNotificationService wakeLockNotificationService = new WakeLockNotificationService();
        if (testNotification()) {
            wakeLockNotificationService.makeNotification(context);
        } else {
            wakeLockNotificationService.destroyNotification(context);
        }
    }

    public boolean getTimer() { return prefs.getBoolean(WAKELOCK_PREF_USETIMER, false); }

    public void updateTimer() {
        // update the timer value in preferences. broadcast not required
    }

    public void broadcastUpdate(Context context) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(WAKELOCK_UPDATE);
        context.sendBroadcast(broadcastIntent);
    }

}
