package com.sc2toolslab.sc2bm.ui.providers;

import android.content.Context;

import com.sc2toolslab.sc2bm.dataaccess.FileStorageHelper;
import com.sc2toolslab.sc2bm.dataaccess.JsonStorageDataAccess;
import com.sc2toolslab.sc2bm.datamanagers.DataManagersJsonStorageConfigurator;
import com.sc2toolslab.sc2bm.datamanagers.interfaces.IBuildOrdersManager;
import com.sc2toolslab.sc2bm.datamanagers.interfaces.IDataManagersConfigurator;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.ui.model.BuildOrderFilter;
import com.sc2toolslab.sc2bm.ui.utils.AppStateInitializer;
import com.sc2toolslab.sc2bm.ui.utils.UiDataViewHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class BuildOrdersProvider {
	private List<String> mFavoriteBuilds;

	private IBuildOrdersManager boManager;
	//private SC2VersionEntity version;
	//private GlobalConstantsInfo globalConstants;

	private static boolean _isInitializing;

	private static BuildOrdersProvider ourInstance;
	public static BuildOrdersProvider getInstance(Context context) {
		if (ourInstance == null || (!_isInitializing && !ourInstance.isConfigInitialized())) {
			ourInstance = new BuildOrdersProvider();
			ourInstance.initProvider(context);
		}

		return ourInstance;
	}

	private ArrayList<IFilterUpdated> mListeners;

	private BuildOrderFilter mBuildFilter;
	private ArrayList<BuildOrderEntity> mFullBuildList;
	private HashMap<RaceEnum, ArrayList<BuildOrderEntity>> mFactionBuildsCache;

	private BuildOrdersProvider() {
	}

	public void initProvider(Context context) {
		_isInitializing = true;

		mFavoriteBuilds = new ArrayList<>();
		mListeners = new ArrayList<>();
		mBuildFilter = new BuildOrderFilter();
		mFullBuildList =  new ArrayList<>();
		mFactionBuildsCache = new HashMap<>();

		IDataManagersConfigurator configurator = new DataManagersJsonStorageConfigurator();
		boManager = configurator.getBuildOrdersManager();
		_loadBuildOrders();

		AppStateInitializer.init(context);

		_isInitializing = false;
	}

	public boolean isConfigInitialized() {
		return boManager != null && mBuildFilter != null && mFullBuildList != null && mFavoriteBuilds != null;
	}

	public void setOnFilterChangedListener(IFilterUpdated listener) {
		mListeners.add(listener);
	}

	public void setFaction(RaceEnum faction) {
		mBuildFilter.Faction = faction;
		invalidateCache();
		callFilterUpdate();
	}

	public void setTitlePattern(String title) {
		mBuildFilter.Title = title;
		invalidateCache();
		callFilterUpdate();
	}

	public void setVersion(String version) {
		mBuildFilter.Version = version;
		invalidateCache();
		callFilterUpdate();
	}

	public void setSortFavorites(Boolean value) {
		mBuildFilter.SortFavorites = value;
		invalidateCache();
		callFilterUpdate();
	}

	public void setSortDate(Boolean value) {
		mBuildFilter.SortDate = value;
		invalidateCache();
		callFilterUpdate();
	}

	public String getTitleFilter() {
		return mBuildFilter.Title;
	}

	public String getVersionFilter() {
		return mBuildFilter.Version;
	}

	public RaceEnum getFactionFilter() {return mBuildFilter.Faction; }

	public BuildOrderEntity getBuildOrderByName(String buildName) {
		return _findBuildOrderInCollection(mFullBuildList, buildName);
	}

	private BuildOrderEntity _findBuildOrderInCollection(ArrayList<BuildOrderEntity> collection, String buildName) {
		for (BuildOrderEntity build : collection) {
			if (build.getName().equals(buildName)) {
				return build;
			}
		}
		return null;
	}

	public void saveBuildOrder(BuildOrderEntity bo) {
		boManager.saveBuildOrder(bo);

		BuildOrderEntity cachedBuild = _findBuildOrderInCollection(mFullBuildList, bo.getName());

		if (cachedBuild == null) {
			mFullBuildList.add(bo);
		} else {
			int cachedIndex = mFullBuildList.indexOf(cachedBuild);
			mFullBuildList.set(cachedIndex, bo);
		}

		invalidateCache();
		callFilterUpdate();
	}

	public boolean isBuildDefault(String name) {
		return FileStorageHelper.checkBuildOrderIsDefault(name + ".txt");
	}

	public void deleteBuildOrder(String name) {
		BuildOrderEntity build = getBuildOrderByName(name);
		if (build != null) {
//			if(isBuildDefault(name)) {
//				FileStorageHelper.addFileNameToDeletedList(name + ".txt");
//			}

			new JsonStorageDataAccess().deleteFromStorage(name + ".txt");
			mFullBuildList.remove(build);
			invalidateCache();
			callFilterUpdate();
		}
	}

	public ArrayList<BuildOrderEntity> getBuildOrdersForFaction(RaceEnum vsFaction) {
		mBuildFilter.VsFaction = vsFaction;

		mBuildFilter.IsFavorite = (mBuildFilter.VsFaction == RaceEnum.NotDefined);

		if (mFactionBuildsCache.get(vsFaction) == null) {
			mFactionBuildsCache.put(vsFaction, _getFilteredBuildOrders());
		}

		return mFactionBuildsCache.get(vsFaction);
	}

	public int getBuildOrdersCountForMatchup(RaceEnum mainFaction, RaceEnum vsFaction) {
		int result = 0;

		for (BuildOrderEntity buildOrder : mFullBuildList) {
			if (_hasValidVsFaction(buildOrder, vsFaction)
					&& _hasValidFaction(buildOrder, mainFaction)
					&& _hasValidVersion(buildOrder, mBuildFilter.Version)) {
				result++;
			}
		}

		return result;
	}

	private void invalidateCache() {
		mFactionBuildsCache.clear();
	}

	private void invalidateFavCache() {
		ArrayList<BuildOrderEntity> cache = mFactionBuildsCache.get(RaceEnum.NotDefined);
		if (cache != null) {
			mFactionBuildsCache.remove(RaceEnum.NotDefined);
		}
	}

	private ArrayList<BuildOrderEntity> _getFilteredBuildOrders() {
		ArrayList<BuildOrderEntity> filteredList = new ArrayList<>();

		for (BuildOrderEntity buildOrder : mFullBuildList) {
			if (_hasValidTitle(buildOrder, mBuildFilter.Title)
					&& _hasValidFaction(buildOrder, mBuildFilter.Faction)
					&& _hasValidVsFaction(buildOrder, mBuildFilter.VsFaction)
					&& _hasValidFavorite(buildOrder, mBuildFilter.IsFavorite)
					&& _hasValidVersion(buildOrder, mBuildFilter.Version)) {
				filteredList.add(buildOrder);
			}
		}

		// sort build orders
		_sortBuildOrders(filteredList);

		return filteredList;
	}

	private void _sortBuildOrders(ArrayList<BuildOrderEntity> list) {
		if (mBuildFilter.SortDate) {
			Collections.sort(list, DateComparator);
		}

		Collections.sort(list, VersionComparator);
		Collections.sort(list, MatchupComparator);

		if (mBuildFilter.SortFavorites) {
			Collections.sort(list, FavoriteComparator);
		}
	}

	public Comparator<BuildOrderEntity> VersionComparator
			= new Comparator<BuildOrderEntity>() {

		public int compare(BuildOrderEntity buildOrder1, BuildOrderEntity buildOrder2) {
			String version1 = buildOrder1.getsC2VersionID().replaceAll("\\.", "");
			String version2 = buildOrder2.getsC2VersionID().replaceAll("\\.", "");

			if(version1.length() > version2.length()) {
				version2 += generateZeros(version1.length() - version2.length());
			} else if (version1.length() < version2.length()) {
				version1 += generateZeros(version2.length() - version1.length());
			}

			return Integer.decode(version2).compareTo(Integer.decode(version1));
		}

		private String generateZeros(Integer count) {
			String result = "";

			int j;
			for (j = 0; j < count; j++) {
				result += "0";
			}

			return result;
		}
	};

	public Comparator<BuildOrderEntity> DateComparator
			= new Comparator<BuildOrderEntity>() {

		public int compare(BuildOrderEntity buildOrder1, BuildOrderEntity buildOrder2) {
			Long date1 = buildOrder1.getVisitedDate() > buildOrder1.getCreationDate() ? buildOrder1.getVisitedDate() : buildOrder1.getCreationDate();
			Long date2 = buildOrder2.getVisitedDate() > buildOrder2.getCreationDate() ? buildOrder2.getVisitedDate() : buildOrder2.getCreationDate();

			return date2.compareTo(date1);
		}
	};

	public Comparator<BuildOrderEntity> MatchupComparator
			= new Comparator<BuildOrderEntity>() {

		public int compare(BuildOrderEntity buildOrder1, BuildOrderEntity buildOrder2) {
			String race1 = UiDataViewHelper.getFactionLetter(buildOrder1.getRace());
			String race2 = UiDataViewHelper.getFactionLetter(buildOrder1.getVsRace());
			String matchup1 = race1 + "v" + race2;

			String race3 = UiDataViewHelper.getFactionLetter(buildOrder2.getRace());
			String race4 = UiDataViewHelper.getFactionLetter(buildOrder2.getVsRace());
			String matchup2 = race3 + "v" + race4;

			return matchup2.compareTo(matchup1);
		}
	};

	public Comparator<BuildOrderEntity> FavoriteComparator
			= new Comparator<BuildOrderEntity>() {

		public int compare(BuildOrderEntity buildOrder1, BuildOrderEntity buildOrder2) {
			Boolean isFirstBuildFavorite = isBuildFavorite(buildOrder1.getName());
			Boolean isSecondBuildFavorite = isBuildFavorite(buildOrder2.getName());

			return isSecondBuildFavorite.compareTo(isFirstBuildFavorite);
		}
	};

	private boolean _hasValidFavorite(BuildOrderEntity build, boolean flag) {
		return !flag || (build != null && isBuildFavorite(build.getName()));
	}

	private void callFilterUpdate() {
		for (IFilterUpdated listener : mListeners) {
			listener.onFilterUpdated();
		}
	}

	private boolean _hasValidTitle(BuildOrderEntity build, String title) {
		return "".equals(title) || build.getName().toLowerCase().contains(title.toLowerCase());
	}

	private boolean _hasValidFaction(BuildOrderEntity build, RaceEnum faction) {return faction == RaceEnum.NotDefined || build.getRace() == RaceEnum.NotDefined || build.getRace() == faction;
	}

	private boolean _hasValidVsFaction(BuildOrderEntity build, RaceEnum faction) {
		return faction == RaceEnum.NotDefined || build.getVsRace() == RaceEnum.NotDefined || build.getVsRace() == faction;
	}

	private boolean _hasValidVersion(BuildOrderEntity build, String version) {
		String checkedConfig = build.getsC2VersionID();

		switch (version) {
			case "2.0.5":
				return checkedConfig.equals(version);
			case "2.0.6":
				return checkedConfig.equals(version);
			case "2.0.7":
				return checkedConfig.equals(version) || checkedConfig.equals("2.0.6");
			case "2.0.8":
				return checkedConfig.equals(version) || checkedConfig.equals("2.0.6") || checkedConfig.equals("2.0.7");
			case "2.0.9":
				return checkedConfig.equals(version) || checkedConfig.equals("2.0.6") || checkedConfig.equals("2.0.7") || checkedConfig.equals("2.0.8");
			case "2.0.11":
				return checkedConfig.equals(version) || checkedConfig.equals("2.0.6") || checkedConfig.equals("2.0.7") || checkedConfig.equals("2.0.8") || checkedConfig.equals("2.0.9");
			case "2.1.8":
				return checkedConfig.equals(version) || checkedConfig.equals("2.0.6") || checkedConfig.equals("2.0.7") || checkedConfig.equals("2.0.8") || checkedConfig.equals("2.0.9") || checkedConfig.equals("2.0.11");
			case "2.1.9":
				return checkedConfig.equals(version) || checkedConfig.equals("2.0.6") || checkedConfig.equals("2.0.7") || checkedConfig.equals("2.0.8") || checkedConfig.equals("2.0.9") || checkedConfig.equals("2.0.11") || checkedConfig.equals("2.1.8");
			case "2.2.0":
				return checkedConfig.equals(version) || checkedConfig.equals("2.0.6") || checkedConfig.equals("2.0.7") || checkedConfig.equals("2.0.8") || checkedConfig.equals("2.0.9") || checkedConfig.equals("2.0.11") || checkedConfig.equals("2.1.8") || checkedConfig.equals("2.1.9");
			case "2.5.0":
				return checkedConfig.equals(version);
			case "3.8.0":
				return checkedConfig.equals(version);
			case "4.1.2":
				return checkedConfig.equals(version);
			default:
				return checkedConfig.equals(version) || checkedConfig.equals(("4.11.3"));
		}
	}

	private void _loadBuildOrders() {
		List<BuildOrderEntity> builds = boManager.getBuildOrders();
		mFullBuildList = new ArrayList<>();
		if (builds != null && builds.size() > 0) {
			mFullBuildList.addAll(builds);
		}
	}

	public void loadBuildOrders(List<BuildOrderEntity> builds) {
		mFullBuildList = new ArrayList<>();
		if (builds != null && builds.size() > 0) {
			mFullBuildList.addAll(builds);
		}

		invalidateCache();
		callFilterUpdate();
	}

	public void loadFavoriteBuilds(List<String> favBuilds) {
		if (mFavoriteBuilds == null)  {
			mFavoriteBuilds = new ArrayList<>();
		}

		if (favBuilds != null) {
			mFavoriteBuilds.addAll(favBuilds);
		}
	}

	public List<String> getFavoriteBuildOrders() {
		return mFavoriteBuilds;
	}

	public void addFavoriteBuild(String buildName) {
		if (mFavoriteBuilds == null) {
			mFavoriteBuilds = new ArrayList<>();
		}

		if (!mFavoriteBuilds.contains(buildName)) {
			mFavoriteBuilds.add(buildName);
			invalidateCache();
			invalidateFavCache();
			callFilterUpdate();
		}
	}

	public void removeFavoriteBuild(String buildName) {
		if (mFavoriteBuilds.contains(buildName)) {
			mFavoriteBuilds.remove(buildName);
			invalidateCache();
			invalidateFavCache();
			callFilterUpdate();
		}
	}

	public boolean isBuildFavorite(String buildName) {
		if (mFavoriteBuilds == null) {
			mFavoriteBuilds = new ArrayList<>();
		}

		return mFavoriteBuilds.contains(buildName);
	}
}

