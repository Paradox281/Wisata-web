-- Menggabungkan kolom schedule dengan booking
ALTER TABLE bookings
    DROP CONSTRAINT IF EXISTS fk_schedule;

-- Menambahkan kolom yang diperlukan dari schedule ke booking
ALTER TABLE bookings
    ADD COLUMN departure_date DATE,
    ADD COLUMN return_date DATE,
    ADD COLUMN available_quota INTEGER,
    ADD COLUMN package_id BIGINT;

-- Memindahkan data dari schedule ke booking
UPDATE bookings b
SET 
    departure_date = s.departure_date,
    return_date = s.return_date,
    available_quota = s.available_quota,
    package_id = s.package_id
FROM schedules s
WHERE b.schedule_id = s.schedule_id;

-- Menambahkan foreign key untuk package_id
ALTER TABLE bookings
    ADD CONSTRAINT fk_package_booking
    FOREIGN KEY (package_id)
    REFERENCES destinations(id);

-- Menghapus kolom schedule_id yang tidak diperlukan lagi
ALTER TABLE bookings
    DROP COLUMN schedule_id;

-- Menghapus tabel schedules yang sudah tidak diperlukan
DROP TABLE IF EXISTS schedules; 