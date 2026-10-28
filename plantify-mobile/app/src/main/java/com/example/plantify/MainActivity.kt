//package com.example.plantify
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.statusBarsPadding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Button
//import androidx.compose.material3.Card
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Tab
//import androidx.compose.material3.TabRow
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import com.example.plantify.ui.theme.PlantifyTheme
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//private enum class PlantifyTab(val title: String) {
//    DIAGNOSTICS("Diagnostics"),
//    CATALOG("My Catalog")
//}
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            PlantifyTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    PlantifyHomeScreen()
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun PlantifyHomeScreen() {
//    var selectedTab by remember { mutableStateOf(PlantifyTab.DIAGNOSTICS) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .statusBarsPadding()
//    ) {
//        Text(
//            text = "🌱 Plantify",
//            style = MaterialTheme.typography.headlineSmall,
//            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
//        )
//
//        TabRow(selectedTabIndex = selectedTab.ordinal) {
//            PlantifyTab.values().forEach { tab ->
//                Tab(
//                    selected = selectedTab == tab,
//                    onClick = { selectedTab = tab },
//                    text = { Text(tab.title) }
//                )
//            }
//        }
//
//        when (selectedTab) {
//            PlantifyTab.DIAGNOSTICS -> PlantDiagnosisScreen()
//            PlantifyTab.CATALOG -> PlantCatalogScreen()
//        }
//    }
//}
//
//@Composable
//fun PlantDiagnosisScreen() {
//    var plantNameText by remember { mutableStateOf("") }
//    var symptomsText by remember { mutableStateOf("") }
//    var resultText by remember {
//        mutableStateOf("The result will appear here after the diagnostics...")
//    }
//    var isLoading by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        Text(
//            text = "AI Diagnostics",
//            style = MaterialTheme.typography.titleLarge
//        )
//
//        OutlinedTextField(
//            value = plantNameText,
//            onValueChange = { plantNameText = it },
//            label = { Text("Plant name (e.g., Monstera)") },
//            modifier = Modifier.fillMaxWidth(),
//            singleLine = true
//        )
//
//        OutlinedTextField(
//            value = symptomsText,
//            onValueChange = { symptomsText = it },
//            label = { Text("Describe the symptoms (e.g., yellow leaves)") },
//            modifier = Modifier.fillMaxWidth(),
//            minLines = 2
//        )
//
//        Button(
//            onClick = {
//                val cleanedPlantName = plantNameText.trim()
//                val cleanedSymptoms = symptomsText.trim()
//
//                if (cleanedPlantName.isBlank() || cleanedSymptoms.isBlank()) {
//                    resultText =
//                        "Please enter the name of the plant and describe the symptoms."
//                    return@Button
//                }
//
//                isLoading = true
//                resultText = "We are analyzing..."
//
//                val apiService = PlantApiService.create()
//                val request = PlantDiagnosisRequest(
//                    plant_name = cleanedPlantName,
//                    symptoms = cleanedSymptoms
//                )
//
//                apiService.diagnosePlant(request)
//                    .enqueue(object : Callback<PlantDiagnosisResponse> {
//                        override fun onResponse(
//                            call: Call<PlantDiagnosisResponse>,
//                            response: Response<PlantDiagnosisResponse>
//                        ) {
//                            isLoading = false
//                            if (response.isSuccessful && response.body() != null) {
//                                val data = response.body()!!
//                                val recommendationsText =
//                                    data.recommendations.joinToString(separator = "\n") {
//                                        "• $it"
//                                    }
//                                resultText =
//                                    "Category: ${data.category}\n" +
//                                            "Severity: ${data.severity}\n\n" +
//                                            "Recommendations:\n$recommendationsText"
//                            } else {
//                                resultText = when (response.code()) {
//                                    400 -> "The entered data is invalid."
//                                    422 -> "Check the plant name and symptom description."
//                                    502 -> "The AI service returned an invalid response."
//                                    503 -> "The AI service is temporarily unavailable."
//                                    else -> "Server error: ${response.code()}"
//                                }
//                            }
//                        }
//
//                        override fun onFailure(
//                            call: Call<PlantDiagnosisResponse>,
//                            t: Throwable
//                        ) {
//                            isLoading = false
//                            resultText = "Network error: ${t.localizedMessage ?: "Unknown error"}"
//                        }
//                    })
//            },
//            modifier = Modifier.fillMaxWidth(),
//            enabled = !isLoading
//        ) {
//            Text(if (isLoading) "Processing..." else "Diagnose")
//        }
//
//        HorizontalDivider()
//
//        Text(
//            text = resultText,
//            style = MaterialTheme.typography.bodyMedium
//        )
//    }
//}
//
//@Composable
//fun PlantCatalogScreen() {
//    val context = LocalContext.current
//    val repository = remember(context) { PlantCatalogRepository(context) }
//    val plants = remember { mutableStateListOf<PlantCatalogItem>() }
//
//    var plantName by remember { mutableStateOf("") }
//    var note by remember { mutableStateOf("") }
//    var editingPlantId by remember { mutableStateOf<String?>(null) }
//    var message by remember { mutableStateOf("Your plants are saved on this device.") }
//    var isLoaded by remember { mutableStateOf(false) }
//
//    LaunchedEffect(repository) {
//        try {
//            plants.clear()
//            plants.addAll(repository.loadPlants())
//            message = "Your plants are saved on this device."
//        } catch (exception: Exception) {
//            message = exception.message
//                ?: "Could not load the catalog. Your saved data may be unavailable."
//        } finally {
//            isLoaded = true
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        Text(
//            text = "My Plant Catalog",
//            style = MaterialTheme.typography.titleLarge
//        )
//        Text(
//            text = "Keep a simple list of your plants and optional notes.",
//            style = MaterialTheme.typography.bodyMedium
//        )
//
//        OutlinedTextField(
//            value = plantName,
//            onValueChange = { plantName = it },
//            label = { Text("Plant name (required)") },
//            modifier = Modifier.fillMaxWidth(),
//            singleLine = true
//        )
//
//        OutlinedTextField(
//            value = note,
//            onValueChange = { note = it },
//            label = { Text("Note (optional)") },
//            modifier = Modifier.fillMaxWidth(),
//            minLines = 2
//        )
//
//        Button(
//            onClick = {
//                if (plantName.isBlank()) {
//                    message = "Please enter a plant name."
//                } else {
//                    try {
//                        val currentEditingId = editingPlantId
//                        val updatedPlants = if (currentEditingId == null) {
//                            repository.addPlant(plantName, note)
//                        } else {
//                            repository.updatePlant(currentEditingId, plantName, note)
//                        }
//
//                        plants.clear()
//                        plants.addAll(updatedPlants)
//                        plantName = ""
//                        note = ""
//                        editingPlantId = null
//                        message = if (currentEditingId == null) {
//                            "Plant added to your catalog."
//                        } else {
//                            "Plant changes saved."
//                        }
//                    } catch (exception: Exception) {
//                        message = exception.message ?: "Could not save this plant."
//                    }
//                }
//            },
//            modifier = Modifier.fillMaxWidth(),
//            enabled = isLoaded
//        ) {
//            Text(if (editingPlantId == null) "Add plant" else "Save changes")
//        }
//
//        if (editingPlantId != null) {
//            OutlinedButton(
//                onClick = {
//                    plantName = ""
//                    note = ""
//                    editingPlantId = null
//                    message = "Editing cancelled."
//                },
//                modifier = Modifier.fillMaxWidth(),
//                enabled = isLoaded
//            ) {
//                Text("Cancel editing")
//            }
//        }
//
//        Text(
//            text = message,
//            style = MaterialTheme.typography.bodySmall,
//            color = if (message.startsWith("Could not") ||
//                message.startsWith("Please") ||
//                message.startsWith("The saved")
//            ) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
//        )
//
//        HorizontalDivider()
//
//        if (plants.isEmpty()) {
//            Text(
//                text = if (isLoaded) {
//                    "Your catalog is empty. Add your first plant above."
//                } else {
//                    "Loading your catalog..."
//                },
//                style = MaterialTheme.typography.bodyMedium
//            )
//        } else {
//            Text(
//                text = "Saved plants (${plants.size})",
//                style = MaterialTheme.typography.titleMedium
//            )
//
//            plants.forEach { plant ->
//                Card(modifier = Modifier.fillMaxWidth()) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(12.dp),
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        Text(
//                            text = plant.name,
//                            style = MaterialTheme.typography.titleMedium
//                        )
//
//                        plant.note?.takeIf { it.isNotBlank() }?.let { savedNote ->
//                            Text(
//                                text = savedNote,
//                                style = MaterialTheme.typography.bodyMedium
//                            )
//                        }
//
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Spacer(modifier = Modifier.weight(1f))
//                            OutlinedButton(
//                                onClick = {
//                                    plantName = plant.name
//                                    note = plant.note.orEmpty()
//                                    editingPlantId = plant.id
//                                    message = "Editing ${plant.name}."
//                                }
//                            ) {
//                                Text("Edit")
//                            }
//                            OutlinedButton(
//                                onClick = {
//                                    try {
//                                        val updatedPlants = repository.deletePlant(plant.id)
//                                        plants.clear()
//                                        plants.addAll(updatedPlants)
//
//                                        if (editingPlantId == plant.id) {
//                                            plantName = ""
//                                            note = ""
//                                            editingPlantId = null
//                                        }
//                                        message = "Plant deleted."
//                                    } catch (exception: Exception) {
//                                        message = exception.message ?: "Could not delete this plant."
//                                    }
//                                }
//                            ) {
//                                Text("Delete")
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//    }
//}

package com.example.plantify

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.plantify.ui.theme.PlantifyTheme
import java.text.DateFormat
import java.util.Date
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private enum class PlantifyTab(val title: String) {
    DIAGNOSTICS("Diagnostics"),
    HISTORY("History"),
    CATALOG("My Catalog")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlantifyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlantifyHomeScreen()
                }
            }
        }
    }
}

@Composable
fun PlantifyHomeScreen() {
    var selectedTab by remember { mutableStateOf(PlantifyTab.DIAGNOSTICS) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Text(
            text = "🌱 Plantify",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        TabRow(selectedTabIndex = selectedTab.ordinal) {
            PlantifyTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = { Text(tab.title) }
                )
            }
        }

        when (selectedTab) {
            PlantifyTab.DIAGNOSTICS -> PlantDiagnosisScreen()
            PlantifyTab.HISTORY -> PlantDiagnosisHistoryScreen()
            PlantifyTab.CATALOG -> PlantCatalogScreen()
        }
    }
}

@Composable
fun PlantDiagnosisScreen() {
    val context = LocalContext.current
    val historyRepository = remember(context) { PlantDiagnosisHistoryRepository(context) }
    var plantNameText by remember { mutableStateOf("") }
    var symptomsText by remember { mutableStateOf("") }
    var resultText by remember {
        mutableStateOf("The result will appear here after the diagnostics...")
    }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "AI Diagnostics", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = plantNameText,
            onValueChange = { plantNameText = it },
            label = { Text("Plant name (e.g., Monstera)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = symptomsText,
            onValueChange = { symptomsText = it },
            label = { Text("Describe the symptoms (e.g., yellow leaves)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Button(
            onClick = {
                val cleanedPlantName = plantNameText.trim()
                val cleanedSymptoms = symptomsText.trim()

                if (cleanedPlantName.length < 2 || cleanedSymptoms.length < 3) {
                    resultText = "Enter a plant name (at least 2 characters) and symptoms (at least 3 characters)."
                    return@Button
                }

                isLoading = true
                resultText = "We are analyzing..."
                val request = PlantDiagnosisRequest(
                    plant_name = cleanedPlantName,
                    symptoms = cleanedSymptoms
                )

                PlantApiService.create().diagnosePlant(request)
                    .enqueue(object : Callback<PlantDiagnosisResponse> {
                        override fun onResponse(
                            call: Call<PlantDiagnosisResponse>,
                            response: Response<PlantDiagnosisResponse>
                        ) {
                            isLoading = false
                            val data = response.body()
                            val isCompleteSuccess = response.isSuccessful &&
                                    data != null &&
                                    data.status.equals("success", ignoreCase = true) &&
                                    data.category.isNotBlank() &&
                                    data.severity.isNotBlank() &&
                                    data.recommendations.isNotEmpty() &&
                                    data.recommendations.all { it.isNotBlank() }

                            if (isCompleteSuccess && data != null) {
                                resultText = formatDiagnosisResult(data)
                                try {
                                    historyRepository.saveSuccessfulDiagnosis(
                                        plantName = cleanedPlantName,
                                        symptoms = cleanedSymptoms,
                                        result = data
                                    )
                                    resultText += "\n\nSaved to diagnosis history."
                                } catch (exception: Exception) {
                                    // Keep the successful result visible even if local persistence fails.
                                    resultText += "\n\nWarning: the result could not be saved to history."
                                }
                            } else {
                                resultText = when {
                                    !response.isSuccessful -> when (response.code()) {
                                        400 -> "The entered data is invalid."
                                        422 -> "Check the plant name and symptom description."
                                        502 -> "The AI service returned an invalid response."
                                        503 -> "The AI service is temporarily unavailable."
                                        else -> "Server error: ${response.code()}"
                                    }
                                    else -> "The server returned an incomplete or invalid diagnosis. It was not saved."
                                }
                            }
                        }

                        override fun onFailure(call: Call<PlantDiagnosisResponse>, t: Throwable) {
                            isLoading = false
                            resultText = "Network error. The diagnosis was not saved to history."
                        }
                    })
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Processing..." else "Diagnose")
        }

        HorizontalDivider()
        Text(text = resultText, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun formatDiagnosisResult(data: PlantDiagnosisResponse): String {
    val recommendationsText = data.recommendations.joinToString(separator = "\n") { "• $it" }
    return "Category: ${data.category}\nSeverity: ${data.severity}\n\nRecommendations:\n$recommendationsText"
}

@Composable
fun PlantDiagnosisHistoryScreen() {
    val context = LocalContext.current
    val repository = remember(context) { PlantDiagnosisHistoryRepository(context) }
    val history = remember { mutableStateListOf<PlantDiagnosisHistoryItem>() }
    var isLoaded by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf<PlantDiagnosisHistoryItem?>(null) }
    var itemToDelete by remember { mutableStateOf<PlantDiagnosisHistoryItem?>(null) }

    fun reloadHistory() {
        try {
            history.clear()
            history.addAll(repository.getAll())
            message = ""
        } catch (exception: Exception) {
            message = exception.message ?: "Could not load diagnosis history."
        } finally {
            isLoaded = true
        }
    }

    LaunchedEffect(repository) { reloadHistory() }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete diagnosis?") },
            text = { Text("This saved diagnosis will be permanently removed from this device.") },
            confirmButton = {
                TextButton(onClick = {
                    val target = itemToDelete
                    if (target != null) {
                        try {
                            history.clear()
                            history.addAll(repository.delete(target.id))
                            if (selectedItem?.id == target.id) selectedItem = null
                            message = "Diagnosis deleted."
                        } catch (exception: Exception) {
                            message = exception.message ?: "Could not delete this diagnosis."
                        }
                    }
                    itemToDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Diagnosis History", style = MaterialTheme.typography.titleLarge)

        if (selectedItem != null) {
            val item = selectedItem!!
            Text("Diagnosis details", style = MaterialTheme.typography.titleMedium)
            Text("Date: ${formatTimestamp(item.createdAt)}")
            Text("Status: ${item.status}")
            HorizontalDivider()
            Text("Plant", style = MaterialTheme.typography.titleSmall)
            Text(item.plantName)
            Text("Symptoms", style = MaterialTheme.typography.titleSmall)
            Text(item.symptoms)
            Text("Category", style = MaterialTheme.typography.titleSmall)
            Text(item.category)
            Text("Severity", style = MaterialTheme.typography.titleSmall)
            Text(item.severity)
            Text("Recommendations", style = MaterialTheme.typography.titleSmall)
            item.recommendations.forEach { Text("• $it") }
            Button(onClick = { selectedItem = null }, modifier = Modifier.fillMaxWidth()) {
                Text("Back to history")
            }
            OutlinedButton(
                onClick = { itemToDelete = item },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Delete this diagnosis") }
        } else {
            if (message.isNotBlank()) {
                Text(
                    message,
                    color = if (message.startsWith("Could not")) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            when {
                !isLoaded -> Text("Loading diagnosis history...")
                history.isEmpty() -> Text("Your diagnosis history is empty. Complete a successful diagnosis to save it here.")
                else -> {
                    Text("Saved diagnoses (${history.size})", style = MaterialTheme.typography.titleMedium)
                    history.forEach { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedItem = item }
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(item.plantName, style = MaterialTheme.typography.titleMedium)
                                Text("${item.category} • ${item.severity}")
                                Text("Symptoms: ${item.symptoms}", style = MaterialTheme.typography.bodyMedium)
                                Text(formatTimestamp(item.createdAt), style = MaterialTheme.typography.bodySmall)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(onClick = { itemToDelete = item }) {
                                        Text("Delete")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

private fun formatTimestamp(timestamp: Long): String =
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(timestamp))

@Composable
fun PlantCatalogScreen() {
    val context = LocalContext.current
    val repository = remember(context) { PlantCatalogRepository(context) }
    val plants = remember { mutableStateListOf<PlantCatalogItem>() }

    var plantName by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var editingPlantId by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf("Your plants are saved on this device.") }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(repository) {
        try {
            plants.clear()
            plants.addAll(repository.loadPlants())
            message = "Your plants are saved on this device."
        } catch (exception: Exception) {
            message = exception.message
                ?: "Could not load the catalog. Your saved data may be unavailable."
        } finally {
            isLoaded = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("My Plant Catalog", style = MaterialTheme.typography.titleLarge)
        Text("Keep a simple list of your plants and optional notes.", style = MaterialTheme.typography.bodyMedium)

        OutlinedTextField(
            value = plantName,
            onValueChange = { plantName = it },
            label = { Text("Plant name (required)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        Button(
            onClick = {
                if (plantName.isBlank()) {
                    message = "Please enter a plant name."
                } else {
                    try {
                        val currentEditingId = editingPlantId
                        val updatedPlants = if (currentEditingId == null) {
                            repository.addPlant(plantName, note)
                        } else {
                            repository.updatePlant(currentEditingId, plantName, note)
                        }
                        plants.clear()
                        plants.addAll(updatedPlants)
                        plantName = ""
                        note = ""
                        editingPlantId = null
                        message = if (currentEditingId == null) "Plant added to your catalog." else "Plant changes saved."
                    } catch (exception: Exception) {
                        message = exception.message ?: "Could not save this plant."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isLoaded
        ) { Text(if (editingPlantId == null) "Add plant" else "Save changes") }

        if (editingPlantId != null) {
            OutlinedButton(
                onClick = {
                    plantName = ""
                    note = ""
                    editingPlantId = null
                    message = "Editing cancelled."
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isLoaded
            ) { Text("Cancel editing") }
        }

        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = if (message.startsWith("Could not") || message.startsWith("Please") || message.startsWith("The saved"))
                MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )
        HorizontalDivider()

        if (plants.isEmpty()) {
            Text(if (isLoaded) "Your catalog is empty. Add your first plant above." else "Loading your catalog...")
        } else {
            Text("Saved plants (${plants.size})", style = MaterialTheme.typography.titleMedium)
            plants.forEach { plant ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(plant.name, style = MaterialTheme.typography.titleMedium)
                        plant.note?.takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            OutlinedButton(onClick = {
                                plantName = plant.name
                                note = plant.note.orEmpty()
                                editingPlantId = plant.id
                                message = "Editing ${plant.name}."
                            }) { Text("Edit") }
                            OutlinedButton(onClick = {
                                try {
                                    val updatedPlants = repository.deletePlant(plant.id)
                                    plants.clear()
                                    plants.addAll(updatedPlants)
                                    if (editingPlantId == plant.id) {
                                        plantName = ""
                                        note = ""
                                        editingPlantId = null
                                    }
                                    message = "Plant deleted."
                                } catch (exception: Exception) {
                                    message = exception.message ?: "Could not delete this plant."
                                }
                            }) { Text("Delete") }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
