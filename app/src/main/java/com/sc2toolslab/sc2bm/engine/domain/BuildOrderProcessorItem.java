package com.sc2toolslab.sc2bm.engine.domain;

import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.domain.BuildItemTypeEnum;

import java.util.UUID;

public class BuildOrderProcessorItem {
	private String id = UUID.randomUUID().toString();

	private String itemName;
	private String displayName;
	private BuildItemTypeEnum itemType;

	private Integer order = 0;
	private Integer secondInTimeLine;
	private Integer finishedSecond;

	private BuildItemStatistics statisticsProvider;

	public BuildOrderProcessorItem(Integer secondInTimeLine, BuildItemEntity item, BuildItemStatistics statisticsProvider, Integer order) {
		this.itemName = item.getName();
		this.itemType = item.getItemType();
		this.displayName = item.getDisplayName();
		this.order = order;
		this.secondInTimeLine = secondInTimeLine;
		this.finishedSecond = secondInTimeLine + item.getBuildTimeInSeconds();
		this.statisticsProvider = statisticsProvider;
	}

	public BuildItemStatistics getStatisticsProvider() {
		return statisticsProvider;
	}

	public void setStatisticsProvider(BuildItemStatistics statisticsProvider) {
		this.statisticsProvider = statisticsProvider;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Integer getSecondInTimeLine() {
		return secondInTimeLine;
	}

	public void setSecondInTimeLine(Integer secondInTimeLine) {
		this.secondInTimeLine = secondInTimeLine;
	}

	public Integer getFinishedSecond() {
		return finishedSecond;
	}

	public void setFinishedSecond(Integer finishedSecond) {
		this.finishedSecond = finishedSecond;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof BuildOrderProcessorItem && ((BuildOrderProcessorItem) o).getId().equals(id);
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String name) {
		this.displayName = name;
	}

	public BuildItemTypeEnum getItemType() { return this.itemType; }
}
