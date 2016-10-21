package bupt.icyicarus.nevernote.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import bupt.icyicarus.nevernote.db.NeverNoteDB;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            NeverNoteDB db = new NeverNoteDB(context);
            SQLiteDatabase dbRead = db.getReadableDatabase();
            Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_ALARM, null, null, null, null, null, null);
            StringBuilder sb = new StringBuilder();
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            while (c.moveToNext()) {
                sb.append(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_ALARM_YEAR)))
                        .append("-")
                        .append(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_ALARM_MONTH)))
                        .append("-").append(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_ALARM_DAY)))
                        .append(" ").append(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_ALARM_HOUR)))
                        .append(":").append(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_ALARM_MINUTE)))
                        .append(":00");
                Intent i = new Intent(context, NeverNoteAlarmReceiver.class);
                int alarmID = Integer.parseInt(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_ID)));
                int noteID = Integer.parseInt(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_ALARM_NOTEID)));
                i.putExtra("alarmID", alarmID);
                i.putExtra("noteID", noteID);
                i.putExtra("noteName", c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_ALARM_NAME)));
                i.putExtra("noteContent", c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_ALARM_CONTENT)));
                PendingIntent pi = PendingIntent.getBroadcast(context, alarmID, i, 0);

                try {
                    am.set(AlarmManager.RTC_WAKEUP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(sb.toString()).getTime(), pi);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
