package com.sc2toolslab.sc2bm.datacontracts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BuildOrderInfo {
	private String Name;
	private String SC2VersionID;
	private String Description;
	private String Race;
	private String VsRace;
	private List<String> BuildOrderItems = new ArrayList<String>();

	private long CreationDate;
	private long VisitedDate;
	private Integer BuildLengthInSeconds;

	private Date AddedDate;
	public Date getAddedDate() {
		return AddedDate;
	}
	public void setAddedDate(Date value) {
		this.AddedDate = value;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		this.Name = name;
	}

	public String getSC2VersionID() {
		return SC2VersionID;
	}

	public void setSC2VersionID(String SC2VersionID) {
		this.SC2VersionID = SC2VersionID;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		this.Description = description;
	}

	public String getRace() {
		return Race;
	}

	public void setRace(String race) {
		this.Race = race;
	}

	public String getVsRace() {
		return VsRace;
	}

	public void setVsRace(String vsRace) {
		this.VsRace = vsRace;
	}

	public List<String> getBuildOrderItems() {
		return BuildOrderItems;
	}

	public void setBuildOrderItems(List<String> buildOrderItems) {
		this.BuildOrderItems = buildOrderItems;
	}

	public long getCreationDate() {
		return CreationDate;
	}

	public void setCreationDate(long creationDate) {
		CreationDate = creationDate;
	}

	public long getVisitedDate() {
		return VisitedDate;
	}

	public void setVisitedDate(long visitedDate) {
		VisitedDate = visitedDate;
	}

	public Integer getBuildLengthInSeconds() {
		return BuildLengthInSeconds;
	}

	public void setBuildLengthInSeconds(Integer buildLengthInSeconds) {
		BuildLengthInSeconds = buildLengthInSeconds;
	}
}
