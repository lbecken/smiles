package com.smiles.staff.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smiles.facilities.dto.CreateFacilityRequest;
import com.smiles.staff.domain.StaffRole;
import com.smiles.staff.dto.CreateStaffRequest;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for StaffController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StaffControllerTest {

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
                .name("Test Facility for Staff")
                .city("Denver")
                .address("777 Mountain Rd")
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
    @WithMockUser(roles = "admin")
    void testCreateStaff_AsAdmin() throws Exception {
        CreateStaffRequest request = CreateStaffRequest.builder()
                .facilityId(facilityId)
                .name("Dr. John Smith")
                .email("john.smith@test.com")
                .role(StaffRole.DENTIST)
                .active(true)
                .build();

        mockMvc.perform(post("/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Dr. John Smith"))
                .andExpect(jsonPath("$.email").value("john.smith@test.com"))
                .andExpect(jsonPath("$.role").value("DENTIST"));
    }

    @Test
    @WithMockUser(roles = "receptionist")
    void testCreateStaff_AsReceptionist_ShouldFail() throws Exception {
        CreateStaffRequest request = CreateStaffRequest.builder()
                .facilityId(facilityId)
                .name("Jane Doe")
                .email("jane.doe@test.com")
                .role(StaffRole.ASSISTANT)
                .build();

        mockMvc.perform(post("/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "admin")
    void testAssignStaffToFacility() throws Exception {
        // Create staff for facility
        CreateStaffRequest request = CreateStaffRequest.builder()
                .facilityId(facilityId)
                .name("Dr. Sarah Johnson")
                .email("sarah.johnson@test.com")
                .role(StaffRole.DENTIST)
                .build();

        mockMvc.perform(post("/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.facilityId").value(facilityId.toString()));
    }

    @Test
    @WithMockUser(roles = "admin")
    void testGetStaffByFacility() throws Exception {
        // Create test staff
        CreateStaffRequest staff1 = CreateStaffRequest.builder()
                .facilityId(facilityId)
                .name("Dr. Alice Brown")
                .email("alice.brown@test.com")
                .role(StaffRole.DENTIST)
                .build();

        CreateStaffRequest staff2 = CreateStaffRequest.builder()
                .facilityId(facilityId)
                .name("Bob Wilson")
                .email("bob.wilson@test.com")
                .role(StaffRole.RECEPTIONIST)
                .build();

        mockMvc.perform(post("/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(staff1)));

        mockMvc.perform(post("/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(staff2)));

        // Get staff by facility
        mockMvc.perform(get("/staff")
                        .param("facilityId", facilityId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
