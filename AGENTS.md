# Project Manifest & Development Standards — ATraders Worker (Java)

## Centralized Configuration Standard (`AppProperties`)

1. **ALL Application Configuration Properties MUST Use `AppProperties`**:
   - ALL configuration settings, system parameters, trading thresholds, entry gaps, risk settings, indicator parameters, timeouts, and feature flags MUST be defined in `tpoints.me.config.AppProperties` (inside nested config classes like `EntrySettings`, `RiskSettings`, etc., with delegate getters/setters in `TradingSettings` or `AppProperties`).
   - **STRICT PROHIBITION:** **NEVER** use `@Value` annotations (`@Value("${...}")`), local static constants, or class-private configuration variables anywhere in services, controllers, or components (e.g., `AutomatIQService35`, `PositionsService`).

2. **Dynamic Orchestrator Sync Compatibility**:
   - Configuration properties in `AppProperties` are automatically parsed via reflection, exposed to the Orchestrator UI, and dynamically synced to worker nodes at runtime. Local fields or `@Value` annotations bypass this system and WILL NOT receive dynamic updates from the Orchestrator.

3. **Service Usage Pattern**:
   - Access configuration properties in strategy, service, and component code strictly via `appProperties.getTradingSettings().get<PropertyName>()` (or `appProperties.get<PropertyName>()`).

## Java & Lombok Standards
- **Style:** Use Java conventions; place `static final` constants at the top.
- **Imports:** Strictly avoid FQNs. Always use explicit `import` statements.
- **Lombok & Fields:** Use `@Slf4j`, constructor autowiring via `@RequiredArgsConstructor`, and `@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)`.

## Spring & Architecture
- **DI:** Prefer `@Component` scan over `@Bean`.
- **Config:** Use `@ConfigurationProperties` / `AppProperties` following 12-factor app principles.
- **Safety:** Prefer `Optional<T>` over nulls. Use `@Transactional(readOnly = true)` for reads.
- **Health:** Implement checks with Spring Boot Actuator.

## REST API Standards
- **Naming:** Controllers should be named `[Entity]Controller`.
- **OpenAPI:** Use `@Tag` for controllers, `@Operation` for endpoints, and `@ParameterObject` for grouped parameters (via `@ModelAttribute`) or `Pageable`.
- **Search Parameters:** Prefer grouping multiple query parameters into a "Criteria" DTO using `@ModelAttribute`.
- **Pagination Defaults:** Use `@SortDefault` to specify default sorting for `Pageable`.
- **Return Types:** Prefer explicit DTOs (e.g., `UserResponse` instead of `Object`).
- **Pagination DTO:** Use `PageResponse<T>` with `data` (List) and `meta` (Object containing `currentPage`, `pageSize`, `totalElements`, `totalPages`). Use `ResponseUtils` to convert Spring Data pages.
- **Specifications:** Move Specification construction into private helper methods in the service layer.
- **Auditing:** Use the `@Auditable` annotation on state-changing methods (POST, PUT, DELETE).
- **Error Handling:** 
  - Prefer throwing specialized exceptions (e.g., `ValidationException`) from the service layer.
  - Use a consistent `ErrorCode` enum and `@RestControllerAdvice` to manage exceptions globally.
  - Return a shared interface (e.g., `BaseResponse`) or a unified `ApiErrorResponse` DTO for consistency.
- **Request/Response Mapping:** Separate Request/Response DTOs, use `@Valid` for bodies, and use standard header constants.
- **DTO Fields:** Avoid primitive types in request/response DTOs intended for external (in/out) communication (e.g., prefer `Boolean` over `boolean`, `Long` over `long`) to support nullability and reduce NullPointerException risks.
- **Security Context:** Inject `SecurityService` to retrieve account ID or tenant context.
- **Service Layer Responsibility:** Keep controllers thin and delegative. Business logic, trading calculations, position tracking, null checks, and payload serialization/deserialization must reside in the service layer.

## Testing Protocol (Spock)
- **Selection:** Use **Spock (Groovy)** for all unit and integration testing (`src/test/groovy/...`).
- **Structure:** Given-When-Then blocks only.
- **Mocking:** Use native Spock `Mock()` and `Stub()`.
- **Run Flag:** Always use `-Dcheckstyle.skip` (if applicable) for test runs.

## Deployment & Operation Safety
- **Deployment Blackout / Freeze Window:** 
  - **STRICT PROHIBITION:** NEVER perform automated deployments, application restarts, or worker node disruptions during forbidden/blackout windows without explicit user confirmation.
  - Outside forbidden blackout windows, worker nodes are designed for fast, seamless restarts at any time (state recovery and exchange sync occur automatically upon boot, ~2-3 minutes).

## Self-Review Protocol
- **Automated Verification:** At the completion of every task, perform a self-review of all changes against the guidelines in this manifest. Include a brief compliance/verification summary in the final walkthrough/summary.

## Git & Workflow
- **Commits & Pushes:** `git commit` and `git push` ARE PERMITTED once features are fully implemented and verified via unit tests. Use clear, descriptive commit messages matching project conventions. Never push broken builds or unverified code.
