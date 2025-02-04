package com.trimblecars.leasemanagement.controller.admin;

import com.trimblecars.leasemanagement.dto.ApiResponse;
import com.trimblecars.leasemanagement.exception.ResourceNotFoundException;
import com.trimblecars.leasemanagement.model.customer.EndCustomer;
import com.trimblecars.leasemanagement.model.customer.LeaseHistory;
import com.trimblecars.leasemanagement.model.owner.VehicleCurrentStatus;
import com.trimblecars.leasemanagement.model.owner.VehicleOwnerInfo;
import com.trimblecars.leasemanagement.model.owner.VehicleRentalInfo;
import com.trimblecars.leasemanagement.service.PdfService;
import com.trimblecars.leasemanagement.service.admin.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    private final PdfService pdfService;


    public AdminController(AdminService adminService, PdfService pdfService) {
        this.adminService = adminService;
        this.pdfService = pdfService;
    }



    @GetMapping("/leases/pdf")
    public ResponseEntity<byte[]> downloadLeaseHistoryPdf() {
        logger.info("Generating lease history PDF...");

        try {
            List<LeaseHistory> leaseHistories = adminService.getAllLeaseHistories();
            if (leaseHistories.isEmpty()) {
                logger.warn("No lease history found for generating PDF.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(null);
            }

            byte[] pdfBytes = pdfService.generateLeaseHistoryPdf(leaseHistories);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=lease_history_report.pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

            logger.info("Lease history PDF generated successfully.");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception ex) {
            logger.error("Error while generating lease history PDF.", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    @PostMapping("/vehicles/register")
    public ResponseEntity<ApiResponse<VehicleRentalInfo>> registerVehicle(@RequestBody VehicleRentalInfo vehicle, @RequestParam String ownerId,@RequestHeader("Authorization") String token) {
        logger.info("Registering vehicle for ownerId: {}", ownerId);
        try {
            VehicleRentalInfo savedVehicle = adminService.registerVehicle(vehicle, ownerId);
            logger.info("Vehicle registered successfully: {}", savedVehicle.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, savedVehicle, "Vehicle registered successfully by Admin."));
        } catch (IllegalArgumentException ex) {
            logger.error("Failed to register vehicle for ownerId: {}. Reason: {}", ownerId, ex.getMessage(), ex);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, ex.getMessage()));
        } catch (Exception ex) {
            logger.error("Unexpected error occurred while registering vehicle for ownerId: {}", ownerId, ex);
            return ResponseEntity.internalServerError().body(new ApiResponse<>(false, null, "Unexpected error: " + ex.getMessage()));
        }
    }

    @DeleteMapping("/vehicles/{vehicleId}")
    public ResponseEntity<ApiResponse<String>> deleteVehicle(@PathVariable String vehicleId,@RequestHeader("Authorization") String token) {
        logger.info("Deleting vehicle with vehicleId: {}", vehicleId);
        try {
            adminService.deleteVehicle(vehicleId);
            logger.info("Vehicle deleted successfully: {}", vehicleId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Vehicle deleted successfully by Admin.", "Deleted"));
        } catch (ResourceNotFoundException ex) {
            logger.error("Vehicle not found for deletion: {}", vehicleId, ex);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, ex.getMessage()));
        }
    }

    @GetMapping("/vehicles")
    public ResponseEntity<ApiResponse<List<VehicleRentalInfo>>> getAllVehicles(@RequestHeader("Authorization") String token) {
        logger.info("Fetching all vehicles.");
        List<VehicleRentalInfo> vehicles = adminService.getAllVehicles();
        logger.info("Retrieved {} vehicles.", vehicles.size());
        return ResponseEntity.ok(new ApiResponse<>(true, vehicles, "All vehicles retrieved successfully."));
    }

    @DeleteMapping("/owners/{ownerId}")
    public ResponseEntity<ApiResponse<String>> deleteOwner(@PathVariable String ownerId,@RequestHeader("Authorization") String token) {
        logger.info("Deleting owner with ownerId: {}", ownerId);
        try {
            adminService.deleteOwner(ownerId);
            logger.info("Owner deleted successfully: {}", ownerId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Owner deleted successfully by Admin.", "Deleted"));
        } catch (ResourceNotFoundException ex) {
            logger.error("Owner not found for deletion: {}", ownerId, ex);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, ex.getMessage()));
        }
    }

    @GetMapping("/owners")
    public ResponseEntity<ApiResponse<List<VehicleOwnerInfo>>> getAllOwners(@RequestHeader("Authorization") String token) {
        logger.info("Fetching all owners.");
        List<VehicleOwnerInfo> owners = adminService.getAllOwners();
        logger.info("Retrieved {} owners.", owners.size());
        return ResponseEntity.ok(new ApiResponse<>(true, owners, "All owners retrieved successfully."));
    }

    @DeleteMapping("/customers/{customerId}")
    public ResponseEntity<ApiResponse<String>> deleteCustomer(@PathVariable String customerId,@RequestHeader("Authorization") String token) {
        logger.info("Deleting customer with customerId: {}", customerId);
        try {
            adminService.deleteCustomer(customerId);
            logger.info("Customer deleted successfully: {}", customerId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Customer deleted successfully by Admin.", "Deleted"));
        } catch (ResourceNotFoundException ex) {
            logger.error("Customer not found for deletion: {}", customerId, ex);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, ex.getMessage()));
        }
    }

    @PutMapping("/vehicles/{vehicleId}/status")
    public ResponseEntity<ApiResponse<VehicleRentalInfo>> updateVehicleStatus(
            @PathVariable String vehicleId,
            @RequestHeader("Authorization") String token,
            @RequestParam VehicleCurrentStatus status) {
        logger.info("Updating status for vehicleId: {} to {}", vehicleId, status);
        try {
            VehicleRentalInfo updatedVehicle = adminService.updateVehicleStatus(vehicleId, status);
            logger.info("Vehicle status updated successfully for vehicleId: {}", vehicleId);
            return ResponseEntity.ok(new ApiResponse<>(true, updatedVehicle, "Vehicle status updated successfully."));
        } catch (ResourceNotFoundException ex) {
            logger.error("Vehicle not found for status update: {}", vehicleId, ex);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, ex.getMessage()));
        }
    }

    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<List<EndCustomer>>> getAllCustomers(@RequestHeader("Authorization") String token) {
        logger.info("Fetching all customers.");
        List<EndCustomer> customers = adminService.getAllCustomers();
        logger.info("Retrieved {} customers.", customers.size());
        return ResponseEntity.ok(new ApiResponse<>(true, customers, "All customers retrieved successfully."));
    }

    @PostMapping("/leases/start")
    public ResponseEntity<ApiResponse<LeaseHistory>> startLease(@RequestParam String customerId, @RequestParam String vehicleId,@RequestHeader("Authorization") String token) {
        logger.info("Starting lease for customerId: {} with vehicleId: {}", customerId, vehicleId);
        try {
            LeaseHistory lease = adminService.startLease(customerId, vehicleId);
            logger.info("Lease started successfully for customerId: {} with vehicleId: {}", customerId, vehicleId);
            return ResponseEntity.ok(new ApiResponse<>(true, lease, "Lease started successfully by Admin."));
        } catch (IllegalArgumentException ex) {
            logger.error("Failed to start lease for customerId: {} with vehicleId: {}. Reason: {}", customerId, vehicleId, ex.getMessage(), ex);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, ex.getMessage()));
        }
    }

    @PostMapping("/leases/end")
    public ResponseEntity<ApiResponse<LeaseHistory>> endLease(@RequestParam String leaseId,@RequestHeader("Authorization") String token) {
        logger.info("Ending lease with leaseId: {}", leaseId);
        try {
            LeaseHistory lease = adminService.endLease(leaseId);
            logger.info("Lease ended successfully with leaseId: {}", leaseId);
            return ResponseEntity.ok(new ApiResponse<>(true, lease, "Lease ended successfully by Admin."));
        } catch (ResourceNotFoundException ex) {
            logger.error("Lease not found for leaseId: {}", leaseId, ex);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, ex.getMessage()));
        }
    }
}