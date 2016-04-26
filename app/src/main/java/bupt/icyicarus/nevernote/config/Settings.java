package bupt.icyicarus.nevernote.config;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.Switch;
import com.gc.materialdesign.widgets.ColorSelector;
import com.gc.materialdesign.widgets.SnackBar;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.init.SetPortrait;

public class Settings extends SetPortrait {

    private List<String> viewSet = new LinkedList<>(Arrays.asList("Overview", "Calender"));

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

        final NiceSpinner niceSpinner = (NiceSpinner) findViewById(R.id.niceSpinner);
        niceSpinner.attachDataSource(viewSet);
        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                p.put("LAUNCH_VIEW", viewSet.get(niceSpinner.getSelectedIndex()));
                SnackBar snackBar = new SnackBar(Settings.this, "This setting will take effect at the next launch", null, null);
                snackBar.show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        niceSpinner.setSelectedIndex(viewSet.indexOf(launchView));

        Switch switchCustomBackground = (Switch) findViewById(R.id.switchCustomBackground);
        switchCustomBackground.setChecked(customBackground);

        Switch switchShowOKButton = (Switch) findViewById(R.id.switchShowOKButton);
        switchShowOKButton.setChecked(showOKButton);

        final FrameLayout containerBackgroundColor = (FrameLayout) findViewById(R.id.containerBackgroundColor);
        final ButtonRectangle sampleBackgroundColor = (ButtonRectangle) findViewById(R.id.sampleBackgroundColor);
        containerBackgroundColor.setVisibility(customBackground ? View.VISIBLE : View.GONE);
        sampleBackgroundColor.setBackgroundColor(Integer.parseInt(p.get("BACKGROUND_COLOR").toString()));

        switchCustomBackground.setOncheckListener(new Switch.OnCheckListener() {
            @Override
            public void onCheck(Switch view, boolean check) {
                customBackground = check;
                containerBackgroundColor.setVisibility(customBackground ? View.VISIBLE : View.GONE);
                sampleBackgroundColor.setBackgroundColor(Integer.parseInt(backgroundColor));
                p.put("CUSTOM_BACKGROUND", check + "");
                p.put("BACKGROUND_COLOR", backgroundColor);
            }
        });

        switchShowOKButton.setOncheckListener(new Switch.OnCheckListener() {
            @Override
            public void onCheck(Switch view, boolean check) {
                showOKButton = check;
                p.put("SHOW_OK", check + "");
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
