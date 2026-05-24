# CareFlow HMS - Enterprise Hospital Management System

**CareFlow HMS** is an event-driven, secure, and clinically-validated hospital management platform designed for OPD triage, bed allocation, and real-time medical staff communication.

## 🚀 Key Clinical & Technical Features
* **Automated Triage Engine:** Uses heuristic rules to prioritize patients (RED/YELLOW/GREEN) and auto-assign departments.
* **Smart Bed Allocation:** Automated suggest-and-assign system for `ADMITTED` patients, with manual override capabilities.
* **Real-time Collaboration:** WebSocket-based "Trauma Alert" system that broadcasts emergencies globally to all connected staff.
* **Security First:** JWT-based authentication with role-based access (RBAC) and OTP-secured password recovery via SMTP.
* **Audit Trail:** Every critical medical action (Discharge, Claiming a patient, Assignment) is recorded in a secure Audit Log for clinical compliance.

## 🛠 Tech Stack
* **Backend:** Java 21, Spring Boot 3.2.5, MySQL, Spring Security, JWT, WebSockets.
* **Frontend:** Angular 18, Bootstrap 5, RxJS, STOMP.
* **Testing:** JUnit 5, Mockito, MockMvc (Backend), Angular Testing Library (Frontend).

## 📊 System Architecture


## 🏃 How to Run
1. **Database:** Ensure MySQL is running and a database named `careflow` exists. Update `application.properties` with your DB credentials.
2. **Backend:** ```bash
   cd careflow-backend
   mvn spring-boot:run