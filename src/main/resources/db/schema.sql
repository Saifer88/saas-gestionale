-- =========================================================================
-- GESTIONALE PAOLA - COMPLETE OPTIMIZED DATABASE SCHEMA (FINAL CONSOLIDATED)
-- Generated from Liquibase Master Changelog
-- =========================================================================

-- 1. TRAINING SERVICE
CREATE TABLE training_service (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description CLOB DEFAULT '',
    duration_minutes INT DEFAULT 60 NOT NULL,
    color_hex VARCHAR(50) DEFAULT '',
    is_archived BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_training_service PRIMARY KEY (id)
);

-- 2. SERVICE RATE
CREATE TABLE service_rate (
    id UUID NOT NULL,
    service_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    price_cents BIGINT DEFAULT 0 NOT NULL,
    duration_minutes INT DEFAULT 60 NOT NULL,
    is_archived BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_service_rate PRIMARY KEY (id),
    CONSTRAINT fk_service_rate_service FOREIGN KEY (service_id) REFERENCES training_service (id) ON DELETE CASCADE
);
CREATE INDEX idx_service_rate_service ON service_rate(service_id);

-- 3. CLIENT
CREATE TABLE client (
    id UUID NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) DEFAULT '' NOT NULL,
    email VARCHAR(255) DEFAULT '' NOT NULL,
    tax_code VARCHAR(50) DEFAULT '' NOT NULL,
    billing_address VARCHAR(500) DEFAULT '' NOT NULL,
    birth_date DATE,
    notes CLOB DEFAULT '' NOT NULL,
    anamnesis CLOB DEFAULT '' NOT NULL,
    physical_analysis CLOB DEFAULT '' NOT NULL,
    preferred_service_id UUID,
    preferred_rate_id UUID,
    joined_on TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    is_archived BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_client PRIMARY KEY (id),
    CONSTRAINT fk_client_pref_service FOREIGN KEY (preferred_service_id) REFERENCES training_service (id) ON DELETE SET NULL,
    CONSTRAINT fk_client_pref_rate FOREIGN KEY (preferred_rate_id) REFERENCES service_rate (id) ON DELETE SET NULL
);
CREATE INDEX idx_client_pref_service ON client(preferred_service_id);
CREATE INDEX idx_client_pref_rate ON client(preferred_rate_id);
CREATE INDEX idx_client_name_archived ON client(is_archived, last_name, first_name);

-- 4. CLIENT APPOINTMENT PREFERENCE
CREATE TABLE client_appointment_preference (
    id UUID NOT NULL,
    client_id UUID NOT NULL,
    weekday INT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME,
    notes CLOB DEFAULT '',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_client_appointment_pref PRIMARY KEY (id),
    CONSTRAINT fk_client_pref_client FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE
);
CREATE INDEX idx_client_pref_client_weekday ON client_appointment_preference(client_id, weekday);

-- 5. TRAINING SESSION
CREATE TABLE training_session (
    id UUID NOT NULL,
    start_date TIMESTAMP NOT NULL,
    duration_minutes INT DEFAULT 60 NOT NULL,
    service_id UUID,
    course_id UUID,
    notes CLOB DEFAULT '' NOT NULL,
    status_raw VARCHAR(50) DEFAULT 'planned' NOT NULL,
    invoice_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_training_session PRIMARY KEY (id),
    CONSTRAINT fk_training_session_service FOREIGN KEY (service_id) REFERENCES training_service (id) ON DELETE SET NULL,
    CONSTRAINT fk_training_session_course FOREIGN KEY (course_id) REFERENCES course (id) ON DELETE SET NULL
);
CREATE INDEX idx_training_session_start ON training_session(start_date);
CREATE INDEX idx_training_session_service ON training_session(service_id);
CREATE INDEX idx_training_session_calendar ON training_session(start_date, status_raw);
CREATE INDEX idx_training_session_course ON training_session(course_id);

-- 6. LESSON PACKAGE
CREATE TABLE lesson_package (
    id UUID NOT NULL,
    client_id UUID NOT NULL,
    purchased_on TIMESTAMP NOT NULL,
    price_cents BIGINT DEFAULT 0 NOT NULL,
    capacity INT DEFAULT 10 NOT NULL,
    expires_on TIMESTAMP,
    notes CLOB DEFAULT '' NOT NULL,
    payment_method_raw VARCHAR(50) DEFAULT 'cash' NOT NULL,
    invoice_date TIMESTAMP,
    is_black BOOLEAN DEFAULT FALSE NOT NULL,
    kind_raw VARCHAR(50) DEFAULT 'lessons' NOT NULL,
    CONSTRAINT pk_lesson_package PRIMARY KEY (id),
    CONSTRAINT fk_lesson_package_client FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE
);
CREATE INDEX idx_lesson_package_client ON lesson_package(client_id);
CREATE INDEX idx_lesson_pkg_client_expires ON lesson_package(client_id, expires_on);

-- 7. SESSION PARTICIPANT
CREATE TABLE session_participant (
    id UUID NOT NULL,
    session_id UUID NOT NULL,
    client_id UUID NOT NULL,
    price_cents BIGINT DEFAULT 0 NOT NULL,
    package_id UUID,
    payment_method_raw VARCHAR(50) DEFAULT 'cash' NOT NULL,
    is_paid BOOLEAN DEFAULT FALSE NOT NULL,
    is_black BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_session_participant PRIMARY KEY (id),
    CONSTRAINT fk_session_part_session FOREIGN KEY (session_id) REFERENCES training_session (id) ON DELETE CASCADE,
    CONSTRAINT fk_session_part_client FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE,
    CONSTRAINT fk_session_part_package FOREIGN KEY (package_id) REFERENCES lesson_package (id) ON DELETE SET NULL
);
CREATE INDEX idx_session_part_session ON session_participant(session_id);
CREATE INDEX idx_session_part_client ON session_participant(client_id);
CREATE INDEX idx_session_part_package ON session_participant(package_id);

-- 8. UNAVAILABILITY
CREATE TABLE unavailability (
    id UUID NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    reason VARCHAR(255) DEFAULT '',
    all_day BOOLEAN DEFAULT FALSE NOT NULL,
    notes CLOB DEFAULT '',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_unavailability PRIMARY KEY (id)
);
CREATE INDEX idx_unavailability_range ON unavailability(start_date, end_date);

-- 11. INVOICE
CREATE TABLE invoice (
    id UUID NOT NULL,
    client_id UUID NOT NULL,
    invoice_number VARCHAR(100) NOT NULL,
    invoice_date TIMESTAMP NOT NULL,
    amount_cents BIGINT DEFAULT 0 NOT NULL,
    tax_cents BIGINT DEFAULT 0 NOT NULL,
    status_raw VARCHAR(50) DEFAULT 'draft' NOT NULL,
    pdf_path VARCHAR(500),
    notes CLOB DEFAULT '',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_invoice PRIMARY KEY (id),
    CONSTRAINT uk_invoice_number UNIQUE (invoice_number),
    CONSTRAINT fk_invoice_client FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE
);
CREATE INDEX idx_invoice_client ON invoice(client_id);
CREATE INDEX idx_invoice_date ON invoice(invoice_date);
CREATE INDEX idx_invoice_client_date ON invoice(client_id, invoice_date);

-- 12. EXPENSE
CREATE TABLE expense (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    date TIMESTAMP NOT NULL,
    amount_cents BIGINT DEFAULT 0 NOT NULL,
    kind_raw VARCHAR(50) DEFAULT 'oneTime' NOT NULL,
    source_key VARCHAR(255) DEFAULT '' NOT NULL,
    is_personal BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_expense PRIMARY KEY (id)
);
CREATE INDEX idx_expense_date ON expense(date);
CREATE INDEX idx_expense_reporting ON expense(date, is_personal);

-- 13. COURSE
CREATE TABLE course (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    weekday INT NOT NULL,
    start_time TIME NOT NULL,
    duration_minutes INT DEFAULT 60 NOT NULL,
    max_capacity INT DEFAULT 10 NOT NULL,
    location VARCHAR(255) DEFAULT '' NOT NULL,
    service_id UUID,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    notes CLOB DEFAULT '',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_course PRIMARY KEY (id),
    CONSTRAINT fk_course_service FOREIGN KEY (service_id) REFERENCES training_service (id) ON DELETE SET NULL
);
CREATE INDEX idx_course_service ON course(service_id);
CREATE INDEX idx_course_weekday ON course(weekday, is_active);

-- 14. COURSE PARTICIPANT
CREATE TABLE course_participant (
    id UUID NOT NULL,
    course_id UUID NOT NULL,
    client_id UUID NOT NULL,
    joined_on TIMESTAMP NOT NULL,
    package_id UUID,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    notes CLOB DEFAULT '',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_course_participant PRIMARY KEY (id),
    CONSTRAINT uk_course_part_course_client UNIQUE (course_id, client_id),
    CONSTRAINT fk_course_part_course FOREIGN KEY (course_id) REFERENCES course (id) ON DELETE CASCADE,
    CONSTRAINT fk_course_part_client FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE,
    CONSTRAINT fk_course_part_package FOREIGN KEY (package_id) REFERENCES lesson_package (id) ON DELETE SET NULL
);
CREATE INDEX idx_course_part_course ON course_participant(course_id);
CREATE INDEX idx_course_part_client ON course_participant(client_id);
CREATE INDEX idx_course_part_package ON course_participant(package_id);

