# 📦 Purchase Order Service

![Java](https://img.shields.io/badge/Java-21-orange.svg) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.0-brightgreen.svg) ![MySQL](https://img.shields.io/badge/MySQL-Database-blue.svg)

A robust and scalable backend service for managing Users, Items, and Purchase Orders. Built with modern Java standards, this service incorporates best practices in transaction management, centralized error handling, business event logging, and observability.

---

## ✨ Key Features & Advantages
* **Modern Threading Model:** Fully configured to utilize **Java Virtual Threads** (`spring.threads.virtual.enabled=true`) for high-throughput and lightweight concurrency.
* **Aspect-Oriented Programming (AOP):** Implements `@LogBusinessEvent` to automatically intercept, trace, and measure the execution time of core business logic without polluting the service layer.
* **Traceability & MDC:** Includes a custom `PurchaseOrderFilter` that automatically handles `X-Correlation-ID` and `X-User-ID` headers, injecting them into the Mapped Diagnostic Context (MDC) for seamless log tracing.
* **Global Exception Handling:** A centralized `@RestControllerAdvice` that translates exceptions (e.g., `ResourceNotFoundException`, `ConflictException`, `MethodArgumentNotValidException`) into standardized, predictable JSON error responses.
* **JPA Auditing:** Automatically manages `created_datetime`, `updated_datetime`, `created_by`, and `updated_by` fields using `@EnableJpaAuditing` and custom `AppRequestContext` resolution.
* **Smart Data Trimming:** Configured with custom Jackson and WebDataBinder modules to automatically trim whitespaces from incoming string payloads.

## 🛠 Specifications
* **Language:** Java 21
* **Framework:** Spring Boot 4.1.0 (Web, Data JPA, Validation)
* **Database:** MySQL
* **Build Tool:** Maven

## ⚙️ Configuration & Profiles

This application uses Spring Profiles to separate environments.

### 1. Local Profile (`local`)
* Runs on port `8090`.
* Exposes SQL queries in the console (`show-sql: true`).
* Requires a local MySQL database named `andreas_oktafian`.
* **To run locally in IntelliJ:** Set `-Dspring.profiles.active=local` in your VM Options, or set the `SPRING_PROFILES_ACTIVE=local` environment variable.

### 2. Production Profile (`prod`)
* Hides SQL queries (`show-sql: false`) and disables auto DDL (`ddl-auto: none`).
* Secures credentials using environment variables: `${DATABASE_URL}`, `${USERNAME}`, and `${PASSWORD}`.

## 🚀 How to Run

1. Clone the repository.
2. Create a MySQL database named `andreas_oktafian`.
3. Manually run the provided SQL scripts from the resources folder to create the tables and insert initial data.
4. Run the application:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local

### 👤 USERS API (`/api/users`)

#### Payload Field Descriptions:
| Field | Type | Required | Rules & Description |
| :--- | :--- | :--- | :--- |
| `first_name` | String | **Yes** | Cannot be blank. User's primary name. |
| `last_name` | String | No | User's family/surname. |
| `email` | String | No | Must be a valid email format. Must be unique across users. |
| `phone` | String | No | Contact number. Must be unique across users. |

#### ➤ GET All Users
* **Method & Endpoint:** `GET /api/users?page=1&size=10`
* **Response (200 OK):**
  ```json
  {
    "meta": {
      "success": true,
      "code": 200,
      "correlation_id": "e3b1aeb1-69c0-4ef4-9859-52254ead7d2d",
      "message": "All users retrieved successfully"
    },
    "data": {
      "content": [
        {
          "id": 10001,
          "first_name": "John",
          "last_name": "Doe",
          "email": "john.doe@example.com",
          "phone": "08123456789",
          "created_by": "admin",
          "updated_by": null,
          "created_datetime": "2026-08-15T12:00:00",
          "updated_datetime": "2026-08-15T12:00:00"
        }
      ],
      "page": { "size": 10, "number": 1, "total_elements": 1, "total_pages": 1 }
    }
  }
  ```

#### ➤ GET User by ID
* **Method & Endpoint:** `GET /api/users/10001` (or `GET /api/users?id=10001`)
* **Response (200 OK):**
  ```json
  {
    "meta": { 
      "success": true, 
      "code": 200, 
      "correlation_id": "e3b1aeb1-69c0-4ef4-9859-52254ead7d2d", 
      "message": "User retrieved successfully" 
    },
    "data": {
      "id": 10001,
      "first_name": "John",
      "last_name": "Doe",
      "email": "john.doe@example.com",
      "phone": "08123456789",
      "created_by": "admin",
      "updated_by": null,
      "created_datetime": "2026-08-15T12:00:00",
      "updated_datetime": "2026-08-15T12:00:00"
    }
  }
  ```

#### ➤ POST (Create User)
* **Method & Endpoint:** `POST /api/users`
* **Request Payload:**
  ```json
  {
    "first_name": "Andreas",
    "last_name": "Oktafian 2",
    "email": "andreas.oktafiann3@example.com",
    "phone": "081112223501"
  }
  ```
* **Response (201 Created):**
  ```json
  {
    "meta": { 
      "success": true, 
      "code": 201, 
      "correlation_id": "e3b1aeb1-69c0-4ef4-9859-52254ead7d2d", 
      "message": "User created successfully" 
    },
    "data": {
      "id": 1854272225,
      "first_name": "Andreas",
      "last_name": "Oktafian 2",
      "email": "andreas.oktafiann3@example.com",
      "phone": "081112223501",
      "created_by": "1048401782",
      "updated_by": null,
      "created_datetime": "2026-08-15T12:42:53.1429079",
      "updated_datetime": "2026-08-15T12:42:53.1429079"
    }
  }
  ```

#### ➤ PUT (Update User)
* **Method & Endpoint:** `PUT /api/users/10001`
* **Request Payload:**
  ```json
  {
    "first_name": "Johnny",
    "last_name": "Doe",
    "email": "johnny.updated@example.com",
    "phone": "08987654321"
  }
  ```
* **Response (200 OK):**
  ```json
  {
    "meta": { 
      "success": true, 
      "code": 200, 
      "correlation_id": "e3b1aeb1-69c0-4ef4-9859-52254ead7d2d", 
      "message": "User updated successfully" 
    },
    "data": {
      "id": 10001,
      "first_name": "Johnny",
      "last_name": "Doe",
      "email": "johnny.updated@example.com",
      "phone": "08987654321",
      "created_by": "1048401782",
      "updated_by": "1048401782",
      "created_datetime": "2026-08-15T12:00:00",
      "updated_datetime": "2026-08-15T12:05:00"
    }
  }
  ```

#### ➤ DELETE User
* **Method & Endpoint:** `DELETE /api/users/10001`
* **Request Payload:** None
* **Response (200 OK):**
  ```json
  {
    "meta": { 
      "success": true, 
      "code": 200, 
      "correlation_id": "e3b1aeb1-69c0-4ef4-9859-52254ead7d2d", 
      "message": "User deleted successfully" 
    }
  }
  ````

### 📦 2. ITEMS API (`/api/items`)

#### Payload Field Descriptions:
| Field | Type | Required | Rules & Description |
| :--- | :--- | :--- | :--- |
| `name` | String | **Yes** *(on create)* | Cannot be blank. Must be unique across items. |
| `description` | String | No | Text description of the product/item. |
| `price` | Integer | **Yes** *(on create)* | Selling price. Must be $\ge 0$ and must be $\ge$ `cost`. |
| `cost` | Integer | **Yes** *(on create)* | Purchase/production cost. Must be $\ge 0$. |

#### ➤ GET All Items
* **Method & Endpoint:** `GET /api/items?page=1&size=10`
* **Response (200 OK):**
  ```json
  {
    "meta": {
      "success": true,
      "code": 200,
      "correlation_id": "e3b1aeb1-69c0-4ef4-9859-52254ead7d2d",
      "message": "All items retrieved successfully"
    },
    "data": {
      "content": [
        {
          "id": 1000,
          "name": "Mechanical Keyboard",
          "description": "RGB Mechanical Keyboard 75%",
          "price": 1500000,
          "cost": 1000000,
          "created_by": "admin",
          "updated_by": null,
          "created_datetime": "2026-08-15T12:00:00",
          "updated_datetime": "2026-08-15T12:00:00"
        }
      ],
      "page": { "size": 10, "number": 1, "total_elements": 1, "total_pages": 1 }
    }
  }
  ```

#### ➤ GET Item by ID
* **Method & Endpoint:** `GET /api/items/1000` (or `GET /api/items?id=1000`)
* **Response (200 OK):**
  ```json
  {
    "meta": { 
      "success": true, 
      "code": 200, 
      "correlation_id": "e3b1aeb1-69c0-4ef4-9859-52254ead7d2d", 
      "message": "Item retrieved successfully" 
    },
    "data": {
      "id": 1000,
      "name": "Mechanical Keyboard",
      "description": "RGB Mechanical Keyboard 75%",
      "price": 1500000,
      "cost": 1000000,
      "created_by": "admin",
      "updated_by": null,
      "created_datetime": "2026-08-15T12:00:00",
      "updated_datetime": "2026-08-15T12:00:00"
    }
  }
  ```

#### ➤ POST (Create Item)
* **Method & Endpoint:** `POST /api/items`
* **Request Payload:**
  ```json
  {
    "name": "Mechanical Keyboard",
    "description": "RGB Mechanical Keyboard 75%",
    "price": 1500000,
    "cost": 1000000
  }
  ```
* **Response (201 Created):**
  ```json
  {
    "meta": { 
      "success": true, 
      "code": 201, 
      "correlation_id": "e3b1aeb1-69c0-4ef4-9859-52254ead7d2d", 
      "message": "Item created successfully" 
    },
    "data": {
      "id": 1000,
      "name": "Mechanical Keyboard",
      "description": "RGB Mechanical Keyboard 75%",
      "price": 1500000,
      "cost": 1000000,
      "created_by": "1048401782",
      "updated_by": null,
      "created_datetime": "2026-08-15T12:00:00",
      "updated_datetime": "2026-08-15T12:00:00"
    }
  }
  ```

#### ➤ PUT (Update Item)
* **Method & Endpoint:** `PUT /api/items/1000`
* **Request Payload:**
  ```json
  {
    "name": "Mechanical Keyboard Pro",
    "description": "Upgraded Version",
    "price": 1700000,
    "cost": 1200000
  }
  ```
* **Response (200 OK):**
  ```json
  {
    "meta": { 
      "success": true, 
      "code": 200, 
      "correlation_id": "e3b1aeb1-69c0-4ef4-9859-52254ead7d2d", 
      "message": "Item updated successfully" 
    },
    "data": {
      "id": 1000,
      "name": "Mechanical Keyboard Pro",
      "description": "Upgraded Version",
      "price": 1700000,
      "cost": 1200000,
      "created_by": "1048401782",
      "updated_by": "1048401782",
      "created_datetime": "2026-08-15T12:00:00",
      "updated_datetime": "2026-08-15T12:05:00"
    }
  }
  ```

#### ➤ DELETE Item
* **Method & Endpoint:** `DELETE /api/items/1000`
* **Request Payload:** None
* **Response (200 OK):**
  ```json
  {
    "meta": { 
      "success": true, 
      "code": 200, 
      "correlation_id": "e3b1aeb1-69c0-4ef4-9859-52254ead7d2d", 
      "message": "Item deleted successfully" 
    }
  }
  ```
* *Constraint Notice:* Returns `409 Conflict` if the item is currently referenced in an existing Purchase Order.

### 🛒 3. PURCHASE ORDERS API (`/api/purchase-orders`)

#### Payload Field Descriptions:
| Field | Type | Required | Rules & Description |
| :--- | :--- | :--- | :--- |
| `description` | String | No | Summary or note for the Purchase Order header. |
| `details` | Array | **Yes** | Must contain at least 1 detail item (`@NotEmpty`). |
| `details[].item_id` | Integer | **Yes** | Existing Item ID in the database (`@NotNull`). |
| `details[].item_qty` | Integer | **Yes** | Purchased quantity. Must be $> 0$ (`@Min(1)`). |

#### Server-Calculated Fields (Response):
* `total_price`: Sum of `(item_price * item_qty)` for all items in the details.
* `total_cost`: Sum of `(item_cost * item_qty)` for all items in the details.
* `details[].item_price`: Unit price snapshot captured from Item master data at transaction time.
* `details[].item_cost`: Unit cost snapshot captured from Item master data at transaction time.

---

#### ➤ GET All Purchase Orders
* **Method & Endpoint:** `GET /api/purchase-orders?page=1&size=10`
* **Response (200 OK):**
  ```json
  {
    "meta": {
      "success": true,
      "code": 200,
      "correlation_id": "e3b1aeb1-69c0-4ef4-9859-52254ead7d2d",
      "message": "All purchase orders retrieved successfully"
    },
    "data": {
      "content": [
        {
          "id": 1,
          "datetime": "2026-08-15T12:00:00",
          "description": "Office Supplies Restock",
          "total_price": 7500000,
          "total_cost": 5000000,
          "created_by": "1048401782",
          "updated_by": null,
          "created_datetime": "2026-08-15T12:00:00",
          "updated_datetime": "2026-08-15T12:00:00"
        }
      ],
      "page": { "size": 10, "number": 1, "total_elements": 1, "total_pages": 1 }
    }
  }
  ```

#### ➤ GET Purchase Order by ID
* **Method & Endpoint:** `GET /api/purchase-orders/1` (or `GET /api/purchase-orders?id=1`)
* **Response (200 OK):**
  ```json
  {
    "meta": { 
      "success": true, 
      "code": 200, 
      "correlation_id": "e3b1aeb1-69c0-4ef4-9859-52254ead7d2d", 
      "message": "Purchase order detail retrieved successfully" 
    },
    "data": {
      "header": {
        "id": 1,
        "datetime": "2026-08-15T12:00:00",
        "description": "Office Supplies Restock",
        "total_price": 7500000,
        "total_cost": 5000000,
        "created_by": "1048401782",
        "updated_by": null,
        "created_datetime": "2026-08-15T12:00:00",
        "updated_datetime": "2026-08-15T12:00:00"
      },
      "details": [
        {
          "id": 1,
          "item_id": 1000,
          "item_qty": 5,
          "item_price": 1500000,
          "item_cost": 1000000
        }
      ]
    }
  }
  ```

#### ➤ POST (Create Purchase Order)
* **Method & Endpoint:** `POST /api/purchase-orders`
* **Request Payload:**
  ```json
  {
    "description": "Office Supplies Restock",
    "details": [
      {
        "item_id": 1000,
        "item_qty": 5
      }
    ]
  }
  ```
* **Response (201 Created):**
  ```json
  {
    "meta": { 
      "success": true, 
      "code": 201, 
      "correlation_id": "e3b1aeb1-69c0-4ef4-9859-52254ead7d2d", 
      "message": "Purchase order created successfully" 
    },
    "data": {
      "header": {
        "id": 1,
        "datetime": "2026-08-15T12:00:00",
        "description": "Office Supplies Restock",
        "total_price": 7500000,
        "total_cost": 5000000,
        "created_by": "1048401782",
        "updated_by": null,
        "created_datetime": "2026-08-15T12:00:00",
        "updated_datetime": "2026-08-15T12:00:00"
      },
      "details": [
        {
          "id": 1,
          "item_id": 1000,
          "item_qty": 5,
          "item_price": 1500000,
          "item_cost": 1000000
        }
      ]
    }
  }
  ```

#### ➤ PUT (Update Purchase Order)
* **Method & Endpoint:** `PUT /api/purchase-orders/1`
* **Request Payload:**
  ```json
  {
    "description": "Office Supplies Restock (Revised)",
    "details": [
      {
        "item_id": 1000,
        "item_qty": 10
      }
    ]
  }
  ```
* **Response (200 OK):**
  ```json
  {
    "meta": { 
      "success": true, 
      "code": 200, 
      "correlation_id": "e3b1aeb1-69c0-4ef4-9859-52254ead7d2d", 
      "message": "Purchase Order updated successfully" 
    },
    "data": {
      "header": {
        "id": 1,
        "datetime": "2026-08-15T12:00:00",
        "description": "Office Supplies Restock (Revised)",
        "total_price": 15000000,
        "total_cost": 10000000,
        "created_by": "1048401782",
        "updated_by": "1048401782",
        "created_datetime": "2026-08-15T12:00:00",
        "updated_datetime": "2026-08-15T12:05:00"
      },
      "details": [
        {
          "id": 2,
          "item_id": 1000,
          "item_qty": 10,
          "item_price": 1500000,
          "item_cost": 1000000
        }
      ]
    }
  }
  ```
* *Execution Note:* Updating a purchase order automatically clears existing detail rows (`header.getDetails().clear()`) and processes the new incoming detail list.

#### ➤ DELETE Purchase Order
* **Method & Endpoint:** `DELETE /api/purchase-orders/1`
* **Request Payload:** None
* **Response (200 OK):**
  ```json
  {
    "meta": { 
      "success": true, 
      "code": 200, 
      "correlation_id": "e3b1aeb1-69c0-4ef4-9859-52254ead7d2d", 
      "message": "Purchase Order deleted successfully" 
    }
  }
  ```