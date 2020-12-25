package com.sc2toolslab.sc2bm.ui.model;

import com.sc2toolslab.sc2bm.domain.BuildItemTypeEnum;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.ui.presenters.SimulatorModeEnum;

public class SimulatorDataItem {
    public String Name;
    public String DisplayName;
    public BuildItemTypeEnum Type;
    public int Count;
    public boolean IsEnabled;
    public boolean IsClickable;
    public RaceEnum Race;
    public SimulatorModeEnum Mode;
}
