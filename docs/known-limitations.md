# Known Limitations

## Existing application

- AI diagnoses may be inaccurate and should not be treated as guaranteed conclusions.
- The current diagnosis flow uses text input rather than plant photographs.
- The health endpoint does not independently verify Gemini availability.
- Some provider failures may be classified using exception information.
- The mobile base URL depends on the emulator or physical-device environment.

## Personal plant catalog

- Catalog data is stored locally on a single device.
- No account-based access or cloud synchronization is provided.
- Clearing application data or uninstalling the application may remove locally stored catalog entries.
- Catalog entries are not automatically synchronized between devices.
- Photographs, watering reminders, and diagnosis history are outside the current catalog scope unless they have been implemented separately.

## Validation

The actual behavior of catalog validation, persistence, and error handling should be confirmed through tests or documented manual checks. Do not assume that every edge case is covered until it has been verified.