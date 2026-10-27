# Struttura del progetto e convenzioni

## Layout

```
src/main/java/com/pirris/
├── model/                      # Entity Panache (active record): 11 di dominio + studio + account
│   ├── BaseModel.java          # PK UUID assegnata su persist
│   ├── AuditedModel.java       # + created_at / updated_at automatici
│   ├── TenantModel.java        # AuditedModel + studio_id: base di ogni entità di dominio
│   ├── StudioModel / AccountModel  # identità/tenant (AuditedModel, NON tenant-scoped)
│   ├── <Nome>Model.java        # una per tabella
│   └── enums/                  # enum di dominio + AttributeConverter dedicati
├── dto/                        # DTO del contratto HTTP (NON le entity)
│   └── <area>/                 # raggruppati per view/area (es. overview/, auth/)
├── repository/                 # accesso dati tenant-aware
│   ├── TenantRepository<T>     # base: filtra/stampa sempre studio_id corrente
│   └── <Nome>Repository.java   # concreti per entità
├── security/                   # TenantContext (studio corrente dal JWT)
├── service/                    # logica di business (CDI @ApplicationScoped)
├── resource/                   # Layer REST, orientato alle view dell'app
│   ├── <Nome>Resource.java     # SOLO interfacce con annotation JAX-RS
│   ├── impl/                   # implementazioni <Nome>ResourceImpl (con @Path + @ApplicationScoped)
│   ├── RestApplication.java    # metadati OpenAPI (prefisso /api via quarkus.rest.path)
│   └── error/                  # ApiError + exception mapper
└── resources/
    ├── application.properties
    └── db/changelog/           # Liquibase master changelog (fonte di verità schema)
```

## Convenzioni di naming

- **Package**: `com.pirris`
- **Entity**: suffisso `Model` + `@Table(name = "snake_case")`. Es: `ClientModel` → tabella `client`.
- **Resource REST**: interfaccia `<Nome>Resource` in `resource/` (porta le annotation di metodo e i ruoli) + implementazione `<Nome>ResourceImpl` in `resource/impl/` (`@ApplicationScoped`). In Quarkus REST l'impl concreta DEVE ripetere `@Path` di classe, altrimenti l'endpoint non viene registrato (404). Il prefisso globale `/api` è impostato da `quarkus.rest.path`, non da `@ApplicationPath`.
- **DTO**: record immutabili in `com.pirris.dto`, disaccoppiati dalle entity.
- Campi Java in `camelCase` con `@Column(name = "snake_case")` esplicito.

## Pattern per le entity

- Le entità di dominio estendono `TenantModel` (id + audit + `studio_id`). `StudioModel`/`AccountModel`, che sono il layer identità e non sono tenant-scoped, estendono `AuditedModel`. Tutte le tabelle hanno `created_at`/`updated_at`.
- Le foreign key sono mappate come **campi `UUID` diretti** (es. `clientId`), non `@ManyToOne`: niente proxy lazy, serializzazione JSON pulita, allineamento 1:1 con le colonne.
- Gli enum di dominio sono **campi tipizzati** persistiti in colonne `*_raw` tramite un `AttributeConverter` dedicato (es. `SessionStatusConverter`), che salva il valore `raw()`. Non usare `@Enumerated` né campi `String` raw con getter transient.
- Campi pubblici (stile Panache active record), default inizializzati per i `NOT NULL`.

## Pattern per il layer web

- Le resource espongono **ciò che una view dell'app chiede**, non le tabelle. Una resource per view (Overview, Agenda, Clienti, ...).
- La resource (Impl) è sottile: valida input e delega la logica a un `Service`.
- DTO di input (draft) e output (view/report) separati dalle entity.
- Scritture `@Transactional`; 404 via `NotFoundException` (mappata a `ApiError`).

## Multi-tenant e sicurezza

- Il tenant è lo **studio** (`studio_id`). Isolamento **applicativo**, non via `@TenantId` di Hibernate (non agganciato in modo affidabile da Quarkus con la strategia discriminator).
- Ogni entità di dominio estende `TenantModel` (colonna `studio_id` + audit).
- L'accesso dati passa **solo** da un `TenantRepository`: ogni lettura è filtrata per lo studio corrente, ogni scrittura lo stampa. Non chiamare `Model.findAll()`/`listAll()` diretti su entità tenant-scoped: bypasserebbero l'isolamento.
- `TenantContext` (`@RequestScoped`) ricava lo studio dal claim `studio_id` del JWT. **Deny-by-default**: senza tenant autenticato l'accesso dati è rifiutato.
- Auth: JWT RS256 (smallrye-jwt), password BCrypt. Login pubblico; `/api/admin/*` solo ruolo `admin`; resto `authenticated`.

## Contabilità: derivata, non memorizzata

- **Nessuna tabella ledger**. Saldi e incassi si calcolano da `training_session` + `session_participant` + `lesson_package` + `expense`.
- **Nessuna tabella di consumo pacchetti**. Residui = `capacity − (partecipanti con quel pacchetto e sessione completata)`.
- Un corso è un template; le sue occorrenze sono `training_session` con `course_id`.
- Testi lunghi (`notes`, `anamnesis`, ...) sono `@JdbcTypeCode(SqlTypes.LONGVARCHAR)` su colonne CLOB.

## Cosa NON fare

- Non usare Hibernate per generare lo schema: è compito di Liquibase. Ogni cambio schema è un nuovo `changeSet`.
- Non reintrodurre un ledger o una tabella di consumo pacchetti: derivare.
- Non introdurre float per il denaro (sempre centesimi `long`).
- Non cancellare fisicamente clienti/servizi/tariffe: usa `archived`.
- Non denormalizzare copiando nomi (`client_name`, `service_name`): derivare dall'id.
