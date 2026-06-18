package com.example.finanseapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finanseapp.api.RetrofitClient;
import com.example.finanseapp.model.Operation;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private TextView tvTotalBalance, tvTotalIncome, tvTotalExpense, tvBalancePercent;
    private TextView tvRecentTitle, tvRecentAmount;
    private LinearLayout layoutRecentOp;
    private LineChart balanceChart;
    private BottomNavigationView bottomNavigation;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();

        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvBalancePercent = findViewById(R.id.tvBalancePercent);
        tvRecentTitle = findViewById(R.id.tvRecentTitle);
        tvRecentAmount = findViewById(R.id.tvRecentAmount);
        layoutRecentOp = findViewById(R.id.layoutRecentOp);
        balanceChart = findViewById(R.id.balanceChart);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        setupChart();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchOperations();
    }

    private void fetchOperations() {
        if (auth.getCurrentUser() == null) return;
        String userId = auth.getCurrentUser().getUid();

        RetrofitClient.getClient().getUserOperations(userId).enqueue(new Callback<List<Operation>>() {
            @Override
            public void onResponse(Call<List<Operation>> call, Response<List<Operation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    processOperations(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Operation>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processOperations(List<Operation> operations) {
        if (operations.isEmpty()) {
            layoutRecentOp.setVisibility(View.GONE);
            tvTotalBalance.setText("0.00 ₽");
            tvTotalIncome.setText("0.00 ₽");
            tvTotalExpense.setText("0.00 ₽");
            tvBalancePercent.setText("Нет данных");
            balanceChart.clear();
            return;
        }

        double totalIncome = 0;
        double totalExpense = 0;

        // Переменные для прошлого и текущего месяца
        double currentMonthNet = 0;
        double lastMonthNet = 0;

        // Определяем текущий и прошлый месяцы (требует Android API 26+)
        java.time.YearMonth currentMonth = null;
        java.time.YearMonth lastMonth = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentMonth = java.time.YearMonth.now();
            lastMonth = currentMonth.minusMonths(1);
        }

        for (Operation op : operations) {
            // Считаем общие суммы за всё время
            if ("INCOME".equals(op.getType())) {
                totalIncome += op.getAmount();
            } else {
                totalExpense += op.getAmount();
            }

            // Считаем статистику по месяцам для процентов (если версия Android позволяет)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && op.getDate() != null) {
                try {
                    java.time.LocalDateTime date = java.time.LocalDateTime.parse(op.getDate());
                    java.time.YearMonth opMonth = java.time.YearMonth.from(date);

                    if (opMonth.equals(currentMonth)) {
                        currentMonthNet += "INCOME".equals(op.getType()) ? op.getAmount() : -op.getAmount();
                    } else if (opMonth.equals(lastMonth)) {
                        lastMonthNet += "INCOME".equals(op.getType()) ? op.getAmount() : -op.getAmount();
                    }
                } catch (Exception ignored) {
                    // Игнорируем ошибки парсинга конкретной даты
                }
            }
        }

        // Вывод общих цифр
        double balance = totalIncome - totalExpense;
        tvTotalBalance.setText(String.format("%.2f ₽", balance));
        tvTotalIncome.setText(String.format("%.2f ₽", totalIncome));
        tvTotalExpense.setText(String.format("%.2f ₽", totalExpense));

        // Высчитываем и выводим процент разницы
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (lastMonthNet != 0) {
                double diff = currentMonthNet - lastMonthNet;
                double percent = (diff / Math.abs(lastMonthNet)) * 100;

                if (percent > 0) {
                    tvBalancePercent.setText(String.format("+%.1f%% к прошлому месяцу", percent));
                    tvBalancePercent.setTextColor(android.graphics.Color.parseColor("#A5B4FC")); // Светло-синий
                } else if (percent < 0) {
                    tvBalancePercent.setText(String.format("%.1f%% к прошлому месяцу", percent));
                    tvBalancePercent.setTextColor(android.graphics.Color.parseColor("#EF4444")); // Красный
                } else {
                    tvBalancePercent.setText("0% к прошлому месяцу");
                    tvBalancePercent.setTextColor(android.graphics.Color.parseColor("#A5B4FC"));
                }
            } else {
                tvBalancePercent.setText("Нет данных за прошлый месяц");
            }
        }

        // Вывод последней операции
        layoutRecentOp.setVisibility(View.VISIBLE);
        Operation lastOp = operations.get(0);
        tvRecentTitle.setText(lastOp.getTitle());

        if ("INCOME".equals(lastOp.getType())) {
            tvRecentAmount.setText(String.format("+%.2f ₽", lastOp.getAmount()));
            tvRecentAmount.setTextColor(android.graphics.Color.parseColor("#22C55E"));
        } else {
            tvRecentAmount.setText(String.format("-%.2f ₽", lastOp.getAmount()));
            tvRecentAmount.setTextColor(android.graphics.Color.parseColor("#EF4444"));
        }

        // Обновляем график
        updateChartData(operations);
    }

    private void setupChart() {
        balanceChart.getDescription().setEnabled(false);
        balanceChart.getLegend().setEnabled(false);
        balanceChart.setTouchEnabled(false);
        balanceChart.getAxisRight().setEnabled(false);
        balanceChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        balanceChart.getXAxis().setDrawGridLines(false);
        balanceChart.getXAxis().setDrawLabels(false);
    }

    private void updateChartData(List<Operation> operations) {
        // Сервер отдает свежие первыми, для графика переворачиваем (старые -> новые)
        List<Operation> chronological = new ArrayList<>(operations);
        Collections.reverse(chronological);

        List<Entry> entries = new ArrayList<>();
        float currentBalance = 0f;

        for (int i = 0; i < chronological.size(); i++) {
            Operation op = chronological.get(i);
            if ("INCOME".equals(op.getType())) {
                currentBalance += op.getAmount();
            } else {
                currentBalance -= op.getAmount();
            }
            entries.add(new Entry(i, currentBalance));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Баланс");
        dataSet.setColor(Color.parseColor("#4F46E5")); // Синяя линия
        dataSet.setLineWidth(3f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Плавные изгибы

        LineData lineData = new LineData(dataSet);
        balanceChart.setData(lineData);
        balanceChart.invalidate(); // Перерисовка графика
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_transactions) {
                startActivity(new Intent(HomeActivity.this, TransactionsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_more) {
                auth.signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
                return true;
            }
            return true;
        });
    }
}