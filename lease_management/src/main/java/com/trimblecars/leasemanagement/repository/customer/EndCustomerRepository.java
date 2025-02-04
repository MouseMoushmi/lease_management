package com.trimblecars.leasemanagement.repository.customer;

import com.trimblecars.leasemanagement.model.customer.EndCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EndCustomerRepository extends JpaRepository<EndCustomer, String> {

    Optional<EndCustomer> findByPhoneNumber(String phoneNumber);

    Optional<EndCustomer> findByEmail(String email);

    boolean existsByDrivingLicenseNumber(String drivingLicenseNumber);
}