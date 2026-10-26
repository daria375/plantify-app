# Plantify — Project Overview

## Purpose

Plantify is an Android application that provides AI-generated information about possible plant problems based on a plant name and a text description of symptoms.

## Target users

People who grow houseplants or other plants and want initial guidance about plant care and possible problems.

## Existing user scenario

1. The user enters a plant name and symptoms.
2. The Android application sends a request to the backend.
3. The backend validates the request and calls Google Gemini.
4. The backend returns a problem category, severity, and recommendations.
5. The Android application displays the result or an error message.

## Planned feature for Lab 3

Add a local personal plant catalog. Users will be able to add plants, view the list, optionally save a short note, and remove plants.

The catalog will be stored on the Android device. It will not require accounts, a remote database, or new backend endpoints.

## Technology stack

- Android: Kotlin and Jetpack Compose.
- Networking: Retrofit, OkHttp, and Gson.
- Backend: Python, FastAPI, and Pydantic.
- AI: Google Gemini through the Google Gen AI SDK.
- Planned local storage: Android SharedPreferences.

## MVP boundaries

The app does not currently demonstrate user authentication, cloud synchronization, diagnosis history, or image-based plant diagnosis.
