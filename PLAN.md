# Backend Implementation Plan - Gestionale Paola

Piano di implementazione del backend a servizio dell'app `paola_gestionale`.

## Phase 1: Modello di dominio ✅
**Obiettivo**: mappare lo schema in entity Panache, normalizzato e verificato.

- [x] `BaseModel` (UUID PK) e `AuditedModel` (created/updated automatici)
- [x] Entity per le 11 tabelle con suffisso `Model`
- [x] Enum di dominio tipizzati con `AttributeConverter` (status, kind, payment method)
- [x] Smagrimento: eliminati `ledger_entry` e `package_use` (dati derivabili)
- [x] Preferenze di appuntamento integrate in `client` (relazione 1-a-1, tabella separata eliminata)
- [x] Rimosse colonne denormalizzate (`service_name`, `client_name`, `location`)
- [x] `course_id` su `training_session` (corsi come occorrenze di sessione)
- [x] `start_time`/`end_time` tipizzati `LocalTime`
- [x] Campi testo lungo come LONGVARCHAR (`@JdbcTypeCode`) per allineare entity e CLOB
- [x] Verifica mapping contro schema Liquibase su H2 (`SchemaMappingTest`)

## Phase 1b: Multi-tenant + autenticazione ✅
**Obiettivo**: isolamento dati tra studi e login sicuro.

- [x] Entity identità `StudioModel` (tenant) + `AccountModel` (ruoli admin/user)
- [x] `studio_id` su tutte le 11 tabelle di dominio (superclasse unica `TenantModel`)
- [x] Isolamento applicativo: `TenantContext` (studio dal JWT) + `TenantRepository` (filtra/stampa sempre lo studio), deny-by-default
- [x] Auth JWT RS256 (smallrye-jwt) + password BCrypt
- [x] Endpoint: login (pubblico), change-password (utente), register studio/account (solo admin)
- [x] Policy HTTP deny-by-default su `/api/*`; prefisso via `quarkus.rest.path`
- [x] Verifica: isolamento tra studi (`TenantIsolationTest`) + flusso auth (`AuthFlowTest`)
- [ ] Hardening prod: chiave privata JWT via env/secret (non classpath), rotazione

## Phase 2: Layer resource per view 🔌
**Obiettivo**: una resource per view dell'app, con DTO dedicati e servizi stub.
Le resource espongono ciò che la schermata chiede, non le tabelle.

Pattern per ogni view: interfaccia `*Resource` (annotation JAX-RS) + `*ResourceImpl`
(delega al service) + DTO in `dto/<area>/` + `*Service` (classe concreta
`@ApplicationScoped`, niente split interfaccia/impl: lo split resta solo sulle
resource, dove è un vincolo di Quarkus REST). Il mapping entity↔DTO è un mapper
MapStruct dedicato in `mapper/` (generato a compile-time, CDI, niente funzioni di
mapping a mano nel service).

Stato per schermata: letture (lista/ricerca/dettaglio) implementate come query
tenant-aware + mapper; scritture e logica derivata sono stub con firma definitiva
(`// TODO` + `UnsupportedOperationException` → oggi 500). Contratto HTTP (DTO, draft
con Bean Validation, path) già definito per tutti gli endpoint.

- [ ] 🚧 **Spese** (`/api/expenses`): CRUD spese
    - [x] `GET /expenses?from=&to=` lista per intervallo (date inclusive, opzionali)
    - [x] `GET /expenses/{id}` dettaglio
    - [ ] `POST /expenses` crea (stub)
    - [ ] `PUT /expenses/{id}` aggiorna (stub)
    - [ ] `DELETE /expenses/{id}` elimina fisicamente, la tabella non ha `archived` (stub)
- [ ] 🚧 **Servizi** (`/api/services`): listino con tariffe (servizio + rate insieme), attiva/disattiva
    - [x] `GET /services?includeArchived=` listino con tariffe
    - [x] `GET /services/{id}` dettaglio con tutte le tariffe (anche archiviate)
    - [ ] `POST` / `PUT /services/{id}` crea/aggiorna servizio (stub)
    - [ ] `DELETE /services/{id}` archivia, `POST /services/{id}/restore` ripristina (stub)
    - [ ] `POST /services/{id}/rates`, `PUT` / `DELETE /services/{id}/rates/{rateId}`, `POST .../restore`: tariffe (stub)
- [ ] 🚧 **Pacchetti** (`/api/packages`): lista per cliente, residui derivati, acquisto
    - [x] `GET /packages?clientId=` lista (cliente opzionale, verificato sul tenant → 404)
    - [x] `GET /packages/{id}` dettaglio
    - [ ] `POST /packages` acquisto (stub)
    - [ ] crediti usati/residui derivati: oggi `0` neutro nel mapper, calcolo in `PackageService` (Phase 3)
- [ ] 🚧 **Corsi** (`/api/courses`): template + gestione occorrenze (come sessioni)
    - [x] `GET /courses?includeInactive=` lista template
    - [x] `GET /courses/{id}` dettaglio con iscritti attivi
    - [ ] `POST` / `PUT /courses/{id}` crea/aggiorna template (stub)
    - [ ] `DELETE /courses/{id}` disattiva, `POST /courses/{id}/restore` riattiva (stub)
    - [ ] `POST /courses/{id}/participants` iscrizione, `DELETE .../participants/{participantId}` disiscrizione soft (stub)
    - [ ] generazione occorrenze come `training_session` (Phase 3, nessun endpoint)
- [ ] 🚧 **Agenda** (`/api/agenda`): calendario giorno/settimana/mese (sessioni non annullate + indisponibilità), slot suggeriti
    - [x] `GET /agenda?view=DAY|WEEK|MONTH&reference=` vista calendario (default WEEK, oggi; settimana lun–dom)
    - [x] `GET /agenda/sessions/{id}` dettaglio sessione con partecipanti
    - [x] `GET /agenda/unavailabilities/{id}` dettaglio indisponibilità
    - [ ] `POST` / `PUT` / `DELETE /agenda/sessions[/{id}]` CRUD sessioni (stub)
    - [ ] `POST` / `PUT` / `DELETE /agenda/sessions/{id}/participants[/{participantId}]` CRUD partecipanti (stub)
    - [ ] `POST` / `PUT` / `DELETE /agenda/unavailabilities[/{id}]` CRUD indisponibilità (stub)
    - [ ] validazione sovrapposizioni e slot suggeriti via `SchedulingService` (Phase 3, nessun endpoint)
- [ ] 🚧 **Pagamenti** (`/api/payments`): saldo cliente, lezioni non pagate, estratto conto
    - [x] contratto esposto (DTO + resource + impl); nessuna lettura implementata
    - [ ] `GET /payments/balances` saldi di tutti i clienti (stub)
    - [ ] `GET /payments/clients/{clientId}/balance` saldo cliente (stub)
    - [ ] `GET /payments/clients/{clientId}/unpaid` lezioni non pagate (stub)
    - [ ] `GET /payments/clients/{clientId}/statement?from=&to=` estratto conto (stub)
- [ ] 🚧 **Overview** (Panoramica): incassi week/mese/anno/previsti, contatori settimanali, appuntamenti di oggi
    - [x] `OverviewResource` (interfaccia, `@RolesAllowed`) + `OverviewResourceImpl` (default `reference` = oggi)
    - [x] `OverviewDto`: `statusRaw` sostituito da `SessionStatus status` tipizzato
    - [ ] `GET /overview?reference=` contatori incassi/settimanali e appuntamenti di oggi (`OverviewService` stub)
- [x] 🚧 **Clienti**: lista/ricerca/filtri (attivi/archiviati), dettaglio con saldo e storico, save draft, archivia
    - [x] `ClientDraftDto` (input; preferenze escluse: snapshot gestito dal flusso sessioni)
    - [x] `ClientRepository` tenant-aware (list attivi, ricerca)
    - [x] `ClientResource` / `ClientResourceImpl` / `ClientService`
    - [x] DTO di output (`ClientSummaryDto` lista, `ClientDetailDto` dettaglio)
    - [ ] saldo e storico: campi nel contratto, popolamento derivato rimandato a `AccountService` (Phase 3)
- [ ] **Report**: incassi per periodo/metodo, ore lavorate, fasce orarie, media oraria, forfettario
- [x] Smoke test registrazione resource (`ResourceRegistrationSmokeTest`) + isolamento tenant sulle nuove letture (`ResourceTenantIsolationTest`)
- [ ] Decisioni aperte: formato JSON enum (nomi Jackson vs `raw()`), stub → 501 invece di 500, sessione eliminata vs `CANCELLED`

## Phase 3: Logica di business (servizi derivati) 🧠
**Obiettivo**: riempire i servizi stub con la logica, tutta derivata dagli eventi.

- [ ] **AccountService**: `saldo = addebiti netti − incassi netti` da sessioni completate + pacchetti
- [ ] **PackageService**: crediti residui = `capacity − partecipanti con pacchetto e sessione completata`; validazione scadenza
- [ ] **SchedulingService**: validazione sovrapposizioni (solo per `planned`), indisponibilità, slot liberi
- [ ] **SessionService**: completamento sessione (effetti derivati, idempotente per natura), reschedule, stati
- [ ] **Course occurrences**: generazione occorrenze dal template corso come `training_session`

## Phase 4: Reporting & Export 📈
- [ ] Dashboard incassi vs spese per periodo
- [ ] Statistiche ore lavorate, fasce orarie, media oraria
- [ ] Estratto conto cliente (saldo iniziale/finale per intervallo)
- [ ] Export CSV/PDF (documento non fiscale)

## Phase 5: Produzione 🚀
- [x] Autenticazione (JWT) e autorizzazione (ruoli) — vedi Phase 1b
- [ ] Hardening config PostgreSQL (pool, segreti via env) e chiave JWT via secret
- [ ] Più operatori per studio (estensione del modello account)
- [ ] Dockerizzazione e CI/CD

## Fuori ambito MVP
Pagamenti manuali, acconti, rimborsi, allocazioni pagamento→addebito, saldi storici
importati, fatturazione elettronica. Reintroducibili (con eventuale tabella dedicata)
solo quando emerge un caso d'uso reale.
