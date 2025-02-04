package com.trimblecars.leasemanagement.model.customer;

import com.trimblecars.leasemanagement.model.owner.VehicleRentalInfo;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "lease_history")
public class LeaseHistory {

    @Id
    @Column(name = "lease_history_id")
    private String id = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private EndCustomer customer;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private VehicleRentalInfo vehicle;

    @Column(name = "lease_start_date", nullable = false)
    private LocalDateTime leaseStartDate;

    @Column(name = "lease_end_date", nullable = false)
    private LocalDateTime leaseEndDate;

    @Column(name = "total_cost", nullable = false)
    private double totalCost;
}