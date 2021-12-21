package com.example.android.geo_loco;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoefenceBroadcastReceiver extends BroadcastReceiver {
    public String ClassRoomNumber;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //Toast.makeText(context, "Geofence Triggered........", Toast.LENGTH_LONG).show();

        ArrayList<String>ClassName=new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {

                //Background work here
                Log.d("first","Geofence Triggered........");

                NotificationHelper notificationHelper = new NotificationHelper(context);

                GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        /*Bundle extras=intent.getExtras();
        if(extras!=null){
            ClassRoomNumber=extras.getString("key");
            Log.d("first",ClassRoomNumber);
        }*/
                if(geofencingEvent.hasError()){
                    Log.d("AskPermission", "onReceive: Error Receiving Geofence Event..");
                    return;
                }


                int transitionType=geofencingEvent.getGeofenceTransition();

                switch (transitionType){
                    case Geofence.GEOFENCE_TRANSITION_ENTER:
                        Toast.makeText(context, "ENTERED INTO GEOFENCE", Toast.LENGTH_SHORT).show();
                        break;
                    case Geofence.GEOFENCE_TRANSITION_DWELL:
                        //Toast.makeText(context, "DWELLING INTO GEOFENCE", Toast.LENGTH_SHORT).show();
                        List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
                        for(Geofence geofence: geofences){
                            ClassName.add(geofence.getRequestId());
                            notificationHelper.sendHighPriorityNotification("Entered "+geofence.getRequestId()+" Classrooom","Your Attendance has been marked",MainActivity.class);
                            //MainActivity.updateButton.setVisibility(View.VISIBLE);
                            //MainActivity.mark_attendance(geofence.getRequestId());
                            Log.d("first",geofence.getRequestId());
                            Log.d("first",Thread.currentThread().getName());
                        }
                        break;
                    case Geofence.GEOFENCE_TRANSITION_EXIT:
                        Toast.makeText(context, "EXITING FROM GEOFENCE", Toast.LENGTH_SHORT).show();
                        break;
                }


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread work here
                        //MainActivity.updateButton.setVisibility(View.VISIBLE);
                        Calendar cal = Calendar.getInstance();
                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH)+1;
                        int day = cal.get(Calendar.DAY_OF_MONTH);

                        String date = day + "-" + month + "-" +  year;
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Attendance");
                        db.child(ClassName.get(0)).child(date).child(MainActivity.UserEnroll[0]).setValue("Marked").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context,"Attendance Marked!",Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context,"Failed to Mark!",Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                });
            }
        });
    }
}