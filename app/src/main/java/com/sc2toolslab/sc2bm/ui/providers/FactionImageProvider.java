package com.sc2toolslab.sc2bm.ui.providers;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.domain.RaceEnum;

public class FactionImageProvider extends BaseImageProvider<RaceEnum> {
	public FactionImageProvider() {
		AddImageMapping(RaceEnum.Terran, R.mipmap.ic_terran_white);
		AddImageMapping(RaceEnum.Protoss, R.mipmap.ic_protoss_white);
		AddImageMapping(RaceEnum.Zerg, R.mipmap.ic_zerg_white);
		AddImageMapping(RaceEnum.NotDefined, R.mipmap.ic_random_white);
	}
}
