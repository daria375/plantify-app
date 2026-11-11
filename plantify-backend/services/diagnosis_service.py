
from domain.interfaces import AIProvider
from domain.models import DiagnosisResult


class DiagnosisService:

    def __init__(self, provider: AIProvider):
        self.provider = provider

    def diagnose(
        self,
        plant_name: str,
        symptoms: str
    ) -> DiagnosisResult:

        return self.provider.diagnose(
            plant_name=plant_name,
            symptoms=symptoms
        )
