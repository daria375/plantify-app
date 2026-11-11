
import os
from dotenv import load_dotenv

load_dotenv()


class Settings:
    """Centralized application configuration."""

    GEMINI_API_KEY: str = os.getenv("GEMINI_API_KEY", "")
    GEMINI_MODEL: str = os.getenv(
        "GEMINI_MODEL",
        "gemini-3.5-flash-lite"
    )
    GEMINI_TIMEOUT_MS: int = int(
        os.getenv("GEMINI_TIMEOUT_MS", "30000")
    )


settings = Settings()
