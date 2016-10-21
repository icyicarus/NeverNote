package bupt.icyicarus.nevernote.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.db.NeverNoteDB;
import bupt.icyicarus.nevernote.init.Initialization;
import bupt.icyicarus.nevernote.receiver.NeverNoteAlarmReceiver;

public class AlarmList extends Initialization {

    NeverNoteDB db;
    SQLiteDatabase dbRead, dbWrite;
    private MaterialListView mlvAlarmList;
    private ArrayList<AlarmInfo> alarmInfoArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_alarm_list);

        Toolbar tbAlarmList = (Toolbar) findViewById(R.id.toolBarAlarmList);
        setSupportActionBar(tbAlarmList);
        needMenu = true;

        mlvAlarmList = (MaterialListView) findViewById(R.id.materialListViewAlarmList);
        db = new NeverNoteDB(this);
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();
        alarmInfoArrayList = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAlarmList();
    }

    private void refreshAlarmList() {
        mlvAlarmList.getAdapter().clearAll();
        if (alarmInfoArrayList != null) {
            alarmInfoArrayList.clear();
        } else {
            alarmInfoArrayList = new ArrayList<>();
        }

        Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_ALARM, null, null, null, null, null, null, null);
        while (c.moveToNext()) {
            alarmInfoArrayList.add(new AlarmInfo(
                    c.getInt(c.getColumnIndex(NeverNoteDB.COLUMN_ID)),
                    c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_ALARM_YEAR)),
                    c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_ALARM_MONTH)),
                    c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_ALARM_DAY)),
                    c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_ALARM_HOUR)),
                    c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_ALARM_MINUTE)),
                    c.getInt(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_ALARM_NOTEID)),
                    c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_ALARM_NAME)),
                    c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_ALARM_CONTENT))
            ));
        }

        for (final AlarmInfo alarmInfo : alarmInfoArrayList) {
            StringBuilder sb = new StringBuilder();
            sb.append(alarmInfo.getYear()).append("-").append(alarmInfo.getMonth()).append("-").append(alarmInfo.getDay()).append(" ").append(alarmInfo.getHour()).append(":").append(alarmInfo.getMinute()).append(":00");
            Card card = new Card.Builder(this)
                    .setTag(alarmInfo)
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_basic_buttons_card)
                    .setTitle(alarmInfo.getName())
                    .setDescription(sb.toString())
                    .addAction(R.id.left_text_button, new TextViewAction(this)
                            .setText("Delete")
                            .setTextResourceColor(R.color.black_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                    dbWrite.delete(NeverNoteDB.TABLE_NAME_ALARM, NeverNoteDB.COLUMN_ID + "=?", new String[]{alarmInfo.getId() + ""});
                                    refreshAlarmList();

                                    Intent i = new Intent(getApplicationContext(), NeverNoteAlarmReceiver.class);
                                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), alarmInfo.getId(), i, 0);
                                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                                    am.cancel(pi);
                                }
                            }))
                    .addAction(R.id.right_text_button, new TextViewAction(this)
                            .setText("Edit")
                            .setTextResourceColor(R.color.orange_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                    final StringBuilder sb = new StringBuilder();
                                    final ContentValues cv = new ContentValues();
                                    Intent i = new Intent(AlarmList.this, NeverNoteAlarmReceiver.class);
                                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), alarmInfo.getId(), i, 0);
                                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                                    am.cancel(pi);
                                    CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment().setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                                            sb.append(year).append("-").append(monthOfYear + 1).append("-").append(dayOfMonth).append(" ");
                                            cv.put(NeverNoteDB.COLUMN_NAME_ALARM_YEAR, year + "");
                                            int tmp = monthOfYear + 1;
                                            cv.put(NeverNoteDB.COLUMN_NAME_ALARM_MONTH, tmp + "");
                                            cv.put(NeverNoteDB.COLUMN_NAME_ALARM_DAY, dayOfMonth + "");

                                            RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment().setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                                                @Override
                                                public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                                                    cv.put(NeverNoteDB.COLUMN_NAME_ALARM_HOUR, hourOfDay + "");
                                                    cv.put(NeverNoteDB.COLUMN_NAME_ALARM_MINUTE, minute + "");
                                                    cv.put(NeverNoteDB.COLUMN_NAME_ALARM_NOTEID, alarmInfo.getNoteID() + "");
                                                    cv.put(NeverNoteDB.COLUMN_NAME_ALARM_NAME, alarmInfo.getName());
                                                    cv.put(NeverNoteDB.COLUMN_NAME_ALARM_CONTENT, alarmInfo.getContent());

                                                    NeverNoteDB db = new NeverNoteDB(AlarmList.this);
                                                    SQLiteDatabase dbWrite = db.getWritableDatabase();
                                                    dbWrite.update(NeverNoteDB.TABLE_NAME_ALARM, cv, NeverNoteDB.COLUMN_ID + "=?", new String[]{alarmInfo.getId() + ""});

                                                    sb.append(hourOfDay).append(":").append(minute).append(":00");
                                                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                                    Intent i = new Intent(AlarmList.this, NeverNoteAlarmReceiver.class);
                                                    i.putExtra("alarmID", alarmInfo.getId());
                                                    i.putExtra("noteID", alarmInfo.getNoteID());
                                                    i.putExtra("noteName", alarmInfo.getName());
                                                    i.putExtra("noteContent", alarmInfo.getContent());
                                                    PendingIntent pi = PendingIntent.getBroadcast(AlarmList.this, alarmInfo.getId(), i, 0);

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
                            }))
                    .endConfig()
                    .build();
            mlvAlarmList.getAdapter().add(card);
        }
    }
}
