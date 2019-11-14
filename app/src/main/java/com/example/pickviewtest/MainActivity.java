package com.example.pickviewtest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pickviewtest.pickview.LoopScrollListener;
import com.example.pickviewtest.pickview.LoopView;
import com.example.pickviewtest.pickview.PresetSelectView;
import com.example.pickviewtest.pickview.TimePickerPopWin;
import com.example.pickviewtest.pickview.UpdateSelectTimeListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.pickviewtest.pickview.TimePickerPopWin.format2LenStr;

public class MainActivity extends AppCompatActivity {

    private TextView tvShowPick, tvShowCurPick;
    private LoopView hourLoopView;
    private LoopView minuteLoopView;
    private LoopView meridianLoopView;
    private RelativeLayout rlPickContainer;
    private LinearLayout llPickRow;
    private TimePickerPopWin.OnTimePickListener onTimePickListener;

    private PresetSelectView psvWallboxSelect;

    private EditText etStart, etEnd;
    private TextView tvStart, tvEnd, tvShowStart, tvShowEnd;


    private TextView tvDate, tvHour, tvMinute;

    private int todayHourStart = 0;
    private int todayMinStart = 0;

    private int tomorrowHourEnd = 0;
    private int tomorrowMinEnd = 0;


    private boolean isToday = true;
    private boolean isDayChanged = false;
    private int dayOffset = 0;
    private int hourOffset = 0;
    private int minuteOffset = 0;


    private int hourStart = 0;
    private int minStart =0;



    private int dayPos = 0;
    private int hourPos = 0;
    private int minutePos = 0;


    private int lastDayPos = -1;
    private int lastHourPos = -1;
    private int lastMinutepos = -1;


    List<String> hourList = new ArrayList();
    List<String> minList = new ArrayList();
    List<String> dayList = new ArrayList();


    private UpdateSelectTimeListener updateStartTimeListener;
    private UpdateSelectTimeListener updateEndTimeListener;
    private long selectStartTime = 0;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initListener();
        initCurrentPicker();
        initCurrentDateInfo();
    }


    private void init() {

        psvWallboxSelect = (PresetSelectView) findViewById(R.id.psv_wallbox_select);

        tvShowPick = findViewById(R.id.tvShowPick);
        tvShowCurPick = findViewById(R.id.tvShowCurPick);


        etStart = findViewById(R.id.et_start);
        etEnd = findViewById(R.id.et_end);

        tvStart = findViewById(R.id.tv_set_start);
        tvEnd = findViewById(R.id.tv_set_end);

        tvShowStart = findViewById(R.id.tv_show_start);
        tvShowEnd = findViewById(R.id.tv_show_end);

    }

    private void initListener() {

        tvShowPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerPopWin timePickerPopWin=new TimePickerPopWin.Builder(MainActivity.this, new TimePickerPopWin.OnTimePickListener() {
                    @Override
                    public void onTimePickCompleted(int hour, int minute, String AM_PM, String time) {
                        Toast.makeText(MainActivity.this, time, Toast.LENGTH_SHORT).show();
                    }
                }).textConfirm("CONFIRM")
                        .textCancel("CANCEL")
                        .btnTextSize(16)
                        .viewTextSize(25)
                        .colorCancel(Color.parseColor("#999999"))
                        .colorConfirm(Color.parseColor("#009900"))
                        .build();
                timePickerPopWin.showPopWin(MainActivity.this);
            }
        });

        tvShowCurPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlPickContainer.setVisibility(View.VISIBLE);
            }
        });


        updateStartTimeListener = new UpdateSelectTimeListener() {
            @Override
            public void updateSelectTime(long selectTime, String selectTimeShow) {
                selectStartTime = selectTime;
                if(etStart != null) {
                    etStart.setText(selectTimeShow);
                }
            }
        };

        updateEndTimeListener = new UpdateSelectTimeListener() {
            @Override
            public void updateSelectTime(long selectTime, String selectTimeShow) {
                if(etEnd != null) {
                    etEnd.setText(selectTimeShow);
                }
            }
        };

        etStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                psvWallboxSelect.setUpdateSelectTimeListener(updateStartTimeListener);
                psvWallboxSelect.initStartTime(System.currentTimeMillis());
                psvWallboxSelect.setVisibility(View.VISIBLE);
            }
        });

        etEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                psvWallboxSelect.setUpdateSelectTimeListener(updateEndTimeListener);
                psvWallboxSelect.initEndTime(selectStartTime);
            }
        });

        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strStartTime = dayList.get(meridianLoopView.getSelectedItem()) + " " + hourList.get(hourLoopView.getSelectedItem()) + ": "+ minList.get(minuteLoopView.getSelectedItem());
                etStart.setHint(strStartTime);
                rlPickContainer.setVisibility(View.INVISIBLE);
            }
        });

        tvEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strStartTime = dayList.get(meridianLoopView.getSelectedItem()) + " " + hourList.get(hourLoopView.getSelectedItem()) + ": "+ minList.get(minuteLoopView.getSelectedItem());
                etEnd.setHint(strStartTime);
                rlPickContainer.setVisibility(View.INVISIBLE);
            }
        });


        tvShowStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initStartTime();
                rlPickContainer.setVisibility(View.VISIBLE);

            }
        });




    }

    private void initCurrentPicker() {

        rlPickContainer = findViewById(R.id.rl_picker_space);
        llPickRow = findViewById(R.id.ll_picker_row);
        hourLoopView = (LoopView) findViewById(R.id.picker_hour_main);
        minuteLoopView = (LoopView) findViewById(R.id.picker_minute_main);
        meridianLoopView = (LoopView) findViewById(R.id.picker_meridian_main);


        onTimePickListener = new TimePickerPopWin.OnTimePickListener() {
            @Override
            public void onTimePickCompleted(int hour, int minute, String AM_PM, String time) {
                Toast.makeText(MainActivity.this, time, Toast.LENGTH_SHORT).show();
            }
        };

        hourLoopView.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                hourOffset = item;
                if(isToday) {
                    hourPos = todayHourStart + hourOffset;
                } else {
                    hourPos = hourOffset;
                }
                refreshMinuteData();
            }
        });

        minuteLoopView.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                minuteOffset = item;
                if(isToday) {
                    minutePos = todayMinStart + minuteOffset;
                } else {
                    minutePos = minuteOffset;
                }
            }
        });

        meridianLoopView.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                dayOffset = item;
                dayPos = dayOffset;
                isDayChanged = lastDayPos != dayPos;
                if(isDayChanged) {
                    refreshHourData();
                }
            }
        });






    }


    private void initStartTime() {

        // 获取当前时间15分钟后的时间
        long currentTime = System.currentTimeMillis() + 15 * 60 * 1000;

        Date today = new Date(currentTime);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        todayHourStart = hourPos = calendar.get(Calendar.HOUR_OF_DAY);
        todayMinStart = minutePos = calendar.get(Calendar.MINUTE);

        tomorrowHourEnd = todayHourStart;
        tomorrowMinEnd = todayMinStart;


        // 获取当前时间15分钟后一天的时间
        long currentTomorrowTime = System.currentTimeMillis() + 15 * 60 * 1000 + 24 * 60 * 60 * 1000;
        Date tomorrow = new Date(currentTomorrowTime);

        Calendar calendarTomorrow = Calendar.getInstance();
        calendarTomorrow.setTime(tomorrow);

        SimpleDateFormat sfMonthDay = new SimpleDateFormat("MMM d");

        dayList.add(sfMonthDay.format(today));
        dayList.add(sfMonthDay.format(tomorrow));

        meridianLoopView.setDataList(dayList);
        meridianLoopView.setInitPosition(dayPos);

        hourLoopView.setMaxTextHeight(meridianLoopView.getMaxTextHeight());

        minuteLoopView.setMaxTextHeight(meridianLoopView.getMaxTextHeight());

        refreshHourData();


        llPickRow.setVisibility(View.VISIBLE);


    }


    private void refreshHourData() {

        isToday = (dayOffset == 0);

        lastHourPos = -1;
        hourOffset = 0;
        minuteOffset = 0;
        minutePos = 0;
        if(isToday) {
            hourPos = todayHourStart + hourOffset;
        } else {
            hourPos = hourOffset;
        }

        hourList.clear();
        if(isToday) {
            for (int i = todayHourStart; i <24; i++) {
                hourList.add(format2LenStr(i));
            }
        } else {
            for (int i = 0; i <= tomorrowHourEnd; i++) {
                hourList.add(format2LenStr(i));
            }
        }

        hourLoopView.setDataList(hourList);
        hourLoopView.refreshScrollY();
        hourLoopView.setInitPosition(0);

        refreshMinuteData();

    }


    private void refreshMinuteData() {

        if(lastHourPos == hourPos) {
            return;
        }
        if(isToday) {
            if(!isDayChanged && lastHourPos != todayHourStart && hourPos != todayHourStart) {
                return;
            }
            minList.clear();
            if(hourPos == todayHourStart) {
                for (int i = todayMinStart; i <60; i++) {
                    minList.add(format2LenStr(i));
                }
            } else {
                for (int i = 0; i <60; i++) {
                    minList.add(format2LenStr(i));
                }
            }

        } else {
            if(!isDayChanged && lastHourPos != tomorrowHourEnd && hourPos != tomorrowHourEnd) {
                return;
            }
            minList.clear();
            if(hourPos == tomorrowHourEnd) {
                for (int i = 0; i <tomorrowMinEnd; i++) {
                    minList.add(format2LenStr(i));
                }
            } else {
                for (int i = 0; i <60; i++) {
                    minList.add(format2LenStr(i));
                }
            }
        }


        minuteLoopView.setDataList(minList);
        minuteLoopView.refreshScrollY();
        minuteLoopView.setInitPosition(0);

        lastHourPos = hourPos;
        isDayChanged = false;
    }




    private void initCurrentDateInfo() {

        tvDate = findViewById(R.id.tv_date);
        tvHour = findViewById(R.id.tv_hour);
        tvMinute = findViewById(R.id.tv_minute);





        // 获取当前时间15分钟后的时间
        long currentTime = System.currentTimeMillis();

        // 获取当前时间15分钟后一天的时间
        long currentTomorrowTime = System.currentTimeMillis() + 15 * 60 * 1000 + 24 * 60 * 60 * 1000;

        Date d = new Date(currentTime);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        SimpleDateFormat sfMonthDay = new SimpleDateFormat("MMM d");

        tvDate.setText(sfMonthDay.format(d));

        String strHour = "  " + hour ;
        tvHour.setText(strHour);

        String strMinute = "  " + minute;
        tvMinute.setText(strMinute);



    }




}
