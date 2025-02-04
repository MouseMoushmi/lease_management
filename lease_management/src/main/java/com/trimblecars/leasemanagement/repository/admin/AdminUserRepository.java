package com.trimblecars.leasemanagement.repository.admin;

import com.trimblecars.leasemanagement.model.admin.AdminUser;
import com.trimblecars.leasemanagement.model.auth.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, String> {

    Optional<AdminUser> findByPhoneNumber(String phoneNumber);

    Optional<AdminUser> findByRole(UserRole role);

    Optional<AdminUser> findByDepartment(String department);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<AdminUser> findByIsActive(boolean isActive);
}