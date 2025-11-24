package com.smiles.patients.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smiles.facilities.dto.CreateFacilityRequest;
import com.smiles.patients.dto.CreatePatientRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for PatientController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID facilityId;

    @BeforeEach
    @WithMockUser(roles = "admin")
    void setUp() throws Exception {
        // Create a test facility
        CreateFacilityRequest facilityRequest = CreateFacilityRequest.builder()
                .name("Test Facility for Patients")
                .city("Phoenix")
                .address("999 Desert Dr")
                .build();

        String facilityResponse = mockMvc.perform(post("/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(facilityRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        facilityId = UUID.fromString(objectMapper.readTree(facilityResponse).get("id").asText());
    }

    @Test
    @WithMockUser(roles = "receptionist")
    void testCreatePatient_AsReceptionist() throws Exception {
        CreatePatientRequest request = CreatePatientRequest.builder()
                .facilityId(facilityId)
                .name("Michael Anderson")
                .birthDate(LocalDate.of(1985, 5, 15))
                .email("michael.anderson@test.com")
                .phone("555-1234")
                .build();

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Michael Anderson"))
                .andExpect(jsonPath("$.email").value("michael.anderson@test.com"));
    }

    @Test
    @WithMockUser(roles = "admin")
    void testCreatePatientUnderFacility() throws Exception {
        // Create patient under facility
        CreatePatientRequest request = CreatePatientRequest.builder()
                .facilityId(facilityId)
                .name("Emma Davis")
                .birthDate(LocalDate.of(1990, 8, 20))
                .email("emma.davis@test.com")
                .build();

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.facilityId").value(facilityId.toString()));
    }

    @Test
    @WithMockUser(roles = "receptionist")
    void testGetPatientsByFacility() throws Exception {
        // Create test patients
        CreatePatientRequest patient1 = CreatePatientRequest.builder()
                .facilityId(facilityId)
                .name("Oliver Martinez")
                .birthDate(LocalDate.of(1975, 3, 10))
                .email("oliver.martinez@test.com")
                .build();

        CreatePatientRequest patient2 = CreatePatientRequest.builder()
                .facilityId(facilityId)
                .name("Sophia Taylor")
                .birthDate(LocalDate.of(1988, 12, 5))
                .email("sophia.taylor@test.com")
                .build();

        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patient1)));

        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patient2)));

        // Get patients by facility
        mockMvc.perform(get("/patients")
                        .param("facilityId", facilityId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
