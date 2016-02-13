package bupt.icyicarus.nevernote.config;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.colorPicker.ColorPicker;
import bupt.icyicarus.nevernote.colorPicker.OpacityBar;
import bupt.icyicarus.nevernote.colorPicker.SVBar;
import bupt.icyicarus.nevernote.init.SetPortrait;

public class Settings extends SetPortrait {

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

        ToggleButton toggleCustomBackground = (ToggleButton) findViewById(R.id.toggleCustomBackground);
        toggleCustomBackground.setChecked(customBackground);
        final LinearLayout containerColorPicker = (LinearLayout) findViewById(R.id.containerColorPicker);
        final ColorPicker colorPicker = (ColorPicker) findViewById(R.id.colorPicker);
        final SVBar svBar = (SVBar) findViewById(R.id.svBar);
        final OpacityBar opacityBar = (OpacityBar) findViewById(R.id.oBar);
        colorPicker.addSVBar(svBar);
        colorPicker.addOpacityBar(opacityBar);

        containerColorPicker.setVisibility(customBackground ? View.VISIBLE : View.GONE);
        colorPicker.setColor(Integer.parseInt(p.get("BACKGROUND_COLOR").toString()));
        colorPicker.setOldCenterColor(Integer.parseInt(p.get("BACKGROUND_COLOR").toString()));

        toggleCustomBackground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                customBackground = isChecked;
                containerColorPicker.setVisibility(customBackground ? View.VISIBLE : View.GONE);
                backgroundColor = colorPicker.getColor() + "";
                p.put("CUSTOM_BACKGROUND", isChecked + "");
                p.put("BACKGROUND_COLOR", backgroundColor);
            }
        });
        colorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                p.put("BACKGROUND_COLOR", color + "");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveConfig(configFileName, p);
    }

}
