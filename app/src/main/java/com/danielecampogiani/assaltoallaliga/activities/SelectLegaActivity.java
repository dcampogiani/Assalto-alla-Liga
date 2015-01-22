package com.danielecampogiani.assaltoallaliga.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.danielecampogiani.assaltoallaliga.R;
import com.danielecampogiani.assaltoallaliga.adapters.TeamListItemAdapter;
import com.danielecampogiani.assaltoallaliga.model.Team;
import com.danielecampogiani.assaltoallaliga.support.DBHelper;
import com.danielecampogiani.assaltoallaliga.support.TeamsParser;
import com.danielecampogiani.assaltoallaliga.support.ViewUtils;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Collections;
import java.util.List;

public class SelectLegaActivity extends ActionBarActivity {

    public static final String RESULT_KEY = "RESULT_KEY";
    private static final String CURRENT_EDIT_TEXT_VALUE = "CURRENT_EDIT_TEXT_VALUE";
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private ProgressBar mProgressbar;
    private MaterialEditText mMaterialEditText;
    private FloatingActionButton fab;
    private List<Team> mTeams;
    private TeamListItemAdapter mTeamListItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lega);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
        mErrorTextView = (TextView) findViewById(R.id.errorTextView);
        mProgressbar = (ProgressBar) findViewById(R.id.progressBar);
        mMaterialEditText = (MaterialEditText) findViewById(R.id.materialEditText);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.icons));
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (savedInstanceState != null) {
            String currentText = savedInstanceState.getString(CURRENT_EDIT_TEXT_VALUE, "");
            if (!currentText.equals(""))
                mMaterialEditText.setText(currentText);
        }

        mMaterialEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mMaterialEditText.getWindowToken(), 0);

                    doStuff();

                    return true;
                }

                return false;
            }
        });

        mMaterialEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.toggleFab(fab, false);
                ViewUtils.toggleAlpha(mProgressbar, true);
                List<ContentValues> contentValues = DBHelper.getContentValuesFromTeams(mTeams);
                ContentValues[] valuesArray = contentValues.toArray(new ContentValues[contentValues.size()]);
                getContentResolver().bulkInsert(Team.TeamContract.CONTENT_URI, valuesArray);
                ViewUtils.toggleAlpha(mProgressbar, false);
                Intent resultIntent = new Intent();
                resultIntent.putExtra(RESULT_KEY, mMaterialEditText.getText().toString());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        doStuff();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMaterialEditText.getText() != null && !mMaterialEditText.getText().toString().equals("")) {
            outState.putString(CURRENT_EDIT_TEXT_VALUE, mMaterialEditText.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void doStuff() {


        if (mMaterialEditText.getText() != null && !mMaterialEditText.getText().toString().equals("")) {

            ViewUtils.toggleAlpha(mProgressbar, true);


            String leagueNameFormatted = mMaterialEditText.getText().toString().toLowerCase().replace(" ", "-");
            if (leagueNameFormatted.charAt(leagueNameFormatted.length() - 1) == '-')
                leagueNameFormatted = leagueNameFormatted.substring(0, leagueNameFormatted.length() - 1);
            final String url = getString(R.string.lega_url, leagueNameFormatted);

            Ion.with(this).load(url).asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String result) {
                    if (e != null) {
                        ViewUtils.toggleError(mErrorTextView, getString(R.string.network_error), true);
                        ViewUtils.toggleAlpha(mProgressbar, false);

                    } else {
                        ViewUtils.toggleError(mErrorTextView, null, false);

                        TeamsParser.parseTeams(result, new TeamsParser.TeamsParserListener() {
                            @Override
                            public void onTeamsParsed(List<Team> teams) {

                                if (teams.size() == 0) {
                                    ViewUtils.toggleFab(fab,false);
                                    ViewUtils.toggleAlpha(mRecyclerView,false);
                                    ViewUtils.toggleError(mErrorTextView, getString(R.string.wrong_lega_name), true);
                                    ViewUtils.toggleAlpha(mProgressbar, false);

                                } else {
                                    ViewUtils.toggleAlpha(mRecyclerView,true);
                                    mTeams = teams;

                                    Collections.sort(mTeams);

                                    mTeamListItemAdapter = new TeamListItemAdapter(mTeams.toArray(new Team[mTeams.size()]), SelectLegaActivity.this);
                                    mRecyclerView.setAdapter(mTeamListItemAdapter);
                                    ViewUtils.toggleFab(fab, true);
                                    ViewUtils.toggleAlpha(mProgressbar, false);

                                }
                            }
                        });

                    }
                }
            });


        }
    }

}
