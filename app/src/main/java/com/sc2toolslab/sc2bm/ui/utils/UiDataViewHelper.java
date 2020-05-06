package com.sc2toolslab.sc2bm.ui.utils;

import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.domain.RaceEnum;

public class UiDataViewHelper {
	private UiDataViewHelper() {}

	public static String getTimeStringFromSeconds(Integer seconds){
		if(seconds == null) {
			return "";
		}

		if (seconds == 0) {
			return "00:00";
		}

		Integer minutes = seconds/60;

		String strMinutes = adjustTimeSymbols(minutes);
		String strSeconds = adjustTimeSymbols(seconds - minutes*60);

		return strMinutes + ":" + strSeconds;
	}

	public static String getFactionLetter(RaceEnum faction) {
		if (faction == RaceEnum.NotDefined) {
			return "R";
		} else {
			return faction.name().substring(0, 1).toUpperCase();
		}
	}

	private static String adjustTimeSymbols(long time) {
		if (time < 10) {
			return "0" + time;
		} else         {
			return Long.toString(time);
		}
	}

	public static boolean isVersionLotv(String version) {
		return version.equals(AppConstants.LOTV_Config);
	}
}
