# Requirements

## Existing functionality

- Enter a plant name and symptoms.
- Reject empty input fields on the mobile client.
- Send diagnosis requests to the FastAPI backend.
- Generate an AI-based category, severity, and recommendations.
- Display loading, success, HTTP error, and network error states.

## Lab 3 catalog requirements

- Display the user's list of plants.
- Add a plant with a required name.
- Allow an optional text note.
- Reject an empty or whitespace-only name.
- Remove a plant from the list.
- Preserve catalog entries after the application is closed and reopened.
- Keep the existing diagnosis scenario working.

## Acceptance criteria

- A plant can be added and appears in the list.
- An empty name is not accepted.
- A plant can be deleted.
- The catalog persists after restarting the app.
- An empty catalog has a clear empty-state message.
- The catalog uses local storage and does not require a network request.
- The existing AI diagnosis flow still works.
- The Android project builds successfully.
- Tests or documented manual checks cover the new behavior.
