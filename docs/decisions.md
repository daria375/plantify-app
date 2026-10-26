# Technical Decisions

## Existing stack

The project uses Kotlin and Jetpack Compose for Android, Retrofit for HTTP communication, FastAPI and Pydantic for the backend, and Google Gemini for AI-based diagnosis.

These choices reflect the existing implementation.

## Local plant catalog

Decision: implement the personal plant catalog entirely within the Android application.

Reason: the feature only needs local add, view, delete, and persistence behavior. A remote database, authentication, and new API endpoints would add unnecessary complexity.

Decision: use SharedPreferences for a small catalog stored as JSON.

Reason: the project already includes Gson through Retrofit's JSON converter, and SharedPreferences is available on Android without adding a separate persistence library.

Decision: keep the existing diagnosis API unchanged.

Reason: catalog management does not require AI diagnosis or backend communication.

## Incremental development

Implement the feature in small steps, inspect `git diff`, and test each completed step before committing it.
