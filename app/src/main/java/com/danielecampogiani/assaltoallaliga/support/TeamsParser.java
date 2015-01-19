package com.danielecampogiani.assaltoallaliga.support;

import android.os.Handler;
import android.os.Looper;

import com.danielecampogiani.assaltoallaliga.model.Team;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielecampogiani on 19/01/15.
 */
public class TeamsParser {

    public static void parseTeams(final String html, final TeamsParserListener listener) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Team> teams = new ArrayList<>();

                Document document = Jsoup.parse(html);

                Elements teamBoxes = document.select("div.col-lg-12.col-md-12.col-sm-12.col-xs-12.whitebox.teambox");

                for (int i = 0; i < teamBoxes.size(); i++) {
                    Element imgeElement = teamBoxes.get(i).select("img.img-responsive.img-circle.logoteam.pull-left").get(0);
                    String imageUrl = imgeElement.absUrl("src");
                    Element nameElement = teamBoxes.get(i).select("h3").get(0);
                    String name = nameElement.text();
                    Team team = new Team();
                    team.setLogoPath(imageUrl);
                    team.setName(name);
                    teams.add(team);
                }

                if (teams != null) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onTeamsParsed(teams);
                        }
                    });
                }
            }
        };
        ThreadsManager.execute(runnable);
    }

    public interface TeamsParserListener {

        public void onTeamsParsed(List<Team> teams);
    }
}
