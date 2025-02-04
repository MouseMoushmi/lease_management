package com.trimblecars.leasemanagement.model.customer;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Data
@Entity
@Table(name = "end_customer")
public class EndCustomer {

    @Id
    @Column(name = "id")
    private String id = UUID.randomUUID().toString();

    @Column(name = "email", unique = true, nullable = false)
    private String email;


    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "country")
    private String country;

    @Column(name = "driving_license_number")
    private String drivingLicenseNumber;

    @Column(name = "verified")
    private boolean verified;

    @Column(name = "active_leases")
    private int activeLeases;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LeaseHistory> leaseHistory;
}