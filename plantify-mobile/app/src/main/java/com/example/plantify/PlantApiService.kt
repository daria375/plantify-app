package com.example.plantify

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// Інтерфейс для зв'язку з бекендом
interface PlantApiService {
    @POST("api/v1/diagnose")
    fun diagnosePlant(
        @Body request: PlantDiagnosisRequest
    ): Call<PlantDiagnosisResponse>

    companion object {
        // IP-адреса для емулятора Android
        private const val BASE_URL = "http://192.168.0.107:8000/"

        fun create(): PlantApiService {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(45, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(45, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient) // Обов'язково підключаємо наш налаштований клієнт!
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(PlantApiService::class.java)
        }
    }
}