package com.sc2toolslab.sc2bm.domain;

import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemAction;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;

import java.util.ArrayList;
import java.util.List;

public class BuildItemEntity {
	private BuildItemTypeEnum itemType;

	private Integer buildTimeInSeconds;
	private String name;
	private String displayName;
	private String productionBuildingName;
	private Integer costMinerals = 0;
	private Integer costGas = 0;
	private Integer costSupply = 0;

	private List<IBuildItemRequirement> orderRequirements = new ArrayList<>();
	private List<IBuildItemRequirement> produceRequirements = new ArrayList<>();

	private List<IBuildItemAction> orderedActions = new ArrayList<>();
	private List<IBuildItemAction> producedActions = new ArrayList<>();

	public BuildItemTypeEnum getItemType() {
		return itemType;
	}

	public void setItemType(BuildItemTypeEnum itemType) {
		this.itemType = itemType;
	}

	public Integer getBuildTimeInSeconds() {
		return buildTimeInSeconds;
	}

	public void setBuildTimeInSeconds(Integer buildTimeInSeconds) {
		this.buildTimeInSeconds = buildTimeInSeconds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProductionBuildingName() {
		return productionBuildingName;
	}

	public void setProductionBuildingName(String productionBuildingName) {
		this.productionBuildingName = productionBuildingName;
	}

	public Integer getCostMinerals() {
		return costMinerals;
	}

	public void setCostMinerals(Integer costMinerals) {
		this.costMinerals = costMinerals;
	}

	public Integer getCostGas() {
		return costGas;
	}

	public void setCostGas(Integer costGas) {
		this.costGas = costGas;
	}

	public Integer getCostSupply() {
		return costSupply;
	}

	public void setCostSupply(Integer costSupply) {
		this.costSupply = costSupply;
	}

	public String getDisplayName() {
		return displayName == null || "".equals(displayName) ? name : displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<IBuildItemRequirement> getOrderRequirements() {
		return orderRequirements;
	}

	public List<IBuildItemRequirement> getProduceRequirements() {
		return produceRequirements;
	}

	public List<IBuildItemAction> getOrderedActions() {
		return orderedActions;
	}

	public List<IBuildItemAction> getProducedActions() {
		return producedActions;
	}
}
