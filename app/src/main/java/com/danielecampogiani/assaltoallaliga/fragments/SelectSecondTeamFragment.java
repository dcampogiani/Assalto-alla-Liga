package com.danielecampogiani.assaltoallaliga.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danielecampogiani.assaltoallaliga.R;
import com.danielecampogiani.assaltoallaliga.adapters.TeamListItemAdapter;
import com.danielecampogiani.assaltoallaliga.model.Team;
import com.danielecampogiani.assaltoallaliga.support.DBHelper;
import com.danielecampogiani.assaltoallaliga.support.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielecampogiani on 15/01/15.
 */
public class SelectSecondTeamFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String SELECTED_INDEX_KEY = "SELECTED_INDEX_KEY";
    private static final String HIDDEN_TEAM_NAME_KEY = "HIDDEN_TEAM_NAME_KEY";
    private static final String SELECTED_TEAM_NAME_KEY = "SELECTED_TEAM_NAME_KEY";
    private List<Team> mTeams;
    private ActionBarActivity mActionBarActivity;
    private Context mContext;
    private AwayTeamSelectedListener mAwayTeamSelectedListener;
    private TeamListItemAdapter mTeamListItemAdapter;
    private int mSelectedIndex;
    private RecyclerView mRecyclerView;
    private String mTeamToHide;
    private String mSelectedTeamName;

    public static SelectSecondTeamFragment newInstance(String hiddenTeamName) {
        SelectSecondTeamFragment result = new SelectSecondTeamFragment();
        Bundle args = new Bundle();
        args.putString(SelectSecondTeamFragment.HIDDEN_TEAM_NAME_KEY, hiddenTeamName);
        result.setArguments(args);
        return result;
    }

    public static SelectSecondTeamFragment newInstance(String hiddenTeamName, String selectedTeamName) {
        SelectSecondTeamFragment result = new SelectSecondTeamFragment();
        Bundle args = new Bundle();
        args.putString(SelectSecondTeamFragment.HIDDEN_TEAM_NAME_KEY, hiddenTeamName);
        args.putString(SelectSecondTeamFragment.SELECTED_TEAM_NAME_KEY, selectedTeamName);
        result.setArguments(args);
        return result;
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof ActionBarActivity)
            mActionBarActivity = (ActionBarActivity) activity;
        if (activity instanceof AwayTeamSelectedListener)
            mAwayTeamSelectedListener = (AwayTeamSelectedListener) activity;

        mContext = activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelectedIndex = -1;

        if (getArguments() != null) {
            mTeamToHide = getArguments().getString(HIDDEN_TEAM_NAME_KEY, "");
            mSelectedTeamName = getArguments().getString(SELECTED_TEAM_NAME_KEY, "");
        } else {
            mTeamToHide = "";
            mSelectedTeamName = "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_teams_list, container, false);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.list_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (savedInstanceState != null)
            mSelectedIndex = savedInstanceState.getInt(SELECTED_INDEX_KEY, -1);
        if (mSelectedIndex >= 0)
            mTeamListItemAdapter.setSelectedItem(mSelectedIndex);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mSelectedIndex = position;
                mTeamListItemAdapter.setSelectedItem(position);
                mAwayTeamSelectedListener.onAwayTeamSelected(mTeams.get(position).getName());
            }
        }));

        mActionBarActivity.getSupportActionBar().setTitle(getString(R.string.choose_away_team));

        getLoaderManager().initLoader(0, null, this);

        return root;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mSelectedIndex >= 0)
            outState.putInt(SELECTED_INDEX_KEY, mSelectedIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{Team.TeamContract._ID, Team.TeamContract.NAME, Team.TeamContract.LOGO};
        return new CursorLoader(mContext, Team.TeamContract.CONTENT_URI, projection, null, null, Team.TeamContract._ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            data.moveToPosition(-1);
            mTeams = DBHelper.getTeamsFromCursor(data);
            for (Team currentTeam : mTeams) {
                if (currentTeam.getName().equalsIgnoreCase(mTeamToHide)) {
                    mTeams.remove(currentTeam);
                    break;
                }
            }
            if (!mSelectedTeamName.equals("")) {
                for (Team currentTeam : mTeams) {
                    if (currentTeam.getName().equalsIgnoreCase(mSelectedTeamName)) {
                        mSelectedIndex = mTeams.indexOf(currentTeam);
                        break;
                    }
                }
            }
            mTeamListItemAdapter = new TeamListItemAdapter(mTeams.toArray(new Team[mTeams.size()]), mContext);
            mRecyclerView.setAdapter(mTeamListItemAdapter);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTeams = new ArrayList<>();
        mTeamListItemAdapter = new TeamListItemAdapter(mTeams.toArray(new Team[mTeams.size()]), mContext);
        mRecyclerView.setAdapter(mTeamListItemAdapter);
    }

    public interface AwayTeamSelectedListener {
        public void onAwayTeamSelected(String teamName);
    }
}
