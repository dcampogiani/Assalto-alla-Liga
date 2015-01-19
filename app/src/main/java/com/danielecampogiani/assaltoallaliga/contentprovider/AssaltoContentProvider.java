package com.danielecampogiani.assaltoallaliga.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.danielecampogiani.assaltoallaliga.model.Team;
import com.tjeannin.provigen.ProviGenOpenHelper;
import com.tjeannin.provigen.ProviGenProvider;

/**
 * Created by danielecampogiani on 18/01/15.
 */
public class AssaltoContentProvider extends ProviGenProvider {

    private static final String DB_NAME = "assalto";
    private static final int VERSION = 1;
    private static Class[] contracts = new Class[]{Team.TeamContract.class};

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
        return new ProviGenOpenHelper(getContext(), DB_NAME, null, VERSION, contracts);
    }

    @Override
    public Class[] contractClasses() {
        return contracts;
    }
}
