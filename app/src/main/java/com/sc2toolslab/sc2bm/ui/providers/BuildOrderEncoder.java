package com.sc2toolslab.sc2bm.ui.providers;

import com.sc2toolslab.sc2bm.R;

import java.util.HashMap;
import java.util.Map;

public class BuildOrderEncoder {
	protected Map<String, String> nameToCode = new HashMap<String, String>();

	public String getCode(String key) {
		return nameToCode.get(key);
	}

	protected void AddImageMapping(String key, String code) {
		nameToCode.put(key, code);
	}

	protected void RemoveImageMapping(String key) {
		nameToCode.remove(key);
	}

	public BuildOrderEncoder() {
		this.nameToCode = new HashMap<>();

		nameToCode.put("DefaultItem", "mq");
		nameToCode.put("StartIdle", "mw");
		nameToCode.put("StopIdleIn3Seconds", "me");
		nameToCode.put("StopIdleIn5Seconds", "mr");
		nameToCode.put("StopIdleIn10Seconds", "mt");
		nameToCode.put("StopIdleIn1Second", "my");

		nameToCode.put("CommandCenter", "ao");
		nameToCode.put("SupplyDepot", "ap");
		nameToCode.put("Barracks", "aa");
		nameToCode.put("OrbitalCommand", "as");
		nameToCode.put("Refinery", "ad");
		nameToCode.put("PlanetaryFortrees", "af");
		nameToCode.put("Bunker", "ag");
		nameToCode.put("Factory", "ah");
		nameToCode.put("EngineeringBay", "aj");
		nameToCode.put("GhostAcademy", "ak");
		nameToCode.put("Starport", "al");
		nameToCode.put("Armory", "az");
		nameToCode.put("FusionCore", "ax");
		nameToCode.put("TechLabOnBarracks", "ac");
		nameToCode.put("ReactorOnFactory", "av");
		nameToCode.put("ReactorOnStarport", "ab");
		nameToCode.put("MissileTurret", "an");
		nameToCode.put("TechLabOnFactory", "am");
		nameToCode.put("TechLabOnStarport", "sq");
		nameToCode.put("SensorTower", "sw");
		nameToCode.put("ReactorOnBarracks", "se");
		//Terran units
		nameToCode.put("SCV", "q");
		nameToCode.put("Marine", "w");
		nameToCode.put("Marauder", "e");
		nameToCode.put("Reaper", "r");
		nameToCode.put("Ghost", "t");
		nameToCode.put("Hellion", "y");
		nameToCode.put("SiegeTank", "u");
		nameToCode.put("Cyclone", "i");
		nameToCode.put("Thor", "o");
		nameToCode.put("Viking", "p");
		nameToCode.put("Medivac", "a");
		nameToCode.put("Raven", "s");
		nameToCode.put("Banshee", "d");
		nameToCode.put("BattleCruiser", "f");
		nameToCode.put("DoubleMarines", "g");
		nameToCode.put("DoubleHellions", "h");
		nameToCode.put("DoubleVikings", "j");
		nameToCode.put("DoubleMedivacs", "k");
		nameToCode.put("VikingPlusMedivac", "l");
		nameToCode.put("DoubleReapers", "z");
		nameToCode.put("DoubleWidowMines", "x");
		nameToCode.put("DoubleHellbat", "c");
		nameToCode.put("Hellbat", "v");
		nameToCode.put("WidowMine", "b");
		nameToCode.put("Nuke", "n");
		nameToCode.put("Liberator", "m");
		nameToCode.put("MarineOnReactor", "aq");
		nameToCode.put("ReaperOnReactor", "aw");
		nameToCode.put("HellionOnReactor", "ae");
		nameToCode.put("HellbatOnReactor", "ar");
		nameToCode.put("WidowMineOnReactor", "at");
		nameToCode.put("VikingOnReactor", "ay");
		nameToCode.put("MedivacOnReactor", "au");
		nameToCode.put("LiberatorOnReactor", "ai");
		//Terran specials
		nameToCode.put("MineralScv", "sr");
		nameToCode.put("GasScv", "st");
		nameToCode.put("LandStarportOnReactor", "sy");
		nameToCode.put("CallSupplyDrop", "su");
		nameToCode.put("LandRaxOnTechLab", "si");
		nameToCode.put("LandFactoryOnTechLab", "so");
		nameToCode.put("ScannerSweep", "sp");
		nameToCode.put("LiftStarportFromTechLab", "sa");
		nameToCode.put("CallMule", "ss");
		nameToCode.put("LiftStarportFromReactor", "sd");
		nameToCode.put("LiftFactoryFromTechLab", "sf");
		nameToCode.put("ReturnScv", "sg");
		nameToCode.put("SalvageBunker", "sh");
		nameToCode.put("LandStarportOnTechLab", "sj");
		nameToCode.put("GoOutScv", "sk");
		nameToCode.put("LiftRaxFromReactor", "sl");
		nameToCode.put("LiftRaxFromTechLab", "sz");
		nameToCode.put("LandRaxOnReactor", "sx");
		nameToCode.put("LiftFactoryFromReactor", "sc");
		nameToCode.put("LandFactoryOnReactor", "sv");
		//Terran upgrades
		nameToCode.put("InfantryWeaponsLevel1", "sb");
		nameToCode.put("InfantryWeaponsLevel3", "sn");
		nameToCode.put("InfantryWeaponsLevel2", "sm");
		nameToCode.put("InfantryArmorLevel1", "dq");
		nameToCode.put("NeosteelFrame", "dw");    //temp...
		nameToCode.put("DurableMaterials", "de");
		nameToCode.put("CorvidReactor", "dr");
		nameToCode.put("CaduceusReactor", "dt");
		nameToCode.put("CombatShield", "dy");
		nameToCode.put("InfantryArmorLevel2", "du");
		nameToCode.put("InfantryArmorLevel3", "di");
		nameToCode.put("SiegeTech", "do");
		nameToCode.put("BehemothReactor", "dp");
		nameToCode.put("ConcussiveShells", "da");
		nameToCode.put("ShipPlatingLevel2", "ds");
		nameToCode.put("ShipPlatingLevel1", "dd");
		nameToCode.put("ShipPlatingLevel3", "df");
		nameToCode.put("HiSecAutoTracking", "dg");
		nameToCode.put("VehiclePlatingLevel3", "dh");
		nameToCode.put("VehiclePlatingLevel1", "dj");
		nameToCode.put("VehiclePlatingLevel2", "dk");
		nameToCode.put("DefenderMode", "dl");

		nameToCode.put("VehicleAndShipPlatingLevel1", "dz");
		nameToCode.put("VehicleAndShipPlatingLevel2", "dx");
		nameToCode.put("VehicleAndShipPlatingLevel3", "dc");

		nameToCode.put("ShipWeaponsLevel1", "dv");
		nameToCode.put("ShipWeaponsLevel2", "db");
		nameToCode.put("ShipWeaponsLevel3", "dn");
		nameToCode.put("InfernalPreIgniter", "dm");
		nameToCode.put("ThorStrikeCanons", "fq");

		nameToCode.put("VehicleWeaponsLevel1", "fw");
		nameToCode.put("VehicleWeaponsLevel2", "fe");
		nameToCode.put("VehicleWeaponsLevel3", "fr");

		nameToCode.put("VehicleAndShipWeaponsLevel1", "ft");
		nameToCode.put("VehicleAndShipWeaponsLevel2", "fy");
		nameToCode.put("VehicleAndShipWeaponsLevel3", "fu");

		nameToCode.put("CloakingField", "fi");
		nameToCode.put("StimPack", "fo");
		nameToCode.put("TransformationServos", "fp");
		//nameToCode.put("SeekerMissile", R.drawable.seekermissile); //not needed any more
		nameToCode.put("PersonalCloaking", "fa");
		nameToCode.put("BuildingArmor", "fs");
		nameToCode.put("WeaponRefit", "fd");
		nameToCode.put("MoebiusReactor", "ff");
		nameToCode.put("NitroPacks", "fg");
		nameToCode.put("DrillingClaws", "fh");
		nameToCode.put("HyperflightRotors", "fj");
		nameToCode.put("HighCapacityFuelTanks", "fk");
		nameToCode.put("MagFieldAccelerator", "fl");
		nameToCode.put("ExplosiveShrapnelShells", "fz");

		nameToCode.put("SmartServos", "fx");
		nameToCode.put("EnhancedMunitions", "fc");

		nameToCode.put("RecalibratedExplosives", "fv");
		nameToCode.put("AdvancedBallistics", "fb");
		nameToCode.put("EnhancedShockwaves", "fn");
		nameToCode.put("NeosteelArmor", "fm");
		nameToCode.put("RapidReignitionSystem", "gq");

		//Protoss buildings
		nameToCode.put("Nexus", "ae");
		nameToCode.put("Pylon", "ar");
		nameToCode.put("DarkShrine", "at");
		nameToCode.put("RoboticsFacility", "ay");
		nameToCode.put("TemplarArchives", "au");
		nameToCode.put("CyberneticsCore", "ai");
		nameToCode.put("RoboticsBay", "ao");
		nameToCode.put("Stargate", "ap");
		nameToCode.put("TwilightCouncil", "aa");
		nameToCode.put("PhotonCanon", "as");
		nameToCode.put("Forge", "ad");
		nameToCode.put("Assimilator", "af");
		nameToCode.put("Gateway", "ag");
		nameToCode.put("FleetBeacon", "ah");
		nameToCode.put("ShieldBattery", "aj");

		//Protoss units
		nameToCode.put("ArchonFromTwoHT", "s");
		nameToCode.put("Zealot", "w");
		nameToCode.put("WarpInHighTemplar", "a");
		nameToCode.put("Phoenix", "p");
		nameToCode.put("ArchonFromOneHTandOneDT", "o");
		nameToCode.put("Colossus", "e");
		nameToCode.put("ArchonFromTwoDT", "i");
		nameToCode.put("WarpInSentry", "u");
		nameToCode.put("Sentry", "r");
		nameToCode.put("HighTemplar", "t");
		nameToCode.put("WarpInStalker", "y");
		nameToCode.put("Probe", "q");
		nameToCode.put("Observer", "d");
		nameToCode.put("WarpInZealot", "f");
		nameToCode.put("Mothership", "g");
		nameToCode.put("DarkTemplar", "h");
		nameToCode.put("WarpInDarkTemplar", "j");
		nameToCode.put("WarpPrism", "k");
		nameToCode.put("VoidRay", "l");
		nameToCode.put("Carrier", "z");
		nameToCode.put("Stalker", "x");
		nameToCode.put("Immortal", "c");
		nameToCode.put("Oracle", "v");
		nameToCode.put("MothershipCore", "b");
		nameToCode.put("Tempest", "n");
		nameToCode.put("Disruptor", "m");
		nameToCode.put("Adept", "aq");
		nameToCode.put("WarpInAdept", "aw");
		//Protoss specials
		nameToCode.put("Chronoboost", "aj");
		nameToCode.put("MineralProbe", "ak");
		nameToCode.put("SwitchToWarpgate", "al");
		nameToCode.put("SwitchToGateway", "az");
		nameToCode.put("GoOutProbe", "ax");
		nameToCode.put("ReturnProbe", "ac");
		nameToCode.put("GasProbe", "av");
		nameToCode.put("MassRecall", "ab");
		//Protoss upgrades
		nameToCode.put("AirArmorLevel1", "sw");
		nameToCode.put("AirArmorLevel2", "se");
		nameToCode.put("AirArmorLevel3", "sr");
		nameToCode.put("GroundArmorLevel1", "st");
		nameToCode.put("GroundArmorLevel2", "sy");
		nameToCode.put("GroundArmorLevel3", "su");
		nameToCode.put("ShieldsLevel1", "si");
		nameToCode.put("ShieldsLevel2", "so");
		nameToCode.put("ShieldsLevel3", "sp");
		nameToCode.put("GraviticDrive", "sa");
		nameToCode.put("GraviticBoosters", "ss");
		nameToCode.put("Charge", "sd");
		nameToCode.put("ResonatingGlaves", "sf");
		nameToCode.put("WarpgateUpgrade", "sg");
		nameToCode.put("PsionicStorm", "sh");
		nameToCode.put("GravitonCatapult", "sj");
		nameToCode.put("GroundWeaponsLevel3", "sk");
		nameToCode.put("GroundWeaponsLevel2", "sl");
		nameToCode.put("GroundWeaponsLevel1", "sz");
		nameToCode.put("Hallucination", "sx");
		nameToCode.put("Blink", "sc");
		nameToCode.put("ExtendedThermalLance", "sv");
		nameToCode.put("AirWeaponsLevel2", "sb");
		nameToCode.put("AirWeaponsLevel1", "sn");
		nameToCode.put("AirWeaponsLevel3", "sm");
		nameToCode.put("AnionPulseCrystals", "dq");
		nameToCode.put("ShadowStride", "dw");
		nameToCode.put("TectonicDestabilizers", "de");
		nameToCode.put("FluxVanes", "dr");

		//Zerg buildings
		nameToCode.put("Spire", "z");
		nameToCode.put("Hive", "x");
		nameToCode.put("GreaterSpire", "c");
		nameToCode.put("SporeCrawler", "v");
		nameToCode.put("HydraliskDen", "b");
		nameToCode.put("NydusNetwork", "n");
		nameToCode.put("EvolutionChamber", "m");
		nameToCode.put("SpineCrawler", "aq");
		nameToCode.put("InfestationPit", "aw");
		nameToCode.put("Lair", "ae");
		nameToCode.put("RoachWarren", "ar");
		nameToCode.put("Hatchery", "at");
		nameToCode.put("MacroHatchery", "ay");
		nameToCode.put("UltraliskCavern", "au");
		nameToCode.put("Extractor", "ai");
		nameToCode.put("SpawningPool", "ao");
		nameToCode.put("BanelingNest", "ap");
		nameToCode.put("LurkerDen", "aa");
		//Zerg units
		nameToCode.put("Queen", "q");
		nameToCode.put("Mutalisk", "w");
		nameToCode.put("Corruptor", "e");
		nameToCode.put("Baneling", "r");
		nameToCode.put("Roach", "t");
		nameToCode.put("Zergling", "y");
		nameToCode.put("Ultralisk", "u");
		nameToCode.put("Drone", "i");
		nameToCode.put("Infestor", "o");
		nameToCode.put("Overlord", "p");
		nameToCode.put("Hydralisk", "a");
		nameToCode.put("Overseer", "s");
		nameToCode.put("Viper", "d");
		nameToCode.put("SwarmHost", "f");
		nameToCode.put("Broodlord", "g");
		nameToCode.put("Lurker", "h");
		nameToCode.put("Ravager", "j");
		nameToCode.put("NydusWorm", "k");
		//Zerg specials
		nameToCode.put("InjectLarva", "l");
		nameToCode.put("ReturnDrone", "as");
		nameToCode.put("GoOutDrone", "ad");
		nameToCode.put("GasDrone", "af");
		nameToCode.put("SpawnCreepTumor", "ag");
		nameToCode.put("MineralDrone", "ah");
		nameToCode.put("CancelExtractor", "aj");
		//Zerg upgrades
		nameToCode.put("FlyerAttacksLevel3", "ak");
		nameToCode.put("FlyerAttacksLevel1", "al");
		nameToCode.put("MetabolicBoost", "az");
		nameToCode.put("FlyerAttacksLevel2", "ax");
		nameToCode.put("MeleeAttacksLevel3", "ac");
		nameToCode.put("AdrenalineGlands", "av");
		nameToCode.put("VentralSacs", "ab");
		nameToCode.put("GlialReconstitution", "an");
		nameToCode.put("MissleAttacksLevel3", "am");
		nameToCode.put("Burrow", "sq");
		nameToCode.put("MissleAttacksLevel2", "sw");
		nameToCode.put("PathogenGlands", "se");
		nameToCode.put("TunnelingClaws", "sr");
		nameToCode.put("GroundCarapaceLevel3", "st");
		nameToCode.put("FlyerCarapaceLevel1", "sy");
		nameToCode.put("GroundCarapaceLevel1", "su");
		nameToCode.put("FlyerCarapaceLevel2", "si");
		nameToCode.put("PneumatizedCarapace", "so");
		nameToCode.put("GroundCarapaceLevel2", "sp");
		nameToCode.put("FlyerCarapaceLevel3", "sa");
		nameToCode.put("NeuralParasite", "ss");
		nameToCode.put("MissleAttacksLevel1", "sd");
		nameToCode.put("ChitinousPlating", "sf");
		nameToCode.put("CentrifugalHooks", "sg");
		nameToCode.put("MeleeAtacksLevel2", "sh");
		nameToCode.put("MeleeAttacksLevel1", "sj");
		nameToCode.put("GroovedSpines", "sk");
		nameToCode.put("MuscularAugments", "sl");
		nameToCode.put("EnduringLocusts", "sz");
		nameToCode.put("FlyingLocusts", "sx");
		nameToCode.put("MutateVentralSacs", "sc");
		nameToCode.put("AdaptiveTalons", "sv");
		nameToCode.put("SeismicSpines", "sb");
		nameToCode.put("AnabolicSynthesis", "sn");
	}
}
