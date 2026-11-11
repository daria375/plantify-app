
import pytest
from fastapi.testclient import TestClient

from main import app
from api.dependencies import get_diagnosis_service

from services.diagnosis_service import DiagnosisService
from infrastructure.providers.mock_provider import MockProvider

from domain.models import DiagnosisResult
from domain.errors import (
    InvalidPlantInputError,
    AIProviderUnavailableError,
    AIProviderError
)


class InvalidInputProvider:

    def diagnose(self, plant_name, symptoms):
        raise InvalidPlantInputError("Incorrect plant data")


class UnavailableProvider:

    def diagnose(self, plant_name, symptoms):
        raise AIProviderUnavailableError("AI service unavailable")


class ErrorProvider:

    def diagnose(self, plant_name, symptoms):
        raise AIProviderError("Unexpected AI error")


@pytest.fixture
def client():
    app.dependency_overrides[get_diagnosis_service] = (
        lambda: DiagnosisService(MockProvider())
    )

    with TestClient(app) as test_client:
        yield test_client

    app.dependency_overrides.clear()


def test_health_check(client):
    response = client.get("/health")

    assert response.status_code == 200
    assert response.json()["status"] == "ok"


def test_successful_diagnosis(client):
    response = client.post(
        "/api/v1/diagnose",
        json={
            "plant_name": "Rose",
            "symptoms": "Yellow spots on leaves"
        }
    )

    assert response.status_code == 200

    data = response.json()

    assert data["status"] == "success"
    assert data["category"] == "Leaf spot"
    assert data["severity"] == "Medium"
    assert len(data["recommendations"]) == 3
    assert data["request_id"]


def test_invalid_request_data(client):
    response = client.post(
        "/api/v1/diagnose",
        json={
            "plant_name": "R",
            "symptoms": "OK"
        }
    )

    assert response.status_code == 422


def test_invalid_plant_input(client):
    app.dependency_overrides[get_diagnosis_service] = (
        lambda: DiagnosisService(InvalidInputProvider())
    )

    response = client.post(
        "/api/v1/diagnose",
        json={
            "plant_name": "asdf",
            "symptoms": "random text"
        }
    )

    assert response.status_code == 400


def test_ai_unavailable(client):
    app.dependency_overrides[get_diagnosis_service] = (
        lambda: DiagnosisService(UnavailableProvider())
    )

    response = client.post(
        "/api/v1/diagnose",
        json={
            "plant_name": "Rose",
            "symptoms": "Yellow spots on leaves"
        }
    )

    assert response.status_code == 503


def test_ai_provider_error(client):
    app.dependency_overrides[get_diagnosis_service] = (
        lambda: DiagnosisService(ErrorProvider())
    )

    response = client.post(
        "/api/v1/diagnose",
        json={
            "plant_name": "Rose",
            "symptoms": "Yellow spots on leaves"
        }
    )

    assert response.status_code == 502

def test_request_id_is_unique(client):
    first_response = client.post("/api/v1/diagnose", json={
        "plant_name": "Rose",
        "symptoms": "Yellow spots on leaves"
    })

    second_response = client.post("/api/v1/diagnose", json={
        "plant_name": "Monstera",
        "symptoms": "Brown edges on leaves"
    })

    assert first_response.status_code == 200
    assert second_response.status_code == 200

    first_id = first_response.json()["request_id"]
    second_id = second_response.json()["request_id"]

    assert first_id
    assert second_id
    assert first_id != second_id