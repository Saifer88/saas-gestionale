# Struttura del progetto e convenzioni

## Layout

```
src/main/java/com/pirris/
├── model/                      # Entity Panache (active record), 11 tabelle
│   ├── BaseModel.java          # PK UUID assegnata su persist
│   ├── AuditedModel.java       # + created_at / updated_at automatici
│   ├── <Nome>Model.java        # una per tabella
│   └── enums/                  # enum di dominio + AttributeConverter dedicati
├── dto/                        # DTO del contratto HTTP (NON le entity)
│   └── <area>/                 # raggruppati per view/area (es. overview/)
├── service/                    # logica di business (CDI @ApplicationScoped)
│   └── <Nome>Service.java      # servizi a supporto delle resource
├── resource/                   # Layer REST, orientato alle view dell'app
│   ├── <Nome>Resource.java     # interfaccia con annotation JAX-RS
│   ├── <Nome>ResourceImpl.java # implementazione (delega ai service)
│   ├── RestApplication.java    # @ApplicationPath("/api") + OpenAPI
│   └── error/                  # ApiError + exception mapper
└── resources/
    ├── application.properties
    └── db/changelog/           # Liquibase master changelog (fonte di verità schema)
```

## Convenzioni di naming

- **Package**: `com.pirris`
- **Entity**: suffisso `Model` + `@Table(name = "snake_case")`. Es: `ClientModel` → tabella `client`.
- **Resource REST**: interfaccia `<Nome>Resource` (porta le annotation) + implementazione `<Nome>ResourceImpl` (`@ApplicationScoped`).
- **DTO**: record immutabili in `com.pirris.dto`, disaccoppiati dalle entity.
- Campi Java in `camelCase` con `@Column(name = "snake_case")` esplicito.

## Pattern per le entity

- Estendi `AuditedModel` se la tabella ha `created_at`/`updated_at`; `BaseModel` altrimenti (vale per `lesson_package` e `session_participant`).
- Le foreign key sono mappate come **campi `UUID` diretti** (es. `clientId`), non `@ManyToOne`: niente proxy lazy, serializzazione JSON pulita, allineamento 1:1 con le colonne.
- Gli enum di dominio sono **campi tipizzati** persistiti in colonne `*_raw` tramite un `AttributeConverter` dedicato (es. `SessionStatusConverter`), che salva il valore `raw()`. Non usare `@Enumerated` né campi `String` raw con getter transient.
- Campi pubblici (stile Panache active record), default inizializzati per i `NOT NULL`.

## Pattern per il layer web

- Le resource espongono **ciò che una view dell'app chiede**, non le tabelle. Una resource per view (Overview, Agenda, Clienti, ...).
- La resource (Impl) è sottile: valida input e delega la logica a un `Service`.
- DTO di input (draft) e output (view/report) separati dalle entity.
- Scritture `@Transactional`; 404 via `NotFoundException` (mappata a `ApiError`).

## Contabilità: derivata, non memorizzata

- **Nessuna tabella ledger**. Saldi e incassi si calcolano da `training_session` + `session_participant` + `lesson_package` + `expense`.
- **Nessuna tabella di consumo pacchetti**. Residui = `capacity − (partecipanti con quel pacchetto e sessione completata)`.
- Un corso è un template; le sue occorrenze sono `training_session` con `course_id`.

## Cosa NON fare

- Non usare Hibernate per generare lo schema: è compito di Liquibase. Ogni cambio schema è un nuovo `changeSet`.
- Non reintrodurre un ledger o una tabella di consumo pacchetti: derivare.
- Non introdurre float per il denaro (sempre centesimi `long`).
- Non cancellare fisicamente clienti/servizi/tariffe: usa `archived`.
- Non denormalizzare copiando nomi (`client_name`, `service_name`): derivare dall'id.
