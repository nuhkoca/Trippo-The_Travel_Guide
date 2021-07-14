package com.nuhkoca.trippo.ui.settings;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.di.GlideApp;
import com.nuhkoca.trippo.helper.AppsExecutor;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.db.repository.FavoriteCountriesRepository;
import com.nuhkoca.trippo.ui.AboutActivity;
import com.nuhkoca.trippo.ui.AuthActivity;
import com.nuhkoca.trippo.util.AlertDialogUtils;
import com.nuhkoca.trippo.util.AppWidgetUtils;
import com.nuhkoca.trippo.util.IntentUtils;
import com.nuhkoca.trippo.util.SnackbarUtils;

import java.util.Objects;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    FavoriteCountriesRepository favoriteCountriesRepository;

    @Inject
    AppsExecutor appsExecutor;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.prefscreen);

        PreferenceManager.setDefaultValues(Objects.requireNonNull(getActivity()), R.xml.prefscreen, false);

        initSummary(getPreferenceScreen());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preference openSourcePref = findPreference(getString(R.string.open_source_key));
        openSourcePref.setOnPreferenceClickListener(preference -> {
            new LibsBuilder()
                    .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                    .withActivityTitle(getString(R.string.open_source_name))
                    .withAutoDetect(true)
                    .start(Objects.requireNonNull(getActivity()));

            return false;
        });

        Preference cachePref = findPreference(getString(R.string.cache_key));
        cachePref.setOnPreferenceClickListener(preference -> {
            if (getActivity() != null) {

                AlertDialogUtils.dialogWithAlert(getActivity(),
                        getString(R.string.cache_warning_title),
                        getString(R.string.cache_warning_text),
                        () -> {
                            View view = getView();

                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                appsExecutor.diskIO().execute(() -> GlideApp.get(getActivity()).clearDiskCache());

                                new SnackbarUtils.Builder()
                                        .setView(view)
                                        .setMessage(getString(R.string.image_cache_info_text))
                                        .setLength(SnackbarUtils.Length.LONG)
                                        .show(getString(R.string.dismiss_action_text), null)
                                        .build();
                            } else {
                                new SnackbarUtils.Builder()
                                        .setView(view)
                                        .setLength(SnackbarUtils.Length.LONG)
                                        .setMessage(getString(R.string.sign_in_alert))
                                        .show(getString(R.string.sign_in_action_text), () -> startActivity(new Intent(getActivity(), AuthActivity.class)));
                            }
                        });
            }

            return false;
        });

        SwitchPreference sensorPref = (SwitchPreference) findPreference(getString(R.string.sensor_key));
        sensorPref.setOnPreferenceClickListener(preference -> {

            View view = getView();

            new SnackbarUtils.Builder()
                    .setView(view)
                    .setMessage(getString(R.string.sensor_warning_text))
                    .setLength(SnackbarUtils.Length.LONG)
                    .show(getString(R.string.dismiss_action_text), null)
                    .build();

            return false;
        });


        Preference aboutPref = findPreference(getString(R.string.about_key));
        aboutPref.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(getActivity(), AboutActivity.class),
                    ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());

            return false;
        });

        Preference updatesPref = findPreference(getString(R.string.updates_key));
        updatesPref.setOnPreferenceClickListener(preference -> {
            new IntentUtils.Builder()
                    .setContext(getActivity())
                    .setAction(IntentUtils.ActionType.GOOGLE_PLAY)
                    .create();

            return false;
        });

        Preference databasePref = findPreference(getString(R.string.database_key));

        databasePref.setOnPreferenceClickListener(preference -> {
            if (getActivity() != null) {

                AlertDialogUtils.dialogWithAlert(getActivity(),
                        getString(R.string.app_db_removing_warning_title),
                        getString(R.string.app_db_removing_warning_text),
                        () -> {
                            View view = getView();

                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                favoriteCountriesRepository.deleteAll();
                                AppWidgetUtils.update(getActivity());

                                new SnackbarUtils.Builder()
                                        .setView(view)
                                        .setMessage(getString(R.string.db_remove_info_text))
                                        .setLength(SnackbarUtils.Length.LONG)
                                        .show(getString(R.string.dismiss_action_text), null)
                                        .build();
                            } else {
                                new SnackbarUtils.Builder()
                                        .setView(view)
                                        .setLength(SnackbarUtils.Length.LONG)
                                        .setMessage(getString(R.string.sign_in_alert))
                                        .show(getString(R.string.sign_in_action_text), () -> startActivity(new Intent(getActivity(), AuthActivity.class)));
                            }
                        });
            }

            return false;
        });

        Preference ttsIntentPref = findPreference(getString(R.string.tts_intent_key));
        ttsIntentPref.setOnPreferenceClickListener(preference -> {
            Intent installIntent = new Intent();
            installIntent.setAction(
                    TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            startActivityForResult(installIntent, Constants.TTS_REQ_CODE);

            return false;
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView lv = view.findViewById(android.R.id.list);
        if (lv != null)
            ViewCompat.setNestedScrollingEnabled(lv, true);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummary(findPreference(key));
        updateSwitchPreferences(findPreference(key));
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updateSummary(p);
            updateSwitchPreferences(p);
        }
    }

    private void updateSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
    }

    private void updateSwitchPreferences(Preference p) {
        if (p instanceof SwitchPreference) {
            if (p.getKey().equals(getString(R.string.notifications_key))) {
                if (getPreferenceManager().getSharedPreferences().getBoolean(getString(R.string.notifications_key), true)) {
                    p.setSummary(getString(R.string.notifications_enabled_text));
                    p.setDefaultValue(getString(R.string.notification_enabled_value));
                } else {
                    p.setSummary(getString(R.string.notifications_disabled_text));
                    p.setDefaultValue(getString(R.string.notification_disabled_value));
                }
            } else if (p.getKey().equals(getString(R.string.bookable_key))) {
                if (getPreferenceManager().getSharedPreferences().getBoolean(getString(R.string.bookable_key), true)) {
                    p.setDefaultValue(getString(R.string.bookable_enabled_value));
                } else {
                    p.setDefaultValue(getString(R.string.bookable_disabled_value));
                }
            } else if (p.getKey().equals(getString(R.string.webview_key))) {
                if (getPreferenceManager().getSharedPreferences().getBoolean(getString(R.string.webview_key), true)) {
                    p.setSummary(getString(R.string.external_browser_enabled_text));
                    p.setDefaultValue(getString(R.string.external_browser_enabled_value));
                } else {
                    p.setSummary(getString(R.string.external_browser_disabled_text));
                    p.setDefaultValue(getString(R.string.external_browser_disabled_value));
                }
            } else {
                ((SwitchPreference) p).setChecked(true);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onStop() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }
}