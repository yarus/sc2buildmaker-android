package com.sc2toolslab.sc2bm.ui.presenters;

import android.os.Handler;

import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessor;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorData;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.ui.model.ItemToSpeak;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.ui.providers.BuildProcessorConfigurationProvider;
import com.sc2toolslab.sc2bm.ui.utils.UiDataViewHelper;
import com.sc2toolslab.sc2bm.ui.views.IBuildPlayerView;

import java.util.ArrayList;
import java.util.List;

public class BuildPlayerPresenter {
	private IBuildPlayerView mView;
	private BuildOrderEntity mBuildEntity;
	private BuildOrderProcessorData mBuildOrder;
	private ArrayList<BuildOrderProcessorItem> mFilteredList;
	private BuildOrderProcessor mBuildProcessor;

	private int mSelectedItemPosition;
	private int mCurrentSecond;
	private int mStartSecond;
	private boolean mIsPlaying;
	private boolean mShowWorkers;
	private String mGameSpeed;

	private Handler mHandler;
	private Runnable mTimerTick;

	public BuildPlayerPresenter(IBuildPlayerView view, String buildName, boolean showWorkers, String gameSpeed, int startSecond) {
		this.mView = view;

		this.mShowWorkers = showWorkers;
		this.mSelectedItemPosition = 0;
		this.mCurrentSecond = startSecond;
		this.mStartSecond = startSecond;
		this.mIsPlaying = false;
		this.mGameSpeed = gameSpeed;

		this.mBuildEntity = BuildOrdersProvider.getInstance(mView.getContext()).getBuildOrderByName(buildName);
		this.mBuildProcessor = BuildProcessorConfigurationProvider.getInstance().getProcessorForBuild(mBuildEntity);
		this.mBuildOrder = mBuildProcessor.getCurrentBuildOrder();
		this.mFilteredList = _getFilteredBuildItems(mBuildOrder);

		this.mHandler = new Handler();
		this.mTimerTick = _loadTimerHandler();

		setCurrentSecond(mStartSecond);
		_bindData();
	}

	public void setCurrentSecond(int value) {
		mCurrentSecond = value;

		BuildOrderProcessorItem selectedItem = _getPreviousItemForSecond(mCurrentSecond);

		if (selectedItem != null) {
			mView.setSelectedPosition(mFilteredList.indexOf(selectedItem));
		}

		mView.setCurrentSecond(value);
	}

	public int getSelectedPosition() {
		return mSelectedItemPosition;
	}

	public boolean getIsPlaying() {
		return mIsPlaying;
	}

	public int getCurrentSecond() {
		return mCurrentSecond;
	}

	public RaceEnum getCurrentFaction() {
		return mBuildOrder.getRace();
	}

	public void setSelectedPosition(int position) {
		mSelectedItemPosition = position;

		BuildOrderProcessorItem currentItem = mFilteredList.get(position);

		setCurrentSecond(currentItem.getSecondInTimeLine());
	}

	public void changePlayMode() {
		this.mIsPlaying = !this.mIsPlaying;

		mView.setPlayPauseImage(mIsPlaying);

		if(mIsPlaying) {
			_runTimer();
		} else {
			_pausePlaying();
		}
	}

	public void stopPlayer() {
		_pausePlaying();

		setCurrentSecond(mStartSecond);
	}

	public void formDestroying() {
		if (mHandler != null) {
			mHandler.removeCallbacks(mTimerTick);
		}
	}

	public void changeWorkersFilter() {
		mShowWorkers = !mShowWorkers;

		this.mFilteredList = _getFilteredBuildItems(mBuildOrder);

		mView.renderList(mFilteredList);
	}

	public boolean getShowWorkers() {
		return mShowWorkers;
	}

	private void _bindData() {
		mView.setBuildName(mBuildOrder.getName());

		mView.setCurrentSecond(mCurrentSecond);
		mView.setFinishSecond(mBuildOrder.getLastBuildItem().getSecondInTimeLine());
		mView.setPlayPauseImage(mIsPlaying);

		mView.renderList(mFilteredList);
	}

	private void _runTimer() {
		// TODO: Separate setting for LOTV?
		mHandler.removeCallbacks(mTimerTick);
		mHandler.postDelayed(mTimerTick, _getGameSpeed());

		mView.setKeepScreenFlag(true);
	}

	private int _getGameSpeed() {
		switch (mGameSpeed.toLowerCase()) {
			case "slower":
				return UiDataViewHelper.isVersionLotv(mBuildOrder.getsC2VersionID()) ? 2291 : 1662;
			case "slow":
				return UiDataViewHelper.isVersionLotv(mBuildOrder.getsC2VersionID()) ? 1725 : 1200;
			case "normal":
				return UiDataViewHelper.isVersionLotv(mBuildOrder.getsC2VersionID()) ? 1380 : 1000;
			case "fast":
				return UiDataViewHelper.isVersionLotv(mBuildOrder.getsC2VersionID()) ? 1141 : 820;
			default:
				return UiDataViewHelper.isVersionLotv(mBuildOrder.getsC2VersionID()) ? 970 : 719;
		}
	}

	private void _pausePlaying() {
		mIsPlaying = false;
		mView.setPlayPauseImage(false);
		mHandler.removeCallbacks(mTimerTick);
		mView.setKeepScreenFlag(false);
	}

	private Runnable _loadTimerHandler() {
		return new Runnable() {
			@Override
			public void run() {
			mView.setCurrentSecond(mCurrentSecond);

			if (AppConstants.IS_FREE && mCurrentSecond >= 180) {
				mView.showMessage("You can't play builds longer than 3 minutes in FREE version of the tool.");
				_pausePlaying();
				return;
			}

			_processCurrentSecond();

			mCurrentSecond++;
			}
		};
	}

	private void _processCurrentSecond() {
		ArrayList<ItemToSpeak> list = _getSpeakList();

		for(ItemToSpeak item : list) {
			BuildOrderProcessorItem currentItem = item.BuildItem;

			mView.showItemInfo(currentItem);

			if(item.Text.contains("x2")) {
				item.Text = item.Text.replace("x2", "");
				item.Text = "Double " + item.Text;
			}

			if (item.Count == 1) {
				mView.speak(item.Text);
			} else {
				mView.speak(item.Count.toString() + " " + item.Text);
			}

			mView.setSelectedPosition(mFilteredList.indexOf(currentItem));
		}

		BuildOrderProcessorItem lastItem = mFilteredList.get(mFilteredList.size() - 1);
		if (lastItem.getSecondInTimeLine() > mCurrentSecond) {
			_runTimer();
		} else {
			_pausePlaying();
		}
	}

	private ArrayList<BuildOrderProcessorItem> _getFilteredBuildItems(BuildOrderProcessorData buildOrder) {
		ArrayList<BuildOrderProcessorItem> showedBuildItems = new ArrayList<>();

		List<String> banedItems = new ArrayList<>();
		banedItems.add("defaultitem"); // BuildPlayerHelper.getInstance().getBanedItems();
		banedItems.add("start idle");
		banedItems.add("stop idle in 3 seconds");
		banedItems.add("stop idle in 5 seconds");
		banedItems.add("stop idle in 10 seconds");

		if (!mShowWorkers) {
			banedItems.add("scv");
			banedItems.add("probe");
			banedItems.add("drone");
		}

		for (BuildOrderProcessorItem item : buildOrder.getBuildOrderItemsClone()) {
			String name = "".equals(item.getDisplayName()) ? item.getItemName().toLowerCase() : item.getDisplayName().toLowerCase();

			if(banedItems.contains(name)) {
				continue;
			}

			showedBuildItems.add(item);
		}

		return showedBuildItems;
	}

	private ArrayList<ItemToSpeak> _getSpeakList() {
		ArrayList<ItemToSpeak> itemsToSpeak = new ArrayList<>();

		List<BuildOrderProcessorItem> started = _getStartedItems(mCurrentSecond);

		for(BuildOrderProcessorItem item : started) {
			String name = "".equals(item.getDisplayName()) ? item.getItemName().toLowerCase() : item.getDisplayName().toLowerCase();

			int itemIndex = _getItemIndex(itemsToSpeak, name);

			if(itemIndex != -1) {
				itemsToSpeak.get(itemIndex).Count++;
				itemsToSpeak.get(itemIndex).BuildItem = item;
			} else {
				itemsToSpeak.add(new ItemToSpeak(1, name, item));
			}
		}

		return itemsToSpeak;
	}

	private List<BuildOrderProcessorItem> _getStartedItems(int secondInTimeLine) {
		List<BuildOrderProcessorItem> startedItems = new ArrayList<>();

		for(BuildOrderProcessorItem item : mFilteredList) {
			int started = item.getSecondInTimeLine();

			if(started == secondInTimeLine) {
				startedItems.add(item);
			}
		}

		return startedItems;
	}

	private Integer _getItemIndex(ArrayList<ItemToSpeak> list, String name) {
		Integer index = -1;

		for(int i = 0; i < list.size();i++) {
			if (list.get(i).Text.equals(name)) {
				index = i;
				break;
			}
		}

		return index;
	}

	private BuildOrderProcessorItem _getPreviousItemForSecond(int second) {
		BuildOrderProcessorItem closestItem = null;

		for (BuildOrderProcessorItem item : mFilteredList) {
			if (item.getSecondInTimeLine() > second) {
				continue;
			}

			if (closestItem == null) {
				closestItem = item;
			}

			int currentItemDif = second - item.getSecondInTimeLine();
			if (currentItemDif < 0) {
				currentItemDif = currentItemDif * -1;
			}

			int closestItemDif = second - closestItem.getSecondInTimeLine();
			if (closestItemDif < 0) {
				closestItemDif = closestItemDif * -1;
			}

			if (currentItemDif <= closestItemDif) {
				closestItem = item;
			}
		}

		return closestItem;
	}
}
