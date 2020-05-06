package com.sc2toolslab.sc2bm.ui.providers;

import com.sc2toolslab.sc2bm.datacontracts.GlobalConstantsInfo;
import com.sc2toolslab.sc2bm.datacontracts.RaceConstantsInfo;
import com.sc2toolslab.sc2bm.datamanagers.DataManagersJsonStorageConfigurator;
import com.sc2toolslab.sc2bm.datamanagers.interfaces.IDataManagersConfigurator;
import com.sc2toolslab.sc2bm.domain.BuildItemsDictionary;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.domain.RaceSettingsEntity;
import com.sc2toolslab.sc2bm.domain.SC2VersionEntity;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessor;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorConfiguration;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorData;
import com.sc2toolslab.sc2bm.engine.modules.BuildManagerModulesList;

import java.net.PortUnreachableException;
import java.util.HashMap;

public class BuildProcessorConfigurationProvider {
	private IDataManagersConfigurator mConfigurator;
	private HashMap<String, SC2VersionEntity> mLoadedVersions;

	private BuildOrderProcessor mBuildProcessor;

	private static BuildProcessorConfigurationProvider ourInstance = new BuildProcessorConfigurationProvider();
	public static BuildProcessorConfigurationProvider getInstance() {
		return ourInstance;
	}

	private BuildProcessorConfigurationProvider() {
		mLoadedVersions = new HashMap<>();
		mConfigurator = new DataManagersJsonStorageConfigurator();
	}

	public BuildOrderProcessorData getLoadedBuildOrder() {
		BuildOrderProcessorData result = null;

		if (mBuildProcessor != null) {
			result = mBuildProcessor.getCurrentBuildOrder();
		}

		return result;
	}

	public BuildOrderProcessor getProcessorForBuild(BuildOrderEntity buildOrder) {
		boolean needInitialization = true;

		if (mBuildProcessor != null)
		{
			BuildOrderProcessorConfiguration config = mBuildProcessor.getConfig();
			if (config != null && config.getsC2VersionID().equals(buildOrder.getsC2VersionID()) && config.getRace() == buildOrder.getRace()) {
				needInitialization = false;
			}
		}

		if (needInitialization) {
			mBuildProcessor = new BuildOrderProcessor(getConfiguration(buildOrder.getsC2VersionID(), buildOrder.getRace()));
		}

		BuildOrderProcessorData currentBuild = mBuildProcessor.getCurrentBuildOrder();
		if (currentBuild == null || !currentBuild.getName().equals(buildOrder.getName())) {
			mBuildProcessor.loadBuildOrder(buildOrder);
		}

		return mBuildProcessor;
	}

	public BuildItemsDictionary getLastItemsDictionary(){
		if (mBuildProcessor != null) {
			BuildOrderProcessorConfiguration config = mBuildProcessor.getConfig();
			if (config != null) {
				return config.getBuildItemsDictionary();
			}
		}

		return null;
	}

	private BuildOrderProcessorConfiguration getConfiguration(String version, RaceEnum faction) {
		SC2VersionEntity versionEntity = getLoadedVersion(version);

		GlobalConstantsInfo mGlobalConstants = versionEntity.getGlobalConstants();

		RaceSettingsEntity raceSettings = versionEntity.getRaceSettingsDictionary().getRaceSettings(faction);
		BuildManagerModulesList buildManagerModules = raceSettings.getModules();
		RaceConstantsInfo raceConstants = raceSettings.getConstants();

		BuildOrderProcessorConfiguration configuration = new BuildOrderProcessorConfiguration();
		configuration.setBuildItemsDictionary(raceSettings.getItemsDictionary());
		configuration.setBuildManagerModules(buildManagerModules);
		configuration.setGlobalConstants(mGlobalConstants);
		configuration.setRace(faction);
		configuration.setRaceConstants(raceConstants);
		configuration.setsC2VersionID(versionEntity.getVersionID());

		return configuration;
	}

	private SC2VersionEntity getLoadedVersion(String version) {
		if (!mLoadedVersions.containsKey(version)) {
			mLoadedVersions.put(version, mConfigurator.getSC2VersionsManager().getVersion(version));
		}

		return mLoadedVersions.get(version);
	}
}
