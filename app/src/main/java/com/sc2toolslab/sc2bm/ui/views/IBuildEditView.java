package com.sc2toolslab.sc2bm.ui.views;

import com.sc2toolslab.sc2bm.domain.RaceEnum;

public interface IBuildEditView extends IView {
	void setBuildName(String name);
	void setDescription(String description);
	void setVsFaction(RaceEnum vsFaction);
	String getBuildName();
	String getDescription();
	RaceEnum getVsFaction();
}
