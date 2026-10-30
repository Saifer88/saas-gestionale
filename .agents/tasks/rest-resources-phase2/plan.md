# Implementation Plan — Phase 2 REST resources (all screens except Report)

Scope recap (user revisions override the original brief):
- Work in `/Users/pirrima001/coding/personal/saas-gestionale` directly. NEVER `git add/commit/push`.
- NO `./mvnw` build/test until the single final step (item 8). Each item's "Verify" is a
  self-review checklist; real verification happens once in item 8.
- IMPLEMENTED = pure read + mapping (TenantRepository query + MapStruct mapper → output DTO).
  STUB = method with final signature whose body is
  `// TODO: ...` + `throw new UnsupportedOperationException("<Service>.<method> not yet implemented");`
  (exact style of `OverviewService.overview`). Every write and every derived-accounting
  computation is a STUB. Stub methods carry no `@Transactional` and no lookups.
- Mappers: only entity → DTO reads. No `applyDraft`. Derived fields get neutral values
  (`constant = "0"` for int, `constant = "0L"` for long, `expression = "java(java.util.List.of())"`)
  with a `// TODO(Phase 3): ...` comment, as in `ClientMapper`.
- Draft DTOs (records + Bean Validation) are defined: they are part of the HTTP contract.
- Every resource interface: `@Path`, `@Produces/@Consumes(APPLICATION_JSON)`,
  `@RolesAllowed({ "admin", "user" })`, `@RestPath`/`@RestQuery`, `@Valid` on drafts,
  `Response` for 201 (create) / 204 (archive/delete/restore). Every `*ResourceImpl` in
  `resource/impl/` repeats the class-level `@Path` + `@ApplicationScoped`, `@Inject`s the
  service, and is thin (copy `ClientResourceImpl`).
- Every repository: `@ApplicationScoped class XxxRepository extends TenantRepository<XxxModel>`
  with `setStudioId` override; named params via `Map.of(...)` (or `HashMap` when a value may be
  null); never `io.quarkus.panache.common.Parameters`; never `Model.findAll()/listAll()`.
  `Sort` from `io.quarkus.panache.common.Sort` is fine.
- 404 always via `com.pirris.resource.error.NotFoundException.of("Entity", id)` through a private
  `requireX(UUID)` helper in the service.
- Any IMPLEMENTED read that receives a referenced id (e.g. `clientId`) first resolves it with
  `findByIdForTenant` → 404 when it belongs to another studio.
- Enums in DTOs are the typed domain enums (`ExpenseKind`, `PaymentMethod`, `PackageKind`,
  `SessionStatus`), never `String`. Money is `long` cents. FKs are `UUID`. No names copied
  into DTOs (no `clientName`, `serviceName`).

---

## 1. Spese (expenses) — `expense`

- [ ] 1. Expense screen: repository, DTOs, mapper, service, resource + impl.
      Files (create):
      - `src/main/java/com/pirris/repository/ExpenseRepository.java`
        - `List<ExpenseModel> listInRange(LocalDateTime fromInclusive, LocalDateTime toExclusive)`:
          both bounds nullable; build the HQL fragment and a `HashMap` param map only with the
          non-null bounds (`date >= :from`, `date < :to`); when both null pass `null` fragment;
          sort `Sort.descending("date")`; via `queryForTenant(...).list()`.
      - `src/main/java/com/pirris/dto/expense/ExpenseDto.java`
        `record ExpenseDto(UUID id, String name, LocalDateTime date, long amountCents, ExpenseKind kind, String sourceKey, boolean personal)`
      - `src/main/java/com/pirris/dto/expense/ExpenseDraftDto.java`
        `record ExpenseDraftDto(@NotBlank String name, @NotNull LocalDateTime date, @PositiveOrZero long amountCents, @NotNull ExpenseKind kind, String sourceKey, boolean personal)`
      - `src/main/java/com/pirris/mapper/ExpenseMapper.java`
        `@Mapper(componentModel = MappingConstants.ComponentModel.CDI)`: `ExpenseDto toDto(ExpenseModel)`, `List<ExpenseDto> toDtos(List<ExpenseModel>)`.
      - `src/main/java/com/pirris/service/ExpenseService.java` (inject `ExpenseRepository expenses`, `ExpenseMapper mapper`):
        - `List<ExpenseDto> list(LocalDate from, LocalDate to)` — IMPLEMENTED: `from.atStartOfDay()` / `to.plusDays(1).atStartOfDay()` (inclusive end date), nulls pass through.
        - `ExpenseDto detail(UUID id)` — IMPLEMENTED (`requireExpense`).
        - `ExpenseDto create(ExpenseDraftDto draft)` — STUB.
        - `ExpenseDto update(UUID id, ExpenseDraftDto draft)` — STUB.
        - `void delete(UUID id)` — STUB (physical delete: `expense` has no archived column).
      - `src/main/java/com/pirris/resource/ExpenseResource.java` — `@Path("/expenses")`
      - `src/main/java/com/pirris/resource/impl/ExpenseResourceImpl.java` — `@Path("/expenses") @ApplicationScoped`
      Endpoints:
      | Method | Path | In | Out | Service |
      |---|---|---|---|---|
      | GET | `/api/expenses?from=&to=` (LocalDate, optional) | – | `List<ExpenseDto>` | IMPLEMENTED |
      | GET | `/api/expenses/{id}` | – | `ExpenseDto` | IMPLEMENTED |
      | POST | `/api/expenses` | `@Valid ExpenseDraftDto` | 201 `ExpenseDto` | STUB |
      | PUT | `/api/expenses/{id}` | `@Valid ExpenseDraftDto` | `ExpenseDto` | STUB |
      | DELETE | `/api/expenses/{id}` | – | 204 | STUB |
      Verify: deferred to item 8. Self-check: impl repeats `@Path`, imports `com.pirris.resource.error.NotFoundException`.

## 2. Servizi (service catalogue) — `training_service` + `service_rate`

- [ ] 2. Catalogue screen: two repositories, DTOs, mapper, service, resource + impl.
      Naming decision: `ServiceCatalog*` for resource/service/mapper (avoids `ServiceService`); DTO area `dto/service`.
      Files (create):
      - `src/main/java/com/pirris/repository/TrainingServiceRepository.java`
        - `List<TrainingServiceModel> list(boolean includeArchived)`: `queryForTenant(includeArchived ? null : "archived = false", Sort.by("name"), null).list()`.
      - `src/main/java/com/pirris/repository/ServiceRateRepository.java`
        - `List<ServiceRateModel> listByServices(Collection<UUID> serviceIds, boolean includeArchived)`: return `List.of()` when ids empty; `"serviceId in :ids"` (+ `" and archived = false"`), `Sort.by("priceCents")`, `Map.of("ids", serviceIds)`.
      - `src/main/java/com/pirris/dto/service/ServiceRateDto.java`
        `record ServiceRateDto(UUID id, UUID serviceId, String name, long priceCents, int durationMinutes, boolean archived)`
      - `src/main/java/com/pirris/dto/service/TrainingServiceDto.java`
        `record TrainingServiceDto(UUID id, String name, String description, int durationMinutes, String colorHex, boolean archived, List<ServiceRateDto> rates)`
      - `src/main/java/com/pirris/dto/service/TrainingServiceDraftDto.java`
        `record TrainingServiceDraftDto(@NotBlank String name, String description, @Positive Integer durationMinutes, String colorHex)`
      - `src/main/java/com/pirris/dto/service/ServiceRateDraftDto.java`
        `record ServiceRateDraftDto(@NotBlank String name, @PositiveOrZero long priceCents, @Positive Integer durationMinutes)`
      - `src/main/java/com/pirris/mapper/ServiceCatalogMapper.java`:
        `ServiceRateDto toRateDto(ServiceRateModel)`, `List<ServiceRateDto> toRateDtos(List<ServiceRateModel>)`,
        `TrainingServiceDto toDto(TrainingServiceModel service, List<ServiceRateDto> rates)` (multi-source: the `rates`
        parameter name maps to the target component; add explicit `@Mapping(target = "id", source = "service.id")`
        style mappings for every scalar component to avoid ambiguity errors).
      - `src/main/java/com/pirris/service/ServiceCatalogService.java` (inject both repos + mapper):
        - `List<TrainingServiceDto> list(boolean includeArchived)` — IMPLEMENTED: load services, load rates for all ids in one query
          (`listByServices`, same `includeArchived`), group with `Collectors.groupingBy(r -> r.serviceId)`, map each via
          `mapper.toDto(s, mapper.toRateDtos(byService.getOrDefault(s.id, List.of())))`.
        - `TrainingServiceDto detail(UUID id)` — IMPLEMENTED: `requireService(id)` + its rates (archived included, so the detail shows everything).
        - `TrainingServiceDto create(TrainingServiceDraftDto)` — STUB.
        - `TrainingServiceDto update(UUID id, TrainingServiceDraftDto)` — STUB.
        - `void archive(UUID id)` / `void restore(UUID id)` — STUB.
        - `ServiceRateDto addRate(UUID serviceId, ServiceRateDraftDto)` — STUB.
        - `ServiceRateDto updateRate(UUID serviceId, UUID rateId, ServiceRateDraftDto)` — STUB.
        - `void archiveRate(UUID serviceId, UUID rateId)` / `void restoreRate(UUID serviceId, UUID rateId)` — STUB.
      - `src/main/java/com/pirris/resource/ServiceCatalogResource.java` — `@Path("/services")`
      - `src/main/java/com/pirris/resource/impl/ServiceCatalogResourceImpl.java`
      Endpoints:
      | Method | Path | In | Out | Service |
      |---|---|---|---|---|
      | GET | `/api/services?includeArchived=false` | – | `List<TrainingServiceDto>` | IMPLEMENTED |
      | GET | `/api/services/{id}` | – | `TrainingServiceDto` | IMPLEMENTED |
      | POST | `/api/services` | `@Valid TrainingServiceDraftDto` | 201 `TrainingServiceDto` | STUB |
      | PUT | `/api/services/{id}` | `@Valid TrainingServiceDraftDto` | `TrainingServiceDto` | STUB |
      | DELETE | `/api/services/{id}` | – | 204 (archive) | STUB |
      | POST | `/api/services/{id}/restore` | – | 204 | STUB |
      | POST | `/api/services/{id}/rates` | `@Valid ServiceRateDraftDto` | 201 `ServiceRateDto` | STUB |
      | PUT | `/api/services/{id}/rates/{rateId}` | `@Valid ServiceRateDraftDto` | `ServiceRateDto` | STUB |
      | DELETE | `/api/services/{id}/rates/{rateId}` | – | 204 (archive) | STUB |
      | POST | `/api/services/{id}/rates/{rateId}/restore` | – | 204 | STUB |
      Verify: deferred to item 8.

## 3. Pacchetti (prepaid packages) — `lesson_package`

- [ ] 3. Packages screen. Depends on existing `ClientRepository` (tenant check of `clientId`).
      Files (create):
      - `src/main/java/com/pirris/repository/LessonPackageRepository.java`
        - `List<LessonPackageModel> listByClient(UUID clientId)`: `"clientId = :cid"`, `Sort.descending("purchasedOn")`.
        - `List<LessonPackageModel> listAllSorted()`: `listAllForTenant(Sort.descending("purchasedOn"))`.
      - `src/main/java/com/pirris/dto/lessonpackage/LessonPackageDto.java`
        `record LessonPackageDto(UUID id, UUID clientId, LocalDateTime purchasedOn, long priceCents, int capacity, LocalDateTime expiresOn, String notes, PaymentMethod paymentMethod, LocalDateTime invoiceDate, boolean black, PackageKind kind, int usedCredits, int remainingCredits)`
      - `src/main/java/com/pirris/dto/lessonpackage/PackagePurchaseDraftDto.java`
        `record PackagePurchaseDraftDto(@NotNull UUID clientId, LocalDateTime purchasedOn, @PositiveOrZero long priceCents, @Positive Integer capacity, LocalDateTime expiresOn, String notes, @NotNull PaymentMethod paymentMethod, LocalDateTime invoiceDate, boolean black, @NotNull PackageKind kind)`
      - `src/main/java/com/pirris/mapper/LessonPackageMapper.java`: `toDto`, `toDtos`;
        `@Mapping(target = "usedCredits", constant = "0")`, `@Mapping(target = "remainingCredits", constant = "0")`
        with `// TODO(Phase 3): derived by PackageService = capacity − participants with this package whose session is completed`.
      - `src/main/java/com/pirris/service/LessonPackageService.java` (inject `LessonPackageRepository packages`, `ClientRepository clients`, `LessonPackageMapper mapper`):
        - `List<LessonPackageDto> list(UUID clientId)` — IMPLEMENTED: when `clientId != null` → `requireClient(clientId)` (404 `Client`) then `listByClient`; else `listAllSorted`.
        - `LessonPackageDto detail(UUID id)` — IMPLEMENTED (`requirePackage`, entity name `"LessonPackage"`).
        - `LessonPackageDto purchase(PackagePurchaseDraftDto draft)` — STUB (`// TODO: tenant-check clientId, persist, update client preferredPackageId if needed; income is derived`).
      - `src/main/java/com/pirris/resource/LessonPackageResource.java` — `@Path("/packages")`
      - `src/main/java/com/pirris/resource/impl/LessonPackageResourceImpl.java`
      Endpoints:
      | Method | Path | In | Out | Service |
      |---|---|---|---|---|
      | GET | `/api/packages?clientId=` (optional UUID) | – | `List<LessonPackageDto>` | IMPLEMENTED (credits neutral 0) |
      | GET | `/api/packages/{id}` | – | `LessonPackageDto` | IMPLEMENTED (credits neutral 0) |
      | POST | `/api/packages` | `@Valid PackagePurchaseDraftDto` | 201 `LessonPackageDto` | STUB |
      Verify: deferred to item 8.

## 4. Corsi (group courses) — `course` + `course_participant`

- [ ] 4. Courses screen. Occurrence generation is Phase 3 (no endpoint now).
      Files (create):
      - `src/main/java/com/pirris/repository/CourseRepository.java`
        - `List<CourseModel> list(boolean includeInactive)`: `includeInactive ? null : "active = true"`, `Sort.by("weekday").and("startTime")`.
      - `src/main/java/com/pirris/repository/CourseParticipantRepository.java`
        - `List<CourseParticipantModel> listActiveByCourse(UUID courseId)`: `"courseId = :cid and active = true"`, `Sort.by("joinedOn")`.
      - `src/main/java/com/pirris/dto/course/CourseSummaryDto.java`
        `record CourseSummaryDto(UUID id, String name, int weekday, LocalTime startTime, int durationMinutes, int maxCapacity, String location, UUID serviceId, boolean active)`
      - `src/main/java/com/pirris/dto/course/CourseParticipantDto.java`
        `record CourseParticipantDto(UUID id, UUID courseId, UUID clientId, LocalDateTime joinedOn, UUID packageId, boolean active, String notes)`
      - `src/main/java/com/pirris/dto/course/CourseDetailDto.java`
        `record CourseDetailDto(UUID id, String name, int weekday, LocalTime startTime, int durationMinutes, int maxCapacity, String location, UUID serviceId, boolean active, String notes, List<CourseParticipantDto> participants)`
      - `src/main/java/com/pirris/dto/course/CourseDraftDto.java`
        `record CourseDraftDto(@NotBlank String name, @NotNull @Min(1) @Max(7) Integer weekday, @NotNull LocalTime startTime, @Positive Integer durationMinutes, @Positive Integer maxCapacity, String location, UUID serviceId, String notes)`
      - `src/main/java/com/pirris/dto/course/CourseEnrollmentDraftDto.java`
        `record CourseEnrollmentDraftDto(@NotNull UUID clientId, UUID packageId, LocalDateTime joinedOn, String notes)`
      - `src/main/java/com/pirris/mapper/CourseMapper.java`: `toSummary`, `toSummaries`, `toParticipantDto`, `toParticipantDtos`,
        `CourseDetailDto toDetail(CourseModel course, List<CourseParticipantDto> participants)` (multi-source, explicit `source = "course.x"` mappings).
      - `src/main/java/com/pirris/service/CourseService.java` (inject both repos + mapper):
        - `List<CourseSummaryDto> list(boolean includeInactive)` — IMPLEMENTED.
        - `CourseDetailDto detail(UUID id)` — IMPLEMENTED: `requireCourse` + `listActiveByCourse`.
        - `CourseDetailDto create(CourseDraftDto)` / `update(UUID, CourseDraftDto)` — STUB.
        - `void deactivate(UUID id)` / `void activate(UUID id)` — STUB (soft, `active` flag).
        - `CourseParticipantDto enroll(UUID courseId, CourseEnrollmentDraftDto)` — STUB (`// TODO: tenant-check course/client/package, capacity check`).
        - `void unenroll(UUID courseId, UUID participantId)` — STUB (soft, `active = false`).
      - `src/main/java/com/pirris/resource/CourseResource.java` — `@Path("/courses")`
      - `src/main/java/com/pirris/resource/impl/CourseResourceImpl.java`
      Endpoints:
      | Method | Path | In | Out | Service |
      |---|---|---|---|---|
      | GET | `/api/courses?includeInactive=false` | – | `List<CourseSummaryDto>` | IMPLEMENTED |
      | GET | `/api/courses/{id}` | – | `CourseDetailDto` | IMPLEMENTED |
      | POST | `/api/courses` | `@Valid CourseDraftDto` | 201 `CourseDetailDto` | STUB |
      | PUT | `/api/courses/{id}` | `@Valid CourseDraftDto` | `CourseDetailDto` | STUB |
      | DELETE | `/api/courses/{id}` | – | 204 (deactivate) | STUB |
      | POST | `/api/courses/{id}/restore` | – | 204 (reactivate) | STUB |
      | POST | `/api/courses/{id}/participants` | `@Valid CourseEnrollmentDraftDto` | 201 `CourseParticipantDto` | STUB |
      | DELETE | `/api/courses/{id}/participants/{participantId}` | – | 204 | STUB |
      Verify: deferred to item 8.

## 5. Agenda — `training_session` + `session_participant` + `unavailability`

- [ ] 5. Agenda screen. Entities stay separate; unified only in `CalendarSlotDto`. Overlap validation / slot
      suggestion are Phase 3 `SchedulingService` (no endpoint now). One `AgendaService` owns calendar,
      session/participant and unavailability endpoints (Phase 3 may extract `SessionService`).
      Files (create):
      - `src/main/java/com/pirris/repository/TrainingSessionRepository.java`
        - `List<TrainingSessionModel> listNotCancelledBetween(LocalDateTime start, LocalDateTime end)`:
          `"status <> :cancelled and startDate >= :start and startDate < :end"`, `Sort.by("startDate")`,
          `Map.of("cancelled", SessionStatus.CANCELLED, "start", start, "end", end)`.
      - `src/main/java/com/pirris/repository/SessionParticipantRepository.java`
        - `List<SessionParticipantModel> listBySessions(Collection<UUID> sessionIds)` (`List.of()` when empty; `"sessionId in :ids"`).
        - `List<SessionParticipantModel> listBySession(UUID sessionId)`.
      - `src/main/java/com/pirris/repository/UnavailabilityRepository.java`
        - `List<UnavailabilityModel> listOverlapping(LocalDateTime start, LocalDateTime end)`: `"startDate < :end and endDate > :start"`, `Sort.by("startDate")`.
      - `src/main/java/com/pirris/dto/agenda/CalendarView.java` — `enum CalendarView { DAY, WEEK, MONTH }`.
      - `src/main/java/com/pirris/dto/agenda/CalendarSlotType.java` — `enum CalendarSlotType { SESSION, UNAVAILABILITY }`.
      - `src/main/java/com/pirris/dto/agenda/CalendarSlotDto.java`
        `record CalendarSlotDto(CalendarSlotType type, UUID id, LocalDateTime start, LocalDateTime end, boolean allDay, SessionStatus status, UUID serviceId, UUID courseId, List<UUID> clientIds, String reason, String notes)`
        (session-only fields null/empty for UNAVAILABILITY; `reason` null for SESSION).
      - `src/main/java/com/pirris/dto/agenda/SessionParticipantDto.java`
        `record SessionParticipantDto(UUID id, UUID sessionId, UUID clientId, long priceCents, UUID packageId, PaymentMethod paymentMethod, boolean paid, boolean black)`
      - `src/main/java/com/pirris/dto/agenda/SessionDetailDto.java`
        `record SessionDetailDto(UUID id, LocalDateTime startDate, int durationMinutes, UUID serviceId, UUID courseId, String notes, SessionStatus status, LocalDateTime invoiceDate, List<SessionParticipantDto> participants)`
      - `src/main/java/com/pirris/dto/agenda/SessionParticipantDraftDto.java`
        `record SessionParticipantDraftDto(@NotNull UUID clientId, @PositiveOrZero long priceCents, UUID packageId, PaymentMethod paymentMethod, boolean paid, boolean black)`
      - `src/main/java/com/pirris/dto/agenda/SessionDraftDto.java`
        `record SessionDraftDto(@NotNull LocalDateTime startDate, @Positive Integer durationMinutes, UUID serviceId, UUID courseId, String notes, SessionStatus status, LocalDateTime invoiceDate, List<@Valid SessionParticipantDraftDto> participants)`
      - `src/main/java/com/pirris/dto/agenda/UnavailabilityDto.java`
        `record UnavailabilityDto(UUID id, LocalDateTime startDate, LocalDateTime endDate, String reason, boolean allDay, String notes)`
      - `src/main/java/com/pirris/dto/agenda/UnavailabilityDraftDto.java`
        `record UnavailabilityDraftDto(@NotNull LocalDateTime startDate, @NotNull LocalDateTime endDate, String reason, boolean allDay, String notes)`
      - `src/main/java/com/pirris/mapper/AgendaMapper.java`:
        - `SessionParticipantDto toParticipantDto(SessionParticipantModel)`, `List<SessionParticipantDto> toParticipantDtos(List<SessionParticipantModel>)`.
        - `SessionDetailDto toSessionDetail(TrainingSessionModel session, List<SessionParticipantDto> participants)` (multi-source, explicit `session.x` sources).
        - `UnavailabilityDto toUnavailabilityDto(UnavailabilityModel)`.
        - `CalendarSlotDto sessionSlot(TrainingSessionModel session, List<UUID> clientIds)`:
          `type` constant `"SESSION"`, `start` ← `session.startDate`, `end` expression
          `"java(session.startDate.plusMinutes(session.durationMinutes))"`, `allDay` constant `"false"`,
          `reason` ignore, explicit `session.id/status/serviceId/courseId/notes`.
        - `CalendarSlotDto unavailabilitySlot(UnavailabilityModel unavailability)`:
          `type` constant `"UNAVAILABILITY"`, `start` ← `startDate`, `end` ← `endDate`, `status/serviceId/courseId` ignore,
          `clientIds` expression `"java(java.util.List.of())"`.
      - `src/main/java/com/pirris/service/AgendaService.java` (inject the 3 repos + mapper):
        - `List<CalendarSlotDto> calendar(CalendarView view, LocalDate reference)` — IMPLEMENTED (read + mapping):
          range: DAY = `[ref, ref+1d)`; WEEK = Monday..Sunday containing ref (`ref.with(DayOfWeek.MONDAY)`, +7d);
          MONTH = `[ref.withDayOfMonth(1), +1 month)`. Load non-cancelled sessions, participants of those sessions in one
          query grouped by `sessionId` into `List<UUID>` of clientIds, overlapping unavailabilities; map each with the
          mapper; concatenate and sort by `CalendarSlotDto::start` (`Comparator.comparing`).
        - `SessionDetailDto sessionDetail(UUID id)` — IMPLEMENTED (`requireSession` + `listBySession`).
        - `SessionDetailDto createSession(SessionDraftDto)` / `updateSession(UUID, SessionDraftDto)` / `void deleteSession(UUID)` — STUB
          (`// TODO(Phase 3): overlap validation via SchedulingService, client preference snapshot`).
        - `SessionParticipantDto addParticipant(UUID sessionId, SessionParticipantDraftDto)` /
          `updateParticipant(UUID sessionId, UUID participantId, SessionParticipantDraftDto)` /
          `void removeParticipant(UUID sessionId, UUID participantId)` — STUB.
        - `UnavailabilityDto unavailabilityDetail(UUID id)` — IMPLEMENTED (`requireUnavailability`).
        - `UnavailabilityDto createUnavailability(UnavailabilityDraftDto)` / `updateUnavailability(UUID, UnavailabilityDraftDto)` /
          `void deleteUnavailability(UUID)` — STUB.
      - `src/main/java/com/pirris/resource/AgendaResource.java` — `@Path("/agenda")`
      - `src/main/java/com/pirris/resource/impl/AgendaResourceImpl.java` — calendar: `view == null ? WEEK : view`,
        `reference == null ? LocalDate.now() : reference` (defaults applied in the impl, as `OverviewResource` documents).
      Endpoints:
      | Method | Path | In | Out | Service |
      |---|---|---|---|---|
      | GET | `/api/agenda?view=DAY\|WEEK\|MONTH&reference=yyyy-MM-dd` (both optional) | – | `List<CalendarSlotDto>` | IMPLEMENTED |
      | GET | `/api/agenda/sessions/{id}` | – | `SessionDetailDto` | IMPLEMENTED |
      | POST | `/api/agenda/sessions` | `@Valid SessionDraftDto` | 201 `SessionDetailDto` | STUB |
      | PUT | `/api/agenda/sessions/{id}` | `@Valid SessionDraftDto` | `SessionDetailDto` | STUB |
      | DELETE | `/api/agenda/sessions/{id}` | – | 204 | STUB |
      | POST | `/api/agenda/sessions/{id}/participants` | `@Valid SessionParticipantDraftDto` | 201 `SessionParticipantDto` | STUB |
      | PUT | `/api/agenda/sessions/{id}/participants/{participantId}` | `@Valid SessionParticipantDraftDto` | `SessionParticipantDto` | STUB |
      | DELETE | `/api/agenda/sessions/{id}/participants/{participantId}` | – | 204 | STUB |
      | GET | `/api/agenda/unavailabilities/{id}` | – | `UnavailabilityDto` | IMPLEMENTED |
      | POST | `/api/agenda/unavailabilities` | `@Valid UnavailabilityDraftDto` | 201 `UnavailabilityDto` | STUB |
      | PUT | `/api/agenda/unavailabilities/{id}` | `@Valid UnavailabilityDraftDto` | `UnavailabilityDto` | STUB |
      | DELETE | `/api/agenda/unavailabilities/{id}` | – | 204 | STUB |
      Verify: deferred to item 8.

## 6. Pagamenti (payments) — derived accounting only

- [ ] 6. Payments screen: DTOs, stub service, resource + impl. No repository, no mapper (nothing is read yet).
      Files (create):
      - `src/main/java/com/pirris/dto/payment/ClientBalanceDto.java`
        `record ClientBalanceDto(UUID clientId, long chargesCents, long paymentsCents, long balanceCents)` (balance = charges − payments, positive = owed).
      - `src/main/java/com/pirris/dto/payment/UnpaidLessonDto.java`
        `record UnpaidLessonDto(UUID participantId, UUID sessionId, LocalDateTime startDate, UUID serviceId, long priceCents)`
      - `src/main/java/com/pirris/dto/payment/StatementDto.java`
        `record StatementDto(UUID clientId, LocalDate from, LocalDate to, long openingBalanceCents, long closingBalanceCents, List<Entry> entries)`
        with nested `record Entry(LocalDateTime date, String type, String description, long amountCents)` (same shape as `ClientDetailDto.HistoryEntry`).
      - `src/main/java/com/pirris/service/PaymentService.java` (no injections), all STUB with `// TODO(Phase 3): AccountService ...`:
        - `List<ClientBalanceDto> balances()`
        - `ClientBalanceDto balance(UUID clientId)`
        - `List<UnpaidLessonDto> unpaidLessons(UUID clientId)`
        - `StatementDto statement(UUID clientId, LocalDate from, LocalDate to)`
      - `src/main/java/com/pirris/resource/PaymentResource.java` — `@Path("/payments")`
      - `src/main/java/com/pirris/resource/impl/PaymentResourceImpl.java`
      Endpoints:
      | Method | Path | In | Out | Service |
      |---|---|---|---|---|
      | GET | `/api/payments/balances` | – | `List<ClientBalanceDto>` | STUB |
      | GET | `/api/payments/clients/{clientId}/balance` | – | `ClientBalanceDto` | STUB |
      | GET | `/api/payments/clients/{clientId}/unpaid` | – | `List<UnpaidLessonDto>` | STUB |
      | GET | `/api/payments/clients/{clientId}/statement?from=&to=` | – | `StatementDto` | STUB |
      Verify: deferred to item 8.

## 7. Overview (Panoramica) — finish

- [ ] 7. Create `OverviewResourceImpl`, align `OverviewResource` with the other resources, type the status in `OverviewDto`.
      Files:
      - modify `src/main/java/com/pirris/dto/overview/OverviewDto.java`: in `TodayAppointment` replace `String statusRaw`
        with `SessionStatus status` (import `com.pirris.model.enums.SessionStatus`); nothing else changes.
      - modify `src/main/java/com/pirris/resource/OverviewResource.java`: add `@RolesAllowed({ "admin", "user" })`
        (import `jakarta.annotation.security.RolesAllowed`); the `{@link OverviewResourceImpl}` javadoc becomes `{@code OverviewResourceImpl}`
        like `ClientResource` (cross-package link).
      - create `src/main/java/com/pirris/resource/impl/OverviewResourceImpl.java`: `@Path("/overview") @ApplicationScoped`,
        `@Inject OverviewService overviewService`, `overview(LocalDate reference)` →
        `overviewService.overview(reference != null ? reference : LocalDate.now())`.
      - `OverviewService` unchanged (stays STUB, consumes nothing new).
      Endpoints:
      | Method | Path | In | Out | Service |
      |---|---|---|---|---|
      | GET | `/api/overview?reference=` (optional LocalDate) | – | `OverviewDto` | STUB |
      Verify: deferred to item 8.

## 8. Smoke tests + single final build/test run

- [ ] 8a. Write the smoke tests (written now, run only in 8b).
      Files (create):
      - `src/test/java/com/pirris/resource/ResourceRegistrationSmokeTest.java` — `@QuarkusTest`; each test method
        `@TestSecurity(user = "smoke@test", roles = "user")` + `@JwtSecurity(claims = @Claim(key = "studio_id", value = "cccccccc-cccc-cccc-cccc-cccccccccccc"))`
        (a studio id not used by other tests). RestAssured `given().when().get(...)`:
        - IMPLEMENTED reads → `.statusCode(200)`: `/api/expenses`, `/api/services`, `/api/packages`, `/api/courses`, `/api/agenda`.
        - STUB-only resources → registered but not implemented: `.statusCode(not(anyOf(is(404), is(401), is(403))))`
          (expected 500 from `UnsupportedOperationException`): `/api/payments/balances`, `/api/overview`.
        - one anonymous call `/api/expenses` → 401 (policy still deny-by-default).
      - `src/test/java/com/pirris/resource/ResourceTenantIsolationTest.java` — copy the `TenantIsolationTest` structure
        (`@TestMethodOrder(OrderAnnotation)`, `QuarkusTransaction.requiringNew()`, studios
        `dddddddd-…` (A) and `eeeeeeee-…` (B)); static fields hold ids across ordered tests:
        - Order 1 (studio A): persist an `ExpenseModel` via `ExpenseRepository.persistForTenant` (name marker, `date = LocalDateTime.now()`)
          and a `ClientModel` via `ClientRepository.persistForTenant`; then HTTP `GET /api/expenses/{id}` → 200.
        - Order 2 (studio B): `GET /api/expenses/{id}` → 404; `GET /api/expenses` → 200 and body does not contain the id
          (`body("id", not(hasItem(id.toString())))`); `GET /api/packages?clientId={A's client id}` → 404 (referenced-id check).
      Verify: in 8b.

- [ ] 8b. Single build + test run (the ONLY Maven invocation of the whole task).
      Commands (from `/Users/pirrima001/coding/personal/saas-gestionale`):
      1. `./mvnw clean compile` — expect BUILD SUCCESS; MapStruct generates `*MapperImpl` under
         `target/generated-sources/annotations/com/pirris/mapper/` with no "Unmapped target" errors.
      2. `./mvnw test` — expect all green: existing `SchemaMappingTest`, `AuthFlowTest`, `TenantIsolationTest`
         plus the two new smoke classes.
      Fix any compile/MapStruct/test failure, then re-run only these two commands.

---

## Ambiguities (flagged, with the default the plan uses)

1. **Enum JSON format.** Domain enums have `raw()` tokens (`"planned"`, `"noShow"`, `"oneTime"`) matching the Swift app,
   but no `@JsonValue`; Jackson will emit `PLANNED`, `NO_SHOW`, ... in DTOs and expect them in drafts/query params.
   Default: leave enums untouched (Jackson names). Decide whether to add `@JsonValue raw()` + `@JsonCreator fromRaw` later.
2. **Stub HTTP status.** Stub endpoints raise `UnsupportedOperationException` → generic 500. Default: no new exception
   mapper; smoke tests assert "not 404/401/403" for stub-only resources. A mapper to `501 Not Implemented` would be cleaner — user's call.
3. **Agenda range contract.** "day/week/month" could be `view+reference` or explicit `from/to`. Default: `view` (DAY|WEEK|MONTH,
   default WEEK) + `reference` (default today), week = Monday–Sunday (same convention as `OverviewService`). Sessions belong to a
   range by `startDate`; unavailability by overlap. PROVISIONAL / NO_SHOW / COMPLETED sessions are shown (only CANCELLED excluded).
4. **Calendar slot content.** Slot carries `clientIds` only (no names, per "don't denormalize"). The app must resolve names
   from the client list; a later enrichment (names via join) would be a contract change.
5. **Packages list without `clientId`.** Brief says "list per client". Default: `clientId` optional; absent → all packages of the
   studio (needed for a plain authenticated smoke GET); present → tenant-checked client (404 if foreign).
6. **Package update/delete.** Not in the brief ("list per client, purchase, detail"); not exposed. Package remaining/used
   credits are in the DTO as neutral 0 — the app will show 0 until Phase 3 `PackageService`.
7. **Service rates editing.** "create/update service and rates" could mean one nested payload. Default: separate rate
   sub-resource (`/services/{id}/rates/...`); service draft carries no rates. Archived rates: list respects `includeArchived`,
   detail returns all rates.
8. **"Activate/deactivate" verb.** Mapped to `DELETE` = archive and `POST .../restore` = unarchive, mirroring Clients. Same for
   courses (`active` flag, no `archived` column) and for course unenrollment (`active = false`, not a physical delete).
9. **Physical deletes.** `expense`, `training_session`, `session_participant`, `unavailability` have no archived column; their
   `DELETE` endpoints are planned as physical deletes (STUB). For sessions, the domain may prefer status `CANCELLED` over
   delete — to be decided in Phase 3 `SessionService`. No session status-transition endpoints (complete/cancel) are exposed now.
10. **`expense.source_key`.** Semantics not documented (likely a recurring-series key). Exposed as optional plain string.
11. **Payments contract.** Brief lists balance, unpaid lessons, statement per client; the plan adds `GET /payments/balances`
    (all clients) because the Pagamenti screen likely lists outstanding balances — drop it if not wanted. Marking lessons as paid
    / manual payments is out of MVP (PLAN.md "Fuori ambito") and not exposed.
12. **OverviewDto other fields.** Only `statusRaw → SessionStatus status` changes as requested; `TodayAppointment.sessionId`
    (String) and `time` (String) and `clientNames` (denormalized names) are left as is — candidates for `UUID`/`LocalTime`/`clientIds`.
13. **OverviewService javadoc** still mentions "ledger entries" (no ledger exists). Left untouched (out of scope); worth fixing in Phase 3.
14. **Invoice (`invoice` table).** No screen in scope owns it (Report excluded); no resource planned.

---

## Digest

Build/test facts (read, not run — no Maven allowed before item 8b):
- Build: `./mvnw clean compile`; tests: `./mvnw test` (H2 in-memory, Liquibase migrate-at-start, `%test` profile).
- `pom.xml`: `maven-compiler-plugin` with `<parameters>true</parameters>`, `<proc>full</proc>`, `mapstruct-processor`
  in `annotationProcessorPaths` — do not touch. No `-Amapstruct.unmappedTargetPolicy` → unmapped targets are warnings.
- Security: `quarkus.http.auth.permission.api.paths=/api/*` authenticated; `/api/auth/login` permit. Tests use
  `@TestSecurity` + `@JwtSecurity(claims = @Claim(key = "studio_id", value = ...))` (`TenantContext.STUDIO_CLAIM`).
- Existing tests: `SchemaMappingTest`, `AuthFlowTest`, `TenantIsolationTest` (pass at last known state; not re-run here).

Reference blocks to copy:
- `ClientResource` (interface): `@Path("/clients") @Produces(APPLICATION_JSON) @Consumes(APPLICATION_JSON) @RolesAllowed({ "admin", "user" })`;
  `@GET List<ClientSummaryDto> list(@RestQuery("q") String q)`; `@GET @Path("/{id}") ClientDetailDto detail(@RestPath UUID id)`;
  `@POST Response create(@Valid ClientDraftDto draft)`; `@DELETE @Path("/{id}") Response archive(@RestPath UUID id)`;
  `@POST @Path("/{id}/restore") Response restore(@RestPath UUID id)`. Imports `org.jboss.resteasy.reactive.RestPath/RestQuery`.
- `ClientResourceImpl`: `@Path("/clients") @ApplicationScoped public class ClientResourceImpl implements ClientResource`,
  `@Inject ClientService clientService;` create → `Response.status(Response.Status.CREATED).entity(created).build()`;
  archive → `Response.noContent().build()`.
- `ClientService`: `@ApplicationScoped`, `@Inject ClientRepository clients; @Inject ClientMapper mapper;`,
  `private ClientModel requireClient(UUID id) { return clients.findByIdForTenant(id).orElseThrow(() -> NotFoundException.of("Client", id)); }`
  (reuse this exact helper shape in `LessonPackageService`).
- `OverviewService.overview` stub body:
  `// TODO: aggregate ...` / `throw new UnsupportedOperationException("OverviewService.overview not yet implemented");`
- `ClientMapper`: `@Mapper(componentModel = MappingConstants.ComponentModel.CDI) public interface ClientMapper`;
  `@Mapping(target = "balanceCents", constant = "0L")`; `@Mapping(target = "history", expression = "java(java.util.List.of())")`.
- `TenantRepository<T extends BaseModel>`: `protected abstract void setStudioId(T, UUID)`;
  `listAllForTenant(Sort)`, `findByIdForTenant(UUID) : Optional<T>`,
  `queryForTenant(String extraQuery /*nullable*/, Sort /*nullable*/, Map<String,Object> params /*nullable*/) : PanacheQuery<T>`,
  `countForTenant()`, `persistForTenant(T)`, `deleteByIdForTenant(UUID)`.
- `ClientRepository`: `@ApplicationScoped`, `setStudioId(...) { entity.studioId = studioId; }`,
  `queryForTenant("archived = false", Sort.by("lastName").and("firstName"), null).list()`.
- `NotFoundException.of(String entity, Object id)` in `com.pirris.resource.error` (mapped to 404 `ApiError`);
  `ValidationExceptionMapper` maps `ConstraintViolationException` → 400.

Entity fields (all extend `TenantModel`: `id`, `studioId`, `createdAt`, `updatedAt`; public fields):
- `ExpenseModel`: `name`, `LocalDateTime date`, `long amountCents`, `ExpenseKind kind`, `String sourceKey`, `boolean personal`.
- `TrainingServiceModel`: `name`, `description`, `int durationMinutes`, `colorHex`, `boolean archived`.
- `ServiceRateModel`: `UUID serviceId`, `name`, `long priceCents`, `int durationMinutes`, `boolean archived`.
- `LessonPackageModel`: `UUID clientId`, `LocalDateTime purchasedOn`, `long priceCents`, `int capacity`, `LocalDateTime expiresOn`,
  `notes`, `PaymentMethod paymentMethod`, `LocalDateTime invoiceDate`, `boolean black`, `PackageKind kind`.
- `CourseModel`: `name`, `int weekday` (1..7), `LocalTime startTime`, `int durationMinutes`, `int maxCapacity`, `location`,
  `UUID serviceId`, `boolean active`, `notes`.
- `CourseParticipantModel`: `UUID courseId`, `UUID clientId`, `LocalDateTime joinedOn`, `UUID packageId`, `boolean active`, `notes`.
- `TrainingSessionModel`: `LocalDateTime startDate`, `int durationMinutes`, `UUID serviceId`, `UUID courseId`, `notes`,
  `SessionStatus status`, `LocalDateTime invoiceDate`.
- `SessionParticipantModel`: `UUID sessionId`, `UUID clientId`, `long priceCents`, `UUID packageId`, `PaymentMethod paymentMethod`,
  `boolean paid`, `boolean black`.
- `UnavailabilityModel`: `LocalDateTime startDate`, `LocalDateTime endDate`, `reason`, `boolean allDay`, `notes`.
- Enums (`com.pirris.model.enums`): `ExpenseKind {ONE_TIME, RECURRING}`, `PackageKind {LESSONS, TIME}`,
  `PaymentMethod {CASH, CARD, TRANSFER, OTHER}`, `SessionStatus {PROVISIONAL, PLANNED, COMPLETED, CANCELLED, NO_SHOW}`.

Files to modify (only two existing files):
- `src/main/java/com/pirris/dto/overview/OverviewDto.java` — `record TodayAppointment(String sessionId, String time, List<String> clientNames, long totalPriceCents, String statusRaw)`
  → last component `SessionStatus status`. No other code references `statusRaw` (grep of `src/main/java` confirmed).
- `src/main/java/com/pirris/resource/OverviewResource.java` — `@Path("/overview") @Produces(APPLICATION_JSON) public interface OverviewResource { @GET OverviewDto overview(@RestQuery LocalDate reference); }`
  → add `@RolesAllowed({ "admin", "user" })`.

Tests covering the change: new `ResourceRegistrationSmokeTest` (one GET per new resource + Overview) and
`ResourceTenantIsolationTest` (Expense detail/list isolation + packages `clientId` referenced-id 404), pattern from
`src/test/java/com/pirris/security/TenantIsolationTest.java` and `src/test/java/com/pirris/resource/AuthFlowTest.java`.
