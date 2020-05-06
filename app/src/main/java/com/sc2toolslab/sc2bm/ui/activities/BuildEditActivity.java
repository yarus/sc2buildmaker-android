package com.sc2toolslab.sc2bm.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.ui.presenters.BuildEditPresenter;
import com.sc2toolslab.sc2bm.ui.views.IBuildEditView;

public class BuildEditActivity extends AppCompatActivity implements IBuildEditView {
	private BuildEditPresenter mPresenter;

	private EditText txtBuildName;
	private EditText txtDescription;
	private Spinner lstVsFaction;

	private String mDescriptionState;
	private String mBuildNameState;
	private String mVsFactionState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_build_edit);

		Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
		setSupportActionBar(toolbar);

		//noinspection ConstantConditions
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		_initControls();

		mPresenter = new BuildEditPresenter(this, getIntent().getStringExtra(AppConstants.BUILD_ORDER_NAME_INTENT_KEY));

		if (savedInstanceState != null) {
			mDescriptionState = savedInstanceState.getString("Description");
			mBuildNameState = savedInstanceState.getString("BuildName");
			mVsFactionState = savedInstanceState.getString("VsFaction");//RaceEnum.valueOf(savedInstanceState.getString("VsFaction"));
		} else {
			mDescriptionState = "";
			mBuildNameState = "";
			mVsFactionState = "";
		}
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();

		_initControls();

		if (mDescriptionState.equals("")) {
			mDescriptionState = getDescription();
		}

		if (mBuildNameState.equals("")) {
			mBuildNameState = getBuildName();
		}

		if (mVsFactionState.equals("")) {
			mVsFactionState = getVsFaction().toString();
		}

		mPresenter.bindData();

		if (!mDescriptionState.equals("")) {
			setDescription(mDescriptionState);
			mDescriptionState = "";
		}

		if (!mBuildNameState.equals("")) {
			setBuildName(mBuildNameState);
			mBuildNameState = "";
		}

		if (!mVsFactionState.equals("")) {
			setVsFaction(RaceEnum.valueOf(mVsFactionState));
			mVsFactionState = "";
		}
	}

	@Override
	public void onBackPressed() {
		_navigateBack();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("Description", getDescription());
		outState.putString("BuildName", getBuildName());
		outState.putString("VsFaction", getVsFaction().toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_buildedit, menu);

		MenuItem itemSave = menu.findItem(R.id.action_build_save);
		if (itemSave != null) {
			itemSave.setVisible(mPresenter.isSaveAvailable());
		}

		MenuItem itemCopy = menu.findItem(R.id.action_build_copy);
		if (itemCopy != null) {
			itemCopy.setVisible(!getBuildName().equals(mPresenter.getInitialBuildName()) && !mPresenter.getInitialBuildName().equals(""));
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.searchable.
		int id = item.getItemId();

		if (id == R.id.action_build_save) {
			_saveAndExit(false);

			return true;
		}

		if (id == R.id.action_build_copy) {
			_saveAndExit(true);

			return true;
		}

		if (id == android.R.id.home) {
			_navigateBack();

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void _saveAndExit(final boolean toCopy) {
		String buildName = getBuildName();

		if (!mPresenter.isInfoChanged()) {
			_navigateBack();
			return;
		}

		if (buildName.equals("")) {
			Toast.makeText(BuildEditActivity.this, "Build name is required!", Toast.LENGTH_SHORT).show();
			return;
		}

		if(mPresenter.hasOtherBuildWithSameName()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Build with this name already exists, rewrite?")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							mPresenter.saveBuildOrder(toCopy);

							Intent resultIntent = new Intent();
							resultIntent.putExtra("BuildSaved", getBuildName());
							setResult(Activity.RESULT_OK, resultIntent);

							Toast.makeText(BuildEditActivity.this, "Build order saved", Toast.LENGTH_SHORT).show();
							_navigateBack();
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
			mPresenter.saveBuildOrder(toCopy);
			Intent resultIntent = new Intent();
			resultIntent.putExtra("BuildSaved", getBuildName());
			setResult(Activity.RESULT_OK, resultIntent);

			Toast.makeText(BuildEditActivity.this, "Build order saved", Toast.LENGTH_SHORT).show();
			_navigateBack();
		}
	}

	private void _navigateBack() {
		if (mPresenter.isInfoChanged()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Changes not saved. Are you sure you want to leave?")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							mPresenter.bindData();
							finish();
							//NavigationManager.startBuildViewActivity(BuildEditActivity.this, getBuildName());
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
			finish();
			//NavigationManager.startBuildViewActivity(this, getBuildName());
		}
	}

	private void _initControls() {
		txtBuildName = (EditText) this.findViewById(R.id.txtBuildName);
		txtBuildName.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				invalidateOptionsMenu();
			}
		});

		txtDescription = (EditText) this.findViewById(R.id.txtDescription);
		lstVsFaction = (Spinner) this.findViewById(R.id.lstVsFaction);
	}

	@Override
	public void setBuildName(String name) {
		txtBuildName.setText(name);
	}

	@Override
	public void setDescription(String description) {
		txtDescription.setText(description);
	}

	@Override
	public void setVsFaction(RaceEnum vsFaction) {
		lstVsFaction.setSelection(_getFactionPositionInList(vsFaction));
	}

	@Override
	public String getBuildName() {
		return txtBuildName.getText().toString();
	}

	@Override
	public String getDescription() {
		return txtDescription.getText().toString();
	}

	@Override
	public RaceEnum getVsFaction() {
		return RaceEnum.valueOf(lstVsFaction.getSelectedItem().toString());
	}

	private int _getFactionPositionInList(RaceEnum faction) {
		if (faction == RaceEnum.Zerg) return 0;
		if (faction == RaceEnum.Terran) return 1;
		if (faction == RaceEnum.Protoss) return 2;
		return 0;
	}

	@Override
	public Context getContext() {
		return BuildEditActivity.this;
	}
}
