# Service Booking and Consulting Platform — Phase 2

**Course:** EECS 3311 Software Design  
**Repository:** https://github.com/heed0ng/EECS3311NCourseProject  
**Author:** Heedong Yang

## 1\. Project Overview

This project implements a **Service Booking and Consulting Platform** for the EECS3311 course project.

Phase 2 extends the Phase 1 backend by adding:

* completed browser-based frontend pages for all three players (**client, consultant, admin**)
* a **Docker-based deployment** using separate backend, frontend, and data containers
* **AI customer assistant** that answers general platform questions through a backend API

The system supports the following major workflows:

* browse consulting services and available offerings
* request, accept, reject, cancel, and complete bookings and reviewing features
* manage consultant availability and service offerings
* process (simulated)payments, manage saved payment methods and review.
* manage basic system policies
* view notifications
* ask the AI assistant general platform questions

## 2\. Architecture Summary

The project follows a layered structure.

### Backend (`backend/src`)

* **`api`**: REST controllers, request/response DTOs, and DTO mappers
* **`service` / `service.impl`**: business logic for booking, consultant, payment, admin, and assistant workflows
* **`repository` / `repository.sqlite`**: persistence interfaces and SQLite implementations
* **`model`**: domain entities for bookings, users, policies, payments, and notification events
* **`state`**: booking lifecycle states
* **`paymentStrategy`**: payment-method-specific validation/processing strategies
* **`observer`**: event publishing and notification observers
* **`ai`**: Groq API client used by the AI assistant
* **`util`**: configuration, enums, and utility exceptions

### Frontend (`frontend/src`)

* static HTML pages for each role
* shared CSS in `css/styles.css`
* role-specific JavaScript in `js/client`, `js/consultant`, and `js/admin`
* shared JavaScript utilities in `js/common`

### Deployment

Docker Compose starts three services:

* **backend**: Spring Boot API
* **frontend**: Nginx serving static files(HTML/CSS/JS)
* **database**: Container holding persisted SQLite volume

## 3\. Design Patterns Used

### GoF Patterns

#### State Pattern

Used for the booking lifecycle.

Key classes:

* `backend.model.core.Booking`
* `backend.state.BookingState`
* `backend.state.RequestedState`
* `backend.state.ConfirmedState`
* `backend.state.PendingPaymentState`
* `backend.state.PaidState`
* `backend.state.RejectedState`
* `backend.state.CancelledState`
* `backend.state.CompletedState`



#### Strategy Pattern

Used for payment-method-specific handling.

Key classes:

* `backend.paymentStrategy.PaymentMethodStrategy`
* `backend.paymentStrategy.CreditCardPaymentStrategy`
* `backend.paymentStrategy.DebitCardPaymentStrategy`
* `backend.paymentStrategy.PayPalPaymentStrategy`
* `backend.paymentStrategy.BankTransferPaymentStrategy`
* `backend.paymentStrategy.PaymentStrategyFactory`



#### Observer Pattern

Used for notification/event publishing.

Key classes:

* `backend.observer.EventPublisher`
* `backend.observer.Observer`
* `backend.observer.ClientObserver`
* `backend.observer.ConsultantObserver`
* `backend.observer.AdminObserver`
* `backend.model.notification.DomainEvent`
* `backend.model.notification.BookingAcceptedEvent`
* `backend.model.notification.BookingRejectedEvent`
* `backend.model.notification.BookingCancelledEvent`
* `backend.model.notification.ConsultantApprovalEvent`
* `backend.model.notification.PaymentProcessedEvent`
* `backend.model.notification.PolicyUpdatedEvent`



### Architectural Patterns

#### Simple Repository Pattern for database

Persistence is separated from business logic through repository interfaces and SQLite-backed implementations.

#### Minimal MVC(Model - View - Controller)

The backend uses controllers for HTTP handling, services for business logic, repositories/models for data, and the frontend acts as the browser-based view layer.



High-level flow:

1. The client opens `client-chatbot.html`.
2. Frontend JavaScript posts the user question to `/api/client/assistant/question`.
3. `ClientAssistantController` passes the question to `ClientAssistantService`.
4. `DefaultClientAssistantService` builds general platform knowledge chunks and current public offering summaries.
5. Relevant chunks are selected with a simple keyword-based retrieval step.
6. The backend sends a system prompt + retrieved context + user question to `GroqChatClient`.
7. The assistant returns a response.
8. If the model is unavailable, service falls back to predefined answers.



## 5\. Main Features Implemented

### Client

* browse services / offerings
* load available slots
* request booking
* view booking history
* cancel booking
* manage payment methods
* process payment
* view payment history
* ask AI assistant questions



### Consultant

* add/update/remove availability slot
* add/remove service offering
* review pending booking requests
* accept/reject booking request
* view schedule
* complete booking



### Admin

* approve/reject consultant registrations
* update cancellation, refund, pricing, and notification policies
* view system status



## 6\. Project tree

```text
EECS3311NCourseProject/
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── frontend/
│   ├── Dockerfile
│   └── src/
├── diagrams/
│   ├── \*.png
│   └── PlantUMLs/
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```



## 7\. How to Run with Docker

### Requirements

* Docker Desktop installed and running

### Environment setup

Create a local `.env` file in the repository root based on `.env.example`:

1. Start the system
From the repository root: (EECS3311CourseProject/)

```bash or powershell
docker compose up --build
```

2. Access the application
* Frontend: `http://localhost:8081`
* Backend API: `http://localhost:8080`



3. Stop the system

```bash
docker compose down
```



## 8\. Diagrams

The repository includes the use case diagrams and class diagram with both comprehensive
and simplified package focused diagrams and their PlantUML sources.

Available under `diagrams/`:

* `UseCaseDiagrm.png`
* `classDiagram.png`
* `classDiagram\_backend.png`
* `packageDiagram\[Helper].png`

Available under `diagrams/PlantUMLs/`:

* `UseCaseDiagrm.puml`
* `classDiagram.puml`
* `classDiagram\_backend.puml`
* `packageDiagram\[Helper].puml`

## 9\.  Limitations

* notifications are currently **in-memory** and are not persisted to SQLite thus, volatile
* AI retrieval is **simple/manual faked RAG**, not vector-database-based retrieval
* SQLite is kept instead of actual SQL server running on the Database Server
* UI styling is minimal and **NOT** production level

