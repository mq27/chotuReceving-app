package com.example.chotudatareceived;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.app.PendingIntent.FLAG_ONE_SHOT;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.graphics.Color.BLUE;

public class MydataService extends Service {
    //creating a mediaplayer object
    private MediaPlayer player;
    String spName,hostName,ownerName;
    private boolean mRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        mRunning=false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mRunning){
            mRunning=true;
            SharedPreferences sp = this.getSharedPreferences("com.example.chotudatareceived", MODE_PRIVATE);
            final String text_for_display = sp.getString("name", spName);
            ownerName=text_for_display.toLowerCase().trim();
            Log.i("99","USER NAME"+text_for_display);
            DatabaseReference follower= FirebaseDatabase.getInstance().getReference().child("Host Name");
            follower.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String follower = String.valueOf(dataSnapshot.getValue()).toLowerCase().trim();
                    hostName=follower;
                    if(hostName.contains("host name=")){
                        hostName= hostName.substring(11).replaceAll("\\p{P}","");
                        Log.i("99","changed host name"+hostName);
                    }
                    Log.i("99","Follwer name"+follower);
                    Log.i("99","hostName"+" "+hostName+" "+"Owner NAme"+" "+ownerName);
                    if(ownerName.equals(hostName)){
                        Log.i("99","i m entering if");
                        notification();
                        dataSnapshot.getRef().removeValue();
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                    }
                    else{
                        //  notification();
                        Toast.makeText(getApplicationContext(),"no one here",Toast.LENGTH_LONG).show();
                        Log.i("99","no one here");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopping the player when service is destroyed
        player.stop();
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public  void notification() {
        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.chotunotify);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.notification);
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID = "com.example.mydatasend"; //your app package name
            NotificationChannel notificationChannel = new NotificationChannel("mq", "mq", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{0, 500, 1000, 1000});
            notificationChannel.canShowBadge();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                notificationChannel.canBubble();
            }
            notificationChannel.getLockscreenVisibility();
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, myIntent, FLAG_ONE_SHOT);
       // myIntent.addFlags(FLAG_ONE_SHOT);
        myIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Notification myNotification = new Notification();
        myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        myNotification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("Chotu Notify!").setContentText("hello" +  " " + "is here")
                .setLargeIcon(bitmap).setTicker("Chotu Notify!")
                .setWhen(System.currentTimeMillis())
                .setSound(sound)
                .setContentIntent(pendingIntent)
                //.setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.fb_icon)
                .build();
        NotificationManager manager = (NotificationManager) getApplicationContext()
                .getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        manager.notify(123, myNotification);
    }
}
