package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.Database.DatabaseClass;
import com.example.myapplication.Database.EntityClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class CreateEvent extends AppCompatActivity implements View.OnClickListener{
    FirebaseAuth fAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    String uid;
    String user_name,ph;
    Button btn_time, btn_date, btn_done;
    ImageView btn_record;
    String timeTonotify;
    DatabaseClass databaseClass;
    EditText editext_message;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        editext_message = findViewById(R.id.content);
        btn_record = findViewById(R.id.btn_record);
        btn_time = findViewById(R.id.sel_time);
        btn_date = findViewById(R.id.sel_date);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        assert user != null;
        uid  = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user_name = Objects.requireNonNull(snapshot.child("users").child(uid).child("username").getValue()).toString();
                ph = Objects.requireNonNull(snapshot.child("users").child(uid).child("phone").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();

            }
        });



        Button btn = findViewById(R.id.save_btn);
        btn_record.setOnClickListener(this);
        btn_time.setOnClickListener(this);
        btn_date.setOnClickListener(this);
        btn.setOnClickListener(this);
        databaseClass = DatabaseClass.getDatabase(getApplicationContext());








    }


    @Override
    public void onClick(View v) {
        if (v == btn_record) {
            recordSpeech();
        } else if (v == btn_time) {
            selectTime();
        } else if (v == btn_date) {
            selectDate();
        } else {

            sendEmail();
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED){
                    sendSMS();
                }
                else{
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS},1);
                }
            }
            submit();
        }


    }

    private void sendEmail() {
        String date = (btn_date.getText().toString().trim());
        String time = (btn_time.getText().toString().trim());
        EditText editTextMessage = findViewById(R.id.content);
        final String msg = editTextMessage.getText().toString().trim();
        final FirebaseUser user = fAuth.getCurrentUser();
        assert user != null;
        String greet = "Hey "+user_name+"! You have the following task scheduled at " + time + " on "+ date+ " : \n";
        SendMail sm = new SendMail(this, user.getEmail(),"Task Scheduled!",greet + msg);
        sm.execute();
    }
    private void sendSMS(){
        EditText editTextMessage = findViewById(R.id.content);
        final String msg = editTextMessage.getText().toString().trim();
        String SMS = msg;
        PendingIntent pInt;
        pInt = PendingIntent.getBroadcast(this,0,new Intent("SMS_SENT"),0);

        try{
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(ph,null,SMS,pInt,null);}
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"Failed to send message!",Toast.LENGTH_LONG).show();
        }

    }
    @Override
    protected void onStart() {
        super.onStart();


        if(fAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this,Title.class));
        }
    }
    private void submit() {
        String text = editext_message.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "Please Enter or record the text", Toast.LENGTH_SHORT).show();
        } else {
            if (btn_time.getText().toString().equals("Select Time") || btn_date.getText().toString().equals("Select date")) {
                Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show();
            } else {
                EntityClass entityClass = new EntityClass();
                String value = (editext_message.getText().toString().trim());
                String date = (btn_date.getText().toString().trim());
                String time = (btn_time.getText().toString().trim());
                entityClass.setEventdate(date);
                entityClass.setEventname(value);
                entityClass.setEventtime(time);
                databaseClass.EventDao().insertAll(entityClass);
                setAlarm(value, date, time);
            }
        }
        startActivity(new Intent(this,Reminder.class));
    }
    private void selectTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                timeTonotify = i + ":" + i1;
                btn_time.setText(FormatTime(i, i1));
            }
        }, hour, minute, false);
        timePickerDialog.show();

    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                btn_date.setText(day + "-" + (month + 1) + "-" + year);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public String FormatTime(int hour, int minute) {

        String time;
        time = "";
        String formattedMinute;

        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
        } else {
            formattedMinute = "" + minute;
        }


        if (hour == 0) {
            time = "12" + ":" + formattedMinute + " AM";
        } else if (hour < 12) {
            time = hour + ":" + formattedMinute + " AM";
        } else if (hour == 12) {
            time = "12" + ":" + formattedMinute + " PM";
        } else {
            int temp = hour - 12;
            time = temp + ":" + formattedMinute + " PM";
        }


        return time;
    }


    private void recordSpeech() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        try {

            startActivityForResult(intent, 1);
        } catch (Exception e) {
            Toast.makeText(this, "Your device does not support Speech recognizer", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                editext_message.setText(text.get(0));
            }
        }

    }


    private void setAlarm(String text, String date, String time) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), AlarmBrodcast.class);
        intent.putExtra("event", text);
        intent.putExtra("time", date);
        intent.putExtra("date", time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String dateandtime = date + " " + timeTonotify;
        DateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");
        try {
            Date date1 = formatter.parse(dateandtime);
            am.set(AlarmManager.RTC_WAKEUP, date1.getTime() - 3600000, pendingIntent);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        finish();

    }






}
