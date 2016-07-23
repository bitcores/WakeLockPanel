package net.bitcores.wakelockpanel.Activity;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import net.bitcores.wakelockpanel.Common.WakeLockPanelCommon;
import net.bitcores.wakelockpanel.R;


/**
 * Created by Nakima on 10/07/2016.
 * modified from
 * http://stackoverflow.com/questions/5533078/timepicker-in-preferencescreen
 *
 * and others
 */
public class TimerPreference extends DialogPreference {
    private NumberPicker minutePicker = null;
    private NumberPicker secondPicker = null;

    private static final int MAX_VALUE = 59;
    private static final int MIN_VALUE = 0;

    public TimerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.dialog_timerlayout);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }
    
    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        minutePicker = (NumberPicker) v.findViewById(R.id.minutePicker);
        secondPicker = (NumberPicker) v.findViewById(R.id.secondPicker);
        minutePicker.setMaxValue(MAX_VALUE);
        minutePicker.setMinValue(MIN_VALUE);
        secondPicker.setMaxValue(MAX_VALUE);
        secondPicker.setMinValue(MIN_VALUE);

        minutePicker.setValue(WakeLockPanelCommon.minutes);
        secondPicker.setValue(WakeLockPanelCommon.seconds);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        WakeLockPanelCommon wakeLockPanelCommon = new WakeLockPanelCommon();

        if (positiveResult) {
            WakeLockPanelCommon.minutes = minutePicker.getValue();
            WakeLockPanelCommon.seconds = secondPicker.getValue();

            wakeLockPanelCommon.broadcastUpdate(getContext());
        }
    }

}
