package com.example.user.worktime;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class viewDetail extends AppCompatActivity
        implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener{
    ListView listv;
    TextView textView;
    Button viewRepeat,viewExtra,viewOff;
    String dbRef = "RepeatDays", keyId;
    final ArrayList<String> key = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail);
        textView = (TextView) findViewById(R.id.textView);
        viewRepeat = (Button) findViewById(R.id.viewRepeat);
//        viewExtra = (Button) findViewById(R.id.viewExtra);
        viewOff = (Button) findViewById(R.id.viewOff);
//        viewRepeat.setOnLongClickListener(this);
//        viewExtra.setOnLongClickListener(this);
//        viewOff.setOnLongClickListener(this);
        listv = (ListView) findViewById(R.id.listView);
        listv.setOnItemClickListener(this);

        viewRepeatDetail(findViewById(R.id.viewRepeat));
    }

    protected void onResume(){
        super.onResume();
        if(dbRef == "RepeatDays") viewRepeatDetail(viewRepeat);
//        else if (dbRef == "Extradays") viewExtraDetail(viewExtra);
        else if (dbRef == "Offdays") viewOffDetail(viewOff);
    }


    public void viewRepeatDetail(View v){
        key.clear();
        textView.setText("工作詳情");
        dbRef = "RepeatDays";
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1);
        listv.setAdapter(adapter);
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("RepeatDays");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String toAdd="";
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    key.add(ds.getKey());
                    toAdd += ds.child("Label").getValue().toString() + "\t\t，天數：" + ds.child("Days").getValue().toString() + "天\n\t\t"
                            + "時數： " + ds.child("Hour").getValue().toString() + "小時，\t\t"
                            + "時薪：" + ds.child("Wage").getValue().toString() + "元";

                    adapter.add(toAdd);
                    toAdd = "";
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Toast.makeText(this,String.valueOf(key.size()),Toast.LENGTH_SHORT).show();
    }

//    public void viewExtraDetail(View v){
//        key.clear();
//        textView.setText("加班詳情");
//        dbRef = "Extradays";
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1,
//                android.R.id.text1);
//        listv.setAdapter(adapter);
//        DatabaseReference mDatabase;
//        mDatabase = FirebaseDatabase.getInstance().getReference("Extradays");
//        adapter.clear();
//        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String toAdd="";
//                for (DataSnapshot ds : dataSnapshot.getChildren()){
//                    key.add(ds.getKey());
//                    toAdd += ds.child("Label").getValue().toString() + "，" + "天數：" + ds.child("Days").getValue().toString() + "\n\t\t"
//                            + "時數：" + ds.child("Hour").getValue().toString() + "小時，\t\t"
//                            + "時薪：" + ds.child("Wage").getValue().toString() + "元";
//
//                    adapter.add(toAdd);
//                    toAdd = "";
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    public void viewOffDetail(View v){
        key.clear();
        textView.setText("請假詳情");
        dbRef = "Offdays";
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1);
        listv.setAdapter(adapter);
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("Offdays");
        adapter.clear();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String toAdd="";
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    key.add(ds.getKey());
                    toAdd += ds.child("Label").getValue().toString() + "，" + "天數：" + ds.child("Days").getValue().toString() + "\n\t\t"
                            + "時數：" + ds.child("Hour").getValue().toString() + "小時，\t\t"
                            + "時薪：" + ds.child("Wage").getValue().toString() + "元";

                    adapter.add(toAdd);
                    toAdd = "";
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    @Override
//    public boolean onLongClick(View v) {
//        if(v.getId() == R.id.viewRepeat)
//        {
//            Intent it = new Intent(this,NewRepeatDay.class);
//            it.putExtra("type","repeat");
//            startActivity(it);
//        }
//        else if (v.getId() ==  R.id.viewExtra)
//        {
//            Intent it = new Intent(this,NewExtraOff.class);
//            it.putExtra("title","新增加班");
//            it.putExtra("dbRef","Extradays");
//            startActivity(it);
//        }
//        else if (v.getId() ==  R.id.viewOff)
//        {
//            Intent it = new Intent(this,NewExtraOff.class);
//            it.putExtra("title","新增請假");
//            it.putExtra("dbRef","Offdays");
//            startActivity(it);
//        }
//        return false;
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < key.size()) {
            final Toast t = Toast.makeText(this, key.get(position),Toast.LENGTH_SHORT);
            keyId = key.get(position);
            final AlertDialog bdr = new AlertDialog.Builder(this)
                    .setTitle("確認刪除？")
                    .setMessage("是否刪除這筆資料")
                    .setPositiveButton("刪除",this)
                    .setNegativeButton("返回",this)
                    .show();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE && keyId != null && keyId != "") {
            final DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference(dbRef + '/' + keyId);
            mDatabase.removeValue();
            onResume();
        }
    }

    public void newEntry(View v){
        Intent it;
        if (dbRef == "RepeatDays") {
            it = new Intent(this,NewRepeatDay.class);
            it.putExtra("type","repeat");
            it.putExtra("dbRef",dbRef);
            startActivity(it);
        } else
        {
            it = new Intent(this,NewExtraOff.class);
            it.putExtra("dbRef",dbRef);
            startActivity(it);
        }
    }
}
