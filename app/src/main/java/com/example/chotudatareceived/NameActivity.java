package com.example.chotudatareceived;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NameActivity extends AppCompatActivity {

    EditText appName,appMail;
    Button sent;
    public String mqname;

    // Alert Dialog Manager
//    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    // Session Manager Class
    SessionManager session;

    public NameActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        View background = findViewById(R.id.userlogin);
  //      Drawable chotu= background.getBackground();
//        chotu.setAlpha(90);
        session = new SessionManager(getApplicationContext());
        appName = (EditText) findViewById(R.id.owner);
        appMail = (EditText) findViewById(R.id.usermail);
        Toast.makeText(getApplicationContext(),
                "User Login Status: " + session.isLoggedIn(),
                Toast.LENGTH_LONG).show();
        sent = findViewById(R.id.sent);
        boolean temp = session.isLoggedIn();
        if(temp == true){
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
        }

        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textName = appName.getText().toString().toLowerCase().trim();
//                String textMail = appMail.getText().toString().toLowerCase().trim();
               if(textName.trim().length()>0) {
                       session.createLoginSession(textName);
                       // Staring MainActivity
                       Intent i = new Intent(getApplicationContext(), MainActivity.class);
                       startActivity(i);
                       finish();
                   } else {
                       Toast.makeText(getApplicationContext(), "username is incorrect", Toast.LENGTH_LONG).show();
                       //                 alertDialog.setTitle( "Login failed.."+ "Username/Password is incorrect");
                   }
               }
        });


    }
}
