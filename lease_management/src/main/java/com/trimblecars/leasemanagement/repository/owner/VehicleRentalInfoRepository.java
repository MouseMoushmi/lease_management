package com.trimblecars.leasemanagement.repository.owner;

import com.trimblecars.leasemanagement.model.owner.VehicleCurrentStatus;
import com.trimblecars.leasemanagement.model.owner.VehicleRentalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRentalInfoRepository extends JpaRepository<VehicleRentalInfo, String> {

    List<VehicleRentalInfo> findByOwnerId(String ownerId);

    List<VehicleRentalInfo> findByCurrentStatus(VehicleCurrentStatus status);

    List<VehicleRentalInfo> findLeaseHistoryById(String vehicleId);
}
