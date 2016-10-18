package bupt.icyicarus.nevernote.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import bupt.icyicarus.nevernote.noteList.NoteListCellData;
import bupt.icyicarus.nevernote.receiver.NeverNoteAlarmReceiver;

public class NoteListFragment extends Fragment {

    private MaterialListView mlvNoteListFragment;
    private ArrayList<NoteListCellData> noteListCellDataArrayList = null;

    public static NoteListFragment newInstance() {
        return new NoteListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_note_list, container, false);
        mlvNoteListFragment = (MaterialListView) root.findViewById(R.id.fragmentNoteList);

        mlvNoteListFragment.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Card card, int position) {

            }

            @Override
            public void onItemLongClick(@NonNull final Card card, int position) {
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

                                                NeverNoteDB db = new NeverNoteDB(getContext());
                                                SQLiteDatabase dbWrite = db.getWritableDatabase();
                                                SQLiteDatabase dbRead = db.getReadableDatabase();
                                                dbWrite.insert(NeverNoteDB.TABLE_NAME_ALARM, null, cv);
                                                Cursor c = dbRead.rawQuery("SELECT last_insert_rowid()", null);
                                                c.moveToFirst();
                                                int alarmID = c.getInt(0);
                                                c.close();

                                                sb.append(hourOfDay).append(":").append(minute).append(":00");
                                                AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                                                Intent i = new Intent(getContext(), NeverNoteAlarmReceiver.class);
                                                i.putExtra("alarmID", alarmID);
                                                i.putExtra("noteID", data.id);
                                                i.putExtra("noteName", data.name);
                                                i.putExtra("noteContent", data.content);
                                                PendingIntent pi = PendingIntent.getBroadcast(getContext(), alarmID, i, 0);

                                                try {
                                                    am.set(AlarmManager.RTC_WAKEUP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sb.toString()).getTime(), pi);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                        .setStartTime(new Date().getHours(), new Date().getMinutes());
                                rtpd.show(getFragmentManager(), "Time Picker Fragment");
                            }
                        })
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setDateRange(new MonthAdapter.CalendarDay(), null);
                cdp.show(getFragmentManager(), "Date Picker Fragment");
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        PublicVariableAndMethods.refreshNoteArrayList(getContext(), mlvNoteListFragment, getResources(), noteListCellDataArrayList, -1);
        super.onResume();
    }
}
