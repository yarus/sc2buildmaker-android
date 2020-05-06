package com.sc2toolslab.sc2bm.ui.model;

import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;

public class ItemToSpeak {
	public Integer Count;
	public String Text;
	public BuildOrderProcessorItem BuildItem;

	public ItemToSpeak(Integer count, String text, BuildOrderProcessorItem buildItem) {
		Count = count;
		Text = text;
		BuildItem = buildItem;
	}
}