package com.trimblecars.leasemanagement.service.owner;

import com.trimblecars.leasemanagement.dto.ApiResponse;
import com.trimblecars.leasemanagement.model.owner.VehicleOwnerInfo;

import com.trimblecars.leasemanagement.model.owner.VehicleRentalInfo;
import com.trimblecars.leasemanagement.repository.owner.VehicleOwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleOwnerService {

    private final VehicleOwnerRepository vehicleOwnerRepository;
    private final VehicleRentalService vehicleRentalService;

    public VehicleOwnerService(VehicleOwnerRepository vehicleOwnerRepository, VehicleRentalService vehicleRentalService) {
        this.vehicleOwnerRepository = vehicleOwnerRepository;
        this.vehicleRentalService = vehicleRentalService;
    }

    @Transactional
    public VehicleOwnerInfo registerOwner(VehicleOwnerInfo owner) {
        if (vehicleOwnerRepository.existsByEmail(owner.getEmail())) {
            throw new IllegalArgumentException("Owner with this email already exists.");
        }
        return vehicleOwnerRepository.save(owner);
    }


    public Optional<VehicleOwnerInfo> getOwnerById(String ownerId) {
        return vehicleOwnerRepository.findById(ownerId);
    }


    @Transactional
    public VehicleOwnerInfo updateOwner(String ownerId, VehicleOwnerInfo updatedOwner) {
        return vehicleOwnerRepository.findById(ownerId).map(existingOwner -> {
            existingOwner.setPhoneNumber(updatedOwner.getPhoneNumber());
            existingOwner.setAddress(updatedOwner.getAddress());
            existingOwner.setCity(updatedOwner.getCity());
            existingOwner.setState(updatedOwner.getState());
            existingOwner.setZipCode(updatedOwner.getZipCode());
            existingOwner.setCountry(updatedOwner.getCountry());
            existingOwner.setDrivingLicenseNumber(updatedOwner.getDrivingLicenseNumber());
            existingOwner.setAadhaarNumber(updatedOwner.getAadhaarNumber());
            existingOwner.setVerified(updatedOwner.isVerified());
            return vehicleOwnerRepository.save(existingOwner);
        }).orElseThrow(() -> new IllegalArgumentException("Owner not found with ID: " + ownerId));
    }


    public List<VehicleOwnerInfo> getAllOwners() {
        return vehicleOwnerRepository.findAll();
    }



    public List<VehicleOwnerInfo> getAllOwnersWithVehicles() {
        List<VehicleOwnerInfo> owners = vehicleOwnerRepository.findAll();
        for (VehicleOwnerInfo owner : owners) {
            List<VehicleRentalInfo> vehicles = vehicleRentalService.getVehiclesByOwner(owner.getId());
            owner.setVehicles(vehicles != null ? vehicles : new ArrayList<>()); // Null safety
        }
        return owners;
    }

    @Transactional
    public void deleteOwner(String ownerId) {
        if (!vehicleOwnerRepository.existsById(ownerId)) {
            throw new IllegalArgumentException("Owner not found with ID: " + ownerId);
        }
        vehicleOwnerRepository.deleteById(ownerId);
    }
}