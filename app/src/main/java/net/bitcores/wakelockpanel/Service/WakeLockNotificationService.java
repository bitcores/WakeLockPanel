package net.bitcores.wakelockpanel.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import net.bitcores.wakelockpanel.Activity.WakeLockSettingsActivity;
import net.bitcores.wakelockpanel.Common.WakeLockPanelCommon;
import net.bitcores.wakelockpanel.Provider.WakeLockWidgetProvider;
import net.bitcores.wakelockpanel.R;

/**
 * Created by Nakima on 16/07/2016.
 */
public class WakeLockNotificationService {

    public WakeLockNotificationService() {

    }

    public void makeNotification(Context context) {
        WakeLockPanelCommon wakeLockPanelCommon = new WakeLockPanelCommon();
        WakeLockPanelService wakeLockPanelService = new WakeLockPanelService();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent activityIntent = new Intent(context, WakeLockSettingsActivity.class);
        Intent wakeLockIntent = new Intent(context, WakeLockWidgetProvider.class);

        Bundle extras = new Bundle();
        extras.putInt("ACTION", WakeLockPanelCommon.WAKELOCK_ACTION_TOGGLENOW);
        extras.putInt("MODE", 0);
        wakeLockIntent.setAction(WakeLockPanelCommon.WAKELOCK_TAG);
        wakeLockIntent.putExtras(extras);

        PendingIntent activityPending = PendingIntent.getActivity(context, WakeLockPanelCommon.WAKELOCK_NOTIFICATION_ID, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent wakeLockPending = PendingIntent.getBroadcast(context, WakeLockPanelCommon.WAKELOCK_ACTION_TOGGLENOW, wakeLockIntent, 0);

        String notificationTitle = context.getResources().getString(R.string.app_name);
        String notificationText = context.getResources().getString(R.string.wakelock_locktype) +
                context.getResources().getString(R.string.timerdivider) +
                context.getResources().getStringArray(R.array.locktype_entries)[wakeLockPanelCommon.getMode()];
        String actionText = context.getResources().getString(R.string.activatewl);
        if (wakeLockPanelService.testToggleActive()) {
            actionText = context.getResources().getString(R.string.deactivatewl);
        }

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setContentIntent(activityPending)
                .setSmallIcon(R.drawable.wakelocklogo)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.wakelocklogo))
                .addAction(0, actionText, wakeLockPending);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        notificationManager.notify(WakeLockPanelCommon.WAKELOCK_NOTIFICATION_ID, notification);
    }

    public void destroyNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(WakeLockPanelCommon.WAKELOCK_NOTIFICATION_ID);
    }
}
