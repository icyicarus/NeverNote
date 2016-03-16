package bupt.icyicarus.nevernote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import bupt.icyicarus.nevernote.calenderLib.CalendarPickerView;
import bupt.icyicarus.nevernote.calenderLib.CalendarPickerView.SelectionMode;
import bupt.icyicarus.nevernote.init.SetPortrait;

import static android.widget.Toast.LENGTH_SHORT;

public class SampleTimesSquareActivity extends SetPortrait {
    private static final String TAG = "CalenderView";
    private CalendarPickerView calendar;
    private AlertDialog theDialog;
    private CalendarPickerView dialogView;

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
                Log.e("date", "onDateSelected");
            }

            @Override
            public void onDateUnselected(Date date) {
                Log.e("date", "onDateUnselected");
            }
        });

//        final Button single = (Button) findViewById(R.id.button_single);
//        final Button multi = (Button) findViewById(R.id.button_multi);
//        final Button range = (Button) findViewById(R.id.button_range);
//        final Button displayOnly = (Button) findViewById(R.id.button_display_only);
//        final Button dialog = (Button) findViewById(R.id.button_dialog);
//        single.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                single.setEnabled(false);
//                multi.setEnabled(true);
//                range.setEnabled(true);
//                displayOnly.setEnabled(true);
//
//                calendar.init(lastYear.getTime(), nextYear.getTime()) //
//                        .inMode(SelectionMode.SINGLE) //
//                        .withSelectedDate(new Date());
//            }
//        });
//
//        multi.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                single.setEnabled(true);
//                multi.setEnabled(false);
//                range.setEnabled(true);
//                displayOnly.setEnabled(true);
//
//                Calendar today = Calendar.getInstance();
//                ArrayList<Date> dates = new ArrayList<Date>();
//                for (int i = 0; i < 5; i++) {
//                    today.add(Calendar.DAY_OF_MONTH, 3);
//                    dates.add(today.getTime());
//                }
//                calendar.init(new Date(), nextYear.getTime()) //
//                        .inMode(SelectionMode.MULTIPLE) //
//                        .withSelectedDates(dates);
//            }
//        });
//
//        range.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                single.setEnabled(true);
//                multi.setEnabled(true);
//                range.setEnabled(false);
//                displayOnly.setEnabled(true);
//
//                Calendar today = Calendar.getInstance();
//                ArrayList<Date> dates = new ArrayList<Date>();
//                today.add(Calendar.DATE, 3);
//                dates.add(today.getTime());
//                today.add(Calendar.DATE, 5);
//                dates.add(today.getTime());
//                calendar.init(new Date(), nextYear.getTime()) //
//                        .inMode(SelectionMode.RANGE) //
//                        .withSelectedDates(dates);
//            }
//        });
//
//        displayOnly.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                single.setEnabled(true);
//                multi.setEnabled(true);
//                range.setEnabled(true);
//                displayOnly.setEnabled(false);
//
//                calendar.init(new Date(), nextYear.getTime()) //
//                        .inMode(SelectionMode.SINGLE) //
//                        .withSelectedDate(new Date())
//                        .displayOnly();
//            }
//        });
//
//        dialog.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialogView = (CalendarPickerView) getLayoutInflater().inflate(R.layout.dialog, null, false);
//                dialogView.init(lastYear.getTime(), nextYear.getTime()) //
//                        .withSelectedDate(new Date());
//                theDialog =
//                        new AlertDialog.Builder(SampleTimesSquareActivity.this).setTitle("I'm a dialog!")
//                                .setView(dialogView)
//                                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        dialogInterface.dismiss();
//                                    }
//                                })
//                                .create();
//                theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//                    @Override
//                    public void onShow(DialogInterface dialogInterface) {
//                        Log.d(TAG, "onShow: fix the dimens!");
//                        dialogView.fixDialogDimens();
//                    }
//                });
//                theDialog.show();
//            }
//        });
//
//        findViewById(R.id.done_button).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "Selected time in millis: " + calendar.getSelectedDate().getTime());
//                String toast = "Selected: " + calendar.getSelectedDate().getTime();
//                Toast.makeText(SampleTimesSquareActivity.this, toast, LENGTH_SHORT).show();
//            }
//        });
    }
}
