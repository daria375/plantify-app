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

## Limitations

- The diagnosis is based on text input.
- The AI may produce inaccurate recommendations.
- Prompt instructions do not guarantee semantic input validation.
- The health endpoint does not independently verify Gemini availability.

## Catalog independence

The personal plant catalog does not use AI. Adding, displaying, and removing catalog entries must work without calling Gemini or the backend.
