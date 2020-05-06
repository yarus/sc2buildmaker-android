package com.sc2toolslab.sc2bm.engine.modules;

import com.sc2toolslab.sc2bm.engine.interfaces.IBuildOrderProcessorModule;

import java.util.HashMap;
import java.util.Map;

public class BuildManagerModuleFactory {
	private static Map<String, IBuildOrderProcessorModule> modulesDictionary;

	static {
		modulesDictionary = new HashMap<>();
		addModule(new CastModule());
		addModule(new LarvaModule());
		addModule(new ChronoboostModule());
		addModule(new ResourceModule());
		addModule(new IdleModule());
		addModule(new LotvLarvaModule());
		addModule(new LotvChronoboostModule());
	}

	private static void addModule(IBuildOrderProcessorModule module) {
		modulesDictionary.put(module.getModuleName(), module);
	}

	public static IBuildOrderProcessorModule getModule(String moduleName) {
		return modulesDictionary.get(moduleName);
	}
}
