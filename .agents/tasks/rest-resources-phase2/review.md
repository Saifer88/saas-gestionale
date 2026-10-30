# Review — Phase 2 REST resources (static, no build/test run by user decision)

Verdict: APPROVED. No blockers.

Evidence checked (read directly; `git diff HEAD` + all untracked files):
- Screens: Spese (`/expenses`), Servizi (`/services`), Pacchetti (`/packages`), Corsi (`/courses`), Agenda (`/agenda`), Pagamenti (`/payments`), Overview (`/overview` impl added). No Report/Rapporti resource.
- Pattern: every new interface has `@Path`, `@Produces(JSON)`, `@RolesAllowed({"admin","user"})`; write-bearing ones also `@Consumes(JSON)`. Every `*ResourceImpl` repeats class `@Path` + `@ApplicationScoped`, `@Inject`s a concrete `@ApplicationScoped` service; thin delegation, 201/204 via `Response`. Repositories concrete `@ApplicationScoped extends TenantRepository<…>` with `setStudioId`.
- Tenant isolation: all reads go through `queryForTenant` / `findByIdForTenant` / `listAllForTenant`; no `Model.find/findAll/listAll`, no `Parameters` (only pre-existing `AccountModel.find` in `AuthService`, out of scope). `:sid` collision avoided (`SessionParticipantRepository.listBySession` uses `:sessionId`).
- Referenced id: `LessonPackageService.list(clientId)` resolves via `ClientRepository.findByIdForTenant` → `NotFoundException.of("Client", id)`; covered by `ResourceTenantIsolationTest` order 2.
- Stubs: all write/derived methods in Expense/ServiceCatalog/LessonPackage/Course/Agenda/Payment services throw `UnsupportedOperationException("<Service>.<method> not yet implemented")` with `// TODO`; no `@Transactional`, no lookups. Implemented methods are pure read + mapping (Agenda calendar merge of sessions + unavailability into `CalendarSlotDto` included). Full endpoint contracts exposed per plan tables.
- Mappers: 5 MapStruct `componentModel = CDI` interfaces, read-only (no `applyDraft`), injected with `@Inject`, no `Mappers.getMapper`. Multi-source methods map every target explicitly; `unavailabilitySlot` ignores/sets the non-matching targets and lets `id/allDay/reason/notes` map by name. Entity field names checked against every DTO component — all match. Derived `usedCredits/remainingCredits` constant `0` with `// TODO(Phase 3)`. No hand-written mapping in services.
- DTOs: records; drafts carry Bean Validation; money `long` cents; enums typed (`ExpenseKind`, `PaymentMethod`, `PackageKind`, `SessionStatus`); `OverviewDto.TodayAppointment.status` is `SessionStatus` and no `statusRaw` references remain.
- No changes to `pom.xml` or `src/main/resources` (Liquibase untouched).
- Tests: `ResourceRegistrationSmokeTest` does authenticated GET → 200 on each resource with an implemented read (expenses, services, packages, courses, agenda) + anonymous 401; Payments/Overview have no implemented read and are not called (consistent with the requested scope). `ResourceTenantIsolationTest` mirrors `TenantIsolationTest` (only `account` has an FK to `studio`, so arbitrary studio ids are valid).
- No compile errors spotted by reading (imports, signatures, `@Override`s, exhaustive enum switches all consistent).

Non-blocking notes (no action required for approval):
- `PaymentResource` and `OverviewResource` omit `@Consumes`; both are GET-only, so no behavioral effect.
- `ExpenseRepository` uses the property `date` and params `:from`/`:to`/`:end` in HQL; these are soft keywords in Hibernate 6 and should parse, but the final runtime verification step is what confirms it (smoke + isolation tests exercise `ORDER BY date` and the range path).
- Runtime proof (compile, MapStruct generation, tests) is deferred to the final verification step by user decision; this review is static only.
