package com.sc2toolslab.sc2bm.ui.views;

import com.sc2toolslab.sc2bm.domain.RaceEnum;

public interface IBuildView extends IView {
	void setBuildName(String name);
	void setVersion(String version);
	void setCreated(long createdDate);
	void setVisited(long visitedDate);
	void setLength(String lengthString);
	void setFaction(RaceEnum mainFaction);
	void setMatchup(RaceEnum mainFaction, RaceEnum vsFaction);
	void setDescription(String description);
}
