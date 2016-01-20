package bupt.icyicarus.nevernote;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import bupt.icyicarus.nevernote.db.NeverNoteDB;
import bupt.icyicarus.nevernote.font.FontManager;
import bupt.icyicarus.nevernote.init.SetPortrait;
import bupt.icyicarus.nevernote.mediaList.MediaAdapter;
import bupt.icyicarus.nevernote.mediaList.MediaListCellData;
import bupt.icyicarus.nevernote.mediaList.MediaType;
import bupt.icyicarus.nevernote.mediaView.audio.AudioRecorder;
import bupt.icyicarus.nevernote.mediaView.audio.AudioViewer;
import bupt.icyicarus.nevernote.mediaView.photo.PhotoViewer;
import bupt.icyicarus.nevernote.mediaView.video.VideoViewer;

public class EditNote extends SetPortrait {

    public static final String EXTRA_NOTE_ID = "noteID";
    public static final String EXTRA_NOTE_NAME = "noteName";
    public static final String EXTRA_NOTE_CONTENT = "noteContent";
    public static final int REQUEST_CODE_GET_PHOTO = 1;
    public static final int REQUEST_CODE_GET_VIDEO = 2;
    public static final int REQUEST_CODE_GET_AUDIO = 3;
    private ListView enListView;
    private File f;
    private int noteID = -1;
    private EditText etName, etContent;
    private MediaAdapter adapter;
    private NeverNoteDB db;
    private SQLiteDatabase dbRead, dbWrite;
    private String currentPath = null;
    private OnClickListener btnClickHandler = new OnClickListener() {
        Intent i;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnSave:
                    saveMedia(saveNote());
                    setResult(RESULT_OK);
                    finish();
                    break;
                case R.id.btnCancel:
                    setResult(RESULT_CANCELED);
                    finish();
                    break;
                case R.id.btnAddPhoto:
                    i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    f = new File(getMediaDir(), System.currentTimeMillis() + ".jpg");
                    if (!f.exists()) {
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    currentPath = f.getAbsolutePath();
                    i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(i, REQUEST_CODE_GET_PHOTO);
                    break;
                case R.id.btnAddVideo:
                    i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    f = new File(getMediaDir(), System.currentTimeMillis() + ".mp4");
                    if (!f.exists()) {
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    currentPath = f.getAbsolutePath();
                    i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(i, REQUEST_CODE_GET_VIDEO);
                    break;
                case R.id.btnAddAudio:
                    i = new Intent(EditNote.this, AudioRecorder.class);
                    currentPath = getMediaDir().getAbsolutePath() + "/" + System.currentTimeMillis() + ".amr";
                    i.putExtra(AudioRecorder.EXTRA_PATH, currentPath);
                    startActivityForResult(i, REQUEST_CODE_GET_AUDIO);
                    break;
                default:
                    break;
            }
        }
    };

    public File getMediaDir() {
        File dir = new File(Environment.getExternalStorageDirectory(), "NeverNoteMedia");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_edit_note);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.containerEditNote), iconFont);

        db = new NeverNoteDB(this);
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();

        enListView = (ListView) findViewById(R.id.enListView);
        adapter = new MediaAdapter(this);
        enListView.setAdapter(adapter);
        enListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaListCellData data = adapter.getItem(position);
                Intent i;

                switch (data.type) {
                    case MediaType.PHOTO:
                        i = new Intent(EditNote.this, PhotoViewer.class);
                        i.putExtra(PhotoViewer.EXTRA_PATH, data.path);
                        startActivity(i);
                        break;
                    case MediaType.VIDEO:
                        i = new Intent(EditNote.this, VideoViewer.class);
                        i.putExtra(VideoViewer.EXTRA_PATH, data.path);
                        startActivity(i);
                        break;
                    case MediaType.AUDIO:
                        i = new Intent(EditNote.this, AudioViewer.class);
                        i.putExtra(AudioViewer.EXTRA_PATH, data.path);
                        startActivity(i);
                        break;
                    default:
                        break;
                }
            }
        });
        enListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final MediaListCellData data = adapter.getItem(position);
                new AlertDialog.Builder(EditNote.this).setTitle("Delete?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbWrite.delete(NeverNoteDB.TABLE_NAME_MEDIA, NeverNoteDB.COLUMN_NAME_MEDIA_PATH + "=?", new String[]{data.path + ""});
                        f = new File(data.path);
                        f.delete();
                        adapter.Remove(data.path);
                        adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("No", null).show();
                return true;//返回false时还会调用onitemclick
            }
        });

        etName = (EditText) findViewById(R.id.etName);
        etContent = (EditText) findViewById(R.id.etContent);

        noteID = getIntent().getIntExtra(EXTRA_NOTE_ID, -1);

        if (noteID > -1) {// edit note
            etName.setText(getIntent().getStringExtra(EXTRA_NOTE_NAME));
            etContent.setText(getIntent().getStringExtra(EXTRA_NOTE_CONTENT));

            Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_MEDIA, null, NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{noteID + ""}, null, null, null);
            while (c.moveToNext()) {
                adapter.Add(new MediaListCellData(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_MEDIA_PATH)), c.getInt(c.getColumnIndex(NeverNoteDB.COLUMN_ID))));
            }
            adapter.notifyDataSetChanged();
            c.close();
        }
        WindowManager wm = this.getWindowManager();
        ((EditText) findViewById(R.id.etContent)).setMaxHeight(wm.getDefaultDisplay().getHeight() * 35 / 100);
        ((EditText) findViewById(R.id.etContent)).setMinHeight(wm.getDefaultDisplay().getHeight() * 20 / 100);

        findViewById(R.id.btnSave).setOnClickListener(btnClickHandler);
        findViewById(R.id.btnCancel).setOnClickListener(btnClickHandler);
        findViewById(R.id.btnAddPhoto).setOnClickListener(btnClickHandler);
        findViewById(R.id.btnAddVideo).setOnClickListener(btnClickHandler);
        findViewById(R.id.btnAddAudio).setOnClickListener(btnClickHandler);
    }

    public void saveMedia(int noteID) {

        MediaListCellData data;
        ContentValues cv;

        for (int i = 0; i < adapter.getCount(); i++) {
            data = adapter.getItem(i);
            if (data.id <= -1) {
                cv = new ContentValues();
                cv.put(NeverNoteDB.COLUMN_NAME_MEDIA_PATH, data.path);
                cv.put(NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID, noteID);
                dbWrite.insert(NeverNoteDB.TABLE_NAME_MEDIA, null, cv);
            }
        }
    }

    public int saveNote() {

        ContentValues cv = new ContentValues();
        cv.put(NeverNoteDB.COLUMN_NAME_NOTE_NAME, etName.getText().toString());
        cv.put(NeverNoteDB.COLUMN_NAME_NOTE_CONTENT, etContent.getText().toString());
        cv.put(NeverNoteDB.COLUMN_NAME_NOTE_DATE, new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));

        if (noteID > -1) {
            dbWrite.update(NeverNoteDB.TABLE_NAME_NOTES, cv, NeverNoteDB.COLUMN_ID + "=?", new String[]{noteID + ""});
            return noteID;
        } else {
            return (int) dbWrite.insert(NeverNoteDB.TABLE_NAME_NOTES, null, cv);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_GET_PHOTO:
            case REQUEST_CODE_GET_VIDEO:
            case REQUEST_CODE_GET_AUDIO:
                toastOut(resultCode + "");
                if (resultCode == RESULT_OK) {
                    adapter.Add(new MediaListCellData(currentPath));
                    adapter.notifyDataSetChanged();
                }
                break;

            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        dbRead.close();
        dbWrite.close();
        super.onDestroy();
    }
}
