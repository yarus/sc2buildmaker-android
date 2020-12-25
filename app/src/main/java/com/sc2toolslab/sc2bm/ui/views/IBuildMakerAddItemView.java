package com.sc2toolslab.sc2bm.ui.views;

import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.ui.model.AddItemDataItem;

import java.util.List;

public interface IBuildMakerAddItemView {
	void renderGrid(List<AddItemDataItem> data);
	void showItemInfoDialog(BuildItemEntity item);
	void sendAddItemCommand(String buildItemName);
	void showMessage(String message);
}