package com.example.finanseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btnStart = findViewById(R.id.btnStart);
        Button btnLogin = findViewById(R.id.btnLogin);

        // Переход на онбординг
        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, Onboarding1Activity.class);
            startActivity(intent);
        });

        // Переход на вход
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}