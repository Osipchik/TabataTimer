package com.example.lab2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class Settings extends PreferenceActivity {
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("theme", true)) {
            setTheme(R.style.Theme_AppCompat);
        }
        Configuration configuration = new Configuration();
        configuration.locale = initLanguage();
        configuration.fontScale = initFontSize();

        getBaseContext().getResources().updateConfiguration(configuration, null);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new ChangeSettingsFragment()).commit();
        super.onCreate(savedInstanceState);
    }

    private Locale initLanguage(){
        Locale locale = new Locale(preferences.getString("test_lang", "ru"));
        Locale.setDefault(locale);

        return locale;
    }

    private float initFontSize(){
        String font = preferences.getString("font_size", "1.0");

        return Float.parseFloat(font);
    }

    public static class ChangeSettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preference);

            Preference button = findPreference("DeleteAll");

            ListPreference language = (ListPreference) findPreference("test_lang");
            Preference theme = findPreference("theme");

            ListPreference font = (ListPreference) findPreference("font_size");

            theme.setOnPreferenceChangeListener(this::onThemeChange);
            language.setOnPreferenceChangeListener(this::onLanguageChange);
            button.setOnPreferenceClickListener(this::onDeleteClick);
            font.setOnPreferenceChangeListener(this::onFontChange);
        }


        private boolean onLanguageChange(Preference preference, Object newValue) {
            Locale locale = new Locale(newValue.toString());

            Locale.setDefault(locale);
            Configuration configuration = new Configuration();
            configuration.locale = locale;
            getActivity().getResources().updateConfiguration(configuration, null);
            getActivity().recreate();

            return true;
        }


        private boolean onFontChange(Preference preference, Object newValue) {
            Configuration configuration = getResources().getConfiguration();

            configuration.fontScale = Float.parseFloat(newValue.toString());

            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            getActivity().getBaseContext().getResources().updateConfiguration(configuration, metrics);
            getActivity().recreate();

            return true;
        }

        private boolean onThemeChange(Preference preference, Object newValue) {
            if ((boolean) newValue) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            getActivity().recreate();

            return true;
        }


        private boolean onDeleteClick(Preference preference) {

            Intent intent = new Intent();
            getActivity().setResult(RESULT_OK, intent);
            getActivity().finish();

            return true;
        }
    }
}