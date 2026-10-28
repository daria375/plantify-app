# Testing

## 1. Backend startup and checks

From the backend directory, activate the project's Python environment if one is used.

Install dependencies:

```bash
pip install -r requirements.txt
```

Start the backend:

```bash
uvicorn main:app --reload
```

These commands assume that `requirements.txt` and `main.py` are located in the backend directory and that Uvicorn is included in the project's dependencies.

Verify the health endpoint:

```text
GET /health
```

Open the FastAPI documentation at `/docs` to inspect and test the diagnosis endpoint.

Record the actual startup and endpoint results. Do not mark checks as passed unless they have been executed.

## 2. Existing diagnosis regression checks

- Submit a valid plant name and meaningful symptoms.
- Submit empty input through the mobile UI.
- Check behavior when the backend is unreachable.
- Verify a successful response when the AI request works.
- Verify that the mobile app handles backend and AI errors appropriately.
- Confirm that the diagnosis request and response models remain compatible with the backend.

## 3. Catalog functional checks

- Add a plant with a valid name.
- Try adding a plant with an empty name.
- Try adding a plant with a whitespace-only name.
- Verify handling of leading and trailing whitespace.
- Add multiple plants.
- Edit an existing plant and verify that its data is updated.
- Verify that editing does not create an unintended duplicate.
- Delete one plant and verify that other entries remain.
- Restart the application and verify that the remaining entries persist.
- Verify the empty-state message when no plants are saved.
- Verify that catalog operations work when the backend is stopped.

## 4. Diagnosis history functional checks

- Submit a valid diagnosis request and verify that a history entry is created.
- Save a request for later and verify that it remains in history without being submitted immediately.
- Verify the `pending` status for a saved request that has not been sent.
- Submit a saved request and verify that its status changes during processing.
- Verify that a successful response updates the existing entry with the diagnosis details.
- Simulate a backend or AI service failure and verify that the entry remains in history with the `error` status.
- Retry a failed entry and verify that the same entry is updated rather than duplicated.
- Verify that a request left in the `sending` state is recovered as `error` after restoring the history.
- Open a history entry and verify its plant name, symptoms, status, and available result or error details.
- Delete one history entry and verify that other entries remain.
- Clear the history and confirm that all entries are removed.
- Verify that the history persists after restarting the application.
- Stop the backend and verify that saved entries can still be viewed and managed.
- Verify that submitting or retrying a request requires backend connectivity.
- Confirm that history operations do not introduce additional HTTP endpoints or change the existing diagnosis API contract.

## 5. Build and automated tests

Run the Android build using the Gradle Wrapper from the correct Android project directory.

Example, if the Android project uses the `app` module:

```powershell
.\gradlew.bat :app:assembleDebug
```

Run the available automated tests using the project's actual module and test configuration.

Record the exact commands and their results. If a test cannot be automated, document the manual steps and expected result.

## 6. Git and documentation checks

- Inspect `git diff`.
- Run `git diff --check`.
- Check for accidentally tracked secrets or temporary files.
- Verify that README startup instructions match the actual project structure.
- Verify that the Knowledge Base describes the final implementation.

Do not expose API keys, passwords, tokens, personal data, medical data, or financial data in test output or documentation.

## 7. Results

Record each check as one of:

- `PASS` — executed and passed.
- `FAIL` — executed and failed.
- `NOT RUN` — not executed.

Do not mark unexecuted checks as passed.