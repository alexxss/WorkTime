package com.example.user.worktime;

import android.content.Intent;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewExtraOff extends AppCompatActivity implements CalendarView.OnDateChangeListener{
    CalendarView calendarView2;
    EditText edittxtLabel, edittxtWage, edittxtHour;
    TextView txtvSelectDate,txtvTitle;
    String dbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_extra_off);

        txtvTitle = (TextView) findViewById(R.id.txtvTitle);

        Intent it = getIntent();
        String title = it.getStringExtra("title");
        txtvTitle.setText(title);
        dbRef = it.getStringExtra("dbRef");

        calendarView2 = (CalendarView) findViewById(R.id.calendarView2);
        calendarView2.setOnDateChangeListener(this);

        txtvSelectDate = (TextView) findViewById(R.id.txtvSelectDate);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        txtvSelectDate.setText(sdf.format(new Date(calendarView2.getDate())).toString());

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


    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        String selectDate = String.valueOf(dayOfMonth) + "/" + String.valueOf(month+1) + "/" + String.valueOf(year);
        txtvSelectDate.setText(selectDate);
    }

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
        newRDay.put("Date",temp);

        mDatabase.push().setValue(newRDay);
        finish();
    }
}
