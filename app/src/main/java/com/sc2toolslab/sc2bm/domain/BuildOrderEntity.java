package com.sc2toolslab.sc2bm.domain;

import com.sc2toolslab.sc2bm.engine.EngineConsts;

import java.util.ArrayList;
import java.util.List;

public class BuildOrderEntity {
	private String name;
	private String sC2VersionID;
	private String description;
	private RaceEnum race;
	private RaceEnum vsRace;
	private List<String> buildOrderItems = new ArrayList<String>();

	private long creationDate;
	private long visitedDate;
	private Integer buildLengthInSeconds;

	public BuildOrderEntity() {
	}

	public BuildOrderEntity(String name, String sC2VersionID, String description, RaceEnum race, RaceEnum vsRace, Integer buildLengthInSeconds, long creationDate, long visitedDate, List<String> buildOrderItems) {
		this.name = name;
		this.sC2VersionID = sC2VersionID;
		this.description = description;
		this.race = race;
		this.vsRace = vsRace;
		this.buildOrderItems = buildOrderItems;
		this.buildLengthInSeconds = buildLengthInSeconds;
		this.creationDate = creationDate;
		this.visitedDate = visitedDate;
	}

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

	public List<String> getBuildOrderItems() {
		return buildOrderItems;
	}

	public void setBuildOrderItems(List<String> buildOrderItems) {
		this.buildOrderItems = buildOrderItems;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public long getVisitedDate() {
		return visitedDate;
	}

	public void setVisitedDate(long visitedDate) {
		this.visitedDate = visitedDate;
	}

	public Integer getBuildLengthInSeconds() {
		return buildLengthInSeconds;
	}

	public void setBuildLengthInSeconds(Integer buildLengthInSeconds) {
		if (buildLengthInSeconds == null) {
			this.buildLengthInSeconds = 0;
		} else {
			this.buildLengthInSeconds = buildLengthInSeconds;
		}
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof BuildOrderEntity) {
			BuildOrderEntity tmp = (BuildOrderEntity)o;
			List<String> tmpItems = tmp.getBuildOrderItems();

			if(_hasDefaultItem(tmpItems) && !_hasDefaultItem(buildOrderItems)) {
				tmpItems = tmpItems.subList(1, tmpItems.size());
			}

			if(buildOrderItems.size() != tmpItems.size()) {
				return false;
			}

			int i = 0;

			for(String buildItem : tmpItems) {
				if(!buildItem.equals(buildOrderItems.get(i))) {
					return false;
				}
				i++;
			}

			return true;
		}

		return false;
	}

	private Boolean _hasDefaultItem(List<String> list) {
		for(String s : list){
			if(s.contains(EngineConsts.DEFAULT_STATE_ITEM_NAME)){
				return true;
			}
		}

		return false;
	}
}
