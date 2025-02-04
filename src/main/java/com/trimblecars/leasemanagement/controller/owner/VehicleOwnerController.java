package com.trimblecars.leasemanagement.controller.owner;

import com.trimblecars.leasemanagement.config.JwtUtil;
import com.trimblecars.leasemanagement.dto.ApiResponse;
import com.trimblecars.leasemanagement.model.owner.VehicleOwnerInfo;
import com.trimblecars.leasemanagement.service.owner.VehicleOwnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/owners")
public class VehicleOwnerController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleOwnerController.class);

    private final VehicleOwnerService vehicleOwnerService;
    JwtUtil jwtUtil;

    public VehicleOwnerController(VehicleOwnerService vehicleOwnerService, JwtUtil jwtUtil) {
        this.vehicleOwnerService = vehicleOwnerService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/registerOwnerDetails")
    public ResponseEntity<ApiResponse<VehicleOwnerInfo>> registerOwner(@RequestBody VehicleOwnerInfo owner,@RequestHeader("Authorization") String token) {
        logger.info("Attempting to register a new owner with email: {}", owner.getEmail());
        try {
            VehicleOwnerInfo savedOwner = vehicleOwnerService.registerOwner(owner);
            logger.info("Owner registered successfully with ID: {}", savedOwner.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, savedOwner, "Owner registered successfully."));
        } catch (IllegalArgumentException e) {
            logger.error("Failed to register owner: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @GetMapping("/details")
    public ResponseEntity<ApiResponse<VehicleOwnerInfo>> getOwnerDetails(@RequestHeader("Authorization") String token) {
        logger.info("Extracting ownerId from JWT token");
        try {
            String ownerId = jwtUtil.extractUserId(token.substring(7));

            logger.info("Fetching owner details for ID: {}", ownerId);
            Optional<VehicleOwnerInfo> owner = vehicleOwnerService.getOwnerById(ownerId);
            if (owner.isPresent()) {
                logger.info("Owner found with ID: {}", ownerId);
                return ResponseEntity.ok(new ApiResponse<>(true, owner.get(), "Owner found."));
            } else {
                logger.warn("Owner not found with ID: {}", ownerId);
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Owner not found."));
            }
        } catch (Exception e) {
            logger.error("Failed to get owner details. Reason: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Failed to retrieve owner details."));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<VehicleOwnerInfo>>> getAllOwners(@RequestHeader("Authorization") String token) {
        logger.info("Fetching all vehicle owners.");
        List<VehicleOwnerInfo> owners = vehicleOwnerService.getAllOwnersWithVehicles();
        logger.info("Retrieved {} owners.", owners.size());
        return ResponseEntity.ok(new ApiResponse<>(true, owners, "All owners retrieved successfully."));
    }

    @PutMapping("update")
    public ResponseEntity<ApiResponse<VehicleOwnerInfo>> updateOwner(
            @RequestHeader("Authorization") String token,
            @RequestBody VehicleOwnerInfo updatedOwner) {

        logger.info("Extracting ownerId from JWT token");
        try {
            String jwtToken = token.substring(7);
            String ownerId = jwtUtil.extractUserId(jwtToken);

            logger.info("Updating owner details for ID: {}", ownerId);
            VehicleOwnerInfo savedOwner = vehicleOwnerService.updateOwner(ownerId, updatedOwner);

            logger.info("Owner details updated successfully for ID: {}", ownerId);
            return ResponseEntity.ok(new ApiResponse<>(true, savedOwner, "Owner details updated successfully."));
        } catch (Exception e) {
            logger.error("Failed to update owner details. Reason: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Failed to update owner details."));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteOwner(@RequestHeader("Authorization") String token) {
        logger.info("Extracting ownerId from JWT token");
        try {
            String ownerId = jwtUtil.extractUserId(token.substring(7));

            logger.info("Attempting to delete owner with ID: {}", ownerId);
            vehicleOwnerService.deleteOwner(ownerId);
            logger.info("Owner deleted successfully with ID: {}", ownerId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Owner deleted successfully.", "Owner deleted successfully."));
        } catch (IllegalArgumentException e) {
            logger.error("Failed to delete owner. Reason: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }
}