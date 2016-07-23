package net.bitcores.wakelockpanel.Activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import net.bitcores.wakelockpanel.Common.WakeLockPanelCommon;
import net.bitcores.wakelockpanel.R;
import net.bitcores.wakelockpanel.Service.WakeLockPanelService;

/**
 * Created by Nakima on 17/07/2016.
 */
public class WakeLockSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private WakeLockPanelService wakeLockPanelService;
    private WakeLockPanelCommon wakeLockPanelCommon;
    private Context context;

    private Preference lockType;
    private Preference useTimer;
    private Preference lockTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        wakeLockPanelService = new WakeLockPanelService();
        wakeLockPanelCommon = new WakeLockPanelCommon();
        wakeLockPanelCommon.initSettings(context);

        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName(WakeLockPanelCommon.WAKELOCK_PREF_NAME);

        // This static call will reset default values only on the first ever read
        PreferenceManager.setDefaultValues(context, R.xml.wakelock_settings, false);

        addPreferencesFromResource(R.xml.wakelock_settings);
        Preference activateIt = findPreference(WakeLockPanelCommon.WAKELOCK_PREF_ACTIVATEIT);
        activateIt.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                toggleActivate();
                return false;
            }
        });

        lockType = findPreference(WakeLockPanelCommon.WAKELOCK_PREF_LOCKTYPE);
        useTimer = findPreference(WakeLockPanelCommon.WAKELOCK_PREF_USETIMER);
        lockTimer = findPreference(WakeLockPanelCommon.WAKELOCK_PREF_LOCKTIMER);

        wakeLockPanelCommon.doNotification(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        wakeLockPanelCommon.doNotification(context);
        wakeLockPanelCommon.broadcastUpdate(context);
    }

    private void toggleActivate() {
        if (wakeLockPanelService.testToggleActive()) {
            lockType.setEnabled(true);
            useTimer.setEnabled(true);
            lockTimer.setEnabled(true);
        } else {
            lockType.setEnabled(false);
            useTimer.setEnabled(false);
            lockTimer.setEnabled(false);
        }

        sendBroadcast(WakeLockPanelCommon.WAKELOCK_ACTION_TOGGLENOW, 0);
    }

    private void sendBroadcast(int action, int mode) {
        Intent serviceIntent = new Intent(context, WakeLockPanelService.class);
        Bundle extras = new Bundle();
        extras.putInt("ACTION", action);
        extras.putInt("MODE", mode);

        serviceIntent.putExtras(extras);
        context.startService(serviceIntent);
    }
}
