# Plantify — Project Overview

## Purpose

Plantify is an Android application that provides AI-generated information about possible plant problems based on a plant name and a text description of symptoms.

The application also includes a personal plant catalog stored locally on the Android device.

## Target users

People who grow houseplants or other plants and want initial guidance about plant care and possible problems, as well as a convenient way to maintain a personal list of plants.

## Existing diagnosis scenario

1. The user enters a plant name and symptoms.
2. The Android application sends a request to the backend.
3. The backend validates the request and calls Google Gemini.
4. The backend returns a problem category, severity, and recommendations.
5. The Android application displays the result or an error message.

## Diagnosis history

The application maintains a local history of diagnosis requests and their results.

Users can:

- Save a diagnosis request for later.
- View saved diagnosis entries and their details.
- See whether a request is pending, being processed, completed, or has failed.
- Retry a failed request without creating a duplicate history entry.
- Delete an individual entry.
- Clear the entire history after confirmation.

History entries are stored locally using `SharedPreferences` and Gson JSON serialization. Requests and completed results remain available between application launches, subject to the limitations of local application storage.

Browsing and managing saved entries does not require a backend connection. Submitting or retrying a diagnosis does.

## Personal plant catalog

The Android application provides a local catalog for managing plant entries.

The catalog supports:
- Adding a plant with a required name.
- Viewing saved plants.
- Editing an existing plant entry.
- Removing a plant.
- Saving an optional note, if supported by the current data model.
- Persisting catalog data locally between application launches.

Catalog operations do not require the FastAPI backend or Gemini. The catalog is stored on the Android device and does not provide cloud synchronization.

## Technology stack

- Android: Kotlin and Jetpack Compose.
- Networking: Retrofit and OkHttp.
- JSON serialization: Gson.
- Backend: Python, FastAPI, and Pydantic.
- AI: Google Gemini through the Google Gen AI SDK.
- Local catalog storage: SharedPreferences, if confirmed by the current implementation.

## MVP boundaries

The application does not currently provide user authentication, cloud synchronization, image-based plant diagnosis, or remote backup of diagnosis history and catalog data.

The local catalog is intended for personal use on a single device.