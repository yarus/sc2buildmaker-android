package com.sc2toolslab.sc2bm.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaceSettingsEntityDictionary {
	private Map<RaceEnum, RaceSettingsEntity> mRaceSettingsDictionary;

	public RaceSettingsEntityDictionary() {
		this.mRaceSettingsDictionary = new HashMap<RaceEnum, RaceSettingsEntity>();
	}

	public List<RaceSettingsEntity> getRaceSettingsList() {
		return new ArrayList<RaceSettingsEntity>(this.mRaceSettingsDictionary.values());
	}

	public RaceSettingsEntity getRaceSettings(RaceEnum race) {
		return this.mRaceSettingsDictionary.get(race);
	}

	public void addRaceSettings(RaceSettingsEntity settings) {
		this.mRaceSettingsDictionary.put(settings.getRace(), settings);
	}
}