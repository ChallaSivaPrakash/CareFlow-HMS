# 🏥 CareFlow HMS (Hospital Management System)

An enterprise-grade, secure, and real-time Hospital Management System designed for high-concurrency clinical environments.

## 🚀 Enterprise Features
* **Role-Based Gateway:** Unified landing page with role-aware routing (Admin, Doctor, Clerk) powered by JWT.
* **Global ER Roster:** A live, synchronized patient queue visible to all medical staff for seamless emergency handoffs.
* **Real-Time WebSocket Intercom:** STOMP-based live messaging and emergency alerts (Redis-ready for horizontal scaling).
* **AI-Ready Triage Engine:** Modular triage logic with a "Human-in-the-Loop" safety guardrail.
* **Data Security & Integrity:** AES-256-GCM database encryption for patient PII and JPA Optimistic Locking for collision-free booking.
* **Integrated Payments:** Stripe-ready webhook pattern for online OPD registration.
* **Clinical Recovery Index:** Real-time patient health tracking via a calculated recovery score.


## 💻 Tech Stack
* **Frontend:** Angular 18 (Standalone, Signals API), Bootstrap 5.
* **Backend:** Java 21, Spring Boot 3.5.14, Spring Security, JPA/Hibernate.
* **Infrastructure:** MySQL 8.0, Redis (for WebSocket Broker), Docker.

## 🛠 Advanced Architectural Solutions
* **Scalable Authentication:** Implemented JWT Access/Refresh tokens with an automated 401-interceptor refresh logic.
* **Concurrency Control:** Utilized JPA `@Version` optimistic locking to prevent double-booking of appointments and bed slots.
* **Sensitive Data Protection:** Implemented AES-256-GCM encryption at the JPA layer to protect patient medical records at rest.
* **Reactive UI:** Migrated to Angular Signals and `ChangeDetectorRef` to eliminate UI rendering freezes in high-frequency data environments.
* **Asynchronous Operations:** Leveraged `@Async` Spring tasks for non-blocking email notifications and automated alerts.

## ⚙️ Deployment

### Using Docker (Recommended)
```bash
docker compose up --build
```

### Manual Setup

#### Backend Configuration
1. Navigate to `careflow-backend`.
2. Update `src/main/resources/application.yml` with your local MySQL credentials.
3. Ensure you have an empty MySQL schema named `hospital_db`.
4. Run the application via `mvn spring-boot:run` (Hibernate will auto-generate the tables).

#### Frontend Configuration
1. Navigate to `hospital-frontend`.
2. Run `npm install` to download dependencies.
3. Start the development server with `ng serve`.
4. Access the Staff Portal at `http://localhost:4200/login`.

## 🛠 Engineering Challenges & Solutions
* **Stateless Authentication & Security:** Implemented a stateless JWT-based authentication system to ensure horizontal scalability. I overcame the "Log Out" revocation challenge by architecting a Redis-ready blacklist strategy, ensuring security without sacrificing performance.

* **Concurrency & Data Integrity:** Resolved potential race conditions in bed allocation by utilizing JPA `@Version` optimistic locking, ensuring that no two nursing stations can claim the same bed simultaneously in a high-traffic emergency environment.

* **Real-time Event Architecture:** Engineered a decoupled, event-driven notification system using WebSockets (STOMP). This replaced expensive polling operations with instantaneous, server-pushed emergency alerts, reducing server CPU load during mass-casualty simulations.

* **AI-Ready Modular Design:** Developed the triage logic using a `@ConditionalOnProperty` strategy pattern. This allows the system to switch between current heuristic triage rules and future AI-based infrastructure providers via simple configuration changes, without refactoring the core persistence layer.

* **Relational Boundary Integrity:** Ensured clinical accountability by strictly mapping Doctor-to-Patient relationships using `@ManyToOne` and `@OneToMany` with restricted data visibility, guaranteeing that patient PII is only accessible to authorized medical personnel.
