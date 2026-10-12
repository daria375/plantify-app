package com.example.plantify

// Модель запиту (тепер містить і назву рослини, і симптоми)
data class PlantDiagnosisRequest(
    val plant_name: String,
    val symptoms: String
)

// Модель відповіді відповідно до твого main.py
data class PlantDiagnosisResponse(
    val status: String,
    val category: String,
    val severity: String,
    val recommendations: List<String>
)