
from typing import Protocol
from domain.models import DiagnosisResult


class AIProvider(Protocol):

    def diagnose(
        self,
        plant_name: str,
        symptoms: str
    ) -> DiagnosisResult:
        ...
