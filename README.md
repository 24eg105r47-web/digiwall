# Full-Stack Digital Wallet System

An enterprise-grade, secure digital wallet application featuring a **Spring Boot** backend and a **React** frontend. It implements robust financial architecture, including a Double-Entry Ledger, Idempotent transactions to prevent double-charging, and JWT Authentication.

---

### Individual Contribution Breakdown

| Contributor | Role | Core Deliverables & Responsibilities |
| :--- | :--- | :--- |
| **Mansi** | Lead Full-Stack Architect | • Architected Double-Entry Bookkeeping Ledger System <br> • Implemented Redis Header-based Idempotency Interceptors <br> • Designed Pessimistic Concurrency Locking logic in Spring Boot <br> • Developed Interactive React Dashboard with Tailwind & Framer Motion <br> • Handled end-to-end Database Schema, API Integration & Debugging |
| **Team Member** | Team Member | • *No code or architectural contributions delivered for this release.* |

> **Note:** All backend business logic, database configurations, state management, and frontend interactive components were individually authored and verified by Mansi.

---

## 🌟 Key Features

- **Double-Entry Ledger**: Implemented inside `WalletService` using transactional SQL locking to guarantee that money is never created or destroyed, only credited and debited.
- **Dashboard Ledger Aggregation**: "Total Sent" and "Total Received" statistics are calculated on the backend via PostgreSQL JPQL queries for 100% accuracy.
- **Idempotency (Double-Charge Prevention)**: The backend physically rejects duplicate transfer attempts caused by network drops by validating a unique `Idempotency-Key` at the PostgreSQL schema level.
- **Role-Based Authentication**: Secure JWT-based auth separating standard Users from Admins.
- **Modern FinTech Dashboard**: A premium, CRED/Revolut inspired dark-mode UI with glassmorphism, dynamic Framer Motion micro-interactions, and real-time balance charts using `Recharts`.
- **Digital Receipt Modal**: Verifiable transaction receipts featuring simulated QR codes, Blockchain Hash IDs, and a PDF download option.
- **Split Bill Widget**: Interactive dashboard tool to calculate bill splits instantly and generate secure request links.
- **Security Badges**: Real-time visual trust indicators emphasizing high-security idempotency protection during transfers.
- **Pessimistic Locking**: Prevents race conditions and deadlocks when executing concurrent transfers between wallets.

---

## 🛠 Tech Stack

**Backend**
- Java 17 / Spring Boot 3
- Spring Security (JWT)
- Spring Data JPA
- PostgreSQL
- Maven

**Frontend**
- React 18
- Vite
- Tailwind CSS (FinTech Design System & Glassmorphism)
- Framer Motion (Micro-interactions & Animations)
- Lucide React (Iconography)
- Recharts (Data Visualization)
- React Router DOM
- Axios

---

## 📂 Project Structure

```text
wallet-system/
├── frontend/             # React Application
│   ├── src/
│   │   ├── api/          # Axios instance & interceptors
│   │   ├── components/   # Reusable UI components
│   │   ├── context/      # React Context (Auth)
│   │   ├── pages/        # Dashboard, Wallet, Transfer, History
│   │   └── styles/       # CSS Design System
│   └── package.json
│
├── backend/              # Spring Boot Backend
│   ├── src/
│   │   └── main/java/com/mansi/wallet_system/
│   │       ├── controller/   # REST Endpoints
│   │       ├── service/      # Business Logic (Ledger, Transfers)
│   │       ├── repository/   # JPA Repositories
│   │       ├── entity/       # Database Models
│   │       └── security/     # JWT Filters & Config
│   ├── pom.xml           # Maven Dependencies
│   └── Dockerfile        # Backend Container
│
└── docker-compose.yml    # Infrastructure
```

---

## 🚀 Getting Started

### 1. Start the Database
Ensure PostgreSQL is running locally on port `5432` with a database named `wallet_system`.
*(Or use Docker)*
```bash
docker compose up -d postgres
```

### 2. Start the Backend (Spring Boot)
Open a terminal in the root directory, then navigate to the backend:
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```
*The API will be available at `http://localhost:8082`*

### 3. Start the Frontend (React)
Open a *new* terminal and navigate to the frontend directory:
```bash
cd frontend
npm install
npm run dev
```
*The UI will be available at `http://localhost:5173`*

---

## 📚 API Documentation (Swagger)

Once the backend is running, you can explore and test the API endpoints using the interactive Swagger UI:
👉 **[http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)**

---

## 🔒 Security & Architecture Notes

- **Password Hashing**: User passwords are encrypted using BCrypt before hitting the database.
- **Idempotency Keys**: For every transfer, the React frontend generates a `crypto.randomUUID()`. The Spring Boot backend checks the `transactions` table (which has a `@Column(unique=true)` constraint on `idempotency_key`) to ensure that a retried network request never results in a double deduction.
- **ACID Compliance**: All monetary operations in `WalletService` are annotated with `@Transactional`. If any part of the double-entry process fails, the entire transaction rolls back instantly.

---

## 👤 Author

**Backend Developer & Lead:** Mansi Gupta