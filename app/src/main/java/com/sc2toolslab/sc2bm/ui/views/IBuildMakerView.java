package com.sc2toolslab.sc2bm.ui.views;

import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.ui.model.QueueDataItem;

import java.util.List;

public interface IBuildMakerView extends IView {
	void setBuildName(String buildName);
	void showMessage(String message);
	void renderList(List<BuildOrderProcessorItem> buildItems, List<QueueDataItem> queue, BuildOrderProcessorItem selectedItem);
}
