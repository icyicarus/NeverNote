package bupt.icyicarus.nevernote.view;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import bupt.icyicarus.nevernote.PublicVariableAndMethods;
import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.db.NeverNoteDB;
import bupt.icyicarus.nevernote.fragment.CalenderFragment;
import bupt.icyicarus.nevernote.fragment.NoteListFragment;
import bupt.icyicarus.nevernote.init.Initialization;

public class MergeView extends Initialization {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_merge_view);
        Toolbar dvToolbar = (Toolbar) findViewById(R.id.tbMergeView);
        setSupportActionBar(dvToolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.vpMergeView);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout dvTabLayout = (TabLayout) findViewById(R.id.tlMergeView);
        dvTabLayout.setupWithViewPager(mViewPager);


        findViewById(R.id.fabmMergeViewAddNote).setOnClickListener(fabClickHandler);
        findViewById(R.id.fabmMergeViewAddPhoto).setOnClickListener(fabClickHandler);
        findViewById(R.id.fabmMergeViewAddAudio).setOnClickListener(fabClickHandler);
        findViewById(R.id.fabmMergeViewAddVideo).setOnClickListener(fabClickHandler);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        NeverNoteDB db;
        SQLiteDatabase dbRead, dbWrite;
        db = new NeverNoteDB(this);
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();
        switch (requestCode) {
            case PublicVariableAndMethods.REQUEST_CODE_GET_PHOTO:
            case PublicVariableAndMethods.REQUEST_CODE_GET_VIDEO:
            case PublicVariableAndMethods.REQUEST_CODE_GET_AUDIO:
                if (resultCode == RESULT_OK) {
                    if (haveTodayNote != -1) {
                        ContentValues cv = new ContentValues();
                        cv.put(NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID, haveTodayNote);
                        cv.put(NeverNoteDB.COLUMN_NAME_MEDIA_PATH, f.getAbsolutePath());
                        dbWrite.insert(NeverNoteDB.TABLE_NAME_MEDIA, null, cv);
                    } else {
//                        ContentValues cv = new ContentValues();
//                        String newNoteDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//                        cv.put(NeverNoteDB.COLUMN_NAME_NOTE_DATE, newNoteDate);
//                        Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_NOTES, null, NeverNoteDB.COLUMN_NAME_NOTE_DATE + "=?", new String[]{newNoteDate + ""}, null, null, null, null);
//                        int id = -1;
//                        while (c.moveToNext()) {
//                            id = c.getInt(c.getColumnIndex(NeverNoteDB.COLUMN_ID));
//                        }
//                        cv.put(NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID, id);
//                        cv.put(NeverNoteDB.COLUMN_NAME_MEDIA_PATH, f.getAbsolutePath());
//                        dbWrite.insert(NeverNoteDB.TABLE_NAME_NOTES, null, cv);
                        Log.e("haveTodayNote", "-1");
                    }
                } else if (f != null) {
                    f.delete();
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return NoteListFragment.newInstance();
            else
                return CalenderFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Overview";
                case 1:
                    return "Calender";
            }
            return null;
        }
    }
}
