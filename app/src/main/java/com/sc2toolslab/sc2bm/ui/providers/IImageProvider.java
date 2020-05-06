package com.sc2toolslab.sc2bm.ui.providers;

public interface IImageProvider<T> {

	Integer getImageResourceIdByKey(T key);
}