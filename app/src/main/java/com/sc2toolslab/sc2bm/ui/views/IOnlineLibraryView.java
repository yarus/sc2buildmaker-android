package com.sc2toolslab.sc2bm.ui.views;

import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;

import java.util.ArrayList;

public interface IOnlineLibraryView extends IView {
	void renderList(ArrayList<BuildOrderEntity> buildItems);
}
