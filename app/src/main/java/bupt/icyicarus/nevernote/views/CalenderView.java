package bupt.icyicarus.nevernote.views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

import java.util.Calendar;
import java.util.Date;

import bupt.icyicarus.nevernote.EditNote;
import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.init.SetPortrait;

public class CalenderView extends SetPortrait {
    private CalendarPickerView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_calenderview);

        Toolbar tbCalender = (Toolbar) findViewById(R.id.tbCalenderView);
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

        FloatingActionButton fabAddNoteCalender = (FloatingActionButton) findViewById(R.id.fabAddNoteCalender);
        if (fabAddNoteCalender != null) {
            fabAddNoteCalender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CalenderView.this, EditNote.class));
                }
            });
        }
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
