package com.sc2toolslab.sc2bm.datamanagers.interfaces;

import com.sc2toolslab.sc2bm.domain.SC2VersionEntity;

import java.util.List;

public interface ISC2VersionsManager {
	SC2VersionEntity getVersion(String versionID);

	List<String> getSupportedVersionIDs();

	void saveVersion(SC2VersionEntity verion);
}