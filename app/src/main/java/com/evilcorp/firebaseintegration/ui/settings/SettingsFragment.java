package com.evilcorp.firebaseintegration.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.evilcorp.firebaseintegration.R;

/**
 * Created by hristo.stoyanov on 21-Feb-17.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = SettingsFragment.class.getSimpleName();
    private static final String ENABLE_NOTIFICATIONS = "enable_notifications";
    private static final String DISPLAY_NAME = "display_name";
    private static final String RUSSIAN_CURSES = "russian_curses";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference connectionPref = findPreference(key);
        switch (key) {
            case ENABLE_NOTIFICATIONS:
                Log.d(TAG, key);
                break;
            case DISPLAY_NAME:
                connectionPref.setSummary(sharedPreferences.getString(key, ""));
                break;
            case RUSSIAN_CURSES:
                connectionPref.setSummary(sharedPreferences.getString(key, ""));
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
