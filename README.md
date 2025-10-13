# Courier Tracking Service

A clean, event-driven **Spring Boot** service that tracks couriers, their locations, and store entrance events with Redis-backed de‑duplication and per‑courier distributed locks.

---

## ⚙️ Tech Stack

- **Java 17**, **Maven**
- **Spring Boot 3.5**: Web, Validation, Data JPA, Cache, Actuator
- **PostgreSQL** + **Flyway** (migrations)
- **Redis (Lettuce)** + **Redisson** (distributed lock)
- **Lombok** 
- **JUnit 5**, **Mockito**, **MockMvc**
- Optional Observability: **Micrometer Prometheus**, **Prometheus**, **Grafana**

> Notes
> - All server-side datetimes are standardized to **UTC**.
> - DB schema is managed by **Flyway**. Hibernate `ddl-auto` is disabled outside tests.

---

## 🧩 Architecture Overview

- **Controllers**
  - `CourierWriteController` — create courier
  - `CourierQueryController` — paginated reads
  - `CourierLocationController` — **write-only** endpoint that accepts location events (202 Accepted)
  - `StoreController` — paginated store listing
  - `StoreEntranceReadController` — paginated store entrance listing with spec
- **Event-driven Location Flow**
  1. Client POSTs a courier location → a **domain event** is published.
  2. `LocationEventHandler` (async) processes the event:
     - Appends a row into `courier_location` with `event_time` and (optional) `segment_meters`.
     - Computes incremental distance using **Haversine**.
     - Tries to create a `store_entrance` for the **nearest store** within configurable radius.
     - Prevents duplicates:
       - **Dedup TTL key** per `{courier, store}` in Redis.
       - **Client-time window** per `{courier, store}` (no TTL) to reject near-duplicate timestamps.
     - Caches **last location** in Redis (ephemeral).
     - Guards the whole process with **per-courier distributed lock** (Redisson).
- **Query side**
  - Paged lists and a **courier detail** view: total meters traveled + visited stores (with counts & last entrance time) + raw entrance DTOs.

---

## 🗄️ Database Schema (high level)

- `store (id, name, latitude, longitude, audit)`
- `store_entrance (store_id FK, courier_id, entrance_time, distance_meters, audit)`
- `courier (identity_number UNIQUE, phone_number UNIQUE, name, surname, audit)`
- `courier_location (courier_id, event_time, lat, lng, segment_meters)`

> Recommended constraints
> - `UNIQUE (courier_id, event_time)` on `courier_location` for idempotence.
> - Index `(courier_id, entrance_time desc)` on `store_entrance` for history queries.

---

## 🔧 Configuration

### Profile: **local** (`application.yml`)
- Postgres: `jdbc:postgresql://localhost:5432/courier_tracking`
- Redis: `localhost:6379`
- Flyway: enabled, schema `couriertracking`
- JPA: `ddl-auto=none`, default schema `couriertracking`, JDBC TZ `UTC`

### Profile: **docker** (`application-docker.yml`)
- Postgres host **`postgres`**, Redis host **`redis`** (docker service names)
- Same Flyway/JPA/UTC settings

### Profile: **test** (`application-test.yml`)
- **H2 (PostgreSQL mode)** in‑memory DB, `ddl-auto=create-drop`
- Flyway disabled in tests

---

## ▶️ Running

### Option A — Local (requires local Postgres & Redis)
1. Create database `courier_tracking`, ensure schema `couriertracking` exists or let Flyway create it.
2. Start Redis (`redis-server` on 6379).
3. Run:
   ```bash
   mvn clean package
   java -jar target/courier-tracking-*.jar
   ```
   or
   ```bash
   mvn spring-boot:run
   ```

### Option B — Docker Compose (recommended)
1. Make sure `docker-compose.yml` is present (includes Postgres, Redis, App).
2. Run:
   ```bash
   docker compose up -d --build
   docker compose logs -f app
   ```
3. App will start with `SPRING_PROFILES_ACTIVE=docker` and use service names `postgres` and `redis`.

> Health check: `http://localhost:8080/actuator/health`

---

## 🌐 REST Endpoints (short list)

### Stores
- `GET /api/v1/store?page=0&size=20` → **paged** store list

### Couriers — write
- `POST /api/v1/couriers`
  ```json
  {
    "identityNumber": "12345678901",
    "phoneNumber": "+905554443322",
    "name": "Ali",
    "surname": "Veli"
  }
  ```

### Couriers — read
- `GET /api/v1/couriers?page=0&size=20`
- `GET /api/v1/couriers/{id}`
- `GET /api/v1/couriers/{id}/detail` (total meters + visited stores + entrances)

### Locations (event-driven)
- `POST /api/v1/locations` (returns **202 Accepted**)
  ```json
  {
    "courierId": 1,
    "lat": 41.0151,
    "lng": 28.9795,
    "time": "2025-10-13T09:05:00"
  }
  ```
  A Postman collection is included at the repository root (CourierTracking.postman_collection.json). You can import it into Postman for quick exploration of the API.

---

## ✅ Validation & Errors

- Custom **TCKN** validator for `identityNumber`.
- Turkish phone format: `+90` followed by 10 digits (e.g. `+905554443322`).
- Global exception handler maps:
  - Validation errors → **400**
  - Not found → **404**
  - Duplicate resource (unique key violation) → **409**

---

## 📊 Observability (optional but nice for demo)

### Actuator & Prometheus
- Enable Prometheus endpoint:
  ```yaml
  management:
    endpoints.web.exposure.include: health,info,prometheus
    metrics.tags.application: courier-tracking
  ```
- Scrape target: `http://app:8080/actuator/prometheus` (Docker) or `http://localhost:8080/actuator/prometheus` (local)

### Compose add-ons
- **Prometheus** (`prom/prometheus`) + **Grafana** (`grafana/grafana`) services can be added to `docker-compose.yml`.
- In Grafana, add Prometheus data source `http://prometheus:9090` and import a Spring/Micrometer dashboard.
- http://localhost:3000 for Grafana
- http://localhost:9090 for Prometheus

---

## 🧪 Tests

- Unit & slice tests with **JUnit 5/Mockito/MockMvc**.
- Run all:
  ```bash
  mvn test
  ```
- Run a specific test class or method:
  ```bash
  mvn -Dtest=StoreControllerTest test
  mvn -Dtest=StoreControllerTest#list_returnsPagedStores_wrappedInApiResult test
  ```
- Building without tests (for quick Docker image builds):
  ```bash
  mvn -DskipTests package
  ```

---

## 🔐 Idempotency & Concurrency

- **Per-courier distributed lock** via Redisson during location event handling.
- **Dedup TTL**: Redis key per `{courier, store}` (e.g., 60s).
- **Client-time window**: last accepted client timestamp per `{courier, store}` (no TTL) to reject near-duplicate events arriving late.
- DB uniqueness on `courier_location (courier_id, event_time)` for strong idempotence.