package com.example.user.worktime;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.timessquare.CalendarPickerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewRepeatDay extends AppCompatActivity implements CalendarPickerView.OnDateSelectedListener {
    EditText edittxtLabel,edittxtWage,edittxtHour;
    TextView textviewDayNum;
    CalendarPickerView calendarView2;
    int dayCount=0;
    List<Boolean> selectedDays = new ArrayList<Boolean>(Arrays.asList(new Boolean[31]));
    List<Boolean> selectedWeekdays = new ArrayList<Boolean>(Arrays.asList(new Boolean[7])); // Sun , Mon , Tue , ... , Sat !

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // HIDE TITLE
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_new_repeat_day);

        // PREVENT KEYBOARD AUTO POP-UP
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        calendarView2 = (CalendarPickerView) findViewById(R.id.calendarView2);
        ViewGroup.LayoutParams layout = calendarView2.getLayoutParams();
        layout.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        calendarView2.setLayoutParams(layout);
        initCalendar();

        textviewDayNum = (TextView) findViewById(R.id.txtviewDayNum);
        edittxtHour = (EditText) findViewById(R.id.edittxtHour);
        edittxtWage = (EditText) findViewById(R.id.edittxtWage);
        edittxtLabel = (EditText) findViewById(R.id.edittxtLabel);
        edittxtLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = s.toString().length();
                if(count > 5){
                    String SubString = edittxtLabel.getText().toString();
                    SubString = SubString.substring(0,5);
                    edittxtLabel.setText(SubString);
                }
            }
        });

        Collections.fill(selectedDays,Boolean.FALSE);
        Collections.fill(selectedWeekdays,Boolean.FALSE);
    }

    // initialize calendar
    void initCalendar(){
        Calendar month_end = Calendar.getInstance();
        //month_end.set(Calendar.HOUR_OF_DAY,1);
        month_end.set(Calendar.DAY_OF_MONTH, month_end.getActualMaximum(Calendar.DAY_OF_MONTH));
        month_end.add(Calendar.DAY_OF_YEAR,1);
        Calendar month_begin = Calendar.getInstance();
        month_begin.set(Calendar.DAY_OF_MONTH,month_begin.getActualMinimum(Calendar.DAY_OF_MONTH));
//        Date date = new Date();
//        date.setTime(month_begin.getTime());
        calendarView2.init(month_begin.getTime(), month_end.getTime())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE);
        calendarView2.setOnDateSelectedListener(this);
    }

    // onClick for Button
    public void addNewRepeat(View v){
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("RepeatDays");
        Map newRDay = new HashMap();
        String temp;

        temp = edittxtLabel.getText().toString();
        if (temp.isEmpty()) {
            Toast.makeText(this,"請輸入標籤！",Toast.LENGTH_SHORT).show();
            return;
        }
        newRDay.put("Label",temp);

        temp = edittxtWage.getText().toString();
        if(temp.isEmpty()) {
            Toast.makeText(this,"請輸入時薪！",Toast.LENGTH_SHORT).show();
            return;
        }
        newRDay.put("Wage",Integer.valueOf(temp));

        temp = edittxtHour.getText().toString();
        if(temp.isEmpty()){
            Toast.makeText(this,"請輸入時數！",Toast.LENGTH_SHORT).show();
            return;
        }
        newRDay.put("Hour",temp);

        temp = textviewDayNum.getText().toString();
        if (temp=="0"){
            Toast.makeText(this,"請輸入天數！",Toast.LENGTH_SHORT).show();
            return;
        }
        newRDay.put("Days",temp);

        temp = "";
        for(int i=0;i<31;i++)
            if(selectedDays.get(i))temp+=String.format("%d",1);
            else temp+=String.format("%d",0);

        newRDay.put("SelectedDays",selectedDays);

//        Log.d("datelog",String.valueOf(boolean b = Boolean.));

        mDatabase.push().setValue(newRDay);
        finish();
    }

    public void onCheckBoxClicked(View v){
        int dayOfWeek=0;
        switch (v.getId()){
            case R.id.Monday:
                dayOfWeek=2;
                break;
            case R.id.Tuesday:
                dayOfWeek=3;
                break;
            case R.id.Wednesday:
                dayOfWeek=4;
                break;
            case R.id.Thursday:
                dayOfWeek=5;
                break;
            case R.id.Friday:
                dayOfWeek=6;
                break;
            case R.id.Saturday:
                dayOfWeek=7;
                break;
            case R.id.Sunday:
                dayOfWeek=1;
                break;
        }
        if (((CheckBox)v).isChecked()) {
            checkDays(dayOfWeek);
        }
        else
            uncheckDays(dayOfWeek);
    }

    void checkDays(int dayOfWeek){
        selectedWeekdays.set(dayOfWeek-1,true);
        Calendar date = Calendar.getInstance();
        int MAX = date.getActualMaximum(Calendar.DAY_OF_MONTH);
        for(int i=1;i<=MAX;i++) {
            date.set(Calendar.DAY_OF_MONTH,i);
            if(date.get(Calendar.DAY_OF_WEEK)==dayOfWeek)
            {
                calendarView2.selectDate(date.getTime());
                if(!selectedDays.get(i-1)) dayCount++; // only count if not yet selected before
                selectedDays.set(i-1,true);
            }
        }
        textviewDayNum.setText(String.valueOf(dayCount));
    }

    void uncheckDays(int dayOfWeek){
        // update array
        selectedWeekdays.set(dayOfWeek-1,false);
        // clear calendar
        initCalendar();
        // re-select days
        dayCount = 0;
        Calendar c = Calendar.getInstance();
        for(int i=0;i<31;i++) {
            if (selectedDays.get(i)){
//            Log.d("datelog",String.format(" dayofweek=%d",dayOfWeek));
                c.set(Calendar.DAY_OF_MONTH,i+1);
//                Log.d("datelog",String.format("Calendar.DAY_OF_WEEK=%d",c.get(Calendar.DAY_OF_WEEK)));
                if(c.get(Calendar.DAY_OF_WEEK)!=dayOfWeek) {
                    calendarView2.selectDate(c.getTime());
                    dayCount++;
                }
                else selectedDays.set(i,false);
            }
        }
        textviewDayNum.setText(String.valueOf(dayCount));
    }

    @Override
    public void onDateSelected(Date date) {
        // get dayOfMonth
        Calendar selectedDay = Calendar.getInstance();
        selectedDay.setTime(date);
        int day = selectedDay.get(Calendar.DAY_OF_MONTH);
        // update textview
        textviewDayNum.setText(String.valueOf(++dayCount));
        // update array
        selectedDays.set(day-1,true);
    }

    @Override
    public void onDateUnselected(Date date) {
        // get dayOfMonth
        Calendar selectedDay = Calendar.getInstance();
        selectedDay.setTime(date);
        int day = selectedDay.get(Calendar.DAY_OF_MONTH);
        // update array
        selectedDays.set(day-1,false);
        // update textview
        textviewDayNum.setText(String.valueOf(--dayCount));
    }
}
