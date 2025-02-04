package com.trimblecars.leasemanagement.controller.customer;

import com.trimblecars.leasemanagement.config.JwtUtil;
import com.trimblecars.leasemanagement.dto.ApiResponse;
import com.trimblecars.leasemanagement.model.customer.EndCustomer;
import com.trimblecars.leasemanagement.model.customer.LeaseHistory;
import com.trimblecars.leasemanagement.model.owner.VehicleRentalInfo;
import com.trimblecars.leasemanagement.service.customer.EndCustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class EndCustomerController {

    private static final Logger logger = LoggerFactory.getLogger(EndCustomerController.class);

    private final EndCustomerService customerService;
    private final JwtUtil jwtUtil;

    public EndCustomerController(EndCustomerService customerService, JwtUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<EndCustomer>> registerCustomer(@RequestBody EndCustomer customer,@RequestHeader("Authorization") String token) {
        logger.info("Registering customer with email: {}", customer.getEmail());
        try {
            EndCustomer savedCustomer = customerService.registerCustomer(customer);
            logger.info("Customer registered successfully with email: {}", customer.getEmail());
            return ResponseEntity.ok(new ApiResponse<>(true, savedCustomer, "Customer registered successfully."));
        } catch (Exception ex) {
            logger.error("Error registering customer with email: {}", customer.getEmail(), ex);
            return ResponseEntity.status(500).body(new ApiResponse<>(false, null, "Failed to register customer: " + ex.getMessage()));
        }
    }


    @GetMapping("/availableVehicles")
    public ResponseEntity<ApiResponse<List<VehicleRentalInfo>>> getAvailableVehicles(@RequestHeader("Authorization") String token) {
        logger.info("Fetching available vehicles for lease.");
        try {
            List<VehicleRentalInfo> vehicles = customerService.getAvailableVehicles();
            logger.info("Retrieved {} available vehicles.", vehicles.size());
            return ResponseEntity.ok(new ApiResponse<>(true, vehicles, "Available vehicles retrieved."));
        } catch (Exception ex) {
            logger.error("Error fetching available vehicles.", ex);
            return ResponseEntity.status(500).body(new ApiResponse<>(false, null, "Failed to fetch available vehicles: " + ex.getMessage()));
        }
    }

    @PostMapping("/lease/start")
    public ResponseEntity<ApiResponse<LeaseHistory>> startLease(
            @RequestHeader("Authorization") String token,
            @RequestParam String vehicleId) {
        try {
            String customerId = jwtUtil.extractUserId(token.substring(7));
            logger.info("Starting lease for customerId: {} with vehicleId: {}", customerId, vehicleId);
            LeaseHistory lease = customerService.startLease(customerId, vehicleId);
            logger.info("Lease started successfully for customerId: {} and vehicleId: {}", customerId, vehicleId);
            return ResponseEntity.ok(new ApiResponse<>(true, lease, "Lease started successfully."));
        } catch (Exception ex) {
            logger.error("Error starting lease. Reason: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body(new ApiResponse<>(false, null, "Failed to start lease: " + ex.getMessage()));
        }
    }


    @PutMapping("/update")
    public ResponseEntity<ApiResponse<EndCustomer>> updateCustomer(
            @RequestHeader("Authorization") String token,
            @RequestBody EndCustomer updatedCustomer) {
        try {
            String customerId = jwtUtil.extractUserId(token.substring(7));
            logger.info("Updating customer details for ID: {}", customerId);
            EndCustomer updatedData = customerService.updateCustomer(customerId, updatedCustomer);
            logger.info("Customer details updated successfully for ID: {}", customerId);
            return ResponseEntity.ok(new ApiResponse<>(true, updatedData, "Customer details updated successfully."));
        } catch (Exception ex) {
            logger.error("Error updating customer details: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body(new ApiResponse<>(false, null, "Failed to update customer details: " + ex.getMessage()));
        }
    }

    @PostMapping("/lease/end")
    public ResponseEntity<ApiResponse<LeaseHistory>> endLease(@RequestParam String leaseId,@RequestHeader("Authorization") String token) {
        logger.info("Ending lease with leaseId: {}", leaseId);
        try {
            LeaseHistory lease = customerService.endLease(leaseId);
            logger.info("Lease ended successfully with leaseId: {}", leaseId);
            return ResponseEntity.ok(new ApiResponse<>(true, lease, "Lease ended successfully."));
        } catch (Exception ex) {
            logger.error("Error ending lease with leaseId: {}", leaseId, ex);
            return ResponseEntity.status(500).body(new ApiResponse<>(false, null, "Failed to end lease: " + ex.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<LeaseHistory>>> getCustomerLeaseHistory(
            @RequestHeader("Authorization") String token) {
        try {
            String customerId = jwtUtil.extractUserId(token.substring(7));
            logger.info("Fetching lease history for customerId: {}", customerId);
            List<LeaseHistory> history = customerService.getCustomerLeaseHistory(customerId);
            logger.info("Retrieved {} lease history records for customerId: {}", history.size(), customerId);
            return ResponseEntity.ok(new ApiResponse<>(true, history, "Customer lease history retrieved."));
        } catch (Exception ex) {
            logger.error("Error fetching lease history.", ex);
            return ResponseEntity.status(500).body(new ApiResponse<>(false, null, "Failed to fetch lease history: " + ex.getMessage()));
        }
    }
}