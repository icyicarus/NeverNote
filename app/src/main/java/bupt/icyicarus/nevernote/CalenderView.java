package bupt.icyicarus.nevernote;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.Date;

import bupt.icyicarus.nevernote.calenderLib.CalendarPickerView;
import bupt.icyicarus.nevernote.calenderLib.CalendarPickerView.SelectionMode;
import bupt.icyicarus.nevernote.config.Settings;
import bupt.icyicarus.nevernote.init.SetPortrait;

public class CalenderView extends SetPortrait {
    private static final String TAG = "CalenderView";
    private CalendarPickerView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_calendar_picker);

        Toolbar tbCalender = (Toolbar) findViewById(R.id.tbCalender);
        setSupportActionBar(tbCalender);

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);

        calendar = (CalendarPickerView) findViewById(R.id.mainCalenderView);
        calendar.init(lastYear.getTime(), nextYear.getTime()) //
                .inMode(SelectionMode.SINGLE) //
                .withSelectedDate(new Date());
        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Intent i = new Intent(CalenderView.this, OverView.class);
                i.putExtra("noteDate", calendar.getSelectedDate().getTime());
                startActivity(i);
            }

            @Override
            public void onDateUnselected(Date date) {
            }
        });
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
            startActivity(new Intent(CalenderView.this, Settings.class));
        }
        if (id == R.id.calender) {
            startActivity(new Intent(CalenderView.this, CalenderView.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (customBackground) {
            findViewById(R.id.mainCalenderView).setBackgroundColor(Integer.parseInt(backgroundColor));
        } else {
            findViewById(R.id.mainCalenderView).setBackgroundColor(Color.WHITE);
        }
    }
}
