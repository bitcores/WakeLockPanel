package net.bitcores.wakelockpanel.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import net.bitcores.wakelockpanel.Common.WakeLockPanelCommon;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by bitcores on 2015-07-20.
 */
public class WakeLockPanelService extends Service {
    private Context mContext;
    private WakeLockPanelCommon wakeLockPanelCommon;
    private static PowerManager pm;
    private static PowerManager.WakeLock wakeLock = null;

    private static ArrayList<Integer> modeList = new ArrayList<Integer>(Arrays.asList(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, PowerManager.SCREEN_DIM_WAKE_LOCK));

    public static Handler mHandler = new Handler();

    public WakeLockPanelService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            Bundle extras = intent.getExtras();
            int action = extras.getInt("ACTION", 0);
            int mode = extras.getInt("MODE", 0);

            if (action == WakeLockPanelCommon.WAKELOCK_ACTION_TOGGLENOW) {
                if (wakeLock != null) {
                    clearLock();
                    Toast.makeText(mContext, "Wakelock disabled", Toast.LENGTH_SHORT).show();
                } else {
                    wakeLock = pm.newWakeLock(modeList.get(wakeLockPanelCommon.getMode()), WakeLockPanelCommon.WAKELOCK_TAG);
                    if (wakeLockPanelCommon.getTimer()) {
                        long ms = ((WakeLockPanelCommon.minutes * 60) + WakeLockPanelCommon.seconds) * 1000;
                        WakeLockPanelCommon.endTime = System.currentTimeMillis() + ms;
                        acquireLock(ms);
                    } else {
                        WakeLockPanelCommon.endTime = System.currentTimeMillis() + 58000;
                        acquireLock(60000);
                    }

                    mHandler.postDelayed(r, 1000);
                    Toast.makeText(mContext, "Wakelock enabled", Toast.LENGTH_SHORT).show();
                }
                wakeLockPanelCommon.doNotification(mContext);
            } else if (wakeLock == null) {
                if (action == WakeLockPanelCommon.WAKELOCK_ACTION_TOGGLEMODE) {
                    wakeLockPanelCommon.toggleWakeLockMode();
                } else if (action == WakeLockPanelCommon.WAKELOCK_ACTION_INCREMENT) {
                    if (WakeLockPanelCommon.timerSelect == 0) {
                        incrementMinutes(mode);
                    } else {
                        incrementSeconds(mode);
                    }
                } else if (action == WakeLockPanelCommon.WAKELOCK_ACTION_TIMER) {
                    wakeLockPanelCommon.toggleTimerMode();

                } else if (action == WakeLockPanelCommon.WAKELOCK_ACTION_INPUT) {
                    wakeLockPanelCommon.selectTimer(mode);
                }
            }

            wakeLockPanelCommon.broadcastUpdate(mContext);
        }

        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
        wakeLockPanelCommon = new WakeLockPanelCommon();
        wakeLockPanelCommon.initSettings(mContext);

        pm = (PowerManager)getSystemService(WakeLockPanelService.POWER_SERVICE);
    }

    @Override
    public void onDestroy() {
        //  if this is getting destroyed then release wakelock
        clearLock();

        super.onDestroy();
    }

    private void acquireLock(long ms) {
        if (wakeLock != null) {
            try {
                wakeLock.acquire(ms);
            } catch (Throwable th) {
                // somehow the wakelock isnt assigned before being activated
            }
        }
    }

    public void clearLock() {
        if (wakeLock != null) {
            try {
                wakeLock.release();
            } catch (Throwable th) {
                // for some reason wakelock was released, the cunt
            }
            wakeLock = null;
        }
    }

    public boolean testToggleActive() {
        return (wakeLock != null);
    }

    public void incrementSeconds(int mode) {
        int remainder = WakeLockPanelCommon.seconds % 5;
        if (mode == 0) {
            WakeLockPanelCommon.seconds += (5 - remainder);
            if (WakeLockPanelCommon.seconds >= 60) {
                incrementMinutes(0);
                WakeLockPanelCommon.seconds = 0;
            }
        } else {
            WakeLockPanelCommon.seconds -= (remainder > 0) ? remainder : 5;
            if (WakeLockPanelCommon.seconds < 0) {
                incrementMinutes(1);
                WakeLockPanelCommon.seconds = 55;
            }
        }
    }

    public void incrementMinutes(int mode) {
        if (mode == 0) {
            WakeLockPanelCommon.minutes++;
            if (WakeLockPanelCommon.minutes >= 60) {
                WakeLockPanelCommon.minutes = 0;
            }
        } else {
            WakeLockPanelCommon.minutes--;
            if (WakeLockPanelCommon.minutes < 0) {
                WakeLockPanelCommon.minutes = 59;
            }
        }
    }

    final Runnable r = new Runnable() {
        public void run() {
        if (wakeLock != null) {
            long time = System.currentTimeMillis();

            if (wakeLockPanelCommon.getTimer()) {
                WakeLockPanelCommon.minutes = (int) ((WakeLockPanelCommon.endTime - time) / 1000) / 60;
                WakeLockPanelCommon.seconds = (int) ((WakeLockPanelCommon.endTime - time) / 1000) % 60;

                if (time >= WakeLockPanelCommon.endTime) {
                    clearLock();
                    wakeLockPanelCommon.resetTimer();
                } else {
                    mHandler.postDelayed(this, 1000);
                }
            } else {
                if (time >= WakeLockPanelCommon.endTime) {
                    WakeLockPanelCommon.endTime = System.currentTimeMillis() + 58000;
                    acquireLock(60000);
                }
                mHandler.postDelayed(this, 1000);
            }

            wakeLockPanelCommon.broadcastUpdate(mContext);
        }
        }
    };

}
