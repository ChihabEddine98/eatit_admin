package com.chihab_eddine98.eatit_admin.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.chihab_eddine98.eatit_admin.R;
import com.chihab_eddine98.eatit_admin.controllers.Commandes;
import com.chihab_eddine98.eatit_admin.model.Order;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;


// This class send a notification for admin that there is a new order arriving
public class ListenOrder extends Service
            implements ChildEventListener {

    FirebaseDatabase bdd;
    DatabaseReference table_order;


    @Override
    public void onCreate() {
        super.onCreate();

        bdd=FirebaseDatabase.getInstance();
        table_order=bdd.getReference("FoodOrder");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        table_order.addChildEventListener(this);

        return super.onStartCommand(intent, flags, startId);
    }

    public ListenOrder() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // Quand on ajoute une nouvelle commande
    // Trigger içi
    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
    {
        Order order=dataSnapshot.getValue(Order.class);

        showNotifcation(dataSnapshot.getKey(),order);
    }

    private void showNotifcation(String key, Order order)
    {
        Intent intent=new Intent(getBaseContext(), Commandes.class);

        PendingIntent contentIntent=PendingIntent.getActivity(getBaseContext(),0,intent,0);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            NotificationChannel channel=
//                    new NotificationChannel("newOrder","newOrder",NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager manager=getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel);
//
//        }

        NotificationCompat.Builder builder=new NotificationCompat.Builder(getBaseContext(),"newOrder");

        builder.setAutoCancel(true)
               .setSmallIcon(R.mipmap.ic_launcher_round)
               .setTicker(" EatIt")
               .setDefaults(Notification.DEFAULT_ALL)
               .setContentIntent(contentIntent)
               .setContentInfo("Nouvelle commande arrivée")
               .setContentText(" Commande #"+key);

        NotificationManager manager=(NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        int randInt=new Random().nextInt(9999-1)+1; // int entre 1 et 9999

        manager.notify(randInt,builder.build());




    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
