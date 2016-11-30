package bupt.icyicarus.nevernote.noteList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;

import bupt.icyicarus.nevernote.db.NeverNoteDB;

public class NoteListCellData {

    public String name;
    public String date;
    public String content;
    public int id;
    public Boolean havePic;
    public String picturePath = "";
    public LinkedList<String> mediaList;

    public NoteListCellData(String name, String date, String content, int id, String latitude, String longitude, Context context) {
        this.name = name;
        this.date = date;
        this.content = content;
        this.id = id;
        this.mediaList = new LinkedList<>();
        this.havePic = checkMedia(id, context);
    }

    private Boolean checkMedia(int id, Context context) {
        NeverNoteDB db = new NeverNoteDB(context);
        SQLiteDatabase dbRead;
        dbRead = db.getReadableDatabase();

        Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_MEDIA, null, NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{id + ""}, null, null, null);
        while (c.moveToNext()) {
            String path = c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_MEDIA_PATH));
            mediaList.add(path);
            if (path.endsWith(".jpg") && this.picturePath.equals("")) {
                this.picturePath = path;
            }
        }
        c.close();
        return !this.picturePath.equals("");
    }
}
