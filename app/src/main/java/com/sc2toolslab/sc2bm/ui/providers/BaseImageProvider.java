package com.sc2toolslab.sc2bm.ui.providers;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseImageProvider<T> implements IImageProvider<T> {
	protected Map<T, Integer> nameToImageId = new HashMap<T, Integer>();

	public Integer getImageResourceIdByKey(T key) {
		return nameToImageId.get(key);
	}

	protected void AddImageMapping(T key, Integer imageId) {
		nameToImageId.put(key, imageId);
	}

	protected void RemoveImageMapping(T key) {
		nameToImageId.remove(key);
	}
}