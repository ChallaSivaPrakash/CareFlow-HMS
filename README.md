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

### 🛠 Engineering Challenges & Solutions
* Stateless Authentication & Security: Implemented a stateless JWT-based authentication system to ensure horizontal scalability. I overcame the "Log Out" revocation challenge by architecting a Redis-ready blacklist strategy, ensuring security without sacrificing performance.

* Concurrency & Data Integrity: Resolved potential race conditions in bed allocation by utilizing JPA @Version optimistic locking, ensuring that no two nursing stations can claim the same bed simultaneously in a high-traffic emergency environment.

* Real-time Event Architecture: Engineered a decoupled, event-driven notification system using WebSockets (STOMP). This replaced expensive polling operations with instantaneous, server-pushed emergency alerts, reducing server CPU load during mass-casualty simulations.

* AI-Ready Modular Design: Developed the triage logic using a @ConditionalOnProperty strategy pattern. This allows the system to switch between current heuristic triage rules and future AI-based infrastructure providers via simple configuration changes, without refactoring the core persistence layer.

* Relational Boundary Integrity: Ensured clinical accountability by strictly mapping Doctor-to-Patient relationships using @ManyToOne and @OneToMany with restricted data visibility, guaranteeing that patient PII is only accessible to authorized medical personnel.