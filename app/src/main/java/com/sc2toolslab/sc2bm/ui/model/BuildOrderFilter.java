package com.sc2toolslab.sc2bm.ui.model;

import com.sc2toolslab.sc2bm.domain.RaceEnum;

public class BuildOrderFilter {
	public String Title;
	public RaceEnum Faction;
	public RaceEnum VsFaction;
	public String Version;
	public boolean IsFavorite;
	public boolean SortFavorites;
	public boolean SortDate;

	public BuildOrderFilter() {
		Title = "";
		Faction = RaceEnum.NotDefined;
		VsFaction = RaceEnum.NotDefined;
		Version = "";
		IsFavorite = false;
		SortFavorites = true;
		SortDate = true;
	}
}
