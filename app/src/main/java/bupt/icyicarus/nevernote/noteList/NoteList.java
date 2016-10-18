package bupt.icyicarus.nevernote.noteList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        setContentView(R.layout.view_note_list);

        Toolbar tbNoteList = (Toolbar) findViewById(R.id.toolBarNoteListView);
        setSupportActionBar(tbNoteList);
        needMenu = true;

        mlvNoteList = (MaterialListView) findViewById(R.id.materialListViewNoteListView);
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
                                                cv.put(NeverNoteDB.COLUMN_NAME_ALARM_HOUR, hourOfDay + "");
                                                cv.put(NeverNoteDB.COLUMN_NAME_ALARM_MINUTE, minute + "");
                                                cv.put(NeverNoteDB.COLUMN_NAME_ALARM_NOTEID, data.id + "");
                                                cv.put(NeverNoteDB.COLUMN_NAME_ALARM_NAME, data.name);
                                                cv.put(NeverNoteDB.COLUMN_NAME_ALARM_CONTENT, data.content);

                                                NeverNoteDB db = new NeverNoteDB(getApplicationContext());
                                                SQLiteDatabase dbWrite = db.getWritableDatabase();
                                                SQLiteDatabase dbRead = db.getReadableDatabase();
                                                dbWrite.insert(NeverNoteDB.TABLE_NAME_ALARM, null, cv);
                                                Cursor c = dbRead.rawQuery("SELECT last_insert_rowid()", null);
                                                c.moveToFirst();
                                                int alarmID = c.getInt(0);
                                                c.close();

                                                sb.append(hourOfDay).append(":").append(minute).append(":00");
                                                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                                Intent i = new Intent(getApplicationContext(), NeverNoteAlarmReceiver.class);
                                                i.putExtra("alarmID", alarmID);
                                                i.putExtra("noteID", data.id);
                                                i.putExtra("noteName", data.name);
                                                i.putExtra("noteContent", data.content);
                                                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), alarmID, i, 0);

                                                try {
                                                    am.set(AlarmManager.RTC_WAKEUP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sb.toString()).getTime(), pi);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        setTitle("Notes on " + sdf.format(new Date(noteDate)));

        findViewById(R.id.floatActionMenuNoteListViewAddNote).setOnClickListener(fabClickHandler);
        findViewById(R.id.floatActionMenuNoteListViewAddPhoto).setOnClickListener(fabClickHandler);
        findViewById(R.id.floatActionMenuNoteListViewAddAudio).setOnClickListener(fabClickHandler);
        findViewById(R.id.floatActionMenuNoteListViewAddVideo).setOnClickListener(fabClickHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PublicVariableAndMethods.refreshNoteArrayList(NoteList.this, mlvNoteList, getResources(), noteListCellDataArrayList, noteDate);
    }
}
