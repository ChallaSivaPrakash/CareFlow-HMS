package com.careflow.hms.repository;

import com.careflow.hms.CareflowApplication;
import com.careflow.hms.entity.Bed;
import com.careflow.hms.entity.Patient;
import com.careflow.hms.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = CareflowApplication.class)
@EnableJpaRepositories(basePackages = "com.careflow.hms.repository")
@EntityScan(basePackages = "com.careflow.hms.entity")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private BedRepository bedRepository;

    @Test
    public void testUserCreationAndRetrieval() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRole("ROLE_ADMIN");
        user.setName("Test Admin");

        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("testuser");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Admin");
    }

    @Test
    public void testPatientTriagePersistence() {
        Patient patient = new Patient();
        patient.setPatientId("P1001");
        patient.setName("John Doe");
        patient.setAge(30);
        patient.setTriageColor("RED");
        patient.setChiefComplaint("Chest pain");
        patient.setStatus("WAITING");

        patientRepository.save(patient);

        List<Patient> waitingPatients = patientRepository.findByStatus("WAITING");
        assertThat(waitingPatients).isNotEmpty();
        assertThat(waitingPatients.stream().anyMatch(p -> p.getPatientId().equals("P1001"))).isTrue();
    }

    @Test
    public void testBedOccupancyQuery() {
        Bed bed = new Bed();
        bed.setBedNumber("B-101");
        bed.setBedType("ICU");
        bed.setIsOccupied(false);
        bed.setWardName("ICU Ward");

        bedRepository.save(bed);

        List<Bed> freeBeds = bedRepository.findByIsOccupiedFalse();
        assertThat(freeBeds).isNotEmpty();
        assertThat(freeBeds.stream().anyMatch(b -> b.getBedNumber().equals("B-101"))).isTrue();
    }
}
