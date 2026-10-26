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
