package com.careflow.hms.triage;

import com.careflow.hms.entity.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service 
@ConditionalOnProperty(name = "triage.engine", havingValue = "llm", matchIfMissing = false) 
public class LlmTriageService implements ITriageService { 
 
    private static final Logger log = LoggerFactory.getLogger(LlmTriageService.class); 
 
    @Override
    public Patient processTriage(Patient patient) {
        PatientIntake intake = new PatientIntake();
        intake.setPatientId(patient.getPatientId());
        intake.setChiefComplaint(patient.getChiefComplaint());
        intake.setSymptoms(""); // Assuming symptoms might come from elsewhere later
        
        TriageResult suggestion = suggestTriage(intake);
        patient.setTriageColor(suggestion.getTriageColor());
        patient.setStatus(suggestion.isRequiresHumanApproval() ? "PENDING_APPROVAL" : "TRIAGE_COMPLETE");
        return patient;
    }

    @Override 
    public TriageResult suggestTriage(PatientIntake intake) { 
        // AI ONLY SUGGESTS — never auto-assigns 
        try { 
            String prompt = buildPrompt(intake); 
            String llmResponse = callLlmApi(prompt); // your AI SDK call 
            TriageResult suggestion = parseResult(llmResponse); 
 
            // Safety guardrails — downgrade overconfident RED if symptoms are ambiguous 
            if ("RED".equals(suggestion.getTriageColor()) && !hasCriticalSymptoms(intake)) { 
                suggestion.setTriageColor("YELLOW"); 
                suggestion.addWarning("AI over-triaged — downgraded to YELLOW by safety guardrail"); 
            } 
 
            suggestion.setRequiresHumanApproval(true); 
            log.info("AI triage suggestion generated for patient {}: {} (requires approval)", 
                intake.getPatientId(), suggestion.getTriageColor()); 
            return suggestion; 
        } catch (Exception e) { 
            log.error("LLM triage failed, defaulting to YELLOW with human review", e); 
            return new TriageResult("YELLOW", "AI unavailable — defaulted to YELLOW", true); 
        } 
    } 
 
    private boolean hasCriticalSymptoms(PatientIntake intake) { 
        String text = ((intake.getSymptoms() != null ? intake.getSymptoms() : "") + " "  + 
                       (intake.getChiefComplaint() != null ? intake.getChiefComplaint() : "")).toLowerCase(); 
        return text.contains("cardiac arrest") || text.contains("unconscious") || 
               text.contains("not breathing") || text.contains("severe bleeding") || 
               text.contains("stroke") || text.contains("heart attack"); 
    }

    private String buildPrompt(PatientIntake intake) {
        return "Analyze patient intake: " + intake.getChiefComplaint();
    }

    private String callLlmApi(String prompt) {
        // Mock LLM call
        return "GREEN";
    }

    private TriageResult parseResult(String llmResponse) {
        return new TriageResult(llmResponse, "AI analysis", true);
    }
} 
