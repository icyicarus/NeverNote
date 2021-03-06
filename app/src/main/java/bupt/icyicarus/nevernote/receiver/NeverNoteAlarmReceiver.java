package bupt.icyicarus.nevernote.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import bupt.icyicarus.nevernote.alarm.NeverNoteAlarm;

public class NeverNoteAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, NeverNoteAlarm.class);
        context.startActivity(intent);
    }
}
