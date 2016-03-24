package bupt.icyicarus.nevernote.init;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class SetPortrait extends AppCompatActivity {

    public Properties p = null;
    public Boolean customBackground = false;
    public String backgroundColor = "#000000";
    public String launchView = "Overview";
    public String mediaDirectory = null;
    public String configFileName = null;

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
            p.put("LAUNCH_VIEW", launchView);
            saveConfig(configFileName, p);
        }
        customBackground = Boolean.parseBoolean(p.get("CUSTOM_BACKGROUND").toString());
        backgroundColor = p.get("BACKGROUND_COLOR").toString();
        launchView = p.get("LAUNCH_VIEW").toString();
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
}
