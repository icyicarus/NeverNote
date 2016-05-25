package bupt.icyicarus.nevernote.init;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import bupt.icyicarus.nevernote.AudioRecorder;
import bupt.icyicarus.nevernote.PublicVariableAndMethods;
import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.config.Settings;
import bupt.icyicarus.nevernote.view.NoteView;

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
    public View.OnClickListener fabClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i;
            switch (v.getId()) {
                case R.id.fabmMergeViewAddNote:
                case R.id.fabmNoteListAddNote:
                    startActivity(new Intent(getApplicationContext(), NoteView.class));
                    break;
                case R.id.fabmMergeViewAddPhoto:
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
                case R.id.fabmMergeViewAddAudio:
                case R.id.fabmNoteListAddAudio:
                    i = new Intent(getApplicationContext(), AudioRecorder.class);
                    currentPath = mediaDirectory + "/" + System.currentTimeMillis() + ".amr";
                    i.putExtra(AudioRecorder.EXTRA_PATH, currentPath);
                    startActivityForResult(i, PublicVariableAndMethods.REQUEST_CODE_GET_AUDIO);
                    break;
                case R.id.fabmMergeViewAddVideo:
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            startActivity(new Intent(this, Settings.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
