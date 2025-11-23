package com.smiles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;

/**
 * Main application class for Smiles Dental Management System.
 *
 * This application uses Spring Modulith to organize code into clearly defined modules:
 * - auth: Authentication and authorization
 * - facilities: Multi-facility management
 * - staff: Staff and employee management
 * - patients: Patient records and demographics
 * - rooms: Treatment rooms and operatories
 * - appointments: Scheduling and calendar management
 * - ehr: Electronic Health Records
 * - materials: Dental materials catalog
 * - inventory: Facility-specific inventory tracking
 * - bom: Bill of Materials for procedures
 * - billing: Billing and invoicing
 * - realtime: WebSocket-based real-time updates
 */
@SpringBootApplication
@Modulith(
    systemName = "Smiles Dental Management",
    sharedModules = "common"
)
public class SmilesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmilesApplication.class, args);
    }

}
