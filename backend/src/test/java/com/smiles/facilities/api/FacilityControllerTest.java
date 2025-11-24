package com.smiles.facilities.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smiles.facilities.dto.CreateFacilityRequest;
import com.smiles.facilities.dto.UpdateFacilityRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for FacilityController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FacilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "admin")
    void testCreateFacility_AsAdmin() throws Exception {
        CreateFacilityRequest request = CreateFacilityRequest.builder()
                .name("Test Facility 1")
                .city("New York")
                .address("123 Main St")
                .build();

        mockMvc.perform(post("/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Facility 1"))
                .andExpect(jsonPath("$.city").value("New York"))
                .andExpect(jsonPath("$.address").value("123 Main St"));
    }

    @Test
    @WithMockUser(roles = "receptionist")
    void testCreateFacility_AsReceptionist_ShouldFail() throws Exception {
        CreateFacilityRequest request = CreateFacilityRequest.builder()
                .name("Test Facility 2")
                .city("Los Angeles")
                .address("456 Oak Ave")
                .build();

        mockMvc.perform(post("/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "admin")
    void testGetAllFacilities_AsAdmin() throws Exception {
        // Create test facilities first
        CreateFacilityRequest facility1 = CreateFacilityRequest.builder()
                .name("Facility A")
                .city("Chicago")
                .address("789 Elm St")
                .build();

        CreateFacilityRequest facility2 = CreateFacilityRequest.builder()
                .name("Facility B")
                .city("Houston")
                .address("321 Pine St")
                .build();

        mockMvc.perform(post("/facilities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(facility1)));

        mockMvc.perform(post("/facilities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(facility2)));

        // Test getting all facilities
        mockMvc.perform(get("/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "admin")
    void testUpdateFacility_AsAdmin() throws Exception {
        // Create a facility first
        CreateFacilityRequest createRequest = CreateFacilityRequest.builder()
                .name("Original Facility")
                .city("Seattle")
                .address("111 Maple St")
                .build();

        String createResponse = mockMvc.perform(post("/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String facilityId = objectMapper.readTree(createResponse).get("id").asText();

        // Update the facility
        UpdateFacilityRequest updateRequest = UpdateFacilityRequest.builder()
                .name("Updated Facility")
                .city("Portland")
                .build();

        mockMvc.perform(put("/facilities/" + facilityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Facility"))
                .andExpect(jsonPath("$.city").value("Portland"));
    }

    @Test
    @WithMockUser(roles = "admin")
    void testDeleteFacility_AsAdmin() throws Exception {
        // Create a facility first
        CreateFacilityRequest createRequest = CreateFacilityRequest.builder()
                .name("Facility to Delete")
                .city("Boston")
                .address("222 Cedar St")
                .build();

        String createResponse = mockMvc.perform(post("/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String facilityId = objectMapper.readTree(createResponse).get("id").asText();

        // Delete the facility
        mockMvc.perform(delete("/facilities/" + facilityId))
                .andExpect(status().isNoContent());

        // Verify it's deleted
        mockMvc.perform(get("/facilities/" + facilityId))
                .andExpect(status().is4xxClientError());
    }
}
