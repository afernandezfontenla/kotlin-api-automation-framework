# Kotlin API Automation Framework

[![API tests](https://github.com/afernandezfontenla/kotlin-api-automation-framework/actions/workflows/ci.yml/badge.svg)](https://github.com/afernandezfontenla/kotlin-api-automation-framework/actions/workflows/ci.yml)

End-to-end REST API test framework written in **Kotlin**, showing a layered, maintainable design
instead of "tests that build raw HTTP calls". The system under test is
[Restful-Booker](https://restful-booker.herokuapp.com), a public API with authentication and full CRUD.

## Stack

| Concern | Choice |
|---|---|
| Language / build | Kotlin 2.2, Gradle (Kotlin DSL), JVM 17 toolchain |
| HTTP | Ktor Client (CIO engine) |
| Serialization | kotlinx.serialization (typed DTOs) |
| Test runner | TestNG |
| Assertions | AssertJ |
| Logging | SLF4J + Logback (secrets masked) |
| CI | GitHub Actions |

## Architecture

```
src/main/kotlin/.../automation
  config/   ApiConfig        environment config (system property > env var > default)
  client/   ApiClient        generic HTTP client: typed bodies, timing, logging, no throw on 4xx/5xx
            ApiResponse      status + body + headers + duration, `bodyAs<T>()` to get a DTO
            LogMasker        keeps passwords/tokens/cookies out of the logs
  api/      AuthApi          one class per API resource: tests never touch raw URLs
            BookingApi
  dto/      AuthDtos, BookingDtos     @Serializable request/response models
  data/     BookingFactory   unique, valid test data

src/test/kotlin/.../automation
  base/     BaseApiTest      client + auth setup, automatic cleanup of created bookings
            Assertions       shouldHaveStatus(): prints the body when a status check fails
  tests/    AuthTests, BookingCrudTests, BookingNegativeTests
```

Layers only depend downward: `tests -> api -> client`. Adding a new resource means adding a DTO file,
an `XxxApi` class and a test class, with no changes to the client.

## Design decisions

- **Typed end to end.** Requests are DTOs, responses are decoded with `response.bodyAs<Booking>()`,
  and assertions compare objects instead of searching strings in JSON.
- **Independent tests.** Each test creates its own data through `BookingFactory`
  and `BaseApiTest` deletes everything afterwards, so tests can run in any order.
- **The client never throws on error status codes**, because negative tests (401/403/404...) need to assert on them.
- **Secrets are masked** in logs (`password`, `token`, `Cookie`, `Authorization`).
- **Known API quirks are documented in the tests** (e.g. delete answers `201`, bad credentials answer `200`).

## What is covered

- **Auth:** valid credentials return a token; invalid ones are rejected.
- **CRUD:** create, get by id, list, filter by name, full update (PUT), partial update (PATCH), delete.
- **Negative / security:** unknown id (404), update/delete without token or with an invalid one (403),
  create with missing fields.

## Running the tests

```bash
./gradlew test
```

Requires only a JDK (the build downloads a JDK 17 toolchain if needed). Reports:

- HTML report: `build/reports/tests/test/index.html`
- Full request/response log: `build/logs/api-tests.log`

### Targeting another environment

```bash
./gradlew test -Dapi.baseUrl=https://staging.example.com -Dapi.username=... -Dapi.password=...
```

or set `API_BASE_URL`, `API_USERNAME`, `API_PASSWORD` as environment variables.

## Troubleshooting

**Windows: `PKIX path building failed` when Gradle downloads dependencies.** A proxy or antivirus is
re-signing HTTPS traffic with a certificate Java does not trust. Run `run-tests.bat`, which makes Java
use the Windows certificate store for that run only.

## Notes

Restful-Booker is a shared public demo that resets periodically and can be slow to wake up,
so an occasional timeout in CI is expected. Timeouts are configured in `ApiClient`.

## Roadmap

- JSON Schema contract validation
- Allure reporting
- Parallel execution
- Retry policy for flaky infrastructure
