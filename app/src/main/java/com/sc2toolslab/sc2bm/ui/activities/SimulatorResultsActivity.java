package com.sc2toolslab.sc2bm.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.ui.adapters.StatisticsListAdapter;
import com.sc2toolslab.sc2bm.ui.model.SimulationResultsData;
import com.sc2toolslab.sc2bm.ui.presenters.SimulatorResultsPresenter;
import com.sc2toolslab.sc2bm.ExpandableGridView;
import com.sc2toolslab.sc2bm.ui.utils.NavigationManager;
import com.sc2toolslab.sc2bm.ui.utils.UiDataViewHelper;
import com.sc2toolslab.sc2bm.ui.views.ISimulatorResultsView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SimulatorResultsActivity extends AppCompatActivity implements ISimulatorResultsView {
    private SimulatorResultsPresenter mPresenter;

    private ExpandableGridView mArmyPanel;
    private ExpandableGridView mUpgradesPanel;
    private ExpandableGridView mBuildingsPanel;

    private TextView mTxtBuildName;
    private TextView mTxtVersion;
    private TextView mTxtCreated;
    private TextView mTxtVisited;
    private TextView mTxtBuildLength;

    private ImageView mImgFaction;
    private ImageView mImgFactionSeparator;

    private TextView mTxtTotalIncome;
    private TextView mTxtAverageIncome;
    private TextView mTxtAverageUnspent;
    private TextView mTxtAverageSpendingQuotient;
    private TextView mTxtSupplyCapTime;
    private TextView mTxtMainSaturationTiming;
    private TextView mTxtNaturalSaturationTiming;
    private TextView mTxtArmySupplyLabel;
    private TextView mTxtUpgradeLabel;
    private TextView mTxtMineralsToGasRatio;
    private TextView mTxtThirdSaturationTiming;
    private TextView mTxtWorkersCount;

    //region Lifecycle handlers

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator_results);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _initControls();

        String buildOrderToCompareWith = getIntent().getStringExtra(AppConstants.BUILD_ORDER_NAME_INTENT_KEY);
        if (buildOrderToCompareWith == null) {
            buildOrderToCompareWith = "";
        }

        mPresenter = new SimulatorResultsPresenter(this, buildOrderToCompareWith);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        _initControls();
    }

    @Override
    public void onBackPressed() {
        _navigateBack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simulator_results, menu);

        MenuItem itemEdit = menu.findItem(R.id.action_build_settings);
        if (itemEdit != null) {
            itemEdit.setVisible(mPresenter.isFreeSimulationMode());
        }

        MenuItem itemSave = menu.findItem(R.id.action_build_save);
        if (itemSave != null) {
            itemSave.setVisible(!mPresenter.isFreeSimulationMode());
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.searchable.
        int id = item.getItemId();

        if (id == R.id.action_build_settings) {
            //save build in temporary file

            mPresenter.cleanLoadedBuildOrder();

            //navigate to build edit
            NavigationManager.startBuildMakerActivity(this, "SYSTEM_SIMULATOR_RESULTS");

            finish();
            return true;
        }

        if (id == R.id.action_build_save) {
            //save build in temporary file

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

    //region Event Handlers

    //endregion

    //region ISimulatorResultsView implementation

    //endregion

    //region private methods

    private void _initControls() {
        mArmyPanel = (ExpandableGridView) findViewById(R.id.gvArmy);
        mUpgradesPanel = (ExpandableGridView) findViewById(R.id.gvUpgrades);
        mBuildingsPanel = (ExpandableGridView) findViewById(R.id.gvBuildings);

        mTxtBuildName = (TextView) findViewById(R.id.txtBuildName);
        mTxtVersion = (TextView) findViewById(R.id.txtVersion);
        mTxtCreated = (TextView) findViewById(R.id.txtCreated);
        mTxtVisited = (TextView) findViewById(R.id.txtVisited);
        mTxtBuildLength = (TextView) findViewById(R.id.txtBuildLength);

        mImgFaction = (ImageView) this.findViewById(R.id.imgFaction);
        mImgFactionSeparator = (ImageView) this.findViewById(R.id.imgFactionSeparator);

        mTxtTotalIncome = (TextView) findViewById(R.id.txtTotalIncome);
        mTxtThirdSaturationTiming = (TextView) findViewById(R.id.txtThirdSaturation);
        mTxtAverageIncome = (TextView) findViewById(R.id.txtAverageIncome);
        mTxtAverageUnspent = (TextView) findViewById(R.id.txtAverageUnspent);
        mTxtAverageSpendingQuotient = (TextView) findViewById(R.id.txtAverageSpendingQuotient);
        mTxtSupplyCapTime = (TextView) findViewById(R.id.txtSupplyCapTime);
        mTxtMainSaturationTiming = (TextView) findViewById(R.id.txtMainSaturationTiming);
        mTxtNaturalSaturationTiming = (TextView) findViewById(R.id.txtNaturalSaturationTiming);

        mTxtArmySupplyLabel = (TextView) findViewById(R.id.txtArmySupplyLabel);
        mTxtUpgradeLabel = (TextView) findViewById(R.id.txtUpgradeLabel);
        mTxtMineralsToGasRatio = (TextView) findViewById(R.id.txtMineralsToGasRatio);
        mTxtWorkersCount = (TextView) findViewById(R.id.txtWorkersCount);
    }

    private void _navigateBack() {
        finish();
    }

    @Override
    public void render(SimulationResultsData data, BuildItemStatistics lastItemStats) {
        _renderStats(mArmyPanel, data.Army, lastItemStats);
        _renderStats(mUpgradesPanel, data.Upgrades, lastItemStats);
        _renderStats(mBuildingsPanel, data.Buildings, lastItemStats);

        _renderStringValue(mTxtBuildName, data.BuildName);
        _renderStringValue(mTxtVersion, data.SC2Version);
        _renderDateValue(mTxtCreated, data.CreatedAt);
        _renderDateValue(mTxtVisited, data.UpdatedAt);
        _renderTimeString(mTxtBuildLength, data.BuildLength);
        _renderFaction(data.Race);

        _renderStringValue(mTxtArmySupplyLabel, "Army Supply (" + data.ArmySupply + ")");
        _renderStringValue(mTxtUpgradeLabel, "Upgrades (" + data.Upgrades.size() + ")");

        _renderIntValue(mTxtTotalIncome, data.TotalIncome);
        _renderTimeString(mTxtThirdSaturationTiming, data.ThirdSaturationTiming);

        _renderDoubleValue(mTxtWorkersCount, data.Workers);
        _renderDoubleValue(mTxtMineralsToGasRatio, data.MineralsToGasRatio);
        _renderDoubleValue(mTxtAverageIncome, data.AverageIncome);
        _renderDoubleValue(mTxtAverageUnspent, data.AverageUnspent);
        _renderDoubleValue(mTxtAverageSpendingQuotient, data.AverageSpendingQuotient);
        _renderTimeString(mTxtSupplyCapTime, data.SupplyCapTime);
        _renderTimeString(mTxtMainSaturationTiming, data.MainSaturationTiming);
        _renderTimeString(mTxtNaturalSaturationTiming, data.NaturalSaturationTiming);
    }

    private void _renderStats(ExpandableGridView panel, List<BuildItemEntity> items, BuildItemStatistics lastItemStats) {
        StatisticsListAdapter adapter = (StatisticsListAdapter) panel.getAdapter();

        if (adapter == null) {
            adapter = new StatisticsListAdapter(this, items, lastItemStats);
            panel.setExpanded(true);
            panel.setAdapter(adapter);
        } else {
            panel.setExpanded(true);
            adapter.updateData(items, lastItemStats);
        }
    }

    private void _renderTimeString(TextView view, int value) {
        String lengthString = UiDataViewHelper.getTimeStringFromSeconds(value);

        if (lengthString.length() > 0) {
            view.setText(lengthString);
        } else {
            view.setText("");
        }
    }

    private void _renderIntValue(TextView view, int value) {
        view.setText(Integer.toString(value));
    }

    private void _renderDoubleValue(TextView view, double value) {
        view.setText(String.format("%.0f", value));
    }

    private void _renderStringValue(TextView view, String value) {
        view.setText(value);
    }

    private void _renderDateValue(TextView view, long date) {
        view.setText(_getDateString(date));
    }

    public void _renderFaction(RaceEnum mainFaction) {
        if (mainFaction == RaceEnum.Protoss){
            mImgFaction.setImageResource(R.mipmap.ic_protoss_gradiented);
            mImgFactionSeparator.setBackgroundResource(R.drawable.protoss_separator);
        }else if (mainFaction == RaceEnum.Terran){
            mImgFaction.setImageResource(R.mipmap.ic_terran_gradiented);
            mImgFactionSeparator.setBackgroundResource(R.drawable.terran_separator);
        }else if (mainFaction == RaceEnum.Zerg){
            mImgFaction.setImageResource(R.mipmap.ic_zerg_gradiented);
            mImgFactionSeparator.setBackgroundResource(R.drawable.zerg_separator);
        }
    }

    private String _getDateString(long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.US);
        return simpleDateFormat.format(new Date(date));
    }

    //endregion

    @Override
    public void showMessage(String message) {
        Toast.makeText(SimulatorResultsActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext() {
        return this;
    }
}