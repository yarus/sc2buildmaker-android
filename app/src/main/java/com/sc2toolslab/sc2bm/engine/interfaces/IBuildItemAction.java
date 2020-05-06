package com.sc2toolslab.sc2bm.engine.interfaces;

import com.sc2toolslab.sc2bm.datacontracts.ItemWithAttributesInfo;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;

public interface IBuildItemAction {
	void doAction(BuildItemStatistics statistics);

	String getActionName();

	ItemWithAttributesInfo getAttributesInfo();
}