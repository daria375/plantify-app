
from services.diagnosis_service import DiagnosisService
from infrastructure.providers.gemini_provider import GeminiProvider


def get_diagnosis_service() -> DiagnosisService:
    provider = GeminiProvider()
    return DiagnosisService(provider)
