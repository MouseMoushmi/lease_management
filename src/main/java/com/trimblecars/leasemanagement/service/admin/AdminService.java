package com.trimblecars.leasemanagement.service.admin;


import com.trimblecars.leasemanagement.exception.ResourceNotFoundException;
import com.trimblecars.leasemanagement.model.admin.AdminUser;
import com.trimblecars.leasemanagement.model.customer.EndCustomer;
import com.trimblecars.leasemanagement.model.customer.LeaseHistory;
import com.trimblecars.leasemanagement.model.owner.VehicleCurrentStatus;
import com.trimblecars.leasemanagement.model.owner.VehicleOwnerInfo;
import com.trimblecars.leasemanagement.model.owner.VehicleRentalInfo;
import com.trimblecars.leasemanagement.repository.admin.AdminUserRepository;
import com.trimblecars.leasemanagement.repository.customer.EndCustomerRepository;
import com.trimblecars.leasemanagement.repository.customer.LeaseHistoryRepository;
import com.trimblecars.leasemanagement.repository.owner.VehicleOwnerRepository;
import com.trimblecars.leasemanagement.repository.owner.VehicleRentalInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final VehicleRentalInfoRepository vehicleRentalRepository;
    private final VehicleOwnerRepository vehicleOwnerRepository;
    private final EndCustomerRepository endCustomerRepository;
    private final LeaseHistoryRepository leaseHistoryRepository;
    private final AdminUserRepository adminUserRepository;

    public AdminService(VehicleRentalInfoRepository vehicleRentalRepository, VehicleOwnerRepository vehicleOwnerRepository, EndCustomerRepository endCustomerRepository, LeaseHistoryRepository leaseHistoryRepository, AdminUserRepository adminUserRepository) {
        this.vehicleRentalRepository = vehicleRentalRepository;
        this.vehicleOwnerRepository = vehicleOwnerRepository;
        this.endCustomerRepository = endCustomerRepository;
        this.leaseHistoryRepository = leaseHistoryRepository;
        this.adminUserRepository = adminUserRepository;
    }

    @Transactional
    public VehicleRentalInfo registerVehicle(VehicleRentalInfo vehicle, String ownerId) {
        Optional<VehicleOwnerInfo> owner = vehicleOwnerRepository.findById(ownerId);
        if (owner.isEmpty()) throw new IllegalArgumentException("Owner not found with ID: " + ownerId);
        vehicle.setOwner(owner.get());
        return vehicleRentalRepository.save(vehicle);
    }

    @Transactional
    public AdminUser registerAdmin(AdminUser admin, String id) {
        Optional<AdminUser> owner = adminUserRepository.findById(id);
        if (owner.isEmpty()) throw new IllegalArgumentException("Admin not found with ID: " + id);
        return adminUserRepository.save(admin);
    }

    @Transactional
    public void deleteVehicle(String vehicleId) {
        vehicleRentalRepository.deleteById(vehicleId);
    }

    public List<VehicleRentalInfo> getAllVehicles() {
        return vehicleRentalRepository.findAll();
    }


    @Transactional
    public VehicleOwnerInfo registerOwner(VehicleOwnerInfo owner) {
        return vehicleOwnerRepository.save(owner);
    }

    @Transactional
    public void deleteOwner(String ownerId) {
        vehicleOwnerRepository.deleteById(ownerId);
    }

    public List<VehicleOwnerInfo> getAllOwners() {
        return vehicleOwnerRepository.findAll();
    }


    @Transactional
    public VehicleRentalInfo updateVehicleStatus(String vehicleId, VehicleCurrentStatus status) {
        Optional<VehicleRentalInfo> vehicleOptional = vehicleRentalRepository.findById(vehicleId);

        if (vehicleOptional.isEmpty()) {
            throw new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId);
        }

        VehicleRentalInfo vehicle = vehicleOptional.get();
        vehicle.setCurrentStatus(status);
        return vehicleRentalRepository.save(vehicle);
    }


    @Transactional
    public EndCustomer registerCustomer(EndCustomer customer) {
        return endCustomerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(String customerId) {
        endCustomerRepository.deleteById(customerId);
    }

    public List<EndCustomer> getAllCustomers() {
        return endCustomerRepository.findAll();
    }


    @Transactional
    public LeaseHistory startLease(String customerId, String vehicleId) {
        LeaseHistory lease = new LeaseHistory();
        lease.setCustomer(endCustomerRepository.findById(customerId).orElseThrow(() -> new IllegalArgumentException("Customer not found")));
        lease.setVehicle(vehicleRentalRepository.findById(vehicleId).orElseThrow(() -> new IllegalArgumentException("Vehicle not found")));
        return leaseHistoryRepository.save(lease);
    }

    @Transactional
    public LeaseHistory endLease(String leaseId) {
        LeaseHistory lease = leaseHistoryRepository.findById(leaseId).orElseThrow(() -> new IllegalArgumentException("Lease not found"));
        leaseHistoryRepository.delete(lease);
        return lease;
    }

    public List<LeaseHistory> getAllLeaseHistories() {
        return leaseHistoryRepository.findAll();
    }
}
