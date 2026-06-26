"""
careflow-ai-engine/app/agents/booking_agent.py

Booking Agent for CareFlow Hospital Management System.
Uses LangChain with OpenAI function calling for natural conversation-based appointment booking.
"""
import os
import logging
from typing import Optional, List, Dict

import httpx
from langchain_openai import ChatOpenAI
from langchain_core.tools import tool
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain.agents import AgentExecutor, create_openai_functions_agent
from langchain_core.messages import BaseMessage, HumanMessage, AIMessage


logger = logging.getLogger(__name__)

# Configuration
BACKEND_BASE_URL = os.getenv("BACKEND_BASE_URL", "http://backend:8080")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
OPENAI_MODEL = os.getenv("OPENAI_MODEL", "gpt-4o-mini")
REQUEST_TIMEOUT = float(os.getenv("REQUEST_TIMEOUT", "10.0"))
AGENT_MAX_ITERATIONS = int(os.getenv("AGENT_MAX_ITERATIONS", "6"))

# In-memory session store for conversation history
_sessions: Dict[str, List[Dict]] = {}


# Tools
@tool
async def check_doctor_availability(department: str, date: str) -> str:
    """
    Check which doctors are available in a specific department on a given date.
    
    Args:
        department: The hospital department (e.g., "Cardiology", "Emergency", "Pediatrics")
        date: The date in ISO format (YYYY-MM-DD)
        
    Returns:
        A formatted string showing available doctors and their open time slots
    """
    url = f"{BACKEND_BASE_URL}/api/appointments/availability"
    params = {"department": department, "date": date}
    headers = {"Accept": "application/json"}
    
    logger.debug(f"Checking availability for {department} on {date} at {url}")
    
    try:
        async with httpx.AsyncClient(timeout=REQUEST_TIMEOUT) as client:
            response = await client.get(url, params=params, headers=headers)
            response.raise_for_status()
            data = response.json()
        
        if not data:
            return f"No doctors available in {department} on {date}."
        
        result = f"Available doctors in {department} on {date}:\n"
        for doc in data:
            slots = ", ".join(doc["availableSlots"]) if doc["availableSlots"] else "No slots available"
            result += f"- Dr. {doc['doctorName']} ({doc['specialty']}): {slots}\n"
        
        return result
        
    except httpx.HTTPStatusError as e:
        error_msg = f"Backend returned status {e.response.status_code}: {e.response.text[:200]}"
        logger.error(error_msg)
        return f"Error checking availability: {error_msg}"
    except httpx.RequestError as e:
        error_msg = f"Network error: {str(e)}"
        logger.error(error_msg)
        return f"Error checking availability: {error_msg}"
    except (ValueError, KeyError, TypeError) as e:
        error_msg = f"Unexpected response: {str(e)}"
        logger.error(error_msg)
        return f"Error checking availability: {error_msg}"


@tool
async def book_appointment(
    patient_name: str,
    patient_contact: str,
    department: str,
    doctor_id: str,
    date: str,
    time: str
) -> str:
    """
    Book an appointment with a specific doctor.
    
    Args:
        patient_name: Full name of the patient
        patient_contact: Phone or email of the patient
        department: Hospital department
        doctor_id: ID of the doctor (numeric)
        date: Date in ISO format (YYYY-MM-DD)
        time: Time in 24h format (HH:MM)
        
    Returns:
        Confirmation message with appointment details
    """
    url = f"{BACKEND_BASE_URL}/api/appointments/agent/book"
    headers = {"Content-Type": "application/json", "Accept": "application/json"}
    payload = {
        "patientName": patient_name,
        "patientContact": patient_contact,
        "department": department,
        "doctorId": int(doctor_id),
        "appointmentDate": date,
        "appointmentTime": time
    }
    
    logger.debug(f"Booking appointment for {patient_name} with doctor {doctor_id} at {date} {time}")
    
    try:
        async with httpx.AsyncClient(timeout=REQUEST_TIMEOUT) as client:
            response = await client.post(url, json=payload, headers=headers)
            
            if response.status_code == 409:
                return f"Sorry, that time slot is no longer available: {response.text}"
            
            response.raise_for_status()
            data = response.json()
        
        # Get doctor name from the response
        doctor_name = data.get("doctor", {}).get("name", f"Doctor {doctor_id}")
        return f"✅ Appointment booked successfully!\n- Patient: {patient_name}\n- Doctor: Dr. {doctor_name}\n- Date: {date}\n- Time: {time}"
        
    except httpx.HTTPStatusError as e:
        error_msg = f"Booking failed (status {e.response.status_code}): {e.response.text[:200]}"
        logger.error(error_msg)
        return f"Error booking appointment: {error_msg}"
    except httpx.RequestError as e:
        error_msg = f"Network error: {str(e)}"
        logger.error(error_msg)
        return f"Error booking appointment: {error_msg}"
    except (ValueError, KeyError, TypeError) as e:
        error_msg = f"Unexpected response: {str(e)}"
        logger.error(error_msg)
        return f"Error booking appointment: {error_msg}"


# Agent initialization
_booking_agent_executor: Optional[AgentExecutor] = None


def _build_prompt() -> ChatPromptTemplate:
    """Build the system prompt for the booking agent"""
    system_template = """You are a friendly and professional hospital receptionist named "CareFlow Booking Assistant".
Your job is to help patients book appointments with doctors in a conversational way.

Instructions:
1. Ask for information step by step if something is missing (don't ask all questions at once)
2. The required information to book an appointment is:
   - Patient's full name
   - Patient's contact information (phone or email)
   - Department they want to visit (e.g., Cardiology, Pediatrics, Emergency, etc.)
   - Preferred date (YYYY-MM-DD)
   - Preferred doctor and time (you must first call check_doctor_availability to find available options)

3. Always use check_doctor_availability first to see what's available before trying to book
4. When booking, make sure to use exactly the doctor ID and time from the availability check
5. Keep the conversation natural and friendly
6. If a user asks something unrelated to booking, politely guide them back to appointment booking
7. If no doctors are available for their requested date, suggest trying a different date or department

Available departments typically include: Cardiology, Neurology, Emergency, Pediatrics, Orthopedics, General Medicine

Important:
- Be patient and don't rush the user
- Confirm all details before booking
- If there's an error, apologize and help them try again
"""
    return ChatPromptTemplate.from_messages([
        ("system", system_template),
        MessagesPlaceholder(variable_name="chat_history", optional=True),
        ("human", "{input}"),
        MessagesPlaceholder(variable_name="agent_scratchpad"),
    ])


def get_booking_agent() -> AgentExecutor:
    """Get or initialize the booking agent"""
    global _booking_agent_executor

    if _booking_agent_executor is not None:
        return _booking_agent_executor

    api_key = OPENAI_API_KEY
    if not api_key:
        raise RuntimeError("OPENAI_API_KEY is not set. Please set it in your environment or .env file.")

    llm = ChatOpenAI(
        model=OPENAI_MODEL,
        temperature=0.7,  # More conversational than triage agent
        api_key=api_key,
    )

    tools = [check_doctor_availability, book_appointment]
    prompt = _build_prompt()

    agent = create_openai_functions_agent(llm=llm, tools=tools, prompt=prompt)

    _booking_agent_executor = AgentExecutor(
        agent=agent,
        tools=tools,
        verbose=True,
        max_iterations=AGENT_MAX_ITERATIONS,
        handle_parsing_errors=True,
        early_stopping_method="generate",
    )

    logger.info(f"Booking agent initialized (model={OPENAI_MODEL}, max_iterations={AGENT_MAX_ITERATIONS})")
    return _booking_agent_executor


# Public API for running the booking flow
async def run_booking_flow(session_id: str, user_message: str) -> str:
    """
    Run the booking agent flow with conversation history.
    
    Args:
        session_id: Unique session ID for the user
        user_message: The user's latest message
        
    Returns:
        The agent's response
    """
    # Get or create session history
    if session_id not in _sessions:
        _sessions[session_id] = []
    
    chat_history = _sessions[session_id]
    
    # Convert to LangChain message format
    langchain_history: List[BaseMessage] = []
    for msg in chat_history:
        if msg["role"] == "user":
            langchain_history.append(HumanMessage(content=msg["content"]))
        elif msg["role"] == "assistant":
            langchain_history.append(AIMessage(content=msg["content"]))
    
    try:
        agent_executor = get_booking_agent()
        result = await agent_executor.ainvoke({
            "input": user_message,
            "chat_history": langchain_history
        })
        
        ai_response = result.get("output", "Sorry, I couldn't process that request.")
        
        # Save to history
        chat_history.append({"role": "user", "content": user_message})
        chat_history.append({"role": "assistant", "content": ai_response})
        
        # Keep history manageable (max 20 messages)
        if len(chat_history) > 20:
            _sessions[session_id] = chat_history[-20:]
        
        return ai_response
        
    except Exception as e:
        logger.exception(f"Error in booking flow for session {session_id}")
        error_response = "Sorry, I encountered an unexpected error. Please try again later."
        
        # Still save to history for context
        chat_history.append({"role": "user", "content": user_message})
        chat_history.append({"role": "assistant", "content": error_response})
        
        return error_response
