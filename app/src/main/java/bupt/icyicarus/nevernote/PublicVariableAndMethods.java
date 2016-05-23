package bupt.icyicarus.nevernote;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import bupt.icyicarus.nevernote.db.NeverNoteDB;
import bupt.icyicarus.nevernote.noteList.NoteListCellData;

public class PublicVariableAndMethods {

    public static final int REQUEST_CODE_GET_PHOTO = 1;
    public static final int REQUEST_CODE_GET_VIDEO = 2;
    public static final int REQUEST_CODE_GET_AUDIO = 3;

    public static BitmapFactory.Options getBitmapOption(int size) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = size;
        return options;
    }

    public static int haveTodayNote(Context context) {
        NeverNoteDB db;
        SQLiteDatabase dbRead;
        int haveTodayNote = -1;

        db = new NeverNoteDB(context);
        dbRead = db.getReadableDatabase();

        ArrayList<NoteListCellData> noteListCellDataArrayList = new ArrayList<>();
        Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_NOTES, null, null, null, null, null, null, null);
        while (c.moveToNext()) {
            noteListCellDataArrayList.add(new NoteListCellData(
                    c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_NAME)),
                    c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_DATE)),
                    c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_CONTENT)),
                    c.getInt(c.getColumnIndex(NeverNoteDB.COLUMN_ID)),
                    context
            ));
        }

        Date noteDate = new Date();
        Date todayDate = new Date(System.currentTimeMillis());
        for (NoteListCellData noteListCellData : noteListCellDataArrayList) {
            try {
                noteDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(noteListCellData.date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (noteDate.getMonth() == todayDate.getMonth() && noteDate.getDate() == todayDate.getDate()) {
                haveTodayNote = noteListCellData.id;
            }
        }
        return haveTodayNote;
    }
}
