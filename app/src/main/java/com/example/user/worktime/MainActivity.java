package com.example.user.worktime;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AlertDialog.OnClickListener{
    TextView ExtraMoney,TotalMoney,OffMoney;
    final int extra=0,total=0,off=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExtraMoney = (TextView) findViewById(R.id.ExtraMoney);
        TotalMoney = (TextView) findViewById(R.id.TotalMoney);
        OffMoney = (TextView) findViewById(R.id.OffMoney);
        calculateWage();
        //CountTotal();
    }
    @Override
    protected void onResume(){
        super.onResume();
        calculateWage();
    }

    // UNUSED FUNCTION
    public void CountTotal(){
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("RepeatDays");
        ValueEventListener valueEventListener2 = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int days, hour,wage, rsum=0,extra,off,total;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    days = Integer.valueOf(ds.child("Days").getValue().toString());
                    hour = Integer.valueOf(ds.child("Hour").getValue().toString());
                    wage = Integer.valueOf(ds.child("Wage").getValue().toString());
                    rsum+= (days*hour*wage);
                }
                extra = Integer.valueOf(ExtraMoney.getText().toString());
                off = Integer.valueOf(OffMoney.getText().toString());
                total = rsum + extra - off;
                TotalMoney.setText(String.valueOf(total));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // CALCULATE TOTAL WAGE TO DISPLAY
    public void calculateWage(){
        final int[] extrasum = {0};
        final int[] offsum = { 0 };
        final int[] rsum = { 0 };
        final int[] total = { 0 };
        DatabaseReference mDatabase;
        Calendar date = Calendar.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference("Extradays");
        ValueEventListener valueEventListener = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int days,hours,wage;
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    days = Integer.valueOf(ds.child("Days").getValue().toString());
                    hours = Integer.valueOf(ds.child("Hour").getValue().toString());
                    wage = Integer.valueOf(ds.child("Wage").getValue().toString());
                    extrasum[0] += (days*hours*wage);
                }
                ExtraMoney.setText(String.valueOf(extrasum[0]));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference("Offdays");
        ValueEventListener valueEventListener1 = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int days, hours, wage;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    days = Integer.valueOf(ds.child("Days").getValue().toString());
                    hours = Integer.valueOf(ds.child("Hour").getValue().toString());
                    wage = Integer.valueOf(ds.child("Wage").getValue().toString());
                    offsum[0] += (days*hours*wage);
                }
                OffMoney.setText(String.valueOf(offsum[0]));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference("RepeatDays");
        ValueEventListener valueEventListener2 = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int days, hour,wage;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    days = Integer.valueOf(ds.child("Days").getValue().toString());
                    hour = Integer.valueOf(ds.child("Hour").getValue().toString());
                    wage = Integer.valueOf(ds.child("Wage").getValue().toString());
                    rsum[0] += (days*hour*wage);
                }
               // extra = Integer.valueOf(ExtraMoney.getText().toString());
                //off = Integer.valueOf(OffMoney.getText().toString());
                total[0] = rsum[0] + extrasum[0] - offsum[0];
                TotalMoney.setText(String.valueOf(rsum[0] + extrasum[0] - offsum[0]));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void viewDetail(View v){
        Intent it = new Intent(this, viewDetail.class);
        startActivity(it);
    }

    public void newMonth(View v){
        AlertDialog bdr = new AlertDialog.Builder(this)
                .setTitle("新的一月")
                .setMessage("確認清除所有資料？")
                .setPositiveButton("確認清除",this)
                .setNegativeButton("返回",this)
                .show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == AlertDialog.BUTTON_POSITIVE){
            ArrayList<String> forms = new ArrayList<String>(3);
            forms.add("RepeatDays"); forms.add("Extradays"); forms.add("Offdays");
            for (String db : forms){
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(db);
                mDatabase.setValue(null);
                /*
                final DatabaseReference[] dbRef = new DatabaseReference[1];
                final DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference(db);
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            dbRef[0] = FirebaseDatabase.getInstance().getReference(db + "/" + ds.getKey());
                            dbRef[0].removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/
            }
         //   mDatabase.removeValue();
         //   onResume();
        }
        calculateWage();
    }
}

