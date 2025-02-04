package com.trimblecars.leasemanagement.model.owner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@Data
@Entity
@Table(name = "vehicle_rental_info")
public class VehicleRentalInfo {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id = UUID.randomUUID().toString();

    @Column(name = "vehicle_name", nullable = false, length = 100)
    private String vehicleName;

    @Column(name = "registration_number", unique = true, nullable = false, length = 50)
    private String registrationNumber;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "make", length = 50)
    private String make;

    @Column(name = "model", length = 50)
    private String model;

    @Column(name = "vehicle_year", nullable = false)
    private int vehicleYear;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "seating_capacity")
    private int seatingCapacity;

    @Column(name = "daily_rental_price", nullable = false)
    private double dailyRentalPrice;

    @Column(name = "fuel_type", length = 20)
    private String fuelType;

    @Column(name = "transmission", length = 20)
    private String transmission;

    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "air_conditioning")
    private boolean airConditioning;

    @Column(name = "gps_enabled")
    private boolean gpsEnabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false)
    private VehicleCurrentStatus currentStatus = VehicleCurrentStatus.IDEAL;

    @Column(name = "insurance_available")
    private boolean insuranceAvailable;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    private VehicleOwnerInfo owner;
}