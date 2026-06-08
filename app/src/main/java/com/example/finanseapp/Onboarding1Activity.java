package com.example.finanseapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Onboarding1Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding1);

        Button nextButton = findViewById(R.id.nextButton);
        TextView skipText = findViewById(R.id.skipText);

        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(Onboarding1Activity.this, Onboarding2Activity.class);
            startActivity(intent);
        });

        skipText.setOnClickListener(v -> {
            Intent intent = new Intent(Onboarding1Activity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}