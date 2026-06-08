package com.example.finanseapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Onboarding3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding3);

        Button nextButton = findViewById(R.id.nextButton);
        TextView skipText = findViewById(R.id.skipText);

        nextButton.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        skipText.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }
}