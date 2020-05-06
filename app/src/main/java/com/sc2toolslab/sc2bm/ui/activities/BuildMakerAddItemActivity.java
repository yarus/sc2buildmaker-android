package com.sc2toolslab.sc2bm.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.domain.BuildItemTypeEnum;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessor;
import com.sc2toolslab.sc2bm.ui.adapters.AddBuildItemsListAdapter;
import com.sc2toolslab.sc2bm.ui.presenters.BuildMakerAddItemPresenter;
import com.sc2toolslab.sc2bm.ui.providers.BuildItemImageProvider;
import com.sc2toolslab.sc2bm.ui.providers.BuildProcessorConfigurationProvider;
import com.sc2toolslab.sc2bm.ui.views.IBuildMakerAddItemView;

import java.util.List;

public class BuildMakerAddItemActivity extends AppCompatActivity implements IBuildMakerAddItemView {
	private int mItemSize, mItemSpacing;

	private BuildMakerAddItemPresenter mPresenter;

	private BuildItemImageProvider mImageProvider;
	private AddBuildItemsListAdapter mAdapter;
	private GridView mGridView;
	private View mItemInfoLayout;
	private Dialog mItemAddDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_build_maker_add);

		mImageProvider = new BuildItemImageProvider();

		mItemSize = getResources().getDimensionPixelSize(R.dimen.additemwidth);
		mItemSpacing = getResources().getDimensionPixelSize(R.dimen.tile_list_item_spacing);

		_initControls();

		mPresenter = new BuildMakerAddItemPresenter(this, BuildItemTypeEnum.valueOf(getIntent().getStringExtra(AppConstants.BUILD_ITEM_TYPE_INTENT_FLAG)), getIntent().getIntExtra("SelectedItemPosition", -1));
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();

		_initControls();
	}

	//region Event Handlers

	public void onOutOfWidnowClick(View v) {
		finish();
	}

	public void onAddActionClick(View v) {
		BuildItemTypeEnum type = BuildItemTypeEnum.valueOf(v.getTag().toString());

		if (type == mPresenter.getItemType()) {
			finish();
		} else {
			getIntent().putExtra(AppConstants.BUILD_ITEM_TYPE_INTENT_FLAG, type.name());
			mPresenter.newItemTypeRequested(type);
		}
	}

	//endregion

	//region IBuildMakerAddItemView implementation

	@Override
	public void sendAddItemCommand(String buildItemName) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra("AddItem", buildItemName);
		setResult(Activity.RESULT_OK, resultIntent);

		finish();
	}

	@Override
	public void renderGrid(BuildOrderProcessor processor, List<BuildItemEntity> data, BuildItemStatistics currentStats, BuildItemStatistics lastItemStats) {
		mAdapter = new AddBuildItemsListAdapter(this, processor, data, currentStats, lastItemStats);
		mGridView.setAdapter(mAdapter);
	}

	@Override
	public void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void showItemInfoDialog(BuildItemEntity item) {
		if (mItemInfoLayout == null) {
			mItemInfoLayout = getLayoutInflater().inflate(R.layout.fragment_build_maker_add_item_info, (ViewGroup) findViewById(R.id.llToast));

			LinearLayout firstColumn = (LinearLayout) mItemInfoLayout.findViewById(R.id.firstColumn);
			firstColumn.setVisibility(View.VISIBLE);

			LinearLayout secondColumn = (LinearLayout) mItemInfoLayout.findViewById(R.id.secondColumn);
			secondColumn.setVisibility(View.VISIBLE);
		}

		_setSupplyView(mItemInfoLayout);
		_setUnitImageForItem(mItemInfoLayout, item);
		_setItemTitle(mItemInfoLayout, item);
		_setRequirementInfoForItem(mItemInfoLayout, item);
		_setMineralsForItem(mItemInfoLayout, item);
		_setGasForItem(mItemInfoLayout, item);
		_setSupplyForItem(mItemInfoLayout, item);
		_SetTimeForItem(mItemInfoLayout, item);

		if (mItemAddDialog == null) {
			mItemAddDialog = new Dialog(this, R.style.dialogTheme) {
				@Override
				public boolean dispatchTouchEvent(MotionEvent ev) {
					dismiss();

					return super.dispatchTouchEvent(ev);
				}
			};
			mItemAddDialog.setContentView(mItemInfoLayout);
		}

		mItemAddDialog.show();
	}

	//endregion

	//region private methods

	private void _initControls() {
		mGridView = (GridView) findViewById(R.id.gvBuildItems);
		mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (mAdapter.getNumColumns() == 0) {
					final int numColumns = (int) Math.floor(mGridView.getWidth() / (mItemSize + mItemSpacing));
					if (numColumns > 0) {
						final int columnWidth = (mGridView.getWidth() / numColumns) - mItemSpacing;
						mAdapter.setNumColumns(numColumns);
						//noinspection SuspiciousNameCombination
						mAdapter.setItemHeight(columnWidth);
					}
				}
			}
		});
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mPresenter.addItemRequested(position);
			}
		});
		mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				mPresenter.itemDetailsRequested(position);
				return true;
			}
		});
	}

	private void _setSupplyView(View layout) {
		ImageView supplyView = (ImageView) layout.findViewById(R.id.toast_supplyimg);

		switch (mPresenter.getBuildFaction()) {
			case Terran:
				supplyView.setImageResource(R.mipmap.bm_supply_terrran);
				break;
			case Protoss:
				supplyView.setImageResource(R.mipmap.bm_supply_protoss);
				break;
			case Zerg:
				supplyView.setImageResource(R.mipmap.bm_supply_zerg);
				break;
		}
	}

	private void _setUnitImageForItem(View layout, BuildItemEntity item) {
		ImageView image = (ImageView) layout.findViewById(R.id.toast_image);

		Integer imageId = mImageProvider.getImageResourceIdByKey(item.getName());
		if (imageId != null) {
			image.setImageResource(imageId);
		} else {
			image.setImageResource(R.drawable.empty_cell);
		}
	}

	private void _setItemTitle(View layout, BuildItemEntity item) {
		TextView title = (TextView) layout.findViewById(R.id.txtName);
		title.setText(item.getDisplayName());
	}

	private void _setRequirementInfoForItem(View layout, BuildItemEntity item) {
		TextView txtRequirement = (TextView) layout.findViewById(R.id.txtRequirementInfo);
		txtRequirement.setText(mPresenter.getRequirementMessage(item));
	}

	private void _setMineralsForItem(View layout, BuildItemEntity item) {
		TextView minerals = (TextView) layout.findViewById(R.id.toast_minerals);
		minerals.setVisibility(View.VISIBLE);
		minerals.setText(String.valueOf(item.getCostMinerals()));

		ImageView mineralsimg = (ImageView) layout.findViewById(R.id.toast_mineralsimg);
		mineralsimg.setVisibility(View.VISIBLE);
	}

	private void _setGasForItem(View layout, BuildItemEntity item) {
		TextView gas = (TextView) layout.findViewById(R.id.toast_gas);
		gas.setVisibility(View.VISIBLE);
		gas.setText(String.valueOf(item.getCostGas()));

		ImageView gasimg = (ImageView) layout.findViewById(R.id.toast_gasimg);
		gasimg.setVisibility(View.VISIBLE);
	}

	private void _setSupplyForItem(View layout, BuildItemEntity item) {
		TextView supply = (TextView) layout.findViewById(R.id.toast_supply);
		supply.setVisibility(View.VISIBLE);
		supply.setText(String.valueOf(item.getCostSupply()));

		ImageView supplyimg = (ImageView) layout.findViewById(R.id.toast_supplyimg);
		supplyimg.setVisibility(View.VISIBLE);
	}

	private void _SetTimeForItem(View layout, BuildItemEntity item) {
		TextView time = (TextView) layout.findViewById(R.id.toast_time);
		time.setVisibility(View.VISIBLE);

		time.setText(String.valueOf(item.getBuildTimeInSeconds()));

		ImageView timeimg = (ImageView) layout.findViewById(R.id.toast_timeimg);
		timeimg.setVisibility(View.VISIBLE);
	}

	//endregion
}