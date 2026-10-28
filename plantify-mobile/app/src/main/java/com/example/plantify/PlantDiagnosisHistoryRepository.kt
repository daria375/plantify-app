package com.example.plantify

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.util.UUID

/** Local persistent storage for successful AI diagnosis results. */
class PlantDiagnosisHistoryRepository(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    private val listType = object : TypeToken<List<PlantDiagnosisHistoryItem>>() {}.type

    @Synchronized
    fun getAll(): List<PlantDiagnosisHistoryItem> {
        val json = preferences.getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            (gson.fromJson<List<PlantDiagnosisHistoryItem>>(json, listType) ?: emptyList())
                .sortedByDescending { it.createdAt }
        } catch (exception: JsonSyntaxException) {
            throw IOException("The saved diagnosis history is damaged and could not be read.", exception)
        } catch (exception: RuntimeException) {
            throw IOException("The saved diagnosis history could not be read.", exception)
        }
    }

    @Synchronized
    fun getById(id: String): PlantDiagnosisHistoryItem? = getAll().firstOrNull { it.id == id }

    @Synchronized
    fun saveSuccessfulDiagnosis(
        plantName: String,
        symptoms: String,
        result: PlantDiagnosisResponse
    ): PlantDiagnosisHistoryItem {
        require(plantName.isNotBlank()) { "Plant name cannot be empty." }
        require(symptoms.isNotBlank()) { "Symptoms cannot be empty." }
        require(result.status.equals("success", ignoreCase = true)) {
            "Only successful diagnosis results can be saved."
        }
        require(result.category.isNotBlank() && result.severity.isNotBlank()) {
            "The diagnosis response is incomplete."
        }
        require(result.recommendations.isNotEmpty() && result.recommendations.all { it.isNotBlank() }) {
            "The diagnosis recommendations are incomplete."
        }

        val item = PlantDiagnosisHistoryItem(
            id = UUID.randomUUID().toString(),
            createdAt = System.currentTimeMillis(),
            plantName = plantName.trim(),
            symptoms = symptoms.trim(),
            status = "ready",
            category = result.category,
            severity = result.severity,
            recommendations = result.recommendations.toList()
        )
        saveAll(getAll() + item)
        return item
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

    private fun saveAll(items: List<PlantDiagnosisHistoryItem>) {
        val json = gson.toJson(items.sortedByDescending { it.createdAt })
        if (!preferences.edit().putString(KEY_HISTORY, json).commit()) {
            throw IOException("Could not save the diagnosis history.")
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "plantify_diagnosis_history"
        const val KEY_HISTORY = "diagnosis_history_json"
    }
}
