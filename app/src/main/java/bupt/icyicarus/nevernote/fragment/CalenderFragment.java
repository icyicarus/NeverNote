package bupt.icyicarus.nevernote.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;

import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.views.OverView;

public class CalenderFragment extends Fragment {
    private CalendarPickerView calendar;

    public static CalenderFragment newInstance() {
        return new CalenderFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calender, container, false);
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);

        calendar = (CalendarPickerView) root.findViewById(R.id.mainCalenderView);
        calendar.init(lastYear.getTime(), nextYear.getTime()) //
                .inMode(CalendarPickerView.SelectionMode.SINGLE) //
                .withSelectedDate(new Date());
        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Intent i = new Intent(getContext(), OverView.class);
                i.putExtra("noteDate", calendar.getSelectedDate().getTime());
                startActivity(i);
            }

            @Override
            public void onDateUnselected(Date date) {
            }
        });
        return root;
    }

    public int getLastDay(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (year % 4 == 0) {
                    if (year % 100 == 0) {
                        if (year % 400 == 0) {
                            return 27;
                        } else
                            return 28;
                    } else
                        return 27;
                } else {
                    return 28;
                }
            default:
                return 0;
        }
    }
}
