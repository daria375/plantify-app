# API Contract

## GET /health

Returns basic backend status information.

## POST /api/v1/diagnose

Accepts a plant name and symptom description.

Request fields:

- `plant_name`: required string, minimum length 2.
- `symptoms`: required string, minimum length 3.

The successful response contains `status`, `category`, `severity`, and `recommendations`.

## Error behavior

- `400`: the backend identifies input as incorrect through the current AI response handling.
- `422`: request validation failure.
- `502`: general AI processing or provider failure.
- `503`: temporarily unavailable or overloaded AI service.
- Network errors are handled by the mobile application.

## Catalog

The local plant catalog does not introduce HTTP endpoints or change the existing API request and response models.

Catalog storage is handled within the Android application.

## Diagnosis History

The diagnosis history is managed locally by the Android application.

- Diagnosis requests are saved locally before being sent to the backend.
- Each history entry has a status: `pending`, `sending`, `ready`, or `error`.
- Successful responses are stored with the corresponding diagnosis details.
- Failed requests remain in the history and can be retried.
- Retrying an existing entry updates that entry rather than creating a duplicate.
- Users can view entry details, delete individual entries, and clear the history after confirmation.
- Requests that were interrupted while being sent are recovered as `error` when the application restores the history.

The history uses local Android storage and does not introduce new HTTP endpoints or change the existing diagnosis API contract.