package cfreyvermont.acadia_mapping_v2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Christian on 11/3/2015. This is a helper for the database that stores
 * the building information so it is easier to retrieve information on the fly.
 */
public final class DatabaseHelper extends SQLiteOpenHelper{
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static final String TABLE_NAME = "building_information";
    public static final String COLUMN_NAME_BUILDING_ID = "building_id";
    public static final String COLUMN_NAME_BUILDING_NAME = "building_name";
    public static final String COLUMN_NAME_BUILDING_CODE = "building_code";
    public static final String COLUMN_NAME_BUILDING_HOURS = "building_hours";
    public static final String COLUMN_NAME_BUILDING_PHONE = "building_phone";
    public static final String COLUMN_NAME_BUILDING_WEBSITE = "building_website";
    public static final String COLUMN_NAME_BUILDING_NOTES = "building_notes";

    public static final String COMMA_SEP = ",";
    public static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INTEGER";

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            COLUMN_NAME_BUILDING_ID + INT_TYPE + " PRIMARY_KEY" + COMMA_SEP +
            COLUMN_NAME_BUILDING_NAME + TEXT_TYPE + COMMA_SEP +
            COLUMN_NAME_BUILDING_CODE + TEXT_TYPE + COMMA_SEP +
            COLUMN_NAME_BUILDING_HOURS + TEXT_TYPE + COMMA_SEP +
            COLUMN_NAME_BUILDING_PHONE + TEXT_TYPE + COMMA_SEP +
            COLUMN_NAME_BUILDING_WEBSITE + TEXT_TYPE + COMMA_SEP +
            COLUMN_NAME_BUILDING_NOTES + TEXT_TYPE +
            ");";

    public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;


    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "building_information.db";

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_TABLE);
            Log.i(DatabaseHelper.class.getName(), "Database Created using:\n" + SQL_CREATE_TABLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(DatabaseHelper.class.getName(), "Upgrading from V" +
            oldVersion + " to V" + newVersion + " , data will be destroyed");
            db.execSQL(SQL_DROP_TABLE);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
}

