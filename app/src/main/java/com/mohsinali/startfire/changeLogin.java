package com.mohsinali.startfire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changeLogin extends AppCompatActivity {

    private ImageView menu, logout;
    private LinearLayout home, closeDrawer, changeLogin, changeWifi;
    private TextInputEditText oldPassword, confirmNewPassword, newPassword;
    private Button updatePassword;
    private TextView profileEmail;
    String oldPass,newPass,confirmNewPass;
    DrawerLayout drawerLayout;
    FirebaseAuth mAuth;
    FirebaseUser user;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_login);

        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        logout = findViewById(R.id.logout);
        closeDrawer = findViewById(R.id.closeDrawer);
        changeLogin = findViewById(R.id.changeLogin);
        changeWifi = findViewById(R.id.changeWifi);
        drawerLayout = findViewById(R.id.drawerLayout);
        oldPassword = findViewById(R.id.oldPassword);
        confirmNewPassword = findViewById(R.id.confrimNewPassword);
        newPassword = findViewById(R.id.newPassword);
        updatePassword = findViewById(R.id.updatePassBtn);
        profileEmail = findViewById(R.id.profileEmail);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);

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
                redirectActivity(changeLogin.this, MainActivity.class);

            }
        });
        changeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(changeLogin.this, changeLogin.class);
            }
        });
        changeWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(changeLogin.this, changeWifi.class);
            }
        });
        closeDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer(drawerLayout);
            }
        });

        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 oldPass = oldPassword.getText().toString().trim();
                 newPass = newPassword.getText().toString().trim();
                 confirmNewPass = confirmNewPassword.getText().toString().trim();


                if (TextUtils.isEmpty(oldPass)) {
                    Toast.makeText(changeLogin.this, "Please enter old password", Toast.LENGTH_SHORT).show();
                }
                if (newPass != confirmNewPass) {
                    Toast.makeText(changeLogin.this, "Please enter the same password", Toast.LENGTH_SHORT).show();

                }
                progressDialog.show();
                updatePassword(oldPass, newPass);


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

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void updatePassword(String oldPass, String newPass) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        user.updatePassword(newPass)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(changeLogin.this, "successfully updated password", Toast.LENGTH_SHORT).show();
                                         oldPassword.setText("");
                                         newPassword.setText("");
                                         confirmNewPassword.setText("");
                                        progressDialog.cancel();


                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(changeLogin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.cancel();

                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(changeLogin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    }
                });
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