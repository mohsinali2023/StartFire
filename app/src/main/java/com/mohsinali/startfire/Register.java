package com.mohsinali.startfire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mohsinali.startfire.databinding.ActivityRegisterBinding;

public class Register extends AppCompatActivity {


    FirebaseAuth mAuth;
    ActivityRegisterBinding binding;
    FirebaseFirestore firebaseFireStore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        firebaseFireStore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);

        binding.loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });
        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String userEmail, userPassword, confirmUserPassword;
                userEmail = binding.email.getText().toString().trim();
                userPassword = binding.password.getText().toString().trim();
                confirmUserPassword = binding.confirmPassword.getText().toString().trim();
                if (TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(Register.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(Register.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!userPassword.equals(confirmUserPassword)) {
                    Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.cancel();
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();

                                    // Save user data in Firestore
                                    firebaseFireStore.collection("User")
                                            .document(mAuth.getUid())
                                            .set(new UserModel(userEmail, userPassword));

                                    // Start the Login activity after registration
                                    startActivity(new Intent(Register.this, Login.class));
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });


    }

}