package com.smiles.appointments.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smiles.appointments.dto.CreateAppointmentRequest;
import com.smiles.appointments.dto.UpdateAppointmentRequest;
import com.smiles.appointments.domain.AppointmentStatus;
import com.smiles.facilities.dto.CreateFacilityRequest;
import com.smiles.patients.dto.CreatePatientRequest;
import com.smiles.rooms.dto.CreateRoomRequest;
import com.smiles.rooms.domain.RoomType;
import com.smiles.staff.dto.CreateStaffRequest;
import com.smiles.staff.domain.StaffRole;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for AppointmentController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID facilityId;
    private UUID dentistId;
    private UUID roomId;
    private UUID patientId;

    @BeforeEach
    void setUp() throws Exception {
        // Create a test facility
        CreateFacilityRequest facilityRequest = CreateFacilityRequest.builder()
            .name("Test Facility for Appointments")
            .city("Phoenix")
            .address("123 Dental Ave")
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

        // Create a dentist
        CreateStaffRequest dentistRequest = CreateStaffRequest.builder()
            .facilityId(facilityId)
            .name("Dr. Smith")
            .email("dr.smith@test.com")
            .role(StaffRole.DENTIST)
            .build();

        String dentistResponse = mockMvc
            .perform(
                post("/staff")
                    .with(user("admin").roles("admin"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dentistRequest))
            )
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        dentistId = UUID.fromString(
            objectMapper.readTree(dentistResponse).get("id").asText()
        );

        // Create a room
        CreateRoomRequest roomRequest = CreateRoomRequest.builder()
            .facilityId(facilityId)
            .name("Room 1")
            .type(RoomType.CHAIR)
            .build();

        String roomResponse = mockMvc
            .perform(
                post("/rooms")
                    .with(user("admin").roles("admin"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(roomRequest))
            )
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        roomId = UUID.fromString(
            objectMapper.readTree(roomResponse).get("id").asText()
        );

        // Create a patient
        CreatePatientRequest patientRequest = CreatePatientRequest.builder()
            .facilityId(facilityId)
            .name("John Doe")
            .birthDate(LocalDate.of(1990, 1, 1))
            .email("john.doe@test.com")
            .build();

        String patientResponse = mockMvc
            .perform(
                post("/patients")
                    .with(user("receptionist").roles("receptionist"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patientRequest))
            )
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        patientId = UUID.fromString(
            objectMapper.readTree(patientResponse).get("id").asText()
        );
    }

    @Test
    void testCreateAppointment_Success() throws Exception {
        Instant startTime = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant endTime = startTime.plus(1, ChronoUnit.HOURS);

        CreateAppointmentRequest request = new CreateAppointmentRequest(
            patientId,
            dentistId,
            roomId,
            facilityId,
            startTime,
            endTime
        );

        mockMvc
            .perform(
                post("/appointments")
                    .with(user("receptionist").roles("receptionist"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.patientId").value(patientId.toString()))
            .andExpect(jsonPath("$.dentistId").value(dentistId.toString()))
            .andExpect(jsonPath("$.roomId").value(roomId.toString()))
            .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void testCreateAppointment_DentistConflict() throws Exception {
        Instant startTime = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant endTime = startTime.plus(1, ChronoUnit.HOURS);

        // Create first appointment
        CreateAppointmentRequest request1 = new CreateAppointmentRequest(
            patientId,
            dentistId,
            roomId,
            facilityId,
            startTime,
            endTime
        );

        mockMvc
            .perform(
                post("/appointments")
                    .with(user("receptionist").roles("receptionist"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request1))
            )
            .andExpect(status().isCreated());

        // Try to create overlapping appointment with same dentist
        CreateAppointmentRequest request2 = new CreateAppointmentRequest(
            patientId,
            dentistId,
            roomId,
            facilityId,
            startTime.plus(30, ChronoUnit.MINUTES),
            endTime.plus(30, ChronoUnit.MINUTES)
        );

        mockMvc
            .perform(
                post("/appointments")
                    .with(user("receptionist").roles("receptionist"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request2))
            )
            .andExpect(status().isConflict());
    }

    @Test
    void testCreateAppointment_RoomConflict() throws Exception {
        // Create second dentist
        CreateStaffRequest dentist2Request = CreateStaffRequest.builder()
            .facilityId(facilityId)
            .name("Dr. Jones")
            .email("dr.jones@test.com")
            .role(StaffRole.DENTIST)
            .build();

        String dentist2Response = mockMvc
            .perform(
                post("/staff")
                    .with(user("admin").roles("admin"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dentist2Request))
            )
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        UUID dentist2Id = UUID.fromString(
            objectMapper.readTree(dentist2Response).get("id").asText()
        );

        Instant startTime = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant endTime = startTime.plus(1, ChronoUnit.HOURS);

        // Create first appointment
        CreateAppointmentRequest request1 = new CreateAppointmentRequest(
            patientId,
            dentistId,
            roomId,
            facilityId,
            startTime,
            endTime
        );

        mockMvc
            .perform(
                post("/appointments")
                    .with(user("receptionist").roles("receptionist"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request1))
            )
            .andExpect(status().isCreated());

        // Try to create overlapping appointment with different dentist but same room
        CreateAppointmentRequest request2 = new CreateAppointmentRequest(
            patientId,
            dentist2Id,
            roomId,
            facilityId,
            startTime.plus(30, ChronoUnit.MINUTES),
            endTime.plus(30, ChronoUnit.MINUTES)
        );

        mockMvc
            .perform(
                post("/appointments")
                    .with(user("receptionist").roles("receptionist"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request2))
            )
            .andExpect(status().isConflict());
    }

    @Test
    void testGetAppointmentsByFacility() throws Exception {
        Instant startTime = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant endTime = startTime.plus(1, ChronoUnit.HOURS);

        // Create appointment
        CreateAppointmentRequest request = new CreateAppointmentRequest(
            patientId,
            dentistId,
            roomId,
            facilityId,
            startTime,
            endTime
        );

        mockMvc
            .perform(
                post("/appointments")
                    .with(user("receptionist").roles("receptionist"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated());

        // Get appointments by facility
        mockMvc
            .perform(
                get("/appointments")
                    .with(user("receptionist").roles("receptionist"))
                    .param("facilityId", facilityId.toString())
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].facilityId").value(facilityId.toString()));
    }

    @Test
    void testCancelAppointment() throws Exception {
        Instant startTime = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant endTime = startTime.plus(1, ChronoUnit.HOURS);

        // Create appointment
        CreateAppointmentRequest request = new CreateAppointmentRequest(
            patientId,
            dentistId,
            roomId,
            facilityId,
            startTime,
            endTime
        );

        String createResponse = mockMvc
            .perform(
                post("/appointments")
                    .with(user("receptionist").roles("receptionist"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        UUID appointmentId = UUID.fromString(
            objectMapper.readTree(createResponse).get("id").asText()
        );

        // Cancel appointment
        mockMvc
            .perform(
                post("/appointments/" + appointmentId + "/cancel")
                    .with(user("receptionist").roles("receptionist"))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void testUpdateAppointment() throws Exception {
        Instant startTime = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant endTime = startTime.plus(1, ChronoUnit.HOURS);

        // Create appointment
        CreateAppointmentRequest request = new CreateAppointmentRequest(
            patientId,
            dentistId,
            roomId,
            facilityId,
            startTime,
            endTime
        );

        String createResponse = mockMvc
            .perform(
                post("/appointments")
                    .with(user("receptionist").roles("receptionist"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        UUID appointmentId = UUID.fromString(
            objectMapper.readTree(createResponse).get("id").asText()
        );

        // Update appointment status
        UpdateAppointmentRequest updateRequest = new UpdateAppointmentRequest(
            null,
            null,
            null,
            null,
            AppointmentStatus.ONGOING
        );

        mockMvc
            .perform(
                put("/appointments/" + appointmentId)
                    .with(user("receptionist").roles("receptionist"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ONGOING"));
    }
}
