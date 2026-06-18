package com.example.finanseapp.model;

public class Operation {
    private String id;
    private String userId;
    private String type;
    private double amount;
    private String title;
    private String date; // Новое поле для даты от сервера

    // Конструктор для создания новой операции (дата и ID сгенерируются на сервере)
    public Operation(String userId, String type, double amount, String title) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.title = title;
    }

    // Пустой конструктор (обязателен для парсинга Retrofit)
    public Operation() {
    }

    // Геттеры
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getTitle() { return title; }
    public String getDate() { return date; } // Тот самый отсутствующий метод

    // Сеттеры
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setType(String type) { this.type = type; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setTitle(String title) { this.title = title; }
    public void setDate(String date) { this.date = date; }
}