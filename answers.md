# WEBDEV2 — Week 10 Lab Answers

Name: Owen Ezekiel A. Anchola
Student ID: 2511003
GitHub repo: https://github.com/owenezekielanchola-ops/webdev2
Date submitted: 2026-11-13

---

## Task 1 — URL Design & Controller Skeleton

**URL design table (method, path, expected status codes):**

| HTTP Method | URL Path | Expected Status Codes |
|---|---|---|
| GET | `/api/pets` | `200` |
| GET | `/api/pets/{id}` | `200`, `404` |
| POST | `/api/pets` | `201`, `400` |
| PUT | `/api/pets/{id}` | `200`, `404`, `400` |
| PATCH | `/api/pets/{id}` | `200`, `404`, `400` |
| DELETE | `/api/pets/{id}` | `204`, `404` |

**Controller skeleton code:**
```java
@RestController
@RequestMapping("/api/pets")
public class PetController {

    @GetMapping
    public List<PetDTO> getAll() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> getById(@PathVariable Long id) {
        return null;
    }

    @PostMapping
    public ResponseEntity<PetDTO> create(@RequestBody CreatePetRequest request) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetDTO> update(@PathVariable Long id, @RequestBody UpdatePetRequest request) {
        return null;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PetDTO> patch(@PathVariable Long id, @RequestBody UpdatePetRequest request) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return null;
    }
}
```

**Evidence (screenshot filename or curl output):**
```text
> curl.exe -s -i http://localhost:8080/api/pets
HTTP/1.1 200 
Content-Type: application/json
Content-Length: 98
Date: Fri, 13 Nov 2026 13:10:03 GMT

[{"id":1,"microchipNumber":"MC-9001","name":"Bantay","species":"Dog","breed":"Aspin","ownerId":1}]
```

**Reflection question:**
_Why do REST URLs use plural nouns (`/api/students`) rather than verb phrases (`/api/getStudents`), and what role does the HTTP method play?_

Your answer:
The REST endpoints are plural nouns because we aren't looking to perform an action on a URL, but rather to access the collection of resources. The HTTP method (GET, POST, PUT, PATCH, DELETE) will allow for the server to know which action to take on the resource.

---

## Task 2 — GET Endpoints — List & Get by ID

**Approach / explanation:**
GET `/api/pets` calls `petService.findAll()` which returns a list of `PetDTO`s with a `200 OK` status. If there are no database rows it returns an empty array. GET `/api/pets/{id}` calls `petService.findById(id)` which translates the `Optional` into either a `200 OK` or `404 Not Found`. `PetDTO` only contains the fields `id`, `microchipNumber`, `name`, `species`, `breed` and `ownerId`. It does not include the `Owner` which is a lazy relationship to the `Owner` entity.

**`PetDTO` class code:**
```java
package com.example.tasktracker.dto;

public class PetDTO {

    private Long id;
    private String microchipNumber;
    private String name;
    private String species;
    private String breed;
    private Long ownerId;

    public PetDTO() {
    }

    public PetDTO(Long id, String microchipNumber, String name, String species, String breed, Long ownerId) {
        this.id = id;
        this.microchipNumber = microchipNumber;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.ownerId = ownerId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMicrochipNumber() { return microchipNumber; }
    public void setMicrochipNumber(String microchipNumber) { this.microchipNumber = microchipNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
```

**`toDTO()` mapping method code:**
```java
public PetDTO toDTO(Pet pet) {
    Long ownerId = pet.getOwner() != null ? pet.getOwner().getId() : null;
    return new PetDTO(
            pet.getId(),
            pet.getMicrochipNumber(),
            pet.getName(),
            pet.getSpecies(),
            pet.getBreed(),
            ownerId
    );
}
```

**Evidence (screenshot filename or curl/Postman output):**
```text
=== GET ALL ===
HTTP/1.1 200 
Content-Type: application/json
Content-Length: 98

[{"id":1,"microchipNumber":"MC-9001","name":"Bantay","species":"Dog","breed":"Aspin","ownerId":1}]

=== GET BY ID 200 ===
HTTP/1.1 200 
Content-Type: application/json

{"id":1,"microchipNumber":"MC-9001","name":"Bantay","species":"Dog","breed":"Aspin","ownerId":1}

=== GET BY ID 404 ===
HTTP/1.1 404 
Content-Length: 0
```

**Reflection question:**
_What would go wrong if you returned the JPA entity directly from `@GetMapping("/{id}")` instead of a DTO?_

Your answer:
If we were to return the JPA entity then we are exposing information about our database schema and any private/internal fields contained within it. It also exposes our API to any changes made to the database. We could also run into issues with LazyInitializationExceptions or infinite JSON loops when Jackson tries to serialise our lazy relationships.

---

## Task 3 — POST Endpoint — Create with 201 + Location

**Approach / explanation:**
I created an object called `CreatePetRequest` which only contains the relevant fields from the input. The `create` method in `PetService` converts the DTO into a `Pet` entity and saves it using the `petRepository.save()` method. Then it converts it back to a `PetDTO` using `toDTO()`. In the `create` method of `PetController` I used `ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri()` to set the location header and return a `201 Created`.

**`CreatePetRequest` class code:**
```java
package com.example.tasktracker.dto;

import jakarta.validation.constraints.NotBlank;

public class CreatePetRequest {

    @NotBlank
    private String microchipNumber;

    @NotBlank
    private String name;

    @NotBlank
    private String species;

    private String breed;

    private Long ownerId;

    public CreatePetRequest() {
    }

    public String getMicrochipNumber() { return microchipNumber; }
    public void setMicrochipNumber(String microchipNumber) { this.microchipNumber = microchipNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
```

**Controller `create()` method code:**
```java
@PostMapping
public ResponseEntity<PetDTO> create(@Valid @RequestBody CreatePetRequest request) {
    PetDTO created = petService.create(request);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.getId())
            .toUri();
    return ResponseEntity.created(location).body(created);
}
```

**Evidence (screenshot filename or curl/Postman output):**
```text
=== POST 201 ===
HTTP/1.1 201 
Location: http://localhost:8080/api/pets/2
Content-Type: application/json

{"id":2,"microchipNumber":"MC-1010","name":"Mochi","species":"Cat","breed":"Scottish Fold","ownerId":1}
```

**Reflection question:**
_Why does the REST contract specify `201 Created` for a successful POST rather than `200 OK`, and why is the `Location` header useful to include?_

Your answer:
The reason why we have a 201 Created status is because we want to inform the client that a new resource has been created. The location header tells the client where the new resource has been created (e.g. `/api/pets/2`) so they can GET or update it.

---

## Task 4 — PUT, PATCH & DELETE

**Approach / explanation:**
`UpdatePetRequest` contains the fields that can be updated when using PUT/PATCH. In the `update` method of `PetService`, all of the fields that can be updated are set on the `Pet` we load. In the `patch` method we check if each field is not null and only update those that have been passed. When we DELETE a pet using `DELETE /api/pets/{id}` we return `false` from the service if the ID doesn't exist. This means the controller will return a `204 No Content` on the first call and a `404 Not Found` on the second call.

**`UpdatePetRequest` class code:**
```java
package com.example.tasktracker.dto;

public class UpdatePetRequest {

    private String microchipNumber;
    private String name;
    private String species;
    private String breed;
    private Long ownerId;

    public UpdatePetRequest() {
    }

    public String getMicrochipNumber() { return microchipNumber; }
    public void setMicrochipNumber(String microchipNumber) { this.microchipNumber = microchipNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
```

**Controller code for PUT, PATCH, DELETE:**
```java
@PutMapping("/{id}")
public ResponseEntity<PetDTO> update(@PathVariable Long id, @RequestBody UpdatePetRequest request) {
    return petService.update(id, request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

@PatchMapping("/{id}")
public ResponseEntity<PetDTO> patch(@PathVariable Long id, @RequestBody UpdatePetRequest request) {
    return petService.patch(id, request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    if (!petService.delete(id)) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.noContent().build();
}
```

**Evidence (screenshot filename or curl/Postman output):**
```text
=== PUT 2 ===
HTTP/1.1 200 
Content-Type: application/json

{"id":2,"microchipNumber":"MC-1010","name":"Mochi Updated","species":"Cat","breed":"British Shorthair","ownerId":1}

=== PATCH 2 (only name sent: {"name":"Mochi Patched"}) ===
HTTP/1.1 200 
Content-Type: application/json

{"id":2,"microchipNumber":"MC-1010","name":"Mochi Patched","species":"Cat","breed":"British Shorthair","ownerId":1}

=== DELETE 2 (1st) ===
HTTP/1.1 204 

=== DELETE 2 (2nd 404) ===
HTTP/1.1 404 
Content-Length: 0
```

**Reflection question:**
_What is the difference between `PUT` and `PATCH` in terms of what the client sends, and what must the service-layer `patch()` method do differently from `update()`?_

Your answer:
PUT requests contain the full representation of the resource therefore all of the updateable fields will be updated. PATCH requests only contain fields that the client wants to update. In the service layer `update()` method all fields are set whereas in the `patch()` method we need to check if each field is not null.

---

## Task 5 — Full DTO Layer Audit + Capstone Milestone 3

**DTO class list (names only — confirm no entity class names appear):**
- `CreatePetRequest`
- `UpdatePetRequest`
- `PetDTO`

**409 Conflict scenario:**
We cannot have two pets with the same `microchipNumber`. Before we save a new pet or update the pet to have another pet's microchip we check if there is already a pet with that `microchipNumber` using `petRepository.existsByMicrochipNumber(...)`. If there is, we throw an exception which results in a `409 Conflict`.

Request:
```http
POST /api/pets
Content-Type: application/json

{"microchipNumber":"MC-1010","name":"Mochi","species":"Cat","breed":"Scottish Fold","ownerId":1}
```

Response:
```http
HTTP/1.1 409 
Content-Type: application/json

{"message":"Microchip number already exists: MC-1010"}
```

**Capstone Milestone 3 smoke-check evidence:**
```text
=== POST 201 ===
HTTP/1.1 201 
Location: http://localhost:8080/api/pets/2
{"id":2,"microchipNumber":"MC-1010","name":"Mochi","species":"Cat","breed":"Scottish Fold","ownerId":1}

=== GET NEW ID 2 ===
HTTP/1.1 200 
{"id":2,"microchipNumber":"MC-1010","name":"Mochi","species":"Cat","breed":"Scottish Fold","ownerId":1}

=== PUT 2 ===
HTTP/1.1 200 
{"id":2,"microchipNumber":"MC-1010","name":"Mochi Updated","species":"Cat","breed":"British Shorthair","ownerId":1}

=== PATCH 2 ===
HTTP/1.1 200 
{"id":2,"microchipNumber":"MC-1010","name":"Mochi Patched","species":"Cat","breed":"British Shorthair","ownerId":1}

=== DELETE 2 (1st) ===
HTTP/1.1 204 

=== GET DELETED 2 (404) ===
HTTP/1.1 404 
```

- [x] `POST` → `201`
- [x] `GET /{new-id}` → `200`
- [x] `PUT /{id}` → `200`
- [x] `PATCH /{id}` → `200` (other fields unchanged)
- [x] `DELETE /{id}` → `204`
- [x] `GET /{deleted-id}` → `404`

**Reflection question:**
_For one endpoint in your capstone API, state: the method, the URL, what status code it returns on success, what status code it returns when the resource is not found, and why there is no JPA entity in the controller method signature._

Your answer:
GET `/api/pets/{id}` - The HTTP method is GET. The URL is `/api/pets/{id}`. It will return a 200 OK with a `PetDTO` if the pet exists. It will return a 404 Not Found if the ID does not exist in the database. There is no `Pet` JPA entity in the method signature because we are only using `PetDTO`s in the controller to keep the API contract separate from the Hibernate entities and any lazy relationships.

---

## Self-Check

- [x] All Week 10 work on the `week10` branch (project at branch root, no `week10/` folder)
- [x] This file completed and renamed `answers.md`
- [x] No JPA entity class appears in any controller method signature
- [x] Repository link pasted into Moodle (no files uploaded)
