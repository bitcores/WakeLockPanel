package net.bitcores.wakelockpanel.Activity;

import android.app.Activity;
import android.os.Bundle;


/**
 * Created by Nakima on 10/07/2016.
 */
public class WakeLockSettingsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new WakeLockSettingsFragment())
                .commit();
    }


}
