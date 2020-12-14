package com.sc2toolslab.sc2bm.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.ui.adapters.StatisticsListAdapter;
import com.sc2toolslab.sc2bm.ui.presenters.SimulatorResultsPresenter;
import com.sc2toolslab.sc2bm.ui.utils.NavigationManager;
import com.sc2toolslab.sc2bm.ui.views.ISimulatorResultsView;

import java.util.ArrayList;
import java.util.List;

public class SimulatorResultsActivity extends AppCompatActivity implements ISimulatorResultsView {
    private SimulatorResultsPresenter mPresenter;

    private GridView mStatisticsPanel;

    //region Lifecycle handlers

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator_results);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(mToolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _initControls();

        mPresenter = new SimulatorResultsPresenter(this);

        BuildItemStatistics lastItemStatistics = (BuildItemStatistics) getIntent().getSerializableExtra("LastItemStatistics");

        if (lastItemStatistics != null) {
            mPresenter.setStatistics(lastItemStatistics);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        _initControls();

        BuildItemStatistics lastItemStatistics = (BuildItemStatistics) getIntent().getSerializableExtra("LastItemStatistics");

        if (lastItemStatistics != null) {
            mPresenter.setStatistics(lastItemStatistics);
        }
    }

    @Override
    public void onBackPressed() {
        _navigateBack();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            BuildItemStatistics lastItemStatistics = (BuildItemStatistics)savedInstanceState.get("LastItemStatistics");
            mPresenter.setStatistics(lastItemStatistics);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("LastItemStatistics", mPresenter.getStatistics());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simulator_results, menu);

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
        mStatisticsPanel = (GridView)findViewById(R.id.gvStatistics);
    }

    private void _navigateBack() {
        finish();
    }

    @Override
    public void render(List<BuildItemEntity> buildItems, BuildItemStatistics currentStats) {
        StatisticsListAdapter adapter = (StatisticsListAdapter) mStatisticsPanel.getAdapter();

        if (adapter == null) {
            adapter = new StatisticsListAdapter(this, buildItems, currentStats);
            mStatisticsPanel.setAdapter(adapter);
        } else {
            adapter.updateData(new ArrayList<>(buildItems), currentStats);
        }
    }

    //endregion

    @Override
    public void showMessage(String message) {
        Toast.makeText(SimulatorResultsActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}