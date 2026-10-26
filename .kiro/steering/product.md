# Prodotto — Gestionale Paola

Backend per la gestione di un'attività di personal training. Fornisce le API che
l'app **paola_gestionale** (client nativo SwiftUI per macOS/iOS) consuma. L'app
nasce offline-first con persistenza locale SwiftData + sync iCloud; questo backend
ne è l'evoluzione lato server e ne rispecchia il modello di dominio e i casi d'uso,
non lo schema di un DB generico.

## Domini funzionali (12 tabelle)

- **Catalogo servizi**: discipline di training (`training_service`) e relative tariffe/durate (`service_rate`).
- **Anagrafica clienti**: dati personali, codice fiscale, indirizzo di fatturazione, anamnesi medica, analisi fisica, preferenze di servizio/tariffa (`client`) e fasce orarie ricorrenti preferite (`client_appointment_preference`).
- **Agenda**: appuntamenti individuali o di gruppo (`training_session`), partecipanti alle sessioni (`session_participant`) e blocchi di indisponibilità del trainer (`unavailability`).
- **Pacchetti prepagati**: pacchetti a lezioni o a tempo (`lesson_package`).
- **Corsi di gruppo**: corsi ricorrenti settimanali come template (`course`) e iscrizioni (`course_participant`).
- **Contabilità**: spese aziendali/personali (`expense`) e fatture clienti (`invoice`).

## Concetti chiave del dominio

- **Importi in centesimi**: tutti i valori monetari sono `*_cents` (BIGINT), mai float. Evita errori di arrotondamento.
- **Contabilità derivata, nessun libro mastro**: non esiste una tabella movimenti (`ledger`). Il saldo cliente e gli incassi si **calcolano** dagli eventi già registrati:
  - addebito/incasso lezione singola → da `training_session` (stato `completed`) + `session_participant` (prezzo, senza pacchetto);
  - acquisto pacchetto → da `lesson_package`;
  - spese → da `expense`.
  Regola saldo: `saldo = addebiti netti − incassi netti` (positivo = dovuto dal cliente).
- **Crediti pacchetto derivati**: niente tabella di consumo. Residui = `capacity − (session_participant con quel pacchetto la cui sessione è completata)`. Il consumo è legato al **completamento** della sessione, non alla prenotazione. Completare con pacchetto non genera incasso.
- **Corsi come sessioni**: `course` è solo il template di ricorrenza (giorno/ora/durata/capienza). Ogni occorrenza concreta è una `training_session` con `course_id` valorizzato, così presenze, completamento e calendario riusano il motore delle sessioni.
- **Flag `is_black`**: pagamento "in nero" (non fatturato), a livello di partecipante. Rilevante per i report fiscali (regime forfettario).
- **Soft delete via `is_archived`**: clienti, servizi e tariffe si archiviano, non si cancellano, per non perdere lo storico collegato.
- **Incassi automatici solo in due casi**: acquisto pacchetto e completamento di lezione singola non coperta da pacchetto. Nessun inserimento manuale di pagamenti (acconti, rimborsi, allocazioni non sono nel MVP).

## Stato

Schema DB completo (12 tabelle Liquibase). Modello Panache implementato e verificato
(converter tipizzati, mapping validato contro lo schema). Layer resource orientato
alle view dell'app in costruzione, con servizi stub da riempire. Logica di business
(deduzione crediti derivata, calcolo saldi/incassi, calendario, reporting, auth)
ancora da sviluppare — vedi `PLAN.md`.
