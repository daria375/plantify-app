# AI Component

## Purpose

The AI component generates a text-based assessment of possible plant problems.

## Integration

- Provider: Google Gemini.
- SDK: `google-genai`.
- Configuration: `GEMINI_API_KEY` from environment configuration.
- Model identifier in the current code: `gemini-3.5-flash-lite`.
- Output: JSON with status, category, severity, and recommendations.

The model identifier and availability must be verified in the actual environment.

## Backend Provider Architecture

The backend uses an AIProvider interface to separate diagnostic logic from the external AI implementation.

Current implementations:

- GeminiProvider — performs real diagnosis using the Google Gemini API.
- MockProvider — returns a predefined diagnostic result and is used for testing without external API calls.

The DiagnosisService receives an AIProvider implementation through constructor injection.

The provider implementation is selected through FastAPI dependency injection.

The Gemini API key, model identifier, and request timeout are managed centrally in `core/config.py`. Values are loaded from environment variables using `python-dotenv`.

Configuration variables:

- `GEMINI_API_KEY` — API key.
- `GEMINI_MODEL` — Gemini model identifier.
- `GEMINI_TIMEOUT_MS` — request timeout in milliseconds.

## Limitations

- The diagnosis is based on text input.
- The AI may produce inaccurate recommendations.
- Prompt instructions do not guarantee semantic input validation.
- The health endpoint does not independently verify Gemini availability.

## Catalog independence

The personal plant catalog does not use AI. Adding, displaying, and removing catalog entries must work without calling Gemini or the backend.
