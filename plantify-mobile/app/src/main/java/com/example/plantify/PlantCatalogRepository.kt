package com.example.plantify

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.util.UUID

/**
 * Локальне сховище особистого каталогу рослин.
 * Каталог не використовує backend або AI.
 */
class PlantCatalogRepository(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    private val listType = object : TypeToken<List<PlantCatalogItem>>() {}.type

    @Synchronized
    fun loadPlants(): List<PlantCatalogItem> {
        val json = preferences.getString(KEY_PLANTS, null) ?: return emptyList()

        return try {
            gson.fromJson<List<PlantCatalogItem>>(json, listType) ?: emptyList()
        } catch (exception: JsonSyntaxException) {
            throw IOException(
                "The saved plant catalog is damaged and could not be read.",
                exception
            )
        } catch (exception: RuntimeException) {
            throw IOException(
                "The saved plant catalog could not be read.",
                exception
            )
        }
    }

    @Synchronized
    fun addPlant(name: String, note: String?): List<PlantCatalogItem> {
        val cleanedName = name.trim()
        require(cleanedName.isNotEmpty()) { "Plant name cannot be empty." }

        val cleanedNote = note?.trim()?.takeIf { it.isNotEmpty() }
        val updatedPlants = loadPlants() + PlantCatalogItem(
            id = UUID.randomUUID().toString(),
            name = cleanedName,
            note = cleanedNote
        )

        savePlants(updatedPlants)
        return updatedPlants
    }

    @Synchronized
    fun updatePlant(id: String, name: String, note: String?): List<PlantCatalogItem> {
        val cleanedName = name.trim()
        require(cleanedName.isNotEmpty()) { "Plant name cannot be empty." }

        val cleanedNote = note?.trim()?.takeIf { it.isNotEmpty() }
        val currentPlants = loadPlants()
        require(currentPlants.any { it.id == id }) {
            "The plant you are trying to edit no longer exists."
        }

        val updatedPlants = currentPlants.map { plant ->
            if (plant.id == id) {
                plant.copy(name = cleanedName, note = cleanedNote)
            } else {
                plant
            }
        }

        savePlants(updatedPlants)
        return updatedPlants
    }

    @Synchronized
    fun deletePlant(id: String): List<PlantCatalogItem> {
        val updatedPlants = loadPlants().filterNot { it.id == id }
        savePlants(updatedPlants)
        return updatedPlants
    }

    private fun savePlants(plants: List<PlantCatalogItem>) {
        val json = gson.toJson(plants)
        val saved = preferences.edit().putString(KEY_PLANTS, json).commit()
        if (!saved) {
            throw IOException("Could not save the plant catalog.")
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "plantify_plant_catalog"
        const val KEY_PLANTS = "plants_json"
    }
}
