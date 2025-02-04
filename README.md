# **Lease Management System**
A Spring Boot-based lease management system that enables vehicle owners to list their cars for lease, end customers to lease vehicles, and an admin panel for managing operations.

---

## **Table of Contents**
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Installation Guide](#installation-guide)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Google OAuth Authentication](#google-oauth-authentication)
- [Swagger UI](#swagger-ui)
- [Running Tests](#running-tests)
- [Contributing](#contributing)

---

## **Features**
✅ **End Customer**
- Register and view available vehicles for lease.
- Start and end leases with a maximum of **2 active leases at a time**.
- View their own lease history.

✅ **Car Owner**
- Register and manage their vehicles.
- View and update vehicle details.

✅ **Admin**
- Manage all vehicles, customers, and owners.
- Update vehicle statuses and lease operations.

---

## **Technology Stack**
- **Java 17**
- **Spring Boot 3.4.2**
- **Spring Security with JWT & Google OAuth**
- **Spring Data JPA**
- **H2 Database**
- **Swagger (Springdoc OpenAPI)**
- **Gradle**
- **Docker Support**
- **Junit & RestAssured for testing**

---

## **Installation Guide**
1. Clone the repository:
   ```sh
   git clone https://github.com/MouseMoushmi/lease_management.git
   cd lease_management
   ```

2. Configure your **database settings** in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.username=
   spring.datasource.password=
   ```

3. **Run the application**:
   ```sh
   ./gradlew bootRun
   ```

4. **Access API Docs:**
   - Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
   - API Documentation: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## **Configuration**
### **Google OAuth**
To enable **Google Login**, configure the following environment variables:



**Login via Google OAuth:**
- Open your browser and go to:  
  **[http://localhost:8080/oauth2/authorization/google](http://localhost:8080/oauth2/authorization/google)**

---


### JWT Authentication
To register and get a JWT token, use the following API:
```
POST /api/auth/register
```
Request Body:
```json
{
    "username": "testUser",
    "email": "test@example.com",
    "password": "password123",
    "role": "ROLE_CUSTOMER"
}
```
After registration, use the token received in the response as a **Bearer Token** for all secured endpoints.

## API Usage
### Register a Vehicle
```
POST /api/vehicles/register
```
Request Body:
```json
{
    "vehicleName": "Toyota Corolla",
    "registrationNumber": "TN-01-AB-1234",
    "ownerId": "12345"
}
```

### Start a Lease
```
POST /api/customers/lease/start
```
Request Params:
- `customerId`: ID of the customer
- `vehicleId`: ID of the vehicle

### End a Lease
```
POST /api/customers/lease/end
```
Request Params:
- `leaseId`: ID of the lease

## Export Lease History
To export lease history as a **PDF Report**, use:
```
GET /api/admin/lease/history/pdf
```

## Configuration
### Google OAuth Configuration
Add the following properties to your `application.properties`:
```
spring.security.oauth2.client.registration.google.client-id=960738618311-2mkg7ac9pieeoggs8lvg2mqo6krkn9p7.apps.googleusercontent.com
spring.security.oauth2.client.registration.google.client-secret=
```
 
## **API Documentation**
### **Swagger UI**
- **Access:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- Provides an interactive UI to test APIs.
- All endpoints are categorized under:
  - `/api/customers/`
  - `/api/owners/`
  - `/api/admin/`


---



## **Running Tests**
Run unit and integration tests using:

```sh
./gradlew test
```

For API tests:
```sh
./gradlew integrationTest
```

---

## **License**
This project is open-source and available under the **MIT License**.

---

### 🚀 **Happy Coding!**
This README covers everything to get started with the **Lease Management System**, including Swagger, Google OAuth, and API documentation.

