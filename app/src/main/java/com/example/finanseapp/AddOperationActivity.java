package com.example.finanseapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finanseapp.api.RetrofitClient;
import com.example.finanseapp.model.Operation;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddOperationActivity extends AppCompatActivity {

    private Button btnIncomeType, btnExpenseType, btnTransferType, btnSaveOperation;
    private EditText etAccount, etCategory, etAmount, etDate, etDescription;
    private FirebaseAuth auth;

    // По умолчанию выбран расход, как в твоем XML дизайне
    private String selectedType = "EXPENSE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_operation);

        // Инициализация Firebase
        auth = FirebaseAuth.getInstance();

        // Привязка UI элементов к ID из XML
        btnIncomeType = findViewById(R.id.btnIncomeType);
        btnExpenseType = findViewById(R.id.btnExpenseType);
        btnTransferType = findViewById(R.id.btnTransferType);
        btnSaveOperation = findViewById(R.id.btnSaveOperation);

        etAccount = findViewById(R.id.etAccount);
        etCategory = findViewById(R.id.etCategory);
        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);
        etDescription = findViewById(R.id.etDescription);

        // Настройка переключателей типов
        btnIncomeType.setOnClickListener(v -> updateTypeSelection("INCOME"));
        btnExpenseType.setOnClickListener(v -> updateTypeSelection("EXPENSE"));
        btnTransferType.setOnClickListener(v ->
                Toast.makeText(this, "Функция перевода в разработке", Toast.LENGTH_SHORT).show()
        );

        // Обработка кнопки "Сохранить"
        btnSaveOperation.setOnClickListener(v -> saveOperationToServer());
    }

    // Метод для визуального переключения активной кнопки
    private void updateTypeSelection(String type) {
        selectedType = type;

        // Цвета из твоего дизайна
        int activeBgColor = Color.parseColor("#F05A3A"); // Оранжевый
        int activeTextColor = Color.parseColor("#FFFFFF"); // Белый
        int inactiveBgColor = Color.parseColor("#FFFFFF"); // Белый
        int inactiveTextColor = Color.parseColor("#070B2D"); // Темно-синий

        // Сбрасываем обе кнопки в неактивное состояние
        btnIncomeType.setBackgroundTintList(ColorStateList.valueOf(inactiveBgColor));
        btnIncomeType.setTextColor(inactiveTextColor);

        btnExpenseType.setBackgroundTintList(ColorStateList.valueOf(inactiveBgColor));
        btnExpenseType.setTextColor(inactiveTextColor);

        // Красим только ту, которую выбрали
        if (type.equals("INCOME")) {
            btnIncomeType.setBackgroundTintList(ColorStateList.valueOf(activeBgColor));
            btnIncomeType.setTextColor(activeTextColor);
        } else if (type.equals("EXPENSE")) {
            btnExpenseType.setBackgroundTintList(ColorStateList.valueOf(activeBgColor));
            btnExpenseType.setTextColor(activeTextColor);
        }
    }

    private void saveOperationToServer() {
        String amountStr = etAmount.getText().toString().trim();
        String category = etCategory.getText().toString().trim();

        if (amountStr.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Укажите сумму и категорию", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        double amount;

        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Неверный формат суммы", Toast.LENGTH_SHORT).show();
            return;
        }

        // В нашей текущей модели на сервере есть поле 'title'. Передаем туда название категории.
        Operation operation = new Operation(userId, selectedType, amount, category);

        // Отправка запроса на бэкенд
        RetrofitClient.getClient().addOperation(operation).enqueue(new Callback<Operation>() {
            @Override
            public void onResponse(Call<Operation> call, Response<Operation> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddOperationActivity.this, "Операция сохранена!", Toast.LENGTH_SHORT).show();
                    finish(); // Успешно - закрываем экран и возвращаемся назад
                } else {
                    Toast.makeText(AddOperationActivity.this, "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Operation> call, Throwable t) {
                Toast.makeText(AddOperationActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}