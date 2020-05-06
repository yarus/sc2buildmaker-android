package com.sc2toolslab.sc2bm.ui.views;

import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;

import java.util.List;

public interface IBuildMakerView extends IView {
	void setBuildName(String buildName);
	void renderList(List<BuildOrderProcessorItem> buildItems);
}
