package bupt.icyicarus.nevernote.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.db.NeverNoteDB;
import bupt.icyicarus.nevernote.view.NoteView;

public class NeverNoteAlarm extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(NeverNoteAlarm.this);
        Intent i = getIntent();
        i.setClass(NeverNoteAlarm.this, NoteView.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(NeverNoteAlarm.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setTicker("setTicker")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(getIntent().getStringExtra("noteName"))
                .setContentText(getIntent().getStringExtra("noteContent"));
        Notification notification = builder.getNotification();
        nm.notify(1, notification);
        NeverNoteDB db = new NeverNoteDB(this);
        SQLiteDatabase dbWrite = db.getWritableDatabase();
        dbWrite.delete(NeverNoteDB.TABLE_NAME_ALARM, NeverNoteDB.COLUMN_ID + "=?", new String[]{getIntent().getIntExtra("alarmID", -1) + ""});
        finish();
    }
}
