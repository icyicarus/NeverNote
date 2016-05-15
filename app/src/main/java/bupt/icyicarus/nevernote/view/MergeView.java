package bupt.icyicarus.nevernote.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.fragment.CalenderFragment;
import bupt.icyicarus.nevernote.fragment.NoteListFragment;
import bupt.icyicarus.nevernote.init.SetPortrait;

public class MergeView extends SetPortrait {
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

        FloatingActionButton fabMergeView = (FloatingActionButton) findViewById(R.id.fabMergeView);
        assert fabMergeView != null;
        fabMergeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
