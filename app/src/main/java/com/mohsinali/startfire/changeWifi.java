package com.mohsinali.startfire;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class changeWifi extends AppCompatActivity {

    private Button connect;
    private ImageView menu, logout;
    private LinearLayout home, closeDrawer, changeLogin, changeWifi;
    private TextView profileEmail;
    private EditText changeIp;
    DrawerLayout drawerLayout;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_wifi);

        connect = findViewById(R.id.connect);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        logout = findViewById(R.id.logout);
        closeDrawer = findViewById(R.id.closeDrawer);
        changeLogin = findViewById(R.id.changeLogin);
        changeWifi = findViewById(R.id.changeWifi);
        drawerLayout = findViewById(R.id.drawerLayout);
        profileEmail = findViewById(R.id.profileEmail);
        changeIp = findViewById(R.id.changeIp);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        String userEmail = user.getEmail();
        profileEmail.setText(userEmail);


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(changeWifi.this, MainActivity.class);

            }
        });
        changeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(changeWifi.this, changeLogin.class);
            }
        });
        changeWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(changeWifi.this, changeWifi.class);
            }
        });
        closeDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer(drawerLayout);
            }
        });




        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Uri webpage = Uri.parse("http://192.168.4.1");
//                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                }
//                WebView webView = new WebView(changeWifi.this);
//                setContentView(webView);
//                webView.loadUrl("http://192.168.4.1");

                String newIpAddress = changeIp.getText().toString();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("new_ip_address", newIpAddress);
                setResult(RESULT_OK, resultIntent);
                finish();





            }
        });




        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
//////////////////////////////////////////
        Intent intent = getIntent();
        if (intent != null) {
            String ipAddress = intent.getStringExtra("ip_address");
            if (ipAddress != null) {
                changeIp.setText(ipAddress);
            }
        }
    }



    public void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);


    }

    public void closeDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }


    }

    public void redirectActivity(Activity activity, Class secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}
