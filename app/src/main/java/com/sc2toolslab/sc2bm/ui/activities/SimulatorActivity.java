package com.sc2toolslab.sc2bm.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.ui.fragments.BuildMakerStatsFragment;
import com.sc2toolslab.sc2bm.ui.presenters.SimulatorPresenter;
import com.sc2toolslab.sc2bm.ui.providers.BuildItemImageProvider;
import com.sc2toolslab.sc2bm.ui.views.ISimulatorView;

public class SimulatorActivity extends AppCompatActivity implements ISimulatorView {
    private SimulatorPresenter mPresenter;
    private BuildItemImageProvider mImageProvider;

    private ImageView mImgPlayPause;
    private BuildMakerStatsFragment mStatsPanelFragment;

    //region Lifecycle handlers

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(mToolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _initControls();

        mStatsPanelFragment = (BuildMakerStatsFragment) getSupportFragmentManager().findFragmentById(R.id.panelStats);

        mImageProvider = new BuildItemImageProvider();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String gameSpeed = prefs.getString(getString(R.string.pref_game_speed), "faster");

        mPresenter = new SimulatorPresenter(this, mStatsPanelFragment, gameSpeed);

        if (savedInstanceState != null) {
            boolean isPlaying = savedInstanceState.getBoolean("IsPlaying");
            int currentSecond = savedInstanceState.getInt("CurrentSecond");

            mPresenter.setCurrentSecond(currentSecond);

            if (isPlaying && !mPresenter.getIsPlaying()) {
                mPresenter.changePlayMode();
            }
        }
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

    //endregion

    //region Event Handlers

    public void onPlayPause(View view) {
        mPresenter.changePlayMode();
    }

    public void onReset(View view) {
        mPresenter.stopPlayer();
    }

    public void onFinish(View view) {
        mPresenter.finishSimulator();
    }

    //endregion

    //region IBuildPlayerView implementation

    @Override
    public void setCurrentSecond(int currentSecond) {

    }

    @Override
    public void setPlayPauseImage(boolean isPlaying) {
        if (isPlaying) {
            mImgPlayPause.setImageResource(R.drawable.pause);
        } else {
            mImgPlayPause.setImageResource(R.drawable.play);
        }
    }

    @Override
    public void setKeepScreenFlag(boolean keepScreen) {
        if(keepScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    //endregion

    //region private methods

    private void _initControls() {
        mImgPlayPause = (ImageView) findViewById(R.id.imgPlayPause);
    }

    private void _navigateBack() {
        finish();
    }

    @Override
    public Context getContext() {
        return this;
    }

    //endregion

    @Override
    public void showMessage(String message) {
        Toast.makeText(SimulatorActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("IsPlaying", mPresenter.getIsPlaying());
        outState.putInt("CurrentSecond", mPresenter.getCurrentSecond());
    }

    @Override
    protected void onDestroy() {
        mPresenter.formDestroying();

        super.onDestroy();
    }
}