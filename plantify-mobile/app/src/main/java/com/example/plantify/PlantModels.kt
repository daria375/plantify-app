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
data class PlantCatalogItem(
    val id: String,
    val name: String,
    val note: String? = null
)

/** One saved diagnosis request and, when available, its result. */
data class PlantDiagnosisHistoryItem(
    val id: String,
    val createdAt: Long,
    val plantName: String,
    val symptoms: String,
    val status: String = DiagnosisStatus.PENDING,
    val category: String = "",
    val severity: String = "",
    val recommendations: List<String> = emptyList(),
    val errorMessage: String? = null
)

object DiagnosisStatus {
    const val PENDING = "pending"
    const val SENDING = "sending"
    const val READY = "ready"
    const val ERROR = "error"
}
