package bupt.icyicarus.nevernote.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.db.NeverNoteDB;
import bupt.icyicarus.nevernote.noteList.NoteList;

public class CalenderFragment extends Fragment {
    public static CalenderFragment newInstance() {
        return new CalenderFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calender, container, false);
        CaldroidFragment caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar calendar = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, calendar.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, calendar.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        NeverNoteDB db = new NeverNoteDB(getContext());
        SQLiteDatabase dbRead = db.getReadableDatabase();
        Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_NOTES, null, null, null, null, null, null, null);
        while (c.moveToNext()) {
            String dateString = c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_DATE));
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateString);
                ColorDrawable blue = new ColorDrawable(getResources().getColor(R.color.blue));
                caldroidFragment.setBackgroundDrawableForDate(blue, date);
                caldroidFragment.setTextColorForDate(R.color.white, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        FragmentTransaction t = getChildFragmentManager().beginTransaction();
        t.replace(R.id.fragmentCalenderView, caldroidFragment);
        t.commit();
        CaldroidListener caldroidListener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                Intent i = new Intent(getContext(), NoteList.class);
                i.putExtra("noteDate", date.getTime());
                startActivity(i);
            }
        };

        c.close();
        caldroidFragment.setCaldroidListener(caldroidListener);
        return root;
    }
}
