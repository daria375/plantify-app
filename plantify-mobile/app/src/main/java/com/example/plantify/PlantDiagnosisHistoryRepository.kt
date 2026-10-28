package com.example.plantify

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.util.UUID

/** Local persistent storage for queued requests and diagnosis results. */
class PlantDiagnosisHistoryRepository(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    private val listType = object : TypeToken<List<PlantDiagnosisHistoryItem>>() {}.type

    init {
        // Recover interrupted sends once per app process, not every time a screen creates a repository.
        synchronized(PlantDiagnosisHistoryRepository::class.java) {
            if (!recoveryDone) {
                recoverInterruptedSends()
                recoveryDone = true
            }
        }
    }

    @Synchronized
    private fun recoverInterruptedSends() {
        val json = preferences.getString(KEY_HISTORY, null) ?: return
        try {
            val stored = gson.fromJson<List<PlantDiagnosisHistoryItem>>(json, listType) ?: emptyList()
            val recovered = stored.map { item ->
                if (item.status.orEmpty() == DiagnosisStatus.SENDING) {
                    item.copy(
                        status = DiagnosisStatus.ERROR,
                        errorMessage = "The app was closed while this request was being sent. You can try again."
                    )
                } else item
            }
            if (recovered != stored) saveAll(recovered)
        } catch (_: Exception) {
            // getAll() will show the detailed storage error to the UI.
        }
    }

    @Synchronized
    fun getAll(): List<PlantDiagnosisHistoryItem> {
        val json = preferences.getString(KEY_HISTORY, null) ?: return emptyList()
        val stored = try {
            gson.fromJson<List<PlantDiagnosisHistoryItem>>(json, listType) ?: emptyList()
        } catch (exception: JsonSyntaxException) {
            throw IOException("The saved diagnosis history is damaged and could not be read.", exception)
        } catch (exception: RuntimeException) {
            throw IOException("The saved diagnosis history could not be read.", exception)
        }

        // Old records from the baseline version may not have the new optional fields.
        // A record left in SENDING after an app restart can be retried safely.
        val normalized = stored.map { item ->
            val normalizedStatus = when (item.status.orEmpty().lowercase()) {
                "ready", DiagnosisStatus.READY -> DiagnosisStatus.READY
                DiagnosisStatus.PENDING -> DiagnosisStatus.PENDING
                DiagnosisStatus.ERROR -> DiagnosisStatus.ERROR
                DiagnosisStatus.SENDING -> DiagnosisStatus.SENDING
                else -> if (item.category.orEmpty().isNotBlank()) DiagnosisStatus.READY else DiagnosisStatus.PENDING
            }
            item.copy(
                status = normalizedStatus,
                category = item.category.orEmpty(),
                severity = item.severity.orEmpty(),
                recommendations = item.recommendations.orEmpty(),
                errorMessage = item.errorMessage
            )
        }.sortedByDescending { it.createdAt }

        if (normalized != stored) saveAll(normalized)
        return normalized
    }

    @Synchronized
    fun getById(id: String): PlantDiagnosisHistoryItem? = getAll().firstOrNull { it.id == id }

    /** Save a request locally without contacting the backend. */
    @Synchronized
    fun savePendingRequest(plantName: String, symptoms: String): PlantDiagnosisHistoryItem {
        val cleanName = plantName.trim()
        val cleanSymptoms = symptoms.trim()
        require(cleanName.length >= 2) { "Enter a plant name with at least 2 characters." }
        require(cleanSymptoms.length >= 3) { "Enter symptoms with at least 3 characters." }

        val item = PlantDiagnosisHistoryItem(
            id = UUID.randomUUID().toString(),
            createdAt = System.currentTimeMillis(),
            plantName = cleanName,
            symptoms = cleanSymptoms,
            status = DiagnosisStatus.PENDING
        )
        saveAll(getAll() + item)
        return item
    }

    @Synchronized
    fun markSending(id: String): PlantDiagnosisHistoryItem? {
        val current = getById(id) ?: return null
        if (current.status == DiagnosisStatus.SENDING || current.status == DiagnosisStatus.READY) return null
        val updated = current.copy(status = DiagnosisStatus.SENDING, errorMessage = null)
        replace(updated)
        return updated
    }

    @Synchronized
    fun markReady(id: String, result: PlantDiagnosisResponse): PlantDiagnosisHistoryItem {
        require(result.status.equals("success", ignoreCase = true)) {
            "Only successful diagnosis results can be saved."
        }
        require(result.category.isNotBlank() && result.severity.isNotBlank()) {
            "The diagnosis response is incomplete."
        }
        require(result.recommendations.isNotEmpty() && result.recommendations.all { it.isNotBlank() }) {
            "The diagnosis recommendations are incomplete."
        }
        val current = getById(id) ?: throw IOException("The saved request no longer exists.")
        val updated = current.copy(
            status = DiagnosisStatus.READY,
            category = result.category.trim(),
            severity = result.severity.trim(),
            recommendations = result.recommendations.map { it.trim() },
            errorMessage = null
        )
        replace(updated)
        return updated
    }

    @Synchronized
    fun markError(id: String, message: String): PlantDiagnosisHistoryItem? {
        val current = getById(id) ?: return null
        val updated = current.copy(
            status = DiagnosisStatus.ERROR,
            errorMessage = message.ifBlank { "The request failed. You can try again." }
        )
        replace(updated)
        return updated
    }

    @Synchronized
    fun delete(id: String): List<PlantDiagnosisHistoryItem> {
        val updated = getAll().filterNot { it.id == id }
        saveAll(updated)
        return updated
    }

    @Synchronized
    fun clear(): List<PlantDiagnosisHistoryItem> {
        saveAll(emptyList())
        return emptyList()
    }

    @Synchronized
    private fun replace(item: PlantDiagnosisHistoryItem) {
        val current = getAll()
        if (current.none { it.id == item.id }) throw IOException("The saved request no longer exists.")
        saveAll(current.map { if (it.id == item.id) item else it })
    }

    private fun saveAll(items: List<PlantDiagnosisHistoryItem>) {
        val json = gson.toJson(items.sortedByDescending { it.createdAt })
        if (!preferences.edit().putString(KEY_HISTORY, json).commit()) {
            throw IOException("Could not save the diagnosis history.")
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "plantify_diagnosis_history"
        const val KEY_HISTORY = "diagnosis_history_json"
        @Volatile var recoveryDone: Boolean = false
    }
}
