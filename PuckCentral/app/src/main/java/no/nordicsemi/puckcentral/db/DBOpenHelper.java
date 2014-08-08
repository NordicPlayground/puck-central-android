package no.nordicsemi.puckcentral.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.droidparts.persist.sql.AbstractDBOpenHelper;

import no.nordicsemi.puckcentral.models.Action;
import no.nordicsemi.puckcentral.models.Puck;
import no.nordicsemi.puckcentral.models.Rule;

public class DBOpenHelper extends AbstractDBOpenHelper {

    private static final String DB_FILE = "PuckCentral.sql";
    private static final int DB_VERSION = 12;

    public DBOpenHelper(Context ctx) {
        super(ctx, DB_FILE, DB_VERSION);
    }

    @Override
    protected void onCreateTables(SQLiteDatabase sqLiteDatabase) {
        createTables(sqLiteDatabase, Puck.class);
        createTables(sqLiteDatabase, Action.class);
        createTables(sqLiteDatabase, Rule.class);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* TODO: add migration */
        dropTables(db);
        onCreate(db);
    }
}
