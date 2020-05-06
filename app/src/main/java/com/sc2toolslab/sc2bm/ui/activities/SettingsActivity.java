package com.sc2toolslab.sc2bm.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.ui.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
		setSupportActionBar(toolbar);

		//noinspection ConstantConditions
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getFragmentManager().beginTransaction().replace(R.id.settingsLayout, new SettingsFragment()).commit();
	}

	@Override
	public void onBackPressed() {
		_navigateBack();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.searchable.
		int id = item.getItemId();

		if (id == android.R.id.home) {
			_navigateBack();

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void _navigateBack() {
		finish();
	}
 }