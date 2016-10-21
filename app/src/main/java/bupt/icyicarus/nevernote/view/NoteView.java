package bupt.icyicarus.nevernote.view;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import bupt.icyicarus.nevernote.AudioRecorder;
import bupt.icyicarus.nevernote.BuildConfig;
import bupt.icyicarus.nevernote.PublicVariableAndMethods;
import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.db.NeverNoteDB;
import bupt.icyicarus.nevernote.font.FontManager;
import bupt.icyicarus.nevernote.init.Initialization;
import bupt.icyicarus.nevernote.mediaList.MediaAdapter;
import bupt.icyicarus.nevernote.mediaList.MediaListCellData;
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;

public class NoteView extends Initialization {

    public static final String EXTRA_NOTE_ID = "noteID";
    public static final String EXTRA_NOTE_LATITUDE = "noteLatitude";
    public static final String EXTRA_NOTE_LONGITUDE = "noteLongitude";
    private ListView enListView;
    private int noteID = -1;
    private String noteLatitude = " ";
    private String noteLongitude = " ";
    private EditText etName, etContent;
    private Button btnAddLocation;
    private MediaAdapter adapter;
    private SQLiteDatabase dbRead, dbWrite;
    private Map<String, String> operationQueue = null;

    private OnClickListener btnClickHandler = new OnClickListener() {
        Intent i;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonNoteViewSaveNote:
                    clearOperationQueue();
                    saveMedia(saveNote());
                    setResult(RESULT_OK);
                    finish();
                    break;
                case R.id.buttonNoteViewAddPhoto:
                    i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    f = new File(mediaDirectory, System.currentTimeMillis() + ".jpg");
                    if (!f.exists()) {
                        try {
                            if (!f.createNewFile())
                                Toast.makeText(NoteView.this, "File not created", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    currentPath = f.getAbsolutePath();
                    i.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(NoteView.this, BuildConfig.APPLICATION_ID + ".provider", f));
                    startActivityForResult(i, PublicVariableAndMethods.REQUEST_CODE_GET_PHOTO);
                    break;
                case R.id.buttonNoteViewAddVideo:
                    i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    f = new File(mediaDirectory, System.currentTimeMillis() + ".mp4");
                    if (!f.exists()) {
                        try {
                            if (!f.createNewFile())
                                Toast.makeText(NoteView.this, "File not created", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    currentPath = f.getAbsolutePath();
                    i.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(NoteView.this, BuildConfig.APPLICATION_ID + ".provider", f));
                    startActivityForResult(i, PublicVariableAndMethods.REQUEST_CODE_GET_VIDEO);
                    break;
                case R.id.buttonNoteViewAddAudio:
                    i = new Intent(NoteView.this, AudioRecorder.class);
                    currentPath = mediaDirectory + "/" + System.currentTimeMillis() + ".wav";
                    int color = getResources().getColor(R.color.colorPrimaryDark);
                    AndroidAudioRecorder.with(NoteView.this)
                            .setFilePath(currentPath)
                            .setColor(color)
                            .setRequestCode(PublicVariableAndMethods.REQUEST_CODE_GET_AUDIO)
                            .setSource(AudioSource.MIC)
                            .setChannel(AudioChannel.STEREO)
                            .setSampleRate(AudioSampleRate.HZ_48000)
                            .setAutoStart(false)
                            .setKeepDisplayOn(true)
                            .record();
                    break;
                case R.id.buttonNoteViewLocation:
                    i = new Intent(NoteView.this, MapView.class);
                    i.putExtra(EXTRA_NOTE_LATITUDE, noteLatitude);
                    i.putExtra(EXTRA_NOTE_LONGITUDE, noteLongitude);
                    startActivityForResult(i, PublicVariableAndMethods.REQUEST_CODE_GET_LOCATION);
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_note);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Toolbar tbMain = (Toolbar) findViewById(R.id.toolBarNoteView);
        setSupportActionBar(tbMain);
        needMenu = true;

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONT_AWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.coordinatorLayoutNoteView), iconFont);

        NeverNoteDB db = new NeverNoteDB(this);
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();
        operationQueue = new HashMap<>();

        enListView = (ListView) findViewById(R.id.listViewNoteViewMediaList);
        adapter = new MediaAdapter(this);
        enListView.setAdapter(adapter);
        enListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaListCellData data = adapter.getItem(position);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri uri;

                switch (data.type) {
                    case PublicVariableAndMethods.REQUEST_CODE_GET_PHOTO:
                        uri = FileProvider.getUriForFile(NoteView.this, BuildConfig.APPLICATION_ID + ".provider", new File(data.path));
                        i.setDataAndType(uri, "image/jpg");
                        startActivity(i);
                        break;
                    case PublicVariableAndMethods.REQUEST_CODE_GET_AUDIO:
                        uri = FileProvider.getUriForFile(NoteView.this, BuildConfig.APPLICATION_ID + ".provider", new File(data.path));
                        i.setDataAndType(uri, "video/mp4");
                        startActivity(i);
                        break;
                    case PublicVariableAndMethods.REQUEST_CODE_GET_VIDEO:
                        uri = FileProvider.getUriForFile(NoteView.this, BuildConfig.APPLICATION_ID + ".provider", new File(data.path));
                        i.setDataAndType(uri, "audio/wav");
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
                new AlertDialog.Builder(NoteView.this).setTitle("Delete?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        operationQueue.put(data.path, "DEL");
                        adapter.Remove(data.path);
                        adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("No", null).show();
                return true;//返回false时还会调用onitemclick
            }
        });

        etName = (EditText) findViewById(R.id.editTextNoteViewName);
        etContent = (EditText) findViewById(R.id.editTextNoteViewContent);
        btnAddLocation = (Button) findViewById(R.id.buttonNoteViewLocation);

        noteID = getIntent().getIntExtra(EXTRA_NOTE_ID, -1);

        if (noteID > -1) {// edit note
            Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_NOTES, null, NeverNoteDB.COLUMN_ID + "=?", new String[]{noteID + ""}, null, null, null);
            c.moveToNext();
            etName.setText(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_NAME)));
            etContent.setText(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_CONTENT)));
            noteLatitude = c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_LATITUDE));
            noteLongitude = c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_LONGITUDE));

            c = dbRead.query(NeverNoteDB.TABLE_NAME_MEDIA, null, NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{noteID + ""}, null, null, null);
            while (c.moveToNext()) {
                adapter.Add(new MediaListCellData(
                        c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_MEDIA_PATH)),
                        c.getInt(c.getColumnIndex(NeverNoteDB.COLUMN_ID))));
            }
            adapter.notifyDataSetChanged();
            c.close();
        }

        WindowManager wm = this.getWindowManager();
        ((EditText) findViewById(R.id.editTextNoteViewContent)).setMaxHeight(wm.getDefaultDisplay().getHeight() * 35 / 100);
        ((EditText) findViewById(R.id.editTextNoteViewContent)).setMinHeight(wm.getDefaultDisplay().getHeight() * 20 / 100);

        findViewById(R.id.buttonNoteViewSaveNote).setOnClickListener(btnClickHandler);
        findViewById(R.id.buttonNoteViewAddPhoto).setOnClickListener(btnClickHandler);
        findViewById(R.id.buttonNoteViewAddVideo).setOnClickListener(btnClickHandler);
        findViewById(R.id.buttonNoteViewAddAudio).setOnClickListener(btnClickHandler);
        findViewById(R.id.buttonNoteViewLocation).setOnClickListener(btnClickHandler);
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
        cv.put(NeverNoteDB.COLUMN_NAME_NOTE_LATITUDE, noteLatitude);
        cv.put(NeverNoteDB.COLUMN_NAME_NOTE_LONGITUDE, noteLongitude);
        if (noteID == -1) {
            cv.put(NeverNoteDB.COLUMN_NAME_NOTE_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        }

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
            case PublicVariableAndMethods.REQUEST_CODE_GET_PHOTO:
            case PublicVariableAndMethods.REQUEST_CODE_GET_VIDEO:
            case PublicVariableAndMethods.REQUEST_CODE_GET_AUDIO:
                if (resultCode == RESULT_OK) {
                    adapter.Add(new MediaListCellData(currentPath));
                    adapter.notifyDataSetChanged();
                } else if (f != null) {
                    if (!f.delete())
                        Toast.makeText(NoteView.this, "File not deleted", Toast.LENGTH_SHORT).show();
                }
                break;
            case PublicVariableAndMethods.REQUEST_CODE_GET_LOCATION:
                if (resultCode == RESULT_OK) {
                    noteLatitude = data.getStringExtra("latitude");
                    noteLongitude = data.getStringExtra("longitude");
                    btnAddLocation.setTextColor(getResources().getColor(R.color.royalblue));
                } else {
                    noteLatitude = " ";
                    noteLongitude = " ";
                    btnAddLocation.setTextColor(Color.GRAY);
                }
                break;
            default:
                break;
        }
    }

    private void clearOperationQueue() {
        for (String key : operationQueue.keySet()) {
            dbWrite.delete(NeverNoteDB.TABLE_NAME_MEDIA, NeverNoteDB.COLUMN_NAME_MEDIA_PATH + "=?", new String[]{key + ""});
            f = new File(key);
            if (!f.delete())
                Toast.makeText(NoteView.this, "File not deleted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.buttonNoteViewSaveNote).setVisibility(showOKButton ? View.VISIBLE : View.GONE);
        if (customBackground) {
            findViewById(R.id.coordinatorLayoutNoteView).setBackgroundColor(Integer.parseInt(backgroundColor));
        } else {
            findViewById(R.id.coordinatorLayoutNoteView).setBackgroundColor(Color.WHITE);
        }
        if (!Objects.equals(noteLatitude, " ") && !Objects.equals(noteLongitude, " "))
            btnAddLocation.setTextColor(getResources().getColor(R.color.royalblue));
        else
            btnAddLocation.setTextColor(Color.GRAY);
    }

    @Override
    protected void onDestroy() {
        dbRead.close();
        dbWrite.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!showOKButton) {
            clearOperationQueue();
            saveMedia(saveNote());
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }
}
