"""
 careflow-ai-engine/app/agents/triage_agent.py

 Triage Agent for CareFlow Hospital Management System.
 Evaluates patient intake data and assigns RED/YELLOW/GREEN severity levels.
 Uses LangChain with OpenAI function calling for structured decision-making.
"""
# Workaround for @tool async compatibility
import sys
if sys.version_info < (3, 11):
    # Wrap async tools with StructuredTool
    from langchain_core.tools import StructuredTool
    # ... use StructuredTool.from_function instead

import os
import logging
from typing import Optional

import httpx
from langchain_openai import ChatOpenAI
from langchain_core.tools import tool
from langchain_core.agents import AgentFinish
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain.agents import AgentExecutor, create_openai_functions_agent
# from app.agents.triage_agent import run_triage


logger = logging.getLogger(__name__)

# ──────────────────────────────────────────────
# Configuration
# ──────────────────────────────────────────────

BACKEND_BASE_URL = os.getenv("BACKEND_BASE_URL", "http://backend:8080")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
OPENAI_MODEL = os.getenv("OPENAI_MODEL", "gpt-4o-mini")
REQUEST_TIMEOUT = float(os.getenv("REQUEST_TIMEOUT", "10.0"))
AGENT_MAX_ITERATIONS = int(os.getenv("AGENT_MAX_ITERATIONS", "6"))

# Standardised triage keywords that indicate RED status
RED_FLAG_KEYWORDS = [
    "chest pain", "chest tightness", "cardiac arrest", "heart attack", "myocardial infarction",
    "stroke", "cerebrovascular", "hemorrhage", "bleeding uncontrolled",
    "difficulty breathing", "respiratory distress", "not breathing", "apnea",
    "unconscious", "unresponsive", "seizure", "anaphylaxis",
    "sepsis", "septic shock", "major trauma", "multi-system trauma",
    "overdose", "poisoning", "suicidal", "self-harm",
]

# ──────────────────────────────────────────────
# Tools
# ──────────────────────────────────────────────

@tool
async def get_patient_vitals(patient_id: str) -> str:
    """
    Fetch and return a clean summary of the patient's current vitals
    from the backend API.

    Args:
        patient_id: The unique identifier of the patient (e.g. "P12345").

    Returns:
        A formatted string summarising the patient's vital signs.
    """
    url = f"{BACKEND_BASE_URL}/api/patients/{patient_id}"
    headers = {"Accept": "application/json"}

    logger.debug("Fetching vitals for patient %s from %s", patient_id, url)

    try:
        async with httpx.AsyncClient(timeout=REQUEST_TIMEOUT) as client:
            response = await client.get(url, headers=headers)
            response.raise_for_status()
            data = response.json()

        # Extract vitals with sensible defaults
        vitals = data.get("vitals", data)  # handle nested or flat response
        summary = (
            f"Patient: {data.get('name', patient_id)}\n"
            f"Age: {data.get('age', 'N/A')}\n"
            f"Heart Rate: {vitals.get('heart_rate', 'N/A')} bpm\n"
            f"Blood Pressure: {vitals.get('blood_pressure', 'N/A')} mmHg\n"
            f"Temperature: {vitals.get('temperature', 'N/A')} °C\n"
            f"Respiratory Rate: {vitals.get('respiratory_rate', 'N/A')} /min\n"
            f"Oxygen Saturation: {vitals.get('oxygen_saturation', 'N/A')} %\n"
            f"Pain Level: {vitals.get('pain_level', 'N/A')} /10\n"
            f"Allergies: {vitals.get('allergies', 'None reported')}\n"
        )
        logger.info("Successfully retrieved vitals for patient %s", patient_id)
        return summary

    except httpx.HTTPStatusError as e:
        error_msg = f"Backend returned HTTP {e.response.status_code} for patient {patient_id}: {e.response.text[:200]}"
        logger.error(error_msg)
        return f"ERROR: {error_msg}"

    except httpx.RequestError as e:
        error_msg = f"Network error fetching vitals for patient {patient_id}: {str(e)}"
        logger.error(error_msg)
        return f"ERROR: {error_msg}"

    except (ValueError, KeyError, TypeError) as e:
        error_msg = f"Unexpected response format for patient {patient_id}: {str(e)}"
        logger.error(error_msg)
        return f"ERROR: {error_msg}"


@tool
async def check_bed_availability() -> str:
    """
    Check current bed occupancy and availability across all departments
    in the hospital.

    Returns:
        A formatted string showing available beds per department / ward.
    """
    url = f"{BACKEND_BASE_URL}/api/beds/occupancy"
    headers = {"Accept": "application/json"}

    logger.debug("Checking bed availability from %s", url)

    try:
        async with httpx.AsyncClient(timeout=REQUEST_TIMEOUT) as client:
            response = await client.get(url, headers=headers)
            response.raise_for_status()
            data = response.json()

        # Normalise whether the API returns a list or a dict with wards/units
        occupancy_data = data.get("wards", data.get("departments", data.get("beds", data)))
        if isinstance(occupancy_data, list):
            lines = ["**Bed Availability Report:**"]
            for entry in occupancy_data:
                ward = entry.get("ward", entry.get("department", entry.get("name", "Unknown")))
                total = entry.get("total_beds", entry.get("total", 0))
                available = entry.get("available_beds", entry.get("available", 0))
                occupied = total - available
                lines.append(f"  • {ward}: {available}/{total} available ({occupied} occupied)")
            return "\n".join(lines)
        elif isinstance(occupancy_data, dict):
            lines = ["**Bed Availability Report:**"]
            for ward, info in occupancy_data.items():
                total = info.get("total_beds", info.get("total", 0))
                available = info.get("available_beds", info.get("available", 0))
                occupied = total - available
                lines.append(f"  • {ward}: {available}/{total} available ({occupied} occupied)")
            return "\n".join(lines)
        else:
            return f"Bed occupancy data: {occupancy_data}"

    except httpx.HTTPStatusError as e:
        error_msg = f"Backend returned HTTP {e.response.status_code} for bed occupancy: {e.response.text[:200]}"
        logger.error(error_msg)
        return f"ERROR: {error_msg}"

    except httpx.RequestError as e:
        error_msg = f"Network error checking bed availability: {str(e)}"
        logger.error(error_msg)
        return f"ERROR: {error_msg}"

    except (ValueError, KeyError, TypeError) as e:
        error_msg = f"Unexpected response format for bed occupancy: {str(e)}"
        logger.error(error_msg)
        return f"ERROR: {error_msg}"


# ──────────────────────────────────────────────
# Agent Initialisation
# ──────────────────────────────────────────────

_triage_agent_executor: Optional[AgentExecutor] = None


def _build_prompt() -> ChatPromptTemplate:
    """
    Build the system prompt that guides the triage agent's behaviour.
    The agent is instructed to always fetch vitals first, then evaluate
    the chief complaint before making a triage decision.
    """
    system_template = (
        "You are a senior triage nurse in a busy hospital emergency department. "
        "Your role is to assess incoming patients and assign a triage severity level.\n\n"
        "STRICT WORKFLOW:\n"
        "1. ALWAYS start by calling `get_patient_vitals` with the patient's ID to fetch their "
        "current vital signs.\n"
        "2. Then call `check_bed_availability` to see which departments have capacity.\n"
        "3. After gathering data, evaluate the patient's chief complaint and vitals together.\n\n"
        "TRIAGE CLASSIFICATION RULES:\n"
        "- **RED (Immediate / Life-Threatening):** Patient has a critical condition requiring "
        "immediate physician attention. This includes: cardiac arrest, chest pain with abnormal "
        "vitals, stroke symptoms (facial droop, weakness, speech difficulty), severe respiratory "
        "distress (SpO2 < 90%), uncontrolled haemorrhage, unconsciousness, anaphylaxis, sepsis, "
        "or major trauma.\n"
        "- **YELLOW (Urgent):** Patient has a serious condition but is stable for a short wait. "
        "Examples: moderate pain, localised infection, mild shortness of breath, fractures, "
        "headache with normal vitals.\n"
        "- **GREEN (Routine / Non-Urgent):** Minor complaints, normal vitals, no acute distress. "
        "Examples: mild cold symptoms, minor cuts, prescription refill requests.\n\n"
        "VALIDATION RULE: If you believe the patient should be RED, review the chief complaint "
        "for explicit RED-flag keywords (chest pain, stroke, difficulty breathing, unconscious, "
        "haemorrhage, anaphylaxis, sepsis, major trauma, overdose, seizure). If none of these "
        "keywords are present, downgrade to YELLOW with a warning note.\n\n"
        "OUTPUT FORMAT:\n"
        "Return a JSON object with exactly these fields:\n"
        "  - triage_color: \"RED\", \"YELLOW\", or \"GREEN\"\n"
        "  - confidence: float between 0.0 and 1.0\n"
        "  - rationale: string explaining the key factors that drove your decision\n"
        "  - requires_human_approval: boolean (true for RED, false otherwise)\n"
        "  - warnings: list of strings with any caveats or concerns\n"
        "  - suggested_department: string (e.g. \"cardiology\", \"neurology\", \"orthopaedics\", \"general\")\n"
        "  - recommended_action: string with one sentence on next steps\n\n"
        "Do NOT include any text outside the JSON object."
    )
    return ChatPromptTemplate.from_messages([
        ("system", system_template),
        MessagesPlaceholder(variable_name="chat_history", optional=True),
        ("human", "{input}"),
        MessagesPlaceholder(variable_name="agent_scratchpad"),
    ])


def get_triage_agent() -> AgentExecutor:
    """
    Initialise (or return cached) LangChain AgentExecutor configured
    with the triage tools and OpenAI function-calling agent.

    The agent is lazily initialised on first call so that environment
    variables and the OpenAI API key are available.
    """
    global _triage_agent_executor

    if _triage_agent_executor is not None:
        return _triage_agent_executor

    api_key = OPENAI_API_KEY
    if not api_key:
        raise RuntimeError(
            "OPENAI_API_KEY is not set. "
            "Please set it in your environment or .env file."
        )

    llm = ChatOpenAI(
        model=OPENAI_MODEL,
        temperature=0.1,
        api_key=api_key,
    )

    tools = [get_patient_vitals, check_bed_availability]
    prompt = _build_prompt()

    agent = create_openai_functions_agent(llm=llm, tools=tools, prompt=prompt)

    _triage_agent_executor = AgentExecutor(
        agent=agent,
        tools=tools,
        verbose=True,          # useful for debugging; set to False in production
        max_iterations=AGENT_MAX_ITERATIONS,
        handle_parsing_errors=True,
        early_stopping_method="generate",
    )

    logger.info(
        "Triage agent initialised (model=%s, max_iterations=%s)",
        OPENAI_MODEL, AGENT_MAX_ITERATIONS,
    )
    return _triage_agent_executor


# ──────────────────────────────────────────────
# Public API — run_triage
# ──────────────────────────────────────────────

# Expected keys in patient_intake
REQUIRED_INTAKE_FIELDS = {"patient_id", "chief_complaint"}


async def run_triage(patient_intake: dict) -> dict:
    """
    Evaluate a patient's intake data and return a structured triage decision.

    Args:
        patient_intake: Dictionary with at minimum:
            - patient_id (str):  The patient identifier used to fetch vitals.
            - chief_complaint (str):  The patient's primary complaint in free text.
            - name (str, optional):  Patient's name for display purposes.
            - additional_notes (str, optional):  Any extra context.

    Returns:
        A dictionary containing:
            - triage_color: "RED", "YELLOW", or "GREEN"
            - confidence: float
            - rationale: str
            - requires_human_approval: bool
            - warnings: list[str]
            - suggested_department: str
            - recommended_action: str
            - error: str (only present if an error occurred)

    Raises:
        ValueError: If required fields are missing from patient_intake.
    """
    # ── Validate input ──────────────────────────────────────────
    missing = REQUIRED_INTAKE_FIELDS - set(patient_intake.keys())
    if missing:
        raise ValueError(
            f"patient_intake missing required fields: {', '.join(sorted(missing))}. "
            f"Requires at least: patient_id and chief_complaint."
        )

    patient_id = patient_intake["patient_id"]
    chief_complaint = patient_intake["chief_complaint"]
    patient_name = patient_intake.get("name", patient_id)
    additional_notes = patient_intake.get("additional_notes", "")

    # Build a natural-language input for the agent
    input_text = (
        f"Triage patient {patient_name} (ID: {patient_id}).\n"
        f"Chief Complaint: {chief_complaint}\n"
    )
    if additional_notes:
        input_text += f"Additional Notes: {additional_notes}\n"

    input_text += (
        "\nPlease follow the triage workflow: fetch vitals, check bed availability, "
        "then classify as RED, YELLOW, or GREEN following the strict rules."
    )

    logger.info(
        "Running triage for patient %s (%s) — complaint: %s",
        patient_name, patient_id, chief_complaint,
    )

    # ── Invoke agent ────────────────────────────────────────────
    try:
        agent_executor = get_triage_agent()
        result = await agent_executor.ainvoke({"input": input_text})

        # The agent should return a JSON string in its output
        output_text = result.get("output", "")

        # Attempt to parse JSON from the output
        import json
        import re

        # Find the first JSON object in the output
        json_match = re.search(r'\{.*\}', output_text, re.DOTALL)
        if json_match:
            decision = json.loads(json_match.group())
        else:
            # Fallback: try parsing the entire output as JSON
            decision = json.loads(output_text)

        # ── Validate RED-flag keywords ──────────────────────────
        triage_color = decision.get("triage_color", "YELLOW")
        if triage_color == "RED":
            complaint_lower = chief_complaint.lower()
            has_red_keyword = any(
                keyword in complaint_lower for keyword in RED_FLAG_KEYWORDS
            )
            if not has_red_keyword:
                logger.warning(
                    "Agent suggested RED for %s but no RED-flag keywords found in chief complaint. "
                    "Downgrading to YELLOW with warning.",
                    patient_id,
                )
                decision["triage_color"] = "YELLOW"
                decision["confidence"] = min(decision.get("confidence", 0.5), 0.6)
                decision["requires_human_approval"] = False
                decision.setdefault("warnings", []).insert(
                    0,
                    "RED was initially suggested but downgraded to YELLOW because no critical "
                    "keywords (chest pain, stroke, difficulty breathing, etc.) were present in "
                    "the chief complaint. A human should review this case.",
                )

        # ── Ensure all expected fields exist ─────────────────────
        decision.setdefault("triage_color", "YELLOW")
        decision.setdefault("confidence", 0.5)
        decision.setdefault("rationale", "No rationale provided by agent.")
        decision.setdefault("requires_human_approval", decision.get("triage_color") == "RED")
        decision.setdefault("warnings", [])
        decision.setdefault("suggested_department", "general")
        decision.setdefault("recommended_action", "Patient requires assessment.")

        logger.info(
            "Triage result for %s: %s (confidence=%.2f)",
            patient_id, decision["triage_color"], decision["confidence"],
        )
        return decision

    except json.JSONDecodeError as e:
        error_msg = f"Failed to parse agent output as JSON: {e}"
        logger.error("%s\nRaw output: %s", error_msg, output_text)
        return _fallback_decision(patient_id, chief_complaint, error_msg)

    except Exception as e:
        error_msg = f"Triage agent execution failed: {type(e).__name__}: {str(e)}"
        logger.exception("Unexpected error during triage for %s", patient_id)
        return _fallback_decision(patient_id, chief_complaint, error_msg)


def _fallback_decision(patient_id: str, chief_complaint: str, error_detail: str) -> dict:
    """
    Generate a safe fallback triage decision when the agent pipeline fails.
    Defaults to YELLOW (urgent) to err on the side of caution.
    """
    logger.warning(
        "Returning fallback YELLOW decision for %s due to: %s",
        patient_id, error_detail,
    )
    return {
        "triage_color": "YELLOW",
        "confidence": 0.3,
        "rationale": (
            f"Fallback decision — the automated triage agent encountered an error: "
            f"{error_detail}. Patient has been assigned YELLOW (urgent) as a safe default. "
            f"Manual review is strongly recommended."
        ),
        "requires_human_approval": True,
        "warnings": [
            "Automated triage failed; this is a fallback decision.",
            f"Error: {error_detail}",
        ],
        "suggested_department": "emergency",
        "recommended_action": "Immediate manual review required — send patient to emergency department for in-person assessment.",
        "error": error_detail,
    }