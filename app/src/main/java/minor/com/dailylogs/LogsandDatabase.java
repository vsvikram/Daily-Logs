package minor.com.dailylogs;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LogsandDatabase {

    public static final String id = "_id";
    public static final String logs = "logs";
    public static final String title = "title";
    public static final String label = "label";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String DATABASE_NAME = "logs_database";
    private static final String DATABASE_TABLE = "logs_table";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " (" + id + " integer primary key autoincrement, "
                    + logs + " text not null, " + title + " text not null, " + label + " text not null);";

    private final Context mCtx;

    public LogsandDatabase(Context ctx) {
        mCtx = ctx;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public void open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    public boolean createLogs(String log, String titles, String labels) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(logs, log);
        initialValues.put(title, titles);
        initialValues.put(label, labels);
        return mDb.insert(DATABASE_TABLE, null, initialValues) > 0;
    }

    /*public Cursor fetchLog(long row_id) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[]{id,
                                logs}, id + "=" + row_id, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }*/

    public boolean deleteLog(long row_id, String labels) {
        return mDb.delete(DATABASE_TABLE, id + " = ? and " + label + " = ?", new String[]{String.valueOf(row_id), String.valueOf(labels)}) > 0;
    }

    public Cursor fetchAllLogs(String labels) {

        return mDb.query(DATABASE_TABLE, new String[]{id,
                        logs, title}, label + "=?",
                new String[]{String.valueOf(labels)}, null, null, null, null);
    }

    public boolean updateLogs(long row_id, String log, String titles, String labels) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(logs, log);
        updatedValues.put(title, titles);
        return mDb.update(DATABASE_TABLE, updatedValues, id + " = ? and " + label + " = ?", new String[]{String.valueOf(row_id), String.valueOf(labels)}) > 0;
    }

    public boolean deleteAll(String labels) {
        return mDb.delete(DATABASE_TABLE, label + " = ?", new String[]{String.valueOf(labels)}) > 0;
    }


}
