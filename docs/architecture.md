# Plantify — System Architecture

## 1. Architecture Overview

Plantify is an Android mobile application with a client-server architecture for AI-powered plant diagnostics. It also provides a personal plant catalog that operates locally on the user's device.

The system consists of three main parts:

1. **Android mobile application** — provides the user interface, collects plant information, displays diagnostic results, and manages the personal plant catalog.
2. **FastAPI backend** — receives diagnostic requests, validates input data, communicates with the AI service, and returns structured responses.
3. **Google Gemini API** — analyzes the submitted plant symptoms and generates diagnostic information and care recommendations.

The plant catalog is independent of the backend and does not require an internet connection for its local operations.

## 2. High-Level Architecture

```text
                    USER
                     |
                     v
          +----------------------+
          | Android Application  |
          |   Kotlin / Compose   |
          +----------------------+
                     |
          +----------+-----------+
          |                      |
          v                      v
+-------------------+   +----------------------+
| Plant Diagnostics |   | Personal Plant       |
| UI and API Client |   | Catalog              |
+-------------------+   +----------------------+
          |                      |
          v                      v
+-------------------+   +----------------------+
| FastAPI Backend   |   | Local Device Storage |
| Input Validation  |   | SharedPreferences    |
+-------------------+   +----------------------+
          |
          v
+-------------------+
| Google Gemini API |
| AI Analysis       |
+-------------------+
```

The diagnostics flow communicates with the backend and the Gemini API. The catalog flow accesses local device storage independently.

## 3. Main Components

### 3.1. Android Mobile Application

The mobile application is implemented in Kotlin using Jetpack Compose.

Its responsibilities include:

- Displaying the application interface.
- Collecting plant names and symptom descriptions.
- Sending diagnostic requests to the backend.
- Displaying diagnostic results and handling loading or error states.
- Allowing users to manage their personal plant catalog.
- Saving and retrieving catalog data locally.

The mobile application communicates with the backend through HTTP requests using Retrofit and OkHttp. Gson is used for JSON serialization and deserialization.

### 3.2. Plant Diagnostics Flow

The diagnostics flow connects the mobile application to the backend.

1. The user enters a plant name and describes its symptoms.
2. The mobile application sends a request to the backend.
3. The backend validates the request data.
4. The backend sends the relevant information to the Gemini API.
5. The AI service generates a structured diagnostic response.
6. The backend returns the response to the mobile application.
7. The application displays the problem category, severity, and recommendations.

The backend exposes `POST /api/v1/diagnose` for diagnostics.

### 3.3. Backend Service

The backend is implemented in Python using FastAPI.

Its responsibilities include:

- Exposing HTTP API endpoints.
- Validating incoming requests with Pydantic.
- Preparing requests for the Gemini API.
- Processing AI-generated results.
- Returning structured JSON responses.
- Handling validation errors and AI service failures.

The backend exposes `GET /health` for a basic health check and `POST /api/v1/diagnose` for plant diagnostics.

### 3.4. AI Service

The application uses the Google Gemini API through the `google-genai` SDK.

The AI component receives plant information and symptom descriptions and generates a response containing:

- Diagnostic status.
- Problem category.
- Severity level.
- Care recommendations.

The backend uses structured output requirements to keep the response compatible with the application's data models.

The Gemini API key is configured through the backend's environment variables.

### 3.5. Personal Plant Catalog

The personal plant catalog is a local feature of the Android application.

Its responsibilities include:

- Adding plant entries.
- Displaying saved plants.
- Removing plant entries.
- Persisting catalog data locally between application restarts.

Catalog data is stored using the application's local storage mechanism. In the current implementation, this is `SharedPreferences` with JSON serialization through Gson.

The catalog does not require a dedicated backend endpoint and does not depend on the Gemini API.

## 4. Data Flows

### 4.1. Plant Diagnostics

```text
User
  |
  v
Android UI
  |
  v
Retrofit HTTP Request
  |
  v
FastAPI Backend
  |
  v
Input Validation
  |
  v
Gemini API
  |
  v
Structured AI Response
  |
  v
FastAPI Response
  |
  v
Android UI Displays Results
```

### 4.2. Plant Catalog

```text
User
  |
  v
Catalog UI
  |
  v
Catalog Data Management
  |
  v
Local Storage
  |
  v
Saved Catalog Entries
```

Catalog operations are handled locally and do not pass through the backend.

## 5. Communication and Data Contracts

The mobile application and backend communicate through HTTP and JSON.

The main diagnostic endpoint is:

- `POST /api/v1/diagnose`

The request contains a plant name and a symptom description. The response contains the diagnostic status, category, severity, and recommendations.

The backend validates incoming data before processing the diagnostic request. The mobile application uses corresponding data models to serialize requests and deserialize responses.

The catalog uses local data storage and does not currently have a server-side API contract.

## 6. Configuration and Security Boundaries

The backend requires a Gemini API key configured through the `GEMINI_API_KEY` environment variable.

The mobile application must use a backend URL that is reachable from the selected Android emulator or physical device.

The Gemini API key must remain on the backend and must not be embedded in the Android application or committed to version control.

## 7. Architectural Boundaries

The current architecture separates two independent application features:

- **AI diagnostics:** depends on the mobile-to-backend connection and availability of the AI service.
- **Personal plant catalog:** operates locally and does not require the backend or AI service.

The backend is responsible for diagnostics and AI integration. Local catalog management remains the responsibility of the Android application.

This separation allows the catalog to function independently and helps preserve the existing diagnostics API when the catalog is developed or modified.