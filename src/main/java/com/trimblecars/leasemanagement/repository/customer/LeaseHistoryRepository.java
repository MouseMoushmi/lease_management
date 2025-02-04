package com.trimblecars.leasemanagement.repository.customer;

import com.trimblecars.leasemanagement.model.customer.LeaseHistory;
import com.trimblecars.leasemanagement.model.customer.EndCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaseHistoryRepository extends JpaRepository<LeaseHistory, String> {


    List<LeaseHistory> findByCustomer(EndCustomer customer);

    List<LeaseHistory> findByCustomerId(String customerId);

    List<LeaseHistory> findByVehicleId(String vehicleId);
}