# Architecture

## Existing mobile component

`MainActivity.kt` starts the Compose UI. `PlantDiagnosisScreen()` manages diagnosis input, loading state, results, and error messages.

`PlantModels.kt` contains Kotlin data classes for the diagnosis request and response.

`PlantApiService.kt` defines the Retrofit endpoint for the backend.

## Existing backend component

`main.py` defines the FastAPI application, the `/health` endpoint, the `/api/v1/diagnose` endpoint, and the Pydantic request and response models.

## Existing AI component

The backend uses the Google Gen AI SDK to request a structured JSON diagnosis from Gemini.

## Planned catalog component

`PlantCatalogRepository.kt` will manage local catalog persistence using SharedPreferences.

`PlantModels.kt` will define a catalog item containing an identifier, plant name, and optional note.

`MainActivity.kt` will provide the user interface for viewing, adding, and removing catalog entries.

## Catalog data flow

1. The UI requests the saved catalog from the repository.
2. The repository reads the locally stored data.
3. The UI displays the list.
4. When a plant is added or removed, the repository saves the updated list.
5. The UI refreshes to reflect the change.

## Architecture boundary

The catalog is local to the Android device. It does not use the FastAPI backend and does not change the diagnosis API contract.
