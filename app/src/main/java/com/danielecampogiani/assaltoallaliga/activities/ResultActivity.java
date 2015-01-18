package com.danielecampogiani.assaltoallaliga.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.danielecampogiani.assaltoallaliga.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * Created by danielecampogiani on 15/01/15.
 */
public class ResultActivity extends ActionBarActivity {

    public static final String HOME_TEAM_NAME = "HOME_TEAM_NAME";
    public static final String AWAY_TEAM_NAME = "AWAY_TEAM_NAME";
    private static final String CURRENT_IMAGE_URL = "CURRENT_IMAGE_URL";

    private String mHomeTeamName;
    private String mAwayTeamName;
    private String mCurrentImageUrl;
    private ImageView imageView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Animation mImageAnimation;
    private FloatingActionButton fab;
    private Toolbar mToolbar;
    private View mToolbarBackground;
    private TextView mErrorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.icons));
        mToolbar.setSubtitleTextColor(getResources().getColor(R.color.primary_light));
        mToolbarBackground = findViewById(R.id.toolbar_background);
        mErrorTextView = (TextView) findViewById(R.id.errorTextView);

        mImageAnimation = new AlphaAnimation(0, 1);
        mImageAnimation.setInterpolator(new AccelerateInterpolator());

        imageView = (ImageView) findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.accent, R.color.primary);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFAB(false);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                StringBuilder builder = new StringBuilder(getString(R.string.will_team_win_vs, mHomeTeamName, mAwayTeamName));
                builder.append(" - ");
                builder.append(mCurrentImageUrl);
                sendIntent.putExtra(Intent.EXTRA_TEXT, builder.toString());
                startActivity(Intent.createChooser(sendIntent, getString(R.string.choose_sharing_app)));
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                doStuff(false);

            }
        });

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            mHomeTeamName = intent.getStringExtra(HOME_TEAM_NAME);
            mAwayTeamName = intent.getStringExtra(AWAY_TEAM_NAME);
            doStuff(true);
        } else {
            mHomeTeamName = savedInstanceState.getString(HOME_TEAM_NAME, "");
            mAwayTeamName = savedInstanceState.getString(AWAY_TEAM_NAME, "");
            mCurrentImageUrl = savedInstanceState.getString(CURRENT_IMAGE_URL, "");
            if (!mCurrentImageUrl.equals("")) {
                Ion.with(imageView).animateIn(mImageAnimation).centerCrop().load(mCurrentImageUrl).setCallback(new FutureCallback<ImageView>() {
                    @Override
                    public void onCompleted(Exception e, ImageView result) {
                        showLoading(false);
                        showFAB(true);
                        mToolbarBackground.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        mToolbar.setSubtitleTextColor(getResources().getColor(R.color.accent));
                    }
                });
            }

        }
        getSupportActionBar().setTitle(getString(R.string.vs, mHomeTeamName, mAwayTeamName));
        getSupportActionBar().setSubtitle(getString(R.string.will_team_win, mHomeTeamName));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (mHomeTeamName != null && !mHomeTeamName.equals(""))
            outState.putString(HOME_TEAM_NAME, mHomeTeamName);

        if (mAwayTeamName != null && !mAwayTeamName.equals(""))
            outState.putString(AWAY_TEAM_NAME, mAwayTeamName);

        if (mCurrentImageUrl != null && !mCurrentImageUrl.equals(""))
            outState.putString(CURRENT_IMAGE_URL, mCurrentImageUrl);

        super.onSaveInstanceState(outState);
    }

    private void doStuff(final boolean firstRun) {

        showFAB(false);

        Ion.with(this).load("http://yesno.wtf/api").asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (e != null) {
                    showError(getString(R.string.network_error), true);
                    if (firstRun)
                        showLoading(false);
                    else
                        swipeRefreshLayout.setRefreshing(false);
                } else {
                    showError("", false);
                    mCurrentImageUrl = result.get("image").getAsString();

                    Ion.with(imageView).animateIn(mImageAnimation).centerCrop().load(mCurrentImageUrl).setCallback(new FutureCallback<ImageView>() {
                        @Override
                        public void onCompleted(Exception e, ImageView result) {
                            if (e != null)
                                showError(getString(R.string.network_error), true);
                            else {
                                showError("", false);
                                if (firstRun)
                                    showLoading(false);
                                else
                                    swipeRefreshLayout.setRefreshing(false);
                                showFAB(true);

                                //mToolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                                mToolbarBackground.animate().translationY(-mToolbarBackground.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                                mToolbar.setSubtitleTextColor(getResources().getColor(R.color.accent));
                            }
                        }
                    });
                }
            }
        });

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

    private void showLoading(boolean value) {
        progressBar.animate().alpha(value ? 1 : 0);
    }
}
