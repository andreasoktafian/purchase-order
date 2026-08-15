*Read this in other languages: [English](README.md), [Indonesian](README-id.md)*

# Purchase Order Management Service

A robust, RESTful Spring Boot microservice designed to manage Items, Users, and Purchase Order transactions. This project demonstrates enterprise-level backend practices including database migrations, automated auditing, N+1 query prevention, and robust validation.

## Tech Stack

*   **Java 21** (Virtual Threads enabled)
*   **Spring Boot** (Web, Data JPA, Validation)
*   **MySQL**
*   **Lombok** (Boilerplate reduction)
*   **Maven**

## Architectural Assumptions & Design Decisions

As a downstream microservice, several architectural decisions were made to ensure scalability, loose coupling, and readiness for a distributed environment:

1.  **API Gateway Offloading (Authentication):**
    This service assumes that JWT extraction and validation are handled centrally by an API Gateway. The service expects user identity to be forwarded via HTTP Headers (intercepted and mapped into `AppRequestContext`). This keeps the service stateless and focused entirely on business logic.
2.  **Loose Coupling (No Hard Foreign Keys to Users):**
    The `users` table acts as a loosely coupled entity. Audit fields (`created_by`, `updated_by`) store string-based identifiers rather than hard physical Foreign Keys. This prevents data loss in PO transactions if a user is hard-deleted from an external IAM (Identity and Access Management) service, maintaining a reliable audit trail.
3.  **Pagination Stability:**
    Utilized Spring Data's `PagedModel` configuration (`@EnableSpringDataWebSupport`) to ensure JSON structure stability for pagination responses, preventing breaking changes on the client side during framework upgrades.

## Key Features & Technical Highlights

*   **Smart Update (PATCH/PUT behavior):** Entity updates intelligently ignore `null` or empty values, allowing partial updates without overwriting existing data.
*   **Cross-Field Validation:** Implemented custom `@AssertTrue` validations at the DTO layer to enforce business rules (e.g., preventing an Item's `price` from being set lower than its `cost`).
*   **N+1 Query Prevention:** Utilized `LEFT JOIN FETCH` in JPA Repositories for endpoints retrieving Purchase Order details, reducing database roundtrips from *O(N)* to *O(1)*.
*   **Automated Auditing:** Integrated `@EnableJpaAuditing` with custom entity listeners to automatically populate `created_datetime` and `updated_datetime`.
*   **Defensive Programming:** Applied `@NonNull` and Jakarta Validation (`@NotBlank`, `@Min`) rigorously across Service and Controller layers to ensure data integrity before database execution.

## Getting Started

### Prerequisites
*   Java 21 JDK installed.
*   MySQL Server running on default port `3306`.
*   Maven installed (or use the provided wrapper).

### 1. Database Setup
Create an empty database in your MySQL server:
```sql
CREATE DATABASE andreas_oktafian;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(20),
    created_by VARCHAR(50),
    created_datetime DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_datetime DATETIME NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price INT NOT NULL,
    cost INT NOT NULL,
    created_by VARCHAR(50),
    created_datetime DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_datetime DATETIME NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE po_h (
    id INT AUTO_INCREMENT PRIMARY KEY,
    datetime DATETIME,
    description VARCHAR(500),
    total_price INT,
    total_cost INT,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_datetime DATETIME,
    updated_datetime DATETIME
);

CREATE TABLE po_d (
    id INT AUTO_INCREMENT PRIMARY KEY,
    poh_id INT,
    item_id INT,
    item_qty INT,
    item_cost INT,
    item_price INT,
    FOREIGN KEY (poh_id) REFERENCES po_h(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES item(id)
);
```

### 2. Insert Data
Insert dummy data:
```sql
INSERT INTO Users (first_name, last_name, email, phone, created_by, created_datetime, updated_datetime)
VALUES
    ('Siti', 'Rahma', 'siti.rahma@example.com', '08111222333', 'system', NOW(), NOW()),
    ('Joko', 'Widodo', 'joko.widodo@example.com', '08122334455', 'system', NOW(), NOW()),
    ('Dewi', 'Lestari', 'dewi.lestari@example.com', '08133445566', 'system', NOW(), NOW()),
    ('Eko', 'Prasetyo', 'eko.prasetyo@example.com', '08144556677', 'system', NOW(), NOW()),
    ('Rina', 'Marlina', 'rina.marlina@example.com', '08155667788', 'system', NOW(), NOW()),
    ('Ahmad', 'Fauzi', 'ahmad.fauzi@example.com', '08166778899', 'system', NOW(), NOW()),
    ('Dian', 'Sastro', 'dian.sastro@example.com', '08177889900', 'system', NOW(), NOW()),
    ('Reza', 'Rahadian', 'reza.rahadian@example.com', '08188990011', 'system', NOW(), NOW()),
    ('Putri', 'Ariani', 'putri.ariani@example.com', '08199001122', 'system', NOW(), NOW()),
    ('Rizky', 'Febian', 'rizky.febian@example.com', '08100112233', 'system', NOW(), NOW()),
    ('Angga', 'Yunanda', 'angga.yunanda@example.com', '08211223344', 'system', NOW(), NOW()),
    ('Tasya', 'Kamila', 'tasya.kamila@example.com', '08222334455', 'system', NOW(), NOW()),
    ('Raffi', 'Ahmad', 'raffi.ahmad@example.com', '08233445566', 'system', NOW(), NOW()),
    ('Nagita', 'Slavina', 'nagita.slavina@example.com', '08244556677', 'system', NOW(), NOW()),
    ('Deddy', 'Corbuzier', 'deddy.corbuzier@example.com', '08255667788', 'system', NOW(), NOW()),
    ('Najwa', 'Shihab', 'najwa.shihab@example.com', '08266778899', 'system', NOW(), NOW()),
    ('Raditya', 'Dika', 'raditya.dika@example.com', '08277889900', 'system', NOW(), NOW()),
    ('Maudy', 'Ayunda', 'maudy.ayunda@example.com', '08288990011', 'system', NOW(), NOW()),
    ('Jerome', 'Polin', 'jerome.polin@example.com', '08299001122', 'system', NOW(), NOW()),
    ('Jessica', 'Jane', 'jessica.jane@example.com', '08200112233', 'system', NOW(), NOW());


INSERT INTO Item (name, description, price, cost, created_by, created_datetime, updated_datetime)
VALUES
    ('Dell UltraSharp 27', 'Monitor 4K USB-C', 7500000, 6000000, 'system', NOW(), NOW()),
    ('Sony WH-1000XM5', 'Wireless Noise Canceling Headphones', 5500000, 4200000, 'system', NOW(), NOW()),
    ('Anker PowerCore 24K', 'Power Bank 140W', 2200000, 1600000, 'system', NOW(), NOW()),
    ('Samsung T7 Shield 1TB', 'Portable SSD External', 1800000, 1300000, 'system', NOW(), NOW()),
    ('Razer DeathAdder V3', 'Gaming Mouse', 950000, 700000, 'system', NOW(), NOW()),
    ('HyperX Cloud III', 'Gaming Headset', 1300000, 950000, 'system', NOW(), NOW()),
    ('Elgato Stream Deck MK.2', 'Control Pad for Content Creators', 2800000, 2100000, 'system', NOW(), NOW()),
    ('Apple iPad Air M2', 'Tablet Apple 11 inch', 10500000, 9000000, 'system', NOW(), NOW()),
    ('iPhone 15 Pro 128GB', 'Smartphone Apple', 18500000, 16000000, 'system', NOW(), NOW()),
    ('Asus ROG Ally', 'Handheld Gaming Console', 11000000, 9500000, 'system', NOW(), NOW()),
    ('Kindle Paperwhite 11th Gen', 'E-Reader E-Ink Display', 2400000, 1800000, 'system', NOW(), NOW()),
    ('DJI Osmo Pocket 3', 'Vlogging Camera 4K', 8500000, 7000000, 'system', NOW(), NOW()),
    ('Smart Desk Lamp RGB', 'Smart Desk Lamp LED', 450000, 300000, 'system', NOW(), NOW()),
    ('Ugreen 100W GaN Charger', 'Multiport Fast Charger', 750000, 500000, 'system', NOW(), NOW()),
    ('SanDisk Extreme 128GB', 'MicroSD Card V30', 350000, 220000, 'system', NOW(), NOW()),
    ('Lian Li O11 Dynamic', 'PC Gaming Case', 2100000, 1500000, 'system', NOW(), NOW()),
    ('Corsair RM850x', 'Power Supply Unit 850W 80+ Gold', 2300000, 1750000, 'system', NOW(), NOW()),
    ('NZXT Kraken 240', 'CPU Liquid Cooler', 2600000, 1900000, 'system', NOW(), NOW()),
    ('Bose SoundLink Flex', 'Portable Bluetooth Speaker', 2500000, 1900000, 'system', NOW(), NOW()),
    ('TP-Link Archer AX55', 'Wi-Fi 6 Router', 1250000, 900000, 'system', NOW(), NOW());
```

### 3. Configuration & Run
Before starting, please review the `src/main/resources/application-local.yaml` file and update the database credentials (`username` and `password`) to match your local MySQL setup. Once configured, run the application using Maven with the `local` profile active:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```