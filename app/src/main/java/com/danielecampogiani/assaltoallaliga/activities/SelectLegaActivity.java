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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.danielecampogiani.assaltoallaliga.R;
import com.danielecampogiani.assaltoallaliga.adapters.TeamListItemAdapter;
import com.danielecampogiani.assaltoallaliga.model.Team;
import com.danielecampogiani.assaltoallaliga.support.DBHelper;
import com.danielecampogiani.assaltoallaliga.support.TeamsParser;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.rengwuxian.materialedittext.MaterialEditText;

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
                showFAB(false);
                showLoading(true);
                List<ContentValues> contentValues = DBHelper.getContentValuesFromTeams(mTeams);
                ContentValues[] valuesArray = contentValues.toArray(new ContentValues[contentValues.size()]);
                getContentResolver().bulkInsert(Team.TeamContract.CONTENT_URI, valuesArray);
                showLoading(false);
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

            showLoading(true);

            String leagueNameFormatted = mMaterialEditText.getText().toString().toLowerCase().replace(" ", "-");
            if (leagueNameFormatted.charAt(leagueNameFormatted.length() - 1) == '-')
                leagueNameFormatted = leagueNameFormatted.substring(0, leagueNameFormatted.length() - 1);
            final String url = getString(R.string.lega_url, leagueNameFormatted);

            Ion.with(this).load(url).asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String result) {
                    if (e != null) {
                        showError(getString(R.string.network_error), true);
                        showLoading(false);
                    } else {
                        showError("", false);

                        TeamsParser.parseTeams(result, new TeamsParser.TeamsParserListener() {
                            @Override
                            public void onTeamsParsed(List<Team> teams) {

                                if (teams.size() == 0) {
                                    showError(getString(R.string.wrong_lega_name), true);
                                    showLoading(false);
                                } else {
                                    mTeams = teams;
                                    mTeamListItemAdapter = new TeamListItemAdapter(mTeams.toArray(new Team[mTeams.size()]), SelectLegaActivity.this);
                                    mRecyclerView.setAdapter(mTeamListItemAdapter);
                                    showFAB(true);
                                    showLoading(false);
                                }
                            }
                        });

                    }
                }
            });


        }
    }

    private void showFAB(boolean show) {
        if (show) {
            TranslateAnimation animation = new TranslateAnimation(0, 0, 400, 0);
            animation.setDuration(300);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setFillAfter(true);
            fab.setVisibility(View.VISIBLE);
            fab.startAnimation(animation);
        } else {
            if (fab.getVisibility() == View.VISIBLE) {
                TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 400);
                animation.setFillAfter(true);
                animation.setInterpolator(new AccelerateInterpolator());
                animation.setDuration(300);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fab.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                fab.startAnimation(animation);
            }
        }
    }

    private void showLoading(boolean value) {
        if (value) {
            Animation animation = new AlphaAnimation(0, 1);
            animation.setInterpolator(new AccelerateInterpolator());
            mProgressbar.setVisibility(View.VISIBLE);
            mProgressbar.startAnimation(animation);

        } else if (mProgressbar.getVisibility() == View.VISIBLE) {
            Animation animation = new AlphaAnimation(1, 0);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mProgressbar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            mProgressbar.startAnimation(animation);
        }
    }

    private void showError(String text, boolean show) {

        if (show) {
            Animation animation = new AlphaAnimation(0, 1);
            animation.setInterpolator(new AccelerateInterpolator());
            mErrorTextView.setText(text);
            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorTextView.startAnimation(animation);

        } else if (mErrorTextView.getVisibility() == View.VISIBLE) {
            Animation animation = new AlphaAnimation(1, 0);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mErrorTextView.setText("");
                    mErrorTextView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            mErrorTextView.startAnimation(animation);
        }


    }
}
