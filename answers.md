# Lab 11 — Answer Template

**Course:** Web Development 2 &nbsp;|&nbsp; **Week:** 11 &nbsp;|&nbsp; **Lab:** Capstone Milestone 3

> **Instructions:** Fill in each section with links to your code and screenshots/recordings. Do not paste full file contents here — link to the file in your repository instead. Submit your GitHub repository URL to Moodle.

---

## Student Information

| Field | Value |
|---|---|
| Name | Owen Ezekiel A. Anchola |
| Student ID | 2511003 |
| GitHub Repository URL | https://github.com/owenezekielanchola-ops/webdev2 |
| Branch (if not `main`) | `week11` |

---

## Task 1 — Input Validation on DTOs

**DTO files with annotations** *(link to files in your repo)*:

- `CreatePetRequest.java`: https://github.com/owenezekielanchola-ops/webdev2/blob/week11/src/main/java/com/example/tasktracker/dto/CreatePetRequest.java
- `UpdatePetRequest.java`: https://github.com/owenezekielanchola-ops/webdev2/blob/week11/src/main/java/com/example/tasktracker/dto/UpdatePetRequest.java
- `PetController.java` (`@Valid` on `POST`, `PUT`, `PATCH`): https://github.com/owenezekielanchola-ops/webdev2/blob/week11/src/main/java/com/example/tasktracker/controller/PetController.java

**Evidence of 400 response for missing field:**

> Paste the JSON response body you received when you sent a POST with a missing required field:

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-11-13T21:31:51.7232786",
  "errors": [
    {
      "field": "name",
      "message": "Pet name is required"
    },
    {
      "field": "ownerId",
      "message": "Owner ID must be at least 1"
    },
    {
      "field": "microchipNumber",
      "message": "Microchip number is required"
    },
    {
      "field": "microchipNumber",
      "message": "Microchip number must be between 3 and 50 characters"
    }
  ]
}
```

---

## Task 2 — Global Exception Handler

**Files created:**

- `GlobalExceptionHandler.java`: https://github.com/owenezekielanchola-ops/webdev2/blob/week11/src/main/java/com/example/tasktracker/exception/GlobalExceptionHandler.java
- `ErrorResponse.java`: https://github.com/owenezekielanchola-ops/webdev2/blob/week11/src/main/java/com/example/tasktracker/dto/ErrorResponse.java
- `ResourceNotFoundException.java`: https://github.com/owenezekielanchola-ops/webdev2/blob/week11/src/main/java/com/example/tasktracker/exception/ResourceNotFoundException.java

**Evidence — 404 response:**

> Paste the response body for a GET request to a non-existent ID:

```json
{
  "status": 404,
  "message": "Pet not found with id: 999",
  "timestamp": "2026-11-13T21:31:51.8850154",
  "errors": null
}
```

**Evidence — 400 response with field errors:**

> Paste the response body for a POST with an invalid body, showing the `errors` array:

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-11-13T21:31:51.7232786",
  "errors": [
    {
      "field": "name",
      "message": "Pet name is required"
    },
    {
      "field": "ownerId",
      "message": "Owner ID must be at least 1"
    },
    {
      "field": "microchipNumber",
      "message": "Microchip number is required"
    },
    {
      "field": "microchipNumber",
      "message": "Microchip number must be between 3 and 50 characters"
    }
  ]
}
```

---

## Task 3 — CORS Configuration

**File created:**

- `WebConfig.java`: https://github.com/owenezekielanchola-ops/webdev2/blob/week11/src/main/java/com/example/tasktracker/config/WebConfig.java

**Evidence — CORS headers present:**

> Paste the curl output or describe the browser Network tab result showing `Access-Control-Allow-Origin`:

```
HTTP/1.1 200 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE,OPTIONS
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 1800
Allow: GET, HEAD, POST, PUT, DELETE, OPTIONS, PATCH
Content-Length: 0
Date: Fri, 13 Nov 2026 13:31:52 GMT
```

---

## Task 4 — Pagination and Filtering

**Files updated:**

- Repository method added: `Page<Pet> findBySpeciesIgnoreCase(String species, Pageable pageable)` in https://github.com/owenezekielanchola-ops/webdev2/blob/week11/src/main/java/com/example/tasktracker/repository/PetRepository.java
- Service method updated: `findAll(int page, int size, String sort, String direction, String species)` in https://github.com/owenezekielanchola-ops/webdev2/blob/week11/src/main/java/com/example/tasktracker/service/PetService.java
- Controller method updated: `getAll(...)` in https://github.com/owenezekielanchola-ops/webdev2/blob/week11/src/main/java/com/example/tasktracker/controller/PetController.java

**Evidence — paginated response:**

> Paste the JSON response for `GET /api/pets?page=0&size=5&sort=name,asc`:

```json
{
  "content": [
    {
      "id": 1,
      "microchipNumber": "MC-9001",
      "name": "Bantay",
      "species": "Dog",
      "breed": "Aspin",
      "ownerId": 1
    },
    {
      "id": 2,
      "microchipNumber": "MC-2026-01",
      "name": "Muning",
      "species": "Cat",
      "breed": "Puspin",
      "ownerId": 1
    }
  ],
  "empty": false,
  "first": true,
  "last": true,
  "number": 0,
  "numberOfElements": 2,
  "pageable": {
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 5,
    "paged": true,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "unpaged": false
  },
  "size": 5,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "totalElements": 2,
  "totalPages": 1
}
```

**Evidence — filtered response:**

> Paste the JSON response for `GET /api/pets?species=Dog`:

```json
{
  "content": [
    {
      "id": 1,
      "microchipNumber": "MC-9001",
      "name": "Bantay",
      "species": "Dog",
      "breed": "Aspin",
      "ownerId": 1
    }
  ],
  "empty": false,
  "first": true,
  "last": true,
  "number": 0,
  "numberOfElements": 1,
  "pageable": {
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 20,
    "paged": true,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "unpaged": false
  },
  "size": 20,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "totalElements": 1,
  "totalPages": 1
}
```

**Evidence — max size cap:**

> What response did you get when you requested `?size=999`? What was the actual page size in the metadata?

```json
{
  "content": [
    {
      "id": 1,
      "microchipNumber": "MC-9001",
      "name": "Bantay",
      "species": "Dog",
      "breed": "Aspin",
      "ownerId": 1
    },
    {
      "id": 2,
      "microchipNumber": "MC-2026-01",
      "name": "Muning",
      "species": "Cat",
      "breed": "Puspin",
      "ownerId": 1
    }
  ],
  "empty": false,
  "first": true,
  "last": true,
  "number": 0,
  "numberOfElements": 2,
  "pageable": {
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 100,
    "paged": true,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "unpaged": false
  },
  "size": 100,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "totalElements": 2,
  "totalPages": 1
}
```
Actual page size in metadata: `size: 100` (`pageable.pageSize: 100`), confirming the server capped `size=999` to `100`.

---

## Task 5 — Documentation and Test Collection

**Files committed:**

- `API.md` location in repo: https://github.com/owenezekielanchola-ops/webdev2/blob/week11/API.md
- `api_tests.json` location in repo: https://github.com/owenezekielanchola-ops/webdev2/blob/week11/api_tests.json

**Test collection run evidence:**

> Paste a screenshot link or describe the test run result (e.g., "5/5 requests passed"):

```
11/11 live HTTP verification checks passed against Spring Boot + MySQL (webdev2_week11):
- POST /api/pets (invalid) -> 400 Bad Request with ErrorResponse.errors array
- GET /api/pets/999 -> 404 Not Found with ErrorResponse
- OPTIONS /api/pets (Origin: http://localhost:3000) -> 200 OK with Access-Control-Allow-Origin: http://localhost:3000
- POST /api/pets (valid) -> 201 Created with Location header
- GET /api/pets?page=0&size=5&sort=name,asc -> 200 OK (sorted, size 5)
- GET /api/pets?species=Dog -> 200 OK (filtered to Dog)
- GET /api/pets?size=999 -> 200 OK (capped to size 100 in metadata)
- GET /api/pets/1 -> 200 OK
- PUT /api/pets/2 -> 200 OK
- PATCH /api/pets/2 -> 200 OK
- DELETE /api/pets/2 -> 204 No Content
```

**List the requests in your collection:**

| Request name | Method | Path | Expected status |
|---|---|---|---|
| Create Pet - Validation Error (400) | `POST` | `/api/pets` | `400` |
| Get Non-Existent Pet (404) | `GET` | `/api/pets/999` | `404` |
| Create Pet (201) | `POST` | `/api/pets` | `201` |
| List Pets Paginated and Sorted (200) | `GET` | `/api/pets?page=0&size=5&sort=name,asc` | `200` |
| Filter Pets by Species (200) | `GET` | `/api/pets?species=Dog` | `200` |
| List Pets Max Size Cap (200) | `GET` | `/api/pets?size=999` | `200` |
| Get Pet by ID (200) | `GET` | `/api/pets/1` | `200` |
| Update Pet PUT (200) | `PUT` | `/api/pets/2` | `200` |
| Partial Update Pet PATCH (200) | `PATCH` | `/api/pets/2` | `200` |
| Delete Pet (204) | `DELETE` | `/api/pets/2` | `204` |

---

## Reflection

> In 2–3 sentences: which of the five topics from Week 11 had the biggest impact on your capstone API? What surprised you most when implementing it?

The largest change was implementing the central error handling using the @RestControllerAdvice and ErrorResponse. This means that we have consistent JSON responses for any validation errors, missing IDs and duplicate conflicts. It was very surprising how easy it is to miss out the @Valid annotation or not catch the size=999 parameter when doing basic CRUD tests but enforcing validation and having a maximum will catch these issues.
