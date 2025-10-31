CREATE TABLE admins (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE accommodation_types (
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

INSERT INTO accommodation_types (name, description) VALUES
    ('casa rural', 'Alojamiento rural completo'),
    ('habitacion albergue', 'Cama individual en habitación compartida de albergue');

CREATE TABLE accommodations (
    id BIGSERIAL PRIMARY KEY,
    accommodation_type_id SMALLINT NOT NULL,
    name VARCHAR(100) NOT NULL UNIQUE,
    price_per_night DECIMAL(10,2) NOT NULL,
    bed_capacity INTEGER,
    active BOOLEAN DEFAULT true,

    CONSTRAINT fk_accommodation_types FOREIGN KEY (accommodation_type_id) REFERENCES accommodation_types(id)
);

INSERT INTO accommodations (accommodation_type_id, name, price_per_night, bed_capacity) VALUES
    (1, 'El naranjo', 60.00, NULL),
    (1, 'El olivo', 60.00, NULL);

INSERT INTO accommodations (accommodation_type_id, name, price_per_night, bed_capacity) VALUES
    (2, 'Habitación Albergue 1 (3 camas)', 20.00, 3),
    (2, 'Habitación Albergue 2 (3 camas)', 20.00, 3),
    (2, 'Habitación Albergue 3 (2 camas)', 20.00, 2),
    (2, 'Habitación Albergue 4 (2 camas)', 20.00, 2),
    (2, 'Habitación Albergue 5 (2 camas)', 20.00, 2);

CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    accommodation_id BIGINT NOT NULL,
    admin_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    beds_reserved INTEGER DEFAULT 1,
    total_price DECIMAL(10,2) NOT NULL,
    paid BOOLEAN DEFAULT false,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,

    CONSTRAINT fk_accommodations_reservations FOREIGN KEY (accommodation_id) REFERENCES accommodations(id),
    CONSTRAINT fk_accommodations_admins FOREIGN KEY (admin_id) REFERENCES admins(id),
    CONSTRAINT chk_dates CHECK (start_date < end_date),
    CONSTRAINT chk_beds CHECK (beds_reserved > 0)
);

CREATE INDEX idx_reservations_dates ON reservations(accommodation_id, start_date, end_date);

CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    surnames VARCHAR(200) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(150),
    nationality VARCHAR(100) NOT NULL,
    gender CHAR(1) NOT NULL CHECK (gender IN ('H', 'M', 'O')),
    is_pilgrim BOOLEAN DEFAULT false,
    dni VARCHAR(50) NOT NULL,

    CONSTRAINT fk_customers_reservations FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
);

CREATE INDEX idx_customers_reservations ON customers(reservation_id);