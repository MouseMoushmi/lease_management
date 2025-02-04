package com.trimblecars.leasemanagement.repository.owner;

import com.trimblecars.leasemanagement.model.owner.VehicleOwnerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VehicleOwnerRepository extends JpaRepository<VehicleOwnerInfo, String> {

    Optional<VehicleOwnerInfo> findByEmail(String email);

    boolean existsByEmail(String email);
}