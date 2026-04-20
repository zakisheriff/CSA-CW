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

### Part 1: Service Architecture & Setup
**Q: Explain the default lifecycle of a JAX-RS Resource class.**
JAX-RS resources are **Request-Scoped** by default. A new instance of the resource class is instantiated for every incoming HTTP request and destroyed after the response is sent. This ensures thread isolation at the resource level but requires that any shared state (like our in-memory data) must be managed in a thread-safe way, such as using singletons or static variables protected by `ConcurrentHashMap` or synchronization blocks to avoid race conditions.

### Part 2: Room Management
**Q: What are the implications of returning only IDs versus returning the full room objects?**
Returning only IDs reduces **network bandwidth** and improves performance for large collections. However, it requires the client to make follow-up requests to fetch details, increasing latency. For the Smart Campus, the list view returns full objects for convenience since the payload is small, while the "Discovery" map uses relative paths to point to full resource collections.

**Q: Is the DELETE operation idempotent?**
Yes. DELETE is idempotent because the final state of the server is the same (room gone) regardless of whether the request is sent once or multiple times. While the first call returns `204 No Content`, subsequent calls to a deleted resource return `404 Not Found`, which is semantically correct for an idempotent operation as the *resource state* remains unchanged.

### Part 3: Sensor Operations & Linking
**Q: Explain the consequences of Content-Type mismatches.**
If a client sends `text/plain` instead of `application/json` to a method annotated with `@Consumes(MediaType.APPLICATION_JSON)`, JAX-RS will reject the request with an **HTTP 415 Unsupported Media Type** error. This is handled by the `MessageBodyReader` which ensures the payload can be safely deserialized into the expected Java POJO.

### Part 4: Deep Nesting with Sub-Resources
**Q: Discuss the architectural benefits of the Sub-Resource Locator pattern.**
Delegating logic to separate resource classes (like `SensorReadingResource`) managed via a locator (`/{sensorId}/readings`) helps manage complexity. It avoids "God classes" that try to handle all nested paths, improves code readability, and allows for cleaner lifecycle management and dependency injection specific to the nested context.

### Part 5: Advanced Error Handling & Observability
**Q: Why is HTTP 422 semantically more accurate than 404 for missing references?**
HTTP **422 Unprocessable Entity** is strictly for cases where the syntax is correct (valid JSON) but the instruction is semantically invalid (referencing a non-existent room ID). A **404** implies the *endpoint* doesn't exist, which could confuse developers into thinking the URL path is wrong rather than the data payload.

**Q: Risks of exposing Java stack traces?**
Stack traces reveal internal implementation details like file paths, library versions (`Jersey`, `Grizzly`), and database schemas. An attacker can use this information to identify known vulnerabilities in specific library versions or gain insights into the system's logic to craft targeted exploits (exploiting "leak-prone" logic).

---

## 📁 Project Structure
- `src/main/java/org/westminster/smartcampus/`
    - `model/`: Data POJOs
    - `resource/`: API Endpoints & Sub-resources
    - `service/`: Singleton In-Memory Data Manager
    - `exception/`: Custom Exceptions & mappers (403, 409, 422, 500)
    - `filter/`: Observability logging filters
