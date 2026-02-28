# 💰 Mini Expense Manager

A production-style full-stack application to track daily expenses with automatic categorization and anomaly detection.

---

## 🛠 Tech Stack

| Layer      | Technology                              |
|------------|------------------------------------------|
| Frontend   | React 18, TypeScript, Axios, React Router |
| Backend    | Java 17, Spring Boot 3.2, Spring Data JPA |
| Database   | PostgreSQL 15                            |
| Build Tool | Maven                                    |
| Libraries  | Lombok, OpenCSV, Bean Validation         |

---

## 🚀 Setup Instructions

### Prerequisites

- Java 17+
- Node.js 18+
- PostgreSQL 15+
- Maven 3.8+

---

### 1. Database Setup

```sql
CREATE DATABASE expense_manager;
```

---

### 2. Backend Setup

```bash
cd backend

# Update credentials in src/main/resources/application.properties if needed:
# spring.datasource.username=postgres
# spring.datasource.password=postgres

mvn clean install
mvn spring-boot:run
```

The backend starts on **http://localhost:8080**.

On startup, the `DataSeeder` automatically seeds 32 vendor-category mappings if the table is empty.

---

### 3. Frontend Setup

```bash
cd frontend
npm install
npm start
```

The frontend starts on **http://localhost:3000** and proxies API calls to `http://localhost:8080`.

---

## 📡 API Reference

### Expenses

| Method | Endpoint                                  | Description              |
|--------|-------------------------------------------|--------------------------|
| POST   | `/api/v1/expenses`                        | Add single expense       |
| POST   | `/api/v1/expenses/upload-csv`             | Upload CSV file          |
| GET    | `/api/v1/expenses/dashboard/monthly-totals` | Monthly totals by category |
| GET    | `/api/v1/expenses/dashboard/top-vendors`  | Top 5 vendors by spend   |
| GET    | `/api/v1/expenses/dashboard/anomalies`    | List all anomalies       |
| GET    | `/api/v1/expenses/dashboard/anomalies/count` | Count of anomalies    |

---

### Sample API Responses

**POST /api/v1/expenses**

Request:
```json
{
  "date": "2024-01-15",
  "amount": 4500.00,
  "vendorName": "Amazon",
  "description": "Office supplies"
}
```

Response (201 Created):
```json
{
  "id": "3f8b1d2a-1234-4abc-b567-89ef01234567",
  "date": "2024-01-15",
  "amount": 4500.00,
  "vendorName": "Amazon",
  "description": "Office supplies",
  "category": "Shopping",
  "isAnomaly": false,
  "createdAt": "2024-01-15T10:30:00"
}
```

**GET /api/v1/expenses/dashboard/monthly-totals**
```json
[
  { "year": 2024, "month": 1, "category": "Food & Dining", "total": 3250.00 },
  { "year": 2024, "month": 1, "category": "Shopping", "total": 12400.00 },
  { "year": 2024, "month": 1, "category": "Transport", "total": 890.00 }
]
```

**GET /api/v1/expenses/dashboard/top-vendors**
```json
[
  { "vendorName": "Amazon", "totalSpend": 15200.00 },
  { "vendorName": "Swiggy", "totalSpend": 8400.00 },
  { "vendorName": "Uber", "totalSpend": 3200.00 },
  { "vendorName": "Netflix", "totalSpend": 1499.00 },
  { "vendorName": "Zomato", "totalSpend": 1200.00 }
]
```

**GET /api/v1/expenses/dashboard/anomalies/count**
```json
{ "count": 3 }
```

**Validation Error (400 Bad Request)**:
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2024-01-15T10:30:00",
  "fieldErrors": {
    "amount": "Amount must be greater than 0",
    "vendorName": "Vendor name is required"
  }
}
```

---

### CSV Upload Format

```
date,amount,vendorName,description
2024-01-15,450.00,Swiggy,Dinner order
2024-01-16,15000.00,Amazon,Laptop accessories
2024-01-17,200.00,Uber,Airport cab
```

- Date format: `yyyy-MM-dd`
- Amount: positive decimal
- Description: optional
- Header row is auto-detected and skipped

---

## 🏗 Architecture & Design Decisions

### Clean Layered Architecture
The backend follows strict layered separation: **Controller → Service → Repository**. Controllers only handle HTTP concerns (request parsing, response codes). All business logic lives in the service layer. Entities are never exposed directly; the `ExpenseMapper` converts between entities and DTOs.

### Strategy Pattern for Categorization
`CategorizationStrategy` is an interface with `RuleBasedCategorizationStrategy` as the default implementation. This makes the system open for extension — a future `AiCategorizationStrategy` can be plugged in without changing the service. The concrete implementation is injected via Spring's DI, adhering to the Dependency Inversion Principle.

### Anomaly Detection as a Separate Service
`AnomalyDetectionService` is decoupled from `ExpenseServiceImpl` by design. It has a single, clearly defined responsibility: determine whether an amount is anomalous for a category. The threshold (3× category average) is computed using a single JPQL aggregate query.

### DTO Pattern
All API inputs/outputs use dedicated DTOs (`ExpenseRequest`, `ExpenseResponse`, etc.), preventing accidental entity exposure and making the API contract explicit and stable independent of the database schema.

### Builder Pattern via Lombok
All DTOs and entities use Lombok's `@Builder` for clean, readable object construction, avoiding telescoping constructors and maintaining immutability at creation time.

### Centralized Exception Handling
`GlobalExceptionHandler` with `@RestControllerAdvice` intercepts all exceptions and returns structured `ErrorResponse` objects with consistent HTTP status codes (400, 404, 500), ensuring no raw stack traces reach the client.

### SOLID Compliance Summary
- **S** – Each class has one reason to change (`AnomalyDetectionService`, `ExpenseMapper`, `DataSeeder` are focused).
- **O** – `CategorizationStrategy` is open for extension, closed for modification.
- **L** – Strategy implementations are substitutable without breaking callers.
- **I** – `CategorizationStrategy` has a single focused method; `ExpenseService` only exposes operations consumers need.
- **D** – `ExpenseServiceImpl` depends on abstractions (`CategorizationStrategy`, `AnomalyDetectionService`), not concretions.

---

## 📋 Assumptions

1. Vendor matching is **case-insensitive** exact match (e.g., "amazon" matches "Amazon").
2. Category average for anomaly detection is computed across **all time** (not windowed by month).
3. First expense in a category is **never** marked anomalous (no baseline to compare against).
4. CSV upload is processed **row by row**; a failed row does not roll back successful rows.
5. The application is single-tenant (no authentication layer).
6. `vendorName` in the CSV is matched against the seed data; unrecognized vendors default to "Others".

---

## 📂 Project Structure

```
mini-expense-manager/
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/expensemanager/
│       ├── ExpenseManagerApplication.java
│       ├── anomaly/
│       │   └── AnomalyDetectionService.java
│       ├── config/
│       │   ├── DataSeeder.java
│       │   └── WebConfig.java
│       ├── controller/
│       │   └── ExpenseController.java
│       ├── dto/
│       │   ├── request/ExpenseRequest.java
│       │   └── response/
│       │       ├── CategoryTotalResponse.java
│       │       ├── CsvUploadResponse.java
│       │       ├── ErrorResponse.java
│       │       ├── ExpenseResponse.java
│       │       └── TopVendorResponse.java
│       ├── entity/
│       │   ├── Expense.java
│       │   └── VendorCategoryMapping.java
│       ├── exception/
│       │   ├── CsvParseException.java
│       │   ├── GlobalExceptionHandler.java
│       │   └── ResourceNotFoundException.java
│       ├── mapper/
│       │   └── ExpenseMapper.java
│       ├── repository/
│       │   ├── ExpenseRepository.java
│       │   └── VendorCategoryMappingRepository.java
│       ├── service/
│       │   ├── ExpenseService.java
│       │   └── impl/ExpenseServiceImpl.java
│       └── strategy/
│           ├── CategorizationStrategy.java
│           └── impl/RuleBasedCategorizationStrategy.java
│
└── frontend/
    ├── package.json
    ├── tsconfig.json
    └── src/
        ├── App.tsx
        ├── index.tsx
        ├── styles.css
        ├── api/
        │   ├── apiClient.ts
        │   └── expenseApi.ts
        ├── components/
        │   └── Navbar.tsx
        ├── hooks/
        │   └── useDashboard.ts
        ├── pages/
        │   ├── AddExpensePage.tsx
        │   ├── CsvUploadPage.tsx
        │   └── DashboardPage.tsx
        └── types/
            └── index.ts
```
# Mini_Expense_Manager
