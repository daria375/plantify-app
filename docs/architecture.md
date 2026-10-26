# Architecture

## Mobile diagnosis component

`MainActivity.kt` starts the Compose UI and provides the entry point for the mobile application.

`PlantModels.kt` contains Kotlin data models, including the diagnosis request and response models.

`PlantApiService.kt` defines the Retrofit communication with the backend.

The diagnosis flow sends plant information and symptoms to the FastAPI backend and displays the returned result or an error.

## Backend component

`main.py` defines the FastAPI application, the `/health` endpoint, the `/api/v1/diagnose` endpoint, and the Pydantic request and response models.

## AI component

The backend uses the Google Gen AI SDK to request a structured JSON diagnosis from Gemini.

## Personal plant catalog

The catalog is implemented within the Android application.

`PlantCatalogRepository.kt` manages local catalog persistence, provided this matches the current implementation.

`PlantModels.kt` may contain the catalog item model, including the fields actually supported by the application.

`MainActivity.kt` or the relevant Compose screen provides the catalog user interface.

The catalog supports adding, viewing, editing, and removing entries, according to the implemented functionality.

## Catalog data flow

1. The UI obtains catalog entries through the catalog repository.
2. The repository loads saved entries from local storage.
3. The UI displays the catalog.
4. When a plant is added, edited, or removed, the repository updates the stored data.
5. The UI refreshes to reflect the change.
6. Saved entries remain available after reopening the application, subject to the behavior of the local storage implementation.

## Architecture boundary

The catalog is local to the Android device. It does not require the FastAPI backend or Gemini and does not change the diagnosis API contract.

The diagnosis component continues to use Retrofit to communicate with the existing backend.

## Dependencies

The catalog should use the existing project dependencies where possible. New persistence libraries or backend endpoints should not be introduced unless required and explicitly justified.