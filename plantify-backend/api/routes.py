
import logging
from uuid import uuid4

from fastapi import APIRouter, Depends, HTTPException

from schemas.diagnosis import (
    PlantDiagnosisRequest,
    PlantDiagnosisResponse
)
from services.diagnosis_service import DiagnosisService
from api.dependencies import get_diagnosis_service

from domain.errors import (
    InvalidPlantInputError,
    AIProviderUnavailableError,
    AIProviderError
)


logger = logging.getLogger(__name__)

router = APIRouter()


@router.get("/health", tags=["System"])
def health_check():
    return {
        "status": "ok",
        "service": "plantify-backend",
        "message": "AI Service is up and running"
    }


@router.post(
    "/api/v1/diagnose",
    response_model=PlantDiagnosisResponse,
    tags=["Diagnostics"]
)
def diagnose_plant(
    request: PlantDiagnosisRequest,
    service: DiagnosisService = Depends(get_diagnosis_service)
):
    request_id = str(uuid4())

    try:
        result = service.diagnose(
            plant_name=request.plant_name,
            symptoms=request.symptoms
        )

        logger.info("Diagnosis completed: request_id=%s", request_id)

        return PlantDiagnosisResponse(
            status="success",
            category=result.category,
            severity=result.severity,
            recommendations=result.recommendations,
            request_id=request_id
        )

    except InvalidPlantInputError as error:
        raise HTTPException(
            status_code=400,
            detail=str(error)
        )

    except AIProviderUnavailableError:
        logger.warning("AI unavailable: request_id=%s", request_id)

        raise HTTPException(
            status_code=503,
            detail="The AI service is temporarily overloaded. Please try again later."
        )

    except AIProviderError:
        logger.exception("AI provider error: request_id=%s", request_id)

        raise HTTPException(
            status_code=502,
            detail="Failed to receive a response from the AI service."
        )
