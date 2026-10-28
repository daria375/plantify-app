# Plantify 🌿

Plantify is an eco-friendly Android application designed to help users take care of their indoor plants. It combines an AI-powered plant diagnostics feature with a personal plant catalog.

## 🚀 Key Features

- **AI Plant Diagnostics:** Describe plant symptoms in natural language and receive an AI-generated analysis, including a problem category, severity level, and recommendations.
- **Diagnosis History:** Save diagnosis requests for later, review previous results, inspect entry details, retry failed requests, delete individual entries, and clear the history.
- **Diagnosis Status Tracking:** Track requests using `pending`, `sending`, `ready`, and `error` statuses.
- **Personal Plant Catalog:** Add and view plant profiles, organize plant information, and remove plants from the catalog.
- **Local Data Persistence:** Catalog data is stored locally on the device, allowing it to remain available after restarting the application.
- **Client-Server Architecture:** The mobile application communicates with a FastAPI backend for plant diagnostics.
- **Input Validation and Error Handling:** Validate user input and handle backend and AI service errors.

## 🛠 Tech Stack

### Mobile Client (`plantify-mobile`)

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Networking:** Retrofit 2, OkHttp 3
- **JSON Parsing:** Gson
- **Local Storage:** SharedPreferences for the plant catalog

### Backend Service (`plantify-backend`)

- **Language:** Python 3.10+
- **Framework:** FastAPI
- **Server:** Uvicorn
- **Data Validation:** Pydantic
- **AI Integration:** Google Gemini API using the `google-genai` SDK
- **Configuration:** `python-dotenv`

## 📂 Project Structure

```text
plantify-app/
├── plantify-backend/
│   ├── main.py
│   ├── requirements.txt
│   ├── .env                  # Local environment variables; do not commit
│   └── .gitignore
│
└── plantify-mobile/
    ├── gradlew
    ├── gradlew.bat
    └── app/
        └── src/main/java/com/example/plantify/
            ├── MainActivity.kt
            ├── PlantDiagnosisScreen.kt
            ├── PlantApiService.kt
            ├── PlantModels.kt
            ├── PlantDiagnosisHistoryRepository.kt
            └── PlantCatalogRepository.kt
```

*Note: The mobile file list is representative. Adjust it to match the actual files in your project.*

## ⚙️ How to Run Locally

### 1. Backend Setup

Open a terminal and navigate to the backend directory:

```bash
cd plantify-backend
```

Install the required Python dependencies:

```bash
pip install -r requirements.txt
```

Create a `.env` file in the `plantify-backend` directory and add your Google Gemini API key:

```env
GEMINI_API_KEY=your_gemini_api_key_here
```

Replace the placeholder with your own API key. Do not share or commit the real key.

Start the backend server:

```bash
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

The backend should now be accessible at:

- **API base URL:** `http://localhost:8000`
- **Swagger UI:** `http://localhost:8000/docs`
- **Health check:** `http://localhost:8000/health`

Keep the backend terminal running while using AI diagnostics in the mobile application.

### 2. Mobile App Setup

1. Open the `plantify-mobile` project in Android Studio.
2. Wait for Gradle synchronization to finish.
3. Open `PlantApiService.kt` and check the configured `BASE_URL`.
4. Set the URL to an address accessible from your Android device or emulator.
5. Run the application on the selected device.

**Important:** `localhost` refers to the device itself when used by an Android application. For a physical device, use your computer's local network IP address, for example:

```text
http://192.168.0.107:8000/
```

Replace the example address with your computer's actual IP address. The phone and computer generally need to be on the same network, and the firewall must allow the connection.

For an Android emulator, use the appropriate host address for your emulator configuration.

The plant catalog is stored locally and does not require the backend to be running. AI diagnostics require the backend and a valid Gemini API key.

## 🌱 Plant Catalog

The personal plant catalog allows users to organize information about their plants.

- Add plants to the catalog.
- View saved plant profiles.
- Remove plants from the catalog.
- Keep catalog data locally on the device between application restarts.

The catalog uses local storage and does not require a separate backend endpoint. Its data is not automatically synchronized between devices or uploaded to a server.

Uninstalling the application or clearing its data may remove the saved catalog.

## 🕘 Diagnosis History

Plantify stores diagnosis requests and their results locally on the Android device.

The diagnosis history supports:

- Saving a request for later.
- Tracking request status: `pending`, `sending`, `ready`, or `error`.
- Viewing previous diagnosis entries and their details.
- Retrying failed requests without creating duplicate history entries.
- Deleting individual entries.
- Clearing all entries after confirmation.
- Recovering requests interrupted during processing.

History data is stored using `SharedPreferences` and Gson JSON serialization. Saved entries persist between application launches, but clearing application data or uninstalling the app may remove them.

Browsing and managing saved entries does not require the backend to be running. Sending or retrying a diagnosis requires the backend and a valid Gemini API configuration.

The history is local and does not require additional backend endpoints or changes to the existing diagnosis API contract.

## 📌 API Endpoints

### 1. System Health Check

**Method:** `GET`

**Endpoint:** `/health`

Example response:

```json
{
  "status": "ok"
}
```

This endpoint checks whether the backend is responding. It does not necessarily verify that the Gemini API is available.

### 2. AI Plant Diagnostics

**Method:** `POST`

**Endpoint:** `/api/v1/diagnose`

Example request:

```json
{
  "plant_name": "Monstera",
  "symptoms": "yellow leaves and brown spots"
}
```

Example response:

```json
{
  "status": "success",
  "category": "Chlorosis due to overwatering",
  "severity": "Medium",
  "recommendations": [
    "Check soil moisture and reduce watering frequency.",
    "Ensure the pot has proper drainage holes.",
    "Apply a balanced fertilizer for foliage plants."
  ]
}
```

The response above is an example. Actual diagnostic results depend on the submitted information and the AI service.

### Error Handling

The backend uses HTTP error responses for invalid requests and service failures. Depending on the error, the client may receive responses such as:

- `400 Bad Request` — invalid input detected by application logic.
- `422 Unprocessable Entity` — request validation failed.
- `502 Bad Gateway` — an upstream AI request failed.
- `503 Service Unavailable` — the AI service is unavailable.

Refer to the backend implementation and Swagger UI for the exact response details.

## 🧪 Testing and Verification

Before considering the application ready, verify the following scenarios:

- The backend starts successfully and `/health` returns a successful response.
- A valid plant name and symptom description produce a diagnostic response when the AI service is available.
- Invalid or incomplete input is handled appropriately.
- Backend or AI service failures are handled without crashing the application.
- A plant can be added to and displayed in the catalog.
- A saved plant remains in the catalog after restarting the application.
- A plant can be removed from the catalog.
- The catalog remains usable when the backend is unavailable.
- The existing AI diagnostics feature continues to work after catalog changes.
- A diagnosis request is saved to history before being sent.
- A request can be saved for later and submitted afterward.
- Successful results are saved in the corresponding history entry.
- Failed requests remain available for retry.
- Retrying a failed request does not create a duplicate entry.
- Interrupted requests are recovered with the `error` status.
- History entries and their details can be viewed.
- Individual entries can be deleted, and the entire history can be cleared after confirmation.
- History persists after restarting the application.
- Saved history can be browsed and managed while the backend is unavailable.

Record the actual results of the tests you run. Do not mark a test as passed unless it has been executed.

## 🔒 Security

- Store the Gemini API key in the backend's local `.env` file.
- Exclude `.env` from version control using `.gitignore`.
- Never commit real API keys, passwords, or other secrets.
- Keep error handling appropriate for users; avoid exposing sensitive configuration or unnecessary internal details.
- The Android application requires network access to communicate with the backend.

**Important:** A `.gitignore` rule does not automatically remove a secret that has already been committed. Check Git tracking status before pushing changes to a remote repository.