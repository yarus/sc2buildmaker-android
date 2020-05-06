package com.sc2toolslab.sc2bm.domain;

import com.sc2toolslab.sc2bm.engine.EngineConsts;

import java.util.LinkedHashMap;
import java.util.Map;

public class BuildItemsDictionary {
	private Map<String, BuildItemEntity> dictionary = new LinkedHashMap<String, BuildItemEntity>();

	public BuildItemsDictionary() {

	}

	public BuildItemsDictionary(Map<String, BuildItemEntity> dictionary) {
		this.dictionary = dictionary;
	}

	public void addItem(BuildItemEntity item) {
		this.dictionary.put(item.getName(), item);
	}

	public BuildItemEntity getItem(String name) {
		return this.dictionary.get(name);
	}

	public Map<String, BuildItemEntity> clone() {
		return new LinkedHashMap<String, BuildItemEntity>(dictionary);
	}

	public BuildItemEntity getDefaultBuildItem() {
		return this.getItem(EngineConsts.DEFAULT_STATE_ITEM_NAME);
	}
}
