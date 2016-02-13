package bupt.icyicarus.nevernote.config;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.init.SetPortrait;

public class Settings extends SetPortrait {

    private ToggleButton toggleCustomBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_settings);

        Toolbar tbMain = (Toolbar) findViewById(R.id.tbSettings);
        setSupportActionBar(tbMain);
    }

    @Override
    protected void onResume() {
        super.onResume();
        toggleCustomBackground = (ToggleButton) findViewById(R.id.toggleCustomBackground);
        toggleCustomBackground.setChecked(customBackground);
        toggleCustomBackground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                p.put("CUSTOM_BACKGROUND", isChecked + "");
                customBackground = isChecked;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveConfig(configFileName, p);
    }

}
