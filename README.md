# 🏥 CareFlow HMS (Hospital Management System)

A modern, enterprise-grade Hospital Management System designed to handle multi-role staff authentication, patient triage, and real-time intra-hospital communication. 

## 🚀 Key Features

* **Multi-Role Dashboards:** Distinct workspaces for Administrators, Doctors, and OPD Clerks, secured via Spring Security and route guards.
* **Real-Time Staff Intercom:** A floating chat widget powered by STOMP WebSockets. Features dynamic topic routing to isolate conversations by medical department (e.g., Cardiology, Neurology).
* **Global Emergency Override:** A zero-latency red-alert broadcast system that pushes emergency trauma notifications to all active staff dashboards simultaneously.
* **Smart Patient Triage:** An OPD intake queue that categorizes patients by priority (RED, YELLOW, GREEN) and allows smart-filtering for doctor assignment.
* **JWT-Based Authentication:** Stateless session management with hashed credentials for secure, scalable access.


## 💻 Tech Stack

**Frontend:**
* Angular 18 (Standalone Components)
* Bootstrap 5 & CSS3
* RxJS & SockJS (WebSocket Client)

**Backend:**
* Java 21 & Spring Boot 3
* Spring Security & JWT
* Spring WebSocket / STOMP Message Broker
* Spring Data JPA / Hibernate

**Database:**
* MySQL 8.0

## ⚙️ Local Setup Instructions

### Backend Configuration
1. Navigate to `careflow-backend`.
2. Update `src/main/resources/application.yml` with your local MySQL credentials.
3. Ensure you have an empty MySQL schema named `hospital_db`.
4. Run the application via `mvn spring-boot:run` (Hibernate will auto-generate the tables).

### Frontend Configuration
1. Navigate to `hospital-frontend`.
2. Run `npm install` to download dependencies.
3. Start the development server with `ng serve`.
4. Access the Staff Portal at `http://localhost:4200/login`.

