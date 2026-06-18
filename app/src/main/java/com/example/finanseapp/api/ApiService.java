package com.example.finanseapp.api;

import com.example.finanseapp.model.Operation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("/api/operations")
    Call<Operation> addOperation(@Body Operation operation);
    @GET("/api/operations/{userId}")
    Call<List<Operation>> getUserOperations(@Path("userId") String userId);
}