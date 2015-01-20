package com.danielecampogiani.assaltoallaliga.model;


import android.net.Uri;

import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.ContentUri;

/**
 * Created by danielecampogiani on 14/01/15.
 */

public class Team implements Comparable<Team> {


    private String name;
    private String logoPath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null)
            throw new NullPointerException("name can't be null");
        else if ("".equals(name))
            throw new IllegalArgumentException("name can't be empty");
        this.name = name;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        if (logoPath == null)
            throw new NullPointerException("logoPath can't be null");
        else if ("".equals(logoPath))
            throw new IllegalArgumentException("logoPath can't be empty");
        this.logoPath = logoPath;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Team team = (Team) o;

        if (!name.equals(team.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(Team another) {

        if (another == null)
            return 1;
        return name.compareTo(another.getName());

    }

    public interface TeamContract extends ProviGenBaseContract {

        @Column(Column.Type.TEXT)
        public static final String NAME = "name";

        @Column(Column.Type.TEXT)
        public static final String LOGO = "logo";

        @ContentUri
        public static final Uri CONTENT_URI = Uri.parse("content://com.danielecampogiani.assaltoallaliga/team");

    }
}
