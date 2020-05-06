package com.sc2toolslab.sc2bm.ui.views;

import com.sc2toolslab.sc2bm.domain.RaceEnum;

public interface INavDrawerView extends IView{
	void setFaction(RaceEnum faction);
	void setVsTerranBuildCount(int count, RaceEnum mainFaction);
	void setVsProtossBuildCount(int count, RaceEnum mainFaction);
	void setVsZergBuildCount(int count, RaceEnum mainFaction);
	void setAddon(String version);
	void setVersion(String version);
}