package bupt.icyicarus.nevernote.init;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import bupt.icyicarus.nevernote.BuildConfig;
import bupt.icyicarus.nevernote.PublicVariableAndMethods;
import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.alarm.AlarmList;
import bupt.icyicarus.nevernote.config.Settings;
import bupt.icyicarus.nevernote.db.NeverNoteDB;
import bupt.icyicarus.nevernote.view.NoteView;
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;

public class Initialization extends AppCompatActivity {

    public final int PERMISSION_REQUEST_RECORD_AUDIO = 1;

    public Properties p = null;
    public Boolean customBackground = false;
    public String backgroundColor = "#000000";
    public Boolean showOKButton = false;
    public int haveTodayNote = -1;

    public String mediaDirectory = null;
    public String configFileName = null;

    public String currentPath = null;
    public File f = null;

    public Boolean needMenu = false;

    public View.OnClickListener fabClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i;
            switch (v.getId()) {
                case R.id.floatingActionMenuMainViewAddNote:
                case R.id.floatActionMenuNoteListViewAddNote:
                    startActivity(new Intent(getApplicationContext(), NoteView.class));
                    break;
                case R.id.floatingActionMenuMainViewAddPhoto:
                case R.id.floatActionMenuNoteListViewAddPhoto:
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
                    i.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(Initialization.this, BuildConfig.APPLICATION_ID + ".provider", f));
                    startActivityForResult(i, PublicVariableAndMethods.REQUEST_CODE_GET_PHOTO);
                    break;
                case R.id.floatingActionMenuMainViewAddAudio:
                case R.id.floatActionMenuNoteListViewAddAudio:
//                    i = new Intent(getApplicationContext(), AudioRecorder.class);
//                    f = new File(mediaDirectory, System.currentTimeMillis() + ".aac");
//                    if (!f.exists()) {
//                        try {
//                            f.createNewFile();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    currentPath = f.getAbsolutePath();
//                    i.putExtra(AudioRecorder.EXTRA_PATH, currentPath);
//                    startActivityForResult(i, PublicVariableAndMethods.REQUEST_CODE_GET_AUDIO);
                    currentPath = mediaDirectory + "/" + System.currentTimeMillis() + ".wav";
                    int color = getResources().getColor(R.color.colorPrimaryDark);
                    AndroidAudioRecorder.with(Initialization.this)
                            // Required
                            .setFilePath(currentPath)
                            .setColor(color)
                            .setRequestCode(PublicVariableAndMethods.REQUEST_CODE_GET_AUDIO)

                            // Optional
                            .setSource(AudioSource.MIC)
                            .setChannel(AudioChannel.STEREO)
                            .setSampleRate(AudioSampleRate.HZ_48000)
                            .setAutoStart(false)
                            .setKeepDisplayOn(true)

                            // Start recording
                            .record();
                    break;
                case R.id.floatingActionMenuMainViewAddVideo:
                case R.id.floatActionMenuNoteListViewAddVideo:
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
                    i.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(Initialization.this, BuildConfig.APPLICATION_ID + ".provider", f));
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setMediaDir(this);
    }

    @Override
    protected void onResume() {
        p = loadConfig(configFileName);
        if (p == null) {
            p = new Properties();
            p.put("CUSTOM_BACKGROUND", "false");
            p.put("BACKGROUND_COLOR", "16777215");
            p.put("SHOW_OK", "false");
            saveConfig(configFileName, p);
        }
        customBackground = Boolean.parseBoolean(p.get("CUSTOM_BACKGROUND").toString());
        backgroundColor = p.get("BACKGROUND_COLOR").toString();
        showOKButton = Boolean.parseBoolean(p.get("SHOW_OK").toString());
        haveTodayNote = PublicVariableAndMethods.haveTodayNote(this);
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        NeverNoteDB db;
        final SQLiteDatabase dbRead, dbWrite;
        db = new NeverNoteDB(this);
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();
        haveTodayNote = PublicVariableAndMethods.haveTodayNote(this);

        switch (requestCode) {
            case PublicVariableAndMethods.REQUEST_CODE_GET_PHOTO:
            case PublicVariableAndMethods.REQUEST_CODE_GET_VIDEO:
            case PublicVariableAndMethods.REQUEST_CODE_GET_AUDIO:
                if (resultCode == RESULT_OK) {
                    if (haveTodayNote != -1) {
                        ContentValues cv = new ContentValues();
                        cv.put(NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID, haveTodayNote);
                        cv.put(NeverNoteDB.COLUMN_NAME_MEDIA_PATH, currentPath);
                        dbWrite.insert(NeverNoteDB.TABLE_NAME_MEDIA, null, cv);
                    } else {
                        ContentValues cv = new ContentValues();
                        String newNoteDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        cv.put(NeverNoteDB.COLUMN_NAME_NOTE_DATE, newNoteDate);
                        cv.put(NeverNoteDB.COLUMN_NAME_NOTE_NAME, "Created on " + newNoteDate);
                        dbWrite.insert(NeverNoteDB.TABLE_NAME_NOTES, null, cv);
                        Cursor c = dbRead.rawQuery("SELECT last_insert_rowid()", null);
                        c.moveToFirst();
                        int noteID = c.getInt(0);
                        c.close();
                        cv.clear();
                        cv.put(NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID, noteID);
                        cv.put(NeverNoteDB.COLUMN_NAME_MEDIA_PATH, currentPath);
                        dbWrite.insert(NeverNoteDB.TABLE_NAME_MEDIA, null, cv);
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

    public void setMediaDir(Context context) {
        mediaDirectory = context.getExternalFilesDir(null).toString();
        configFileName = mediaDirectory + "/config.properties";
    }

    public Properties loadConfig(String filename) {
        Properties properties = new Properties();
        try {
            FileInputStream configStream = new FileInputStream(filename);
            properties.load(configStream);
            configStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return properties;
    }

    public boolean saveConfig(String filename, Properties properties) {
        try {
            File configFile = new File(filename);
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            FileOutputStream configStream = new FileOutputStream(configFile);
            properties.store(configStream, "");
            configStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (needMenu) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        } else return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (needMenu) {
            int id = item.getItemId();
            switch (id) {
                case R.id.settings:
                    startActivity(new Intent(this, Settings.class));
                    break;
                case R.id.alarmList:
                    startActivity(new Intent(this, AlarmList.class));
                    break;
                default:
                    break;
            }
            return super.onOptionsItemSelected(item);
        } else return false;
    }
}
