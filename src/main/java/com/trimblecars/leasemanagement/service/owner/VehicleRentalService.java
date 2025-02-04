package com.trimblecars.leasemanagement.service.owner;

import com.trimblecars.leasemanagement.model.owner.VehicleRentalInfo;
import com.trimblecars.leasemanagement.model.owner.VehicleOwnerInfo;
import com.trimblecars.leasemanagement.repository.owner.VehicleOwnerRepository;
import com.trimblecars.leasemanagement.repository.owner.VehicleRentalInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleRentalService {

    private final VehicleRentalInfoRepository vehicleRentalRepository;
    private final VehicleOwnerRepository vehicleOwnerRepository;

    public VehicleRentalService(VehicleRentalInfoRepository vehicleRentalRepository, VehicleOwnerRepository vehicleOwnerRepository) {
        this.vehicleRentalRepository = vehicleRentalRepository;
        this.vehicleOwnerRepository = vehicleOwnerRepository;
    }


    @Transactional
    public VehicleRentalInfo registerVehicle(VehicleRentalInfo vehicle, String ownerId) {
        Optional<VehicleOwnerInfo> owner = vehicleOwnerRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new IllegalArgumentException("Owner not found with ID: " + ownerId);
        }
        vehicle.setOwner(owner.get());
        return vehicleRentalRepository.save(vehicle);
    }




    public Optional<VehicleRentalInfo> getVehicleById(String vehicleId) {
        return vehicleRentalRepository.findById(vehicleId);
    }


    public List<VehicleRentalInfo> getVehiclesByOwner(String ownerId) {
        return vehicleRentalRepository.findByOwnerId(ownerId);
    }


    public List<VehicleRentalInfo> getLeaseHistory(String vehicleId) {
        return vehicleRentalRepository.findLeaseHistoryById(vehicleId);
    }

    public void deleteAllVehicles() {
        vehicleRentalRepository.deleteAll();
    }
}