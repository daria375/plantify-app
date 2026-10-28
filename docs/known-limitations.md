# Plantify — Known Limitations

## Purpose

This document describes the known limitations of the current Plantify MVP.

These limitations are documented and acknowledged but are not addressed in the current implementation scope. They may be reconsidered in future development iterations.

## 1. AI Diagnostic Accuracy

AI-generated plant diagnoses may be inaccurate, incomplete, or based on insufficient symptom information.

The application relies on text descriptions and does not independently verify the diagnosis against laboratory results or an expert assessment.

**Current decision:** AI results are provided as generated recommendations. Improving diagnostic accuracy and adding independent verification are outside the current scope.

## 2. Text-Only Plant Diagnostics

The current diagnostic workflow uses text input to describe plant symptoms.

The application does not currently analyze photographs of plants or automatically detect visual symptoms.

**Current decision:** Image-based diagnosis is not included in the current MVP.

## 3. Dependence on External AI Services

AI diagnostics require a working backend, internet connectivity, a valid Gemini API key, and availability of the external AI service.

If the backend or AI service is unavailable, the diagnostic feature may not return a result.

**Current decision:** The application uses the existing backend and Gemini integration. Offline AI diagnosis and alternative AI providers are not currently implemented.

## 4. Basic Backend Health Check

The `/health` endpoint checks whether the backend responds to a health-check request. It does not necessarily verify that the Gemini API is reachable or that AI diagnostics can be completed successfully.

**Current decision:** A more comprehensive dependency health check is not included in the current implementation.

## 5. Local-Only Plant Catalog

Plant catalog data is stored on the user's device.

The catalog is not synchronized with a remote server and is not automatically shared across devices.

**Current decision:** The catalog remains local. Cloud synchronization and user accounts are outside the current MVP scope.

## 6. Catalog Data Recovery

Catalog data depends on the application's local storage. Clearing application data or uninstalling the application may result in the loss of saved entries.

The application does not currently provide cloud backup or a dedicated catalog export and import mechanism.

**Current decision:** Backup, recovery, and cross-device data transfer are not currently implemented.

## 7. Limited Plant Care Management

The catalog is intended for basic plant organization. It is not a complete plant care management system.

Features such as watering reminders, care schedules, plant growth history, and photo galleries are not currently included.

**Current decision:** The current implementation focuses on basic catalog operations. Additional care management features are outside the current scope.

## 8. Network Configuration

The Android application must be configured with a backend URL reachable from the selected emulator or physical device.

A URL that works on the development computer may not work on a physical device if it uses `localhost` or an inaccessible network address.

**Current decision:** Backend connectivity is configured for the development environment. Automatic environment detection and production deployment configuration are not currently implemented.

## 9. Limited Automated Test Coverage

Some application scenarios may require manual verification, particularly UI interactions, local catalog persistence, and behavior when external services are unavailable.

The actual level of automated test coverage depends on the tests implemented in the repository.

**Current decision:** The project focuses on validating the current MVP functionality. Expanding automated test coverage is not part of the current implementation scope.

## Summary

The limitations listed above are known and documented. They define the current boundaries of the Plantify MVP and are intentionally left unresolved in this development iteration.

They should be reconsidered if the project expands to include production deployment, image-based diagnosis, cloud synchronization, advanced plant care management, or more comprehensive automated testing.