package com.danielecampogiani.assaltoallaliga.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.danielecampogiani.assaltoallaliga.R;
import com.danielecampogiani.assaltoallaliga.fragments.InfoFragment;
import com.danielecampogiani.assaltoallaliga.fragments.SelectFirstTeamFragment;
import com.danielecampogiani.assaltoallaliga.fragments.SelectSecondTeamFragment;
import com.danielecampogiani.assaltoallaliga.model.Team;
import com.nispok.snackbar.Snackbar;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends ActionBarActivity implements SelectFirstTeamFragment.HomeTeamSelectedListener, SelectSecondTeamFragment.AwayTeamSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String HOME_TEAM_NAME_KEY = "HOME_TEAM_NAME_KEY";
    private static final String AWAY_TEAM_NAME_KEY = "AWAY_TEAM_NAME_KEY";
    private static final String IS_DUAL_PANE_KEY = "IS_DUAL_PANE";
    private static final int ADD_TEAMS_REQUEST = 1;
    private String mHomeTeamName;
    private String mAwayTeamName;
    private boolean mIsDualPane;

    private void switchSingleFragment(Fragment newFragment) {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.slide_out_right).replace(R.id.container, newFragment).addToBackStack(null).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        getSupportLoaderManager().initLoader(0, null, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.icons));
        toolbar.setBackgroundColor(getResources().getColor(R.color.primary));

        View leftContainer = findViewById(R.id.left_container);

        mIsDualPane = leftContainer != null && leftContainer.getVisibility() == View.VISIBLE;

        if (savedInstanceState == null) {

            if (mIsDualPane) {
                getSupportFragmentManager().beginTransaction().replace(R.id.left_container, SelectFirstTeamFragment.newInstance()).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.right_container, InfoFragment.newInstance(getString(R.string.choose_home_team))).commit();

            } else
                getSupportFragmentManager().beginTransaction().replace(R.id.container, SelectFirstTeamFragment.newInstance()).commit();


        } else {

            Boolean oldValue = savedInstanceState.getBoolean(IS_DUAL_PANE_KEY);
            mHomeTeamName = savedInstanceState.getString(HOME_TEAM_NAME_KEY, "");
            mAwayTeamName = savedInstanceState.getString(AWAY_TEAM_NAME_KEY, "");

            if (mIsDualPane != oldValue) {//rotated

                if (mIsDualPane) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.left_container, SelectFirstTeamFragment.newInstance(mHomeTeamName)).commit();

                    if (mAwayTeamName.equalsIgnoreCase(""))
                        getSupportFragmentManager().beginTransaction().replace(R.id.right_container, InfoFragment.newInstance(getString(R.string.choose_home_team))).commit();

                    else
                        getSupportFragmentManager().beginTransaction().replace(R.id.right_container, SelectSecondTeamFragment.newInstance(mHomeTeamName, mAwayTeamName)).commit();

                } else {

                    if (mHomeTeamName.equalsIgnoreCase(""))
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, SelectFirstTeamFragment.newInstance()).commit();
                    else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, SelectFirstTeamFragment.newInstance(mHomeTeamName)).commit();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, SelectSecondTeamFragment.newInstance(mHomeTeamName, mAwayTeamName)).addToBackStack(null).commit();


                    }
                }

            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (mHomeTeamName != null && !mHomeTeamName.equals(""))
            outState.putString(HOME_TEAM_NAME_KEY, mHomeTeamName);
        if (mAwayTeamName != null && !mAwayTeamName.equals(""))
            outState.putString(AWAY_TEAM_NAME_KEY, mAwayTeamName);
        outState.putBoolean(IS_DUAL_PANE_KEY, mIsDualPane);

        super.onSaveInstanceState(outState);
    }


    private void goToResultActivity() {
        Intent resultIntent = new Intent(this, ResultActivity.class);
        resultIntent.putExtra(ResultActivity.HOME_TEAM_NAME, mHomeTeamName);
        resultIntent.putExtra(ResultActivity.AWAY_TEAM_NAME, mAwayTeamName);
        startActivity(resultIntent);
    }


    @Override
    public void onHomeTeamSelected(String teamName) {
        mHomeTeamName = teamName;
        SelectSecondTeamFragment selectSecondTeamFragment = SelectSecondTeamFragment.newInstance(mHomeTeamName);
        if (mIsDualPane)
            getSupportFragmentManager().beginTransaction().replace(R.id.right_container, selectSecondTeamFragment).commit();
        else
            switchSingleFragment(selectSecondTeamFragment);


    }

    @Override
    public void onAwayTeamSelected(String teamName) {
        mAwayTeamName = teamName;
        goToResultActivity();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{Team.TeamContract._ID, Team.TeamContract.NAME, Team.TeamContract.LOGO};
        return new CursorLoader(this, Team.TeamContract.CONTENT_URI, projection, null, null, Team.TeamContract._ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            goToSelectLega();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_TEAMS_REQUEST) {
            if (resultCode == RESULT_OK) {
                String legaName = data.getStringExtra(SelectLegaActivity.RESULT_KEY);
                if (legaName != null && !legaName.equals(""))
                    Snackbar.with(this).text(getString(R.string.using_lega, legaName)).show(this);
            } else {
                goToSelectLega();
            }
        }
    }

    private void goToSelectLega() {
        Toast.makeText(this, getString(R.string.no_teams_in_content_provider), Toast.LENGTH_SHORT).show();
        Intent addTeams = new Intent(this, SelectLegaActivity.class);
        startActivityForResult(addTeams, ADD_TEAMS_REQUEST);
    }
}
