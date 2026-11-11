
from dataclasses import dataclass
from typing import List


@dataclass
class DiagnosisResult:
    category: str
    severity: str
    recommendations: List[str]
