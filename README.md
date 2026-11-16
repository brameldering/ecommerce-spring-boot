# ecommerce-spring-boot

Sample e-commerce REST API: Spring Boot 3, Java 21, PostgreSQL, Flyway, and JWT security. The API is contract-first: the
interfaces and DTOs are generated from `src/main/resources/api/openapi.yaml`.

## Running locally

### Prerequisites

- Java 21
- PostgreSQL on `localhost:5432` with a database `ecomm` and a user that can create the `ecomm` schema.
  Credentials come from `DB_USERNAME` / `DB_PASSWORD` (default `packt` / `packt`).

One way to get a matching database with Docker:

```bash
docker run --name ecomm-db -d -p 5432:5432 -e POSTGRES_DB=ecomm -e POSTGRES_USER=packt -e POSTGRES_PASSWORD=packt postgres:16
```

### Start the app

```bash
./gradlew bootRun
```

Flyway creates the schema and loads the seed data on startup. The app listens on port 8080.

### Run the tests

```bash
./gradlew test
```

The tests don't need a database or a real JWT: controller tests use `@WithMockUser`, and service tests use Mockito.

## Testing the API with Swagger UI

Open <http://localhost:8080/swagger-ui.html> while the app is running.

The product endpoints (`/api/v1/products/**`) are public. Every other endpoint needs a bearer token:

1. Get a token. Use **POST /auth/token** with one of the seeded logins below, or sign up with **POST /users**, which
   returns a token immediately for a new USER-role account.
2. Copy `accessToken` from the response.
3. Click **Authorize** and paste the token on its own (Swagger adds `Bearer `).
4. Use **Try it out** on any endpoint.

Access tokens expire after 15 minutes. Get a new one with **POST /auth/token/refresh** and the `refreshToken`, or sign
in again.

## Testing the API with Postman

[`postman/ecommerce-api.postman_collection.json`](postman/ecommerce-api.postman_collection.json) calls every endpoint in
order. It signs in as the seeded `admin`, saves the tokens and any new IDs in collection variables, checks each
response's status code, and finally deletes the data it created, so you can run it repeatedly.

- **Postman app:** click **Import**, select the file, then right-click the **Ecommerce API** collection and choose
  **Run collection**.
- **Postman CLI:**

  ```bash
  postman collection run postman/ecommerce-api.postman_collection.json
  ```

Each run also places one order for the seeded customer `user1`. To point the collection at another server, change
its `baseUrl` variable.

[`TestAPIs.har`](TestAPIs.har) contains the same requests as an HTTP Archive. It has no scripts, so the collection is
easier to use.

### Seeded local logins

These accounts are created by `src/main/resources/db/migration/R__insert_initial_data.sql`. They are for local
development only.

| Username | Password | Role  | Can access                                  |
|----------|----------|-------|---------------------------------------------|
| `admin`  | `tiger`  | ADMIN | Everything, including `/api/v1/addresses/**` |
| `user`   | `tiger`  | USER  | Everything except `/api/v1/addresses/**`     |

> **Seed data is reset.** `R__insert_initial_data.sql` is a repeatable migration that deletes all rows and re-inserts
> the seed data whenever the file changes. Anything you create through the API is lost the next time that happens.
