package net.bitcores.wakelockpanel.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.bitcores.wakelockpanel.Common.WakeLockPanelCommon;
import net.bitcores.wakelockpanel.Service.WakeLockNotificationService;

/**
 * Created by Nakima on 10/07/2016.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        WakeLockNotificationService wakeLockNotificationService = new WakeLockNotificationService();
        WakeLockPanelCommon wakeLockPanelCommon = new WakeLockPanelCommon();
        wakeLockPanelCommon.initSettings(context);

        if (wakeLockPanelCommon.testOnBoot() && wakeLockPanelCommon.testNotification()) {
            wakeLockNotificationService.makeNotification(context);
        }
    }
}