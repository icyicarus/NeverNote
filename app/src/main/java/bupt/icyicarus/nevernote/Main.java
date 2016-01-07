package bupt.icyicarus.nevernote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;

import bupt.icyicarus.nevernote.db.NeverNoteDB;
import bupt.icyicarus.nevernote.init.SetPortrait;
import bupt.icyicarus.nevernote.noteList.NoteAdapter;
import bupt.icyicarus.nevernote.noteList.NoteListCellData;

public class Main extends SetPortrait {
    private ListView mainListView;
    private OnClickListener btnAddNote_clickHandler = new OnClickListener() {

        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent(Main.this, EditNote.class), REQUEST_CODE_ADD_NOTE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_main_view);

        mainListView = (ListView) findViewById(R.id.mainListView);

        db = new NeverNoteDB(this);
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();

        adapter = new NoteAdapter(this);
        Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_NOTES, null, null, null, null, null, null);
        while (c.moveToNext()) {
            adapter.Add(new NoteListCellData(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_NAME)), c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_DATE)), c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_CONTENT)), c.getInt(c.getColumnIndex(NeverNoteDB.COLUMN_ID))));
        }
        adapter.notifyDataSetChanged();
        c.close();

        mainListView.setAdapter(adapter);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoteListCellData data = adapter.getItem(position);
                Intent i = new Intent(Main.this, EditNote.class);

                i.putExtra(EditNote.EXTRA_NOTE_ID, data.id);
                i.putExtra(EditNote.EXTRA_NOTE_NAME, data.name);
                i.putExtra(EditNote.EXTRA_NOTE_CONTENT, data.content);
                startActivityForResult(i, REQUEST_CODE_EDIT_NOTE);
            }
        });
        mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(Main.this).setTitle("Delete?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NoteListCellData data = adapter.getItem(position);
                        File f;

                        Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_MEDIA, null, NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{data.id + ""}, null, null, null);
                        while (c.moveToNext()) {
                            f = new File(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_MEDIA_PATH)));
                            f.delete();
                        }
                        dbWrite.delete(NeverNoteDB.TABLE_NAME_MEDIA, NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{data.id + ""});
                        dbWrite.delete(NeverNoteDB.TABLE_NAME_NOTES, NeverNoteDB.COLUMN_ID + "=?", new String[]{data.id + ""});
                        c.close();
                        refreshNotesListView();
                    }
                }).setNegativeButton("No", null).show();
                return true;
            }
        });

        refreshNotesListView();

        findViewById(R.id.btnAddNote).setOnClickListener(btnAddNote_clickHandler);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_ADD_NOTE:
            case REQUEST_CODE_EDIT_NOTE:
                if (resultCode == Activity.RESULT_OK) {
                    refreshNotesListView();
                }
                break;

            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void refreshNotesListView() {
        adapter.Clear();
        Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_NOTES, null, null, null, null, null, null);
        while (c.moveToNext()) {
            adapter.Add(new NoteListCellData(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_NAME)), c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_DATE)), c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_CONTENT)), c.getInt(c.getColumnIndex(NeverNoteDB.COLUMN_ID))));
        }
        mainListView.setAdapter(adapter);
    }

    private NoteAdapter adapter = null;
    private NeverNoteDB db;
    private SQLiteDatabase dbRead, dbWrite;

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_EDIT_NOTE = 2;
}
