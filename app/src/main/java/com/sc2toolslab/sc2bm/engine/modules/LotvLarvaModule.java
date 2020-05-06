package com.sc2toolslab.sc2bm.engine.modules;

public class LotvLarvaModule extends LarvaModule {
	@Override
	public String getModuleName() {
		return "LotvLarvaModule";
	}

	@Override
	public int getLarvaAdjuster() {
		return 3;
	}

	@Override
	public int getSpawnLarvaTime() {
		return 11;
	}
}
