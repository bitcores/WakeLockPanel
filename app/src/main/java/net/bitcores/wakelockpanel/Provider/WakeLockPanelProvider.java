package net.bitcores.wakelockpanel.Provider;

import net.bitcores.wakelockpanel.Common.WakeLockPanelCommon;
import net.bitcores.wakelockpanel.R;
import net.bitcores.wakelockpanel.Service.WakeLockPanelService;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.samsung.android.sdk.look.cocktailbar.SlookCocktailManager;
import com.samsung.android.sdk.look.cocktailbar.SlookCocktailProvider;

/**
 * Created by bitcores on 2015-07-20.
 */
public class WakeLockPanelProvider extends SlookCocktailProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();

        if (action.equals(WakeLockPanelCommon.WAKELOCK_UPDATE)) {
            ComponentName wakeLockCocktail = new ComponentName(context, WakeLockPanelProvider.class);
            SlookCocktailManager cocktailBarManager = SlookCocktailManager.getInstance(context);
            int[] cocktailIds = cocktailBarManager.getCocktailIds(wakeLockCocktail);

            updateCocktails(context, cocktailBarManager, cocktailIds);
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
    public void onUpdate(Context context, SlookCocktailManager cocktailBarManager, int[] cocktailIds) {
        updateCocktails(context, cocktailBarManager, cocktailIds);

        super.onUpdate(context, cocktailBarManager, cocktailIds);
    }

    private void updateCocktails(Context context, SlookCocktailManager cocktailBarManager, int[] cocktailIds) {
        WakeLockPanelService wakeLockPanelService = new WakeLockPanelService();
        RemoteViews layout = new RemoteViews(context.getPackageName(), R.layout.wakelockpanel_layout);

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
        if (wakeLockPanelService.testToggleActive()) {
            layout.setImageViewResource(R.id.wakeLockNowToggle, R.drawable.lockon);
            layout.setInt(R.id.minuteText, "setBackgroundColor", 0x00FFFFFF);
            layout.setInt(R.id.secondText, "setBackgroundColor", 0x00FFFFFF);
        } else {
            layout.setImageViewResource(R.id.wakeLockNowToggle, R.drawable.lockoff);
            if (WakeLockPanelCommon.timerSelect == 0) {
                layout.setInt(R.id.minuteText, "setBackgroundColor", 0x88555555);
                layout.setInt(R.id.secondText, "setBackgroundColor", 0x00FFFFFF);
            } else {
                layout.setInt(R.id.minuteText, "setBackgroundColor", 0x00FFFFFF);
                layout.setInt(R.id.secondText, "setBackgroundColor", 0x88555555);
            }
        }

        if (!wakeLockPanelService.testMode()) {
            layout.setImageViewResource(R.id.wakeLockMode, R.drawable.modebright);
        } else {
            layout.setImageViewResource(R.id.wakeLockMode, R.drawable.modedim);
        }

        if (!wakeLockPanelService.testTimer()) {
            layout.setImageViewResource(R.id.timerButton, R.drawable.unchecked);
        } else {
            layout.setImageViewResource(R.id.timerButton, R.drawable.checked);
        }

        layout.setImageViewResource(R.id.plusButton, R.drawable.plus);
        layout.setImageViewResource(R.id.minusButton, R.drawable.minus);
        layout.setTextViewText(R.id.minuteText, wakeLockPanelService.getMinutes());
        layout.setTextViewText(R.id.secondText, wakeLockPanelService.getSeconds());


        for (int i = 0; i < cocktailIds.length; i++) {
            cocktailBarManager.updateCocktail(cocktailIds[i], layout);
        }
    }

    private PendingIntent makePending(Context context, int action, int mode) {
        Bundle extras = new Bundle();
        extras.putInt("ACTION", action);
        extras.putInt("MODE", mode);

        Intent intent = new Intent(context, WakeLockPanelProvider.class);
        intent.setAction(WakeLockPanelCommon.WAKELOCK_TAG);
        intent.putExtras(extras);

        return PendingIntent.getBroadcast(context, (action + mode), intent, 0);
    }


}
