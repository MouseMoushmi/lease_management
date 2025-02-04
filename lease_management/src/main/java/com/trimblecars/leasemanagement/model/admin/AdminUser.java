package com.trimblecars.leasemanagement.model.admin;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Data
@Entity
@Table(name = "admin_users")
public class AdminUser {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id = UUID.randomUUID().toString();


    @Column(name = "email")
    private String email;

    @Column(name = "phone_number",  length = 15)
    private String phoneNumber;

    @Column(name = "role")
    private String role;

    @Column(name = "department")
    private String department;

    @Column(name = "assigned_region")
    private String assignedRegion;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "super_admin_privileges")
    private boolean hasSuperAdminPrivileges = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login")
    private Date lastLogin;
}
