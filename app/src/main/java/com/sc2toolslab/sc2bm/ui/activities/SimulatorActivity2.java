package com.sc2toolslab.sc2bm.ui.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.ui.adapters.BuildActionListAdapter;
import com.sc2toolslab.sc2bm.ui.adapters.SimulationDataItemsListAdapter;
import com.sc2toolslab.sc2bm.ui.fragments.BuildMakerStatsFragment;
import com.sc2toolslab.sc2bm.ui.model.QueueDataItem;
import com.sc2toolslab.sc2bm.ui.model.SimulatorDataItem;
import com.sc2toolslab.sc2bm.ui.presenters.SimulatorModeEnum;
import com.sc2toolslab.sc2bm.ui.presenters.SimulatorPresenter2;
import com.sc2toolslab.sc2bm.ui.providers.BuildItemImageProvider;
import com.sc2toolslab.sc2bm.ui.utils.NavigationManager;
import com.sc2toolslab.sc2bm.ui.views.ISimulatorView2;

import java.util.List;

public class SimulatorActivity2 extends AppCompatActivity implements ISimulatorView2 {
    private SimulatorPresenter2 mPresenter;

    private int mItemSize, mItemSpacing;

    private BuildItemImageProvider mImageProvider;
    private BuildMakerStatsFragment mStatsPanelFragment;

    private ListView mActionsQueueListView;
    private GridView mMainItemsGridView;
    private View mItemInfoLayout;
    private Dialog mItemAddDialog;

    private ImageView mImgBaseMode;
    private ImageView mImgPlayPause;
    private ImageView mImgBuildStructure;
    private ImageView mImgSpecial;
    private ImageView mArmyMode;
    private FrameLayout mBtnBaseMode;
    private FrameLayout mBtnBuildStructure;
    private FrameLayout mBtnLarva;
    private FrameLayout mBtnSpecial;
    private FrameLayout mBtnArmy;
    private TextView mModeName;

    private boolean mIsItemAddShown = false;

    private AdapterView.OnItemClickListener mQueueClickListener;
    private AdapterView.OnItemClickListener mMainItemClickListener;
    private AdapterView.OnItemClickListener mBuildItemClickListener;
    private ViewTreeObserver.OnGlobalLayoutListener mBuildItemLayoutListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator_2);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(mToolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mItemSize = getResources().getDimensionPixelSize(R.dimen.additem_dialog_width);
        mItemSpacing = getResources().getDimensionPixelSize(R.dimen.tile_list_item_spacing);

        _initControls();

        mImageProvider = new BuildItemImageProvider();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String gameSpeed = prefs.getString(getString(R.string.pref_game_speed), "faster");

        mPresenter = new SimulatorPresenter2(this, mStatsPanelFragment, gameSpeed);

        if (savedInstanceState != null) {
            boolean isPlaying = savedInstanceState.getBoolean("IsPlaying");
            int currentSecond = savedInstanceState.getInt("CurrentSecond");

            mPresenter.setCurrentSecond(currentSecond);

            if (isPlaying && !mPresenter.getIsPlaying()) {
                mPresenter.changePlayMode();
            }
        }

        _renderStaticUi();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        _initControls();
    }

    @Override
    protected void onDestroy() {
        mPresenter.finish();

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("IsPlaying", mPresenter.getIsPlaying());
        outState.putInt("CurrentSecond", mPresenter.getCurrentSecond());
    }

    private void _initControls() {
        mStatsPanelFragment = (BuildMakerStatsFragment) getSupportFragmentManager().findFragmentById(R.id.panelStats);
        mActionsQueueListView = (ListView)findViewById(R.id.actionsQueueList);
        mMainItemsGridView = (GridView)findViewById(R.id.gvMainItems);
        mImgBaseMode = (ImageView) findViewById(R.id.imgBaseMode);
        mImgPlayPause = (ImageView) findViewById(R.id.btnPlayPause);
        mBtnLarva = (FrameLayout) findViewById(R.id.btnLarva);
        mImgBuildStructure = (ImageView) findViewById(R.id.imgBuildStructure);
        mBtnBuildStructure = (FrameLayout) findViewById(R.id.btnBuildStructure);
        mBtnSpecial = (FrameLayout) findViewById(R.id.btnSpecial);
        mImgSpecial = (ImageView) findViewById(R.id.imgSpecial);
        mArmyMode = (ImageView) findViewById(R.id.imgArmyMode);
        mBtnArmy = (FrameLayout) findViewById(R.id.btnArmy);
        mBtnBaseMode = (FrameLayout) findViewById(R.id.btnBaseMode);
        mModeName = (TextView) findViewById(R.id.txtModeName);
    }

    //region ISimulatorView2 methods

    public Context getContext() {
        return this;
    }

    public void showMessage(String message) {
        Toast.makeText(SimulatorActivity2.this, message, Toast.LENGTH_SHORT).show();
    }

    public void render(BuildOrderProcessorItem lastItem, List<QueueDataItem> mQueue, List<SimulatorDataItem> mMainList, List<SimulatorDataItem> buildItemsForStructure, BuildItemEntity buildStructure) {
        _renderQueue(mQueue, lastItem);
        _renderMainItems(mMainList);

        if (mIsItemAddShown) {
            _renderItemAdd(buildItemsForStructure, buildStructure);
        }
    }

    //endregion

    //region Event Handlers

    public void onChangeModeClick(View view) {
        int tag = Integer.valueOf(view.getTag().toString());

        SimulatorModeEnum mode = SimulatorModeEnum.BASE;
        switch (tag) {
            case 1:
                mode = SimulatorModeEnum.BASE;
                break;
            case 2:
                mode = SimulatorModeEnum.BUILD;
                break;
            case 3:
                mode = SimulatorModeEnum.MORPH;
                break;
            case 4:
                mode = SimulatorModeEnum.SPECIAL;
                break;
            case 5:
                mode = SimulatorModeEnum.ARMY;
                break;
        }

        if (mPresenter.getCurrentMode() != mode) {
            mPresenter.changeMode(mode);

            _renderStaticUi();
        }
    }

    public void onPlayPause(View view) {
        mPresenter.changePlayMode();
        _setPlayPauseImage(mPresenter.getIsPlaying());
        _setKeepScreenFlag(mPresenter.getIsPlaying());
    }

    public void onReset(View view) {
        mPresenter.reset();
        _setPlayPauseImage(mPresenter.getIsPlaying());
        _setKeepScreenFlag(mPresenter.getIsPlaying());
    }

    public void onFinish(View view) {
        if (mPresenter.getIsPlaying()) {
            mPresenter.changePlayMode();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to finish simulation?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BuildItemStatistics lastItemStatistics = mPresenter.getLastItemStatistics();
                        NavigationManager.startSimulatorResultsActivity(SimulatorActivity2.this, lastItemStatistics);
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
    }

    //endregion

    //region rendering

    private void _renderStaticUi() {
        _renderBuildStructureIcon(mPresenter.getRace(), mPresenter.getCurrentMode() == SimulatorModeEnum.BASE);
        _renderLarvaButton(mPresenter.getRace(), mPresenter.getCurrentMode() == SimulatorModeEnum.MORPH);
        _renderBuildButton(mPresenter.getRace(), mPresenter.getCurrentMode() == SimulatorModeEnum.BUILD);
        _renderSpecialButton(mPresenter.getRace(), mPresenter.getCurrentMode() == SimulatorModeEnum.SPECIAL);
        _renderArmyButton(mPresenter.getRace(), mPresenter.getCurrentMode() == SimulatorModeEnum.ARMY);
        _setPlayPauseImage(mPresenter.getIsPlaying());
        _setKeepScreenFlag(mPresenter.getIsPlaying());
        _renderModeName(mPresenter.getCurrentMode());
    }

    private void _renderModeName(SimulatorModeEnum mode) {
        String name = "Your Base";

        switch (mode) {
            case BASE:
                name = "Your Structures";
                break;
            case BUILD:
                name = "Build New Structure";
                break;
            case SPECIAL:
                name = "Special Action";
                break;
            case ARMY:
                name = "Your Units";
                break;
            case MORPH:
                name = "Morph Unit";
                break;
        }

        mModeName.setText(name);
    }

    public void _renderBuildStructureIcon(RaceEnum race, boolean isSelected) {
        if (isSelected) {
            mBtnBaseMode.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
        } else {
            mBtnBaseMode.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
        }

        if (race == RaceEnum.Terran) {
            mImgBaseMode.setImageResource(R.drawable.bi_commandcenter);
        } else if (race == RaceEnum.Protoss) {
            mImgBaseMode.setImageResource(R.drawable.bi_nexus);
        } else {
            mImgBaseMode.setImageResource(R.drawable.bi_hatchery);
        }
    }

    private void _renderLarvaButton(RaceEnum race, boolean isSelected) {
        if (isSelected) {
            mBtnLarva.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
        } else {
            mBtnLarva.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
        }

        mBtnLarva.setVisibility(race == RaceEnum.Zerg ? View.VISIBLE : View.GONE);
    }

    private void _renderSpecialButton(RaceEnum race, boolean isSelected) {
        if (isSelected) {
            mBtnSpecial.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
        } else {
            mBtnSpecial.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
        }

        if (race == RaceEnum.Terran) {
            mImgSpecial.setImageResource(R.drawable.bi_callmule);
        } else if (race == RaceEnum.Protoss) {
            mImgSpecial.setImageResource(R.drawable.bi_chronoboost);
        } else if (race == RaceEnum.Zerg) {
            mImgSpecial.setImageResource(R.drawable.bi_spawncreeptumor);
        }
    }

    private void _renderArmyButton(RaceEnum race, boolean isSelected) {
        if (isSelected) {
            mBtnArmy.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
        } else {
            mBtnArmy.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
        }

        if (race == RaceEnum.Terran) {
            mArmyMode.setImageResource(R.drawable.bi_marine);
        } else if (race == RaceEnum.Protoss) {
            mArmyMode.setImageResource(R.drawable.bi_zealot);
        } else if (race == RaceEnum.Zerg) {
            mArmyMode.setImageResource(R.drawable.bi_zergling);
        }
    }

    private void _renderBuildButton(RaceEnum race, boolean isSelected) {
        if (isSelected) {
            mBtnBuildStructure.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
        } else {
            mBtnBuildStructure.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
        }

        if (race == RaceEnum.Terran) {
            mImgBuildStructure.setImageResource(R.drawable.bi_scv);
        } else if (race == RaceEnum.Protoss) {
            mImgBuildStructure.setImageResource(R.drawable.bi_probe);
        } else if (race == RaceEnum.Zerg) {
            mImgBuildStructure.setImageResource(R.drawable.bi_drone);
        }
    }

    private void _renderMainItems(List<SimulatorDataItem> buildItems) {
        SimulationDataItemsListAdapter adapter = (SimulationDataItemsListAdapter) mMainItemsGridView.getAdapter();

        if (adapter == null) {
            adapter = new SimulationDataItemsListAdapter(this, buildItems);
            mMainItemsGridView.setAdapter(adapter);
        } else {
            adapter.updateData(buildItems);
        }

        final SimulationDataItemsListAdapter finalAdapter = adapter;

        if (mMainItemClickListener == null) {
            mMainItemClickListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SimulatorDataItem item = finalAdapter.getItem(position);

                    if (!item.IsEnabled || !item.IsClickable) {
                        return;
                    }

                    if (mPresenter.getCurrentMode() == SimulatorModeEnum.BUILD) {
                        mPresenter.addBuildItem(item.Name);
                    } else if (mPresenter.getCurrentMode() == SimulatorModeEnum.BASE) {
                        // Show add item
                        mPresenter.setBuildStructure(item.Name);
                        //_showItemInfoDialog(item);
                        // NavigationManager.startBuildMakerAddItemActivity(this, item.Type, mPresenter.getSelectedIndex(), false);
                    } else if (mPresenter.getCurrentMode() == SimulatorModeEnum.MORPH) {
                        // Show morph item
                        mPresenter.addBuildItem(item.Name);
                        // NavigationManager.startBuildMakerAddItemActivity(this, item.Type, mPresenter.getSelectedIndex(), false);
                    } else if (mPresenter.getCurrentMode() == SimulatorModeEnum.SPECIAL) {
                        mPresenter.addBuildItem(item.Name);
                    }
                }
            };

            mMainItemsGridView.setOnItemClickListener(mMainItemClickListener);
        }
    }

    private void _renderQueue(List<QueueDataItem> mQueue, BuildOrderProcessorItem lastItem) {
        BuildActionListAdapter adapter = (BuildActionListAdapter) mActionsQueueListView.getAdapter();

        if(adapter == null) {
            adapter = new BuildActionListAdapter(this, mQueue, lastItem);
            mActionsQueueListView.setAdapter(adapter);
        } else {
            adapter.updateData(mQueue, lastItem);
        }

        final BuildActionListAdapter finalAdapter = adapter;
        if (mQueueClickListener == null) {
            mQueueClickListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    QueueDataItem queueItem = finalAdapter.getItem(position);
                    mPresenter.cancelBuildItem(queueItem.Item);
                }
            };

            mActionsQueueListView.setOnItemClickListener(mQueueClickListener);
        }
    }

    public void _setKeepScreenFlag(boolean keepScreen) {
        if(keepScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public void _setPlayPauseImage(boolean isPlaying) {
        if (isPlaying) {
            mImgPlayPause.setImageResource(R.drawable.pause);
        } else {
            mImgPlayPause.setImageResource(R.drawable.play);
        }
    }

    //endregion
    private void _renderItemAdd(List<SimulatorDataItem> items, BuildItemEntity buildStructure) {
        if (mItemInfoLayout == null) {
            mItemInfoLayout = getLayoutInflater().inflate(R.layout.fragment_simulator_build_items, (ViewGroup) findViewById(R.id.llToast));
        }

        _setItemTitle(mItemInfoLayout, buildStructure);

        final GridView gvBuildItems = (GridView) mItemInfoLayout.findViewById(R.id.gvBuildItems);
        gvBuildItems.setVisibility(View.VISIBLE);

        SimulationDataItemsListAdapter adapter = (SimulationDataItemsListAdapter) gvBuildItems.getAdapter();

        if(adapter == null) {
            adapter = new SimulationDataItemsListAdapter(this, items);
            gvBuildItems.setAdapter(adapter);
        } else {
            adapter.updateData(items);
        }

        final SimulationDataItemsListAdapter finalAdapter = adapter;

        if (mBuildItemLayoutListener == null) {
            mBuildItemLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (finalAdapter.getNumColumns() == 0) {
                        final int numColumns = (int) Math.floor(gvBuildItems.getWidth() / (mItemSize + mItemSpacing));
                        if (numColumns > 0) {
                            final int columnWidth = (gvBuildItems.getWidth() / numColumns) - mItemSpacing;
                            finalAdapter.setNumColumns(numColumns);
                            //noinspection SuspiciousNameCombination
                            finalAdapter.setItemHeight(columnWidth);
                        }
                    }
                }
            };

            gvBuildItems.getViewTreeObserver().addOnGlobalLayoutListener(mBuildItemLayoutListener);
        }

        if (mBuildItemClickListener == null) {
            mBuildItemClickListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SimulatorDataItem item = finalAdapter.getItem(position);
                    if (item.IsEnabled && item.IsClickable) {
                        mPresenter.addBuildItem(item.Name);
                        mIsItemAddShown = false;
                        mItemAddDialog.dismiss();
                    }
                }
            };

            gvBuildItems.setOnItemClickListener(mBuildItemClickListener);
        }
    }

    public void showItemsForStructure(List<SimulatorDataItem> items, BuildItemEntity buildStructure) {
        if (items == null || items.size() == 0) {
            return;
        }

        _renderItemAdd(items, buildStructure);

        if (mItemAddDialog == null) {
            mItemAddDialog = new Dialog(this, R.style.dialogTheme) {
                @Override
                public boolean onTouchEvent(MotionEvent event)
                {
                    int action = event.getAction();

                    if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN){
                        mIsItemAddShown = false;
                        this.cancel();
                        this.dismiss();
                    }
                    return false;
                }
            };
            mItemAddDialog.setContentView(mItemInfoLayout);
        }

        mIsItemAddShown = true;
        mItemAddDialog.setCanceledOnTouchOutside(true);
        mItemAddDialog.setCancelable(true);
        mItemAddDialog.show();
    }

    private void _setItemTitle(View layout, BuildItemEntity item) {
        TextView title = (TextView) layout.findViewById(R.id.txtName);
        if (item != null) {
            title.setText(item.getDisplayName());
        } else {
            title.setText("");
        }
    }
}
