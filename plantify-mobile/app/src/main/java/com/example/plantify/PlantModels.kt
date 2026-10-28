//package com.example.plantify
//
//// Модель запиту для AI-діагностики.
//data class PlantDiagnosisRequest(
//    val plant_name: String,
//    val symptoms: String
//)
//
//// Модель відповіді AI-діагностики.
//data class PlantDiagnosisResponse(
//    val status: String,
//    val category: String,
//    val severity: String,
//    val recommendations: List<String>
//)
//
//// Модель одного запису особистого каталогу.
//// ID потрібен, щоб можна було видаляти записи з однаковими назвами.
//data class PlantCatalogItem(
//    val id: String,
//    val name: String,
//    val note: String? = null
//)

package com.example.plantify

// Request model for AI plant diagnostics.
data class PlantDiagnosisRequest(
    val plant_name: String,
    val symptoms: String
)

// Response model returned by the backend.
data class PlantDiagnosisResponse(
    val status: String,
    val category: String,
    val severity: String,
    val recommendations: List<String>
)

// One entry in the personal plant catalog.
// ID allows entries with identical names to be managed separately.
data class PlantCatalogItem(
    val id: String,
    val name: String,
    val note: String? = null
)

// A successfully completed AI diagnosis saved on this device.
data class PlantDiagnosisHistoryItem(
    val id: String,
    val createdAt: Long,
    val plantName: String,
    val symptoms: String,
    val status: String = "ready",
    val category: String,
    val severity: String,
    val recommendations: List<String>
)
