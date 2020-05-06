package com.sc2toolslab.sc2bm.engine.domain;

public class ResourcesEntity {

	private int minerals;
	private int gas;

	public ResourcesEntity() {

	}

	public ResourcesEntity(int minerals, int gas) {
		this.minerals = minerals;
		this.gas = gas;
	}

	public int getMinerals() {
		return minerals;
	}

	public void setMinerals(int minerals) {
		this.minerals = minerals;
	}

	public int getGas() {
		return gas;
	}

	public void setGas(int gas) {
		this.gas = gas;
	}

}