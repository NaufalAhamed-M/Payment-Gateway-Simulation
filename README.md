# Payment Gateway Simulator

A Spring Boot based payment gateway simulator that models the core lifecycle
of an online payment system, including payment creation, idempotency,
processing, retries, refunds, webhooks, settlement, reconciliation and audit logging.

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA / Hibernate
- MySQL
- Maven
- REST API
- JUnit 5 / Mockito
- Swagger / OpenAPI
- Postman
- Git / GitHub

## Features

- Merchant management
- Payment creation
- Card / UPI / Net Banking support
- Payment state management
- Idempotency protection
- Concurrency-safe payment creation
- Simulated payment processor
- SUCCESS / FAILED / TIMEOUT / UNKNOWN scenarios
- Payment status resolution
- Automatic retry handling
- Partial and full refunds
- Webhook generation
- Webhook delivery and retry mechanism
- HMAC webhook signature verification
- Audit logging
- Settlement processing
- Gateway fee calculation
- Reconciliation
- Global exception handling
- Request validation
- Swagger API documentation
- Unit testing

## Architecture

The application follows a layered architecture:

Controller
↓
Service
↓
Repository
↓
MySQL

Supporting components:

Payment Processor
Webhook Service
Settlement Service
Reconciliation Service
Audit Service

## Payment Flow

Client
↓
Create Payment
↓
Idempotency Check
↓
Payment CREATED
↓
Payment PROCESSING
↓
Simulated Processor
├── SUCCESS → Settlement → Reconciliation
├── FAILED
└── TIMEOUT → UNKNOWN → Status Resolution / Retry

## Refund Flow

Payment SUCCESS
↓
Create Refund
↓
Validate Refund Amount
↓
Refund PROCESSING
↓
Refund SUCCESS / FAILED

## Webhook Flow

Payment Status Change
↓
Create Webhook Event
↓
PENDING
↓
PROCESSING
↓
Merchant Endpoint
├── 2xx → DELIVERED
└── Error → FAILED
↓
Retry

Webhook requests contain an HMAC-SHA256 signature for
integrity and authenticity verification.

## Idempotency

Each payment request requires an idempotency key.

The combination of:

merchant_id + idempotency_key

is uniquely constrained at the database level to prevent duplicate
payments during concurrent requests.

## Settlement

For successful payments:

Gross Amount
↓
Gateway Fee
↓
Net Settlement Amount

The simulator currently applies a 2% gateway fee.

## Reconciliation

The reconciliation module compares the expected settlement amount
with the actual amount recorded by the system and marks the record
as MATCHED or MISMATCHED.

## API Documentation

Swagger UI:

http://localhost:9000/swagger-ui.html

OpenAPI specification:

http://localhost:9000/v3/api-docs

## Running the Project

### Requirements

- Java 17
- Maven
- MySQL

### Database

Create:

payment_gateway

Configure database credentials in:

src/main/resources/application.properties

### Run

mvn spring-boot:run

Application:

http://localhost:9000

## Testing

Run:

mvn clean test

The project contains unit tests for core business services including
payment creation, payment processing, refunds and settlement processing.

## Project Structure

src/main/java/com/example/payment

├── controller
├── service
├── repository
├── entity
├── dto
├── enums
├── processor
├── exception
├── config
└── util