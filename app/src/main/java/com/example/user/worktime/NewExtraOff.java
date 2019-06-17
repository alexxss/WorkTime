package com.example.user.worktime;

import android.content.Intent;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.timessquare.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewExtraOff extends AppCompatActivity implements CalendarPickerView.OnDateSelectedListener{
    CalendarPickerView calendarView2;
    EditText edittxtLabel;
    TextView txtvSelectDate,txtvTitle, edittxtWage, edittxtHour;
    String dbRef;
    int numDays = 0;
    List<Boolean> selectedDays = new ArrayList<Boolean>(Arrays.asList(new Boolean[31]));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // HIDE TITLE
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_new_extra_off);

        // PREVENT KEYBOARD AUTO POP-UP
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        txtvTitle = (TextView) findViewById(R.id.txtvTitle);

        Intent it = getIntent();
//        String title = it.getStringExtra("title");
//        txtvTitle.setText(title);
        dbRef = it.getStringExtra("dbRef");
        if(dbRef == "ExtraDays") txtvTitle.setText("新增加班");
        else if (dbRef == "OffDays") txtvTitle.setText("新增請假");

        calendarView2 = (CalendarPickerView) findViewById(R.id.calendarView2);
        ViewGroup.LayoutParams layout = calendarView2.getLayoutParams();
        layout.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        calendarView2.setLayoutParams(layout);
        initCalendar();

        txtvSelectDate = (TextView) findViewById(R.id.txtvSelectDate);
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        //txtvSelectDate.setText(sdf.format(new Date(calendarView2.getDate())).toString());

        edittxtHour = (TextView) findViewById(R.id.edittxtHour);
        edittxtWage = (TextView) findViewById(R.id.edittxtWage);
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
//        Log.d("datetag!!",String.valueOf(month_begin.getTime()));
//        Log.d("datetag!!",String.valueOf(date));
//        Log.d("datetag!!",String.valueOf(month_end.getTime()));
        calendarView2.init(month_begin.getTime(), month_end.getTime())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE);
        calendarView2.setOnDateSelectedListener(this);
    }

    // onClick for button
    public void onClick(View v){
   //     Toast.makeText(this,dbRef,Toast.LENGTH_SHORT).show();

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference(dbRef);
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

        temp = txtvSelectDate.getText().toString();
        if(numDays==0) {
            Toast.makeText(this,"請選擇日期！",Toast.LENGTH_SHORT).show();
            return;
        }
        newRDay.put("Days",temp);

//        newRDay.put("SelectedDays",St)
        temp = "";
        for(int i=0;i<31;i++)
            if(selectedDays.get(i))temp+=String.format("%d",1);
            else temp+=String.format("%d",0);

        newRDay.put("SelectedDays",selectedDays);

        mDatabase.push().setValue(newRDay);
        finish();
    }

    // when date selected: update array, update text view
    @Override
    public void onDateSelected(Date date) {
        // get Day_Of_Month
        Calendar selectedDay = Calendar.getInstance();
        selectedDay.setTime(date);
        int day = selectedDay.get(Calendar.DAY_OF_MONTH);
        selectedDays.set(day-1,true);
        txtvSelectDate.setText(String.valueOf(++numDays));
        // calculate wage & hour?
        updateWageAndHour(day-1);
    }

    // when date unselected: update array, update text view
    @Override
    public void onDateUnselected(Date date) {
        Calendar selectedDay = Calendar.getInstance();
        selectedDay.setTime(date);
        int day = selectedDay.get(Calendar.DAY_OF_MONTH);
        selectedDays.set(day-1,false);
        txtvSelectDate.setText(String.valueOf(--numDays));
        // subtract wage & hour
        subtractWageAndHour(day-1);
    }

    public void updateWageAndHour(final int day){
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("RepeatDays");
        ValueEventListener valueEventListener2 = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int hour = 0, wage = 0,rsum,hoursum;
                if(!edittxtWage.getText().toString().isEmpty())
                    rsum=Integer.valueOf(edittxtWage.getText().toString());
                else rsum = 0;
                if (!edittxtHour.getText().toString().isEmpty())
                    hoursum=Integer.valueOf(edittxtHour.getText().toString());
                else hoursum = 0;

                GenericTypeIndicator<List<Boolean>> t = new GenericTypeIndicator<List<Boolean>>() {};
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    hour = Integer.valueOf(ds.child("Hour").getValue().toString());
                    wage = Integer.valueOf(ds.child("Wage").getValue().toString());
                    List<Boolean> eventDays = ds.child("SelectedDays").getValue(t);
                    if(eventDays.get(day)){
                        rsum += (hour*wage);
                        hoursum += hour;
                        edittxtWage.setText(String.valueOf(rsum));
                        edittxtHour.setText(String.valueOf(hoursum));
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void subtractWageAndHour(final int day){
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("RepeatDays");
        ValueEventListener valueEventListener2 = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int hour = 0, wage = 0,rsum,hoursum;
                if(!edittxtWage.getText().toString().isEmpty())
                    rsum=Integer.valueOf(edittxtWage.getText().toString());
                else rsum = 0;
                if (!edittxtHour.getText().toString().isEmpty())
                    hoursum=Integer.valueOf(edittxtHour.getText().toString());
                else hoursum = 0;

                GenericTypeIndicator<List<Boolean>> t = new GenericTypeIndicator<List<Boolean>>() {};
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    hour = Integer.valueOf(ds.child("Hour").getValue().toString());
                    wage = Integer.valueOf(ds.child("Wage").getValue().toString());
                    List<Boolean> eventDays = ds.child("SelectedDays").getValue(t);
                    if(eventDays.get(day)){
                        rsum -= (hour*wage);
                        hoursum -= hour;
                        edittxtWage.setText(String.valueOf(rsum));
                        edittxtHour.setText(String.valueOf(hoursum));
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
