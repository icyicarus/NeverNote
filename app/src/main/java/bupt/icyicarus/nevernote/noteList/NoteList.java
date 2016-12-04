package bupt.icyicarus.nevernote.noteList;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import bupt.icyicarus.nevernote.PublicVariableAndMethods;
import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.db.NeverNoteDB;
import bupt.icyicarus.nevernote.init.Initialization;
import bupt.icyicarus.nevernote.receiver.NeverNoteAlarmReceiver;

public class NoteList extends Initialization {
    private MaterialListView materialListViewNoteListView;
    private ArrayList<NoteListCellData> noteListCellDataArrayList = null;
    private long noteDate = -1;
    private long lastClick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_note_list);

        Toolbar tbNoteList = (Toolbar) findViewById(R.id.toolBarNoteListView);
        setSupportActionBar(tbNoteList);
        needMenu = true;

        materialListViewNoteListView = (MaterialListView) findViewById(R.id.materialListViewNoteListView);
        materialListViewNoteListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull final Card card, int position) {
                if ((System.currentTimeMillis() - lastClick) > 1000) {
                    lastClick = System.currentTimeMillis();
                } else {
                    assert card.getTag() != null;
                    LinkedList<String> noteMedias = ((NoteListCellData) card.getTag()).mediaList;
                    final ArrayList<Uri> mediaUris = new ArrayList<>();
                    if (noteMedias != null && !noteMedias.isEmpty()) {
                        for (String str : noteMedias) {
                            mediaUris.add(FileProvider.getUriForFile(NoteList.this, getApplicationContext().getPackageName() + ".provider", new File(str)));
                        }
                    }
                    final EditText emailField = new EditText(NoteList.this);
                    emailField.setHint("email@example.com");
                    emailField.setInputType(32);
                    new AlertDialog.Builder(NoteList.this).setTitle("Share to:")
                            .setView(emailField, 50, 0, 50, 0)
                            .setCancelable(false)
                            .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (mediaUris.isEmpty()) {
                                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                                        emailIntent.setData(Uri.parse("mailto:" + emailField.getText()));
                                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, ((NoteListCellData) card.getTag()).name);
                                        emailIntent.putExtra(Intent.EXTRA_TEXT, ((NoteListCellData) card.getTag()).content);
                                        startActivity(emailIntent);
                                    } else {
                                        String[] emailRecipients = {String.valueOf(emailField.getText())};
                                        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailRecipients);
                                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, ((NoteListCellData) card.getTag()).name);
                                        emailIntent.putExtra(Intent.EXTRA_TEXT, ((NoteListCellData) card.getTag()).content);
                                        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, mediaUris);
                                        emailIntent.setType("*/*");
                                        startActivity(emailIntent);
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", null).show();
                }
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

                                RadialTimePickerDialogFragment radialTimePickerDialogFragment = new RadialTimePickerDialogFragment()
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
                                                    am.set(AlarmManager.RTC_WAKEUP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(sb.toString()).getTime(), pi);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                        .setStartTime(new Date().getHours(), new Date().getMinutes());
                                radialTimePickerDialogFragment.show(getSupportFragmentManager(), "Time Picker Fragment");
                            }
                        })
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setDateRange(new MonthAdapter.CalendarDay(), null);
                cdp.show(getSupportFragmentManager(), "Date Picker Fragment");
            }
        });

        noteDate = getIntent().getLongExtra("noteDate", -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        setTitle("Notes on " + sdf.format(new Date(noteDate)));

        findViewById(R.id.floatingActionMenuNoteListViewAddNote).setOnClickListener(fabClickHandler);
        findViewById(R.id.floatActionMenuNoteListViewAddPhoto).setOnClickListener(fabClickHandler);
        findViewById(R.id.floatingActionMenuNoteListViewAddAudio).setOnClickListener(fabClickHandler);
        findViewById(R.id.floatingActionMenuNoteListViewAddVideo).setOnClickListener(fabClickHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PublicVariableAndMethods.refreshNoteArrayList(NoteList.this, materialListViewNoteListView, getResources(), noteListCellDataArrayList, noteDate);

        if (ActivityCompat.checkSelfPermission(NoteList.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            findViewById(R.id.floatingActionMenuNoteListViewAddAudio).setEnabled(false);
        }
    }
}
