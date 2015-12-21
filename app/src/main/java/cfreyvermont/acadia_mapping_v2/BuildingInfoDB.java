package cfreyvermont.acadia_mapping_v2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Christian on 11/3/2015. It will provide functions that
 * activities can use in order to access the database in a standardized manner.
 */
public class BuildingInfoDB {
    private SQLiteDatabase db;

    public BuildingInfoDB(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * Creates a new record in the database, given an array of values.
     *
     * @param array the values to put into the DB
     * @return the result of the INSERT (-1 on error)
     */
    public long createRecord(String[] array) throws Exception {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME_BUILDING_ID, array[0]);
        values.put(DatabaseHelper.COLUMN_NAME_BUILDING_NAME, array[1]);
        values.put(DatabaseHelper.COLUMN_NAME_BUILDING_CODE, array[2]);
        values.put(DatabaseHelper.COLUMN_NAME_BUILDING_PURPOSE, array[3]);
        values.put(DatabaseHelper.COLUMN_NAME_BUILDING_HOURS, array[4]);
        values.put(DatabaseHelper.COLUMN_NAME_BUILDING_PHONE, array[5]);
        values.put(DatabaseHelper.COLUMN_NAME_BUILDING_WEBSITE, array[6]);
        values.put(DatabaseHelper.COLUMN_NAME_BUILDING_NOTES, array[7]);
        return db.insertOrThrow(DatabaseHelper.TABLE_NAME, null, values);
    }

    /**
     * Checks to see if the table contains data, as we don't want to add it twice.
     *
     * @return the # of rows in the DB.
     */
    public int getSize() {
        int size = 0;
        /* Get everything to see to determine the # of rows in the DB */
        Cursor dbCursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_NAME, null);
        if ((dbCursor != null) && (dbCursor.moveToFirst())) {
            size = dbCursor.getInt(0);
            dbCursor.close();
        }
        return size;
    }

    /**
     * Returns a record based on the building ID
     *
     * @param id the id of the building in the DB
     * @return the tuple containing the buildings information.
     */
    public ContentValues selectRecordById(int id) {
        String selection = DatabaseHelper.COLUMN_NAME_BUILDING_ID + " = " +
                Integer.toString(id);

        /* This function uses the following arguments
         * table: The table to run the query on
         * columns: the columns we want returned (all of them)
         * selection: The search question, which is everything after the
         *            WHERE in a SQL statement.
         * The rest in order: selectionArgs, Group by Rows?,
         * filter by row group?, sorting order
         */
        Cursor dbCursor = db.query(DatabaseHelper.TABLE_NAME, null, selection,
                null, null, null, null);

        if(dbCursor.moveToFirst()) {
            ContentValues cv = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(dbCursor, cv);
            dbCursor.close();
            return cv;
        }
        return null;
    }

    /**
     * Returns a record based on the building code given.
     *
     * @param code the building code to look for.
     * @return the tuple that matches the given building code.
     */
    public ContentValues selectRecordByCode(String code) {
        String whereClause = DatabaseHelper.COLUMN_NAME_BUILDING_CODE + " =?";

        /* See selectRecordById for more information */
        Cursor dbCursor = db.query(DatabaseHelper.TABLE_NAME, null, whereClause,
                new String[]{ code }, null, null, null);

        if((dbCursor != null) && (dbCursor.moveToFirst())) {
                ContentValues cv = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(dbCursor, cv);
            dbCursor.close();
                return cv;
        }
        Log.i("Query Result:", "Empty Set");
        return null;
    }
}
