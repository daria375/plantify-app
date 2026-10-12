# Plantify 🌿

**Plantify** is an eco-friendly mobile application designed to help users take care of their indoor plants, featuring an integrated AI component for diagnosing plant conditions and diseases based on text symptom descriptions.

---

## 🚀 Key Features

- **AI Symptom Diagnostics:** Describe plant issues in natural language and get instant analysis from artificial intelligence with detailed categories, severity levels, and recommendations.
- **Care Organization:** Maintain plant profiles and track regular care routines.
- **Client-Server Architecture:** Reliable request processing, data validation, and error handling on the server side.

---

## 🛠 Tech Stack

### Mobile Client (`plantify-mobile`)
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Networking Layer:** Retrofit2, OkHttp3
- **JSON Parsing:** Gson

### Backend Service (`plantify-backend`)
- **Language:** Python 3.10+
- **Framework:** FastAPI, Uvicorn
- **Data Validation:** Pydantic
- **AI Integration:** Google Gemini API (`google-genai` SDK) with Structured Outputs
- **Configuration:** `python-dotenv`

---

## 📂 Project Structure

```text
plantify-app/
├── plantify-backend/          # Server-side (Python / FastAPI)
│   ├── main.py                # Server logic, prompt engineering, and endpoints
│   ├── requirements.txt       # Python dependencies
│   └── .env                   # Local configurations and API key (isolated)
│
└── plantify-mobile/           # Mobile app (Android Studio / Kotlin)
    └── app/src/main/java/com/example/plantify/
        ├── MainActivity.kt    # Main screen and UI (Jetpack Compose)
        ├── PlantApiService.kt # Retrofit configuration and backend communication
        └── PlantModels.kt     # Request and response data models
⚙️ How to Run Locally
1. Backend Setup
Navigate to the backend directory:

Bash
cd plantify-backend
Install the required dependencies:

Bash
pip install -r requirements.txt
Create a .env file in the root of the plantify-backend folder and add your Google Gemini API key:

Фрагмент коду
GEMINI_API_KEY=your_gemini_api_key_here
Run the Uvicorn server:

Bash
uvicorn main:app --reload --host 0.0.0.0 --port 8000
The server will be available at: http://localhost:8000 (Swagger UI: http://localhost:8000/docs).

2. Mobile App Setup
Open the plantify-mobile project in Android Studio.

Check the PlantApiService.kt file and make sure BASE_URL points to your PC's local network IP address (e.g., http://192.168.0.107:8000/).

Run the project on an Android emulator or physical device.

📌 API Endpoints Overview
1. System Health Check
GET /health

Response: {"status": "ok"}

2. AI Plant Diagnostics
POST /api/v1/diagnose

Example Request:

JSON
{
  "plant_name": "Monstera",
  "symptoms": "yellow leaves and brown spots"
}
Example Response:

JSON
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
🔒 Security
All secret keys (GEMINI_API_KEY) are isolated in the .env environment and are excluded from version control.

Server-side error handling is implemented to hide technical stack traces from users.

The application requests only essential permissions (INTERNET).
