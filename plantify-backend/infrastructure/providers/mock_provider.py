
from domain.models import DiagnosisResult


class MockProvider:

    def diagnose(
        self,
        plant_name: str,
        symptoms: str
    ) -> DiagnosisResult:

        return DiagnosisResult(
            category="Leaf spot",
            severity="Medium",
            recommendations=[
                "Remove affected leaves.",
                "Avoid watering the leaves.",
                "Monitor the plant for further symptoms."
            ]
        )
