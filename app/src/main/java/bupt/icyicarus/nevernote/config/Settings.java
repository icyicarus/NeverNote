package bupt.icyicarus.nevernote.config;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.ColorSelector;

import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.init.Initialization;

public class Settings extends Initialization {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_settings);

        Toolbar tbMain = (Toolbar) findViewById(R.id.toolBarSettingsView);
        setSupportActionBar(tbMain);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SwitchCompat scShowOKButton = (SwitchCompat) findViewById(R.id.switchCompatSettingsViewShowSaveButton);
        scShowOKButton.setChecked(showOKButton);

        SwitchCompat scCustomBackground = (SwitchCompat) findViewById(R.id.switchCompatSettingsViewCustomBackground);
        scCustomBackground.setChecked(customBackground);

        final FrameLayout containerBackgroundColor = (FrameLayout) findViewById(R.id.frameLayoutSettingsViewSampleBackgroundColor);
        final ButtonRectangle sampleBackgroundColor = (ButtonRectangle) findViewById(R.id.buttonRectangleSampleBackgroundColor);
        containerBackgroundColor.setVisibility(customBackground ? View.VISIBLE : View.GONE);
        sampleBackgroundColor.setBackgroundColor(Integer.parseInt(p.get("BACKGROUND_COLOR").toString()));

        scShowOKButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showOKButton = isChecked;
                p.put("SHOW_OK", isChecked + "");
            }
        });

        scCustomBackground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                customBackground = isChecked;
                containerBackgroundColor.setVisibility(customBackground ? View.VISIBLE : View.GONE);
                sampleBackgroundColor.setBackgroundColor(Integer.parseInt(backgroundColor));
                p.put("CUSTOM_BACKGROUND", isChecked + "");
                p.put("BACKGROUND_COLOR", backgroundColor);
            }
        });

        sampleBackgroundColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorSelector colorSelector = new ColorSelector(Settings.this, Integer.parseInt(backgroundColor), new ColorSelector.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        backgroundColor = color + "";
                        p.put("BACKGROUND_COLOR", color + "");
                        sampleBackgroundColor.setBackgroundColor(color);
                    }
                });
                colorSelector.show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveConfig(configFileName, p);
    }
}
