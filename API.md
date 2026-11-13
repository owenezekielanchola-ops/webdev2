# Veterinary Clinic Pet API Documentation

Base URL: `http://localhost:8080/api/pets`

CORS is enabled for `http://localhost:3000` on `/api/**` (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS`, credentials allowed).

---

## Endpoints

| Method | Path | Description | Request Body / Params | Success Status | Error Statuses |
|---|---|---|---|---|---|
| `GET` | `/api/pets` | List pets with pagination, sorting, and optional species filter | Query: `page`, `size`, `sort`, `direction`, `species` | `200 OK` | `500` |
| `GET` | `/api/pets/{id}` | Get a single pet by ID | Path: `id` | `200 OK` | `404`, `500` |
| `POST` | `/api/pets` | Create a new pet | JSON: `CreatePetRequest` | `201 Created` | `400`, `404`, `409`, `500` |
| `PUT` | `/api/pets/{id}` | Full update of an existing pet | Path: `id`, JSON: `UpdatePetRequest` | `200 OK` | `400`, `404`, `409`, `500` |
| `PATCH` | `/api/pets/{id}` | Partial update of an existing pet | Path: `id`, JSON: `UpdatePetRequest` | `200 OK` | `400`, `404`, `409`, `500` |
| `DELETE` | `/api/pets/{id}` | Delete a pet by ID | Path: `id` | `204 No Content` | `404`, `500` |

---

## Query Parameters (`GET /api/pets`)

| Parameter | Default | Notes |
|---|---|---|
| `page` | `0` | Zero-based page index (minimum `0`) |
| `size` | `20` | Items per page (clamped between `1` and `100`) |
| `sort` | `id` | Field to sort by; also supports `field,dir` (e.g. `sort=name,asc`) |
| `direction` | `asc` | Sort direction (`asc` or `desc`) |
| `species` | *(optional)* | Case-insensitive exact match filter (e.g. `species=Dog`) |

---

## Request Schemas & Validation

### `CreatePetRequest`

- `microchipNumber` (`String`): `@NotBlank`, `@Size(min = 3, max = 50)`
- `name` (`String`): `@NotBlank`, `@Size(max = 100)`
- `species` (`String`): `@NotBlank`, `@Size(max = 50)`
- `breed` (`String`, optional): `@Size(max = 80)`
- `ownerId` (`Long`, optional): `@Min(1)`

### `UpdatePetRequest`

- `microchipNumber` (`String`): `@Size(min = 3, max = 50)` (required in `PUT`)
- `name` (`String`): `@Size(max = 100)` (required in `PUT`)
- `species` (`String`): `@Size(max = 50)` (required in `PUT`)
- `breed` (`String`, optional): `@Size(max = 80)`
- `ownerId` (`Long`, optional): `@Min(1)`

---

## Examples

### 1. List Pets (Paginated & Sorted)

**Request**
```http
GET /api/pets?page=0&size=5&sort=name,asc
```

**Response (`200 OK`)**
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
  "size": 5,
  "totalElements": 2,
  "totalPages": 1
}
```

### 2. Create Pet

**Request**
```http
POST /api/pets
Content-Type: application/json
```
```json
{
  "microchipNumber": "MC-2026-01",
  "name": "Muning",
  "species": "Cat",
  "breed": "Puspin",
  "ownerId": 1
}
```

**Response (`201 Created`)**
`Location: http://localhost:8080/api/pets/2`
```json
{
  "id": 2,
  "microchipNumber": "MC-2026-01",
  "name": "Muning",
  "species": "Cat",
  "breed": "Puspin",
  "ownerId": 1
}
```

### 3. Validation Error (`400 Bad Request`)

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

### 4. Resource Not Found (`404 Not Found`)

```json
{
  "status": 404,
  "message": "Pet not found with id: 999",
  "timestamp": "2026-11-13T21:31:51.8850154",
  "errors": null
}
```
