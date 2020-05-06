package com.sc2toolslab.sc2bm.ui.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;

public class SettingsFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		final ListPreference selectedConfig = (ListPreference) findPreference(getString(R.string.pref_selectedConfig));

		/*if (AppConstants.IS_FREE) {
			selectedConfig.setEntries(getResources().getStringArray(R.array.versionsFree));
			selectedConfig.setEntryValues(getResources().getStringArray(R.array.versionsValues));
			selectedConfig.setDefaultValue(AppConstants.HOTS_Config);
		} else {
		*/
		selectedConfig.setEntries(getResources().getStringArray(R.array.versionsFull));
		selectedConfig.setEntryValues(getResources().getStringArray(R.array.versionsValues));
		selectedConfig.setDefaultValue(AppConstants.LOTV_Config);
		//}

		selectedConfig.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				final String newConfig = newValue.toString();
				BuildOrdersProvider.getInstance(getActivity()).setVersion(newConfig);
				return true;
			}
		});

		CheckBoxPreference sortFavorite = (CheckBoxPreference) findPreference(getString(R.string.pref_sort_favorites));
		sortFavorite.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Boolean newConfig = (Boolean) newValue;

				BuildOrdersProvider.getInstance(getActivity()).setSortFavorites(newConfig);

				return true;
			}
		});

		CheckBoxPreference sortDate = (CheckBoxPreference) findPreference(getString(R.string.pref_sort_date));
		sortDate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Boolean newConfig = (Boolean) newValue;

				BuildOrdersProvider.getInstance(getActivity()).setSortDate(newConfig);

				return true;
			}
		});
	}
}
