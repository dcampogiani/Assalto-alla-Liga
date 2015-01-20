package com.danielecampogiani.assaltoallaliga.support;

import android.content.ContentValues;
import android.database.Cursor;

import com.danielecampogiani.assaltoallaliga.model.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielecampogiani on 18/01/15.
 */
public class DBHelper {

    public static List<Team> getTeamsFromContentValues(List<ContentValues> values) {

        if (values == null)
            throw new NullPointerException("values can't be null");

        List<Team> result = new ArrayList<>(values.size());

        for (ContentValues current : values) {
            Team currentTeam = new Team();
            currentTeam.setName(current.getAsString(Team.TeamContract.NAME));
            currentTeam.setLogoPath(current.getAsString(Team.TeamContract.LOGO));
            result.add(currentTeam);
        }

        return result;
    }

    public static List<Team> getTeamsFromCursor(Cursor cursor) {

        if (cursor == null)
            throw new NullPointerException("cursor can't be null");

        List<Team> result = new ArrayList<>(cursor.getCount());

        while (cursor.moveToNext()) {
            Team team = new Team();
            team.setName(cursor.getString(cursor.getColumnIndex(Team.TeamContract.NAME)));
            team.setLogoPath(cursor.getString(cursor.getColumnIndex(Team.TeamContract.LOGO)));
            result.add(team);
        }

        return result;
    }

    public static List<ContentValues> getContentValuesFromTeams(List<Team> teams) {

        if (teams == null)
            throw new NullPointerException("teams can't be null");

        List<ContentValues> result = new ArrayList<>(teams.size());
        for (Team currentTeam : teams) {
            ContentValues current = new ContentValues();
            current.put(Team.TeamContract.NAME, currentTeam.getName());
            current.put(Team.TeamContract.LOGO, currentTeam.getLogoPath());
            result.add(current);
        }
        return result;
    }
}
