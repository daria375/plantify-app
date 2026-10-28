# Technical Decisions

## Existing technology stack

The project uses Kotlin and Jetpack Compose for Android, Retrofit and OkHttp for networking, FastAPI and Pydantic for the backend, and Google Gemini for AI-based diagnosis.

These choices reflect the existing implementation.

## Local personal plant catalog

**Decision:** implement catalog management within the Android application.

**Reason:** adding, viewing, editing, and deleting personal plant entries does not require AI diagnosis or remote processing. A backend service, authentication, and cloud database would add complexity beyond the current MVP requirements.

**Decision:** use SharedPreferences with JSON serialization for the catalog, provided this matches the actual repository implementation.

**Reason:** this approach is suitable for a small local catalog and can reuse the existing Gson dependency. It avoids adding a separate persistence library.

**Decision:** keep catalog operations independent of the diagnosis API.

**Reason:** catalog management does not require changes to the existing diagnosis request or response models.

## Local diagnosis history

**Decision:** manage diagnosis history within the Android application.

**Reason:** saving and browsing previous diagnosis requests and results does not require a separate backend history service. Local storage keeps the feature independent of additional server-side infrastructure.

**Decision:** use `SharedPreferences` with Gson JSON serialization for diagnosis history, matching the existing repository implementation.

**Reason:** this reuses the existing local persistence approach and avoids introducing another storage dependency.

**Decision:** assign each history entry a persistent identifier and update the existing entry when retrying a failed request.

**Reason:** retries should not create duplicate history entries for the same saved request.

**Decision:** represent the request lifecycle with `pending`, `sending`, `ready`, and `error` statuses.

**Reason:** these statuses allow the application to distinguish saved requests, active processing, successful diagnoses, and failed or interrupted requests.

**Decision:** keep history management separate from the diagnosis API contract.

**Reason:** history operations are local. The existing diagnosis endpoint remains responsible for processing diagnosis requests.

## Incremental development

Implement changes in small, focused steps. Inspect `git diff`, run relevant checks, and update the Knowledge Base after implementation.

## Testing and security

Use synthetic or anonymized test data. Never commit API keys or other secrets. Do not report a test as successful unless it has actually been executed.