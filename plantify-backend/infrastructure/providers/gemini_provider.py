
import json

from google import genai
from google.genai import types
from pydantic import BaseModel

from core.config import settings
from domain.models import DiagnosisResult
from domain.errors import (
    InvalidPlantInputError,
    AIProviderUnavailableError,
    AIProviderError,
    InvalidAIResponseError
)


class GeminiDiagnosisData(BaseModel):
    category: str
    severity: str
    recommendations: list[str]


class GeminiProvider:
    def __init__(self):
        if not settings.GEMINI_API_KEY:
            raise RuntimeError(
                "GEMINI_API_KEY is not configured"
            )

        self.client = genai.Client(
            api_key=settings.GEMINI_API_KEY,
            http_options=types.HttpOptions(
                timeout=settings.GEMINI_TIMEOUT_MS
            )
        )

    def diagnose(
        self,
        plant_name: str,
        symptoms: str
    ) -> DiagnosisResult:

        prompt = f"""
        You are a strict expert agronomist and botanist.

        Plant name: {plant_name}
        Symptoms: {symptoms}

        If the plant name or symptoms are meaningless,
        random characters, or unrelated to plants,
        return:
        - category: Incorrect data
        - severity: High
        - recommendations: ["Please enter the actual name of the plant and a meaningful description of the symptoms."]

        Otherwise, provide a professional diagnosis in English:
        - category: name of the problem or disease
        - severity: Low, Medium, or High
        - recommendations: exactly 3 practical tips
        """

        try:
            response = self.client.models.generate_content(
                model=settings.GEMINI_MODEL,
                contents=prompt,
                config=types.GenerateContentConfig(
                    response_mime_type="application/json",
                    response_schema=GeminiDiagnosisData,
                    temperature=0.1
                )
            )

            if not response.text:
                raise InvalidAIResponseError(
                    "Empty AI response"
                )

            ai_data = json.loads(response.text)
            result = GeminiDiagnosisData.model_validate(ai_data)

            if result.category == "Incorrect data":
                raise InvalidPlantInputError(
                    "Incorrect data or a random sequence of characters has been entered."
                )

            if (
                not result.category.strip()
                or result.severity not in ["Low", "Medium", "High"]
                or len(result.recommendations) != 3
                or any(not item.strip() for item in result.recommendations)
            ):
                raise InvalidAIResponseError(
                    "The AI response does not meet the requirements."
                )

            return DiagnosisResult(
                category=result.category,
                severity=result.severity,
                recommendations=result.recommendations
            )

        except (
            InvalidPlantInputError,
            InvalidAIResponseError
        ):
            raise

        except Exception as error:
            error_text = str(error).upper()

            if (
                "503" in error_text
                or "UNAVAILABLE" in error_text
                or "RESOURCE_EXHAUSTED" in error_text
            ):
                raise AIProviderUnavailableError(
                    "The AI service is temporarily unavailable."
                ) from error

            raise AIProviderError(
                "Failed to receive a response from the AI service."
            ) from error
