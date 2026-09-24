-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: src/main/resources/db/changelog/db.changelog-master.xml
-- Ran at: 24/09/26, 19:39
-- Against: SA@jdbc:h2:file:./target/db/testdb
-- Liquibase version: 4.20.0
-- *********************************************************************

-- Create Database Lock Table
CREATE TABLE DATABASECHANGELOGLOCK (ID INT NOT NULL, LOCKED BOOLEAN NOT NULL, LOCKGRANTED TIMESTAMP, LOCKEDBY VARCHAR(255), CONSTRAINT PK_DATABASECHANGELOGLOCK PRIMARY KEY (ID));

-- Initialize Database Lock Table
DELETE FROM DATABASECHANGELOGLOCK;

INSERT INTO DATABASECHANGELOGLOCK (ID, LOCKED) VALUES (1, FALSE);

-- Lock Database
UPDATE DATABASECHANGELOGLOCK SET LOCKED = TRUE, LOCKEDBY = 'Pc-Mario (192.168.1.170)', LOCKGRANTED = NOW() WHERE ID = 1 AND LOCKED = FALSE;

-- Create Database Change Log Table
CREATE TABLE DATABASECHANGELOG (ID VARCHAR(255) NOT NULL, AUTHOR VARCHAR(255) NOT NULL, FILENAME VARCHAR(255) NOT NULL, DATEEXECUTED TIMESTAMP NOT NULL, ORDEREXECUTED INT NOT NULL, EXECTYPE VARCHAR(10) NOT NULL, MD5SUM VARCHAR(35), DESCRIPTION VARCHAR(255), COMMENTS VARCHAR(255), TAG VARCHAR(255), LIQUIBASE VARCHAR(20), CONTEXTS VARCHAR(255), LABELS VARCHAR(255), DEPLOYMENT_ID VARCHAR(10));

-- Changeset src/main/resources/db/changelog/db.changelog-master.xml::001-create-table-training-service::gestionale
-- Training services offered
CREATE TABLE training_service (id UUID NOT NULL, name VARCHAR(255) NOT NULL, description CLOB DEFAULT '', duration_minutes INT DEFAULT 60 NOT NULL, color_hex VARCHAR(50) DEFAULT '', is_archived BOOLEAN DEFAULT FALSE NOT NULL, created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL, CONSTRAINT pk_training_service PRIMARY KEY (id));

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('001-create-table-training-service', 'gestionale', 'src/main/resources/db/changelog/db.changelog-master.xml', NOW(), 1, '8:2dae9bdd02e51bba9d61fb5b7d11be06', 'createTable tableName=training_service', 'Training services offered', 'EXECUTED', NULL, NULL, '4.20.0', '0271563432');

-- Changeset src/main/resources/db/changelog/db.changelog-master.xml::002-create-table-service-rate::gestionale
-- Pricing rates for training services (Schema V3)
CREATE TABLE service_rate (id UUID NOT NULL, service_id UUID NOT NULL, name VARCHAR(255) NOT NULL, price_cents BIGINT DEFAULT 0 NOT NULL, duration_minutes INT DEFAULT 60 NOT NULL, is_archived BOOLEAN DEFAULT FALSE NOT NULL, created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL, CONSTRAINT pk_service_rate PRIMARY KEY (id));

ALTER TABLE service_rate ADD CONSTRAINT fk_service_rate_service FOREIGN KEY (service_id) REFERENCES training_service (id) ON DELETE CASCADE;

CREATE INDEX idx_service_rate_service ON service_rate(service_id);

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('002-create-table-service-rate', 'gestionale', 'src/main/resources/db/changelog/db.changelog-master.xml', NOW(), 2, '8:29aea50f3cb7adee5413e8ac85dc2516', 'createTable tableName=service_rate; addForeignKeyConstraint baseTableName=service_rate, constraintName=fk_service_rate_service, referencedTableName=training_service; createIndex indexName=idx_service_rate_service, tableName=service_rate', 'Pricing rates for training services (Schema V3)', 'EXECUTED', NULL, NULL, '4.20.0', '0271563432');

-- Changeset src/main/resources/db/changelog/db.changelog-master.xml::003-create-table-client::gestionale
-- Client profiles (Merged V1-V16 with taxCode, billingAddress, birthDate, preferences)
CREATE TABLE client (id UUID NOT NULL, first_name VARCHAR(255) NOT NULL, last_name VARCHAR(255) NOT NULL, phone VARCHAR(50) DEFAULT '' NOT NULL, email VARCHAR(255) DEFAULT '' NOT NULL, tax_code VARCHAR(50) DEFAULT '' NOT NULL, billing_address VARCHAR(500) DEFAULT '' NOT NULL, birth_date date, notes CLOB DEFAULT '' NOT NULL, anamnesis CLOB DEFAULT '' NOT NULL, physical_analysis CLOB DEFAULT '' NOT NULL, preferred_service_id UUID, preferred_rate_id UUID, joined_on TIMESTAMP NOT NULL, created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL, is_archived BOOLEAN DEFAULT FALSE NOT NULL, CONSTRAINT pk_client PRIMARY KEY (id));

ALTER TABLE client ADD CONSTRAINT fk_client_pref_service FOREIGN KEY (preferred_service_id) REFERENCES training_service (id) ON DELETE SET NULL;

ALTER TABLE client ADD CONSTRAINT fk_client_pref_rate FOREIGN KEY (preferred_rate_id) REFERENCES service_rate (id) ON DELETE SET NULL;

CREATE INDEX idx_client_pref_service ON client(preferred_service_id);

CREATE INDEX idx_client_pref_rate ON client(preferred_rate_id);

CREATE INDEX idx_client_name_archived ON client(is_archived, last_name, first_name);

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('003-create-table-client', 'gestionale', 'src/main/resources/db/changelog/db.changelog-master.xml', NOW(), 3, '8:f985aeacc6ac24ab3e1e8e6492c03908', 'createTable tableName=client; addForeignKeyConstraint baseTableName=client, constraintName=fk_client_pref_service, referencedTableName=training_service; addForeignKeyConstraint baseTableName=client, constraintName=fk_client_pref_rate, referencedTa...', 'Client profiles (Merged V1-V16 with taxCode, billingAddress, birthDate, preferences)', 'EXECUTED', NULL, NULL, '4.20.0', '0271563432');

-- Changeset src/main/resources/db/changelog/db.changelog-master.xml::004-create-table-client-appointment-preference::gestionale
-- Client weekly appointment preferences
CREATE TABLE client_appointment_preference (id UUID NOT NULL, client_id UUID NOT NULL, weekday INT NOT NULL, start_time VARCHAR(10) NOT NULL, end_time VARCHAR(10), notes CLOB DEFAULT '', created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL, CONSTRAINT pk_client_appointment_pref PRIMARY KEY (id));

ALTER TABLE client_appointment_preference ADD CONSTRAINT fk_client_pref_client FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE;

CREATE INDEX idx_client_pref_client_weekday ON client_appointment_preference(client_id, weekday);

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('004-create-table-client-appointment-preference', 'gestionale', 'src/main/resources/db/changelog/db.changelog-master.xml', NOW(), 4, '8:ff3bb6296b3481d730a5b05f43d16b36', 'createTable tableName=client_appointment_preference; addForeignKeyConstraint baseTableName=client_appointment_preference, constraintName=fk_client_pref_client, referencedTableName=client; createIndex indexName=idx_client_pref_client_weekday, table...', 'Client weekly appointment preferences', 'EXECUTED', NULL, NULL, '4.20.0', '0271563432');

-- Changeset src/main/resources/db/changelog/db.changelog-master.xml::005-create-table-training-session::gestionale
-- Training session appointments (Merged V1-V16)
CREATE TABLE training_session (id UUID NOT NULL, start_date TIMESTAMP NOT NULL, duration_minutes INT DEFAULT 60 NOT NULL, service_id UUID, service_name VARCHAR(255) DEFAULT '' NOT NULL, location VARCHAR(255) DEFAULT '' NOT NULL, notes CLOB DEFAULT '' NOT NULL, status_raw VARCHAR(50) DEFAULT 'planned' NOT NULL, invoice_date TIMESTAMP, is_black BOOLEAN DEFAULT FALSE NOT NULL, created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL, CONSTRAINT pk_training_session PRIMARY KEY (id));

ALTER TABLE training_session ADD CONSTRAINT fk_training_session_service FOREIGN KEY (service_id) REFERENCES training_service (id) ON DELETE SET NULL;

CREATE INDEX idx_training_session_start ON training_session(start_date);

CREATE INDEX idx_training_session_service ON training_session(service_id);

CREATE INDEX idx_training_session_calendar ON training_session(start_date, status_raw);

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('005-create-table-training-session', 'gestionale', 'src/main/resources/db/changelog/db.changelog-master.xml', NOW(), 5, '8:3e42e1ef18f93f783d5fa4934646e36b', 'createTable tableName=training_session; addForeignKeyConstraint baseTableName=training_session, constraintName=fk_training_session_service, referencedTableName=training_service; createIndex indexName=idx_training_session_start, tableName=training_...', 'Training session appointments (Merged V1-V16)', 'EXECUTED', NULL, NULL, '4.20.0', '0271563432');

-- Changeset src/main/resources/db/changelog/db.changelog-master.xml::006-create-table-lesson-package::gestionale
-- Lesson packages and time packages (Merged V1-V16)
CREATE TABLE lesson_package (id UUID NOT NULL, client_id UUID NOT NULL, client_name VARCHAR(255) DEFAULT '' NOT NULL, purchased_on TIMESTAMP NOT NULL, price_cents BIGINT DEFAULT 0 NOT NULL, capacity INT DEFAULT 10 NOT NULL, expires_on TIMESTAMP, notes CLOB DEFAULT '' NOT NULL, payment_method_raw VARCHAR(50) DEFAULT 'cash' NOT NULL, invoice_date TIMESTAMP, is_black BOOLEAN DEFAULT FALSE NOT NULL, kind_raw VARCHAR(50) DEFAULT 'lessons' NOT NULL, CONSTRAINT pk_lesson_package PRIMARY KEY (id));

ALTER TABLE lesson_package ADD CONSTRAINT fk_lesson_package_client FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE;

CREATE INDEX idx_lesson_package_client ON lesson_package(client_id);

CREATE INDEX idx_lesson_pkg_client_expires ON lesson_package(client_id, expires_on);

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('006-create-table-lesson-package', 'gestionale', 'src/main/resources/db/changelog/db.changelog-master.xml', NOW(), 6, '8:8af369358cf70e11c11b06340905f9b1', 'createTable tableName=lesson_package; addForeignKeyConstraint baseTableName=lesson_package, constraintName=fk_lesson_package_client, referencedTableName=client; createIndex indexName=idx_lesson_package_client, tableName=lesson_package; createIndex...', 'Lesson packages and time packages (Merged V1-V16)', 'EXECUTED', NULL, NULL, '4.20.0', '0271563432');

-- Changeset src/main/resources/db/changelog/db.changelog-master.xml::007-create-table-session-participant::gestionale
-- Session participants (Merged V1-V16 with paymentMethod and participant-level isPaid)
CREATE TABLE session_participant (id UUID NOT NULL, session_id UUID NOT NULL, client_id UUID NOT NULL, client_name VARCHAR(255) DEFAULT '' NOT NULL, price_cents BIGINT DEFAULT 0 NOT NULL, package_id UUID, payment_method_raw VARCHAR(50) DEFAULT 'cash' NOT NULL, is_paid BOOLEAN DEFAULT FALSE NOT NULL, CONSTRAINT pk_session_participant PRIMARY KEY (id));

ALTER TABLE session_participant ADD CONSTRAINT fk_session_part_session FOREIGN KEY (session_id) REFERENCES training_session (id) ON DELETE CASCADE;

ALTER TABLE session_participant ADD CONSTRAINT fk_session_part_client FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE;

ALTER TABLE session_participant ADD CONSTRAINT fk_session_part_package FOREIGN KEY (package_id) REFERENCES lesson_package (id) ON DELETE SET NULL;

CREATE INDEX idx_session_part_session ON session_participant(session_id);

CREATE INDEX idx_session_part_client ON session_participant(client_id);

CREATE INDEX idx_session_part_package ON session_participant(package_id);

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('007-create-table-session-participant', 'gestionale', 'src/main/resources/db/changelog/db.changelog-master.xml', NOW(), 7, '8:6302dd936cc8966207957747b05712c1', 'createTable tableName=session_participant; addForeignKeyConstraint baseTableName=session_participant, constraintName=fk_session_part_session, referencedTableName=training_session; addForeignKeyConstraint baseTableName=session_participant, constrai...', 'Session participants (Merged V1-V16 with paymentMethod and participant-level isPaid)', 'EXECUTED', NULL, NULL, '4.20.0', '0271563432');

-- Changeset src/main/resources/db/changelog/db.changelog-master.xml::008-create-table-package-use::gestionale
-- Track usage and deductions from lesson packages
CREATE TABLE package_use (id UUID NOT NULL, package_id UUID NOT NULL, session_participant_id UUID, session_id UUID, used_at TIMESTAMP NOT NULL, units_deducted INT DEFAULT 1 NOT NULL, notes CLOB DEFAULT '', created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL, CONSTRAINT pk_package_use PRIMARY KEY (id));

ALTER TABLE package_use ADD CONSTRAINT fk_package_use_package FOREIGN KEY (package_id) REFERENCES lesson_package (id) ON DELETE CASCADE;

ALTER TABLE package_use ADD CONSTRAINT fk_package_use_part FOREIGN KEY (session_participant_id) REFERENCES session_participant (id) ON DELETE SET NULL;

ALTER TABLE package_use ADD CONSTRAINT fk_package_use_session FOREIGN KEY (session_id) REFERENCES training_session (id) ON DELETE SET NULL;

CREATE INDEX idx_package_use_pkg ON package_use(package_id);

CREATE INDEX idx_package_use_session_part ON package_use(session_participant_id);

CREATE INDEX idx_package_use_session ON package_use(session_id);

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('008-create-table-package-use', 'gestionale', 'src/main/resources/db/changelog/db.changelog-master.xml', NOW(), 8, '8:709c02cab980e41b0033f04e703721e2', 'createTable tableName=package_use; addForeignKeyConstraint baseTableName=package_use, constraintName=fk_package_use_package, referencedTableName=lesson_package; addForeignKeyConstraint baseTableName=package_use, constraintName=fk_package_use_part,...', 'Track usage and deductions from lesson packages', 'EXECUTED', NULL, NULL, '4.20.0', '0271563432');

-- Changeset src/main/resources/db/changelog/db.changelog-master.xml::009-create-table-ledger-entry::gestionale
-- Accounting and cash ledger entries
CREATE TABLE ledger_entry (id UUID NOT NULL, date TIMESTAMP NOT NULL, amount_cents BIGINT DEFAULT 0 NOT NULL, direction_raw VARCHAR(50) DEFAULT 'income' NOT NULL, category VARCHAR(100) DEFAULT '' NOT NULL, description CLOB DEFAULT '' NOT NULL, payment_method_raw VARCHAR(50) DEFAULT 'cash' NOT NULL, is_black BOOLEAN DEFAULT FALSE NOT NULL, client_id UUID, package_id UUID, session_id UUID, created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL, CONSTRAINT pk_ledger_entry PRIMARY KEY (id));

ALTER TABLE ledger_entry ADD CONSTRAINT fk_ledger_entry_client FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE SET NULL;

ALTER TABLE ledger_entry ADD CONSTRAINT fk_ledger_entry_package FOREIGN KEY (package_id) REFERENCES lesson_package (id) ON DELETE SET NULL;

ALTER TABLE ledger_entry ADD CONSTRAINT fk_ledger_entry_session FOREIGN KEY (session_id) REFERENCES training_session (id) ON DELETE SET NULL;

CREATE INDEX idx_ledger_entry_date ON ledger_entry(date);

CREATE INDEX idx_ledger_entry_client ON ledger_entry(client_id);

CREATE INDEX idx_ledger_entry_pkg ON ledger_entry(package_id);

CREATE INDEX idx_ledger_entry_session ON ledger_entry(session_id);

CREATE INDEX idx_ledger_reporting ON ledger_entry(date, direction_raw, is_black);

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('009-create-table-ledger-entry', 'gestionale', 'src/main/resources/db/changelog/db.changelog-master.xml', NOW(), 9, '8:36a0d1d2c3051cb060d99f0b96121780', 'createTable tableName=ledger_entry; addForeignKeyConstraint baseTableName=ledger_entry, constraintName=fk_ledger_entry_client, referencedTableName=client; addForeignKeyConstraint baseTableName=ledger_entry, constraintName=fk_ledger_entry_package, ...', 'Accounting and cash ledger entries', 'EXECUTED', NULL, NULL, '4.20.0', '0271563432');

-- Changeset src/main/resources/db/changelog/db.changelog-master.xml::010-create-table-unavailability::gestionale
-- Trainer schedule unavailability periods
CREATE TABLE unavailability (id UUID NOT NULL, start_date TIMESTAMP NOT NULL, end_date TIMESTAMP NOT NULL, reason VARCHAR(255) DEFAULT '', all_day BOOLEAN DEFAULT FALSE NOT NULL, notes CLOB DEFAULT '', created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL, CONSTRAINT pk_unavailability PRIMARY KEY (id));

CREATE INDEX idx_unavailability_range ON unavailability(start_date, end_date);

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('010-create-table-unavailability', 'gestionale', 'src/main/resources/db/changelog/db.changelog-master.xml', NOW(), 10, '8:dc7f7f6e0efc1fe5bd0d3776d90fc052', 'createTable tableName=unavailability; createIndex indexName=idx_unavailability_range, tableName=unavailability', 'Trainer schedule unavailability periods', 'EXECUTED', NULL, NULL, '4.20.0', '0271563432');

-- Changeset src/main/resources/db/changelog/db.changelog-master.xml::011-create-table-invoice::gestionale
-- Electronic invoices for clients
CREATE TABLE invoice (id UUID NOT NULL, client_id UUID NOT NULL, invoice_number VARCHAR(100) NOT NULL, invoice_date TIMESTAMP NOT NULL, amount_cents BIGINT DEFAULT 0 NOT NULL, tax_cents BIGINT DEFAULT 0 NOT NULL, status_raw VARCHAR(50) DEFAULT 'draft' NOT NULL, pdf_path VARCHAR(500), notes CLOB DEFAULT '', created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL, CONSTRAINT pk_invoice PRIMARY KEY (id), CONSTRAINT uk_invoice_number UNIQUE (invoice_number));

ALTER TABLE invoice ADD CONSTRAINT fk_invoice_client FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE;

CREATE INDEX idx_invoice_client ON invoice(client_id);

CREATE INDEX idx_invoice_date ON invoice(invoice_date);

CREATE INDEX idx_invoice_client_date ON invoice(client_id, invoice_date);

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('011-create-table-invoice', 'gestionale', 'src/main/resources/db/changelog/db.changelog-master.xml', NOW(), 11, '8:49425335b6747fb3199665a959a4f92d', 'createTable tableName=invoice; addForeignKeyConstraint baseTableName=invoice, constraintName=fk_invoice_client, referencedTableName=client; createIndex indexName=idx_invoice_client, tableName=invoice; createIndex indexName=idx_invoice_date, tableN...', 'Electronic invoices for clients', 'EXECUTED', NULL, NULL, '4.20.0', '0271563432');

-- Changeset src/main/resources/db/changelog/db.changelog-master.xml::012-create-table-expense::gestionale
-- One-time and recurring business/personal expenses
CREATE TABLE expense (id UUID NOT NULL, name VARCHAR(255) NOT NULL, date TIMESTAMP NOT NULL, amount_cents BIGINT DEFAULT 0 NOT NULL, kind_raw VARCHAR(50) DEFAULT 'oneTime' NOT NULL, source_key VARCHAR(255) DEFAULT '' NOT NULL, is_personal BOOLEAN DEFAULT FALSE NOT NULL, created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL, CONSTRAINT pk_expense PRIMARY KEY (id));

CREATE INDEX idx_expense_date ON expense(date);

CREATE INDEX idx_expense_reporting ON expense(date, is_personal);

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('012-create-table-expense', 'gestionale', 'src/main/resources/db/changelog/db.changelog-master.xml', NOW(), 12, '8:a18e7cce731ff27aedba2e07e4b2267a', 'createTable tableName=expense; createIndex indexName=idx_expense_date, tableName=expense; createIndex indexName=idx_expense_reporting, tableName=expense', 'One-time and recurring business/personal expenses', 'EXECUTED', NULL, NULL, '4.20.0', '0271563432');

-- Changeset src/main/resources/db/changelog/db.changelog-master.xml::013-create-table-course::gestionale
-- Recurring weekly courses / group classes
CREATE TABLE course (id UUID NOT NULL, name VARCHAR(255) NOT NULL, weekday INT NOT NULL, start_time VARCHAR(10) NOT NULL, duration_minutes INT DEFAULT 60 NOT NULL, max_capacity INT DEFAULT 10 NOT NULL, location VARCHAR(255) DEFAULT '' NOT NULL, service_id UUID, is_active BOOLEAN DEFAULT TRUE NOT NULL, notes CLOB DEFAULT '', created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL, CONSTRAINT pk_course PRIMARY KEY (id));

ALTER TABLE course ADD CONSTRAINT fk_course_service FOREIGN KEY (service_id) REFERENCES training_service (id) ON DELETE SET NULL;

CREATE INDEX idx_course_service ON course(service_id);

CREATE INDEX idx_course_weekday ON course(weekday, is_active);

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('013-create-table-course', 'gestionale', 'src/main/resources/db/changelog/db.changelog-master.xml', NOW(), 13, '8:c47bd02af782d943e4aaa05cb2f1cce4', 'createTable tableName=course; addForeignKeyConstraint baseTableName=course, constraintName=fk_course_service, referencedTableName=training_service; createIndex indexName=idx_course_service, tableName=course; createIndex indexName=idx_course_weekda...', 'Recurring weekly courses / group classes', 'EXECUTED', NULL, NULL, '4.20.0', '0271563432');

-- Changeset src/main/resources/db/changelog/db.changelog-master.xml::014-create-table-course-participant::gestionale
-- Enrollment of clients in courses, optionally linked to time packages
CREATE TABLE course_participant (id UUID NOT NULL, course_id UUID NOT NULL, client_id UUID NOT NULL, joined_on TIMESTAMP NOT NULL, package_id UUID, is_active BOOLEAN DEFAULT TRUE NOT NULL, notes CLOB DEFAULT '', created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL, CONSTRAINT pk_course_participant PRIMARY KEY (id));

ALTER TABLE course_participant ADD CONSTRAINT fk_course_part_course FOREIGN KEY (course_id) REFERENCES course (id) ON DELETE CASCADE;

ALTER TABLE course_participant ADD CONSTRAINT fk_course_part_client FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE;

ALTER TABLE course_participant ADD CONSTRAINT fk_course_part_package FOREIGN KEY (package_id) REFERENCES lesson_package (id) ON DELETE SET NULL;

ALTER TABLE course_participant ADD CONSTRAINT uk_course_part_course_client UNIQUE (course_id, client_id);

CREATE INDEX idx_course_part_course ON course_participant(course_id);

CREATE INDEX idx_course_part_client ON course_participant(client_id);

CREATE INDEX idx_course_part_package ON course_participant(package_id);

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('014-create-table-course-participant', 'gestionale', 'src/main/resources/db/changelog/db.changelog-master.xml', NOW(), 14, '8:73b87a1b524fb3b903035d71b8b91d3e', 'createTable tableName=course_participant; addForeignKeyConstraint baseTableName=course_participant, constraintName=fk_course_part_course, referencedTableName=course; addForeignKeyConstraint baseTableName=course_participant, constraintName=fk_cours...', 'Enrollment of clients in courses, optionally linked to time packages', 'EXECUTED', NULL, NULL, '4.20.0', '0271563432');

-- Release Database Lock
UPDATE DATABASECHANGELOGLOCK SET LOCKED = FALSE, LOCKEDBY = NULL, LOCKGRANTED = NULL WHERE ID = 1;

