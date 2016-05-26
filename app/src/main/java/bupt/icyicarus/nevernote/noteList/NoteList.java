package bupt.icyicarus.nevernote.noteList;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.dexafree.materialList.view.MaterialListView;

import java.util.ArrayList;

import bupt.icyicarus.nevernote.PublicVariableAndMethods;
import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.db.NeverNoteDB;
import bupt.icyicarus.nevernote.init.Initialization;

public class NoteList extends Initialization {
    private MaterialListView mlvNoteList;
    private ArrayList<NoteListCellData> noteListCellDataArrayList = null;
    private NeverNoteDB db;
    private SQLiteDatabase dbRead, dbWrite;
    private long noteDate = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_note_list);

        Toolbar tbMain = (Toolbar) findViewById(R.id.tbNoteList);
        setSupportActionBar(tbMain);

        mlvNoteList = (MaterialListView) findViewById(R.id.mlvNoteList);

        db = new NeverNoteDB(this);
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();

        noteDate = getIntent().getLongExtra("noteDate", -1);

        findViewById(R.id.fabmNoteListAddNote).setOnClickListener(fabClickHandler);
        findViewById(R.id.fabmNoteListAddPhoto).setOnClickListener(fabClickHandler);
        findViewById(R.id.fabmNoteListAddAudio).setOnClickListener(fabClickHandler);
        findViewById(R.id.fabmNoteListAddVideo).setOnClickListener(fabClickHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PublicVariableAndMethods.refreshNoteArrayList(NoteList.this, mlvNoteList, getResources(), noteListCellDataArrayList, dbRead, dbWrite, noteDate);
        if (customBackground) {
            findViewById(R.id.cNoteList).setBackgroundColor(Integer.parseInt(backgroundColor));
        } else {
            findViewById(R.id.cNoteList).setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PublicVariableAndMethods.REQUEST_CODE_GET_PHOTO:
            case PublicVariableAndMethods.REQUEST_CODE_GET_VIDEO:
            case PublicVariableAndMethods.REQUEST_CODE_GET_AUDIO:
                if (resultCode == RESULT_OK) {
                    if (haveTodayNote != -1) {
                        ContentValues cv = new ContentValues();
                        cv.put(NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID, haveTodayNote);
                        cv.put(NeverNoteDB.COLUMN_NAME_MEDIA_PATH, f.getAbsolutePath());
                        dbWrite.insert(NeverNoteDB.TABLE_NAME_MEDIA, null, cv);
                    } else {
//                        ContentValues cv = new ContentValues();
//                        String newNoteDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//                        cv.put(NeverNoteDB.COLUMN_NAME_NOTE_DATE, newNoteDate);
//                        Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_NOTES, null, NeverNoteDB.COLUMN_NAME_NOTE_DATE + "=?", new String[]{newNoteDate + ""}, null, null, null, null);
//                        int id = -1;
//                        while (c.moveToNext()) {
//                            id = c.getInt(c.getColumnIndex(NeverNoteDB.COLUMN_ID));
//                        }
//                        cv.put(NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID, id);
//                        cv.put(NeverNoteDB.COLUMN_NAME_MEDIA_PATH, f.getAbsolutePath());
//                        dbWrite.insert(NeverNoteDB.TABLE_NAME_NOTES, null, cv);
                        Log.e("haveTodayNote", "-1");
                    }
                } else if (f != null) {
                    f.delete();
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
