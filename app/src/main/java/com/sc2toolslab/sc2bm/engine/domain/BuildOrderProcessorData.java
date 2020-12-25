package com.sc2toolslab.sc2bm.engine.domain;

import com.sc2toolslab.sc2bm.engine.EngineConsts;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BuildOrderProcessorData {
	private String name;
	private String sC2VersionID;
	private String description;
	private RaceEnum race;
	private RaceEnum vsRace;
	private long created;
	private long visited;

	private BuildOrderProcessorItem lastBuildItem;
	private List<BuildOrderProcessorItem> buildItems = new ArrayList<BuildOrderProcessorItem>();

	//region overriden methods

	@Override
	public boolean equals(Object o) {
		if(o instanceof BuildOrderProcessorData) {
			BuildOrderProcessorData tmp = (BuildOrderProcessorData)o;
			List<BuildOrderProcessorItem> tmpItems = tmp.getBuildItems();

			if(_hasDefaultItem(tmpItems) && !_hasDefaultItem(buildItems)) {
				tmpItems = tmpItems.subList(1, tmpItems.size());
			}

			if(buildItems.size() != tmpItems.size()) {
				return false;
			}

			int i = 0;

			for(BuildOrderProcessorItem buildItem : tmpItems) {
				if(!buildItem.getItemName().equals(buildItems.get(i).getItemName())) {
					return false;
				}
				i++;
			}

			return true;
		}

		return false;
	}

	//endregion

	//region field accessors
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getsC2VersionID() {
		return sC2VersionID;
	}

	public void setsC2VersionID(String sC2VersionID) {
		this.sC2VersionID = sC2VersionID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public RaceEnum getRace() {
		return race;
	}

	public void setRace(RaceEnum race) {
		this.race = race;
	}

	public RaceEnum getVsRace() {
		return vsRace;
	}

	public void setVsRace(RaceEnum vsRace) {
		this.vsRace = vsRace;
	}

	public BuildOrderProcessorItem getLastBuildItem() {
		return lastBuildItem;
	}

	public void setLastBuildItem(BuildOrderProcessorItem lastBuildItem) {
		this.lastBuildItem = lastBuildItem;
	}

	public List<BuildOrderProcessorItem> getBuildItems() {
		return buildItems;
	}

	public void setBuildItems(List<BuildOrderProcessorItem> buildItems) {
		this.buildItems = buildItems;
	}

	public Integer getBuildLengthInSeconds() {
		return lastBuildItem.getSecondInTimeLine();
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public long getVisited() {
		return visited;
	}

	public void setVisited(long visited) {
		this.visited = visited;
	}
	//endregion

	//region public methods
	public void addBuildItem(BuildOrderProcessorItem item) {
		buildItems.add(item);
		lastBuildItem = item;
	}

	public BuildOrderEntity generateBuildOrderEntity() {
		List<String> buildItemsNames = new ArrayList<String>();
		for(BuildOrderProcessorItem item : buildItems) {
			buildItemsNames.add(item.getItemName());
		}
		return new BuildOrderEntity(name, sC2VersionID, description, race, vsRace,
				getBuildLengthInSeconds(), System.currentTimeMillis(),
				System.currentTimeMillis(), buildItemsNames);
	}

	public List<BuildOrderProcessorItem> getBuildOrderItemsClone() {
		return new ArrayList<BuildOrderProcessorItem>(buildItems);
	}

	public List<BuildOrderProcessorItem> getFinishedItemsClone(int secondInTimeLine) {
		List<BuildOrderProcessorItem> finishedItems = new ArrayList<BuildOrderProcessorItem>();
		for(BuildOrderProcessorItem item : buildItems) {
			if(item.getFinishedSecond() == secondInTimeLine) {
				finishedItems.add(item);
			}
		}

		return finishedItems;
	}

	public List<BuildOrderProcessorItem> getStartedItemsClone(int secondInTimeLine, int predictionInSeconds) {
		List<BuildOrderProcessorItem> startedItems = new ArrayList<BuildOrderProcessorItem>();

		for(BuildOrderProcessorItem item : buildItems) {
			Integer started = item.getSecondInTimeLine();

			if(started == (secondInTimeLine + predictionInSeconds)) {
				startedItems.add(item);
			}
		}

		return startedItems;
	}

	public BuildOrderProcessorItem getPreviousStartedItem(int second, int predictionInSeconds) {
		for(int i = second; i > 0;i--) {
			List<BuildOrderProcessorItem> items = getStartedItemsClone(i, predictionInSeconds);
			if (items.size() > 0) {
				return items.get(0);
			}
		}

		return null;
	}

	public void removeLastItem() {
		if (lastBuildItem == null) return;

		BuildOrderProcessorItem newLastItem = getItemBefore(lastBuildItem);
		buildItems.remove(lastBuildItem);
		lastBuildItem = newLastItem;
	}

	public void removeItem(BuildOrderProcessorItem item) {
		if (item == lastBuildItem) {
			removeLastItem();
			return;
		}

		buildItems.remove(item);
		if (buildItems.size() > 0) {
			lastBuildItem = buildItems.get(buildItems.size() - 1);
		}
	}

	public BuildOrderProcessorItem getItemBefore(BuildOrderProcessorItem baseItem) {
		List<BuildOrderProcessorItem> items = new ArrayList<>();

		for(BuildOrderProcessorItem item : buildItems) {
			if(item.getOrder() < baseItem.getOrder()) {
				items.add(item);
			}
		}
		Collections.sort(items, Collections.reverseOrder(new OrderComparator()));

		return items.isEmpty() ? null : items.get(0);
	}
	//endregion

	//region comparators
	static class OrderComparator implements Comparator<BuildOrderProcessorItem> {
		public int compare(BuildOrderProcessorItem b1, BuildOrderProcessorItem b2) {
			int order1 = b1.getOrder();
			int order2 = b2.getOrder();

			if (order1 == order2)
				return 0;
			else if (order1 > order2)
				return 1;
			else
				return -1;
		}
	}
	//endregion

	//region private methods
	private Boolean _hasDefaultItem(List<BuildOrderProcessorItem> list) {
		for(BuildOrderProcessorItem item : list){
			if(item.getItemName().equals(EngineConsts.DEFAULT_STATE_ITEM_NAME)){
				return true;
			}
		}

		return false;
	}
	//endregion
}
