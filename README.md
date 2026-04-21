# Smart Campus Sensor & Room Management API

A robust RESTful web service built with JAX-RS (Jersey) and Grizzly for managing a university's sensor network and room allocation.

## 🚀 Getting Started

### Prerequisites
- Java 8 or higher
- Apache Maven

### Run with NetBeans (Recommended)
1.  Open NetBeans IDE.
2.  Go to **File > Open Project** and select the `SmartCampusAPI` folder.
3.  Wait for NetBeans to scan the Maven dependencies.
4.  Right-click the project in the **Projects** tab and select **Run**.

### Run with Terminal (Requires Maven)
1. Build the project:
   ```bash
   mvn clean install
   ```
2. Run the server:
   ```bash
   mvn exec:java
   ```
   The API will be available at `http://localhost:8080/api/v1`.

---

## 🛠️ API Interaction Examples

### 1. Discovery Endpoint
```bash
curl -X GET http://localhost:8080/api/v1
```

### 2. Room Management
**List all rooms:**
```bash
curl -X GET http://localhost:8080/api/v1/rooms
```
**Create a new room:**
```bash
curl -X POST -H "Content-Type: application/json" -d '{"id":"LIB-302", "name":"Library Lounge", "capacity":20}' http://localhost:8080/api/v1/rooms -v
```

### 3. Sensor Operations
**Register a sensor to a room:**
```bash
curl -X POST -H "Content-Type: application/json" -d '{"id":"CO2-001", "type":"CO2", "status":"ACTIVE", "roomId":"LIB-301"}' http://localhost:8080/api/v1/sensors
```
**Filter sensors by type:**
```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature"
```

### 4. Deep Nesting (Readings)
**Post a reading for a specific sensor:**
```bash
curl -X POST -H "Content-Type: application/json" -d '{"value":450.0}' http://localhost:8080/api/v1/sensors/CO2-001/readings
```
**Get reading history:**
```bash
curl -X GET http://localhost:8080/api/v1/sensors/CO2-001/readings
```

---

## 📝 Conceptual Report (Reflection Answers)

## 📝 Conceptual Report (Reflection Answers)

### Part 1: Service Architecture & Setup
**Q: Explain the default lifecycle of a JAX-RS Resource class.**
By default, JAX-RS resources are **Request-Scoped (Stateless)**. The JAX-RS runtime (e.g., Jersey) instantiates a new instance of the resource class for every incoming HTTP request and discards it after the response is sent. 
*   **Architectural Impact:** This ensures total thread isolation at the resource level, preventing request leakage. However, it means the API cannot rely on instance variables for state.
*   **Synchronization Strategy:** In our "Smart Campus" system, shared data is delegated to a singleton `InventoryService`. To prevent race conditions and data corruption during concurrent requests (e.g., two sensors posting readings simultaneously), we utilize thread-safe structures like `ConcurrentHashMap` and explicit `synchronized` blocks on shared objects.

**Q: Why is Hypermedia (HATEOAS) a hallmark of advanced RESTful design?**
HATEOAS (Hypermedia as the Engine of Application State) decouples the client from the server's URI structure. 
*   **Benefit:** Instead of the client hardcoding URLs (which creates a fragile system), the server provides dynamic links (hypermedia) in the JSON response.
*   **Advantage:** This allows the API to evolve (e.g., changing `/rooms` to `/campus/rooms`) without breaking compliant clients. Our `DiscoveryResource` demonstrates this by providing a single entry point from which all other resources can be navigated.

### Part 2: Room Management
**Q: What are the implications of returning only IDs versus returning full objects?**
*   **Returning IDs only:** Optimizes network bandwidth and reduces serialization overhead, which is critical for low-power IoT mobile clients. However, it creates the "N+1 Problem," where a client must make multiple follow-up requests to get any meaningful data, increasing total latency.
*   **Returning Full Objects:** Provides immediate context at the cost of larger payloads. In this API, we return full objects for specific fetches to reduce client-side complexity, while providing a filtered list view to balance performance.

**Q: Is the DELETE operation idempotent?**
Yes. An operation is idempotent if multiple identical requests have the same effect on the server state.
*   **Scenario:** If a client sends `DELETE /rooms/LIB-301` twice:
    1.  The first call deletes the room and returns `204 No Content`.
    2.  The second call finds the room already gone and returns `404 Not Found`.
*   **Justification:** While the *response code* changes, the *state of the server* (the room being absent) is identical after both calls. Therefore, DELETE is idempotent.

### Part 3: Sensor Operations & Linking
**Q: What are the technical consequences if a client sends data with a format that mismatches the `@Consumes` annotation (e.g., XML instead of JSON)?**
JAX-RS will immediately reject the request with an **HTTP 415 Unsupported Media Type** status code. 
*   **Mechanism:** The runtime uses "Content Negotiation" to match the client's `Content-Type` header against the resource's `@Consumes` metadata. 
*   **Impact:** This acts as a primary security and integrity barrier, preventing the application from attempting to parse malformed or unexpected data structures which could lead to deserialization vulnerabilities or server-side crashes.

**Q: Contrast Query Parameters vs. URL Paths for filtering.**
*   **Path Parameters (`/sensors/type/CO2`):** Implies the filter is a resource hierarchy. This is less flexible because it suggests a static structure.
*   **Query Parameters (`/sensors?type=CO2`):** Correctly models the request as a query over a collection. 
*   **Superiority:** Query parameters allow for complex multi-criteria filtering (e.g., `?type=CO2&status=ACTIVE`) which would be architecturally messy and ambiguous to represent in a URL path.

### Part 4: Sub-Resources
**Q: Discuss the benefits of the Sub-Resource Locator pattern.**
Delegating logic to a `SensorReadingResource` via a locator (`/{sensorId}/readings`) enforces **Separation of Concerns**.
*   **Avoids "God Classes":** It prevents the `SensorResource` from becoming a massive, unmaintainable controller.
*   **Contextual Logic:** It allows the nested resource to focus purely on "Readings" while inheriting the "Sensor context" from the locator. This makes the codebase modular and enables cleaner dependency injection for complex sub-trees.

### Part 5: Advanced Error Handling & Observability
**Q: Why is HTTP 422 more accurate than 404 for missing references?**
A **404 Not Found** suggests that the *resource endpoint itself* (the URL) is incorrect. In contrast, **422 Unprocessable Entity** signifies that the server understands the request syntax (valid JSON) but cannot process the instructions because they are semantically invalid (e.g., referencing a room ID that doesn't exist). This distinction is critical for debugging, as it tells the developer that their code is correct, but their data is out of sync.

**Q: Risks of exposing Java stack traces?**
Stack traces are a "gold mine" for attackers. They reveal:
1.  **Software Versions:** Enabling attackers to look up known CVEs for specific Jersey or Grizzly versions.
2.  **Internal Paths:** Exposing the server's directory structure.
3.  **Code Logic:** Hints about the underlying algorithms which can be exploited for "Insecure Direct Object Reference" (IDOR) attacks.
Our API uses a **Global Safety Net (500 Mapper)** to intercept all throwables and return a generic, secure message.

---

## 🏛️ Design Patterns & Architectural Decisions

To ensure a first-class, scalable system, several key architectural patterns were implemented:

1.  **Singleton Pattern (`InventoryService`):** Ensures a single, centralized source of truth for in-memory data, preventing synchronization issues across multiple resource instances.
2.  **Sub-Resource Locator Pattern (`SensorResource`):** Delegates nested URI logic to specialized classes, reducing complexity and enforcing a strict resource hierarchy.
3.  **Data Transfer Object (DTO) / POJO Pattern:** Core entities are cleanly encapsulated, ensuring that the API layer is decoupled from any future persistence logic.
4.  **Observer-like Side Effects:** When a `SensorReading` is posted, the system automatically triggers an update to the parent `Sensor` object, ensuring data consistency across the entire API graph.
5.  **Chain of Responsibility (Filters):** Custom JAX-RS filters handle cross-cutting concerns (logging, authentication-readiness) without polluting individual business logic methods.
6.  **Strategy Pattern (Exception Mappers):** Separate mappers for each exception type allow the system to choose the correct HTTP response strategy based on the specific business failure.

---

## 📁 Project Structure
- `src/main/java/org/westminster/smartcampus/`
    - `model/`: Data POJOs with HATEOAS link support.
    - `resource/`: REST Endpoints using Sub-resource locators.
    - `service/`: Singleton Inventory Manager & Validation Logic.
    - `exception/`: Dedicated architectural Mappers (403, 409, 422, 500).
    - `filter/`: Observability logging filters (Request/Response).
