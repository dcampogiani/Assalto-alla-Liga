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
 * Created by danielecampogiani on 14/01/15.
 */
public class SelectFirstTeamFragment extends Fragment {

    private static final String SELECTED_TEAM_NAME_KEY = "SELECTED_TEAM_NAME_KEY";
    private static final String SELECTED_INDEX_KEY = "SELECTED_INDEX_KEY";
    private List<Team> mTeams;
    private ActionBarActivity mActionBarActivity;
    private Context mContext;
    private TeamListItemAdapter mTeamListItemAdapter;
    private int mSelectedIndex;
    private HomeTeamSelectedListener mHomeTeamSelectedListener;

    public static SelectFirstTeamFragment newInstance() {
        return new SelectFirstTeamFragment();
    }

    public static SelectFirstTeamFragment newInstance(String selectedTeamName) {
        SelectFirstTeamFragment result = new SelectFirstTeamFragment();
        Bundle args = new Bundle();
        args.putString(SELECTED_TEAM_NAME_KEY, selectedTeamName);
        result.setArguments(args);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTeams = TeamsManager.getInstance().getTeams();
        mSelectedIndex = -1;
        if (getArguments() != null) {
            String teamName = getArguments().getString(SELECTED_TEAM_NAME_KEY, "");
            if (!teamName.equals("")) {
                for (Team currentTeam : mTeams) {
                    if (currentTeam.getName().equalsIgnoreCase(teamName)) {
                        mSelectedIndex = mTeams.indexOf(currentTeam);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof ActionBarActivity)
            mActionBarActivity = (ActionBarActivity) activity;

        if (activity instanceof HomeTeamSelectedListener)
            mHomeTeamSelectedListener = (HomeTeamSelectedListener) activity;

        mContext = mActionBarActivity;

        super.onAttach(activity);
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
                mHomeTeamSelectedListener.onHomeTeamSelected(mTeams.get(position).getName());
            }
        }));

        mActionBarActivity.getSupportActionBar().setTitle(getString(R.string.choose_home_team));

        return root;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mSelectedIndex >= 0)
            outState.putInt(SELECTED_INDEX_KEY, mSelectedIndex);
        super.onSaveInstanceState(outState);
    }

    public interface HomeTeamSelectedListener {
        public void onHomeTeamSelected(String teamName);
    }
}
