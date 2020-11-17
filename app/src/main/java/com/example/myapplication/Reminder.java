package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class Reminder extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    Button set_time,create_evt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        set_time = findViewById(R.id.settime);
        create_evt = findViewById(R.id.addevent);
        set_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePicker();
                timePicker.show(getSupportFragmentManager(),"time picker");

            }
        });
        create_evt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Reminder.this,CreateEvent.class));
            }
        });

    }


    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        Toast.makeText(this,"Reminder Set!",Toast.LENGTH_SHORT).show();
        Calendar calendar = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
       now.set(Calendar.HOUR_OF_DAY,hourOfDay);
        now.set(Calendar.MINUTE,minute);
        now.set(Calendar.SECOND,0);
        now.set(Calendar.MILLISECOND,0);




        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(Reminder.this,ReminderBroadcast.class);
        PendingIntent pd = PendingIntent.getBroadcast(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (am != null) {
            am.setExact(AlarmManager.RTC_WAKEUP,now.getTimeInMillis(),pd);
        }


    }


}
