package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.Adapter.EventAdapter;
import com.example.myapplication.Database.DatabaseClass;
import com.example.myapplication.Database.EntityClass;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Reminder extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    Button set_time,create_evt;
    EventAdapter eventAdapter;
    RecyclerView recyclerview;
    DatabaseClass databaseClass;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        mAuth = FirebaseAuth.getInstance();

        /*EntityClass entityClass = new EntityClass();
        databaseClass.EventDao().delete(entityClass);*/
        set_time = findViewById(R.id.settime);
        create_evt = findViewById(R.id.addevent);
        recyclerview = findViewById(R.id.recyclerview);
        databaseClass = DatabaseClass.getDatabase(getApplicationContext());

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
    protected void onResume() {
        super.onResume();
        setAdapter();


    }
    private void setAdapter() {
        final List<EntityClass> classList = databaseClass.EventDao().getAllData();
        eventAdapter = new EventAdapter(getApplicationContext(), classList);
        recyclerview.setAdapter(eventAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                classList.remove(position);
                eventAdapter.notifyDataSetChanged();
                databaseClass.EventDao().delete(classList.get(position));





            }
        });
        helper.attachToRecyclerView(recyclerview);
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
