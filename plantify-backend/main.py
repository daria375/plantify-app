from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from typing import List
import os
from dotenv import load_dotenv
from google import genai
from google.genai import types
import json

# Завантажуємо змінні з файлу .env
load_dotenv()

# Отримуємо ключ і ініціалізуємо клієнт Gemini явно
api_key = os.getenv("GEMINI_API_KEY")
client = genai.Client(api_key=api_key)

app = FastAPI(
    title="Plantify Backend API",
    description="Backend service for Plantify eco-app with real Gemini AI diagnostics",
    version="2.0.0"
)

# Pydantic-модель вхідного запиту
class PlantDiagnosisRequest(BaseModel):
    plant_name: str = Field(..., min_length=2, description="Plant name")
    symptoms: str = Field(..., min_length=3, description="Text description of the problem's symptoms")

# Pydantic-модель успішної відповіді
class PlantDiagnosisResponse(BaseModel):
    status: str = "success"
    category: str
    severity: str
    recommendations: List[str]

# 1. Ендпоінт перевірки доступності
@app.get("/health", tags=["System"])
def health_check():
    return {
        "status": "ok",
        "service": "plantify-backend",
        "message": "AI Service is up and running"
    }

@app.post("/api/v1/diagnose", response_model=PlantDiagnosisResponse, tags=["Diagnostics"])
def diagnose_plant(request: PlantDiagnosisRequest):
    try:
        # Оновлений промпт із суворою перевіркою на валідність даних
        prompt = f"""
        You are a strict expert agronomist and botanist. Verify the input data.
        Plant name: {request.plant_name}
        Symptoms: {request.symptoms}

        ATTENTION: If the plant name or symptoms consist of a random string of characters (such as "rrr", "asdf", etc.), are meaningless, or do not relate to plants, you must return exactly those values:
        - "category": "Incorrect data"
        - "severity": "High"
        - "recommendations": ["Please enter the actual name of the plant and a meaningful description of the symptoms."]

        Otherwise, conduct a professional diagnosis in English:
        - "category": name of the problem or disease.
        - "severity": "Low", "Medium", or "High".
        - "recommendations": a list of 3 practical tips.
        """

        response = client.models.generate_content(
            model='gemini-3.5-flash-lite',
            contents=prompt,
            config=types.GenerateContentConfig(
                response_mime_type="application/json",
                response_schema=PlantDiagnosisResponse,
                temperature=0.1,  #менша температура для суворого дотримання інструкцій
            ),
        )

        ai_data = json.loads(response.text)

        # Якщо модель визначила, що введено нісенітницю — повертаємо помилку 400
        if ai_data.get("category") == "Incorrect data":
            raise HTTPException(
                status_code=400,
                detail="Incorrect data or a random sequence of characters has been entered."
            )

        return PlantDiagnosisResponse(
            status="success",
            category=ai_data.get("category", "General stress"),
            severity=ai_data.get("severity", "Medium"),
            recommendations=ai_data.get("recommendations", ["Provide care for the plant and monitor it."])
        )

    except HTTPException as he:
        raise he
    except Exception as e:
        error_text = str(e)
        print(f"AI service error: {error_text}")

        if "503" in error_text or "UNAVAILABLE" in error_text:
            raise HTTPException(
                status_code=503,
                detail="The AI ​​service is temporarily overloaded. Please try again later."
            )

        raise HTTPException(
            status_code=502,
            detail="Failed to receive a response from the AI ​​service."
        )