
from pydantic import BaseModel, Field
from typing import List


class PlantDiagnosisRequest(BaseModel):
    plant_name: str = Field(
        ...,
        min_length=2,
        description="Plant name"
    )

    symptoms: str = Field(
        ...,
        min_length=3,
        description="Text description of the problem's symptoms"
    )


class PlantDiagnosisResponse(BaseModel):
    status: str = "success"
    category: str
    severity: str
    recommendations: List[str]
    request_id: str
