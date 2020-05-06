package com.sc2toolslab.sc2bm.ui.views;

import com.sc2toolslab.sc2bm.domain.RaceEnum;

public interface IBuildMakerStatsView {
	void setTime(Integer seconds);
	void setEnergy(Integer energy);
	void setLarva(Integer larva);
	void setMinerals(Integer minerals);
	void setWorkersOnMinerals(Integer workersOnMinerals);
	void setGas(Integer gas);
	void setWorkersOnGas(Integer workersOnGas);
	void setSupply(Integer current, Integer allowed);
	void setLarvaVisibility(boolean visible);
	void setSupplyImage(RaceEnum faction);
}
