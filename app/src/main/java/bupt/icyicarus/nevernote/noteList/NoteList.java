package bupt.icyicarus.nevernote.noteList;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;
import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import bupt.icyicarus.nevernote.PublicVariableAndMethods;
import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.db.NeverNoteDB;
import bupt.icyicarus.nevernote.init.Initialization;
import bupt.icyicarus.nevernote.mediaView.audio.AudioRecorder;
import bupt.icyicarus.nevernote.view.NoteView;

public class NoteList extends Initialization {
    private MaterialListView mlvOverView;
    private ArrayList<NoteListCellData> noteListCellDataArrayList = null;
    private NeverNoteDB db;
    private SQLiteDatabase dbRead, dbWrite;
    private long noteDate = -1;
    private long noteAddDate = -1;
    private String currentPath = null;
    private File f = null;

    private OnClickListener clickHandlerNoteList = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i;
            switch (v.getId()) {
                case R.id.fabmNoteListAddNote:
                    startActivity(new Intent(NoteList.this, NoteView.class));
                    break;
                case R.id.fabmNoteListAddPhoto:
                    i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    f = new File(mediaDirectory, System.currentTimeMillis() + ".jpg");
                    if (!f.exists()) {
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    currentPath = f.getAbsolutePath();
                    i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(i, PublicVariableAndMethods.REQUEST_CODE_GET_PHOTO);
                    break;
                case R.id.fabmNoteListAddAudio:
                    i = new Intent(NoteList.this, AudioRecorder.class);
                    currentPath = mediaDirectory + "/" + System.currentTimeMillis() + ".amr";
                    i.putExtra(AudioRecorder.EXTRA_PATH, currentPath);
                    startActivityForResult(i, PublicVariableAndMethods.REQUEST_CODE_GET_AUDIO);
                    break;
                case R.id.fabmNoteListAddVideo:
                    i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    f = new File(mediaDirectory, System.currentTimeMillis() + ".mp4");
                    if (!f.exists()) {
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    currentPath = f.getAbsolutePath();
                    i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(i, PublicVariableAndMethods.REQUEST_CODE_GET_VIDEO);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_note_list);

        Toolbar tbMain = (Toolbar) findViewById(R.id.tbNoteList);
        setSupportActionBar(tbMain);

        mlvOverView = (MaterialListView) findViewById(R.id.mlvNoteList);

        db = new NeverNoteDB(this);
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();

        noteDate = getIntent().getLongExtra("noteDate", -1);

        FloatingActionButton fabmNoteListAddNote = (FloatingActionButton) findViewById(R.id.fabmNoteListAddNote);
        FloatingActionButton fabmNoteListAddPhoto = (FloatingActionButton) findViewById(R.id.fabmNoteListAddPhoto);
        FloatingActionButton fabmNoteListAddAudio = (FloatingActionButton) findViewById(R.id.fabmNoteListAddAudio);
        FloatingActionButton fabmNoteListAddVideo = (FloatingActionButton) findViewById(R.id.fabmNoteListAddVideo);
        fabmNoteListAddNote.setOnClickListener(clickHandlerNoteList);
        fabmNoteListAddPhoto.setOnClickListener(clickHandlerNoteList);
        fabmNoteListAddAudio.setOnClickListener(clickHandlerNoteList);
        fabmNoteListAddVideo.setOnClickListener(clickHandlerNoteList);
    }

    public void refreshNoteArrayList() {
        mlvOverView.getAdapter().clearAll();
        if (noteListCellDataArrayList != null) {
            noteListCellDataArrayList.clear();
        } else {
            noteListCellDataArrayList = new ArrayList<>();
        }
        Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_NOTES, null, null, null, null, null, null, null);
        while (c.moveToNext()) {
            try {
                noteAddDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_DATE))).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (noteDate == -1 || (noteDate != -1 && noteAddDate >= noteDate && noteAddDate < (noteDate + 86400000))) {
                noteListCellDataArrayList.add(new NoteListCellData(
                        c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_NAME)),
                        c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_DATE)),
                        c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_CONTENT)),
                        c.getInt(c.getColumnIndex(NeverNoteDB.COLUMN_ID)),
                        NoteList.this
                ));
            }
        }
        for (NoteListCellData noteListCellData : noteListCellDataArrayList) {
            addCard(noteListCellData);
        }
        c.close();
    }

    private void addCard(NoteListCellData noteListCellData) {
        Card card;
        if (noteListCellData.havePic) {

            Bitmap bitmap = BitmapFactory.decodeFile(noteListCellData.picturePath, PublicVariableAndMethods.getBitmapOption(16));

            card = new Card.Builder(this)
                    .setTag(noteListCellData)
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_basic_image_buttons_card_layout)
                    .setTitle(noteListCellData.name)
                    .setDescription(noteListCellData.date)
                    .setDrawable(new BitmapDrawable(getResources(), bitmap))
                    .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                        @Override
                        public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                            requestCreator.fit();
                        }
                    })
                    .addAction(R.id.left_text_button, new TextViewAction(this)
                            .setText("Delete")
                            .setTextResourceColor(R.color.black_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, final Card card) {
                                    new AlertDialog.Builder(NoteList.this).setTitle("Delete this note?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            NoteListCellData data = (NoteListCellData) card.getTag();
                                            File f;

                                            Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_MEDIA, null, NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{data.id + ""}, null, null, null);
                                            while (c.moveToNext()) {
                                                f = new File(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_MEDIA_PATH)));
                                                if (!f.delete())
                                                    Log.e("file", "delete error");
                                            }
                                            dbWrite.delete(NeverNoteDB.TABLE_NAME_MEDIA, NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{data.id + ""});
                                            dbWrite.delete(NeverNoteDB.TABLE_NAME_NOTES, NeverNoteDB.COLUMN_ID + "=?", new String[]{data.id + ""});
                                            c.close();
                                            refreshNoteArrayList();
                                            haveTodayNote = PublicVariableAndMethods.haveTodayNote(NoteList.this);
                                        }
                                    }).setNegativeButton("No", null).show();
                                }
                            }))
                    .addAction(R.id.right_text_button, new TextViewAction(this)
                            .setText("Edit")
                            .setTextResourceColor(R.color.orange_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                    NoteListCellData data = (NoteListCellData) card.getTag();
                                    Intent i = new Intent(NoteList.this, NoteView.class);

                                    if (data != null) {
                                        i.putExtra(NoteView.EXTRA_NOTE_ID, data.id);
                                        i.putExtra(NoteView.EXTRA_NOTE_NAME, data.name);
                                        i.putExtra(NoteView.EXTRA_NOTE_CONTENT, data.content);
                                    }
                                    startActivity(i);
                                }
                            }))
                    .endConfig()
                    .build();
        } else {
            card = new Card.Builder(this)
                    .setTag(noteListCellData)
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_basic_buttons_card)
                    .setTitle(noteListCellData.name)
                    .setDescription(noteListCellData.date)
                    .addAction(R.id.left_text_button, new TextViewAction(this)
                            .setText("Delete")
                            .setTextResourceColor(R.color.black_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, final Card card) {
                                    new AlertDialog.Builder(NoteList.this).setTitle("Delete this note?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            NoteListCellData data = (NoteListCellData) card.getTag();
                                            File f;

                                            Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_MEDIA, null, NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{data.id + ""}, null, null, null);
                                            while (c.moveToNext()) {
                                                f = new File(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_MEDIA_PATH)));
                                                if (!f.delete())
                                                    Log.e("file", "delete error");
                                            }
                                            dbWrite.delete(NeverNoteDB.TABLE_NAME_MEDIA, NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{data.id + ""});
                                            dbWrite.delete(NeverNoteDB.TABLE_NAME_NOTES, NeverNoteDB.COLUMN_ID + "=?", new String[]{data.id + ""});
                                            c.close();
                                            refreshNoteArrayList();
                                        }
                                    }).setNegativeButton("No", null).show();
                                }
                            }))
                    .addAction(R.id.right_text_button, new TextViewAction(this)
                            .setText("Edit")
                            .setTextResourceColor(R.color.orange_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                    NoteListCellData data = (NoteListCellData) card.getTag();
                                    Intent i = new Intent(NoteList.this, NoteView.class);

                                    if (data != null) {
                                        i.putExtra(NoteView.EXTRA_NOTE_ID, data.id);
                                        i.putExtra(NoteView.EXTRA_NOTE_NAME, data.name);
                                        i.putExtra(NoteView.EXTRA_NOTE_CONTENT, data.content);
                                    }
                                    startActivity(i);
                                }
                            }))
                    .endConfig()
                    .build();
        }
        mlvOverView.getAdapter().add(card);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNoteArrayList();
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
