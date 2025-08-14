## Orders App
### Overview

This is a Spring Boot application that uses a PostgreSQL database. The database is started using Docker Compose, and must be running before the application starts.

### Prerequisites

* Java 17
* Maven
* Docker & Docker Compose installed

### Setup

### 1. Start the Database
From the project root, run:

`docker-compose up -d`

This will start the PostgreSQL database with the configuration defined in docker-compose.yml.

Make sure the database container is running before starting the application:

`docker-compose ps`

### 2. Configure Application

Update application.yml (or .properties) if needed:

```
spring:
    datasource:
        url:
            jdbc: postgresql://localhost:5432/postgres
            username: myuser
            password: mypassword
```

Make sure these match the credentials defined in docker-compose.yml.

### 3. Build and Run the Application

* Using Maven:

```
mvn clean install
mvn spring-boot:run
```

* Using IDEA without applied profiles.

### 4. Verify

Application logs should show a successful database connection.

Access the REST API via http://localhost:8080.

Access the Swagger UI via http://localhost:8080/swagger-ui/index.html

### 5. Stop Database

To stop the database container:

`docker-compose down`

