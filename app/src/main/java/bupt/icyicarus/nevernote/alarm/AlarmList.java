package bupt.icyicarus.nevernote.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;

import java.util.ArrayList;

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
        setContentView(R.layout.aty_alarm_list);

        Toolbar tbAlarmList = (Toolbar) findViewById(R.id.tbAlarmList);
        setSupportActionBar(tbAlarmList);

        mlvAlarmList = (MaterialListView) findViewById(R.id.mlvAlarmList);
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
                                    i.setFlags(alarmInfo.getId());
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
                                    //TODO
                                    Toast.makeText(AlarmList.this, "EDIT", Toast.LENGTH_SHORT).show();
                                }
                            }))
                    .endConfig()
                    .build();
            mlvAlarmList.getAdapter().add(card);
        }
    }
}
