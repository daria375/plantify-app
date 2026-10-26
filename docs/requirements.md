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

## Security and data handling

- Never store API keys or secrets in source code or documentation.
- Use synthetic or anonymized data for tests and examples.
- Do not introduce unnecessary backend endpoints or transmit catalog data to the AI service.