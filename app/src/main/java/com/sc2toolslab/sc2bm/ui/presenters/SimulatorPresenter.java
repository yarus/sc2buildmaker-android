package com.sc2toolslab.sc2bm.ui.presenters;

import android.os.Handler;

import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessor;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorData;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.ui.model.ItemToSpeak;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.ui.providers.BuildProcessorConfigurationProvider;
import com.sc2toolslab.sc2bm.ui.utils.UiDataViewHelper;
import com.sc2toolslab.sc2bm.ui.views.IBuildMakerStatsView;
import com.sc2toolslab.sc2bm.ui.views.ISimulatorView;

import java.util.ArrayList;
import java.util.List;

public class SimulatorPresenter {
    private ISimulatorView mView;
    private boolean mIsPlaying;
    private String mGameSpeed;
    private BuildOrderEntity mBuildEntity;
    private BuildOrderProcessorData mBuildOrder;
    private BuildOrderProcessor mBuildProcessor;
    private int mCurrentSecond;

    private BuildMakerStatsPresenter mStatsPresenter;
    private IBuildMakerStatsView mStatsView;

    private Handler mHandler;
    private Runnable mTimerTick;

    public SimulatorPresenter(ISimulatorView view, IBuildMakerStatsView statsView, String gameSpeed) {
        this.mView = view;
        this.mGameSpeed = gameSpeed;
        this.mStatsView = statsView;

        mBuildEntity = new BuildOrderEntity("",
                BuildOrdersProvider.getInstance(mView.getContext()).getVersionFilter(),
                "",
                BuildOrdersProvider.getInstance(mView.getContext()).getFactionFilter(),
                RaceEnum.NotDefined,
                0,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                new ArrayList<String>());

        mBuildProcessor = BuildProcessorConfigurationProvider.getInstance().getProcessorForBuild(mBuildEntity);
        mBuildOrder = mBuildProcessor.getCurrentBuildOrder();

        mStatsPresenter = new BuildMakerStatsPresenter(statsView, this.mBuildOrder);

        this.mCurrentSecond = 0;
        this.mIsPlaying = false;
        this.mGameSpeed = gameSpeed;

        this.mHandler = new Handler();
        this.mTimerTick = _loadTimerHandler();

        setCurrentSecond(0);

        bindData();
    }

    public boolean getIsPlaying() {
        return mIsPlaying;
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

    public void formDestroying() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mTimerTick);
        }
    }

    public void stopPlayer() {
        _pausePlaying();

        setCurrentSecond(0);
    }

    public void setCurrentSecond(int value) {
        mCurrentSecond = value;

        mView.setCurrentSecond(value);
    }

    public void finishSimulator() {

    }

    public int getCurrentSecond() {
        return mCurrentSecond;
    }

    public void bindData() {

    }

    private void _runTimer() {
        // TODO: Separate setting for LOTV?
        mHandler.removeCallbacks(mTimerTick);
        mHandler.postDelayed(mTimerTick, _getGameSpeed());

        mView.setKeepScreenFlag(true);
    }

    private void _pausePlaying() {
        mIsPlaying = false;
        mView.setPlayPauseImage(false);
        mHandler.removeCallbacks(mTimerTick);
        mView.setKeepScreenFlag(false);
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

    private Runnable _loadTimerHandler() {
        return new Runnable() {
            @Override
            public void run() {
            mView.setCurrentSecond(mCurrentSecond);

            mBuildProcessor.addBuildItem("StartIdle");
            mBuildProcessor.addBuildItem("StopIdleIn1Second");

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

        // Update all panels
        BuildItemStatistics stats = mBuildProcessor.getCurrentStatistics();

        mStatsPresenter.bindStats(stats);
    }
}
