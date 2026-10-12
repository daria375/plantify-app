package com.example.plantify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plantify.ui.theme.PlantifyTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlantifyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlantDiagnosisScreen()
                }
            }
        }
    }
}

@Composable
fun PlantDiagnosisScreen() {
    var plantNameText by remember { mutableStateOf("") }
    var symptomsText by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("The result will appear here after the diagnostics...") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🌱 Plantify — AI Diagnostics",
            style = MaterialTheme.typography.titleLarge
        )

        // Поле введення назви рослини
        OutlinedTextField(
            value = plantNameText,
            onValueChange = { plantNameText = it },
            label = { Text("Plant name (e.g., Monstera)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Поле введення симптомів
        OutlinedTextField(
            value = symptomsText,
            onValueChange = { symptomsText = it },
            label = { Text("Describe the symptoms (e.g., yellow leaves)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Кнопка відправки запиту на бекенд
        Button(
            onClick = {
                if (plantNameText.isBlank() || symptomsText.isBlank()) {
                    resultText = "Please enter the name of the plant and describe the symptoms."
                    return@Button
                }
                isLoading = true
                resultText = "We are analyzing..."

                val apiService = PlantApiService.create()
                val request = PlantDiagnosisRequest(
                    plant_name = plantNameText,
                    symptoms = symptomsText
                )

                apiService.diagnosePlant(request).enqueue(object : Callback<PlantDiagnosisResponse> {
                    override fun onResponse(
                        call: Call<PlantDiagnosisResponse>,
                        response: Response<PlantDiagnosisResponse>
                    ) {
                        isLoading = false
                        if (response.isSuccessful && response.body() != null) {
                            val data = response.body()!!
                            val recommendationsText = data.recommendations.joinToString(separator = "\n") { "• $it" }

                            resultText = "Category: ${data.category}\n" +
                                    "Severity: ${data.severity}\n\n" +
                                    "Recommendations:\n$recommendationsText"
                        } else {
                            resultText = when (response.code()) {
                                400 -> "The entered data is invalid."
                                422 -> "Check the plant name and symptom description."
                                502 -> "The AI service returned an invalid response."
                                503 -> "The AI service is temporarily unavailable."
                                else -> "Server error: ${response.code()}"
                            }
                        }
                    }

                    override fun onFailure(call: Call<PlantDiagnosisResponse>, t: Throwable) {
                        isLoading = false
                        resultText = "Network error: ${t.localizedMessage}"
                    }
                })
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Processing..." else "Diagnose")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Блок виведення результату
        Text(
            text = resultText,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}