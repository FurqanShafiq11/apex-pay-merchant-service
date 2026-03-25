# ApexPay: Merchant Solutions & Settlement Engine
[![Java CI with Maven](https://github.com/FurqanShafiq11/apex-pay-merchant-service/actions/workflows/maven.yml/badge.svg)](https://github.com/FurqanShafiq11/apex-pay-merchant-service/actions/workflows/maven.yml)
![Java Version](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.x-green)
![Docker](https://img.shields.io/badge/Docker-Verified-blue)
![Kubernetes](https://img.shields.io/badge/K8s-GKE_Ready-326ce5)

**ApexPay** is a high-availability, cloud-native microservice designed for **Corporate Bank Technology - Merchant Solutions**. This project demonstrates a production-grade implementation of an API-based payment orchestrator capable of handling merchant transaction processing and automated settlements.

---

## 🏦 Project Overview (Deutsche Bank Context)

This project was built to demonstrate technical excellence in the following areas required for the **Merchant Solutions** group:

*   **Idempotent Transaction Processing:** Uses custom logic and unique database constraints to prevent duplicate payments—a critical safety feature in fintech to prevent double-charging merchants during network retries.
*   **High Availability & Fault Tolerance:** Implemented **Resilience4j Circuit Breakers** to ensure the system remains responsive even if downstream card authorization networks or internal APIs fail.
*   **Microservice Architecture:** Engineered with a clean separation of concerns (DTOs, Services, Repositories) following the **#GlobalHausbank** technical standards.
*   **Cloud-Native Observability:** Integrated **Spring Boot Actuator** to provide health-check endpoints for **Kubernetes (GKE)** liveness and readiness probes.

---

## 🛠 Tech Stack

*   **Backend:** Java 21 (JDK), Spring Boot 3.3+
*   **Database:** H2 (In-memory development) / PostgreSQL compatibility via **Hibernate/JPA**
*   **Resilience:** Resilience4j (Circuit Breaker & Fallback mechanisms)
*   **Build & CI/CD:** Maven, GitHub Actions
*   **Containerization:** Docker & Kubernetes (GKE Ready)

---

## 🏗 Key Features & Design Patterns

### 1. The Idempotency Guard
In financial services, network retries can lead to duplicate transactions. ApexPay implements an **Idempotency Key** check at the Service layer, ensuring that any request with the same `merchantId` and `idempotencyKey` returns the original result instead of creating a new charge.

### 2. Resilience4j Circuit Breaker
Wrapped the downstream "Bank Authorization" simulator with a Circuit Breaker. 
*   **Closed State:** Normal operation.
*   **Open State:** If failure rates exceed 50%, the system automatically diverts to a **Fallback Method**, protecting the merchant platform from hanging.

### 3. Kubernetes Orchestration
The provided `k8s-deployment.yaml` defines a multi-replica deployment strategy with health probes, ensuring the application is self-healing and ready for **Google Kubernetes Engine (GKE)**.

---

## 🚦 Getting Started

### Prerequisites
*   Java 21
*   Docker Desktop
*   Maven

### Running Locally
```bash
### Build the project
./mvnw clean package -DskipTests

### Start the application
./mvnw spring-boot:run

### Running via Docker
docker pull furqan11s/merchant-payment-service:latest
docker run -p 8080:8080 furqan11s/merchant-payment-service:latest

### API Documentation
Create a Merchant Payment
Endpoint: POST /api/v1/payments

### Sample Request Body:
{
  "merchantId": "DB-BERLIN-001",
  "amount": 1250.75,
  "currency": "EUR",
  "idempotencyKey": "tx-uuid-998877"
}

### Response (201 Created):
{
  "id": "a1b2c3d4-e5f6-47a8-b9c0-d1e2f3a4b5c6",
  "status": "SUCCESS",
  "merchantId": "DB-BERLIN-001",
  "amount": 1250.75,
  "currency": "EUR",
  "createdAt": "2024-03-25T14:43:00"
}

### Monitoring & Databases
Health Check: http://localhost:8080/actuator/health
H2 Database Console: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:paymentdb
User: sa | Pass: password

👤 Author
Furqan Shafiq
