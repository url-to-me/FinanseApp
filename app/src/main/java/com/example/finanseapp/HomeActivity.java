package com.example.finanseapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private Button btnAddIncome;
    private Button btnAddExpense;
    private Button btnLogout;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();

        btnAddIncome = findViewById(R.id.btnAddIncome);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnLogout = findViewById(R.id.btnLogout);

        btnAddIncome.setOnClickListener(v -> {
            Toast.makeText(this, "Добавление дохода подключим позже", Toast.LENGTH_SHORT).show();
        });

        btnAddExpense.setOnClickListener(v -> {
            Toast.makeText(this, "Добавление расхода подключим позже", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            auth.signOut();

            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}