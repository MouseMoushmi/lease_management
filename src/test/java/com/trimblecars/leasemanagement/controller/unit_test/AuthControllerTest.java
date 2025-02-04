package com.trimblecars.leasemanagement.controller.unit_test;

import com.trimblecars.leasemanagement.config.JwtUtil;
import com.trimblecars.leasemanagement.controller.AuthController;
import com.trimblecars.leasemanagement.dto.ApiResponse;
import com.trimblecars.leasemanagement.dto.LoginResponse;
import com.trimblecars.leasemanagement.dto.UserRequest;
import com.trimblecars.leasemanagement.model.auth.User;
import com.trimblecars.leasemanagement.model.auth.UserRole;
import com.trimblecars.leasemanagement.model.owner.VehicleOwnerInfo;
import com.trimblecars.leasemanagement.repository.UserRepository;
import com.trimblecars.leasemanagement.service.customer.EndCustomerService;
import com.trimblecars.leasemanagement.service.owner.VehicleOwnerService;
import com.trimblecars.leasemanagement.service.user.GoogleOAuth2Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private GoogleOAuth2Service googleOAuth2Service;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private VehicleOwnerService vehicleOwnerService;

    @Mock
    private EndCustomerService customerService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void registerUser_shouldReturnSuccessResponse() {
        // Given
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password123");
        userRequest.setPassword("password123");
        userRequest.setRole(UserRole.ROLE_CUSTOMER);

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword("hashedPassword");
        user.setRole(userRequest.getRole());
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("hashedPassword");
        when(jwtUtil.generateToken(anyString(), anyString(),anyString())).thenReturn("mockJwtToken");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        ResponseEntity<ApiResponse<LoginResponse>> response = authController.register(userRequest);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals("test@example.com", response.getBody().getData().getEmail());
        assertNotNull(response.getBody().getData().getToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_shouldReturnError_whenEmailExists() {
        // Given
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        userRequest.setEmail("existing@example.com");
        userRequest.setPassword("password123");
        userRequest.setRole(UserRole.ROLE_CUSTOMER);

        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(true);

        ResponseEntity<ApiResponse<LoginResponse>> response = authController.register(userRequest);

        // Then
        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Email already exists", response.getBody().getMessage());
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void loginUser_shouldReturnSuccessResponse() {
        // Given
        String username = "testUser";
        String password = "password123";

        // Mock authentication object
        org.springframework.security.core.Authentication mockAuthentication = mock(org.springframework.security.core.Authentication.class);
        User user = new User();
        user.setUsername(username);
        user.setEmail("test@example.com");
        user.setPassword("123456");
        user.setRole(UserRole.ROLE_CUSTOMER);
        when(mockAuthentication.getPrincipal()).thenReturn(user);

        when(authenticationManager.authenticate(any())).thenReturn(mockAuthentication);
        when(jwtUtil.generateToken(anyString(), anyString(),anyString())).thenReturn("mockJwtToken");

        ResponseEntity<ApiResponse<LoginResponse>> response = authController.login(username, password);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals("mockJwtToken", response.getBody().getData().getToken());
        verify(jwtUtil, times(1)).generateToken(anyString(), anyString(),anyString());
    }


    @Test
    void loginUser_shouldReturnError_whenInvalidCredentials() {
        // Given
        String username = "invalidUser";
        String password = "wrongPassword";

        when(authenticationManager.authenticate(any())).thenThrow(new IllegalArgumentException("Invalid credentials"));

        // When
        ResponseEntity<ApiResponse<LoginResponse>> response = authController.login(username, password);

        // Then
        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid input: Invalid credentials", response.getBody().getMessage());
    }


    @Test
    void registerVehicleOwner_shouldCallVehicleOwnerService() {
        // Given
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("ownerUser");
        userRequest.setEmail("owner@example.com");
        userRequest.setPassword("password123");
        userRequest.setRole(UserRole.ROLE_OWNER);

        User user = new User();
        user.setId("ownerId");
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword("hashedPassword");
        user.setRole(userRequest.getRole());
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("hashedPassword");
        when(jwtUtil.generateToken(anyString(), anyString(),anyString())).thenReturn("mockJwtToken");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        ResponseEntity<ApiResponse<LoginResponse>> response = authController.register(userRequest);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        verify(vehicleOwnerService, times(1)).registerOwner(any(VehicleOwnerInfo.class));
    }
}