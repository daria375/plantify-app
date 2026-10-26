package com.example.plantify

// Модель запиту для AI-діагностики.
data class PlantDiagnosisRequest(
    val plant_name: String,
    val symptoms: String
)

// Модель відповіді AI-діагностики.
data class PlantDiagnosisResponse(
    val status: String,
    val category: String,
    val severity: String,
    val recommendations: List<String>
)

// Модель одного запису особистого каталогу.
// ID потрібен, щоб можна було видаляти записи з однаковими назвами.
data class PlantCatalogItem(
    val id: String,
    val name: String,
    val note: String? = null
)
