package com.example.pickviewtest.pickview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.pickviewtest.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.pickviewtest.pickview.TimePickerPopWin.format2LenStr;

public class PresetSelectView extends RelativeLayout {


    private LoopView lvDay, lvHour, lvMinute;

    private long selectInitTime = 0;
    private long selectResultTime = 0;

    private int todayDayStart = 0;
    private int todayHourStart = 0;
    private int todayMinStart = 0;

    private int tomorrowHourEnd = 0;
    private int tomorrowMinEnd = 0;

    private boolean isToday = true;
    private boolean isDayChanged = false;
    private int dayOffset = 0;
    private int hourOffset = 0;
    private int minuteOffset = 0;

    private int dayPos = 0;
    private int hourPos = 0;
    private int minutePos = 0;


    private int lastDayPos = -1;
    private int lastHourPos = -1;
    private int lastMinutepos = -1;


    List<String> hourList = new ArrayList();
    List<String> minList = new ArrayList();
    List<String> dayList = new ArrayList();


    private UpdateSelectTimeListener updateSelectTimeListener;




    public PresetSelectView(Context context) {
        super(context);
        init(context);
    }


    public PresetSelectView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }


    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.wallbox_preset_select_layout,this,true);
        lvDay = findViewById(R.id.wallbox_picker_day);
        lvHour = findViewById(R.id.wallbox_picker_hour);
        lvMinute = findViewById(R.id.wallbox_picker_minute);

        initListener();

        long currentTime = System.currentTimeMillis();
        initTime(currentTime);

    }


    private void initListener() {

        lvDay.setLoopListener(new LoopScrollListener() {
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


        lvHour.setLoopListener(new LoopScrollListener() {
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

        lvMinute.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                minuteOffset = item;
                if(isToday) {
                    minutePos = todayMinStart + minuteOffset;
                } else {
                    minutePos = minuteOffset;
                }
                updateSelectTime();
            }
        });


    }


    public void setUpdateSelectTimeListener(UpdateSelectTimeListener listener) {
        updateSelectTimeListener = listener;
    }


    private void reset() {

        selectResultTime = 0;

        todayDayStart = 0;
        todayHourStart = 0;
        todayMinStart = 0;

        tomorrowHourEnd = 0;
        tomorrowMinEnd = 0;


        isToday = true;
        isDayChanged = false;
        dayOffset = 0;
        hourOffset = 0;
        minuteOffset = 0;

        dayPos = 0;
        hourPos = 0;
        minutePos = 0;


        lastDayPos = -1;
        lastHourPos = -1;
        lastMinutepos = -1;


        hourList.clear();
        minList.clear();
        dayList.clear();
    }


    public void initStartTime(long currentTime) {
        // 开始时间的起始选择时间是当前时间的后15分钟
        selectInitTime = currentTime + 15 * 60 * 1000;
        initTime(currentTime);
    }

    public void initEndTime(long startTime) {
        // 结束时间的起始选择时间是开始时间的后10分钟
        selectInitTime = startTime + 10 * 60 * 1000;
        initTime(startTime);
    }

    public void initTime(long time) {

        reset();

        Calendar calendar = Calendar.getInstance();

        Date selectInitDate = new Date(selectInitTime);
        calendar.setTime(selectInitDate);

        todayDayStart = calendar.get(Calendar.DAY_OF_MONTH);
        todayHourStart = hourPos = calendar.get(Calendar.HOUR_OF_DAY);
        todayMinStart = minutePos = calendar.get(Calendar.MINUTE);



        Date today = new Date(time);
        calendar.setTime(today);

        tomorrowHourEnd = calendar.get(Calendar.HOUR_OF_DAY);
        tomorrowMinEnd = calendar.get(Calendar.MINUTE);


        // 选择最大时间为起始时间的后24小时时间
        long tomorrowTime = time + 24 * 60 * 60 * 1000;
        Date tomorrow = new Date(tomorrowTime);

        Calendar calendarTomorrow = Calendar.getInstance();
        calendarTomorrow.setTime(tomorrow);

        SimpleDateFormat sfMonthDay = new SimpleDateFormat("MMM d");

        dayList.clear();
        dayList.add(sfMonthDay.format(today));
        dayList.add(sfMonthDay.format(tomorrow));

        lvDay.setDataList(dayList);
        lvDay.refreshScrollY();
        lvDay.setInitPosition(0);

        lvHour.setMaxTextHeight(lvDay.getMaxTextHeight());

        lvMinute.setMaxTextHeight(lvDay.getMaxTextHeight());

        refreshHourData();

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

        lvHour.setDataList(hourList);
        lvHour.refreshScrollY();
        lvHour.setInitPosition(0);

        refreshMinuteData();

    }

    private void refreshMinuteData() {

        if(lastHourPos == hourPos) {
            return;
        }
        if(isToday) {
            if(!isDayChanged && lastHourPos != todayHourStart && hourPos != todayHourStart) {
                updateSelectTime();
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
                updateSelectTime();
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


        lvMinute.setDataList(minList);
        lvMinute.refreshScrollY();
        lvMinute.setInitPosition(0);

        minuteOffset = 0;
        lastHourPos = hourPos;
        isDayChanged = false;
        updateSelectTime();
    }


    private long getSelectTime() {
        if(isToday) {
            if(hourOffset == 0) {
                selectResultTime = selectInitTime + hourOffset * 60 * 60 * 1000 + minuteOffset * 60 * 1000;
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, todayHourStart + hourOffset);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MINUTE, minuteOffset);
                calendar.set(Calendar.MILLISECOND, 0);
                selectResultTime = calendar.getTime().getTime();
                if(selectResultTime < selectInitTime) {
                    selectResultTime += 24 * 60 * 60 * 1000;
                }
            }

        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 24);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            selectResultTime = calendar.getTime().getTime() + hourOffset * 60 * 60 * 1000 + minuteOffset * 60 * 1000;
            if(selectResultTime < selectInitTime) {
                selectResultTime += 24 * 60 * 60 * 1000;
            }
        }

        return selectResultTime;
    }


    private void updateSelectTime() {
        if(updateSelectTimeListener != null) {
            Date selectDate = new Date(getSelectTime());
            SimpleDateFormat sfSelectDay = new SimpleDateFormat("MMMM d H:mm");
            String selectResult = sfSelectDay.format(selectDate);
            updateSelectTimeListener.updateSelectTime(selectResultTime,selectResult);
        }
    }







}
