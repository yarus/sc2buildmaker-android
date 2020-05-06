package com.sc2toolslab.sc2bm.datacontracts;

import java.util.ArrayList;
import java.util.List;

public class BuildItemInfo {
	private String ItemType;

	private Integer BuildTimeInSeconds;
	private String Name;
	private String DisplayName;
	private String ProductionBuildingName;
	private Integer CostMinerals = 0;
	private Integer CostGas = 0;
	private Integer CostSupply = 0;

	private List<ItemWithAttributesInfo> OrderRequirements = new ArrayList<ItemWithAttributesInfo>();
	private List<ItemWithAttributesInfo> ProduceRequirements = new ArrayList<ItemWithAttributesInfo>();

	private List<ItemWithAttributesInfo> OrderedActions = new ArrayList<ItemWithAttributesInfo>();
	private List<ItemWithAttributesInfo> ProducedActions = new ArrayList<ItemWithAttributesInfo>();

	public String getItemType() {
		return ItemType;
	}

	public void setItemType(String itemType) {
		this.ItemType = itemType;
	}

	public Integer getBuildTimeInSeconds() {
		return BuildTimeInSeconds;
	}

	public void setBuildTimeInSeconds(Integer buildTimeInSeconds) {
		this.BuildTimeInSeconds = buildTimeInSeconds;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		this.Name = name;
	}

	public String getDisplayName() {
		return DisplayName;
	}

	public void setDisplayName(String displayName) {
		this.DisplayName = displayName;
	}

	public String getProductionBuildingName() {
		return ProductionBuildingName;
	}

	public void setProductionBuildingName(String productionBuildingName) {
		this.ProductionBuildingName = productionBuildingName;
	}

	public Integer getCostMinerals() {
		return CostMinerals;
	}

	public void setCostMinerals(Integer costMinerals) {
		this.CostMinerals = costMinerals;
	}

	public Integer getCostGas() {
		return CostGas;
	}

	public void setCostGas(Integer costGas) {
		this.CostGas = costGas;
	}

	public Integer getCostSupply() {
		return CostSupply;
	}

	public void setCostSupply(Integer costSupply) {
		this.CostSupply = costSupply;
	}

	public List<ItemWithAttributesInfo> getOrderRequirements() {
		return OrderRequirements;
	}

	public void setOrderRequirements(List<ItemWithAttributesInfo> orderRequirements) {
		this.OrderRequirements = orderRequirements;
	}

	public List<ItemWithAttributesInfo> getProduceRequirements() {
		return ProduceRequirements;
	}

	public void setProduceRequirements(List<ItemWithAttributesInfo> produceRequirements) {
		this.ProduceRequirements = produceRequirements;
	}

	public List<ItemWithAttributesInfo> getOrderedActions() {
		return OrderedActions;
	}

	public void setOrderedActions(List<ItemWithAttributesInfo> orderedActions) {
		this.OrderedActions = orderedActions;
	}

	public List<ItemWithAttributesInfo> getProducedActions() {
		return ProducedActions;
	}

	public void setProducedActions(List<ItemWithAttributesInfo> producedActions) {
		this.ProducedActions = producedActions;
	}
}
