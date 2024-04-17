package com.chezi008.afplayer.widget.afcalendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chezi008.afplayer.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AFCalendarView extends FrameLayout {

    private RecyclerView recyclerView;
    private CalendarAdapter calendarAdapter;
    private List<Date> mData;

    public AFCalendarView(@NonNull Context context) {
        super(context);
    }

    public AFCalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_af_calendar, this);
        initView();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerView);

        mData = generateDates();
        calendarAdapter = new CalendarAdapter(mData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.addItemDecoration(new AFCalendarDecoration(new AFCalendarDecoration.GroupListener() {
            @Override
            public String getGroupName(int position) {
                if (position > mData.size() - 1) {
                    return "";
                }
                Date date = mData.get(position);
                Calendar instance = Calendar.getInstance();
                instance.setTime(date);
                int month = instance.get(Calendar.MONTH)+1;
                return month + "月";
            }
        }));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(calendarAdapter);
    }

    private List<Date> generateDates() {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i <= 100; i++) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }

        return dates;
    }
}
