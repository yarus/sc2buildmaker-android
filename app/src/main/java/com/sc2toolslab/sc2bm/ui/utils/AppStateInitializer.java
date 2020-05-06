package com.sc2toolslab.sc2bm.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AppStateInitializer {
	private AppStateInitializer() {}

	public static void init(Context context){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

		_loadVersion(settings, context);
		_loadSort(settings, context);
		_loadFaction(settings, context);
		_loadPlayerSettings(settings, context);

		BuildOrdersProvider.getInstance(context).loadFavoriteBuilds(_getFavoriteBuilds(settings));
	}

	private static void _loadVersion(SharedPreferences settings, Context context) {
		String version = settings.getString(context.getString(R.string.pref_selectedConfig), "");

		if(version == null || version.equals("") || _isVersionOutdated(version)) {

			//if (AppConstants.IS_FREE) {
				//version = AppConstants.HOTS_Config;
			//} else {
			version = AppConstants.LOTV_Config;
			//}

			SharedPreferences.Editor editor = settings.edit();
			editor.putString(context.getString(R.string.pref_selectedConfig), version);
			editor.apply();
		}

		BuildOrdersProvider.getInstance(context).setVersion(version);
	}

	private static boolean _isVersionOutdated(String version) {
		return AppConstants.OutdatedVersions.contains(version);
	}

	private static void _loadSort(SharedPreferences settings, Context context) {
		Boolean favValue = settings.getBoolean(context.getString(R.string.pref_sort_favorites), true);
		Boolean dateValue = settings.getBoolean(context.getString(R.string.pref_sort_date), true);

		BuildOrdersProvider.getInstance(context).setSortFavorites(favValue);
		BuildOrdersProvider.getInstance(context).setSortDate(dateValue);
	}

	private static void _loadFaction(SharedPreferences settings, Context context) {
		String factionStr = settings.getString("SelectedFaction", "");

		if (factionStr != null && !factionStr.equals("")) {
			RaceEnum faction = RaceEnum.valueOf(factionStr);
			BuildOrdersProvider.getInstance(context).setFaction(faction);

			return;
		}

		SharedPreferences.Editor editor = settings.edit();

		BuildOrdersProvider.getInstance(context).setFaction(RaceEnum.NotDefined);
		editor.putString("SelectedFaction", RaceEnum.NotDefined.name());

		editor.apply();
	}

	private static void _loadPlayerSettings(SharedPreferences settings, Context context) {
		String gameSpeed = settings.getString(context.getString(R.string.pref_game_speed), "");
		if (gameSpeed == null || gameSpeed.equals("")) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(context.getString(R.string.pref_game_speed), "Faster");
			editor.apply();
		}

		String startSecond = settings.getString(context.getString(R.string.pref_start_second), "");
		if (startSecond == null || startSecond.equals("")) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(context.getString(R.string.pref_start_second), "12");
			editor.apply();
		}
	}

	private static List<String> _getFavoriteBuilds(SharedPreferences settings) {
		Set<String> favoriteBuilds = settings.getStringSet("FavoriteBuilds", null);

		List<String> result = new ArrayList<>();
		if (favoriteBuilds != null) {
			result.addAll(favoriteBuilds);
		}

		return result;
	}
}
