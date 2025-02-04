package com.trimblecars.leasemanagement.service.customer;

import com.trimblecars.leasemanagement.exception.ResourceNotFoundException;
import com.trimblecars.leasemanagement.model.customer.EndCustomer;
import com.trimblecars.leasemanagement.model.customer.LeaseHistory;
import com.trimblecars.leasemanagement.model.owner.VehicleCurrentStatus;
import com.trimblecars.leasemanagement.model.owner.VehicleRentalInfo;
import com.trimblecars.leasemanagement.repository.customer.EndCustomerRepository;
import com.trimblecars.leasemanagement.repository.customer.LeaseHistoryRepository;
import com.trimblecars.leasemanagement.repository.owner.VehicleRentalInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EndCustomerService {

    private final EndCustomerRepository customerRepository;
    private final LeaseHistoryRepository leaseHistoryRepository;
    private final VehicleRentalInfoRepository vehicleRepository;

    public EndCustomerService(EndCustomerRepository customerRepository,
                              LeaseHistoryRepository leaseHistoryRepository,
                              VehicleRentalInfoRepository vehicleRepository) {
        this.customerRepository = customerRepository;
        this.leaseHistoryRepository = leaseHistoryRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public EndCustomer registerCustomer(EndCustomer customer) {
        return customerRepository.save(customer);
    }

    public List<VehicleRentalInfo> getAvailableVehicles() {
        return vehicleRepository.findByCurrentStatus(VehicleCurrentStatus.IDEAL);
    }

    @Transactional
    public LeaseHistory startLease(String customerId, String vehicleId) {
        EndCustomer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        if (customer.getActiveLeases() >= 2) {
            throw new IllegalArgumentException("Customer can have a maximum of 2 active leases.");
        }

        VehicleRentalInfo vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        if (vehicle.getCurrentStatus() != VehicleCurrentStatus.IDEAL) {
            throw new IllegalArgumentException("Vehicle is not available for lease.");
        }

        vehicle.setCurrentStatus(VehicleCurrentStatus.ON_LEASE);
        vehicleRepository.save(vehicle);

        customer.setActiveLeases(customer.getActiveLeases() + 1);
        customerRepository.save(customer);

        LeaseHistory leaseHistory = new LeaseHistory();
        leaseHistory.setCustomer(customer);
        leaseHistory.setVehicle(vehicle);
        leaseHistory.setLeaseStartDate(LocalDateTime.now());

        return leaseHistoryRepository.save(leaseHistory);
    }

    @Transactional
    public LeaseHistory endLease(String leaseId) {
        LeaseHistory lease = leaseHistoryRepository.findById(leaseId)
                .orElseThrow(() -> new IllegalArgumentException("Lease not found"));

        lease.setLeaseEndDate(LocalDateTime.now());
        lease.getVehicle().setCurrentStatus(VehicleCurrentStatus.IDEAL);
        lease.getCustomer().setActiveLeases(lease.getCustomer().getActiveLeases() - 1);

        vehicleRepository.save(lease.getVehicle());
        customerRepository.save(lease.getCustomer());

        return leaseHistoryRepository.save(lease);
    }

    @Transactional
    public EndCustomer updateCustomer(String customerId, EndCustomer updatedCustomer) {
        Optional<EndCustomer> existingCustomerOpt = customerRepository.findById(customerId);
        if (existingCustomerOpt.isEmpty()) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }

        EndCustomer existingCustomer = existingCustomerOpt.get();

        if (updatedCustomer.getPhoneNumber() != null) {
            existingCustomer.setPhoneNumber(updatedCustomer.getPhoneNumber());
        }
        if (updatedCustomer.getAddress() != null) {
            existingCustomer.setAddress(updatedCustomer.getAddress());
        }
        if (updatedCustomer.getCity() != null) {
            existingCustomer.setCity(updatedCustomer.getCity());
        }
        if (updatedCustomer.getState() != null) {
            existingCustomer.setState(updatedCustomer.getState());
        }
        if (updatedCustomer.getZipCode() != null) {
            existingCustomer.setZipCode(updatedCustomer.getZipCode());
        }
        if (updatedCustomer.getCountry() != null) {
            existingCustomer.setCountry(updatedCustomer.getCountry());
        }
        if (updatedCustomer.getDrivingLicenseNumber() != null) {
            existingCustomer.setDrivingLicenseNumber(updatedCustomer.getDrivingLicenseNumber());
        }

        return customerRepository.save(existingCustomer);
    }

    public List<LeaseHistory> getCustomerLeaseHistory(String customerId) {
        return leaseHistoryRepository.findByCustomerId(customerId);
    }
}