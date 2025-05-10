package com.cinepass.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Usuário está autenticado, vá para MainActivity
            startActivity(new Intent(LauncherActivity.this, MainActivity.class));
        } else {
            // Usuário não está autenticado, vá para Intro e depois Login
            startActivity(new Intent(LauncherActivity.this, Intro.class));
        }
        finish();
    }
}
