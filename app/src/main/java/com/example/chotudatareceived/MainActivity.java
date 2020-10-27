package com.example.chotudatareceived;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.graphics.Color.BLUE;


public class MainActivity extends AppCompatActivity {
    int id = 0;
    public String person_name, detect_Name;
    static String notifyName;
    TextView tv1, tv2, tv3;
    public EditText nameNotify;
    public DatabaseReference reference, detection_Name, trained_Name, host,imageURL;
    public FirebaseDatabase database;
    Button btn1, btn2, btn3;
    ImageButton userbtn;
    String dataRetrieve;
    SessionManager session;
    ImageView imgShow;
    MydataService mydataService = new MydataService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        Bundle b = i.getBundleExtra("owner name");

        startService(new Intent(MainActivity.this, MydataService.class));

        getSupportActionBar().hide();
       // View background = findViewById(R.id.background);
       // Drawable chotu = background.getBackground();
//        chotu.setAlpha(90);
        imgShow= findViewById(R.id.imageView);
        session = new SessionManager(getApplicationContext());
        // host responses will save in this node.
        reference = database.getInstance().getReference().child("Responses");
        // Host names will save in this node.
        host = database.getInstance().getReference().child("Host Name");
        // Guest names will save in this node.
        detection_Name = database.getInstance().getReference().child("Detection name");
        // All info of known person by chotu will save here.
        trained_Name = database.getInstance().getReference().child("Trained Person Names");
        imageURL= database.getInstance().getReference().child("IMAGE URL");
        tv1 = (TextView) findViewById(R.id.name);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        userbtn = (ImageButton) findViewById(R.id.username);
        NameActivity nameActivity = new NameActivity();
        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
        session.checkLogin();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // application user name.
        final String name = user.get(SessionManager.KEY_NAME);
        Log.i("77", name);
        // email of application user.
        String email = user.get(SessionManager.KEY_EMAIL);
        // Fetching responses from

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    id = (int) dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String last_response = String.valueOf(dataSnapshot.getValue());
                Log.i("Responses", last_response);
                // textView.setText(last_name);
                person_name = last_response;

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
        });

        detection_Name.addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String last_name = String.valueOf(dataSnapshot.getValue());
                detect_Name = last_name;
                Log.i("Retrieve name only:", String.valueOf(dataSnapshot.getValue()));
                // notification();

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
        });


        trained_Name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String detected_name = String.valueOf(dataSnapshot.child(detect_Name.toLowerCase()).getValue());
                Log.i("data", "Detected Person Info Only:" + " " + detected_name);
                String formattedData = formatString(detected_name);
                Log.i("data", formattedData);
                String again=formattedData.replace("{","").replace("}","")
                        .replace("=",":").replace(",","");
                tv1.setText(again);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        imageURL.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image_url = String.valueOf(dataSnapshot.getValue());
                Log.i("url","Image URL"+image_url);
                Picasso.get().load(image_url).into(imgShow);
                Log.i("url","Image downloaded!!!");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(String.valueOf(id)).setValue(detect_Name + " " + "please wait in conference room. i will meet there.");

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(String.valueOf(id)).setValue(detect_Name + " " + " please come into my office.");

            }
        });

        userbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.logoutUser();
            }
        });

    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void notification() {
        String name = detect_Name;
        Log.i("notification", name);
        notifyName = name;
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
                getApplicationContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        myIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Notification myNotification = new Notification();
        myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        myNotification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("Chotu Notify!").setContentText(name + " " + "is here")
                .setLargeIcon(bitmap).setTicker("Chotu Notify!")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true)
                .setSmallIcon(R.drawable.fb_icon)
                .build();
        NotificationManager manager = (NotificationManager) getApplicationContext()
                .getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        manager.notify(123, myNotification);
    }

    public static String formatString(String text) {

        StringBuilder json = new StringBuilder();
        String indentString = "";

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            switch (letter) {
                case '{':
                case '[':
                    json.append("\n" + indentString + letter + "\n");
                    indentString = indentString + "\t";
                    json.append(indentString);
                    break;
                case '}':
                case ']':
                    indentString = indentString.replaceFirst("\t", "");
                    json.append("\n" + indentString + letter);
                    break;
                case ',':
                    json.append(letter + "\n" + indentString);
                    break;

                default:
                    json.append(letter);
                    break;
            }
        }

        return json.toString();
    }
}
