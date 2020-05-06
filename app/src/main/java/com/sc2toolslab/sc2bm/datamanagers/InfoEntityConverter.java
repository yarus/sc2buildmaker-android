package com.sc2toolslab.sc2bm.datamanagers;

import com.sc2toolslab.sc2bm.datacontracts.BuildItemInfo;
import com.sc2toolslab.sc2bm.datacontracts.BuildOrderInfo;
import com.sc2toolslab.sc2bm.datacontracts.ItemWithAttributesInfo;
import com.sc2toolslab.sc2bm.datacontracts.RaceSettingsInfo;
import com.sc2toolslab.sc2bm.datacontracts.SC2VersionInfo;
import com.sc2toolslab.sc2bm.domain.ApplicationException;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.domain.BuildItemTypeEnum;
import com.sc2toolslab.sc2bm.domain.BuildItemsDictionary;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.domain.RaceSettingsEntity;
import com.sc2toolslab.sc2bm.domain.RaceSettingsEntityDictionary;
import com.sc2toolslab.sc2bm.domain.SC2VersionEntity;
import com.sc2toolslab.sc2bm.engine.buildactions.BuildItemActionsFactory;
import com.sc2toolslab.sc2bm.engine.modules.BuildManagerModuleFactory;
import com.sc2toolslab.sc2bm.engine.modules.BuildManagerModulesList;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildOrderProcessorModule;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemAction;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;
import com.sc2toolslab.sc2bm.engine.requirements.BuildItemRequirementFactory;

import java.util.List;

public class InfoEntityConverter {

	private InfoEntityConverter() {
	}

	public static SC2VersionEntity convert(SC2VersionInfo item) {
		SC2VersionEntity result = new SC2VersionEntity();
		result.setVersionID(item.getVersionID());
		result.setAddonID(item.getAddonID());
		result.setGlobalConstants(item.getGlobalSettings());
		result.setRaceSettingsDictionary(new RaceSettingsEntityDictionary());

		for (RaceSettingsInfo raceSettingsInfo : item.getRaceSettingsList()) {
			RaceSettingsEntity raceSettingsEntity = convert(raceSettingsInfo);
			result.getRaceSettingsDictionary().addRaceSettings(raceSettingsEntity);
		}

		return result;
	}

	public static SC2VersionInfo convert(SC2VersionEntity item) {
		SC2VersionInfo result = new SC2VersionInfo();
		result.setVersionID(item.getVersionID());
		result.setGlobalSettings(item.getGlobalConstants());

		for (RaceSettingsEntity entity : item.getRaceSettingsDictionary().getRaceSettingsList()) {
			RaceSettingsInfo info = convert(entity);
			result.getRaceSettingsList().add(info);
		}

		return result;
	}

	public static RaceSettingsEntity convert(RaceSettingsInfo item) {
		RaceSettingsEntity result = new RaceSettingsEntity();
		result.setRace(RaceEnum.valueOf(item.getRace()));
		result.setConstants(item.getConstants());
		result.setItemsDictionary(new BuildItemsDictionary());
		result.setModules(new BuildManagerModulesList());

		for (BuildItemInfo buildItemInfo : item.getBuildItems()) {
			BuildItemEntity buildItemEntity = convert(buildItemInfo);
			result.getItemsDictionary().addItem(buildItemEntity);
		}

		for (String raceModule : item.getRaceModules()) {
			IBuildOrderProcessorModule module = BuildManagerModuleFactory.getModule(raceModule);
			result.getModules().add(module);
		}

		return result;
	}

	public static RaceSettingsInfo convert(RaceSettingsEntity item) {
		RaceSettingsInfo result = new RaceSettingsInfo();
		result.setRace(item.getRace().toString());
		result.setConstants(item.getConstants());

		for (BuildItemEntity buildItemEntity : item.getItemsDictionary().clone().values()) {
			BuildItemInfo itemInfo = convert(buildItemEntity);
			result.getBuildItems().add(itemInfo);
		}

		for (IBuildOrderProcessorModule raceModule : item.getModules()) {
			result.getRaceModules().add(raceModule.getModuleName());
		}

		return result;
	}

	public static BuildItemEntity convert(BuildItemInfo item) {
		BuildItemEntity result = new BuildItemEntity();
		result.setBuildTimeInSeconds(item.getBuildTimeInSeconds());
		result.setCostGas(item.getCostGas());
		result.setCostMinerals(item.getCostMinerals());
		result.setCostSupply(item.getCostSupply());
		result.setDisplayName(item.getDisplayName());
		result.setItemType(BuildItemTypeEnum.valueOf(item.getItemType()));
		result.setName(item.getName());
		result.setProductionBuildingName(item.getProductionBuildingName());

		addRequirement(item.getOrderRequirements(), result.getOrderRequirements());
		addRequirement(item.getProduceRequirements(), result.getProduceRequirements());

		addActions(item.getOrderedActions(), result.getOrderedActions(), false);
		addActions(item.getProducedActions(), result.getProducedActions(), false);

		return result;
	}

	private static void addRequirement(List<ItemWithAttributesInfo> infos, List<IBuildItemRequirement> entities) {
		for(ItemWithAttributesInfo attr : infos) {
			try {
				entities.add(BuildItemRequirementFactory.constructRequirement(attr));
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
		}
	}

	private static void addActions(List<ItemWithAttributesInfo> infos, List<IBuildItemAction> resultList, boolean flag) {
		for (ItemWithAttributesInfo action : infos) {
			IBuildItemAction actionEntity = null;
			try {
				actionEntity = BuildItemActionsFactory.constructAction(action);
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
			resultList.add(actionEntity);
		}
	}

	public static BuildItemInfo convert(BuildItemEntity item) {
		BuildItemInfo result = new BuildItemInfo();
		result.setBuildTimeInSeconds(item.getBuildTimeInSeconds());
		result.setCostGas(item.getCostGas());
		result.setCostMinerals(item.getCostMinerals());
		result.setCostSupply(item.getCostSupply());
		result.setDisplayName(item.getDisplayName());
		result.setItemType(item.getItemType().toString());
		result.setName(item.getName());
		result.setProductionBuildingName(item.getProductionBuildingName());

		addActions(item.getOrderedActions(), result.getOrderedActions());
		addActions(item.getProducedActions(), result.getProducedActions());
		addRequirements(item.getOrderRequirements(), result.getOrderRequirements());
		addRequirements(item.getProduceRequirements(), result.getProduceRequirements());

		return result;
	}

	private static void addRequirements(List<IBuildItemRequirement> requirements, List<ItemWithAttributesInfo> resultList) {
		for (IBuildItemRequirement req : requirements) {
			resultList.add(req.getAttributesInfo());
		}
	}

	private static void addActions(List<IBuildItemAction> actions, List<ItemWithAttributesInfo> resultList) {
		for (IBuildItemAction itemAction : actions) {
			resultList.add(itemAction.getAttributesInfo());
		}
	}

	public static BuildOrderInfo convert(BuildOrderEntity buildOrder) {
		BuildOrderInfo result = new BuildOrderInfo();
		result.setName(buildOrder.getName());
		result.setDescription(buildOrder.getDescription());
		result.setRace(buildOrder.getRace().toString());
		result.setVsRace(buildOrder.getVsRace().toString());
		result.setSC2VersionID(buildOrder.getsC2VersionID());

		result.setCreationDate(buildOrder.getCreationDate());
		result.setVisitedDate(buildOrder.getVisitedDate());
		result.setBuildLengthInSeconds(buildOrder.getBuildLengthInSeconds());

		for (String item : buildOrder.getBuildOrderItems()) {
			result.getBuildOrderItems().add(item);
		}

		return result;
	}

	public static BuildOrderEntity convert(BuildOrderInfo item) {
		BuildOrderEntity result = new BuildOrderEntity();
		result.setName(item.getName());
		result.setDescription(item.getDescription());
		result.setRace(RaceEnum.valueOf(item.getRace()));
		result.setVsRace(RaceEnum.valueOf(item.getVsRace()));
		result.setsC2VersionID(item.getSC2VersionID());

		result.setCreationDate(item.getCreationDate());
		result.setVisitedDate(item.getVisitedDate());
		result.setBuildLengthInSeconds(item.getBuildLengthInSeconds());

		for (String buildItem : item.getBuildOrderItems()) {
			result.getBuildOrderItems().add(buildItem);
		}

		return result;
	}
}
