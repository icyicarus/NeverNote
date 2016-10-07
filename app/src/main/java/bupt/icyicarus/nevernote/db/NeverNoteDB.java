package bupt.icyicarus.nevernote.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NeverNoteDB extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    public static final String COLUMN_ID = "_id";
    public static final String TABLE_NAME_NOTES = "notes";
    public static final String TABLE_NAME_MEDIA = "media";
    public static final String TABLE_NAME_ALARM = "alarm";
    public static final String COLUMN_NAME_NOTE_NAME = "name";
    public static final String COLUMN_NAME_NOTE_CONTENT = "content";
    public static final String COLUMN_NAME_NOTE_DATE = "date";
    public static final String COLUMN_NAME_MEDIA_PATH = "path";
    public static final String COLUMN_NAME_MEDIA_OWNER_NOTE_ID = "owner";
    public static final String COLUMN_NAME_ALARM_YEAR = "year";
    public static final String COLUMN_NAME_ALARM_MONTH = "month";
    public static final String COLUMN_NAME_ALARM_DAY = "day";
    public static final String COLUMN_NAME_ALARM_HOUR = "hour";
    public static final String COLUMN_NAME_ALARM_MINUTE = "minute";
    public static final String COLUMN_NAME_ALARM_NOTEID = "noteid";
    public static final String COLUMN_NAME_ALARM_NAME = "name";
    public static final String COLUMN_NAME_ALARM_CONTENT = "content";

    public NeverNoteDB(Context context) {
        super(context, "notes", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME_NOTES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME_NOTE_NAME + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_NAME_NOTE_CONTENT + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_NAME_NOTE_DATE + " TEXT NOT NULL DEFAULT \"\""
                + ")");
        db.execSQL("CREATE TABLE " + TABLE_NAME_MEDIA + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME_MEDIA_PATH + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_NAME_MEDIA_OWNER_NOTE_ID + " INTEGER NOT NULL DEFAULT 0"
                + ")");
        db.execSQL("CREATE TABLE " + TABLE_NAME_ALARM + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME_ALARM_YEAR + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_NAME_ALARM_MONTH + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_NAME_ALARM_DAY + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_NAME_ALARM_HOUR + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_NAME_ALARM_MINUTE + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_NAME_ALARM_NOTEID + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_NAME_ALARM_NAME + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_NAME_ALARM_CONTENT + " TEXT NOT NULL DEFAULT \"\""
                + ")");
    }

    private static final String TABLE_NAME_LOCATION = "location";
    private static final String COLUMN_NAME_LOCATION_NOTEID = "noteid";
    private static final String COLUMN_NAME_LOCATION_LATITUDE = "latitude";
    private static final String COLUMN_NAME_LOCATION_LONGITUDE = "longitude";


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("CREATE TABLE " + TABLE_NAME_LOCATION + "("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_NAME_LOCATION_NOTEID + " TEXT NOT NULL DEFAULT \"\","
                        + COLUMN_NAME_LOCATION_LATITUDE + " REAL NOT NULL DEFAULT 0.0,"
                        + COLUMN_NAME_LOCATION_LONGITUDE + " REAL NOT NULL DEFAULT 0.0"
                        + ")");
                break;
            default:
                throw new IllegalStateException(
                        "onUpgrade() with unknown oldVersion" + oldVersion);
        }
    }
}
