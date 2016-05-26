package bupt.icyicarus.nevernote.noteList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import bupt.icyicarus.nevernote.PublicVariableAndMethods;
import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.db.NeverNoteDB;
import bupt.icyicarus.nevernote.init.Initialization;
import bupt.icyicarus.nevernote.receiver.NeverNoteAlarmReceiver;

public class NoteList extends Initialization {
    private MaterialListView mlvNoteList;
    private ArrayList<NoteListCellData> noteListCellDataArrayList = null;
    private long noteDate = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_note_list);

        Toolbar tbNoteList = (Toolbar) findViewById(R.id.tbNoteList);
        setSupportActionBar(tbNoteList);

        mlvNoteList = (MaterialListView) findViewById(R.id.mlvNoteList);
        mlvNoteList.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Card card, int position) {

            }

            @Override
            public void onItemLongClick(@NonNull Card card, int position) {
                final NoteListCellData data = (NoteListCellData) card.getTag();
                final StringBuilder sb = new StringBuilder();
                final ContentValues cv = new ContentValues();
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                            @Override
                            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                                sb.append(year).append("-").append(monthOfYear + 1).append("-").append(dayOfMonth).append(" ");
                                cv.put(NeverNoteDB.COLUMN_NAME_ALARM_YEAR, year + "");
                                int tmp = monthOfYear + 1;
                                cv.put(NeverNoteDB.COLUMN_NAME_ALARM_MONTH, tmp + "");
                                cv.put(NeverNoteDB.COLUMN_NAME_ALARM_DAY, dayOfMonth + "");

                                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                                        .setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                                                sb.append(hourOfDay).append(":").append(minute).append(":00");
                                                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                                Intent i = new Intent(NoteList.this, NeverNoteAlarmReceiver.class);
                                                i.putExtra(NeverNoteDB.COLUMN_NAME_NOTE_NAME, data.name);
                                                i.putExtra(NeverNoteDB.COLUMN_NAME_NOTE_DATE, data.date);
                                                i.putExtra(NeverNoteDB.COLUMN_NAME_NOTE_CONTENT, data.content);
                                                PendingIntent pi = PendingIntent.getBroadcast(NoteList.this, 0, i, 0);

                                                cv.put(NeverNoteDB.COLUMN_NAME_ALARM_HOUR, hourOfDay + "");
                                                cv.put(NeverNoteDB.COLUMN_NAME_ALARM_MINUTE, minute + "");
                                                cv.put(NeverNoteDB.COLUMN_NAME_ALARM_NAME, data.name);
                                                cv.put(NeverNoteDB.COLUMN_NAME_ALARM_CONTENT, data.content);

                                                NeverNoteDB db = new NeverNoteDB(NoteList.this);
                                                SQLiteDatabase dbWrite = db.getWritableDatabase();
                                                dbWrite.insert(NeverNoteDB.TABLE_NAME_ALARM, null, cv);
//                                                try {
//                                                    am.set(AlarmManager.RTC_WAKEUP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sb.toString()).getTime(), pi);
//                                                } catch (ParseException e) {
//                                                    e.printStackTrace();
//                                                }
                                                am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 10000, pi);
                                            }
                                        })
                                        .setStartTime(new Date().getHours(), new Date().getMinutes());
                                rtpd.show(getSupportFragmentManager(), "Time Picker Fragment");
                            }
                        })
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setDateRange(new MonthAdapter.CalendarDay(), null);
                cdp.show(getSupportFragmentManager(), "Date Picker Fragment");
            }
        });

        noteDate = getIntent().getLongExtra("noteDate", -1);

        findViewById(R.id.fabmNoteListAddNote).setOnClickListener(fabClickHandler);
        findViewById(R.id.fabmNoteListAddPhoto).setOnClickListener(fabClickHandler);
        findViewById(R.id.fabmNoteListAddAudio).setOnClickListener(fabClickHandler);
        findViewById(R.id.fabmNoteListAddVideo).setOnClickListener(fabClickHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PublicVariableAndMethods.refreshNoteArrayList(NoteList.this, mlvNoteList, getResources(), noteListCellDataArrayList, noteDate);
        if (customBackground) {
            findViewById(R.id.cNoteList).setBackgroundColor(Integer.parseInt(backgroundColor));
        } else {
            findViewById(R.id.cNoteList).setBackgroundColor(Color.WHITE);
        }
    }
}
