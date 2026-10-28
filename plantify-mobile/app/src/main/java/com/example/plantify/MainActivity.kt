package com.example.plantify

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
    var resultText by remember { mutableStateOf("The result will appear here after the diagnostics...") }
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
                val cleanName = plantNameText.trim()
                val cleanSymptoms = symptomsText.trim()
                if (cleanName.length < 2 || cleanSymptoms.length < 3) {
                    resultText = "Enter a plant name (at least 2 characters) and symptoms (at least 3 characters)."
                } else {
                    try {
                        // Save first, so the request is not lost if the network/backend fails.
                        val item = historyRepository.savePendingRequest(cleanName, cleanSymptoms)
                        isLoading = true
                        resultText = "Request saved. Sending it to the AI service..."
                        sendSavedDiagnosis(context, historyRepository, item.id) { updated, message ->
                            isLoading = false
                            resultText = if (updated?.status == DiagnosisStatus.READY) {
                                formatDiagnosisResult(
                                    PlantDiagnosisResponse(
                                        status = "success",
                                        category = updated.category,
                                        severity = updated.severity,
                                        recommendations = updated.recommendations
                                    )
                                ) + "\n\nSaved to diagnosis history."
                            } else {
                                "${message}\n\nThe request remains in diagnosis history. You can retry it there."
                            }
                        }
                    } catch (exception: Exception) {
                        resultText = exception.message ?: "Could not save the request locally."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Processing..." else "Diagnose now")
        }

        OutlinedButton(
            onClick = {
                val cleanName = plantNameText.trim()
                val cleanSymptoms = symptomsText.trim()
                if (cleanName.length < 2 || cleanSymptoms.length < 3) {
                    resultText = "Enter a plant name (at least 2 characters) and symptoms (at least 3 characters)."
                } else {
                    try {
                        historyRepository.savePendingRequest(cleanName, cleanSymptoms)
                        resultText = "Request saved as Pending. Open Diagnosis History and tap Send when you are ready."
                    } catch (exception: Exception) {
                        resultText = exception.message ?: "Could not save the request locally."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Save for later (offline queue)")
        }

        Text(
            "Use ‘Save for later’ when you do not have internet or want to send the request later.",
            style = MaterialTheme.typography.bodySmall
        )
        HorizontalDivider()
        Text(text = resultText, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun sendSavedDiagnosis(
    context: android.content.Context,
    repository: PlantDiagnosisHistoryRepository,
    itemId: String,
    onStarted: () -> Unit = {},
    onFinished: (PlantDiagnosisHistoryItem?, String) -> Unit
) {
    val sendingItem = try {
        repository.markSending(itemId)
    } catch (exception: Exception) {
        onFinished(null, exception.message ?: "Could not update the request status.")
        return
    }

    // A null result means the item is missing, already sending, or already completed.
    if (sendingItem == null) {
        onFinished(repository.getById(itemId), "This request is already being sent or has already completed.")
        return
    }

    onStarted()
    val request = PlantDiagnosisRequest(
        plant_name = sendingItem.plantName,
        symptoms = sendingItem.symptoms
    )

    try {
        PlantApiService.create().diagnosePlant(request)
            .enqueue(object : Callback<PlantDiagnosisResponse> {
                override fun onResponse(
                    call: Call<PlantDiagnosisResponse>,
                    response: Response<PlantDiagnosisResponse>
                ) {
                    val data = response.body()
                    val completeSuccess = response.isSuccessful &&
                            data != null &&
                            data.status.equals("success", ignoreCase = true) &&
                            data.category.isNotBlank() &&
                            data.severity.isNotBlank() &&
                            data.recommendations.isNotEmpty() &&
                            data.recommendations.all { it.isNotBlank() }

                    try {
                        if (completeSuccess && data != null) {
                            val ready = repository.markReady(itemId, data)
                            onFinished(ready, "Diagnosis completed.")
                        } else {
                            val errorMessage = when {
                                !response.isSuccessful -> when (response.code()) {
                                    400 -> "The entered data is invalid. Edit the request or try again."
                                    422 -> "Check the plant name and symptom description."
                                    502 -> "The AI service returned an invalid response. You can retry."
                                    503 -> "The AI service is temporarily unavailable. You can retry later."
                                    else -> "Server error: ${response.code()}. You can retry."
                                }
                                else -> "The server returned an incomplete diagnosis. You can retry."
                            }
                            repository.markError(itemId, errorMessage)
                            onFinished(repository.getById(itemId), errorMessage)
                        }
                    } catch (exception: Exception) {
                        val message = exception.message ?: "Could not update the saved diagnosis."
                        try { repository.markError(itemId, message) } catch (_: Exception) { }
                        onFinished(repository.getById(itemId), message)
                    }
                }

                override fun onFailure(call: Call<PlantDiagnosisResponse>, t: Throwable) {
                    val message = "Network error: ${t.localizedMessage ?: "cannot reach the backend"}. You can retry when the connection is available."
                    try {
                        repository.markError(itemId, message)
                    } catch (exception: Exception) {
                        onFinished(repository.getById(itemId), exception.message ?: message)
                        return
                    }
                    onFinished(repository.getById(itemId), message)
                }
            })
    } catch (exception: Exception) {
        val message = exception.message ?: "Could not start the request. You can retry."
        try { repository.markError(itemId, message) } catch (_: Exception) { }
        onFinished(repository.getById(itemId), message)
    }
}

private fun formatDiagnosisResult(data: PlantDiagnosisResponse): String {
    val recommendationsText = data.recommendations.joinToString(separator = "\n") { "• $it" }
    return "Category: ${data.category}\nSeverity: ${data.severity}\n\nRecommendations:\n$recommendationsText"
}

private fun statusLabel(status: String): String = when (status) {
    DiagnosisStatus.PENDING -> "Pending"
    DiagnosisStatus.SENDING -> "Sending"
    DiagnosisStatus.READY -> "Ready"
    DiagnosisStatus.ERROR -> "Error - you can retry"
    else -> status
}

@Composable
fun PlantDiagnosisHistoryScreen() {
    val context = LocalContext.current
    val repository = remember(context) { PlantDiagnosisHistoryRepository(context) }
    val history = remember { mutableStateListOf<PlantDiagnosisHistoryItem>() }
    val sendingIds = remember { mutableStateListOf<String>() }
    var isLoaded by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf<PlantDiagnosisHistoryItem?>(null) }
    var itemToDelete by remember { mutableStateOf<PlantDiagnosisHistoryItem?>(null) }
    var showClearConfirmation by remember { mutableStateOf(false) }

    fun reloadHistory() {
        try {
            val items = repository.getAll()
            history.clear()
            history.addAll(items)
            selectedItem = selectedItem?.let { selected -> items.firstOrNull { it.id == selected.id } }
            message = ""
        } catch (exception: Exception) {
            message = exception.message ?: "Could not load diagnosis history."
        } finally {
            isLoaded = true
        }
    }

    fun sendItem(id: String) {
        if (id in sendingIds) return
        sendingIds.add(id)
        reloadHistory()
        sendSavedDiagnosis(context, repository, id, onStarted = { reloadHistory() }) { _, resultMessage ->
            sendingIds.remove(id)
            message = resultMessage
            reloadHistory()
        }
    }

    LaunchedEffect(repository) { reloadHistory() }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete request?") },
            text = { Text("This request and its saved result will be removed from this device.") },
            confirmButton = {
                TextButton(onClick = {
                    val target = itemToDelete
                    if (target != null) {
                        try {
                            repository.delete(target.id)
                            if (selectedItem?.id == target.id) selectedItem = null
                            reloadHistory()
                            message = "History item deleted."
                        } catch (exception: Exception) {
                            message = exception.message ?: "Could not delete this item."
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

    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = { Text("Clear diagnosis history?") },
            text = { Text("All saved requests and results will be permanently removed from this device.") },
            confirmButton = {
                TextButton(onClick = {
                    try {
                        repository.clear()
                        history.clear()
                        selectedItem = null
                        message = "Diagnosis history cleared."
                    } catch (exception: Exception) {
                        message = exception.message ?: "Could not clear history."
                    }
                    showClearConfirmation = false
                }) { Text("Clear all") }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmation = false }) { Text("Cancel") }
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

        if (message.isNotBlank()) {
            Text(
                message,
                color = if (message.contains("Could not", ignoreCase = true) || message.contains("error", ignoreCase = true))
                    MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (selectedItem != null) {
            val item = selectedItem!!
            Text("Request details", style = MaterialTheme.typography.titleMedium)
            Text("Date: ${formatTimestamp(item.createdAt)}")
            Text("Status: ${statusLabel(item.status)}")
            HorizontalDivider()
            Text("Plant", style = MaterialTheme.typography.titleSmall)
            Text(item.plantName)
            Text("Symptoms", style = MaterialTheme.typography.titleSmall)
            Text(item.symptoms)

            if (item.status == DiagnosisStatus.READY) {
                Text("Category", style = MaterialTheme.typography.titleSmall)
                Text(item.category)
                Text("Severity", style = MaterialTheme.typography.titleSmall)
                Text(item.severity)
                Text("Recommendations", style = MaterialTheme.typography.titleSmall)
                item.recommendations.forEach { Text("• $it") }
            } else {
                Text(item.errorMessage ?: when (item.status) {
                    DiagnosisStatus.PENDING -> "This request is saved locally and has not been sent yet."
                    DiagnosisStatus.SENDING -> "The request is currently being processed."
                    else -> "The request was not completed. You can try sending it again."
                })
            }

            if (item.status == DiagnosisStatus.PENDING || item.status == DiagnosisStatus.ERROR) {
                Button(
                    onClick = { sendItem(item.id) },
                    enabled = item.id !in sendingIds,
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (item.id in sendingIds) "Sending..." else "Send / Retry") }
            }
            Button(onClick = { selectedItem = null }, modifier = Modifier.fillMaxWidth()) {
                Text("Back to history")
            }
            OutlinedButton(
                onClick = { itemToDelete = item },
                enabled = item.id !in sendingIds,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Delete this item") }
        } else {
            if (!isLoaded) {
                Text("Loading diagnosis history...")
            } else if (history.isEmpty()) {
                Text("Your diagnosis history is empty. Run a diagnosis or save a request for later.")
            } else {
                Text("Saved requests and diagnoses (${history.size})", style = MaterialTheme.typography.titleMedium)
                OutlinedButton(
                    onClick = { showClearConfirmation = true },
                    enabled = sendingIds.isEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Clear all history") }

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
                            Text("Status: ${statusLabel(item.status)}")
                            if (item.status == DiagnosisStatus.READY) {
                                Text("${item.category} • ${item.severity}")
                            } else {
                                Text(item.errorMessage ?: item.symptoms, style = MaterialTheme.typography.bodyMedium)
                            }
                            Text("Symptoms: ${item.symptoms}", style = MaterialTheme.typography.bodyMedium)
                            Text(formatTimestamp(item.createdAt), style = MaterialTheme.typography.bodySmall)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (item.status == DiagnosisStatus.PENDING || item.status == DiagnosisStatus.ERROR) {
                                    Button(
                                        onClick = { sendItem(item.id) },
                                        enabled = item.id !in sendingIds,
                                        modifier = Modifier.weight(1f)
                                    ) { Text(if (item.id in sendingIds) "Sending..." else "Send / Retry") }
                                }
                                OutlinedButton(
                                    onClick = { itemToDelete = item },
                                    enabled = item.id !in sendingIds,
                                    modifier = Modifier.weight(1f)
                                ) { Text("Delete") }
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
            message = exception.message ?: "Could not load the catalog. Your saved data may be unavailable."
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

