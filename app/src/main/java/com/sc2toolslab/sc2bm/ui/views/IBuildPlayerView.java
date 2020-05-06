package com.sc2toolslab.sc2bm.ui.views;

import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;

import java.util.List;

public interface IBuildPlayerView extends IView {
	void showMessage(String message);
	void speak(String text);
	void setSelectedPosition(int position);
	void showItemInfo(BuildOrderProcessorItem buildItem);
	void setBuildName(String buildName);
	void renderList(List<BuildOrderProcessorItem> buildItems);
	void setFinishSecond(int finishSecond);
	void setCurrentSecond(int currentSecond);
	void setPlayPauseImage(boolean isPlaying);
	void setKeepScreenFlag(boolean keepScreen);
}
