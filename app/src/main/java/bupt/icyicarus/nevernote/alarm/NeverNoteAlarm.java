package bupt.icyicarus.nevernote.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.db.NeverNoteDB;

public class NeverNoteAlarm extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(NeverNoteAlarm.this);
        PendingIntent pendingIntent = PendingIntent.getActivity(NeverNoteAlarm.this, 0, new Intent(NeverNoteAlarm.this, NeverNoteAlarm.class), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setTicker("setTicker")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(getIntent().getStringExtra(NeverNoteDB.COLUMN_NAME_NOTE_NAME))
                .setContentText(getIntent().getStringExtra(NeverNoteDB.COLUMN_NAME_NOTE_DATE));
        Notification notification = builder.getNotification();
        nm.notify(1, notification);
        finish();
    }
}
