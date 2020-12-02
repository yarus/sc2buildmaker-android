package com.sc2toolslab.sc2bm.ui.views;

import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;

import java.util.List;

public interface ISimulatorView extends IView {
    void showMessage(String message);
    void setCurrentSecond(int currentSecond);
    void setPlayPauseImage(boolean isPlaying);
    void setKeepScreenFlag(boolean keepScreen);
}
