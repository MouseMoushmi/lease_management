package com.trimblecars.leasemanagement.controller.functional_test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;


import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerFunctionalTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void registerUser_shouldReturnSuccess() {
        String requestBody = """
            {
                "username": "testUser",
                "email": "test@example.com",
                "password": "password123",
                "role": "ROLE_CUSTOMER"
            }
        """;

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.email", equalTo("test@example.com"))
                .body("message", equalTo("User registered successfully"));
    }

    @Test
    void loginUser_shouldReturnToken() {
        String requestBody = "email=test@example.com&password=password123";

        RestAssured
                .given()
                .contentType(ContentType.URLENC)
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.token", notNullValue());
    }
}