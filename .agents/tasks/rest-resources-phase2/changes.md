# Changes — Phase 2 REST resources

## Iteration 1 — initial implementation of plan.md (no build, no tests run)

All paths relative to `src/main/java/com/pirris/` unless noted. IMPLEMENTED = read + mapping;
STUB = final signature, `// TODO` + `UnsupportedOperationException("<Service>.<method> not yet implemented")`.

### 1. Spese
Created: `repository/ExpenseRepository.java`, `dto/expense/ExpenseDto.java`, `dto/expense/ExpenseDraftDto.java`,
`mapper/ExpenseMapper.java`, `service/ExpenseService.java`, `resource/ExpenseResource.java`,
`resource/impl/ExpenseResourceImpl.java`.

| ExpenseService method | Classification |
|---|---|
| `list(LocalDate from, LocalDate to)` | IMPLEMENTED |
| `detail(UUID id)` | IMPLEMENTED |
| `create(ExpenseDraftDto)` | STUB |
| `update(UUID, ExpenseDraftDto)` | STUB |
| `delete(UUID)` | STUB |

### 2. Servizi
Created: `repository/TrainingServiceRepository.java`, `repository/ServiceRateRepository.java`,
`dto/service/ServiceRateDto.java`, `dto/service/TrainingServiceDto.java`, `dto/service/TrainingServiceDraftDto.java`,
`dto/service/ServiceRateDraftDto.java`, `mapper/ServiceCatalogMapper.java`, `service/ServiceCatalogService.java`,
`resource/ServiceCatalogResource.java`, `resource/impl/ServiceCatalogResourceImpl.java`.

| ServiceCatalogService method | Classification |
|---|---|
| `list(boolean includeArchived)` | IMPLEMENTED |
| `detail(UUID id)` | IMPLEMENTED |
| `create(TrainingServiceDraftDto)` | STUB |
| `update(UUID, TrainingServiceDraftDto)` | STUB |
| `archive(UUID)` | STUB |
| `restore(UUID)` | STUB |
| `addRate(UUID, ServiceRateDraftDto)` | STUB |
| `updateRate(UUID, UUID, ServiceRateDraftDto)` | STUB |
| `archiveRate(UUID, UUID)` | STUB |
| `restoreRate(UUID, UUID)` | STUB |

### 3. Pacchetti
Created: `repository/LessonPackageRepository.java`, `dto/lessonpackage/LessonPackageDto.java`,
`dto/lessonpackage/PackagePurchaseDraftDto.java`, `mapper/LessonPackageMapper.java`, `service/LessonPackageService.java`,
`resource/LessonPackageResource.java`, `resource/impl/LessonPackageResourceImpl.java`.

| LessonPackageService method | Classification |
|---|---|
| `list(UUID clientId)` | IMPLEMENTED (tenant-checks `clientId` → 404; `usedCredits`/`remainingCredits` neutral 0, `TODO(Phase 3)` in mapper) |
| `detail(UUID id)` | IMPLEMENTED (credits neutral 0) |
| `purchase(PackagePurchaseDraftDto)` | STUB |

### 4. Corsi
Created: `repository/CourseRepository.java`, `repository/CourseParticipantRepository.java`,
`dto/course/CourseSummaryDto.java`, `dto/course/CourseParticipantDto.java`, `dto/course/CourseDetailDto.java`,
`dto/course/CourseDraftDto.java`, `dto/course/CourseEnrollmentDraftDto.java`, `mapper/CourseMapper.java`,
`service/CourseService.java`, `resource/CourseResource.java`, `resource/impl/CourseResourceImpl.java`.

| CourseService method | Classification |
|---|---|
| `list(boolean includeInactive)` | IMPLEMENTED |
| `detail(UUID id)` | IMPLEMENTED |
| `create(CourseDraftDto)` | STUB |
| `update(UUID, CourseDraftDto)` | STUB |
| `deactivate(UUID)` | STUB |
| `activate(UUID)` | STUB |
| `enroll(UUID, CourseEnrollmentDraftDto)` | STUB |
| `unenroll(UUID, UUID)` | STUB |

### 5. Agenda
Created: `repository/TrainingSessionRepository.java`, `repository/SessionParticipantRepository.java`,
`repository/UnavailabilityRepository.java`, `dto/agenda/CalendarView.java`, `dto/agenda/CalendarSlotType.java`,
`dto/agenda/CalendarSlotDto.java`, `dto/agenda/SessionParticipantDto.java`, `dto/agenda/SessionDetailDto.java`,
`dto/agenda/SessionParticipantDraftDto.java`, `dto/agenda/SessionDraftDto.java`, `dto/agenda/UnavailabilityDto.java`,
`dto/agenda/UnavailabilityDraftDto.java`, `mapper/AgendaMapper.java`, `service/AgendaService.java`,
`resource/AgendaResource.java`, `resource/impl/AgendaResourceImpl.java`.

| AgendaService method | Classification |
|---|---|
| `calendar(CalendarView, LocalDate)` | IMPLEMENTED (sessions + unavailability merged into `CalendarSlotDto`) |
| `sessionDetail(UUID)` | IMPLEMENTED |
| `createSession(SessionDraftDto)` | STUB |
| `updateSession(UUID, SessionDraftDto)` | STUB |
| `deleteSession(UUID)` | STUB |
| `addParticipant(UUID, SessionParticipantDraftDto)` | STUB |
| `updateParticipant(UUID, UUID, SessionParticipantDraftDto)` | STUB |
| `removeParticipant(UUID, UUID)` | STUB |
| `unavailabilityDetail(UUID)` | IMPLEMENTED |
| `createUnavailability(UnavailabilityDraftDto)` | STUB |
| `updateUnavailability(UUID, UnavailabilityDraftDto)` | STUB |
| `deleteUnavailability(UUID)` | STUB |

### 6. Pagamenti
Created: `dto/payment/ClientBalanceDto.java`, `dto/payment/UnpaidLessonDto.java`, `dto/payment/StatementDto.java`,
`service/PaymentService.java`, `resource/PaymentResource.java`, `resource/impl/PaymentResourceImpl.java`.
No repository or mapper (nothing is read yet).

| PaymentService method | Classification |
|---|---|
| `balances()` | STUB |
| `balance(UUID clientId)` | STUB |
| `unpaidLessons(UUID clientId)` | STUB |
| `statement(UUID clientId, LocalDate from, LocalDate to)` | STUB |

### 7. Overview
Created: `resource/impl/OverviewResourceImpl.java`.
Modified: `dto/overview/OverviewDto.java` (`TodayAppointment.statusRaw: String` → `status: SessionStatus`),
`resource/OverviewResource.java` (`@RolesAllowed({ "admin", "user" })`, javadoc `{@link}` → `{@code}`).

| OverviewService method | Classification |
|---|---|
| `overview(LocalDate)` | STUB (unchanged) |

### Tests (written, not run)
Created under `src/test/java/com/pirris/resource/`:
- `ResourceRegistrationSmokeTest.java` — authenticated GET → 200 on implemented reads only
  (`/api/expenses`, `/api/services`, `/api/packages`, `/api/courses`, `/api/agenda` with DAY/WEEK/MONTH);
  anonymous `/api/expenses` → 401. Per user revision 2, stub-only resources (Pagamenti, Overview) are not called.
- `ResourceTenantIsolationTest.java` — studio A persists an expense + client; A reads the expense (200);
  studio B: expense detail 404, expense list without A's id, `/api/packages?clientId=<A's client>` 404.

### Notes for the build step
- `SessionParticipantRepository.listBySession` uses `:sessionId` (not `:sid`, which `queryForTenant` reserves for the tenant).
- `ExpenseRepository.listInRange` filters on the entity property `date`; if HQL rejects it as a keyword, qualify it.
