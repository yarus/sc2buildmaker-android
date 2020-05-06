package com.sc2toolslab.sc2bm.ui.presenters;

import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessor;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorData;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.ui.providers.BuildProcessorConfigurationProvider;
import com.sc2toolslab.sc2bm.ui.views.IBuildEditView;

public class BuildEditPresenter {
	private IBuildEditView mView;
	private BuildOrderEntity mBuildOrder;

	public BuildEditPresenter(IBuildEditView view, String buildName) {
		this.mView = view;
		this.mBuildOrder = _getBuildOrder(buildName);

		bindData();
	}

	private BuildOrderEntity _getBuildOrder(String buildName) {
		BuildOrderEntity result = null;

		BuildOrderEntity initialBuild;

		if (buildName.equals("")) {
			BuildOrderProcessorData loadedBuild = BuildProcessorConfigurationProvider.getInstance().getLoadedBuildOrder();
			initialBuild = loadedBuild.generateBuildOrderEntity();
		} else {
			initialBuild = BuildOrdersProvider.getInstance(mView.getContext()).getBuildOrderByName(buildName);
		}

		BuildOrderProcessor processor = BuildProcessorConfigurationProvider.getInstance().getProcessorForBuild(initialBuild);
		if (processor != null) {
			BuildOrderProcessorData currentBuild = processor.getCurrentBuildOrder();
			if (currentBuild != null && currentBuild.getName().equals(buildName)) {
				result = processor.getBuildEntityFormCurrentBuild();
				result.setDescription(initialBuild.getDescription());
				result.setVsRace(initialBuild.getVsRace());
			}
		}

		if (result == null) {
			result = initialBuild;
		}

		return result;
	}

	public void bindData() {
		String buildName = mBuildOrder.getName();

		if (!isSaveAvailable()) {
			buildName += " Copy";
		}

		mView.setBuildName(buildName);
		mView.setDescription(mBuildOrder.getDescription());
		mView.setVsFaction(mBuildOrder.getVsRace());
	}

	public void saveBuildOrder(boolean copy) {
		if (!copy && !mBuildOrder.getName().equals(mView.getBuildName()) && !mBuildOrder.getName().equals("")) {
			BuildOrdersProvider.getInstance(mView.getContext()).deleteBuildOrder(mBuildOrder.getName());
		}

		mBuildOrder.setName(mView.getBuildName());
		mBuildOrder.setDescription(mView.getDescription());
		mBuildOrder.setVsRace(mView.getVsFaction());

		//BuildOrdersProvider.getInstance().deleteBuildOrder("NewBuildOrder");
		BuildOrdersProvider.getInstance(mView.getContext()).saveBuildOrder(mBuildOrder);
	}

	public boolean isInfoChanged() {
		if (mBuildOrder.getName().equals("")) {
			return true;
		}

		BuildOrderEntity initialBuild = BuildOrdersProvider.getInstance(mView.getContext()).getBuildOrderByName(mBuildOrder.getName());

		return !mBuildOrder.getName().equals(mView.getBuildName())
				|| !mBuildOrder.getDescription().equals(mView.getDescription())
				|| mBuildOrder.getVsRace() != mView.getVsFaction()
				|| !mBuildOrder.equals(initialBuild);
	}

	public boolean hasOtherBuildWithSameName() {
		BuildOrderEntity sameBuild = BuildOrdersProvider.getInstance(mView.getContext()).getBuildOrderByName(mView.getBuildName());
		return sameBuild != null && !mBuildOrder.getName().equals(sameBuild.getName());
	}

	public String getInitialBuildName() {
		return mBuildOrder.getName();
	}

	public boolean isSaveAvailable() {
		return !BuildOrdersProvider.getInstance(mView.getContext()).isBuildDefault(mBuildOrder.getName());
	}
}
