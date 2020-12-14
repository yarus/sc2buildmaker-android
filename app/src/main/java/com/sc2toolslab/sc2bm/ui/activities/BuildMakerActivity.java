package com.sc2toolslab.sc2bm.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.domain.BuildItemTypeEnum;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.ui.adapters.BuildActionListAdapter;
import com.sc2toolslab.sc2bm.ui.adapters.BuildItemsListAdapter;
import com.sc2toolslab.sc2bm.ui.fragments.BuildMakerStatsFragment;
import com.sc2toolslab.sc2bm.ui.model.QueueDataItem;
import com.sc2toolslab.sc2bm.ui.presenters.BuildMakerPresenter;
import com.sc2toolslab.sc2bm.ui.utils.FloatingActionButton;
import com.sc2toolslab.sc2bm.ui.utils.NavigationManager;
import com.sc2toolslab.sc2bm.ui.views.IBuildMakerView;

import java.util.ArrayList;
import java.util.List;

public class BuildMakerActivity extends AppCompatActivity implements IBuildMakerView {
	private ListView buildListView;
	protected ListView actionsQueueListView;

	private BuildMakerStatsFragment mStatsPanelFragment;
	private BuildMakerPresenter mPresenter;
	private FloatingActionButton mBtnUndo;

	//region LifeCycle overrides

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_build_maker);

		Toolbar mToolbar = (Toolbar) findViewById(R.id.appBar);
		setSupportActionBar(mToolbar);

		//noinspection ConstantConditions
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//_setUndoBtn();
		_initControls();

		mStatsPanelFragment = (BuildMakerStatsFragment) getSupportFragmentManager().findFragmentById(R.id.panelStats);

		mPresenter = new BuildMakerPresenter(this, mStatsPanelFragment, getIntent().getStringExtra(AppConstants.BUILD_ORDER_NAME_INTENT_KEY));

		_scrollListViewToBottom();
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();

		_initControls();

		mPresenter.setSelectedIndex(mPresenter.getSelectedIndex());
	}

	@Override
	public void onBackPressed() {
		_navigateBack();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_buildmaker, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.searchable.
		int id = item.getItemId();

		if (id == R.id.action_build_save) {
			//save build in temporary file

			//navigate to build edit
			NavigationManager.startBuildEditActivity(this, mPresenter.getBuildName());

			return true;
		}

		if (id == android.R.id.home) {
			_navigateBack();

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	//endregion

	//region EventHandlers

	public void onAddActionClick(View v) {
		int tag = Integer.valueOf(v.getTag().toString());

		BuildItemTypeEnum type = BuildItemTypeEnum.Unit;
		switch (tag) {
			case 1:
				type = BuildItemTypeEnum.Unit;
				break;
			case 2:
				type = BuildItemTypeEnum.Building;
				break;
			case 3:
				type = BuildItemTypeEnum.Upgrade;
				break;
			case 4:
				type = BuildItemTypeEnum.Special;
				break;
		}

		NavigationManager.startBuildMakerAddItemActivity(this, type, mPresenter.getSelectedIndex());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
			case (1) : {
				if (resultCode == Activity.RESULT_OK) {
					String newItem = data.getStringExtra("AddItem");
					if (newItem != null && !"".equals(newItem)) {
						if (!mPresenter.addBuildItem(newItem)) {
							Toast.makeText(this, R.string.unable_to_add_item_message, Toast.LENGTH_LONG).show();
						} else {
							_scrollListViewToBottom();
						}
					}

					String newBuildName = data.getStringExtra("BuildSaved");
					if (newBuildName != null && !newBuildName.equals("")) {
						mPresenter.updateBuildName(newBuildName);
					}
				}

				break;
			}
		}
	}

	//endregion

	/*
	private void _setUndoBtn() {
		mBtnUndo = new FloatingActionButton.Builder(this)
				.withDrawable(getResources().getDrawable(R.mipmap.ic_undo))
				.withButtonColor(Color.RED)
				.withGravity(Gravity.BOTTOM | Gravity.END)
				.withMargins(0, 0, 0, 120)
				.create();

		mBtnUndo.setAlpha(0.5f);

		mBtnUndo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPresenter.undoLastItem();
				_scrollListViewToBottom();
				mBtnUndo.setAlpha(0.5f);
			}
		});

		mBtnUndo.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(v.getContext(), "Undo last build item", Toast.LENGTH_SHORT).show();
				mBtnUndo.setAlpha(0.5f);
				return false;
			}
		});
	}
	*/

	public void onUndoActionClick(View v) {
		//mPresenter.undoLastItem();
		mPresenter.undoSelectedItem();
		_scrollListViewToBottom();
	}

	private void _initControls() {
		buildListView = (ListView)findViewById(R.id.list);
		actionsQueueListView = (ListView)findViewById(R.id.actionsQueueList);

		buildListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			                        long arg3) {
				((BuildItemsListAdapter) buildListView.getAdapter()).setSelectedIndex(arg2);
				mPresenter.setSelectedIndex(arg2);
			}
		});

		actionsQueueListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				BuildItemsListAdapter baseAdapter = ((BuildItemsListAdapter) buildListView.getAdapter());
				BuildActionListAdapter statAdapter = ((BuildActionListAdapter) actionsQueueListView.getAdapter());

				BuildOrderProcessorItem baseItem = baseAdapter.getItem(baseAdapter.getSelectedIndex());

				QueueDataItem statItem = statAdapter.getItem(arg2);

				if (baseItem != null && statItem != null) {
					Toast.makeText(BuildMakerActivity.this, statItem.Item.getDisplayName() + " will be completed in " + (statItem.Item.getFinishedSecond() - baseItem.getSecondInTimeLine()) + " seconds", Toast.LENGTH_LONG).show();
				}
			}
		});

		actionsQueueListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				BuildItemsListAdapter baseAdapter = ((BuildItemsListAdapter) buildListView.getAdapter());
				BuildActionListAdapter statAdapter = ((BuildActionListAdapter) actionsQueueListView.getAdapter());

				BuildOrderProcessorItem baseItem = baseAdapter.getItem(baseAdapter.getSelectedIndex());

				QueueDataItem statItem = statAdapter.getItem(arg2);

				if (baseItem != null && statItem != null && statItem.Item.getItemType() == BuildItemTypeEnum.Unit) {
					mPresenter.addBuildItem(statItem.Item.getItemName());
				}

				return true;
			}
		});
	}

	private void _navigateBack() {
		if (mPresenter.isBuildModified()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Build order was changed. Are you sure you want to leave?")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							mPresenter.unloadCurrentBuild();
							finish();
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
						}
					});
			AlertDialog dlg = builder.create();
			dlg.show();
		} else {
			mPresenter.unloadCurrentBuild();
			finish();
		}
	}

	@Override
	public void setBuildName(String buildName) {
		//noinspection ConstantConditions
		getSupportActionBar().setTitle(buildName);
	}

	@Override
	public void renderList(List<BuildOrderProcessorItem> buildItems, List<QueueDataItem> queue, BuildOrderProcessorItem selectedItem) {
		if(buildListView.getAdapter() == null) {

			buildListView.setAdapter(new BuildItemsListAdapter(this, buildItems));
			actionsQueueListView.setAdapter(new BuildActionListAdapter(this, queue, selectedItem));
		} else {
			int selectedIndex = mPresenter.getSelectedIndex();

			((BuildItemsListAdapter)buildListView.getAdapter()).updateData(buildItems, selectedIndex);

			((BuildActionListAdapter)actionsQueueListView.getAdapter()).updateData(queue, selectedItem);
		}
	}

	private void _scrollListViewToBottom() {
		buildListView.post(new Runnable() {
			@Override
			public void run() {
				// Select the last row so it will scroll into view...
				int items = buildListView.getAdapter().getCount();

				if (items > 0) {
					buildListView.setSelection(mPresenter.getSelectedIndex());
				}
			}
		});
	}

	private void _scrollListViewToTop() {
		buildListView.post(new Runnable() {
			@Override
			public void run() {
				// Select the last row so it will scroll into view...
				buildListView.setSelection(1);
			}
		});
	}

	@Override
	public Context getContext() {
		return BuildMakerActivity.this;
	}
}
