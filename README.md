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
```bash
docker-compose up --build