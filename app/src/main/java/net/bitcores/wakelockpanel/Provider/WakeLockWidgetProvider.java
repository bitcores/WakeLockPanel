package net.bitcores.wakelockpanel.Provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.RemoteViews;

import net.bitcores.wakelockpanel.Common.WakeLockPanelCommon;
import net.bitcores.wakelockpanel.R;
import net.bitcores.wakelockpanel.Service.WakeLockPanelService;

/**
 * Created by bitcores on 2015-07-20.
 */
public class WakeLockWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();

        if (action.equals(WakeLockPanelCommon.WAKELOCK_UPDATE) || action.equals(WakeLockPanelCommon.WAKELOCK_WIDGET_UPDATE)) {
            ComponentName wakeLockWidget = new ComponentName(context, WakeLockWidgetProvider.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] widgetIds = appWidgetManager.getAppWidgetIds(wakeLockWidget);

            updateWidgets(context, appWidgetManager, widgetIds);
        } else if (action.equals(WakeLockPanelCommon.WAKELOCK_TAG)) {
            Intent serviceIntent = new Intent(context, WakeLockPanelService.class);
            Bundle extras = intent.getExtras();

            serviceIntent.putExtras(extras);

            context.startService(serviceIntent);
        }

    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        WakeLockPanelService wakeLockPanelService = new WakeLockPanelService();
        wakeLockPanelService.clearLock();

        super.onDisabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] widgetIds) {
        updateWidgets(context, appWidgetManager, widgetIds);

        super.onUpdate(context, appWidgetManager, widgetIds);
    }

    private void updateWidgets(Context context, AppWidgetManager appWidgetManager, int[] widgetIds) {
        WakeLockPanelService wakeLockPanelService = new WakeLockPanelService();
        WakeLockPanelCommon wakeLockPanelCommon = new WakeLockPanelCommon();
        wakeLockPanelCommon.initSettings(context);

        RemoteViews layout = new RemoteViews(context.getPackageName(), R.layout.wakelockwidget_layout);

        Boolean portrait = (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
        Matrix mat = new Matrix();
        mat.postRotate(270);

        //  MAKE PENDING INTENTS
        PendingIntent toggleNowPending = makePending(context, WakeLockPanelCommon.WAKELOCK_ACTION_TOGGLENOW, 0);
        PendingIntent modePending = makePending(context, WakeLockPanelCommon.WAKELOCK_ACTION_TOGGLEMODE, 0);
        PendingIntent timerPending = makePending(context, WakeLockPanelCommon.WAKELOCK_ACTION_TIMER, 0);
        PendingIntent plusPending = makePending(context, WakeLockPanelCommon.WAKELOCK_ACTION_INCREMENT, 0);
        PendingIntent minusPending = makePending(context, WakeLockPanelCommon.WAKELOCK_ACTION_INCREMENT, 1);
        PendingIntent minutePending = makePending(context, WakeLockPanelCommon.WAKELOCK_ACTION_INPUT, 0);
        PendingIntent secondPending = makePending(context, WakeLockPanelCommon.WAKELOCK_ACTION_INPUT, 1);

        //  ATTACH PENDING INTENTS
        layout.setOnClickPendingIntent(R.id.wakeLockNowToggle, toggleNowPending);
        layout.setOnClickPendingIntent(R.id.wakeLockMode, modePending);
        layout.setOnClickPendingIntent(R.id.timerButton, timerPending);
        layout.setOnClickPendingIntent(R.id.plusButton, plusPending);
        layout.setOnClickPendingIntent(R.id.minusButton, minusPending);
        layout.setOnClickPendingIntent(R.id.minuteText, minutePending);
        layout.setOnClickPendingIntent(R.id.secondText, secondPending);

        //  SET RESOURCES
        Bitmap bMap;
        if (wakeLockPanelService.testToggleActive()) {
            bMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lockon);

            if (portrait) {
                bMap = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), mat, true);
            }
            layout.setImageViewBitmap(R.id.wakeLockNowToggle, bMap);
            layout.setInt(R.id.minuteText, "setBackgroundColor", 0x00FFFFFF);
            layout.setInt(R.id.secondText, "setBackgroundColor", 0x00FFFFFF);
        } else {
            bMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lockoff);

            if (portrait) {
                bMap = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), mat, true);
            }
            layout.setImageViewBitmap(R.id.wakeLockNowToggle, bMap);
            if (WakeLockPanelCommon.timerSelect == 0) {
                layout.setInt(R.id.minuteText, "setBackgroundColor", 0x88555555);
                layout.setInt(R.id.secondText, "setBackgroundColor", 0x00FFFFFF);
            } else {
                layout.setInt(R.id.minuteText, "setBackgroundColor", 0x00FFFFFF);
                layout.setInt(R.id.secondText, "setBackgroundColor", 0x88555555);
            }
        }

        if (!wakeLockPanelCommon.testMode()) {
            bMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.modebright);
        } else {
            bMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.modedim);
        }
        if (portrait) {
            bMap = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), mat, true);
        }
        layout.setImageViewBitmap(R.id.wakeLockMode, bMap);

        if (!wakeLockPanelCommon.getTimer()) {
            layout.setImageViewResource(R.id.timerButton, R.drawable.unchecked);
        } else {
            layout.setImageViewResource(R.id.timerButton, R.drawable.checked);
        }

        layout.setImageViewResource(R.id.plusButton, R.drawable.plus);
        layout.setImageViewResource(R.id.minusButton, R.drawable.minus);
        layout.setTextViewText(R.id.minuteText, wakeLockPanelCommon.getMinutes());
        layout.setTextViewText(R.id.secondText, wakeLockPanelCommon.getSeconds());

        for (int wId: widgetIds) {
            appWidgetManager.updateAppWidget(wId, layout);
        }
    }

    private PendingIntent makePending(Context context, int action, int mode) {
        Bundle extras = new Bundle();
        extras.putInt("ACTION", action);
        extras.putInt("MODE", mode);

        Intent intent = new Intent(context, WakeLockWidgetProvider.class);
        intent.setAction(WakeLockPanelCommon.WAKELOCK_TAG);
        intent.putExtras(extras);

        return PendingIntent.getBroadcast(context, (action + mode), intent, 0);
    }


}
