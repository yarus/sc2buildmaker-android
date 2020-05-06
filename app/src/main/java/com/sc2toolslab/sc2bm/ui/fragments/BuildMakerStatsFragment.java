package com.sc2toolslab.sc2bm.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.ui.utils.UiDataViewHelper;
import com.sc2toolslab.sc2bm.ui.views.IBuildMakerStatsView;

public class BuildMakerStatsFragment extends Fragment implements IBuildMakerStatsView {
	private TextView mTxtTime;
	private TextView mTxtEnergy;
	private TextView mTxtLarva;
	private TextView mTxtMinerals;
	private TextView mTxtWorkersOnMinerals;
	private TextView mTxtGas;
	private TextView mTxtWorkersOnGas;
	private TextView mTxtSupply;
	private FrameLayout mFrameLarva;
	private ImageView mImgMinerals;
	private ImageView mImgGas;
	private ImageView mImgSupply;
	private ImageView mImgEnergy;
	private ImageView mImgLarva;
	private ImageView mImgTimer;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_build_maker_stats, container, false);

		mTxtTime = (TextView) layout.findViewById(R.id.txtTime);
		mTxtEnergy = (TextView) layout.findViewById(R.id.txtEnergy);
		mTxtLarva = (TextView) layout.findViewById(R.id.txtLarva);
		mTxtMinerals = (TextView) layout.findViewById(R.id.txtMinerals);
		mTxtWorkersOnMinerals = (TextView) layout.findViewById(R.id.txtWorkerOnMinerals);
		mTxtGas = (TextView) layout.findViewById(R.id.txtGas);
		mTxtWorkersOnGas = (TextView) layout.findViewById(R.id.txtWorkersOnGas);
		mTxtSupply = (TextView) layout.findViewById(R.id.txtSupply);
		mFrameLarva = (FrameLayout) layout.findViewById(R.id.frameLarva);
		mImgSupply = (ImageView) layout.findViewById(R.id.imgSupply);
		mImgMinerals = (ImageView) layout.findViewById(R.id.imgMinerals);
		mImgGas = (ImageView) layout.findViewById(R.id.imgGas);
		mImgEnergy = (ImageView) layout.findViewById(R.id.imgEnergy);
		mImgLarva = (ImageView) layout.findViewById(R.id.imgLarva);
		mImgTimer = (ImageView) layout.findViewById(R.id.imgTimer);

		mImgMinerals.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(v.getContext(), "On top-left - number of workers on minerals, on bottom-right - amount of available minerals.", Toast.LENGTH_SHORT).show();
			}
		});

		mImgSupply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(v.getContext(), "Left value - current supply, right value - supply limit", Toast.LENGTH_SHORT).show();
			}
		});

		mImgGas.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(v.getContext(), "On top-left - number of workers on gas, on bottom-right - amount of gas.", Toast.LENGTH_SHORT).show();
			}
		});

		mImgEnergy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(v.getContext(), "Number of available casts.", Toast.LENGTH_SHORT).show();
			}
		});

		mImgLarva.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(v.getContext(), "Amount of Larva available", Toast.LENGTH_SHORT).show();
			}
		});

		mImgTimer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(v.getContext(), "Current point in game time", Toast.LENGTH_SHORT).show();
			}
		});

		return layout;
	}

	//region IBuildMakerStatsView implementation

	@Override
	public void setTime(Integer seconds) {
		if(!isAdded()) {
			return;
		}

		mTxtTime.setText(UiDataViewHelper.getTimeStringFromSeconds(seconds));
	}

	@Override
	public void setEnergy(Integer energy) {
		if(!isAdded()) {
			return;
		}

		mTxtEnergy.setText(energy.toString());
	}

	@Override
	public void setLarva(Integer larva) {
		if(!isAdded()) {
			return;
		}

		if (mFrameLarva.getVisibility() == View.VISIBLE) {
			mTxtLarva.setText(larva.toString());
		}
	}

	@Override
	public void setMinerals(Integer minerals) {
		if(!isAdded()) {
			return;
		}

		mTxtMinerals.setText(minerals.toString());
	}

	@Override
	public void setWorkersOnMinerals(Integer workersOnMinerals) {
		if(!isAdded()) {
			return;
		}

		mTxtWorkersOnMinerals.setText(workersOnMinerals.toString());
	}

	@Override
	public void setGas(Integer gas) {
		if(!isAdded()) {
			return;
		}

		mTxtGas.setText(gas.toString());
	}

	@Override
	public void setWorkersOnGas(Integer workersOnGas) {
		if(!isAdded()) {
			return;
		}

		mTxtWorkersOnGas.setText(workersOnGas.toString());
	}

	@Override
	public void setSupply(Integer current, Integer allowed) {
		if(!isAdded()) {
			return;
		}

		mTxtSupply.setText(current.toString() + "/" + allowed.toString());
	}

	@Override
	public void setLarvaVisibility(boolean visible) {
		if(!isAdded()) {
			return;
		}

		mFrameLarva.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setSupplyImage(RaceEnum faction) {
		if(!isAdded()) {
			return;
		}

		if(faction == RaceEnum.Protoss) {
			mImgSupply.setImageResource(R.mipmap.bm_supply_protoss);
		} else if(faction == RaceEnum.Terran) {
			mImgSupply.setImageResource(R.mipmap.bm_supply_terrran);
		} else {
			mImgSupply.setImageResource(R.mipmap.bm_supply_zerg);
		}
	}

	//endregion
}
