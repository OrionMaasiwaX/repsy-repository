# Repsy REST API

Repsy REST API is a backend system for managing Repsy packages using Spring Boot, PostgreSQL, and pluggable storage strategies (File System or MinIO). The application allows users to upload `.rep` packages along with metadata and later download them on request.

---

## Features

- Upload `.rep` package and corresponding `meta.json`
- Validate and store `meta.json` in PostgreSQL
- Storage support via Strategy Pattern:
  - File System
  - MinIO (Object Storage)
- Retrieve previously uploaded files via HTTP GET
- Dockerized setup with PostgreSQL and MinIO

---

## Technologies Used

- Java 17
- Spring Boot 3.x
- PostgreSQL
- MinIO
- Docker, Docker Compose

---

## How to Run

### 1. Clone the Repository
```bash
git clone https://github.com/yourname/repsy-rest-api.git
cd repsy-rest-api
```

### 2. Build the Project
```bash
mvn clean package
```

### 3. Run with Docker
```bash
docker-compose up --build
```

This will:
- Start PostgreSQL
- Start MinIO
- Build and run the Repsy Spring Boot App

---

## Endpoints

### Upload Package

`POST /{packageName}/{version}`

**Request Type:** `multipart/form-data`

**Accepted Files:**
- `package.rep` (binary)
- `meta.json` (JSON)

**Sample curl:**
```bash
curl -X POST http://localhost:8080/mypackage/1.0.0 \
  -F "package.rep=@package.rep" \
  -F "meta.json=@meta.json"
```

**Behavior:**
- Validates the meta.json file
- Stores metadata to database
- Stores both files using the active storage strategy

### Download File

`GET /{packageName}/{version}/{fileName}`

**Returns:**
- File if exists (with correct MIME type)
- `404 Not Found` if file does not exist

**Sample curl:**
```bash
curl -O http://localhost:8080/mypackage/1.0.0/meta.json
```

---

## Configuration

### `application.properties`
```properties
# Choose storage: file-system | object-storage
storage.strategy=file-system

# File System Base Path
storage.file.base-path=/data

# MinIO Configuration (used if storage.strategy=object-storage)
storage.object.endpoint=http://repsy-minio:9000
storage.object.access-key=minioadmin
storage.object.secret-key=minioadmin
storage.object.bucket=repsy
```

You can override these via environment variables as well.

---

## Docker

### Compose Setup
`docker-compose.yml` includes:
- PostgreSQL (port 5432)
- MinIO (ports 9000, 9001)
- repsy-rest-api app (port 8080)

### Build and Run
```bash
docker-compose up --build
```

---

## Project Structure

- `repsy-rest-api/`: Main Spring Boot App
- `file-system-storage/`: Storage library for local disk
- `object-storage/`: Storage library for MinIO

---

## Commit History (Important)

- Each core feature has its own commit:
  - ✅ Endpoint implementations
  - ✅ Storage strategy integrations
  - ✅ Docker setup
  - ✅ README added

---

## Deployment

- Build Docker image:
```bash
docker build -t youruser/repsy-rest-api:latest .
```
- Push to registry (e.g., Repsy Docker repo):
```bash
docker push youruser/repsy-rest-api:latest
```

---

## Error Handling

- `400 Bad Request` → missing/invalid files
- `404 Not Found` → file not found
- `500 Internal Server Error` → server crash (with logs)

---

