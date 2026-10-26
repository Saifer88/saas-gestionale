# Stack tecnologico e comandi

## Stack

- **Framework**: Quarkus 3.39.5
- **Linguaggio**: Java 21 (`maven.compiler.release=21`)
- **ORM**: Hibernate ORM with Panache (pattern **active record**)
- **Database**:
  - Produzione / remoto: **PostgreSQL**
  - Sviluppo locale: **H2 file-based** (`./data/gestionale`, modalità PostgreSQL)
  - Test: **H2 in-memory** (modalità PostgreSQL)
- **Migrazioni**: Liquibase 4.20.0 (changelog XML, singolo master consolidato)
- **REST**: Quarkus REST (RESTEasy Reactive) + Jackson
- **Validazione**: Hibernate Validator (Bean Validation su entity/DTO)
- **Docs API**: SmallRye OpenAPI + Swagger UI
- **Health**: SmallRye Health
- **Build**: Maven con wrapper (`./mvnw`)

## Comandi

```bash
# Dev mode con hot reload (H2 locale)
./mvnw quarkus:dev

# Compilazione
./mvnw clean compile

# Test (avvia Quarkus + Liquibase su H2, valida il mapping)
./mvnw test

# Package (jar)
./mvnw package

# Migrazioni Liquibase manuali
./mvnw liquibase:update
./mvnw liquibase:validate
```

## Endpoint utili in dev

- API base path: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/q/swagger-ui`
- OpenAPI schema: `http://localhost:8080/q/openapi`
- Health: `http://localhost:8080/q/health`

## Regole importanti

- **Lo schema DB è di proprietà di Liquibase**, non di Hibernate:
  `quarkus.hibernate-orm.schema-generation.database.generation=none`. Non aggiungere
  colonne solo lato entity: modifica prima il changelog Liquibase.
- Ogni cambio di schema è un nuovo `changeSet` nel master changelog (mai modificare
  changeSet già rilasciati in produzione). In sviluppo lo schema può essere riscritto.
- Mantieni lo stack a **PostgreSQL + H2**. Non reintrodurre driver MySQL/SQLite.
- Gli enum di dominio si persistono con un `AttributeConverter` dedicato (valore
  `raw()` in colonna `*_raw`). Le entity espongono il tipo enum, non `String`.
- Importi monetari sempre in centesimi (`long`), mai `double`/`float`.
- **Contabilità derivata**: nessuna tabella ledger né di consumo pacchetti. Saldi,
  incassi e crediti residui si calcolano nei `Service` da sessioni/pacchetti/spese.
- CORS abilitato per gli origin del frontend in dev; configurabile via `CORS_ORIGINS`.
- Credenziali DB prod via variabili d'ambiente: `DB_USER`, `DB_PASSWORD`, `DB_URL`.
