package com.sc2toolslab.sc2bm.ui.providers;

import com.sc2toolslab.sc2bm.R;

public class BuildItemImageProvider extends BaseImageProvider<String> {

	public BuildItemImageProvider() {
		//Terran Buildings
		nameToImageId.put("CommandCenter", R.drawable.bi_commandcenter);
		nameToImageId.put("SupplyDepot", R.drawable.bi_supplaydepo);
		nameToImageId.put("Barracks", R.drawable.bi_barracks);
		nameToImageId.put("OrbitalCommand", R.drawable.bi_orbitalcommand);
		nameToImageId.put("Refinery", R.drawable.bi_refinery);
		nameToImageId.put("PlanetaryFortrees", R.drawable.bi_planetaryfortress);
		nameToImageId.put("Bunker", R.drawable.bi_bunker);
		nameToImageId.put("Factory", R.drawable.bi_factory);
		nameToImageId.put("EngineeringBay", R.drawable.bi_engineeringbay);
		nameToImageId.put("GhostAcademy", R.drawable.bi_ghostacademy);
		nameToImageId.put("Starport", R.drawable.bi_starport);
		nameToImageId.put("Armory", R.drawable.bi_armory);
		nameToImageId.put("FusionCore", R.drawable.bi_fusioncore);
		nameToImageId.put("TechLabOnBarracks", R.drawable.bi_techlab);
		nameToImageId.put("ReactorOnFactory", R.drawable.bi_reactor);
		nameToImageId.put("ReactorOnStarport", R.drawable.bi_reactor);
		nameToImageId.put("MissileTurret", R.drawable.bi_missleturret);
		nameToImageId.put("TechLabOnFactory", R.drawable.bi_techlab);
		nameToImageId.put("TechLabOnStarport", R.drawable.bi_techlab);
		nameToImageId.put("SensorTower", R.drawable.bi_sensortower);
		nameToImageId.put("ReactorOnBarracks", R.drawable.bi_reactor);
		//Terran units
		nameToImageId.put("SCV", R.drawable.bi_scv);
		nameToImageId.put("Marine", R.drawable.bi_marine);
		nameToImageId.put("Marauder", R.drawable.bi_marauder);
		nameToImageId.put("Reaper", R.drawable.bi_reaper);
		nameToImageId.put("Ghost", R.drawable.bi_ghost);
		nameToImageId.put("Hellion", R.drawable.bi_helion);
		nameToImageId.put("SiegeTank", R.drawable.bi_tank);
		nameToImageId.put("Cyclone", R.drawable.bi_cyclone);
		nameToImageId.put("Thor", R.drawable.bi_thor);
		nameToImageId.put("Viking", R.drawable.bi_viking);
		nameToImageId.put("Medivac", R.drawable.bi_medivak);
		nameToImageId.put("Raven", R.drawable.bi_raven);
		nameToImageId.put("Banshee", R.drawable.bi_banshee);
		nameToImageId.put("BattleCruiser", R.drawable.bi_battlecruiser);
		nameToImageId.put("DoubleMarines", R.drawable.bi_marine_x2);
		nameToImageId.put("DoubleHellions", R.drawable.bi_helion_x2);
		nameToImageId.put("DoubleVikings", R.drawable.bi_viking_x2);
		nameToImageId.put("DoubleMedivacs", R.drawable.bi_medivak_x2);
		nameToImageId.put("VikingPlusMedivac", R.drawable.bi_viking_medivak);
		nameToImageId.put("DoubleReapers", R.drawable.bi_reaper_x2);
		nameToImageId.put("DoubleWidowMines", R.drawable.bi_widowmine_x2);
		nameToImageId.put("DoubleHellbat", R.drawable.bi_hellbat_x2);
		nameToImageId.put("Hellbat", R.drawable.bi_hellbat);
		nameToImageId.put("WidowMine", R.drawable.bi_widowmine);
		nameToImageId.put("Nuke", R.drawable.bi_nuke);
		nameToImageId.put("Liberator", R.drawable.bi_liberator);
		nameToImageId.put("CycloneOnReactor", R.drawable.bi_cycloneonreactor);
		nameToImageId.put("MarineOnReactor", R.drawable.bi_marineonreactor);
		nameToImageId.put("ReaperOnReactor", R.drawable.bi_reaperonreactor);
		nameToImageId.put("HellionOnReactor", R.drawable.bi_helliononreactor);
		nameToImageId.put("HellbatOnReactor", R.drawable.bi_hellbatonreactor);
		nameToImageId.put("WidowMineOnReactor", R.drawable.bi_widowmineonreactor);
		nameToImageId.put("VikingOnReactor", R.drawable.bi_vikingonreactor);
		nameToImageId.put("MedivacOnReactor", R.drawable.bi_medivaconreactor);
		nameToImageId.put("LiberatorOnReactor", R.drawable.bi_liberatoronreactor);
		//Terran specials
		nameToImageId.put("MineralScv", R.drawable.bi_mineralscv);
		nameToImageId.put("GasScv", R.drawable.bi_gasscv);
		nameToImageId.put("LandStarportOnReactor", R.drawable.bi_landstarport_reactor);
		nameToImageId.put("CallSupplyDrop", R.drawable.bi_supplydrop);
		nameToImageId.put("LandRaxOnTechLab", R.drawable.bi_landbarracks_lab);
		nameToImageId.put("LandFactoryOnTechLab", R.drawable.bi_landfactory_lab);
		nameToImageId.put("ScannerSweep", R.drawable.bi_scannersweep);
		nameToImageId.put("LiftStarportFromTechLab", R.drawable.bi_liftstarport_lab);
		nameToImageId.put("CallMule", R.drawable.bi_callmule);
		nameToImageId.put("LiftStarportFromReactor", R.drawable.bi_liftstarport_reactor);
		nameToImageId.put("LiftFactoryFromTechLab", R.drawable.bi_liftfactory_lab);
		nameToImageId.put("ReturnScv", R.drawable.bi_scout_in_terran);
		nameToImageId.put("SalvageBunker", R.drawable.bi_salvagebunker);
		nameToImageId.put("LandStarportOnTechLab", R.drawable.bi_landstarport_lab);
		nameToImageId.put("GoOutScv", R.drawable.bi_scout_out_terran);
		nameToImageId.put("LiftRaxFromReactor", R.drawable.bi_liftbarracks_reactor);
		nameToImageId.put("LiftRaxFromTechLab", R.drawable.bi_liftbarracks_lab);
		nameToImageId.put("LandRaxOnReactor", R.drawable.bi_landbarracks_reactor);
		nameToImageId.put("LiftFactoryFromReactor", R.drawable.bi_liftfactory_reactor);
		nameToImageId.put("LandFactoryOnReactor", R.drawable.bi_landfactory_reactor);
		//Terran upgrades
		nameToImageId.put("InfantryWeaponsLevel1", R.drawable.bi_infantryweapons1);
		nameToImageId.put("InfantryWeaponsLevel3", R.drawable.bi_infantryweapons3);
		nameToImageId.put("InfantryWeaponsLevel2", R.drawable.bi_infantryweapons2);
		nameToImageId.put("InfantryArmorLevel1", R.drawable.bi_infantryarmor1);
		nameToImageId.put("NeosteelFrame", R.drawable.bi_shipplating1);    //temp...
		nameToImageId.put("DurableMaterials", R.drawable.bi_durablematerials);
		nameToImageId.put("CorvidReactor", R.drawable.bi_corvidreactor);
		nameToImageId.put("CaduceusReactor", R.drawable.bi_caduceusreactor);
		nameToImageId.put("CombatShield", R.drawable.bi_combatshield);
		nameToImageId.put("InfantryArmorLevel2", R.drawable.bi_infantryarmor2);
		nameToImageId.put("InfantryArmorLevel3", R.drawable.bi_infantryarmor3);
		nameToImageId.put("SiegeTech", R.drawable.bi_siegetech);
		nameToImageId.put("BehemothReactor", R.drawable.bi_behemothreactor);
		nameToImageId.put("ConcussiveShells", R.drawable.bi_concussiveshells);
		nameToImageId.put("ShipPlatingLevel2", R.drawable.bi_shipplating2);
		nameToImageId.put("ShipPlatingLevel1", R.drawable.bi_shipplating1);
		nameToImageId.put("ShipPlatingLevel3", R.drawable.bi_shipplating3);
		nameToImageId.put("HiSecAutoTracking", R.drawable.bi_hisecautotracking);
		nameToImageId.put("VehiclePlatingLevel3", R.drawable.bi_vehicleplating3);
		nameToImageId.put("VehiclePlatingLevel1", R.drawable.bi_vehicleplating1);
		nameToImageId.put("VehiclePlatingLevel2", R.drawable.bi_vehicleplating2);
		nameToImageId.put("DefenderMode", R.drawable.bi_defendermode);
		nameToImageId.put("SmartServos", R.drawable.bi_smartservos);
		nameToImageId.put("EnhancedMunitions", R.drawable.bi_enhancedmunitions);

		nameToImageId.put("VehicleAndShipPlatingLevel1", R.drawable.bi_vehicleplating1);
		nameToImageId.put("VehicleAndShipPlatingLevel2", R.drawable.bi_vehicleplating2);
		nameToImageId.put("VehicleAndShipPlatingLevel3", R.drawable.bi_vehicleplating3);

		nameToImageId.put("ShipWeaponsLevel1", R.drawable.bi_shipweapons1);
		nameToImageId.put("ShipWeaponsLevel2", R.drawable.bi_shipweapons2);
		nameToImageId.put("ShipWeaponsLevel3", R.drawable.bi_shipweapons3);
		nameToImageId.put("InfernalPreIgniter", R.drawable.bi_infernal_preigniter);
		nameToImageId.put("ThorStrikeCanons", R.drawable.bi_mmstrikecannons);

		nameToImageId.put("VehicleWeaponsLevel1", R.drawable.bi_vehicleweapons1);
		nameToImageId.put("VehicleWeaponsLevel2", R.drawable.bi_vehicleweapons2);
		nameToImageId.put("VehicleWeaponsLevel3", R.drawable.bi_vehicleweapons3);

		nameToImageId.put("VehicleAndShipWeaponsLevel1", R.drawable.bi_vehicleweapons1);
		nameToImageId.put("VehicleAndShipWeaponsLevel2", R.drawable.bi_vehicleweapons2);
		nameToImageId.put("VehicleAndShipWeaponsLevel3", R.drawable.bi_vehicleweapons3);

		nameToImageId.put("CloakingField", R.drawable.bi_cloakingfield);
		nameToImageId.put("StimPack", R.drawable.bi_stimpack);
		nameToImageId.put("TransformationServos", R.drawable.bi_transformation_servos);
		//nameToImageId.put("SeekerMissile", R.drawable.seekermissile); //not needed any more
		nameToImageId.put("PersonalCloaking", R.drawable.bi_personalcloaking);
		nameToImageId.put("RapidFireLaunchers", R.drawable.bi_rapidfirelaunchers);
		nameToImageId.put("BuildingArmor", R.drawable.bi_buildingarmor);
		nameToImageId.put("WeaponRefit", R.drawable.bi_weaponrefit);
		nameToImageId.put("MoebiusReactor", R.drawable.bi_moebiusreactor);
		nameToImageId.put("NitroPacks", R.drawable.bi_nitropacks);
		nameToImageId.put("DrillingClaws", R.drawable.bi_drillingclaws);
		nameToImageId.put("HyperflightRotors", R.drawable.bi_hyperflightrotors);
		nameToImageId.put("HighCapacityFuelTanks", R.drawable.bi_highcapacityfueltanks);
		nameToImageId.put("MagFieldAccelerator", R.drawable.bi_magfieldaccelerator);
		nameToImageId.put("ExplosiveShrapnelShells", R.drawable.bi_explosiveshrapnelshells);
		nameToImageId.put("RecalibratedExplosives", R.drawable.bi_recalibratedexplosives);
		nameToImageId.put("AdvancedBallistics", R.drawable.bi_advancedballistics);
		nameToImageId.put("EnhancedShockwaves", R.drawable.bi_enhancedshockwaves);
		nameToImageId.put("NeosteelArmor", R.drawable.bi_neosteelarmor);
		nameToImageId.put("RapidReignitionSystem", R.drawable.bi_rapidreignitionsystem);

		//Protoss buildings
		nameToImageId.put("Nexus", R.drawable.bi_nexus);
		nameToImageId.put("Pylon", R.drawable.bi_pylon);
		nameToImageId.put("DarkShrine", R.drawable.bi_darkshrine);
		nameToImageId.put("RoboticsFacility", R.drawable.bi_roboticsfacility);
		nameToImageId.put("TemplarArchives", R.drawable.bi_templararchives);
		nameToImageId.put("CyberneticsCore", R.drawable.bi_cyberneticscore);
		nameToImageId.put("RoboticsBay", R.drawable.bi_roboticsbay);
		nameToImageId.put("Stargate", R.drawable.bi_stargate);
		nameToImageId.put("TwilightCouncil", R.drawable.bi_twilightcouncil);
		nameToImageId.put("PhotonCanon", R.drawable.bi_photoncannon);
		nameToImageId.put("Forge", R.drawable.bi_forge);
		nameToImageId.put("Assimilator", R.drawable.bi_assimilator);
		nameToImageId.put("Gateway", R.drawable.bi_gateway);
		nameToImageId.put("FleetBeacon", R.drawable.bi_fleetbeacon);
		nameToImageId.put("ShieldBattery", R.drawable.bi_shieldbattery);
		//Protoss units
		nameToImageId.put("ArchonFromTwoHT", R.drawable.bi_archon);
		nameToImageId.put("Zealot", R.drawable.bi_zealot);
		nameToImageId.put("WarpInHighTemplar", R.drawable.bi_warphightemplar);
		nameToImageId.put("Phoenix", R.drawable.bi_phoenix);
		nameToImageId.put("ArchonFromOneHTandOneDT", R.drawable.bi_archon);
		nameToImageId.put("Colossus", R.drawable.bi_colossus);
		nameToImageId.put("ArchonFromTwoDT", R.drawable.bi_archon);
		nameToImageId.put("WarpInSentry", R.drawable.bi_warpsentry);
		nameToImageId.put("Sentry", R.drawable.bi_sentry);
		nameToImageId.put("HighTemplar", R.drawable.bi_hightemplar);
		nameToImageId.put("WarpInStalker", R.drawable.bi_warpstalker);
		nameToImageId.put("Probe", R.drawable.bi_probe);
		nameToImageId.put("Observer", R.drawable.bi_observer);
		nameToImageId.put("WarpInZealot", R.drawable.bi_warpzealot);
		nameToImageId.put("Mothership", R.drawable.bi_mothership);
		nameToImageId.put("DarkTemplar", R.drawable.bi_darktemplar);
		nameToImageId.put("WarpInDarkTemplar", R.drawable.bi_warpdarktemplar);
		nameToImageId.put("WarpPrism", R.drawable.bi_warpprism);
		nameToImageId.put("VoidRay", R.drawable.bi_voidray);
		nameToImageId.put("Carrier", R.drawable.bi_carrier);
		nameToImageId.put("Stalker", R.drawable.bi_stalker);
		nameToImageId.put("Immortal", R.drawable.bi_immortal);
		nameToImageId.put("Oracle", R.drawable.bi_oracle);
		nameToImageId.put("MothershipCore", R.drawable.bi_mothership_core);
		nameToImageId.put("Tempest", R.drawable.bi_tempest);
		nameToImageId.put("Disruptor", R.drawable.bi_disruptor);
		nameToImageId.put("Adept", R.drawable.bi_adept);
		nameToImageId.put("WarpInAdept", R.drawable.bi_warpadept);
		//Protoss specials
		nameToImageId.put("Chronoboost", R.drawable.bi_chronoboost);
		nameToImageId.put("MineralProbe", R.drawable.bi_mineralprobe);
		nameToImageId.put("SwitchToWarpgate", R.drawable.bi_warpgate);
		nameToImageId.put("SwitchToGateway", R.drawable.bi_gateway);
		nameToImageId.put("GoOutProbe", R.drawable.bi_scout_out_protoss);
		nameToImageId.put("ReturnProbe", R.drawable.bi_scout_in_protoss);
		nameToImageId.put("GasProbe", R.drawable.bi_gasprobe);
		nameToImageId.put("StartIdle", R.drawable.bi_startidle);
		nameToImageId.put("StopIdleIn3Seconds", R.drawable.bi_stopidle3);
		nameToImageId.put("StopIdleIn5Seconds", R.drawable.bi_stopidle5);
		nameToImageId.put("StopIdleIn10Seconds", R.drawable.bi_stopidle10);
		nameToImageId.put("MassRecall", R.drawable.bi_massrecall);
		//Protoss upgrades
		nameToImageId.put("AirArmorLevel1", R.drawable.bi_airarmorlevel1);
		nameToImageId.put("AirArmorLevel2", R.drawable.bi_airarmorlevel2);
		nameToImageId.put("AirArmorLevel3", R.drawable.bi_airarmorlevel3);
		nameToImageId.put("GroundArmorLevel1", R.drawable.bi_groundarmorlevel1);
		nameToImageId.put("GroundArmorLevel2", R.drawable.bi_groundarmorlevel2);
		nameToImageId.put("GroundArmorLevel3", R.drawable.bi_groundarmorlevel3);
		nameToImageId.put("ShieldsLevel1", R.drawable.bi_shieldslevel1);
		nameToImageId.put("ShieldsLevel2", R.drawable.bi_shieldslevel2);
		nameToImageId.put("ShieldsLevel3", R.drawable.bi_shieldslevel3);
		nameToImageId.put("GraviticDrive", R.drawable.bi_graviticdrive);
		nameToImageId.put("GraviticBoosters", R.drawable.bi_graviticbooster);
		nameToImageId.put("Charge", R.drawable.bi_charge);
		nameToImageId.put("ResonatingGlaves", R.drawable.bi_resonatingglaves);
		nameToImageId.put("WarpgateUpgrade", R.drawable.bi_warpgateupgrade);
		nameToImageId.put("PsionicStorm", R.drawable.bi_psionicstorm);
		nameToImageId.put("GravitonCatapult", R.drawable.bi_gravitoncatapult);
		nameToImageId.put("GroundWeaponsLevel3", R.drawable.bi_groundweaponslevel3);
		nameToImageId.put("GroundWeaponsLevel2", R.drawable.bi_groundweaponslevel2);
		nameToImageId.put("GroundWeaponsLevel1", R.drawable.bi_groundweaponslevel1);
		nameToImageId.put("Hallucination", R.drawable.bi_hallucination);
		nameToImageId.put("Blink", R.drawable.bi_blink);
		nameToImageId.put("ExtendedThermalLance", R.drawable.bi_extendedthermallance);
		nameToImageId.put("AirWeaponsLevel2", R.drawable.bi_airweaponslevel2);
		nameToImageId.put("AirWeaponsLevel1", R.drawable.bi_airweaponslevel1);
		nameToImageId.put("AirWeaponsLevel3", R.drawable.bi_airweaponslevel3);
		nameToImageId.put("AnionPulseCrystals", R.drawable.bi_anionpulsecrystals);
		nameToImageId.put("ShadowStride", R.drawable.bi_shadowstride);

		//Zerg buildings
		nameToImageId.put("Spire", R.drawable.bi_spire);
		nameToImageId.put("Hive", R.drawable.bi_hive);
		nameToImageId.put("GreaterSpire", R.drawable.bi_greaterspire);
		nameToImageId.put("SporeCrawler", R.drawable.bi_sporecrawler);
		nameToImageId.put("HydraliskDen", R.drawable.bi_hydraliskden);
		nameToImageId.put("NydusNetwork", R.drawable.bi_nydusnetwork);
		nameToImageId.put("EvolutionChamber", R.drawable.bi_evolutionchamber);
		nameToImageId.put("SpineCrawler", R.drawable.bi_spinecrawler);
		nameToImageId.put("InfestationPit", R.drawable.bi_infestationpit);
		nameToImageId.put("Lair", R.drawable.bi_lair);
		nameToImageId.put("RoachWarren", R.drawable.bi_roachwarren);
		nameToImageId.put("Hatchery", R.drawable.bi_hatchery);
		nameToImageId.put("MacroHatchery", R.drawable.bi_macrohatchery);
		nameToImageId.put("UltraliskCavern", R.drawable.bi_ultraliskcavern);
		nameToImageId.put("Extractor", R.drawable.bi_extractor);
		nameToImageId.put("SpawningPool", R.drawable.bi_spawningpool);
		nameToImageId.put("BanelingNest", R.drawable.bi_banelingnest);
		nameToImageId.put("LurkerDen", R.drawable.bi_lurkerden);
		//Zerg units
		nameToImageId.put("Queen", R.drawable.bi_queen);
		nameToImageId.put("Mutalisk", R.drawable.bi_mutalisk);
		nameToImageId.put("Corruptor", R.drawable.bi_corruptor);
		nameToImageId.put("Baneling", R.drawable.bi_baneling);
		nameToImageId.put("Roach", R.drawable.bi_roach);
		nameToImageId.put("Zergling", R.drawable.bi_zergling);
		nameToImageId.put("Ultralisk", R.drawable.bi_ultralisk);
		nameToImageId.put("Drone", R.drawable.bi_drone);
		nameToImageId.put("Infestor", R.drawable.bi_infestor);
		nameToImageId.put("Overlord", R.drawable.bi_overlord);
		nameToImageId.put("Hydralisk", R.drawable.bi_hydralisk);
		nameToImageId.put("Overseer", R.drawable.bi_overseer);
		nameToImageId.put("Viper", R.drawable.bi_viper);
		nameToImageId.put("SwarmHost", R.drawable.bi_swarm_host);
		nameToImageId.put("Broodlord", R.drawable.bi_broodlord);
		nameToImageId.put("Lurker", R.drawable.bi_lurker);
		nameToImageId.put("Ravager", R.drawable.bi_ravager);
		nameToImageId.put("NydusWorm", R.drawable.bi_nydusworm);
		//Zerg specials
		nameToImageId.put("InjectLarva", R.drawable.bi_spawnlarva);
		nameToImageId.put("ReturnDrone", R.drawable.bi_scout_in_zerg);
		nameToImageId.put("GoOutDrone", R.drawable.bi_scout_out_zerg);
		nameToImageId.put("GasDrone", R.drawable.bi_gasdrone);
		nameToImageId.put("SpawnCreepTumor", R.drawable.bi_spawncreeptumor);
		nameToImageId.put("MineralDrone", R.drawable.bi_mineraldrone);
		nameToImageId.put("CancelExtractor", R.drawable.bi_cancelextractor);
		//Zerg upgrades
		nameToImageId.put("FlyerAttacksLevel3", R.drawable.bi_flyerattacks3);
		nameToImageId.put("FlyerAttacksLevel1", R.drawable.bi_flyerattacks1);
		nameToImageId.put("MetabolicBoost", R.drawable.bi_metabolicboost);
		nameToImageId.put("FlyerAttacksLevel2", R.drawable.bi_flyerattacks2);
		nameToImageId.put("MeleeAttacksLevel3", R.drawable.bi_meleeattacks3);
		nameToImageId.put("AdrenalineGlands", R.drawable.bi_adrenalglands);
		nameToImageId.put("VentralSacs", R.drawable.bi_ventralsacs);
		nameToImageId.put("GlialReconstitution", R.drawable.bi_glialreconstitution);
		nameToImageId.put("MissleAttacksLevel3", R.drawable.bi_missileattacks3);
		nameToImageId.put("Burrow", R.drawable.bi_burrow);
		nameToImageId.put("MissleAttacksLevel2", R.drawable.bi_missileattacks2);
		nameToImageId.put("PathogenGlands", R.drawable.bi_pathogenglands);
		nameToImageId.put("TunnelingClaws", R.drawable.bi_tunnelingclaws);
		nameToImageId.put("GroundCarapaceLevel3", R.drawable.bi_groundcarapace3);
		nameToImageId.put("FlyerCarapaceLevel1", R.drawable.bi_flyercarapace1);
		nameToImageId.put("GroundCarapaceLevel1", R.drawable.bi_groundcarapace1);
		nameToImageId.put("FlyerCarapaceLevel2", R.drawable.bi_flyercarapace2);
		nameToImageId.put("PneumatizedCarapace", R.drawable.bi_pneumatizedcarapace);
		nameToImageId.put("GroundCarapaceLevel2", R.drawable.bi_groundcarapace2);
		nameToImageId.put("FlyerCarapaceLevel3", R.drawable.bi_flyercarapace3);
		nameToImageId.put("NeuralParasite", R.drawable.bi_neuralparasite);
		nameToImageId.put("MissleAttacksLevel1", R.drawable.bi_missileattacks1);
		nameToImageId.put("ChitinousPlating", R.drawable.bi_chitinousplating);
		nameToImageId.put("CentrifugalHooks", R.drawable.bi_centrifugalhooks);
		nameToImageId.put("MeleeAtacksLevel2", R.drawable.bi_meleeattacks2);
		nameToImageId.put("MeleeAttacksLevel1", R.drawable.bi_meleeattacks1);
		nameToImageId.put("GroovedSpines", R.drawable.bi_groovedspines);
		nameToImageId.put("MuscularAugments", R.drawable.bi_muscularaugments);
		nameToImageId.put("EnduringLocusts", R.drawable.bi_enduring_locusts);
		nameToImageId.put("FlyingLocusts", R.drawable.bi_flyinglocusts);
		nameToImageId.put("MutateVentralSacs", R.drawable.bi_morphventralsacs);
		nameToImageId.put("AdaptiveTalons", R.drawable.bi_adaptivetalons);
	}
}