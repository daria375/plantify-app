# Testing

## Backend checks

From the backend directory, activate the project's Python environment and run:

```bash
pip install -r requirements.txt
uvicorn main:app --reload
```

Verify `/health` and test `/api/v1/diagnose` through `/docs`.

## Existing diagnosis regression checks

- Submit a valid plant name and meaningful symptoms.
- Submit empty input from the mobile UI.
- Check behavior when the backend is unreachable.
- Verify that the app displays a successful response when the AI request works.

## Catalog checks

- Add a plant with a valid name.
- Try adding a blank name.
- Add a plant with an optional note.
- Add more than one plant.
- Delete a plant.
- Restart the app and verify that remaining entries persist.
- Verify the empty-state message.
- Verify that catalog operations work when the backend is stopped.

Record actual results. Do not mark unexecuted checks as passed.
