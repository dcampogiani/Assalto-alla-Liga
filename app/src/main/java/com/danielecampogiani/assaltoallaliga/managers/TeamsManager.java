package com.danielecampogiani.assaltoallaliga.managers;

import com.danielecampogiani.assaltoallaliga.model.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielecampogiani on 14/01/15.
 */
public class TeamsManager {

    private static TeamsManager mInstance;

    private final List<Team> teams;

    private TeamsManager() {

        teams = new ArrayList<>(8);

        Team jaguares = new Team();
        Team vanilla = new Team();
        Team sons = new Team();
        Team chiavo = new Team();
        Team san = new Team();
        Team incrocio = new Team();
        Team mai = new Team();
        Team libertas = new Team();

        jaguares.setName("Jaguares");
        jaguares.setLogoName("jaguares");
        vanilla.setName("Vanilla Thunder");
        sons.setName("Sons Of Gascoigne");
        chiavo.setName("Chiavo Verona");
        chiavo.setLogoName("chiavo");
        san.setName("San Antonio Spurs");
        incrocio.setName("Incrocio Sbagliato");
        incrocio.setLogoName("incrocio");
        mai.setName("Mai Una Gioia");
        libertas.setName("Libertas");
        libertas.setLogoName("libertas");

        teams.add(jaguares);
        teams.add(vanilla);
        teams.add(sons);
        teams.add(chiavo);
        teams.add(san);
        teams.add(incrocio);
        teams.add(mai);
        teams.add(libertas);
    }

    public static synchronized TeamsManager getInstance() {
        if (mInstance == null)
            mInstance = new TeamsManager();
        return mInstance;
    }

    public List<Team> getTeams() {
        return new ArrayList<>(teams);
    }
}
