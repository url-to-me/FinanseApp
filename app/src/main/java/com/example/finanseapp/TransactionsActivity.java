package com.example.finanseapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finanseapp.api.RetrofitClient;
import com.example.finanseapp.model.Operation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private Button btnFilterAll, btnFilterIncome, btnFilterExpense, btnNewOperation;
    private BottomNavigationView bottomNavigation;

    private List<Operation> allOperations = new ArrayList<>();
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        auth = FirebaseAuth.getInstance();

        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterIncome = findViewById(R.id.btnFilterIncome);
        btnFilterExpense = findViewById(R.id.btnFilterExpense);
        btnNewOperation = findViewById(R.id.btnNewOperation);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter();
        recyclerView.setAdapter(adapter);

        setupFilters();
        setupBottomNavigation();

        btnNewOperation.setOnClickListener(v -> {
            startActivity(new Intent(TransactionsActivity.this, AddOperationActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOperations();
    }

    private void loadOperations() {
        if (auth.getCurrentUser() == null) return;

        RetrofitClient.getClient().getUserOperations(auth.getCurrentUser().getUid())
                .enqueue(new Callback<List<Operation>>() {
                    @Override
                    public void onResponse(Call<List<Operation>> call, Response<List<Operation>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            allOperations = response.body();
                            adapter.setOperations(allOperations); // Показываем все
                            setFilterActive(btnFilterAll);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Operation>> call, Throwable t) {
                        Toast.makeText(TransactionsActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupFilters() {
        btnFilterAll.setOnClickListener(v -> {
            adapter.setOperations(allOperations);
            setFilterActive(btnFilterAll);
        });

        btnFilterIncome.setOnClickListener(v -> {
            List<Operation> incomes = new ArrayList<>();
            for (Operation op : allOperations) if ("INCOME".equals(op.getType())) incomes.add(op);
            adapter.setOperations(incomes);
            setFilterActive(btnFilterIncome);
        });

        btnFilterExpense.setOnClickListener(v -> {
            List<Operation> expenses = new ArrayList<>();
            for (Operation op : allOperations) if ("EXPENSE".equals(op.getType())) expenses.add(op);
            adapter.setOperations(expenses);
            setFilterActive(btnFilterExpense);
        });
    }

    private void setFilterActive(Button activeButton) {
        // Сброс всех кнопок в серый
        Button[] buttons = {btnFilterAll, btnFilterIncome, btnFilterExpense};
        for (Button btn : buttons) {
            btn.setTextColor(Color.parseColor("#737992"));
            btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        }
        // Выделение активной кнопки синим
        activeButton.setTextColor(Color.parseColor("#4F46E5"));
        activeButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EEF2FF")));
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_transactions);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Возврат на главную
                startActivity(new Intent(TransactionsActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_more) {
                auth.signOut();
                startActivity(new Intent(TransactionsActivity.this, LoginActivity.class));
                finish();
                return true;
            }
            return true;
        });
    }
}