package com.careflow.hms.controller;

import com.careflow.hms.CareflowApplication;
import com.careflow.hms.entity.Patient;
import com.careflow.hms.repository.PatientRepository;
import com.careflow.hms.service.AuditService;
import com.careflow.hms.service.WebSocketNotificationService;
import com.careflow.hms.triage.ITriageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.careflow.hms.security.JwtTokenProvider;
import com.careflow.hms.security.JwtAuthenticationFilter;
@WebMvcTest(controllers = PatientController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = CareflowApplication.class)
@AutoConfigureMockMvc(addFilters = false)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientRepository patientRepository;

    @MockBean
    private ITriageService triageService;

    @MockBean
    private WebSocketNotificationService webSocketNotificationService;

    @MockBean
    private AuditService auditService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    public void testCreatePatient() throws Exception {
        Patient patient = new Patient();
        patient.setName("John Doe");
        patient.setChiefComplaint("Chest pain");

        Patient triagedPatient = new Patient();
        triagedPatient.setName("John Doe");
        triagedPatient.setTriageColor("RED");

        when(triageService.processTriage(any(Patient.class))).thenReturn(triagedPatient);
        when(patientRepository.save(any(Patient.class))).thenReturn(triagedPatient);

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.triageColor").value("RED"));
    }
}
