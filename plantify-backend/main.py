
from fastapi import FastAPI
from api.routes import router


app = FastAPI(
    title="Plantify Backend API",
    description="Backend service for Plantify eco-app with modular AI diagnostics",
    version="3.0.0"
)

app.include_router(router)
