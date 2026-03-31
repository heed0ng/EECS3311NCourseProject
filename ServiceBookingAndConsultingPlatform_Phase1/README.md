# Service Booking and Consulting Platform – Phase 1

## Overview ##
This project implements the Phase 1 backend of a Service Booking and Consulting Platform for EECS3311 Group Project

The project supports:
- Browsing consulting services
- Requesting and managing bookings
- Consultant availability and booking decisions
- Simulated payment processing
- Simple broadcasted notification
- Single admin policy and consultant approval management



## Implemented GoF Design Patterns ##

### State Pattern

Used for the booking lifecycle.
Flow can be:
- Requested - Rejected
- Requested - Confirmed - PendingPayment - Paid - (Consultant mark) - Completed
- Requested - Cancelled by client at any time unless:
--> Deadline passed(24 hours default, for now hard coded)
--> Booking StartTime passed


Main context:
- `model.core.Booking`

States:
- `state.RequestedState`
- `state.ConfirmedState`
- `state.PendingPaymentState`
- `state.PaidState`
- `state.RejectedState`
- `state.CancelledState`
- `state.CompletedState`


### Strategy Pattern
Used for payment processing.

Strategy interface:
- `payment.PaymentMethodStrategy`

Concrete strategies:
- `payment.CreditCardPaymentStrategy`
- `payment.DebitCardPaymentStrategy`
- `payment.PayPalPaymentStrategy`
- `payment.BankTransferPaymentStrategy`

Factory:
- `payment.PaymentStrategyFactory`


### Observer Pattern
Used for notification and event publishing.

Publisher:
- `observer.EventPublisher`

Observer interface:
- `observer.Observer`

Concrete observers:
- `observer.ClientObserver`
- `observer.ConsultantObserver`
- `observer.AdminObserver`

Event hierarchy:
- `model.notification.DomainEvent`
- `model.notification.BookingRequestedEvent`
- `model.notification.BookingAcceptedEvent`
- `model.notification.BookingRejectedEvent`
- `model.notification.BookingCancelledEvent`
- `model.notification.PaymentProcessedEvent`
- `model.notification.ConsultantApprovalEvent`


## Repository Pattern
Repository is used as one additional architectural pattern to separate DB logic from business logic.

Repository interfaces include:
- `repository.BookingRepository`
- `repository.ClientRepository`
- `repository.ConsultantRepository`
- `repository.AvailabilitySlotRepository`
- `repository.ConsultantServiceOfferingRepository`
- `repository.ConsultingServiceRepository`
- `repository.SavedPaymentMethodRepository`
- `repository.PaymentTransactionRepository`
- `repository.PolicyRepository`

SQLite implementations are under:
- `repository.sqlite`


## Package Structure
Main packages:
- model
-> `model.core`
-> `model.user`
-> `model.payment`
-> `model.policy`
-> `model.notification`

- `service`
-> `service.impl`

- `repository`
-> `repository.sqlite`

- `payment`
- `state`
- `observer`
- `ui`
- `util`: Only for storing enums


## How to Run
1. Open the project in Eclipse.
2. Ensure the SQLite JDBC library is available in the build path.
3. Run:
   - `ui.TerminalUI`
4. Follow the terminal menu to demonstrate Phase 1 use cases.


## UML Diagrams
Diagrams are included in:

`docs/diagrams/`


Included diagrams:
- use case diagram

- backend overview diagram
- (partial)backend GoF-pattern-focused class diagram
- (partial)package diagrams


## Notes
- For now,(0329) Phase 1 backend-focused implementation.
- Notifications are simplified for Phase 1.
- SQLite is used for persistence.


## GitHub Repository
https://github.com/heed0ng/ServiceBookingAndConsultingPlatform_Phase1


## Author
Heedong Yang
EECS3311N Software Design 26 Winter
York University