package com.example.finanseapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Onboarding2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding2);

        Button nextButton = findViewById(R.id.nextButton);
        TextView skipText = findViewById(R.id.skipText);

        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(Onboarding2Activity.this, Onboarding3Activity.class);
            startActivity(intent);
        });

        skipText.setOnClickListener(v -> {
            Intent intent = new Intent(Onboarding2Activity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}