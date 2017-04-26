package es.deusto.onthestreet.onthestreet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by aitor on 4/22/17.
 * This class loads the layout of the Setting/Preferences page.
 * Binds a listener for whenever a change is done in the Setting/Preferences page.
 */

public class MySettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preferences);

        // Bind a callback listener for changes in preferences page:
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals("Theme")) {
        Log.i("MYLOG", "(Theme) Has seleccionado: " + sharedPreferences.getString(key,""));
//        if (sharedPreferences.getString(key,"").equals("AppTheme2")) {
//            theme_color = "AppTheme2";
//        } else {
//            theme_color = "AppTheme";
//        }
    }
}
