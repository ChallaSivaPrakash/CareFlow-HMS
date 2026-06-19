package com.careflow.hms.triage;

import com.careflow.hms.entity.Patient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "triage.engine", havingValue = "llm", matchIfMissing = true)
public class LlmTriageService implements ITriageService {

    private static final Logger log = LoggerFactory.getLogger(LlmTriageService.class);

    @Value("${ai.engine.url:http://ai-engine:8000}")
    private String aiEngineUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Patient processTriage(Patient patient) {
        PatientIntake intake = new PatientIntake();
        if (patient.getPatientId() != null) {
            intake.setPatientId(patient.getPatientId().toString());
        } else {
            intake.setPatientId("PENDING_" + System.currentTimeMillis());
        }
        intake.setChiefComplaint(patient.getChiefComplaint());
        
        // Build additional_notes from patient vitals/info
        StringBuilder additionalNotes = new StringBuilder();
        if (patient.getName() != null) {
            additionalNotes.append("Name: ").append(patient.getName()).append("; ");
        }
        if (patient.getAge() != null) {
            additionalNotes.append("Age: ").append(patient.getAge()).append("; ");
        }
        if (patient.getGender() != null) {
            additionalNotes.append("Gender: ").append(patient.getGender()).append("; ");
        }
        if (patient.getBloodPressure() != null) {
            additionalNotes.append("BP: ").append(patient.getBloodPressure()).append("; ");
        }
        if (patient.getHeartRate() != null) {
            additionalNotes.append("HR: ").append(patient.getHeartRate()).append("; ");
        }
        if (patient.getSpO2() != null) {
            additionalNotes.append("SpO2: ").append(patient.getSpO2()).append("; ");
        }
        intake.setSymptoms(additionalNotes.length() > 0 ? additionalNotes.toString() : "");

        TriageResult suggestion = suggestTriage(intake);
        patient.setTriageColor(suggestion.getTriageColor());
        patient.setStatus(suggestion.isRequiresHumanApproval() ? "PENDING_APPROVAL" : "TRIAGE_COMPLETE");
        return patient;
    }

    @Override
    public TriageResult suggestTriage(PatientIntake intake) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("patient_id", intake.getPatientId());
            requestBody.put("chief_complaint", intake.getChiefComplaint());
            if (intake.getSymptoms() != null && !intake.getSymptoms().isBlank()) {
                requestBody.put("additional_notes", intake.getSymptoms());
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            String url = aiEngineUrl + "/api/ai/triage";
            log.info("Calling AI engine at: {}", url);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("AI engine returned non-OK status: " + response.getStatusCode());
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            String triageColor = root.path("triage_color").asText("YELLOW");
            String rationale = root.path("rationale").asText("AI analysis complete");
            boolean requiresHumanApproval = root.path("requires_human_approval").asBoolean(true);

            TriageResult result = new TriageResult(triageColor, rationale, requiresHumanApproval);

            // Add warnings if present
            JsonNode warningsNode = root.path("warnings");
            if (warningsNode.isArray()) {
                for (JsonNode warning : warningsNode) {
                    result.addWarning(warning.asText());
                }
            }

            log.info("AI triage suggestion generated for patient {}: {} (requires approval: {})",
                intake.getPatientId(), result.getTriageColor(), result.isRequiresHumanApproval());
            return result;

        } catch (Exception e) {
            log.error("LLM triage failed, defaulting to YELLOW with human review", e);
            return new TriageResult("YELLOW", "AI unavailable — defaulted to YELLOW", true);
        }
    }
} 
