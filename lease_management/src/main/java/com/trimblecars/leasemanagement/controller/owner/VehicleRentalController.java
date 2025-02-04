package com.trimblecars.leasemanagement.controller.owner;

import com.trimblecars.leasemanagement.config.JwtUtil;
import com.trimblecars.leasemanagement.dto.ApiResponse;
import com.trimblecars.leasemanagement.model.owner.VehicleRentalInfo;
import com.trimblecars.leasemanagement.service.owner.VehicleRentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicle Management", description = "Endpoints for managing vehicles")
public class VehicleRentalController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleRentalController.class);

    private final VehicleRentalService vehicleRentalService;
    private final JwtUtil jwtUtil;

    public VehicleRentalController(VehicleRentalService vehicleRentalService, JwtUtil jwtUtil) {
        this.vehicleRentalService = vehicleRentalService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<VehicleRentalInfo>> registerVehicle(
            @RequestBody VehicleRentalInfo vehicle,
            @RequestHeader("Authorization") String token) {

        logger.info("Extracting ownerId from JWT token");
        try {
            String ownerId = jwtUtil.extractUserId(token.substring(7));

            logger.info("Registering a new vehicle for owner ID: {}", ownerId);
            VehicleRentalInfo savedVehicle = vehicleRentalService.registerVehicle(vehicle, ownerId);

            logger.info("Vehicle registered successfully with ID: {}", savedVehicle.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, savedVehicle, "Vehicle registered successfully."));
        } catch (IllegalArgumentException e) {
            logger.error("Failed to register vehicle. Reason: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<ApiResponse<VehicleRentalInfo>> getVehicle(@PathVariable String vehicleId, @RequestHeader("Authorization") String token) {
        logger.info("Fetching details for vehicle ID: {}", vehicleId);
        Optional<VehicleRentalInfo> vehicle = vehicleRentalService.getVehicleById(vehicleId);
        if (vehicle.isPresent()) {
            logger.info("Vehicle found with ID: {}", vehicleId);
            return ResponseEntity.ok(new ApiResponse<>(true, vehicle.get(), "Vehicle found."));
        } else {
            logger.warn("Vehicle not found with ID: {}", vehicleId);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Vehicle not found."));
        }
    }

    @GetMapping("/owner")
    public ResponseEntity<ApiResponse<List<VehicleRentalInfo>>> getVehiclesByOwner(
            @RequestHeader("Authorization") String token) {

        logger.info("Extracting ownerId from JWT token");
        try {
            String ownerId = jwtUtil.extractUserId(token.substring(7));

            logger.info("Fetching vehicles for owner ID: {}", ownerId);
            List<VehicleRentalInfo> vehicles = vehicleRentalService.getVehiclesByOwner(ownerId);
            logger.info("Retrieved {} vehicles for owner ID: {}", vehicles.size(), ownerId);

            return ResponseEntity.ok(new ApiResponse<>(true, vehicles, "Vehicles retrieved successfully."));
        } catch (Exception e) {
            logger.error("Failed to fetch vehicles. Reason: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new ApiResponse<>(false, null, "Failed to retrieve vehicles."));
        }
    }

    @GetMapping("/{vehicleId}/history")
    public ResponseEntity<ApiResponse<List<VehicleRentalInfo>>> getLeaseHistory(@PathVariable String vehicleId, @RequestHeader("Authorization") String token) {
        logger.info("Fetching lease history for vehicle ID: {}", vehicleId);
        List<VehicleRentalInfo> leaseHistory = vehicleRentalService.getLeaseHistory(vehicleId);
        logger.info("Retrieved {} lease history entries for vehicle ID: {}", leaseHistory.size(), vehicleId);
        return ResponseEntity.ok(new ApiResponse<>(true, leaseHistory, "Lease history retrieved successfully."));
    }

    @DeleteMapping("/all")
    public ResponseEntity<ApiResponse<String>> deleteAllVehicles(@RequestHeader("Authorization") String token) {
        logger.info("Deleting all vehicles.");
        try {
            vehicleRentalService.deleteAllVehicles();
            logger.info("All vehicles deleted successfully.");
            return ResponseEntity.ok(new ApiResponse<>(true, "All vehicles deleted successfully.", "All vehicles deleted successfully."));
        } catch (Exception e) {
            logger.error("Failed to delete all vehicles. Reason: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new ApiResponse<>(false, null, "An error occurred: " + e.getMessage()));
        }
    }
}