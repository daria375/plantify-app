
class InvalidPlantInputError(Exception):
    """The provided plant data is invalid."""


class AIProviderUnavailableError(Exception):
    """The AI provider is temporarily unavailable."""


class AIProviderError(Exception):
    """An unexpected AI provider error occurred."""


class InvalidAIResponseError(AIProviderError):
    """The AI provider returned an invalid response."""
