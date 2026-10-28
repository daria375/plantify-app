# Requirements

## Existing diagnosis functionality

- Enter a plant name and symptoms.
- Reject empty input fields on the mobile client.
- Send diagnosis requests to the FastAPI backend.
- Generate an AI-based category, severity, and recommendations.
- Display loading, success, HTTP error, and network error states.

## Personal plant catalog requirements

- Display the user's saved plants.
- Add a plant with a required name.
- Allow an optional text note, if supported by the data model.
- Reject an empty or whitespace-only plant name.
- Handle leading and trailing whitespace consistently.
- Edit an existing plant entry without creating a duplicate.
- Preserve the identifier of an entry when editing it, if the model uses persistent identifiers.
- Remove a selected plant.
- Preserve catalog entries after the application is closed and reopened.
- Refresh the displayed catalog after an add, edit, or delete operation.
- Keep catalog operations local and independent of backend availability.
- Keep the existing diagnosis scenario working.

## Diagnosis history requirements

- Save a diagnosis request locally before attempting to send it.
- Assign each saved request a persistent identifier and creation timestamp.
- Track the request status using `pending`, `sending`, `ready`, or `error`.
- Save valid diagnosis results in the corresponding history entry.
- Preserve failed requests and make them available for retry.
- Retry an existing entry without creating a duplicate entry.
- Recover requests left in the `sending` state as `error` when restoring history.
- Display the saved history entries, including their status.
- Allow users to view the details of a history entry.
- Allow users to delete an individual entry.
- Allow users to clear the entire history after confirmation.
- Preserve history entries between application launches.
- Keep history browsing and management independent of backend availability.
- Use the existing diagnosis API when a saved request is submitted or retried.
- Do not introduce additional API endpoints solely for history management.

## Acceptance criteria

- A valid plant can be added and appears in the catalog.
- An empty or whitespace-only name is rejected.
- An existing plant can be edited.
- Editing a plant does not create an unintended duplicate.
- A selected plant can be deleted without deleting other entries.
- Catalog data persists after restarting the application.
- An empty catalog has a clear empty-state message.
- Catalog operations do not require a network request.
- The existing AI diagnosis flow still works.
- Mobile and backend API models remain compatible.
- The Android project builds successfully.
- Automated tests or documented manual checks cover the new and existing behavior.
- A diagnosis request is saved in history before it is sent.
- A saved request can be submitted later.
- A successful diagnosis updates the corresponding history entry with its result.
- A failed request remains available in history and can be retried.
- Retrying a request does not create a duplicate history entry.
- An interrupted request is recovered with the `error` status.
- A user can view an entry's details, delete one entry, and clear the history after confirmation.
- History entries persist after restarting the application.
- Saved history can be browsed and managed when the backend is unavailable.
- History management does not change the existing diagnosis API contract.

## Security and data handling

- Never store API keys or secrets in source code or documentation.
- Use synthetic or anonymized data for tests and examples.
- Do not introduce unnecessary backend endpoints or transmit catalog data to the AI service.