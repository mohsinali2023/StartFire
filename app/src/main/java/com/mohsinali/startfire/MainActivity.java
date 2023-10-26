package com.mohsinali.startfire;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohsinali.startfire.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private ImageView menu, logout;
    private LinearLayout home, closeDrawer, changeLogin, changeWifi;
    private TextView profileEmail,txtRES;


    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference pathRef;
    DrawerLayout drawerLayout;
    ActivityMainBinding binding;
    String currentUserId;
    private String ipAddress = "192.168.4.1";

    private static final int REQUEST_CODE_CHANGE_WIFI = 1;


    private OkHttpClient client = new OkHttpClient();

    private Handler handler = new Handler(Looper.getMainLooper());
    private static final int DELAY_MILLISECONDS = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        menu = findViewById(R.id.menu);
        logout = findViewById(R.id.logout);
        closeDrawer = findViewById(R.id.closeDrawer);
        changeLogin = findViewById(R.id.changeLogin);
        changeWifi = findViewById(R.id.changeWifi);
        home = findViewById(R.id.home);
        drawerLayout = findViewById(R.id.drawerLayout);
        profileEmail = findViewById(R.id.profileEmail);
        txtRES = findViewById(R.id.txtRES);


        ////////////////////////////////////
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        NetworkRequest request = builder.build();
        connManager.requestNetwork(request, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    connManager.bindProcessToNetwork(network);
                }
            }
        });


        ////////////////////////////////////////



        // Navigation Drawer
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, MainActivity.class);


            }
        });
        changeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, changeLogin.class);
            }
        });
        changeWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                redirectActivity(MainActivity.this, changeWifi.class);
//
////                Intent intent = new Intent(MainActivity.this, changeWifi.class);

                Intent intent = new Intent(MainActivity.this, changeWifi.class);
                startActivityForResult(intent, REQUEST_CODE_CHANGE_WIFI);


            }
        });
        closeDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer(drawerLayout);
            }
        });

        // Firebase Database
        database = FirebaseDatabase.getInstance("https://startfire-34da5-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
        } else {
            // Handle the case when the user is not signed in
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

        //user profile
        String userEmail = user.getEmail();
        profileEmail.setText(userEmail);

        pathRef = database.getReference().child("UsersData").child(currentUserId).child("readings");
        pathRef.child("wifiStatus").setValue("off");
        binding.statusTV.setText("Offline");
////////////////////////////////////////////////////////////////
//
//        if (isConnectedToSpecificWifi("Mohsin Ali")) {
//            // Your app is connected to the specified Wi-Fi network.
//            // You can perform actions specific to this network.
//           binding.statusTV.setText("connected");
//            binding.statusTV.setTextColor(getResources().getColor(R.color.white));
////            Toast.makeText(this, "connected to specific network", Toast.LENGTH_SHORT).show();
//        } else {
//            // Your app is not connected to the specified Wi-Fi network.
//            // Handle this case accordingly.
//            binding.statusTV.setText("offline");
//            binding.statusTV.setTextColor(getResources().getColor(R.color.Red));
////            Toast.makeText(this, "Not connected to the specified Wi-Fi network", Toast.LENGTH_SHORT).show();
//        }
        startRepeatingTask();

        /////////////////////////////

        // Read from the database
        pathRef.child("wifiStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if ("off".equals(value)) {
                    binding.statusTV.setText("Offline");
                    binding.statusTV.setTextColor(getResources().getColor(R.color.Red));
//                    Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                } else {
                    binding.statusTV.setText("Online");
                    binding.statusTV.setTextColor(getResources().getColor(R.color.white));

                    Toast.makeText(MainActivity.this, "Connection Established", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(MainActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();
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

        // Code for changing Stroke color
        Drawable initialDrawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initialDrawable = binding.unlockBtn.getForeground();
        }
        Drawable newDrawable = initialDrawable.getConstantState().newDrawable().mutate();

        int initialStrokeColor = getResources().getColor(R.color.white);
        ((GradientDrawable) newDrawable).setStroke(2, initialStrokeColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.unlockBtn.setForeground(newDrawable);
        }
///////////////////////////////Control Buttons///////////////////////////////////////////////////////////////
        binding.ignitionSwtich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pathRef.child("ignition").setValue("1");
                    sendCommand("ignitionon");
                } else {
                    pathRef.child("ignition").setValue("0");
                    pathRef.child("start").setValue("0");
                    pathRef.child("lock").setValue("0");
                    pathRef.child("indicator").setValue("0");
                    ///sending using local server
                    sendCommand("ignitionoff");


                    //Start Switch
                    int newStrokeColorUnpressed = getResources().getColor(R.color.white);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ((GradientDrawable) binding.startSwitch.getForeground()).setStroke(2, newStrokeColorUnpressed);
                    }
                    int ButtonColor = getResources().getColor(R.color.white);
                    binding.startSwitch.setTextColor(ButtonColor);
                    binding.startSwitch.setText("Start");

                    //unlock button
                    DrawableCompat.setTint(binding.lockBtn.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.white));
                    DrawableCompat.setTint(binding.unlockBtn.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.pressedcolor));
                    int newStrokeColorPressed = getResources().getColor(R.color.pressedcolor);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ((GradientDrawable) binding.lockBtn.getForeground()).setStroke(4, newStrokeColorUnpressed);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ((GradientDrawable) binding.unlockBtn.getForeground()).setStroke(4, newStrokeColorPressed);
                    }
                    //indicator switch
                    if (binding.indicatorSwitch.isChecked()) {
                        binding.indicatorSwitch.setChecked(false);
                    }

                }
            }
        });
        binding.indicatorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    pathRef.child("indicator").setValue("1");
                    sendCommand("indicatoron");

                } else {
                    pathRef.child("indicator").setValue("0");
                    sendCommand("indicatoroff");

                }
            }
        });
        binding.startSwitch.setOnClickListener(new View.OnClickListener() {
            private boolean isOn = false;

            @Override
            public void onClick(View v) {
                isOn = !isOn;
                if (isOn) {

                    int newStrokeColorPressed = getResources().getColor(R.color.pressedcolor);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ((GradientDrawable) binding.startSwitch.getForeground()).setStroke(2, newStrokeColorPressed);
                    }

                    sendCommand("start");
                    int ButtonColor = getResources().getColor(R.color.pressedcolor);
                    binding.startSwitch.setTextColor(ButtonColor);





                    pathRef.child("startedValue").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String value = dataSnapshot.getValue(String.class);
                            if ("start".equals(value)) {
                                binding.startSwitch.setText("Started");
                                int ButtonColor = getResources().getColor(R.color.pressedcolor);
                                binding.startSwitch.setTextColor(ButtonColor);

                            } else {
                                binding.startSwitch.setText("Start");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w(TAG, "Failed to read value.", error.toException());
                            Toast.makeText(MainActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();
                        }
                    });

                    pathRef.child("start").setValue("1");

                } else {
                    int newStrokeColorUnpressed = getResources().getColor(R.color.white);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ((GradientDrawable) binding.startSwitch.getForeground()).setStroke(2, newStrokeColorUnpressed);
                    }
                    int ButtonColor = getResources().getColor(R.color.white);
                    binding.startSwitch.setTextColor(ButtonColor);
                    pathRef.child("start").setValue("0");
                    sendCommand("stop");

                    binding.startSwitch.setText("Start");
                }
            }
        });
        binding.lockBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                DrawableCompat.setTint(binding.unlockBtn.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.white));
                DrawableCompat.setTint(binding.lockBtn.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.pressedcolor));

                int newStrokeColorUnpressed = getResources().getColor(R.color.white);
                int newStrokeColorPressed = getResources().getColor(R.color.pressedcolor);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ((GradientDrawable) binding.unlockBtn.getForeground()).setStroke(2, newStrokeColorUnpressed);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ((GradientDrawable) binding.lockBtn.getForeground()).setStroke(2, newStrokeColorPressed);
                }

                pathRef.child("lock").setValue("1");
                sendCommand("lock");


            }
        });
        binding.unlockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawableCompat.setTint(binding.lockBtn.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.white));
                DrawableCompat.setTint(binding.unlockBtn.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.pressedcolor));

                int newStrokeColorUnpressed = getResources().getColor(R.color.white);
                int newStrokeColorPressed = getResources().getColor(R.color.pressedcolor);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ((GradientDrawable) binding.lockBtn.getForeground()).setStroke(2, newStrokeColorUnpressed);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ((GradientDrawable) binding.unlockBtn.getForeground()).setStroke(2, newStrokeColorPressed);
                }

                pathRef.child("lock").setValue("0");
                sendCommand("red");


            }
        });

    }

    ////////////////////////////////////Functions/////////////////////////////////////////////////////////

    public void sendCommand(String cmd) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String command = "http://" + ipAddress + "/" + cmd;
//                Log.d("Command------------------------------------------", command);
                Request request = new Request.Builder().url(command).build();
                try {
                    Response response = client.newCall(request).execute();
                    String myResponse = response.body().string();
                    final String cleanResponse = myResponse.replaceAll("\\<.*?\\>", ""); // remove HTML tags
                    cleanResponse.replace("\n", ""); // remove all new line characters
                    cleanResponse.replace("\r", ""); // remove all carriage characters
                    cleanResponse.replace(" ", ""); // removes all space characters
                    cleanResponse.replace("\t", ""); // removes all tab characters
                    cleanResponse.trim();
                    Log.d("Response  = ", cleanResponse);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            txtRES.setText(cleanResponse);
                            Toast.makeText(MainActivity.this, cleanResponse, Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }





    ////////
    private void startRepeatingTask() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Place your code here that you want to execute every 3 seconds
                if (isConnectedToSpecificWifi("StartFire Local")) {
                    // Your app is connected to the specified Wi-Fi network.
                    // You can perform actions specific to this network.
                    binding.statusTV.setText("connected");
                    binding.statusTV.setTextColor(getResources().getColor(R.color.white));
//                    Toast.makeText(MainActivity.this, "connected to specific network", Toast.LENGTH_SHORT).show();
                } else {
                    // Your app is not connected to the specified Wi-Fi network.
                    // Handle this case accordingly.
                    binding.statusTV.setText("offline");
                    binding.statusTV.setTextColor(getResources().getColor(R.color.Red));
//                    Toast.makeText(MainActivity.this, "Not connected to the specified Wi-Fi network", Toast.LENGTH_SHORT).show();
                }

                // Schedule the next execution
                handler.postDelayed(this, DELAY_MILLISECONDS);
            }
        }, DELAY_MILLISECONDS);
    }

    private boolean isConnectedToSpecificWifi(String ssid) {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            // Connected to Wi-Fi
            String currentSsid = networkInfo.getExtraInfo();
            if (currentSsid != null && currentSsid.contains(ssid)) {
                // Connected to the specific Wi-Fi network
                return true;
            }
        }

        // Not connected to the specific Wi-Fi network
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CHANGE_WIFI && resultCode == RESULT_OK) {
            String newIpAddress = data.getStringExtra("new_ip_address");
            if (newIpAddress != null && !newIpAddress.isEmpty()) {
                ipAddress = newIpAddress; // Update the ipAddress variable
                Toast.makeText(this, "IP address updated: " + ipAddress, Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        pathRef.child("wifiStatus").setValue("off");


    }

    @Override
    protected void onResume() {
        super.onResume();
        pathRef.child("wifiStatus").setValue("off");

    }

    @Override
    protected void onPause() {
        super.onPause();
        pathRef.child("wifiStatus").setValue("off");



    }


}




