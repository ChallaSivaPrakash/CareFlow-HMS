import random
import os
from fastapi import FastAPI, HTTPException, Response
from pydantic import BaseModel

from typing import List, Optional, Dict, Any

from app.agents.triage_agent import run_triage

app = FastAPI(
    title="CareFlow AI Triage Engine",
    description="AI-powered patient triage service for CareFlow HMS",
    version="1.0.0"
)

# Track initialization state
_initialized = False

@app.on_event("startup")
async def startup():
    global _initialized
    # Try connecting to Qdrant
    try:
        from qdrant_client import QdrantClient
        qdrant_url = os.getenv("QDRANT_URL", "http://qdrant:6333")
        client = QdrantClient(url=qdrant_url)
        client.get_collections()  # Will raise if Qdrant not ready
        _initialized = True
        print(f"✅ Connected to Qdrant at {qdrant_url}")
    except Exception as e:
        print(f"⚠️ Qdrant not yet available: {e}")
        _initialized = False

# Request / Response Schemas
class TriageRequest(BaseModel):
    patient_id: str
    chief_complaint: str
    name: Optional[str] = None
    additional_notes: Optional[str] = None
    # Keep for backward compatibility
    patient_age: Optional[int] = None
    symptoms: Optional[List[str]] = None

class TriageResponse(BaseModel):
    triage_color: str          # RED | YELLOW | GREEN
    recommended_department: str
    confidence: float
    rationale: str
    requires_human_approval: bool
    warnings: List[str]
    suggested_department: str
    recommended_action: str
    error: Optional[str] = None

@app.get("/health", tags=["System"])
def health_check():
    return {
        "status": "healthy",
        "service": "careflow-ai-engine",
        "version": "1.0.0"
    }

@app.get("/health/ready", tags=["System"])
async def readiness():
    """Readiness probe — returns 200 only when dependencies are ready"""
    if not _initialized:
        return Response(status_code=503, content='{"status":"not_ready","reason":"Qdrant not connected"}')
    return {"status": "ready", "engine": "agentic-rag", "qdrant": "connected"}


@app.post("/api/ai/triage", response_model=TriageResponse, tags=["Triage"])
async def triage_patient(request: TriageRequest):
    if not request.chief_complaint or not request.patient_id:
        raise HTTPException(status_code=400, detail="patient_id and chief_complaint are required.")
    
    intake = {
        "patient_id": request.patient_id,
        "chief_complaint": request.chief_complaint,
        "name": request.name
    }
    if request.additional_notes:
        intake["additional_notes"] = request.additional_notes
    
    # Await the async agent
    decision = await run_triage(intake)
    
    return TriageResponse(
        triage_color=decision.get("triage_color", "YELLOW"),
        recommended_department=decision.get("suggested_department", "Emergency"),
        confidence=decision.get("confidence", 0.5),
        rationale=decision.get("rationale", "No rationale provided"),
        requires_human_approval=decision.get("requires_human_approval", True),
        warnings=decision.get("warnings", []),
        suggested_department=decision.get("suggested_department", "Emergency"),
        recommended_action=decision.get("recommended_action", "Patient requires assessment"),
        error=decision.get("error")
    )