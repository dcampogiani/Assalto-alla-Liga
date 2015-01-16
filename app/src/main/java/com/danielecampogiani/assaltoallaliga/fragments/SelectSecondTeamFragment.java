package com.danielecampogiani.assaltoallaliga.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danielecampogiani.assaltoallaliga.R;
import com.danielecampogiani.assaltoallaliga.adapters.TeamListItemAdapter;
import com.danielecampogiani.assaltoallaliga.managers.TeamsManager;
import com.danielecampogiani.assaltoallaliga.model.Team;
import com.danielecampogiani.assaltoallaliga.support.RecyclerItemClickListener;

import java.util.List;

/**
 * Created by danielecampogiani on 15/01/15.
 */
public class SelectSecondTeamFragment extends Fragment {

    private static final String SELECTED_INDEX_KEY = "SELECTED_INDEX_KEY";
    private static final String HIDDEN_TEAM_NAME_KEY = "HIDDEN_TEAM_NAME_KEY";
    private static final String SELECTED_TEAM_NAME_KEY = "SELECTED_TEAM_NAME_KEY";
    private List<Team> mTeams;
    private ActionBarActivity mActionBarActivity;
    private Context mContext;
    private AwayTeamSelectedListener mAwayTeamSelectedListener;
    private TeamListItemAdapter mTeamListItemAdapter;
    private int mSelectedIndex;

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
        mTeams = TeamsManager.getInstance().getTeams();

        if (getArguments() != null) {
            String teamToHide = getArguments().getString(HIDDEN_TEAM_NAME_KEY, "");
            for (Team currentTeam : mTeams) {
                if (currentTeam.getName().equalsIgnoreCase(teamToHide)) {
                    mTeams.remove(currentTeam);
                    break;
                }
            }

            String selectedTeamName = getArguments().getString(SELECTED_TEAM_NAME_KEY, "");
            if (!selectedTeamName.equals("")) {
                for (Team currentTeam : mTeams) {
                    if (currentTeam.getName().equalsIgnoreCase(selectedTeamName)) {
                        mSelectedIndex = mTeams.indexOf(currentTeam);
                        break;
                    }
                }
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_teams_list, container, false);
        mTeamListItemAdapter = new TeamListItemAdapter(mTeams.toArray(new Team[mTeams.size()]), mContext);

        RecyclerView mRecyclerView = (RecyclerView) root.findViewById(R.id.list_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(mTeamListItemAdapter);

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

        return root;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mSelectedIndex >= 0)
            outState.putInt(SELECTED_INDEX_KEY, mSelectedIndex);
        super.onSaveInstanceState(outState);
    }

    public interface AwayTeamSelectedListener {
        public void onAwayTeamSelected(String teamName);
    }
}
