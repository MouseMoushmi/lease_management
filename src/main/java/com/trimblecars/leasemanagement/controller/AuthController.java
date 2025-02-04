package com.trimblecars.leasemanagement.controller;

import com.trimblecars.leasemanagement.config.JwtUtil;
import com.trimblecars.leasemanagement.dto.ApiResponse;
import com.trimblecars.leasemanagement.dto.LoginResponse;
import com.trimblecars.leasemanagement.dto.UserRequest;
import com.trimblecars.leasemanagement.model.admin.AdminUser;
import com.trimblecars.leasemanagement.model.auth.User;
import com.trimblecars.leasemanagement.model.auth.UserRole;
import com.trimblecars.leasemanagement.model.customer.EndCustomer;
import com.trimblecars.leasemanagement.model.owner.VehicleOwnerInfo;
import com.trimblecars.leasemanagement.repository.UserRepository;
import com.trimblecars.leasemanagement.service.admin.AdminService;
import com.trimblecars.leasemanagement.service.customer.EndCustomerService;
import com.trimblecars.leasemanagement.service.owner.VehicleOwnerService;
import com.trimblecars.leasemanagement.service.user.GoogleOAuth2Service;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final GoogleOAuth2Service googleOAuth2Service;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VehicleOwnerService vehicleOwnerService;
    private final EndCustomerService customerService;
    private final AdminService adminService;

    public AuthController(GoogleOAuth2Service googleOAuth2Service, JwtUtil jwtUtil, AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, VehicleOwnerService vehicleOwnerService, EndCustomerService customerService, AdminService adminService) {
        this.googleOAuth2Service = googleOAuth2Service;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.vehicleOwnerService = vehicleOwnerService;
        this.customerService = customerService;
        this.adminService = adminService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@RequestBody UserRequest userRequest) {
        try {
            if (userRepository.existsByEmail(userRequest.getEmail())) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Email already exists"));
            }
            User user = new User();
            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getEmail());
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            user.setRole(UserRole.valueOf(userRequest.getRole().name()));
            user.setCreatedAt(LocalDateTime.now());
            String jwtToken = jwtUtil.generateToken(user.getId(), user.getEmail(),user.getRole().name());
            user.setToken(jwtToken);
            userRepository.save(user);
            if (user.getRole() == UserRole.ROLE_OWNER) {
                VehicleOwnerInfo vehicleOwnerInfo = new VehicleOwnerInfo();
                vehicleOwnerInfo.setId(user.getId());
                vehicleOwnerInfo.setEmail(user.getEmail());
                vehicleOwnerService.registerOwner(vehicleOwnerInfo);
            } else if (user.getRole() == UserRole.ROLE_CUSTOMER) {
                EndCustomer endCustomer = new EndCustomer();
                endCustomer.setId(user.getId());
                endCustomer.setEmail(user.getEmail());
                customerService.registerCustomer(endCustomer);
            } else if (user.getRole() == UserRole.ROLE_ADMIN) {
                AdminUser adminUser = new AdminUser();
                adminUser.setId(user.getId());
                adminUser.setEmail(user.getEmail());
                adminService.registerAdmin(adminUser, user.getId());
            }
            LoginResponse loginResponse = new LoginResponse(jwtToken, user.getEmail(), user.getUsername(), "User registered successfully");

            return ResponseEntity.ok(new ApiResponse<>(true, loginResponse, "User registered successfully"));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Invalid role: " + ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, null, "An error occurred: " + ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestParam String email, @RequestParam String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            User user = (User) authentication.getPrincipal();
            String token = jwtUtil.generateToken(user.getId(), user.getEmail(),user.getRole().name());
            user.setToken(token);

            LoginResponse loginResponse = new LoginResponse(token, user.getEmail(), user.getUsername(), "Login successful");

            return ResponseEntity.ok(new ApiResponse<>(true, loginResponse, "Login successful"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Invalid input: " + ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, null, "An error occurred: " + ex.getMessage()));
        }
    }

    @GetMapping("/google")
    public ResponseEntity<ApiResponse<LoginResponse>> googleLogin(OAuth2AuthenticationToken token) {
        try {
            if (token == null) {
                throw new IllegalArgumentException("OAuth2AuthenticationToken is null");
            }

            User user = googleOAuth2Service.processOAuthPostLogin(token);
            String jwtToken = jwtUtil.generateToken(user.getId(), user.getEmail(),user.getRole().name());
            user.setToken(jwtToken);
            userRepository.save(user);
            if (user.getRole() == UserRole.ROLE_OWNER) {
                VehicleOwnerInfo vehicleOwnerInfo = new VehicleOwnerInfo();
                vehicleOwnerInfo.setId(user.getId());
                vehicleOwnerInfo.setEmail(user.getEmail());
                vehicleOwnerService.registerOwner(vehicleOwnerInfo);
            } else if (user.getRole() == UserRole.ROLE_CUSTOMER) {
                EndCustomer endCustomer = new EndCustomer();
                endCustomer.setId(user.getId());
                endCustomer.setEmail(user.getEmail());
                customerService.registerCustomer(endCustomer);
            } else if (user.getRole() == UserRole.ROLE_ADMIN) {
                AdminUser adminUser = new AdminUser();
                adminUser.setId(user.getId());
                adminUser.setEmail(user.getEmail());
                adminService.registerAdmin(adminUser, user.getId());
            }
            LoginResponse loginResponse = new LoginResponse(jwtToken, user.getEmail(), user.getUsername(), "Logged in with Google");

            return ResponseEntity.ok(new ApiResponse<>(true, loginResponse, "Logged in with Google"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Invalid input: " + ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, null, "An unexpected error occurred: " + ex.getMessage()));
        }
    }
}

//http://localhost:8080/oauth2/authorization/google