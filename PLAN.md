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
(delega al service) + DTO in `dto/<area>/` + `*Service` stub.

- [ ] 🚧 **Overview** (Panoramica): incassi week/mese/anno/previsti, contatori settimanali, appuntamenti di oggi
    - [x] `OverviewResource` (interfaccia)
    - [x] `OverviewDto`
    - [x] `OverviewService` (stub)
    - [ ] `OverviewResourceImpl`
    - [ ] rivedere `OverviewDto`: rimuovere `statusRaw` (sostituito da `SessionStatus` tipizzato)
- [ ] **Agenda**: calendario giorno/settimana/mese (sessioni non annullate + indisponibilità), slot suggeriti
- [ ] **Clienti**: lista/ricerca/filtri (attivi/archiviati), dettaglio con saldo e storico, save draft, archivia
    - [x] `ClientDraftDto` (residuo creato prima dell'approccio per-view; da integrare qui)
    - [x] `ClientRepository` tenant-aware (list attivi, ricerca)
    - [ ] `ClientResource` / `ClientResourceImpl` / `ClientService`
    - [ ] DTO di output (lista, dettaglio con saldo e storico)
- [ ] **Servizi**: listino con tariffe (servizio + rate insieme), attiva/disattiva
- [ ] **Pacchetti**: lista per cliente, residui derivati, acquisto
- [ ] **Pagamenti**: saldo cliente, lezioni non pagate, estratto conto
- [ ] **Spese**: CRUD spese
- [ ] **Report**: incassi per periodo/metodo, ore lavorate, fasce orarie, media oraria, forfettario
- [ ] **Corsi**: template + gestione occorrenze (come sessioni)

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
