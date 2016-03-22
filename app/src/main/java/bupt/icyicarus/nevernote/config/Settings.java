package bupt.icyicarus.nevernote.config;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.gc.materialdesign.views.Switch;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.init.SetPortrait;

public class Settings extends SetPortrait {

    private List<String> listLaunchView;
    private ArrayAdapter<String> adapterListLaunchView;
    private List<String> viewSet = new LinkedList<>(Arrays.asList("Overview", "Calender"));

    public static void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        SpinnerAdapter spinnerAdapter = spinner.getAdapter(); //得到SpinnerAdapter对象
        int k = spinnerAdapter.getCount();
        for (int i = 0; i < k; i++) {
            if (value.equals(spinnerAdapter.getItem(i).toString())) {
                spinner.setSelection(i, true);// 默认选中项
                break;
            }
        }
    }

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
                Toast.makeText(Settings.this, "This setting will take effect at the next launch", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        niceSpinner.setSelectedIndex(viewSet.indexOf(launchView));

        Switch switchCustomBackground = (Switch) findViewById(R.id.switchCustomBackground);
        switchCustomBackground.setChecked(customBackground);

        final LinearLayout containerColorPicker = (LinearLayout) findViewById(R.id.containerColorPicker);
        final ColorPicker colorPicker = (ColorPicker) findViewById(R.id.colorPicker);
        final SVBar svBar = (SVBar) findViewById(R.id.svBar);
        final OpacityBar opacityBar = (OpacityBar) findViewById(R.id.oBar);
        colorPicker.addSVBar(svBar);
        colorPicker.addOpacityBar(opacityBar);

        containerColorPicker.setVisibility(customBackground ? View.VISIBLE : View.GONE);
        colorPicker.setColor(Integer.parseInt(p.get("BACKGROUND_COLOR").toString()));
        colorPicker.setOldCenterColor(Integer.parseInt(p.get("BACKGROUND_COLOR").toString()));

        switchCustomBackground.setOncheckListener(new Switch.OnCheckListener() {
            @Override
            public void onCheck(Switch view, boolean check) {
                customBackground = check;
                containerColorPicker.setVisibility(customBackground ? View.VISIBLE : View.GONE);
                backgroundColor = colorPicker.getColor() + "";
                p.put("CUSTOM_BACKGROUND", check + "");
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
