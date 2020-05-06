package com.sc2toolslab.sc2bm.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.dataaccess.FileStorageHelper;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.ui.presenters.BuildViewPresenter;
import com.sc2toolslab.sc2bm.ui.utils.NavigationManager;
import com.sc2toolslab.sc2bm.ui.utils.UiDataViewHelper;
import com.sc2toolslab.sc2bm.ui.views.IBuildView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BuildViewActivity extends AppCompatActivity implements IBuildView {
	@SuppressWarnings("FieldCanBeLocal")
	private BuildViewPresenter mPresenter;

	private TextView mTxtName;
	private TextView mTxtVersion;
	private TextView mTxtCreated;
	private TextView mTxtVisited;
	private TextView mLengthLabel;
	private TextView mLengthValue;
	private TextView mTxtMutchup;
	private TextView mTxtDescription;

	private ImageView mImgFaction;
	private ImageView mImgFactionSeparator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_build_view);

		Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
		setSupportActionBar(toolbar);

		//noinspection ConstantConditions
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		_initControls();

		mPresenter = new BuildViewPresenter(this, getIntent().getStringExtra(AppConstants.BUILD_ORDER_NAME_INTENT_KEY));
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();

		_initControls();

		mPresenter = new BuildViewPresenter(this, getIntent().getStringExtra(AppConstants.BUILD_ORDER_NAME_INTENT_KEY));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
			case (1) : {
				if (resultCode == Activity.RESULT_OK) {
					String newBuildName = data.getStringExtra("BuildSaved");
					if (newBuildName != null && !newBuildName.equals("")) {
						mPresenter.updateBuildName(newBuildName);
					}
				}

				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_buildview, menu);

		MenuItem itemDelete = menu.findItem(R.id.action_build_delete);
		if (itemDelete != null) {
			itemDelete.setVisible(!mPresenter.isBuildDefault());
		}

		MenuItem itemShare = menu.findItem(R.id.action_build_share);
		if (itemShare != null) {
			itemShare.setVisible(!mPresenter.isBuildDefault());
		}

		MenuItem itemEdit = menu.findItem(R.id.action_build_editinfo);
		if (itemEdit != null) {
			itemEdit.setVisible(!mPresenter.isBuildDefault());
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.searchable.
		int id = item.getItemId();

		if (id == R.id.action_build_delete) {
			_deleteBuild();
			return true;
		}

		if (id == R.id.action_build_play) {
			NavigationManager.startBuildPlayerActivity(this, mPresenter.getBuildName());
			return true;
		}

		if (id == R.id.action_build_editinfo) {
			NavigationManager.startBuildEditActivity(this, mPresenter.getBuildName());
			return true;
		}

		if (id == R.id.action_build_share) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

			String username = prefs.getString(getString(R.string.pref_username), "");
			String password = prefs.getString(getString(R.string.pref_password), "");

			if (username.equals("") || password.equals("")) {
				Toast.makeText(BuildViewActivity.this, "SC2BM account credentials are not provided. Please update Settings.", Toast.LENGTH_LONG).show();
				return true;
			} else {
				mPresenter.uploadBuildOrder(username, password);
			}

			return true;
		}

		if (id == R.id.action_build_craft) {
			NavigationManager.startBuildMakerActivity(this, mPresenter.getBuildName());
			return true;
		}

		if (id == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void _deleteBuild() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Delete build order?")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						mPresenter.deleteCurrentBuild();

						Toast.makeText(BuildViewActivity.this, "Build order deleted", Toast.LENGTH_SHORT).show();

						BuildViewActivity.this.finish();
						//NavUtils.navigateUpFromSameTask(BuildViewActivity.this);
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

	private void _initControls() {
		mTxtName = (TextView) this.findViewById(R.id.bdview_name);
		mTxtVersion = (TextView) this.findViewById(R.id.bdview_version);
		mTxtCreated = (TextView) this.findViewById(R.id.txtCreatedValue);
		mTxtVisited = (TextView) this.findViewById(R.id.txtVisitedValue);
		mLengthLabel = (TextView) this.findViewById(R.id.txtBuildLengthLabel);
		mLengthValue = (TextView) this.findViewById(R.id.txtBuildLengthValue);
		mTxtMutchup = (TextView) this.findViewById(R.id.bdview_mutchup);
		mTxtDescription = (TextView) this.findViewById(R.id.bdview_description);

		mImgFaction = (ImageView) this.findViewById(R.id.imgFaction);
		mImgFactionSeparator = (ImageView) this.findViewById(R.id.imgFactionSeparator);
	}

	@Override
	public void setBuildName(String name) {
		mTxtName.setText(name);
	}

	@Override
	public void setVersion(String version) {
		mTxtVersion.setText(version);
	}

	@Override
	public void setCreated(long createdDate) {
		mTxtCreated.setText(_getDateString(createdDate));
	}

	@Override
	public void setVisited(long visitedDate) {
		mTxtVisited.setText(_getDateString(visitedDate));
	}

	@Override
	public void setLength(String lengthString) {
		if (lengthString.length() > 0) {
			mLengthValue.setText(lengthString);
			mLengthLabel.setVisibility(TextView.VISIBLE);
		} else {
			mLengthValue.setText("");
			mLengthLabel.setVisibility(TextView.INVISIBLE);
		}
	}

	@Override
	public void setFaction(RaceEnum mainFaction) {
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

	@Override
	public void setMatchup(RaceEnum mainFaction, RaceEnum vsFaction) {
		mTxtMutchup.setText("Matchup: " + UiDataViewHelper.getFactionLetter(mainFaction) + "v" + UiDataViewHelper.getFactionLetter(vsFaction));
	}

	@Override
	public void setDescription(String description) {
		mTxtDescription.setText(description);
	}

	private String _getDateString(long date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.US);
		return simpleDateFormat.format(new Date(date));
	}

	@Override
	public Context getContext() {
		return this;
	}
}