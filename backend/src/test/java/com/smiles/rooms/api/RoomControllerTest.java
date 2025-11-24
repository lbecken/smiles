package com.smiles.rooms.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smiles.facilities.dto.CreateFacilityRequest;
import com.smiles.rooms.domain.RoomType;
import com.smiles.rooms.dto.CreateRoomRequest;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for RoomController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID facilityId;

    @BeforeEach
    //@WithMockUser(roles = "admin")
    void setUp() throws Exception {
        // Create a test facility
        CreateFacilityRequest facilityRequest = CreateFacilityRequest.builder()
            .name("Test Facility for Rooms")
            .city("Miami")
            .address("555 Beach Blvd")
            .build();

        String facilityResponse = mockMvc
            .perform(
                post("/facilities")
                    .with(user("admin").roles("admin"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(facilityRequest))
            )
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        facilityId = UUID.fromString(
            objectMapper.readTree(facilityResponse).get("id").asText()
        );
    }

    @Test
    //@WithMockUser(roles = "receptionist")
    void testCreateRoom_AsReceptionist() throws Exception {
        CreateRoomRequest request = CreateRoomRequest.builder()
            .facilityId(facilityId)
            .name("Room 1")
            .type(RoomType.CHAIR)
            .build();

        mockMvc
            .perform(
                post("/rooms")
                    .with(user("receptionist").roles("receptionist"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Room 1"))
            .andExpect(jsonPath("$.type").value("CHAIR"));
    }

    @Test
    //@WithMockUser(roles = "receptionist")
    void testGetRoomsByFacility() throws Exception {
        // Create test rooms
        CreateRoomRequest room1 = CreateRoomRequest.builder()
            .facilityId(facilityId)
            .name("Chair A")
            .type(RoomType.CHAIR)
            .build();

        CreateRoomRequest room2 = CreateRoomRequest.builder()
            .facilityId(facilityId)
            .name("Surgery Room 1")
            .type(RoomType.SURGERY_ROOM)
            .build();

        mockMvc.perform(
            post("/rooms")
                .with(user("receptionist").roles("receptionist"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(room1))
        );

        mockMvc.perform(
            post("/rooms")
                .with(user("receptionist").roles("receptionist"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(room2))
        );

        // Get rooms by facility
        mockMvc
            .perform(
                get("/rooms")
                    .with(user("receptionist").roles("receptionist"))
                    .param("facilityId", facilityId.toString())
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2));
    }
}
